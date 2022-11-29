package com.alipay.sofa.doc.service;

import com.alipay.sofa.doc.model.Context;
import com.alipay.sofa.doc.model.Repo;
import com.alipay.sofa.doc.model.SyncRequest;
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

import java.util.Locale;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
@Component
public class SyncService {

    public static final Logger LOGGER = LoggerFactory.getLogger(SyncService.class);

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

    @Value("${sofa.doc.syncTocMode}")
    String defaultSyncTocMode;

    @Value("${sofa.doc.slugGenMode}")
    String defaultSlugGenMode;

    /**
     * @param request 同步请求
     * @return 同步结果
     */
    public SyncResult doSync(SyncRequest request) {
        SyncResult result;
        try {
            String yuqueNamespace = request.getYuqueNamespace();
            Assert.notNull(yuqueNamespace, "yuqueNamespace 不能为空，请在「.aci.yml」里配置要同步的语雀知识库");

            String gitRepo = request.getLocalRepoPath();
            String gitDocRoot = request.getGitDocRoot();
            Assert.notNull(gitRepo, "gitRepo 不能为空");
            Assert.notNull(gitDocRoot, "gitDocRoot 不能为空");

            // 先找是否有自定义 token，没有的话再找是否有自定义 user，否则走默认 user
            String yuqueToken = request.getYuqueToken();
            if (StringUtils.isBlank(yuqueToken)) {
                String yuqueUser = request.getYuqueUser();
                if (StringUtils.isBlank(yuqueUser)) {
                    yuqueUser = StringUtils.substringBefore(yuqueNamespace, "/"); //团队账号
                }
                yuqueToken = tokenService.getTokenByUser(yuqueUser);
            }
            Assert.notNull(yuqueToken, "yuqueUser或yuqueToken未配置，请申请语雀团队 Token 并联系管理员进行配置");

            Context.SyncMode syncTocMode;
            String syncTocStr = request.getSyncMode();
            if (StringUtils.isBlank(syncTocStr)) {
                syncTocStr = defaultSyncTocMode;
            }
            syncTocMode = Context.SyncMode.valueOf(syncTocStr.toUpperCase(Locale.ROOT));

            Context.SlugGenMode slugGenMode;
            String slugGenModeStr = request.getSlugGenMode();
            if (StringUtils.isBlank(slugGenModeStr)) {
                slugGenModeStr = defaultSlugGenMode;
            }
            slugGenMode = Context.SlugGenMode.valueOf(slugGenModeStr.toUpperCase(Locale.ROOT));

            String header = request.getHeader();
            String footer = request.getFooter();

            String yuqueSite = request.getYuqueSite();
            if (StringUtils.isBlank(yuqueSite)) {
                yuqueSite = DEFAULT_YUQUE_SITE;
            } else if (!yuqueSite.endsWith("/")) {
                yuqueSite = yuqueSite + "/";
            }
            String baseUrl = yuqueSite + "api/v2"; // https://yuque.antfin.com/api/v2

            YuqueClient client = new YuqueClient(baseUrl, yuqueToken);

            Repo repo = new Repo()
                    .setSite(yuqueSite)
                    .setNamespace(yuqueNamespace)
                    .setLocalDocPath(FileUtils.contactPath(gitRepo, gitDocRoot)) // 下载代码到本地的地址
                    .setGitHttpURL(request.getGitHttpURL()) // 不带.git的地址，用于拼接字符串
                    .setTocType("markdown")
                    .setTocFile(request.getGitDocToc());

            Context context = new Context().setSyncMode(syncTocMode).setSlugGenMode(slugGenMode)
                    .setSlugPrefix(request.getSlugPrefix()).setSlugSuffix(request.getSlugSuffix())
                    .setHeader(header).setFooter(footer).setGitDocRoot(gitDocRoot);

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
            result = new SyncResult(false, "同步异常！ 简单原因为：" + e.getMessage() + "，更多请查看 " +
                    "<a href=\"https://yuque.antfin.com/zhanggeng.zg/git-to-yuque/faq\" target=\"_blank\">FAQ</a> 或者后台日志");
            LOGGER.error("同步异常：" + e.getMessage(), e);
        }
        return result;
    }

}
