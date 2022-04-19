package com.alipay.sofa.doc.web;

import com.alipay.sofa.doc.model.SyncRequest;
import com.alipay.sofa.doc.model.SyncResult;
import com.alipay.sofa.doc.service.GitService;
import com.alipay.sofa.doc.service.SyncService;
import com.alipay.sofa.doc.utils.FileUtils;
import com.alipay.sofa.doc.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
@Controller
public class UploadController {
    public static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

    @Autowired
    private SyncService syncService;

    @Autowired
    private GitService gitService;

    @Value("${sofa.doc.git.doc.root}")
    String defaultGitDocRoot;

    @Value("${sofa.doc.git.cachePath}")
    String gitCacheRepo;

    @RequestMapping(value = "/v1/rest/syncByZip", method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public SyncResult uploadFileHandler(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            LOGGER.info("Zip file {} is empty.", file.getOriginalFilename());
            return new SyncResult(false,
                    "Zip file " + file.getOriginalFilename() + " is empty.");
        } else {
            DefaultMultipartHttpServletRequest servletRequest = (DefaultMultipartHttpServletRequest) request;
            SyncRequest syncRequest = new SyncRequest();
            try {
                String yuqueNamespace = servletRequest.getParameter("yuqueNamespace");
                Assert.notNull(yuqueNamespace, "yuqueNamespace 不能为空，请在「.aci.yml」里配置要同步的语雀知识库");
                syncRequest.setYuqueNamespace(yuqueNamespace);

                String gitRepo = servletRequest.getParameter("gitRepo");
                String gitDocRoot = servletRequest.getParameter("gitDocRoot"); // git
                if (StringUtils.isBlank(gitDocRoot)) {
                    gitDocRoot = defaultGitDocRoot;
                }

                Assert.notNull(gitRepo, "gitRepo 不能为空");
                Assert.notNull(gitDocRoot, "gitDocRoot 不能为空");
                syncRequest.setGitHttpURL(gitService.getGitHttpURL(gitRepo));  // 不带.git的地址，用于拼接字符串，例如：http://code.alipay.com/zhanggeng.zg/test-doc
                syncRequest.setGitDocRoot(gitDocRoot);

            } catch (Exception e) {
                LOGGER.error("同步异常：" + e.getMessage(), e);
                return new SyncResult(false, e.getMessage());
            }

            // 0. 下载到本地并解压
            File localZip = null;
            File localDir = null;
            try {
                File dir = new File(gitCacheRepo, "uploads");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                // 保存到本地
                String timeMs = System.currentTimeMillis() + "";
                localZip = new File(dir, timeMs + "_" + file.getOriginalFilename());
                file.transferTo(localZip);
                localDir = new File(dir, timeMs);
                // 解压
                FileUtils.unPacket(localZip.toPath(), localDir.toPath());
            } catch (Exception e) {
                LOGGER.error("解压" + localZip + "失败", e);
                return new SyncResult(false,
                        "解压" + file.getOriginalFilename() + "失败，请检查是否为 Zip 文件，并保证第一层就是 git 内容");
            }
            syncRequest.setLocalRepoPath(localDir.getAbsolutePath());

            // 可选参数
            syncRequest.setSyncMode(servletRequest.getParameter("syncTocMode"));
            syncRequest.setSlugGenMode(servletRequest.getParameter("slugGenMode"));
            syncRequest.setHeader(servletRequest.getParameter("header"));
            syncRequest.setFooter(servletRequest.getParameter("footer"));
            syncRequest.setYuqueToken(servletRequest.getParameter("yuqueToken"));
            syncRequest.setYuqueUser(servletRequest.getParameter("yuqueUser"));

            SyncResult result;
            try {
                result = syncService.doSync(syncRequest);
            } catch (Exception e) {
                LOGGER.error("同步异常：" + e.getMessage(), e);
                result = new SyncResult(false, "同步异常！ 简单原因为：" + e.getMessage() + "，更多请查看后台日志");
            } finally {
                LOGGER.info("remove old directory after sync: {} and {}", localZip, localDir);
                FileUtils.cleanDirectory(localZip);
                FileUtils.cleanDirectory(localDir);
            }
            return result;
        }
    }
}
