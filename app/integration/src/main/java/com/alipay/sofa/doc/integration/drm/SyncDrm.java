package com.alipay.sofa.doc.integration.drm;

import com.alipay.drm.client.api.annotation.DAttribute;
import com.alipay.drm.client.api.annotation.DResource;
import com.alipay.sofa.specs.annotation.drm.DrmAttributeSpec;
import com.alipay.sofa.specs.annotation.drm.DrmResourceSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
@Component
@DResource(id = "com.alipay.sofa.doc.syncDrm")
@DrmResourceSpec(name = "sofadoc 的同步配置")
public class SyncDrm {

    public static final Logger LOGGER = LoggerFactory.getLogger(SyncDrm.class);

    @DAttribute
    @DrmAttributeSpec(name = "调用语雀同步文档API的间隔时间")
    private String syncDocSleep;

    private int syncDocSleepTime = 150;

    public String getSyncDocSleep() {
        return syncDocSleep;
    }

    public SyncDrm setSyncDocSleep(String syncDocSleep) {
        this.syncDocSleep = syncDocSleep;
        this.syncDocSleepTime = Integer.parseInt(syncDocSleep);
        return this;
    }

    public int getSyncDocSleepTime() {
        return syncDocSleepTime;
    }
}
