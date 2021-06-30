package com.alipay.sofa.doc.service;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
public class SyncServiceTest {

    @Test
    public void getGitPath() {
        SyncService syncService = new SyncService();
        Assert.assertEquals("https://code.alipay.com/zhanggeng.zg/test-doc",
                syncService.getGitPath("https://gitlab.alipay-inc.com/zhanggeng.zg/test-doc.git"));
        Assert.assertEquals("http://code.alipay.com/zhanggeng.zg/test-doc",
                syncService.getGitPath("http://gitlab.alipay-inc.com/zhanggeng.zg/test-doc.git"));
        Assert.assertEquals("https://code.alipay.com/zhanggeng.zg/test-doc",
                syncService.getGitPath("https://gitlab.alipay-inc.com/zhanggeng.zg/test-doc/"));
        Assert.assertEquals("http://code.alipay.com/zhanggeng.zg/test-doc",
                syncService.getGitPath("http://gitlab.alipay-inc.com/zhanggeng.zg/test-doc/"));
        Assert.assertEquals("http://code.alipay.com/zhanggeng.zg/test-doc",
                syncService.getGitPath("git@code.alipay.com:zhanggeng.zg/test-doc.git"));
        Assert.assertEquals("http://code.alipay.com/zhanggeng.zg/test-doc",
                syncService.getGitPath("git://code.alipay.com/zhanggeng.zg/test-doc.git"));
    }

    @Test
    public void getGitRepoName() {
        SyncService syncService = new SyncService();
        Assert.assertEquals("code.alipay.com/zhanggeng.zg/test-doc",
                syncService.getGitRepoName("http://gitlab.alipay-inc.com/zhanggeng.zg/test-doc.git"));
        Assert.assertEquals("code.alipay.com/zhanggeng.zg/test-doc",
                syncService.getGitRepoName("http://gitlab.alipay-inc.com/zhanggeng.zg/test-doc/"));
        Assert.assertEquals("code.alipay.com/zhanggeng.zg/test-doc",
                syncService.getGitRepoName("git@code.alipay.com:zhanggeng.zg/test-doc.git"));
        Assert.assertEquals("code.alipay.com/zhanggeng.zg/test-doc",
                syncService.getGitRepoName("git://code.alipay.com/zhanggeng.zg/test-doc.git"));
    }

    @Test
    public void getGitShhRepo() {
        SyncService syncService = new SyncService();
        Assert.assertEquals("git@code.alipay.com:zhanggeng.zg/test-doc.git",
                syncService.getGitShhRepo("https://gitlab.alipay-inc.com/zhanggeng.zg/test-doc.git"));
        Assert.assertEquals("git@code.alipay.com:zhanggeng.zg/test-doc.git",
                syncService.getGitShhRepo("http://gitlab.alipay-inc.com/zhanggeng.zg/test-doc.git"));
        Assert.assertEquals("git@code.alipay.com:zhanggeng.zg/test-doc.git",
                syncService.getGitShhRepo("https://gitlab.alipay-inc.com/zhanggeng.zg/test-doc"));
        Assert.assertEquals("git@code.alipay.com:zhanggeng.zg/test-doc.git",
                syncService.getGitShhRepo("http://gitlab.alipay-inc.com/zhanggeng.zg/test-doc/"));
        Assert.assertEquals("git@code.alipay.com:zhanggeng.zg/test-doc.git",
                syncService.getGitShhRepo("git@code.alipay.com:zhanggeng.zg/test-doc.git"));
        Assert.assertEquals("git@code.alipay.com:zhanggeng.zg/test-doc.git",
                syncService.getGitShhRepo("git://code.alipay.com/zhanggeng.zg/test-doc.git"));
    }


}
