package com.cos.jwtpractice.config.jwt;

import com.cos.jwtpractice.config.auth.PrincipalDetails;
import com.cos.jwtpractice.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.BufferedReader;
import java.io.IOException;


// 스프링 시큐리티에 UsernamePasswordAuthenticationFilter가 있음.
// post로 login request(username, password)하면 이 필터가 동작한다.
// 근데 우리가 formLogin -> disable해놔서 작동 x
// 이걸 다시 securityConfig에 등록해야 함.

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager; // 무조건 받아야 로그인 진행 가능.

    @Override // /login 요청을 하면 로그인 시도를 위해 실행되는 메서드
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter: 로그인 시도 중");

        // 1. username, password를 받음
//            BufferedReader br = request.getReader();
//            String input = null;
//            while ((input = br.readLine()) != null) {
//                System.out.println(input);
//            }
            //username, password 담김

        // json 형식으로 로그인 진행한다고 가정
       try {
           ObjectMapper om = new ObjectMapper();
           User user = om.readValue(request.getInputStream(), User.class);
           System.out.println("user = " + user);
           UsernamePasswordAuthenticationToken authenticationToken = // 토큰을 만들어줍시다
                   new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
           System.out.println("authenticationToken = " + authenticationToken);

           // PrincipalDetailsService의 loadUserByUsername() 메서드가 실행된다.
           // authentication에는 내 로그인 정보가 담긴다.
           // authentication 객체는 세션 영역에 저장된다. -> 로그인이 되었다는 뜻.
           Authentication authentication = authenticationManager.authenticate(authenticationToken); // 토큰을 날려줍시다.
           // getPrincipal()은 object를 리턴하므로 다운캐스팅해주자.
           PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
           System.out.println(principalDetails.getUser().getUsername());

           return authentication;
        } catch (IOException e) {
           e.printStackTrace();
           //throw new RuntimeException(e);
        }
        System.out.println("===================");
        // 2. 정상인지 로그인 시도. authenticationManager로 로그인 시도
        // PrincipalDetailsService -> loadUserbyUsername()이 실행됨.
        // 3. PrincipalDetails를 세션에 담음 (세션에 담아야만 권한 관리가 가능)
        // 4. JWT토큰을 만들어서 응답
        return null;
    }
}
