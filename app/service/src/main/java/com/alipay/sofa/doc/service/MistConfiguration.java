package com.alipay.sofa.doc.service;

import com.alipay.mist.sdk.MistClient;
import com.alipay.mist.sdk.MistSDKAutoConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
@Configuration
@ConditionalOnProperty(havingValue = "false", prefix = "sofa.doc", name = "yuque.token", matchIfMissing = true)
@ConfigurationProperties(prefix = "sofa.doc.mist")
public class MistConfiguration {

    @Value("${spring.application.name}")
    String appName;

    String mode;
    String tenant;
    HashMap<String, String> factors = new HashMap<>();

    @Bean
    MistSDKAutoConfig mistSDKAutoConfig(){
        // https://yuque.antfin-inc.com/tecsec/vng4vi/ses69b#uC5Jt
        MistSDKAutoConfig mistSDKAutoConfig = new MistSDKAutoConfig();
        mistSDKAutoConfig.setAppName(appName);
        mistSDKAutoConfig.setMode(mode);
        mistSDKAutoConfig.setTenant(tenant);
        mistSDKAutoConfig.setAccessType("factor");
        mistSDKAutoConfig.setFactorInfo(factors);
        return mistSDKAutoConfig;
    }

    @Bean
    MistClient mistClient() {
        return new MistClient(mistSDKAutoConfig());
    }

    public String getMode() {
        return mode;
    }

    public MistConfiguration setMode(String mode) {
        this.mode = mode;
        return this;
    }

    public String getTenant() {
        return tenant;
    }

    public MistConfiguration setTenant(String tenant) {
        this.tenant = tenant;
        return this;
    }

    public HashMap<String, String> getFactors() {
        return factors;
    }

    public MistConfiguration setFactors(HashMap<String, String> factors) {
        this.factors = factors;
        return this;
    }
}
