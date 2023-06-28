package com.alipay.sofa.doc.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

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
        StandardServletMultipartResolver resolver = new StandardServletMultipartResolver();
        return resolver;
    }

    @Bean
    public GenericFilterBean cachingRequestBodyFilter() {
        return new CachingRequestBodyFilter();
    }

    public static class CachingRequestBodyFilter extends GenericFilterBean {

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
                throws IOException, ServletException {
            HttpServletRequest currentRequest = (HttpServletRequest) servletRequest;
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(currentRequest);

            chain.doFilter(wrappedRequest, servletResponse);
        }
    }
}
