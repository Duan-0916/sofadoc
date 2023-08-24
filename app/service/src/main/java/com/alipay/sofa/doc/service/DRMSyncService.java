package com.alipay.sofa.doc.service;

import com.alipay.opssla.common.service.facade.DrmManageFacade;
import com.alipay.opssla.common.service.facade.model.DrmOperateResult;
import com.alipay.opssla.common.service.facade.model.DrmQueryResult;
import com.alipay.sofa.doc.model.DRMDataID;
import com.alipay.sofa.rpc.api.annotation.RpcConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
@Component
public class DRMSyncService {

    public static final Logger LOGGER = LoggerFactory.getLogger(DRMSyncService.class);

    /**
     * 替换 RESOURCE_DOMAIN 里的应用名
     */
    public static final String REPLACE_RESOURCE_DOMAIN = "1";
    /**
     * 替换 RESOURCE_ID 里的应用名
     */
    public static final String REPLACE_RESOURCE_ID = "2";

    @RpcConsumer(uniqueId = "${drm.manage.facade.uniqueId}")
    DrmManageFacade drmManageFacade;

    /**
     * @param dataIDStr 要同步的key
     * @param watchMode 观察者模式
     * @param syncmode 同步模式 1是应用，2是还id
     * @return 同步成功与否
     */
    public boolean sync(String dataIDStr, boolean watchMode, String syncmode) {
        DRMDataID drmDataID = DRMDataID.parseFrom(dataIDStr);
        DrmQueryResult<Map<String, String>> result = drmManageFacade.queryValueByAllZone(drmDataID.getResourceId(),
                drmDataID.getResourceDomain(),
                drmDataID.getResourceVersion(),
                drmDataID.getAttributeName());

        LOGGER.info("query {} all zone:", dataIDStr);

        // 值: zone列表
        HashMap<String, List<String>> valueZone = new HashMap<>();
        // 按值做一下聚合，减少推送调用次数
        Map<String, String> target = result.getTarget();
        for (Map.Entry<String, String> entry : target.entrySet()) {
            LOGGER.info("  zone: {}, value: {}", entry.getKey(), entry.getValue());
            List<String> zones = valueZone.get(entry.getValue());
            if (zones == null) {
                zones = new ArrayList<>();
                zones.add(entry.getKey());
                valueZone.put(entry.getValue(), zones);
            } else {
                zones.add(entry.getKey());
            }
        }

        // 组装要同步的 dataId
        DRMDataID newDataId = new DRMDataID()
                .setAttributeName(drmDataID.getAttributeName())
                .setResourceVersion(drmDataID.getResourceVersion());
        if (REPLACE_RESOURCE_DOMAIN.equals(syncmode)) {
            newDataId.setResourceDomain(drmDataID.getResourceDomain() + "c");// 3.0 特殊处理
            newDataId.setResourceId(drmDataID.getResourceId());
        } else if(REPLACE_RESOURCE_ID.equals(syncmode)) {
            newDataId.setResourceDomain(drmDataID.getResourceDomain()); // 3.0 特殊处理
            newDataId.setResourceId(drmDataID.getResourceId() + "c");
        }

        boolean AllResult = true;

        for (Map.Entry<String, List<String>> entry : valueZone.entrySet()) {
            List<String> logicZones = entry.getValue();
            String value = entry.getKey();
            if (value == null) {
                LOGGER.info("skip sync because null value: {}:name={}.{},version={}@DRM, {}",
                        newDataId.getResourceDomain(),
                        newDataId.getResourceId(), newDataId.getAttributeName(),
                        newDataId.getResourceVersion(), logicZones);
                continue;
            }

            if (watchMode) {
                // 观察者模式，只打印日志
                LOGGER.info("Watch sync request: {}:name={}.{},version={}@DRM, {}, {}", newDataId.getResourceDomain(),
                        newDataId.getResourceId(), newDataId.getAttributeName(),
                        newDataId.getResourceVersion(), logicZones, value);
            } else {
                LOGGER.info("Invoke sync request: {}:name={}.{},version={}@DRM, {}, {}", newDataId.getResourceDomain(),
                        newDataId.getResourceId(), newDataId.getAttributeName(),
                        newDataId.getResourceVersion(), logicZones, value);
                DrmOperateResult operateResult = drmManageFacade.setValueByConfigServer(newDataId.getResourceId(),
                        newDataId.getResourceDomain(),
                        newDataId.getResourceVersion(),
                        newDataId.getAttributeName(), value, logicZones);
                if (!operateResult.isSuccess()) {
                    LOGGER.error(operateResult.getMessage());
                    AllResult = false;
                }
            }
        }
        return AllResult;
    }
}
