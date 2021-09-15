package com.alipay.sofa.doc.service;

import com.alipay.sofa.doc.model.Context;
import com.alipay.sofa.doc.model.MenuItem;
import com.alipay.sofa.doc.model.Repo;
import com.alipay.sofa.doc.model.TOC;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
public class TOCCheckerTest {

    @Test
    public void testCheck() throws IOException {
        TOCChecker checker = new TOCChecker();
        Repo repo = new Repo().setNamespace("xxx/yyy").setLocalPath(Files.createTempDirectory(null).toAbsolutePath().toString());
        TOC toc = new TOC();
        toc.getSubMenuItems().add(new MenuItem().setTitle("xxx").setType(MenuItem.MenuItemType.DOC).setUrl("../xxx.md"));
        toc.getSubMenuItems().add(new MenuItem().setTitle("yyy").setType(MenuItem.MenuItemType.DOC).setUrl("./yyy.md"));
        toc.getSubMenuItems().add(new MenuItem().setTitle("zzz").setType(MenuItem.MenuItemType.DOC).setUrl("zzz.md"));
        toc.getSubMenuItems().add(new MenuItem().setTitle("e1").setType(MenuItem.MenuItemType.DOC).setUrl("中文.md"));
        toc.getSubMenuItems().add(new MenuItem().setTitle("e2").setType(MenuItem.MenuItemType.DOC).setUrl("..123.md"));
        toc.getSubMenuItems().add(new MenuItem().setTitle("e3").setType(MenuItem.MenuItemType.DOC).setUrl("1234567890123456789012345678901234567.md"));

        toc.getSubMenuItems().add(new MenuItem().setTitle("ggg").setType(MenuItem.MenuItemType.TITLE).setUrl(""));
        toc.getSubMenuItems().add(new MenuItem().setTitle("baidu").setType(MenuItem.MenuItemType.LINK).setUrl("http://baidu.com"));
        try {
            checker.check(repo, toc, new Context().setSlugGenMode(Context.SlugGenMode.FILENAME));
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof RuntimeException);
            Assert.assertTrue(e.getMessage().contains("xxx"));
            Assert.assertTrue(e.getMessage().contains("yyy"));
            Assert.assertTrue(e.getMessage().contains("zzz"));
            Assert.assertFalse(e.getMessage().contains("e1"));
            Assert.assertFalse(e.getMessage().contains("e2"));
            Assert.assertFalse(e.getMessage().contains("e3"));
            Assert.assertFalse(e.getMessage().contains("ggg"));
            Assert.assertFalse(e.getMessage().contains("baidu"));
        }
    }

    @Test
    public void testIsLegalSlug() {
        TOCChecker checker = new TOCChecker();
        Assert.assertFalse(checker.isLegalSlug("a"));
        Assert.assertFalse(checker.isLegalSlug("ab"));
        Assert.assertFalse(checker.isLegalSlug("1231中文"));
        Assert.assertFalse(checker.isLegalSlug("1231/"));
        Assert.assertFalse(checker.isLegalSlug("AAA"));
        Assert.assertFalse(checker.isLegalSlug("..112"));
        Assert.assertFalse(checker.isLegalSlug("1234567890123456789012345678901234567"));

        Assert.assertTrue(checker.isLegalSlug("faq"));
        Assert.assertTrue(checker.isLegalSlug("1231-11"));
        Assert.assertTrue(checker.isLegalSlug("aabb_222"));
        Assert.assertTrue(checker.isLegalSlug("aabb.222"));
        Assert.assertTrue(checker.isLegalSlug("abss-123_12313.213asdb"));
        Assert.assertTrue(checker.isLegalSlug("123456789012345678901234567890123456"));

    }

}
