package com.alipay.sofa.doc.service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.aclinkelib.common.service.facade.model.v2.AntCIComponentRestRequest;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
@Deprecated
public class SyncServiceMain {

    public static void main(String[] args) {

        String text = "{\"executionTaskId\":\"60c3329a9977a906566e580b\"," +
                        "\"inputs\":{\"pipelineUrlPrefix\":\"https://code.alipay.com/zhanggeng.zg/test-doc/pipelines/11822121\"," +
                        "\"yuqueNamespace\":\"zhanggeng.zg/wclhxb\"," +
                        "\"appName\":\"test-doc\"," +
                        "\"baselineBranch\":\"\"," +
                        "\"operator\":\"zhanggeng.zg\"," +
                        "\"tenantName\":\"alipay\"," +
                        "\"baselineCommitId\":\"\"," +
                        "\"gitRepo\":\"https://code.alipay.com/zhanggeng.zg/sofa-boot-doc.git\"," +
                        "\"yuqueToken\":\"taN7NRjDb5HAS6hx35B8H5c4KhQY32eq1l6wK1C3\"," +
                        "\"gitCommitId\":\"3a2cb93dfa3717f948ebcc0d3f331b9aca635257\"," +
                        "\"gitBranch\":\"master\",\"fromSystem\":\"KUJUTA\"}," +
                        "\"submitResultHeaders\":{\"AuthApp\":\"dynamic:git-to-yuque\"," +
                        "\"AuthToken\":\"hAssebxPum48csNH7SrujN69igDfzePef8l35VgDxtGMC5snfr3UJ5rnNDb6FoIwNMTl0f0YHbetZXjT0mriIw==\"}," +
                        "\"submitResultUrl\":\"https://aclinkelib.alipay.com/rest/submit\"}";
        System.out.println(text);

        AntCIComponentRestRequest request = JSONObject.parseObject(text,  AntCIComponentRestRequest.class);
        SyncService syncService = new SyncService();
        syncService.tocChecker = new TocChecker();
        syncService.gitService = new GitService();
        syncService.summaryMdTocParser = new SummaryMdTocParser();
        syncService.yuqueDocService = new YuqueDocService();
        syncService.yuqueTocService = new YuqueTocService();
        syncService.doSync(request);
    }
}
