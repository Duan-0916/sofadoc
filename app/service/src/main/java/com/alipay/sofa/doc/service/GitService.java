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

import javax.annotation.PostConstruct;
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
        File tf = new File( System.getProperty("java.io.tmpdir"), System.currentTimeMillis() + ".id_rsa");
        LOGGER.info("Temporary deploy key path is: {}", tf.getAbsolutePath());
        IOUtils.copy(resource.getInputStream(),new FileOutputStream(tf));
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
     * @param gitPath  远程 git 地址
     * @param repoName 仓库名称
     * @param branch   分支
     * @param commitId 分支提交Id
     * @return 本地地址
     * @throws Exception 出现异常
     */
    public String clone(String gitPath, String repoName, String branch, String commitId) throws Exception {
        File localPath = new File(gitCacheRepo + "/" + repoName);
        File gitDir = new File(localPath, ".git");
        Git git = null;
        try {
            if (cacheEnable && gitDir.exists()) {
                // git pull
                LOGGER.info(".git directory exists, try git pull: {}", gitDir.getAbsolutePath());
                git = new Git(new FileRepository(gitDir));
                try {
                    gitPull(git, branch);
                    LOGGER.info("git pull success! {}", gitDir.getAbsolutePath());
                } catch (Exception e) {
                    LOGGER.warn("git pull failed, try remove directory and git clone: " + gitDir.getAbsolutePath(), e);
                    git.close();
                    FileUtils.cleanDirectory(localPath);
                    git = gitClone(gitPath, branch, localPath);
                    LOGGER.info("git clone success! {}", localPath.getAbsolutePath());
                }
            } else {
                // git clone
                if (cacheEnable) {
                    LOGGER.info(".git directory not exists, try git clone: {}", gitDir.getAbsolutePath());
                } else {
                    LOGGER.info("remove old directory and try git clone: {}", gitDir.getAbsolutePath());
                }
                FileUtils.cleanDirectory(localPath);
                git = gitClone(gitPath, branch, localPath);
                LOGGER.info("git clone success! {}", localPath.getAbsolutePath());
            }
            // git checkout
            checkOutCommitId(git, commitId, "c_" + commitId);
        } finally {
            if (git != null) {
                git.close();
            }
        }
        return localPath.getAbsolutePath();
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
                .setTransportConfigCallback(transport -> {
                    SshTransport sshTransport = (SshTransport) transport;
                    sshTransport.setSshSessionFactory(sshSessionFactory);
                }).call();
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
            git.checkout().setCreateBranch(true).setName(newBranch).setStartPoint(commitId).call();
        } catch (RefAlreadyExistsException e) {
            // 如果分支已存在，直接切
            git.checkout().setName(newBranch).setStartPoint(commitId).call();
        }
    }
}
