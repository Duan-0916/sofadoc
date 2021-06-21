package com.alipay.sofa.doc.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by zhanggeng on 2019/1/4.
 *
 * @author <a href="mailto:zhanggeng.zg@antfin.com">zhanggeng</a>
 */
public class PropertiesUtils {

    public static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtils.class);

    private static final Properties PROPERTIES = new Properties();

    static {
        PROPERTIES.putAll(System.getProperties());
        try {
            PROPERTIES.load(PropertiesUtils.class.getResourceAsStream("/application.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER.info("{}", PROPERTIES);
    }

    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue);
    }
}
