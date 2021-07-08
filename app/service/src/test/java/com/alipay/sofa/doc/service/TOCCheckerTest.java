package com.alipay.sofa.doc.service;

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
        toc.getSubMenuItems().add(new MenuItem().setTitle("ggg").setType(MenuItem.MenuItemType.TITLE).setUrl(""));
        toc.getSubMenuItems().add(new MenuItem().setTitle("baidu").setType(MenuItem.MenuItemType.LINK).setUrl("http://baidu.com"));
        try {
            checker.check(repo, toc);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof RuntimeException);
            Assert.assertTrue(e.getMessage().contains("xxx"));
            Assert.assertTrue(e.getMessage().contains("yyy"));
            Assert.assertTrue(e.getMessage().contains("zzz"));
            Assert.assertFalse(e.getMessage().contains("ggg"));
            Assert.assertFalse(e.getMessage().contains("baidu"));
        }
    }
}
