package com.alipay.sofa.doc.service;

import com.alipay.sofa.doc.model.Context;
import com.alipay.sofa.doc.model.Repo;
import com.alipay.sofa.doc.model.TOC;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
public class SummaryMdTocParserTest {

    private final SummaryMdTOCParser parser = new SummaryMdTOCParser();

    @Test
    public void testparse() throws IOException {
        String localPath = SummaryMdTocParserTest.class.getResource("/toc").getPath();
        Repo repo = new Repo().setNamespace("xxx").setLocalPath(localPath);
        Context context = new Context().setSyncMode(Context.SyncMode.MERGE);
        TOC toc = parser.parse(repo, context);

        Assert.assertEquals(2, toc.getSubMenuItems().size());
        Assert.assertEquals(2, toc.getSubMenuItems().get(0).getSubMenuItems().size());
    }

    @Test(expected = RuntimeException.class)
    public void testparseError1() throws IOException {
        Repo repo = new Repo().setLocalPath("xxx");
        Context context = new Context().setSyncMode(Context.SyncMode.MERGE);
        TOC toc = parser.parse(repo, context);
    }

    @Test
    public void testparseLines() {
        List<String> lines = new ArrayList<>();
        lines.add("*[0](xxx.md)");
        lines.add("  *[00](xxx.md)");
        lines.add("    *[000](xxx.md)");
        lines.add("    *[001](xxx.md)");
        lines.add("    *[002](xxx.md)");
        lines.add("      *[0021](xxx.md)");
        lines.add("      *[0022](xxx.md)");
        lines.add("*[1](xxx.md)");
        lines.add("  *[10](xxx.md)");
        lines.add("  *[11](xxx.md)");
        lines.add("*[2](xxx.md)");

        TOC toc = parser.parseSummaryLines(lines);
        Assert.assertEquals(3, toc.getSubMenuItems().size());
        Assert.assertEquals(1, toc.getSubMenuItems().get(0).
                getSubMenuItems().size());
        Assert.assertEquals(2, toc.getSubMenuItems().get(1).
                getSubMenuItems().size());
        Assert.assertEquals(3, toc.getSubMenuItems().get(0).
                getSubMenuItems().get(0).getSubMenuItems().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testparseLinesError1() {
        List<String> lines = new ArrayList<>();
        lines.add("*[0](xxx.md)");
        lines.add(" *[00](xxx.md)");

        TOC toc = parser.parseSummaryLines(lines);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testparseLinesError2() {
        List<String> lines = new ArrayList<>();
        lines.add("*[0](xxx.md)");
        lines.add("  *[00](xxx.md)");
        lines.add("      *[000](xxx.md)");

        TOC toc = parser.parseSummaryLines(lines);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testparseLinesError3() {
        List<String> lines = new ArrayList<>();
        lines.add("  *[0](xxx.md)");
        lines.add("    *[00](xxx.md)");
        lines.add("      *[00](xxx.md)");
        lines.add("*[1](xxx.md)");

        TOC toc = parser.parseSummaryLines(lines);
    }
}
