package com.alipay.sofa.doc.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
public class FileUtilsTest {

    @Test
    public void trimPath() {
        Assert.assertEquals("aaa", FileUtils.trimPath("/aaa"));
        Assert.assertEquals("aaa", FileUtils.trimPath("/aaa/"));
        Assert.assertEquals("aaa", FileUtils.trimPath("aaa/"));
        Assert.assertEquals("aaa", FileUtils.trimPath("aaa//"));
        Assert.assertEquals("aaa", FileUtils.trimPath("//aaa//"));
        Assert.assertEquals("./aaa", FileUtils.trimPath("./aaa/"));
        Assert.assertEquals("../aaa", FileUtils.trimPath("../aaa/"));
        Assert.assertEquals("aaa/bbb/ccc", FileUtils.trimPath("/aaa/bbb/ccc"));
        Assert.assertEquals("aaa/bbb/ccc", FileUtils.trimPath("/aaa/bbb/ccc/"));
        Assert.assertEquals("aaa/bbb/ccc", FileUtils.trimPath("aaa/bbb/ccc/"));
    }

    @Test
    public void contactPath() {
        Assert.assertEquals("/aaa", FileUtils.contactPath("/aaa"));
        Assert.assertEquals("/aaa/", FileUtils.contactPath("/aaa/"));
        Assert.assertEquals("aaa/", FileUtils.contactPath("aaa/"));
        Assert.assertEquals("/aaa/bbb", FileUtils.contactPath("/aaa/", "/bbb"));
        Assert.assertEquals("//aaa/bbb", FileUtils.contactPath("//aaa//", "/bbb"));
        Assert.assertEquals("/aaa/bbb", FileUtils.contactPath("/aaa", "bbb"));
        Assert.assertEquals("/aaa/bbb", FileUtils.contactPath("/aaa", "bbb/"));
        Assert.assertEquals("/aaa/bbb", FileUtils.contactPath("/aaa", "/bbb"));
        Assert.assertEquals("/aaa/", FileUtils.contactPath("/aaa", "/"));
        Assert.assertEquals("/aaa/", FileUtils.contactPath("/aaa", "/"));
    }

}