package com.alipay.sofa.doc.service;

import com.alipay.mist.sdk.MistClient;
import com.alipay.mist.utils.MistSDKException;
import com.alipay.sofa.doc.integration.drm.YuqueTokenDrm;
import com.alipay.sofa.doc.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
@Component
public class TokenService {

    public static final Logger LOGGER = LoggerFactory.getLogger(TokenService.class);

    @Autowired(required = false)
    MistClient mistClient;

    @Autowired(required = false)
    private YuqueTokenDrm yuqueTokenDrm;

    @Value("${sofa.doc.yuque.token:}")
    String defaultYuqueToken;

    /**
     * get token by yuque user and yuque namespace
     *
     * @param yuqueUser yuque user
     * @return yuque token
     */
    public String getTokenByUser(String yuqueUser) {
        // 优先从 drm 里取
        String token = null;
        if (yuqueTokenDrm != null) {
            token = yuqueTokenDrm.getTokenByUser(yuqueUser);
            if (StringUtils.isNotBlank(token)) {
                return token;
            }
        }
        // 再从 mist 里取
        if (mistClient != null) {
            if (StringUtils.isNotEmpty(yuqueUser)) {
                try {
                    return mistClient.getSecret("other_manual_sofadoc_" + yuqueUser);
                } catch (MistSDKException e) {
                    LOGGER.error("Query token of " + yuqueUser + " from mist error!", e);
                }
            } else {
                throw new IllegalArgumentException("yuqueUser is null, please add user info in secretmng.");
            }
        }
        return defaultYuqueToken;
    }
}
