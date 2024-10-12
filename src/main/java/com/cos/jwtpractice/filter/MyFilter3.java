package com.cos.jwtpractice.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class MyFilter3 implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request; // 상위 인터페이스로 캐스팅
        HttpServletResponse res = (HttpServletResponse) response;

        // 토큰 이름: hgr. 토큰이 hgr일 때에만 진입하고, 아니면 컨트롤러 접근을 막음.
        // ID, PW 정상적으로 로그인되면 토큰을 만들어주고 응답함.
        // 요청 때마다 header authorization에 value 값으로 토큰을 가지고 옴
        // 그때 토큰이 넘어오면 이 토큰이 내가 만든 토큰이 맞는지만 검증하면 됨. (RSA, HS256)
        if (req.getMethod().equals("POST")) {
            System.out.println("POST request");
            String headerAuth = req.getHeader("Authorization");
            System.out.println("headerAuth = " + headerAuth);
            System.out.println("필터3");

            if (headerAuth.equals("hgr")) {
                chain.doFilter(request, response); // 헤더 이름이 일치할 때에만 필터를 타게 하자.
            } else {
                PrintWriter out = res.getWriter();
                out.println("인증안됨");
            }
        }
    }
}
