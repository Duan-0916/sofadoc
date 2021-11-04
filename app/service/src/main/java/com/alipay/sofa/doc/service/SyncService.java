package com.alipay.sofa.doc.service;

import com.alipay.aclinkelib.common.service.facade.model.v2.AntCIComponentRestRequest;
import com.alipay.sofa.doc.model.Context;
import com.alipay.sofa.doc.model.Repo;
import com.alipay.sofa.doc.model.SyncResult;
import com.alipay.sofa.doc.model.TOC;
import com.alipay.sofa.doc.utils.FileUtils;
import com.alipay.sofa.doc.utils.StringUtils;
import com.alipay.sofa.doc.utils.YuqueClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.File;
import java.util.Locale;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
@Component
public class SyncService {

    public static final Logger LOGGER = LoggerFactory.getLogger(SyncService.class);

    @Autowired
    GitService gitService;
    @Autowired
    SummaryMdTOCParser summaryMdTocParser;
    @Autowired
    TOCChecker tocChecker;
    @Autowired
    YuqueDocService yuqueDocService;
    @Autowired
    YuqueTocService yuqueTocService;
    @Autowired
    TokenService tokenService;

    public static final String DEFAULT_YUQUE_SITE = "https://yuque.antfin.com/";

    @Value("${sofa.doc.yuque.uesr}")
    String defaultYuequeUser;

    @Value("${sofa.doc.git.doc.root}")
    String defaultGitDocRoot;

    @Value("${sofa.doc.syncTocMode}")
    String defaultSyncTocMode;

    @Value("${sofa.doc.slugGenMode}")
    String defaultSlugGenMode;

    @Value("${sofa.doc.git.cacheEnable}")
    boolean cacheEnable = true;

    /**
     * @param request 同步请求
     * @return 同步结果
     */
    public SyncResult doSync(AntCIComponentRestRequest request) {
        SyncResult result;
        String localPath = null;
        try {
            String gitRepo = request.getInputs().get("gitRepo");
            String yuqueNamespace = request.getInputs().get("yuqueNamespace");
            String gitDocRoot = request.getInputs().get("gitDocRoot"); // git
            if (StringUtils.isBlank(gitDocRoot)) {
                gitDocRoot = defaultGitDocRoot;
            }

            Context.SyncMode syncTocMode;
            String syncTocStr = request.getInputs().get("syncTocMode");
            if (StringUtils.isBlank(syncTocStr)) {
                syncTocStr = defaultSyncTocMode;
            }
            syncTocMode = Context.SyncMode.valueOf(syncTocStr.toUpperCase(Locale.ROOT));

            Context.SlugGenMode slugGenMode;
            String slugGenModeStr = request.getInputs().get("slugGenMode");
            if(StringUtils.isBlank(slugGenModeStr)){
                slugGenModeStr = defaultSlugGenMode;
            }
            slugGenMode = Context.SlugGenMode.valueOf(slugGenModeStr.toUpperCase(Locale.ROOT));

            String header = request.getInputs().get("header");
            String footer = request.getInputs().get("footer");

            // 先找是否有自定义 token，没有的话再找是否有自定义 user，否则走默认 user
            String yuqueToken = request.getInputs().get("yuqueToken");
            if (StringUtils.isBlank(yuqueToken)) {
                String yuqueUser = request.getInputs().get("yuqueUser");
                if (StringUtils.isBlank(yuqueUser)) {
                    yuqueUser = defaultYuequeUser;
                }
                yuqueToken = tokenService.getTokenByUser(yuqueUser);
            }

            Assert.notNull(gitRepo, "gitRepo 不能为空");
            Assert.notNull(yuqueNamespace, "yuqueNamespace 不能为空，请在「.aci.yml」里配置要同步的语雀知识库");
            Assert.notNull(gitDocRoot, "gitDocRoot 不能为空");
            Assert.notNull(yuqueToken, "yuqueToken 不能为空，请添加「蚂蚁集团中间件」为语雀成员，" +
                    "或在 aci.yml 里配置 yuqueToken，或在 aci.yml 里配置 yuqueUser 并联系管理员托管 token。");

            String gitPath = getGitPath(gitRepo); // 不带.git的地址，用于拼接字符串
            String gitRepoName = getGitRepoName(gitRepo); // 不带 http和.git的地址，用于生成本地文件夹
            String gitShhRepo = getGitShhRepo(gitRepo); // 用于下载代码
            String gitCommitId = request.getInputs().get("gitCommitId");
            String gitBranch = request.getInputs().get("gitBranch");
            String yuqueSite = request.getInputs().get("yuqueSite");
            if (StringUtils.isBlank(yuqueSite)) {
                yuqueSite = DEFAULT_YUQUE_SITE;
            } else if (!yuqueSite.endsWith("/")) {
                yuqueSite = yuqueSite + "/";
            }
            String baseUrl = yuqueSite + "api/v2"; // https://yuque.antfin.com/api/v2

            YuqueClient client = new YuqueClient(baseUrl, yuqueToken);

            // 0. 下载代码到本地并解析
            try {
                localPath = gitService.clone(gitShhRepo, gitRepoName, gitBranch, gitCommitId);
            } catch (Exception e) {
                throw new RuntimeException("Failed to download repo: " + e.getMessage(), e);
            }

            Repo repo = new Repo()
                    .setSite(yuqueSite)
                    .setNamespace(yuqueNamespace)
                    .setLocalPath(FileUtils.contactPath(localPath, gitDocRoot))
                    .setGitPath(gitPath)
                    .setTocType("markdown");

            Context context = new Context().setSyncMode(syncTocMode).setSlugGenMode(slugGenMode)
                    .setHeader(header).setFooter(footer);

            // 1. 解析本地目录
            TOC toc = summaryMdTocParser.parse(repo, context);

            // 2. 检查 toc 内容是否正确
            tocChecker.check(repo, toc, context);

            // 3. 根据目录进行文章同步
            yuqueDocService.syncDocs(client, repo, toc, context);

            // 4. 同步目录
            yuqueTocService.syncToc(client, repo, toc, context);

            String url = FileUtils.contactPath(yuqueSite, yuqueNamespace);

            result = new SyncResult(true, "同步成功！ 请访问 <a href=\""
                    + url + "\" target=\"_blank\" >" + url + "</a> 查看最新文档！");
        } catch (Exception e) {
            result = new SyncResult(false, "同步异常！ 简单原因为：" + e.getMessage() + "，更多请查看后台日志");
            LOGGER.error("同步异常：" + e.getMessage(), e);
        } finally {
            if (!cacheEnable && localPath != null) {
                FileUtils.cleanDirectory(new File(localPath));
            }
        }
        return result;
    }

    /**
     * @param gitRepo http://gitlab.alipay-inc.com/zhanggeng.zg/test-doc.git
     * @return git@code.alipay.com:zhanggeng.zg/test-doc.git
     */
    String getGitShhRepo(String gitRepo) {
        gitRepo = gitRepo.replace("gitlab.alipay-inc.com", "code.alipay.com");
        String sshPath = gitRepo.replace("http://", "git@")
                .replace("git://", "git@")
                .replace("https://", "git@")
                .replace(":", "/")
                .replaceFirst("/", ":");
        if (sshPath.endsWith("/")) {
            sshPath = sshPath.substring(0, sshPath.length() - 1);
        }
        if (!sshPath.endsWith(".git")) {
            sshPath = sshPath + ".git";
        }
        return sshPath;
    }

    /**
     * @param gitRepo http://gitlab.alipay-inc.com/zhanggeng.zg/test-doc.git
     * @return git@code.alipay.com:zhanggeng.zg/test-doc
     */
    String getGitPath(String gitRepo) {
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
        String gitPath = getGitPath(gitRepo);
        if (gitPath.contains("://")) {
            gitPath = gitPath.substring(gitPath.indexOf("://") + 3);
        } else if (gitPath.contains("@")) {
            gitPath = gitPath.substring(gitPath.indexOf("@") + 1);
        }
        gitPath = gitPath.replace(":", "/");
        return gitPath;
    }
}
