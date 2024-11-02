package org.pbl4.pbl4_be.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.pbl4.pbl4_be.security.jwt.AuthEntryPointJwt;
import org.pbl4.pbl4_be.security.jwt.AuthTokenFilter;
import org.pbl4.pbl4_be.services.UserDetailsServiceImpl;

@Configuration
@EnableMethodSecurity  // Cho phép các annotation bảo mật ở cấp độ phương thức như @PreAuthorize
public class WebSecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    // Bean cho AuthTokenFilter - bộ lọc kiểm tra JWT
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    // Cấu hình AuthenticationProvider cho Spring Security với UserDetailsService và PasswordEncoder
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Bean AuthenticationManager giúp xử lý xác thực
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // PasswordEncoder để mã hóa mật khẩu
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Cấu hình chuỗi lọc bảo mật
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Tắt CSRF
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) // Xử lý lỗi xác thực
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Không tạo session
                .authorizeHttpRequests(auth -> // Cấu hình quyền truy cập
                        auth.requestMatchers("/api/auth/**").permitAll() // Cho phép truy cập vào các endpoint auth
                                .requestMatchers("/api/test/**").permitAll() // Cho phép truy cập vào các endpoint thử nghiệm
                                .anyRequest().authenticated() // Yêu cầu xác thực cho tất cả các yêu cầu còn lại
                );

        http.authenticationProvider(authenticationProvider()); // Cấu hình provider cho xác thực

        // Thêm AuthTokenFilter vào chuỗi lọc bảo mật
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}