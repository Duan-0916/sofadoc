package com.alipay.sofa.doc.service;

import com.alipay.sofa.doc.utils.FileUtils;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.util.FS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
@Component
public class GitService {

    public static final Logger LOGGER = LoggerFactory.getLogger(GitService.class);

    @Value("${sofa.doc.git.cacheEnable}")
    boolean cacheEnable = true;

    @Value("${sofa.doc.git.cachePath}")
    String gitCacheRepo = "/home/admin/.sofadocs";

    @Value("${sofa.doc.git.deployKeyFile}")
    String gitDeployKeyFile = "/deploy/id_rsa";

    SshSessionFactory sshSessionFactory;

    @PostConstruct
    public void init() throws IOException {
        ClassPathResource resource = new ClassPathResource(gitDeployKeyFile);
        //GitService.class.getResource(gitDeployKeyFile).getPath();
        File tf = new File(System.getProperty("java.io.tmpdir"), System.currentTimeMillis() + ".id_rsa");
        LOGGER.info("Temporary deploy key path is: {}", tf.getAbsolutePath());
        IOUtils.copy(resource.getInputStream(), new FileOutputStream(tf));
        String keyPath = tf.getAbsolutePath();
        sshSessionFactory = new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host hc, Session session) {
                session.setConfig("StrictHostKeyChecking", "no");
            }

            @Override
            protected JSch createDefaultJSch(FS fs) throws JSchException {
                JSch sch = super.createDefaultJSch(fs);
                sch.removeAllIdentity();
                sch.addIdentity(keyPath); //添加私钥文件
                return sch;
            }
        };
    }

    /**
     * @param gitRepo  repo 地址
     * @param branch   分支
     * @param commitId 分支提交Id
     * @return 本地地址
     * @throws Exception 出现异常
     */
    public String clone(String gitRepo, String branch, String commitId) throws Exception {
        String gitRepoName = getGitRepoName(gitRepo); // 不带 http和.git的地址，用于生成本地文件夹，例如：code.alipay.com/zhanggeng.zg/test-doc
        return clone(gitRepo, gitRepoName, branch, commitId);
    }

    /**
     * @param gitSshURL  远程 git 地址
     * @param repoName 仓库名称
     * @param branch   分支
     * @param commitId 分支提交Id
     * @return 本地地址
     * @throws Exception 出现异常
     */
    public String clone(String gitSshURL, String repoName, String branch, String commitId) throws Exception {
        File localRepoPath;
        Git git = null;
        try {
            if (cacheEnable) {
                // 可以加快速度，但是同一个仓库并发同步的情况下，缓存模式可能会报错
                localRepoPath = new File(gitCacheRepo + "/" + repoName);
                File gitDir = new File(localRepoPath, ".git");
                if (gitDir.exists()) {
                    // git pull
                    LOGGER.info(".git directory exists, try git pull: {}", gitDir.getAbsolutePath());
                    git = new Git(new FileRepository(gitDir));
                    try {
                        gitPull(git, branch);
                        LOGGER.info("git pull success! {}", gitDir.getAbsolutePath());
                    } catch (Exception e) {
                        LOGGER.warn("git pull failed, try remove directory and git clone: " + gitDir.getAbsolutePath(), e);
                        git.close();
                        FileUtils.cleanDirectory(localRepoPath);
                        git = gitClone(gitSshURL, branch, localRepoPath);
                        LOGGER.info("git clone success! {}", localRepoPath.getAbsolutePath());
                    }
                } else {
                    LOGGER.info(".git directory not exists, try git clone: {}", gitDir.getAbsolutePath());
                    FileUtils.cleanDirectory(localRepoPath);
                    git = gitClone(gitSshURL, branch, localRepoPath);
                    LOGGER.info("git clone success! {}", localRepoPath.getAbsolutePath());
                }
            } else {
                // 每次都重新下载，支持毫秒级并发
                localRepoPath = new File(gitCacheRepo + "/" + repoName + "_" + System.currentTimeMillis());
                LOGGER.info("remove old directory and try git clone: {}", localRepoPath.getAbsolutePath());
                FileUtils.cleanDirectory(localRepoPath);
                git = gitClone(gitSshURL, branch, localRepoPath);
                LOGGER.info("git clone success! {}", localRepoPath.getAbsolutePath());
            }
            // git checkout
            checkOutCommitId(git, commitId, "c_" + System.currentTimeMillis());
        } finally {
            if (git != null) {
                git.close();
            }
        }
        return localRepoPath.getAbsolutePath();
    }

    /**
     * git clone
     *
     * @param gitUrl    远程地址
     * @param branch    分支
     * @param localPath 本地存储地址
     * @return Git代码库对象
     * @throws GitAPIException 出现异常
     */
    private Git gitClone(String gitUrl, String branch, File localPath) throws GitAPIException {
        return Git.cloneRepository()
                .setURI(gitUrl) //设置远程URI
                .setBranch(branch) //设置clone下来的分支
                .setDirectory(localPath) //设置下载存放路径
                //.setCredentialsProvider(credentialsProvider) // 设置权限验证，匿名下载不用设置
//                .setTransportConfigCallback(transport -> {
//                    SshTransport sshTransport = (SshTransport) transport;
//                    sshTransport.setSshSessionFactory(sshSessionFactory);
//                })
                .call();
    }

    /**
     * git pull
     *
     * @param git    代码库
     * @param branch 要 pull 的分支
     * @throws GitAPIException 出现异常
     */
    private void gitPull(Git git, String branch) throws GitAPIException {
        git.pull().setRemoteBranchName(branch).call();
    }

    /**
     * @param git       代码库
     * @param commitId  commit id
     * @param newBranch 新分支名称
     * @throws Exception
     */
    private void checkOutCommitId(Git git, String commitId, String newBranch) throws Exception {
        try {

            git.checkout().setCreateBranch(true).setName(newBranch).call();
        } catch (RefAlreadyExistsException e) {
            // 如果分支已存在，直接切
            git.checkout().setName(newBranch).call();
        }
    }

    /**
     * @param gitRepo http://gitlab.alipay-inc.com/zhanggeng.zg/test-doc.git
     * @return git@code.alipay.com:zhanggeng.zg/test-doc.git
     */
    String getGitSshURL(String gitRepo) {
        gitRepo = gitRepo.replace("gitlab.alipay-inc.com", "code.alipay.com");
        String sshURL = gitRepo.replace("http://", "git@")
                .replace("git://", "git@")
                .replace("https://", "git@")
                .replace(":", "/")
                .replaceFirst("/", ":");
        if (sshURL.endsWith("/")) {
            sshURL = sshURL.substring(0, sshURL.length() - 1);
        }
        if (!sshURL.endsWith(".git")) {
            sshURL = sshURL + ".git";
        }
        return sshURL;
    }

    /**
     * @param gitRepo git@code.alipay.com:zhanggeng.zg/test-doc
     * @return http://code.alipay.com/zhanggeng.zg/test-doc
     */
    public String getGitHttpURL(String gitRepo) {
        gitRepo = gitRepo.replace("gitlab.alipay-inc.com", "code.alipay.com");
        gitRepo = gitRepo.replace("git@", "http://");
        gitRepo = gitRepo.replace("git://", "http://");
        if (gitRepo.endsWith(".git")) {
            gitRepo = gitRepo.substring(0, gitRepo.length() - 4);
        }
        if (gitRepo.endsWith("/")) {
            gitRepo = gitRepo.substring(0, gitRepo.length() - 1);
        }
        gitRepo = gitRepo.replace(":", "/");
        gitRepo = gitRepo.replace("http///", "http://");
        gitRepo = gitRepo.replace("https///", "https://");
        return gitRepo;
    }

    /**
     * @param gitRepo http://gitlab.alipay-inc.com/zhanggeng.zg/test-doc.git 或者 git@code.alipay.com:zhanggeng.zg/test-doc.git
     * @return 唯一路径 code.alipay.com/zhanggeng.zg/test-doc
     */
    String getGitRepoName(String gitRepo) {
        String gitPath = getGitHttpURL(gitRepo);
        if (gitPath.contains("://")) {
            gitPath = gitPath.substring(gitPath.indexOf("://") + 3);
        } else if (gitPath.contains("@")) {
            gitPath = gitPath.substring(gitPath.indexOf("@") + 1);
        }
        gitPath = gitPath.replace(":", "/");
        return gitPath;
    }
}
