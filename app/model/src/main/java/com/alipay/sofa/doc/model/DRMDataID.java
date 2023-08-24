package com.alipay.sofa.doc.model;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
public class DRMDataID {

    /**
     * 资源 ID
     */
    private String resourceId;
    /**
     * 资源域
     */
    private String resourceDomain;
    /**
     * 资源版本
     */
    private String resourceVersion;
    /**
     * 属性名
     */
    private String attributeName;

    /**
     * Alipay.paypluspmtc:name=com.alipay.sofa.middleware.mesh.drm.SecurityResource.RbacFilterConfig,version=3.0@DRM
     * sofa.config:name=sofa.ingress.apps.paypluspmtc.routerRule,version=3.0@DRM
     * <p>
     * 参数说明如下：
     * ● resourceId：资源 ID，和 DRM 资源类的 @DResource 的 ID 保持一致。示例为com.alipay.meshyserver.biz.service.impl.drm.BizDrmConfigResource。
     * ● resourceDomain：资源域，通常是Alipay.$appName，示例为Alipay.meshyserver。
     * ● resourceVersion：资源版本，3.0 或 1.0。
     * ● attributeName：属性名，和 DRM 资源类的属性字段保持一致。示例为bizTypeForLoadJob。
     *
     * @param dataID dataid 字符串
     * @return DRMDataID 对象
     */
    public static DRMDataID parseFrom(String dataID) {
        DRMDataID drmDataID = new DRMDataID();
        String[] rd = dataID.split(":");
        if (rd.length != 2) {
            throw new IllegalArgumentException("illegal dataid: " + dataID);
        }
        drmDataID.resourceDomain = rd[0];

        // rd[1]   name=com.alipay.sofa.middleware.mesh.drm.SecurityResource.RbacFilterConfig,version=3.0@DRM
        String[] rr = rd[1].split(",");
        if (rr.length != 2) {
            throw new IllegalArgumentException("illegal dataid: " + dataID);
        }

        // rr[0] name=com.alipay.sofa.middleware.mesh.drm.SecurityResource.RbacFilterConfig
        String[] ra = rr[0].split("=");
        if (ra.length != 2) {
            throw new IllegalArgumentException("illegal dataid: " + dataID);
        }

        String resourceIdAndAttributeName = ra[1];
        int idx = resourceIdAndAttributeName.lastIndexOf(".");
        drmDataID.resourceId = resourceIdAndAttributeName.substring(0, idx);
        drmDataID.attributeName = resourceIdAndAttributeName.substring(idx + 1);

        // rr[1] version=3.0@DRM
        String[] va = rr[1].split("=");
        if (va.length != 2) {
            throw new IllegalArgumentException("illegal dataid: " + dataID);
        }
        String[] vs = va[1].split("@");
        if (vs.length != 2) {
            throw new IllegalArgumentException("illegal dataid: " + dataID);
        }
        drmDataID.resourceVersion = vs[0];
        return drmDataID;
    }

    public String getResourceId() {
        return resourceId;
    }

    public DRMDataID setResourceId(String resourceId) {
        this.resourceId = resourceId;
        return this;
    }

    public String getResourceDomain() {
        return resourceDomain;
    }

    public DRMDataID setResourceDomain(String resourceDomain) {
        this.resourceDomain = resourceDomain;
        return this;
    }

    public String getResourceVersion() {
        return resourceVersion;
    }

    public DRMDataID setResourceVersion(String resourceVersion) {
        this.resourceVersion = resourceVersion;
        return this;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public DRMDataID setAttributeName(String attributeName) {
        this.attributeName = attributeName;
        return this;
    }

    @Override
    public String toString() {
        return "DRMDataID{" +
                "resourceId='" + resourceId + '\'' +
                ", resourceDomain='" + resourceDomain + '\'' +
                ", resourceVersion='" + resourceVersion + '\'' +
                ", attributeName='" + attributeName + '\'' +
                '}';
    }
}
