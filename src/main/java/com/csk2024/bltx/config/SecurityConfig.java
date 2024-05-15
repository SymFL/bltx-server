package com.csk2024.bltx.config;

import com.csk2024.bltx.config.filter.TokenVerifyFilter;
import com.csk2024.bltx.config.handler.MyAuthenticationFailureHandler;
import com.csk2024.bltx.config.handler.MyAuthenticationSuccessHandler;
import com.csk2024.bltx.config.handler.MyLogoutSuccessHandler;
import com.csk2024.bltx.constant.Constants;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security 配置类
 */
@Configuration
public class SecurityConfig {
    @Resource
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;
    @Resource
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;
    @Resource
    private TokenVerifyFilter tokenVerifyFilter;
    @Resource
    private MyLogoutSuccessHandler myLogoutSuccessHandler;

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, CorsConfigurationSource corsConfigurationSource) throws Exception {
        return httpSecurity
                .formLogin((formLogin) ->
                        formLogin.loginProcessingUrl(Constants.LOGIN_URI)  //登录处理地址
                        .usernameParameter("loginAct")
                        .passwordParameter("loginPwd")
                        .successHandler(myAuthenticationSuccessHandler)
                        .failureHandler(myAuthenticationFailureHandler))

                .authorizeHttpRequests((authorize) -> {
                    authorize.requestMatchers(Constants.LOGIN_URI).permitAll()
                            .anyRequest().authenticated();  //除了登录界面可以放行，其他任何请求都需要登录后才可以访问
                })

                .csrf(AbstractHttpConfigurer::disable)  //禁用跨站请求伪造

                //支持跨域请求
                .cors((cors) -> cors.configurationSource(corsConfigurationSource))

                // session 创建策略
                .sessionManagement((session) ->{
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS); // 无 session 状态，即禁用 session
                })

                // 添加自定义的 Filter
                .addFilterBefore(tokenVerifyFilter, LogoutFilter.class)

                // 退出登录
                .logout((logout) -> {
                    logout.logoutUrl("/api/logout")
                            .logoutSuccessHandler(myLogoutSuccessHandler);
                })

                .build();
    }

    /**
     * 配置跨域请求允许策略
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));    //允许任何来源的请求
        configuration.setAllowedMethods(Arrays.asList("*"));    //允许任何请求方法
        configuration.setAllowedHeaders(Arrays.asList("*"));    //允许任何请求头

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);  //任意路径都使用该规则访问
        return source;
    }

}
