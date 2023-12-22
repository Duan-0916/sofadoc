package com.alipay.sofa.doc.web;

import com.alibaba.fastjson.JSON;
import com.alipay.aclinkelib.common.rest.HttpResult;
import com.alipay.aclinkelib.common.rest.RestClient;
import com.alipay.aclinkelib.common.rest.RetryRestClient;
import com.alipay.aclinkelib.common.service.facade.model.APIStringResult;
import com.alipay.aclinkelib.common.service.facade.model.v2.AntCIComponentRestRequest;
import com.alipay.aclinkelib.common.service.facade.model.v2.AntCIComponentRestResponse;
import com.alipay.aclinkelib.common.service.facade.model.v2.AntCIComponentStatus;
import com.alipay.aclinkelib.common.util.JsonUtil;
import com.alipay.aclinkelib.common.util.ThreadContextUtil;
import com.alipay.common.tracer.concurrent.TracerRunnable;
import com.alipay.sofa.common.thread.NamedThreadFactory;
import com.alipay.sofa.doc.model.SyncRequest;
import com.alipay.sofa.doc.model.SyncResult;
import com.alipay.sofa.doc.service.GitService;
import com.alipay.sofa.doc.service.SyncService;
import com.alipay.sofa.doc.utils.FileUtils;
import com.alipay.sofa.doc.utils.NetUtils;
import com.alipay.sofa.doc.utils.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class AciController {

    /**
     * Logger
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(AciController.class);

    /**
     * Task executor
     */
    public static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(10, 10, 1,
            TimeUnit.MINUTES, new LinkedBlockingQueue(100), new NamedThreadFactory("T-GITTOYUQUE"));

    /**
     * 正在执行的任务数
     */
    private static final AtomicInteger RUNNING_TASK_COUNT = new AtomicInteger();
    /**
     * Local IP
     */
    private static final String LOCAL_IP = NetUtils.getLocalIpv4();

    @Autowired
    private SyncService syncService;

    @Autowired
    GitService gitService;

    @Value("${sofa.doc.git.doc.root}")
    String defaultGitDocRoot;

    @Value("${sofa.doc.git.cacheEnable}")
    boolean cacheEnable = true;

    /**
     *
     * {
     *   "ref": "refs/heads/main",
     *   "before": "6dcb09b5b57875f334f61aebed695e2e4193db5e",
     *   "after": "b7efb0c59b54948f4e6b2121e215f9f6d0c2b3f3",
     *   "repository": {
     *     "name": "my-repo",
     *     "full_name": "my-username/my-repo",
     *     "html_url": "https://github.com/my-username/my-repo"
     *   },
     *   "pusher": {
     *     "name": "John Doe"
     *   },
     *   "commits": [
     *     {
     *       "id": "b7efb0c59b54948f4e6b2121e215f9f6d0c2b3f3",
     *       "message": "Add new feature",
     *       "timestamp": "2021-09-01T10:30:00Z",
     *       "author": {
     *         "name": "John Doe",
     *         "email": "johndoe@example.com"
     *       }
     *     }
     *   ]
     * }
     *
     *
     *
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "v1/rest/sync", method = RequestMethod.POST)
    @ResponseBody
    public SyncResult doRestSampleSync(HttpServletRequest request,@RequestBody AntCIComponentRestRequest componentRequest) {
//    public SyncResult doRestSampleSync(HttpServletRequest request,@RequestBody String payload) {



        ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) request;
        String body = new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
        StringBuilder heads = new StringBuilder();
        Enumeration<String> headNames = request.getHeaderNames();
        while (headNames.hasMoreElements()) {
            heads.append(headNames.nextElement()).append(",");
        }

        String yuqueNamespace = componentRequest.getInputs().get("yuqueNamespace");
        String yuqueSite = componentRequest.getInputs().get("yuqueSite");


        // 创建一个 SyncRequest 对象
        SyncRequest syncRequest = new SyncRequest();
        syncRequest.setYuqueNamespace(yuqueNamespace);
        syncRequest.setYuqueSite(yuqueSite);

        // 调用 doSync 方法进行同步操作
//        SyncResult result = syncService.doSync(syncRequest);
        SyncResult result = doSync(componentRequest);

        // 根据同步结果进行相应的处理
        if (!cacheEnable && syncRequest.getLocalRepoPath() != null) {
            // 清理旧的目录
            FileUtils.cleanDirectory(new File(syncRequest.getLocalRepoPath()));
        }

        return result;
    }

    /**
     * 执行同步动作
     *
     * @param componentRequest
     * @return
     */
    public SyncResult doSync(AntCIComponentRestRequest componentRequest) {
        SyncRequest syncRequest = null;
        SyncResult result;
        try {
            syncRequest = aciRequestToSyncRequest(componentRequest);
            result = syncService.doSync(syncRequest);
        } catch (Exception e) {
            LOGGER.error("同步异常：" + e.getMessage(), e);
            result = new SyncResult(false, "同步异常！ 简单原因为：" + e.getMessage() + "，更多请查看 " +
                    "<a href=\"https://yuque.antfin.com/zhanggeng.zg/git-to-yuque/faq\" target=\"_blank\">FAQ</a> 或者后台日志");
        } finally {
            String localPath;
            if (!cacheEnable && syncRequest != null && (localPath = syncRequest.getLocalRepoPath()) != null) {
                LOGGER.info("remove old directory after sync: {}", localPath);
                FileUtils.cleanDirectory(new File(localPath));
            }
        }
        return result;
    }

    private SyncRequest aciRequestToSyncRequest(AntCIComponentRestRequest request) {
        SyncRequest syncRequest = new SyncRequest();

        try {
            Map<String, String> inputs = request.getInputs();
            String yuqueNamespace = inputs.get("yuqueNamespace");
            Assert.notNull(yuqueNamespace, "yuqueNamespace 不能为空，请在「.aci.yml」里配置要同步的语雀知识库");
            syncRequest.setYuqueNamespace(yuqueNamespace);

            String gitRepo = inputs.get("gitRepo");
            String gitDocRoot = inputs.get("gitDocRoot");
            if (StringUtils.isBlank(gitDocRoot)) {
                gitDocRoot = defaultGitDocRoot;
            }

            Assert.notNull(gitRepo, "gitRepo 不能为空");
            Assert.notNull(gitDocRoot, "gitDocRoot 不能为空");
            syncRequest.setGitHttpURL(gitService.getGitHttpURL(gitRepo));  // 不带.git的地址，用于拼接字符串，例如：http://code.alipay.com/zhanggeng.zg/test-doc
            syncRequest.setGitDocRoot(gitDocRoot);

            syncRequest.setGitDocToc(inputs.get("gitDocToc"));

            String gitCommitId = inputs.get("gitCommitId");
            String gitBranch = inputs.get("gitBranch");

            // 0. 下载代码到本地并解析
            String localRepoPath;
            try {
                localRepoPath = gitService.clone(gitRepo, gitBranch, gitCommitId);
            } catch (Exception e) {
                throw new RuntimeException("Failed to download repo: " + e.getMessage(), e);
            }
            syncRequest.setLocalRepoPath(localRepoPath);

            // 可选参数
            syncRequest.setSyncMode(inputs.get("syncTocMode"));
            syncRequest.setSlugGenMode(inputs.get("slugGenMode"));
            syncRequest.setSlugPrefix(inputs.get("slugPrefix"));
            syncRequest.setSlugSuffix(inputs.get("slugSuffix"));
            syncRequest.setHeader(inputs.get("header"));
            syncRequest.setFooter(inputs.get("footer"));
            syncRequest.setYuqueSite(inputs.get("yuqueSite"));
            syncRequest.setYuqueToken(inputs.get("yuqueToken"));
            syncRequest.setYuqueUser(inputs.get("yuqueUser"));

            return syncRequest;
        } catch (Exception e) {
            LOGGER.error("同步异常：" + e.getMessage(), e);
            throw e;
        }
    }
}
