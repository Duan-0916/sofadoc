package com.alipay.sofa.doc.web;

import com.alipay.aclinkelib.common.service.facade.model.v2.AntCIComponentRestRequest;
import com.alipay.sofa.doc.model.SyncRequest;
import com.alipay.sofa.doc.model.SyncResult;
import com.alipay.sofa.doc.service.SyncService;
import com.alipay.sofa.doc.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
public class SyncController {

    @Autowired
    private SyncService syncService;

    @Value("${sofa.doc.git.cacheEnable}")
    private boolean cacheEnable;

    @PostMapping("/sync")
    public SyncResult syncToKnowledgeBase(@RequestBody AntCIComponentRestRequest request) {
        // 从请求中获取需要的参数
        String yuqueNamespace = request.getInputs().get("yuqueNamespace");
        String yuqueSite = request.getInputs().get("yuqueSite");

        // 创建一个 SyncRequest 对象
        SyncRequest syncRequest = new SyncRequest();
        syncRequest.setYuqueNamespace(yuqueNamespace);
        syncRequest.setYuqueSite(yuqueSite);

        // 调用 doSync 方法进行同步操作
        SyncResult result = syncService.doSync(syncRequest);

        // 根据同步结果进行相应的处理
        if (!cacheEnable && syncRequest.getLocalRepoPath() != null) {
            // 清理旧的目录
            FileUtils.cleanDirectory(new File(syncRequest.getLocalRepoPath()));
        }

        return result;
    }
}
