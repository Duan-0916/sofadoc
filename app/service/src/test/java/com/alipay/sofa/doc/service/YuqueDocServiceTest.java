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
        Doc doc = service.query(client,"zhanggeng.zg/whyya9", "cn56z2");
    }

    @Test
    public void getSlug() {
        YuqueDocService service = new YuqueDocService();
        Assert.assertEquals("xxx", service.getSlug("xxx"));
        Assert.assertEquals("xxx", service.getSlug("XXX"));
        Assert.assertEquals("xxx", service.getSlug("XxX"));
        Assert.assertEquals("aa-b", service.getSlug("/aa/b"));
        Assert.assertEquals("aa-b", service.getSlug("/aa/b.md"));
        Assert.assertEquals("aa-b", service.getSlug("aa/b.md"));
        Assert.assertEquals("aa-b", service.getSlug("/aa/b.MD"));
        Assert.assertEquals("aa-bb-c", service.getSlug("/aa/bb/c.md"));
        Assert.assertEquals("aa-b-b-c", service.getSlug("/aa/b-b/c.md"));
    }

    @Test
    public void syncWithChild() {

    }
}