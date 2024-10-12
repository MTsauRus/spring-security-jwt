package com.cos.jwtpractice.config;

import com.cos.jwtpractice.filter.MyFilter1;
import com.cos.jwtpractice.filter.MyFilter2;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<MyFilter1> filter1() {
        FilterRegistrationBean<MyFilter1> bean = new FilterRegistrationBean<>(new MyFilter1());
        bean.addUrlPatterns("/*"); // 모든 Url에 대하여
        bean.setOrder(0);  // 번호가 낮을수록 우선순위가 높다.
        return bean;
    }

    @Bean
    public FilterRegistrationBean<MyFilter2> filter2() {
        FilterRegistrationBean<MyFilter2> bean = new FilterRegistrationBean<>(new MyFilter2());
        bean.addUrlPatterns("/*"); // 모든 Url에 대하여
        bean.setOrder(1);  // 번호가 낮을수록 우선순위가 높다.
        return bean;
    }

}
