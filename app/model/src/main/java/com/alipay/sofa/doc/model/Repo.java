package com.alipay.sofa.doc.model;

/**
 * 语雀知识库
 */
public class Repo {
    private int id;
    // Book
    private String type;
    private String slug;
    private String name;
    private String description;
    private String tocYml;
    /**
     * 站点：https://yuque.antfin.com/
     */
    private String site;
    /**
     * 命名空间  zhanggeng.zg/test
     */
    private String namespace;
    private transient String localPath;
    private String gitPath;
    // markdown/json/yml
    private String tocType;
    private String token;

    public int getId() {
        return id;
    }

    public Repo setId(int id) {
        this.id = id;
        return this;
    }

    public String getType() {
        return type;
    }

    public Repo setType(String type) {
        this.type = type;
        return this;
    }

    public String getSlug() {
        return slug;
    }

    public Repo setSlug(String slug) {
        this.slug = slug;
        return this;
    }

    public String getName() {
        return name;
    }

    public Repo setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Repo setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getTocYml() {
        return tocYml;
    }

    public Repo setTocYml(String tocYml) {
        this.tocYml = tocYml;
        return this;
    }

    public String getNamespace() {
        return namespace;
    }

    public Repo setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getLocalPath() {
        return localPath;
    }

    public Repo setLocalPath(String localPath) {
        this.localPath = localPath;
        return this;
    }

    public String getGitPath() {
        return gitPath;
    }

    public Repo setGitPath(String gitPath) {
        this.gitPath = gitPath;
        return this;
    }

    public String getTocType() {
        return tocType;
    }

    public Repo setTocType(String tocType) {
        this.tocType = tocType;
        return this;
    }

    public String getToken() {
        return token;
    }

    public Repo setToken(String token) {
        this.token = token;
        return this;
    }

    public Repo setSite(String site) {
        this.site = site;
        return this;
    }

    public String getSite() {
        return site;
    }
}
