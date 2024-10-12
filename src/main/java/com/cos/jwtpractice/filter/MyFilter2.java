package com.cos.jwtpractice.filter;

import jakarta.servlet.*;

import java.io.IOException;

public class MyFilter2 implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("필터2");
        chain.doFilter(request, response); // 1회성에 그치지 않고 다시 체인에 등록
        // securityConfig에 등록해주자.
    }
}
