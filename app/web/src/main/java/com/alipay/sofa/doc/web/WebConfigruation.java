package com.alipay.sofa.doc.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
@Configuration
public class WebConfigruation {

   /* @Bean
    public CommonsMultipartResolver commonsMultipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setMaxUploadSize(20*1024*1024);
        return resolver;
    }*/

    @Bean
    public StandardServletMultipartResolver multipartResolver() {
        StandardServletMultipartResolver  resolver = new StandardServletMultipartResolver();
        return resolver;
    }
}
