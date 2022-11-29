package com.alipay.sofa.doc.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
public class StringUtilsTest {

    @Test
    public void testSubstringBefore() {
        Assert.assertEquals(StringUtils.substringBefore(null, "*"), null);
        Assert.assertEquals(StringUtils.substringBefore("", "*"), "");
        Assert.assertEquals(StringUtils.substringBefore("*", null), "");
        Assert.assertEquals(StringUtils.substringBefore("abc", "b"), "a");
        Assert.assertEquals(StringUtils.substringBefore("abcba", "c"), "ab");
        Assert.assertEquals(StringUtils.substringBefore("abc", "a"), "");
        Assert.assertEquals(StringUtils.substringBefore("abc", "d"), "");
        Assert.assertEquals(StringUtils.substringBefore("abc", ""), "");
    }

}
