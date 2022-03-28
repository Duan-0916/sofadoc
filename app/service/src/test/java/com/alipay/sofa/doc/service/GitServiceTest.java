package com.alipay.sofa.doc.service;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
public class GitServiceTest {

    @Test
    public void getGitHttpPath() {
        GitService syncService = new GitService();
        Assert.assertEquals("https://code.alipay.com/zhanggeng.zg/test-doc",
                syncService.getGitHttpURL("https://gitlab.alipay-inc.com/zhanggeng.zg/test-doc.git"));
        Assert.assertEquals("http://code.alipay.com/zhanggeng.zg/test-doc",
                syncService.getGitHttpURL("http://gitlab.alipay-inc.com/zhanggeng.zg/test-doc.git"));
        Assert.assertEquals("https://code.alipay.com/zhanggeng.zg/test-doc",
                syncService.getGitHttpURL("https://gitlab.alipay-inc.com/zhanggeng.zg/test-doc/"));
        Assert.assertEquals("http://code.alipay.com/zhanggeng.zg/test-doc",
                syncService.getGitHttpURL("http://gitlab.alipay-inc.com/zhanggeng.zg/test-doc/"));
        Assert.assertEquals("http://code.alipay.com/zhanggeng.zg/test-doc",
                syncService.getGitHttpURL("git@code.alipay.com:zhanggeng.zg/test-doc.git"));
        Assert.assertEquals("http://code.alipay.com/zhanggeng.zg/test-doc",
                syncService.getGitHttpURL("git://code.alipay.com/zhanggeng.zg/test-doc.git"));
    }

    @Test
    public void getGitRepoName() {
        GitService syncService = new GitService();
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
        GitService syncService = new GitService();
        Assert.assertEquals("git@code.alipay.com:zhanggeng.zg/test-doc.git",
                syncService.getGitSshURL("https://gitlab.alipay-inc.com/zhanggeng.zg/test-doc.git"));
        Assert.assertEquals("git@code.alipay.com:zhanggeng.zg/test-doc.git",
                syncService.getGitSshURL("http://gitlab.alipay-inc.com/zhanggeng.zg/test-doc.git"));
        Assert.assertEquals("git@code.alipay.com:zhanggeng.zg/test-doc.git",
                syncService.getGitSshURL("https://gitlab.alipay-inc.com/zhanggeng.zg/test-doc"));
        Assert.assertEquals("git@code.alipay.com:zhanggeng.zg/test-doc.git",
                syncService.getGitSshURL("http://gitlab.alipay-inc.com/zhanggeng.zg/test-doc/"));
        Assert.assertEquals("git@code.alipay.com:zhanggeng.zg/test-doc.git",
                syncService.getGitSshURL("git@code.alipay.com:zhanggeng.zg/test-doc.git"));
        Assert.assertEquals("git@code.alipay.com:zhanggeng.zg/test-doc.git",
                syncService.getGitSshURL("git://code.alipay.com/zhanggeng.zg/test-doc.git"));
    }

}
