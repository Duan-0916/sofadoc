package com.alipay.sofa.doc.service;

import com.alipay.sofa.doc.model.Doc;
import com.alipay.sofa.doc.utils.YuqueClient;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
public class YuqueDocServiceTest {

    static YuqueClient client;

    @BeforeClass
    public static void init() {
        String XAuthToken = "8CR8ApDwrQr99EOvuVg7g0CUGGnsjHDojFX7z9Zy";
        String baseUrl = "https://yuque.antfin.com/api/v2";
        client = new YuqueClient(baseUrl, XAuthToken);
    }

    @Test
    public void syncDocs() {
        YuqueDocService service = new YuqueDocService();
    }

    @Test
    public void sync() {
    }

    @Test
    public void query() {
        YuqueDocService service = new YuqueDocService();
        Doc doc = service.query(client, "zhanggeng.zg/whyya9", "cn56z2");
    }

    @Test
    public void syncWithChild() {

    }
}