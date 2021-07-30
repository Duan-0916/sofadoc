package com.alipay.sofa.doc.service;

import com.alipay.sofa.doc.model.Doc;
import com.alipay.sofa.doc.model.MenuItem;
import com.alipay.sofa.doc.model.TOC;
import com.alipay.sofa.doc.utils.FileUtils;
import com.alipay.sofa.doc.utils.YuqueClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
public class DownloadYuqueRepoToGitBook {

    public static final Logger LOGGER = LoggerFactory.getLogger(DownloadYuqueRepoToGitBook.class);

    public static String yuqueSite = "https://yuque.antfin.com/";
    public static String root = "/Users/zhanggeng/workspace/code.alipay.com/sofa-docs/zdal-docs";
    public static String yuqueNamespace = "middleware/zdal";
    public static YuqueTocService service = new YuqueTocService();
    public static YuqueDocService docService = new YuqueDocService();

    public static void main(String[] args) throws IOException {

        String XAuthToken = "8CR8ApDwrQr99EOvuVg7g0CUGGnsjHDojFX7z9Zy";
        String baseUrl = "https://yuque.antfin.com/api/v2";
        YuqueClient client = new YuqueClient(baseUrl, XAuthToken);

        String tocJson = service.queryTocJson(client, yuqueNamespace);
        LOGGER.info(tocJson);

        List<String> content = new ArrayList<>();
        TOC toc = service.tocJsonToMenu(tocJson);

        for (MenuItem level0 : toc.getSubMenuItems()) {
            if (level0.getTitle().contains("Release Note")
                    || level0.getTitle().contains("SOFABoot Starter")
                    || level0.getTitle().contains("内部文档")) {
                continue;
            }
            sync(level0, root, client, content);
        }

        File summaryFile = new File(root, "SUMMARY.md");
        FileUtils.writeLines(summaryFile, content);
    }

    private static void sync(MenuItem menuItem, String root, YuqueClient client, List<String> content) throws IOException {
        MenuItem.MenuItemType type = menuItem.getType();
        if (MenuItem.MenuItemType.DOC.equals(type)) {
            Doc doc = docService.query(client, yuqueNamespace, menuItem.getSlug());
            String fileName = menuItem.getUrl() + ".md";
            //  String url = yuqueSite + yuqueNamespace + "/" + menuItem.getUrl() + "/markdown?attachment=true&latexcode=false&anchor=false&linebreak=false";
            // File file = client.download(url, root, fileName);

            File file = new File(root, fileName);
            FileUtils.string2File(file, clearContent(doc.getBody()));

            LOGGER.info("download {} to {} ", menuItem.getUrl(), file.getAbsolutePath());
            content.add(getPrefixBlank(menuItem.getLevel()) + "* [" + menuItem.getTitle() + "](" + fileName + ")");
        } else if (MenuItem.MenuItemType.LINK.equals(type)) {
            content.add(getPrefixBlank(menuItem.getLevel()) + "* [" + menuItem.getTitle() + "](" + menuItem.getUrl() + ")");
        } else if (MenuItem.MenuItemType.TITLE.equals(type)) {
            content.add(getPrefixBlank(menuItem.getLevel()) + "* [" + menuItem.getTitle() + "]()");
        }
        for (MenuItem sub : menuItem.getSubMenuItems()) {
            sync(sub, root, client, content);
        }
    }

    private static String clearContent(String src) {
        String s = src
                .replaceAll("<a name=(.*)></a>\n", "")
                .replaceAll("<br />", "")
                .replaceAll("---\n", "")
                .replaceAll("\\*\\* ", "\\*\\*")
                .replaceAll(" \\*\\*", "\\*\\*")
                .replaceAll("-\\*\\*", "- \\*\\*")
                .replaceAll("\\.\\*\\*", "\\. \\*\\*");
        s = removeAsterisk(s, ":::info", ":::");
        s = removeAsterisk(s, ":::tips", ":::");
        s = removeAsterisk(s, ":::warning", ":::");
        return s;
    }

    static String removeAsterisk(String s, String prefix, String subfix) {
        StringBuilder sb = new StringBuilder();
        removeAsteriskIter(s, sb, prefix, subfix);
        return sb.toString();
    }

    /**
     * 删除 双* 号
     *
     * @param data
     * @param sb
     * @param prefix
     * @param subfix
     */
    private static void removeAsteriskIter(String data, StringBuilder sb, String prefix, String subfix) {
        int idx = data.indexOf(prefix);
        if (idx >= 0) {
            sb.append(data, 0, idx + prefix.length());
            String s2 = data.substring(idx + prefix.length());
            int lastIdx = s2.indexOf(subfix);
            if (lastIdx >= 0) {
                String content = s2.substring(0, lastIdx + subfix.length());
                sb.append(content.replaceAll("\\*\\*", ""));
                removeAsteriskIter(s2.substring(lastIdx + subfix.length()), sb, prefix, subfix);
            } else {
                sb.append(data);
            }
        } else {
            sb.append(data);
        }
    }

    private static String getPrefixBlank(int level) {
        if (level == 0) {
            return "";
        }
        if (level == 1) {
            return "  ";
        }
        if (level == 2) {
            return "    ";
        }
        if (level == 3) {
            return "      ";
        }
        if (level == 4) {
            return "        ";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }
}
