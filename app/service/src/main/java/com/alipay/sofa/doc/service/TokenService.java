package com.alipay.sofa.doc.service;

import com.alipay.mist.sdk.MistClient;
import com.alipay.sofa.doc.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
@Component
public class TokenService {

    @Autowired(required = false)
    MistClient mistClient;

    @Value("${sofa.doc.yuque.token:}")
    String defaultYuqueToken;

    /**
     * get token by yuque user and yuque namespace
     *
     * @param yuqueUser yuque user
     * @return yuque token
     */
    public String getTokenByUser(String yuqueUser) {
        if (mistClient != null) {
            if (StringUtils.isNotEmpty(yuqueUser)) {
                return mistClient.getSecret("other_manual_sofadoc_" + yuqueUser);
            }
            throw new IllegalArgumentException("yuqueUser is null, please add user info in secretmng.");
        } else {
            return defaultYuqueToken;
        }
    }
}
