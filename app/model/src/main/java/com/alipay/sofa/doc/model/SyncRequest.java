package com.alipay.sofa.doc.model;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
public class SyncRequest {

    /**
     * 语雀知识库命名空间 例如知识库 “https://yuque.antfin.com/zhanggeng.zg/whyya9/” 的命名空间就是 “zhanggeng.zg/whyya9”
     */
    String yuqueNamespace;
    /**
     * 语雀访问 Token  （2021.10.4 不推荐）
     */
    @Deprecated
    String yuqueToken;
    /**
     * 语雀访问用户 （2021.10.4 新增）
     * 托管配置文档：https://yuque.antfin-inc.com/middleware/improveue/puabua
     */
    String yuqueUser;
    /**
     * yuque站点  https://yuque.antfin.com/
     */
    String yuqueSite;
    /**
     * 目录同步模式
     */
    String syncMode;
    /**
     * 语雀访问路径生成模式
     */
    String slugGenMode;
    /**
     * 语雀 slug 前缀，使用生成模式字段生成 slug 后，还会追加 前缀-，默认空
     */
    String slugPrefix;
    /**
     * 语雀 slug 后缀，使用生成模式字段生成 slug 后，还会追加 -后缀，默认空
     */
    String slugSuffix;
    /**
     * 自定义页眉
     */
    String header;
    /**
     * 自定义页脚
     */
    String footer;
    /**
     * 要同步的文件路径，例如一个 git 目录，一个zip解压目录等
     * 例如 /home/admin/.sofadocs/code.alipay.com/zhanggeng.zg/test-doc_1648197954077
     */
    String localRepoPath;
    /**
     * 文档所在文件夹的相对路径，默认 repo 的根目录 ./
     */
    String gitDocRoot;
    /**
     * 文档目录的文件名，默认 "SUMMARY.md"
     */
    String gitDocToc;
    /**
     * 文档所在 git 库的 http 访问路径
     * https://code.alipay.com/zhanggeng.zg/test-doc
     */
    String gitHttpURL;

    String gitCommitId;

    String gitRepo;

    /**
     * Gets get file path.
     *
     * @return the get file path
     */
    public String getLocalRepoPath() {
        return localRepoPath;
    }

    /**
     * Sets set file path.
     *
     * @param localRepoPath the file path
     * @return the set file path
     */
    public SyncRequest setLocalRepoPath(String localRepoPath) {
        this.localRepoPath = localRepoPath;
        return this;
    }

    /**
     * Gets get yuque namespace.
     *
     * @return the get yuque namespace
     */
    public String getYuqueNamespace() {
        return yuqueNamespace;
    }

    /**
     * Sets set yuque namespace.
     *
     * @param yuqueNamespace the yuque namespace
     * @return the set yuque namespace
     */
    public SyncRequest setYuqueNamespace(String yuqueNamespace) {
        this.yuqueNamespace = yuqueNamespace;
        return this;
    }

    /**
     * Gets get sync toc mode.
     *
     * @return the get sync toc mode
     */
    public String getSyncMode() {
        return syncMode;
    }

    /**
     * Sets set sync toc mode.
     *
     * @param syncMode the sync toc mode
     * @return the set sync toc mode
     */
    public SyncRequest setSyncMode(String syncMode) {
        this.syncMode = syncMode;
        return this;
    }

    /**
     * Gets get slug gen mode.
     *
     * @return the get slug gen mode
     */
    public String getSlugGenMode() {
        return slugGenMode;
    }

    /**
     * Sets set slug gen mode.
     *
     * @param slugGenMode the slug gen mode
     * @return the set slug gen mode
     */
    public SyncRequest setSlugGenMode(String slugGenMode) {
        this.slugGenMode = slugGenMode;
        return this;
    }

    /**
     * Gets get header.
     *
     * @return the get header
     */
    public String getHeader() {
        return header;
    }

    /**
     * Sets set header.
     *
     * @param header the header
     * @return the set header
     */
    public SyncRequest setHeader(String header) {
        this.header = header;
        return this;
    }

    /**
     * Gets get footer.
     *
     * @return the get footer
     */
    public String getFooter() {
        return footer;
    }

    /**
     * Sets set footer.
     *
     * @param footer the footer
     * @return the set footer
     */
    public SyncRequest setFooter(String footer) {
        this.footer = footer;
        return this;
    }

    /**
     * Gets get yuque token.
     *
     * @return the get yuque token
     */
    public String getYuqueToken() {
        return yuqueToken;
    }

    /**
     * Sets set yuque token.
     *
     * @param yuqueToken the yuque token
     * @return the set yuque token
     */
    public SyncRequest setYuqueToken(String yuqueToken) {
        this.yuqueToken = yuqueToken;
        return this;
    }

    /**
     * Gets get yuque user.
     *
     * @return the get yuque user
     */
    public String getYuqueUser() {
        return yuqueUser;
    }

    /**
     * Sets set yuque user.
     *
     * @param yuqueUser the yuque user
     * @return the set yuque user
     */
    public SyncRequest setYuqueUser(String yuqueUser) {
        this.yuqueUser = yuqueUser;
        return this;
    }

    /**
     * Gets get yuque site.
     *
     * @return the get yuque site
     */
    public String getYuqueSite() {
        return yuqueSite;
    }

    /**
     * Sets set yuque site.
     *
     * @param yuqueSite the yuque site
     * @return the set yuque site
     */
    public SyncRequest setYuqueSite(String yuqueSite) {
        this.yuqueSite = yuqueSite;
        return this;
    }

    public String getGitDocRoot() {
        return gitDocRoot;
    }

    public SyncRequest setGitDocRoot(String gitDocRoot) {
        this.gitDocRoot = gitDocRoot;
        return this;
    }

    public String getGitHttpURL() {
        return gitHttpURL;
    }

    public SyncRequest setGitHttpURL(String gitHttpURL) {
        this.gitHttpURL = gitHttpURL;
        return this;
    }

    public String getGitDocToc() {
        return gitDocToc;
    }

    public SyncRequest setGitDocToc(String gitDocToc) {
        this.gitDocToc = gitDocToc;
        return this;
    }

    public String getSlugPrefix() {
        return slugPrefix;
    }

    public SyncRequest setSlugPrefix(String slugPrefix) {
        this.slugPrefix = slugPrefix;
        return this;
    }

    public String getSlugSuffix() {
        return slugSuffix;
    }

    public String getGitCommitId() {
        return gitCommitId;
    }

    public void setGitCommitId(String gitCommitId) {
        this.gitCommitId = gitCommitId;
    }

    public String getGitRepo() {
        return gitRepo;
    }

    public void setGitRepo(String gitRepo) {
        this.gitRepo = gitRepo;
    }

    public SyncRequest setSlugSuffix(String slugSuffix) {
        this.slugSuffix = slugSuffix;
        return this;
    }
}
