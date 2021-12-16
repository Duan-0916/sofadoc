package com.alipay.sofa.doc.service;

import com.alipay.sofa.doc.model.Context;
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
    public void testGetProject() {
        YuqueDocService service = new YuqueDocService();
        Assert.assertEquals("xxx/yyy", service.getProject("http://code.alipay.com/xxx/yyy.git"));
        Assert.assertEquals("xxx/yyy", service.getProject("http://code.alipay.com/xxx/yyy"));
        Assert.assertEquals("xxx/yyy", service.getProject("code.alipay.com/xxx/yyy.git"));
        Assert.assertEquals("xxx/yyy", service.getProject("code.alipay.com/zzz/xxx/yyy"));
    }

    @Test
    public void testGetFilePath() {
        YuqueDocService service = new YuqueDocService();
        Context context1 = new Context().setGitDocRoot("/");
        Context context2 = new Context().setGitDocRoot("/doc");
        Context context3 = new Context().setGitDocRoot("/doc/");
        Assert.assertEquals("a.md", service.getFilePath(null, "a.md"));
        Assert.assertEquals("a.md", service.getFilePath(null, "/a.md"));
        Assert.assertEquals("a.md", service.getFilePath(context1, "a.md"));
        Assert.assertEquals("a/a.md", service.getFilePath(context1, "/a/a.md"));
        Assert.assertEquals("doc/a.md", service.getFilePath(context2, "a.md"));
        Assert.assertEquals("doc/a/a.md", service.getFilePath(context2, "/a/a.md"));
        Assert.assertEquals("doc/a.md", service.getFilePath(context3, "a.md"));
        Assert.assertEquals("doc/a/a.md", service.getFilePath(context3, "/a/a.md"));
    }

    @Test
    public void syncWithChild() {

    }
}