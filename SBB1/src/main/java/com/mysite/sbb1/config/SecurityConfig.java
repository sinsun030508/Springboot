package com.mysite.sbb1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity //url링크를 타고 들어 갈 수 있게 해줌
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.formLogin(Customizer.withDefaults());
        http.logout(Customizer.withDefaults());

        http.authorizeHttpRequests((authorize) -> authorize
                .requestMatchers("/css/**", "/js/**").permitAll()//css, js 접근 권한 허용
                .requestMatchers("/").permitAll()//루트는 내가 접근안해도 허용
                .requestMatchers("/member/**").permitAll()
                .anyRequest().authenticated());//허용한것이 아니면 로그인화면으로


        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
