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
        Assert.assertEquals("aa-b", service.url2Slug("/aa/b"));
        Assert.assertEquals("aa-b", service.url2Slug("/aa/b.md"));
        Assert.assertEquals("aa-b", service.url2Slug("aa/b.md"));
        Assert.assertEquals("aa-b", service.url2Slug("/aa/b.MD"));
        Assert.assertEquals("aa-bb-c", service.url2Slug("/aa/bb/c.md"));
        Assert.assertEquals("aa-b-b-c", service.url2Slug("/aa/b-b/c.md"));
        Assert.assertEquals("aa-b-b-c", service.url2Slug("/aa/b-b/c中文.md"));
        Assert.assertEquals("event_extraction-readme", service.url2Slug("solutions/knowledge_mining/event_extraction/README.md"));
        Assert.assertEquals("012345678901234567890123456789012345", service.url2Slug("0123456789012345678901234567890123456789.md"));
        Assert.assertEquals("012345678901234567890123456789012345", service.url2Slug("0123456789012345678901234567890123456.md"));
    }
}
