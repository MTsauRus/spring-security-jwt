package com.cos.jwtpractice.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.jwtpractice.config.auth.PrincipalDetails;
import com.cos.jwtpractice.config.auth.UserRepository;
import com.cos.jwtpractice.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

// 시큐리티에 BasicAuthenticationFilter가 있음.
// 권한, 인증이 필요한 특정 주소를 요청했을 때 이 필터를 무조건 타게 되어있음.
// 권한 및 인증이 필요 없는 주소인 경우 해당 필터를 타지 않음.
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;

    }

    // 인증이나 권한이 필요한 주소요청이 있을 때 해당 필터를 타게 됨.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("인증/권한 필요 주소 요청 (BasicAuthenticationFilter)");

        // authorization 헤더 값을 받아옴. (토큰 값)
        // 이걸 검증해서 정상적인 사용자인지 검증할 것임
        String jwtHeader = request.getHeader("Authorization");
        System.out.println("jwtHeader = " + jwtHeader);

        // 만약 헤더에 토큰이 없거나 정상적이지 않다면 다시 체인 타게 보내고 리턴
        // 헤더 자체가 있는지 확인하는 부분
        if (jwtHeader == null || !jwtHeader.startsWith("Bearer")) {
            chain.doFilter(request, response);
            return;
        }

        // JWT 토큰을 검증. 정상적인 사용자인지 확인
        // Bearer 부분을 삭제하고 토큰만 가져옴
        String jwtToken = request.getHeader("Authorization").replace("Bearer ", "");
        // 서명하는 부분. 서명이 잘 되었다면 claim의 username을 가져와 스트링으로 캐스팅해온다.
        String username = JWT.require(Algorithm.HMAC512("hgr"))
                .build().verify(jwtToken).getClaim("username").asString();

        // 서명이 정상적으로 되었다면
        if (username != null) {
            User userEntity = userRepository.findByUsername(username);

            PrincipalDetails principalDetails = new PrincipalDetails(userEntity);
            // Authenication 객체를 강제로 만듦.
            // 그 전에는 authenticationManager.authenticate(authenticationToken); 이걸 실행한 후 로그인에 성공하면 객체 리턴해줬음.
            // Jwt 토큰 서명을 통해 서명이 정상이면 Authentication 객체를 만들어줌.
            Authentication authentication = // 세 번째 param은 권한 부여
                    new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities()); // 원래 두번째 null 자리에 비밀번호를 넣어야댐

            // security를 저장할 수 있는 session
            // 강제로 시큐리티 세션에 접근하여 Authentication 객체를 넣어줌.
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("authentication = " + authentication + "1231232132332");

            // 다 했음. 이제 다시 체인으로 돌려보냄.
            chain.doFilter(request, response);
        }

    }
}
