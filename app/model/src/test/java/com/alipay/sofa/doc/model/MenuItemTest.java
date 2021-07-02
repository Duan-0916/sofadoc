package com.alipay.sofa.doc.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
public class MenuItemTest {
    @Test
    public void getSlug() {
        MenuItem service = new MenuItem();
        Assert.assertEquals("xxx", service.url2Slug("xxx"));
        Assert.assertEquals("xxx", service.url2Slug("XXX"));
        Assert.assertEquals("xxx", service.url2Slug("XxX.md"));
        Assert.assertEquals("b", service.url2Slug("/aa/b"));
        Assert.assertEquals("b", service.url2Slug("/aa/b.md"));
        Assert.assertEquals("b", service.url2Slug("aa/b.md"));
        Assert.assertEquals("b", service.url2Slug("/aa/b.MD"));
        Assert.assertEquals("c", service.url2Slug("/aa/bb/c.md"));
        Assert.assertEquals("c", service.url2Slug("/aa/b-b/c.md"));
    }
}
