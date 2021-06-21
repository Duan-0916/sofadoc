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
import com.alipay.sofa.boot.util.NamedThreadFactory;
import com.alipay.sofa.doc.model.SyncResult;
import com.alipay.sofa.doc.service.SyncService;
import com.alipay.sofa.doc.utils.NetUtils;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
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

    @RequestMapping(value = "v1/rest/sync", method = RequestMethod.POST)
    @ResponseBody
    public APIStringResult doRestSampleSync(HttpServletRequest request,
                                            @RequestBody AntCIComponentRestRequest componentRequest) {
        ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) request;
        String body = new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
        StringBuilder heads = new StringBuilder();
        Enumeration<String> headNames = request.getHeaderNames();
        while (headNames.hasMoreElements()) {
            heads.append(headNames.nextElement()).append(",");
        }
        LOGGER.info("Receive aci request, heads: {} \n body: {}", heads.substring(0, heads.length() - 1), body);

        // 这个只是开启一个线程,可以直接new Thread() 的方式去整。
        EXECUTOR.execute(new TracerRunnable() {
            @Override
            public void doRun() {
                try {
                    RUNNING_TASK_COUNT.incrementAndGet();
                    ThreadContextUtil.setTask(componentRequest.getExecutionTaskId());
                    LOGGER.info("start sync send request");

                    SyncResult result = syncService.doSync(componentRequest);
                    // 重新组装返回数据信息
                    AntCIComponentRestResponse restResponseV2 = new AntCIComponentRestResponse();
                    restResponseV2.setExecutionTaskId(componentRequest.getExecutionTaskId());
                    restResponseV2.setStatus(result.isSuccess() ? AntCIComponentStatus.SUCCESS : AntCIComponentStatus.FAILED);
                    Map<String, String> submitOutputs = new HashMap<>();
                    submitOutputs.put("resultMsg", result.getMessage());
                    submitOutputs.put("debugReq", "<button onclick=\"document.getElementById('g2ydebug').style.display='block'\">显示 debug 信息</button>\n" +
                            "<span id=\"g2ydebug\" style=\"display:none\">" +
                            "- server:" + LOCAL_IP + "<br/>" +
                            "- heads: " + heads + "<br/>" +
                            "- body: " + body + "</span>");
                    restResponseV2.setOutputs(submitOutputs);
                    restResponseV2.setArtifacts(new HashMap<>());
                    String postData = JsonUtil.toJson(restResponseV2);
                    final StringEntity postEntity = RestClient.getStringEntity(postData);
                    final String submitResultUrl = componentRequest.getSubmitResultUrl();
                    LOGGER.info("start sync send request: {}, {}",  JSON.toJSONString(submitResultUrl), JSON.toJSONString(postData));
                    /* 回调REST接口 */
                    HttpResult httpResult = new RetryRestClient().post(submitResultUrl, postEntity,
                            componentRequest.getSubmitResultHeaders(), true);
                    LOGGER.info("start sync send status:" + httpResult.getStatus());
                } catch (Exception e) {
                    LOGGER.error("send post msg fail:" + e.getMessage(), e);
                } finally {
                    RUNNING_TASK_COUNT.incrementAndGet();
                }
            }
        });
        return new APIStringResult();
    }
}
