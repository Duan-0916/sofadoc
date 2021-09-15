package com.alipay.sofa.doc.service;

import com.alipay.sofa.doc.model.Context;
import com.alipay.sofa.doc.model.Repo;
import com.alipay.sofa.doc.model.TOC;
import com.alipay.sofa.doc.utils.YuqueClient;
import org.junit.BeforeClass;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
public class YuqueTocServiceTest {

    static YuqueClient client;

    @BeforeClass
    public static void init() {
        String XAuthToken = "8CR8ApDwrQr99EOvuVg7g0CUGGnsjHDojFX7z9Zy";
        String baseUrl = "https://yuque.antfin.com/api/v2";
        client = new YuqueClient(baseUrl, XAuthToken);
    }

    //@Test
    public void removeToc() {
        YuqueTocService service = new YuqueTocService();
        service.removeAll(client,"zhanggeng.zg/whyya9");
    }

    //@Test
    public void syncToc() {
        YuqueTocService service = new YuqueTocService();

        List<String> lines = new ArrayList<>();
        lines.add("* [0](xxx.md)");
        lines.add("  * [00](xxx.md)");
        lines.add("    * [000](xxx.md)");
        lines.add("    * [001](xxx.md)");
        lines.add("    * [002]()");
        lines.add("      * [0021](xxx.md)");
        lines.add("      * [0022](xxx.md)");
        lines.add("* [1]()");
        lines.add("  * [10](xxx.md)");
        lines.add("  * [11](xxx.md)");
        lines.add("* [2](xxx.md)");

        TOC toc = new SummaryMdTOCParser().parseSummaryLines(lines);
        Repo repo = new Repo().setNamespace("zhanggeng.zg/whyya9");
        Context context = new Context().setSyncMode(Context.SyncMode.OVERRIDE);
        service.syncToc(client, repo, toc, context);
    }

}
