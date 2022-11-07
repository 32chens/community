package com.chenlf.community.config;

import com.chenlf.community.util.CommunityUtil;
import com.chenlf.community.util.SystemConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 
 * @author ChenLF
 * @date 2022/11/07 20:40
 **/

@Configuration
@EnableWebSecurity
public class SecurityConfig {

//    @Autowired
//    private UserDetailsServiceImpl userDetailsService;

    //获取AuthenticationManager（认证管理器），登录时认证使用
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    //配置过滤
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()//关闭csrf
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//关闭session
                .and()
                .authorizeRequests(auth ->
                        {
                            try {
                                auth.antMatchers("/resources/**").permitAll()
                                    .antMatchers(
                                            "/user/setting",
                                            "/user/upload",
                                            "/discuss/add",
                                            "/comment/add/**",
                                            "/letter/**",
                                            "/notice/**",
                                            "/like",
                                            "/follow",
                                            "/unfollow"
                                            )
                                    .hasAnyAuthority(
                                            SystemConstants.AUTHORITY_USER,
                                            SystemConstants.AUTHORITY_ADMIN,
                                            SystemConstants.AUTHORITY_MODERATOR
                                    )
                                    .anyRequest().permitAll().and().csrf().disable();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                );
                http.exceptionHandling()//权限不够的处理
                    .authenticationEntryPoint(new AuthenticationEntryPoint() {
                        //没有登录
                        @Override
                        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                            String xRequestedWith = request.getHeader("x-requested-with");
                            if ("XMLHttpRequest".equals(xRequestedWith)){//异步
                                response.setContentType("application/plain;charset=utf-8");
                                PrintWriter writer = response.getWriter();
                                writer.write(CommunityUtil.getJSONString(403,"你还没有登录"));
                            }else{//同步
                                response.sendRedirect(request.getContextPath()+ "/login");
                            }
                        }
                    })
                    .accessDeniedHandler(new AccessDeniedHandler() {
                        //权限不足
                        @Override
                        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                            String xRequestedWith = request.getHeader("x-requested-with");
                            if ("XMLHttpRequest".equals(xRequestedWith)){//异步
                                response.setContentType("application/plain;charset=utf-8");
                                PrintWriter writer = response.getWriter();
                                writer.write(CommunityUtil.getJSONString(403,"你没有访问该功能的权限"));
                            }else{//同步
                                response.sendRedirect(request.getContextPath()+ "/denied");
                            }
                        }
                    });

                http.logout().logoutUrl("/securitylogout");//用一个不存在的路径,避免之前写的logout逻辑不执行
                return http.build();
    }

//    //配置加密方式
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    //配置跨源访问(CORS)
    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
}
