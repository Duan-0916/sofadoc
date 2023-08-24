package com.alipay.sofa.doc.web;

import com.alipay.sofa.doc.model.DRMSyncResult;
import com.alipay.sofa.doc.service.DRMSyncService;
import com.alipay.sofa.doc.utils.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
@RestController
public class DRMSyncController {

    public static final Logger LOGGER = LoggerFactory.getLogger(DRMSyncController.class);

    @Autowired
    private DRMSyncService drmSyncService;

    @RequestMapping(value = "/drm/hello", method = RequestMethod.GET)
    @ResponseBody
    public String sayHello() {
        return "Hello from " + NetUtils.getLocalHost();
    }

    @RequestMapping(value = "/drm/syncByDataIDs", method = RequestMethod.POST)
    @ResponseBody
    public DRMSyncResult doRestSampleSync(@RequestParam String token,
                                          @RequestParam(defaultValue = "true") boolean watchMode,
                                          @RequestBody List<String> dataIDs) {

        LOGGER.info("Receive sync request, token:{}, body: {}", token, dataIDs);
        if (!"3.0".equals(token)) {
            // TODO 后面改成读取 drm
            return new DRMSyncResult().setSuccess(false).setMessage("The token is wrong.");
        }
        if (dataIDs == null) {
            return new DRMSyncResult().setSuccess(false).setMessage("The dataId list is null");
        }

        List<String> successes = new ArrayList<>();
        List<String> failures = new ArrayList<>();
        for (String dataID : dataIDs) {
            try {
                if (drmSyncService.sync(dataID, watchMode)) {
                    successes.add(dataID);
                } else {
                    failures.add(dataID);
                }
            } catch (Exception e) {
                LOGGER.error("sync " + dataID + "error", e);
                failures.add(dataID);
            }
        }
        DRMSyncResult result = new DRMSyncResult()
                .setSuccess(successes.size() == dataIDs.size()).setMessage("")
                .setMessage("Sync drm " + successes.size() + "/" + dataIDs.size() + " finish.")
                .setSuccesses(successes)
                .setFailures(failures)
                .setIp(NetUtils.getLocalHost());
        LOGGER.info("sync result: {}", result);
        return result;
    }
}
