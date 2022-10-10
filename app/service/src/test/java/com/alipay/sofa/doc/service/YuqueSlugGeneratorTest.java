package com.alipay.sofa.doc.service;

import com.alipay.sofa.doc.model.Context;
import com.alipay.sofa.doc.model.Context.SlugGenMode;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
public class YuqueSlugGeneratorTest {

    @Test
    public void getSlugFilName() {
        YuqueSlugGenerator service = new YuqueSlugGenerator();
        Assert.assertEquals("xxx", service.url2Slug("xxx", SlugGenMode.FILENAME));
        Assert.assertEquals("xxx", service.url2Slug("XXX", SlugGenMode.FILENAME));
        Assert.assertEquals("xxx", service.url2Slug("XxX.md", SlugGenMode.FILENAME));
        Assert.assertEquals("b", service.url2Slug("/aa/b", SlugGenMode.FILENAME));
        Assert.assertEquals("b", service.url2Slug("/aa/b.md", SlugGenMode.FILENAME));
        Assert.assertEquals("b", service.url2Slug("aa/b.md", SlugGenMode.FILENAME));
        Assert.assertEquals("b", service.url2Slug("/aa/b.MD", SlugGenMode.FILENAME));
        Assert.assertEquals("c", service.url2Slug("/aa/bb/c.md", SlugGenMode.FILENAME));
        Assert.assertEquals("c", service.url2Slug("/aa/b-b/c.md", SlugGenMode.FILENAME));
    }

    @Test
    public void getSlugPrefixAndSuffix() {
        YuqueSlugGenerator service = new YuqueSlugGenerator();
        Assert.assertEquals("aaa-xxx", service.url2Slug("XXX",
                new Context().setSlugGenMode(SlugGenMode.FILENAME).setSlugPrefix("aaa")));
        Assert.assertEquals("xxx-bbb", service.url2Slug("XXX",
                new Context().setSlugGenMode(SlugGenMode.FILENAME).setSlugSuffix("bbb")));
        Assert.assertEquals("aaa-xxx-bbb", service.url2Slug("XXX",
                new Context().setSlugGenMode(SlugGenMode.FILENAME).setSlugPrefix("aaa").setSlugSuffix("bbb")));
        Assert.assertEquals("aaa-xxx-bbb", service.url2Slug("XXX",
                new Context().setSlugGenMode(SlugGenMode.FILENAME).setSlugPrefix("aaa ").setSlugSuffix(" bbb")));
        Assert.assertEquals("aaa-xxx-bbb", service.url2Slug("XXX",
                new Context().setSlugGenMode(SlugGenMode.FILENAME).setSlugPrefix("AaA").setSlugSuffix("BbB")));
    }

    @Test
    public void getSlug() {
        YuqueSlugGenerator service = new YuqueSlugGenerator();
        Assert.assertEquals("xxx", service.url2Slug("xxx", SlugGenMode.DIRS_FILENAME));
        Assert.assertEquals("xxx", service.url2Slug("XXX", SlugGenMode.DIRS_FILENAME));
        Assert.assertEquals("xxx", service.url2Slug("XxX.md", SlugGenMode.DIRS_FILENAME));
        Assert.assertEquals("aa-b", service.url2Slug("/aa/b", SlugGenMode.DIRS_FILENAME));
        Assert.assertEquals("aa-b", service.url2Slug("/aa/b.md", SlugGenMode.DIRS_FILENAME));
        Assert.assertEquals("aa-b", service.url2Slug("aa/b.md", SlugGenMode.DIRS_FILENAME));
        Assert.assertEquals("aa-b", service.url2Slug("/aa/b.MD", SlugGenMode.DIRS_FILENAME));
        Assert.assertEquals("aa-bb-c", service.url2Slug("/aa/bb/c.md", SlugGenMode.DIRS_FILENAME));
        Assert.assertEquals("aa-b-b-c", service.url2Slug("/aa/b-b/c.md", SlugGenMode.DIRS_FILENAME));
        Assert.assertEquals("aa-b-b-c", service.url2Slug("/aa/b-b/c中文.md", SlugGenMode.DIRS_FILENAME));
        Assert.assertEquals("event_extraction-readme", service.url2Slug("solutions/knowledge_mining/event_extraction/README.md", SlugGenMode.DIRS_FILENAME));
        Assert.assertEquals("012345678901234567890123456789012345", service.url2Slug("0123456789012345678901234567890123456789.md", SlugGenMode.DIRS_FILENAME));
        Assert.assertEquals("012345678901234567890123456789012345", service.url2Slug("0123456789012345678901234567890123456.md", SlugGenMode.DIRS_FILENAME));
    }
}