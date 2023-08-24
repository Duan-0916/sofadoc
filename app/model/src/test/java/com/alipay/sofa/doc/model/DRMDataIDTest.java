package com.alipay.sofa.doc.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */

public class DRMDataIDTest {

    @Test
    public void testParse(){
        DRMDataID drmDataID = DRMDataID.parseFrom("Alipay.paypluspmtc:name=com.alipay.sofa.middleware.mesh.drm.SecurityResource.RbacFilterConfig,version=3.0@DRM");
        Assert.assertEquals("Alipay.paypluspmtc", drmDataID.getResourceDomain());
        Assert.assertEquals("com.alipay.sofa.middleware.mesh.drm.SecurityResource", drmDataID.getResourceId());
        Assert.assertEquals("RbacFilterConfig", drmDataID.getAttributeName());
        Assert.assertEquals("3.0", drmDataID.getResourceVersion());

        drmDataID = DRMDataID.parseFrom("sofa.config:name=sofa.ingress.apps.paypluspmtc.routerRule,version=3.0@DRM");
        Assert.assertEquals("sofa.config", drmDataID.getResourceDomain());
        Assert.assertEquals("sofa.ingress.apps.paypluspmtc", drmDataID.getResourceId());
        Assert.assertEquals("routerRule", drmDataID.getAttributeName());
        Assert.assertEquals("3.0", drmDataID.getResourceVersion());

        try {
            drmDataID = DRMDataID.parseFrom("Alipay.paypluspmtcname=com.alipay.sofa.middleware.mesh.drm.SecurityResource.RbacFilterConfig,version=3.0@DRM");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof IllegalArgumentException);
        }
        try {
            drmDataID = DRMDataID.parseFrom("Alipay.paypluspmtc:name=com.alipay.sofa.middleware.mesh.drm.SecurityResource.RbacFilterConfig");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof IllegalArgumentException);
        }
        try {
            drmDataID = DRMDataID.parseFrom("Alipay.paypluspmtc:name=com.alipay.sofa.middleware.mesh.drm.SecurityResource.RbacFilterConfig,version");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof IllegalArgumentException);
        }
        try {
            drmDataID = DRMDataID.parseFrom("Alipay.paypluspmtc:name=com.alipay.sofa.middleware.mesh.drm.SecurityResource.RbacFilterConfig,version=3.0");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof IllegalArgumentException);
        }
    }
}
