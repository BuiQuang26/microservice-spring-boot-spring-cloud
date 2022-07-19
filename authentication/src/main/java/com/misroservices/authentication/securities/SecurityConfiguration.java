package com.misroservices.authentication.securities;

import com.misroservices.authentication.entities.RoleConstance;
import com.misroservices.authentication.repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfiguration {

    private static final String[] AUTH_WHITELIST = {
            // -- Swagger UI v2
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            // -- Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/swagger-ui/**",
            // other public endpoints of your API may be appended to this array
            "/api/auth/register",
            "/api/auth/login"
    };

    private static final String[] AUTH_ROUTE_ADMIN = {
            "/api/admin/**"
    };

    private final CustomUserDetailService userDetailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final CustomExceptionEntryPoint customExceptionEntryPoint;
    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfiguration(CustomUserDetailService userDetailService, BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository, CustomExceptionEntryPoint customExceptionEntryPoint, JwtTokenProvider jwtTokenProvider) {
        this.userDetailService = userDetailService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.customExceptionEntryPoint = customExceptionEntryPoint;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //Authentication
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailService).passwordEncoder(bCryptPasswordEncoder);
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
        http.authenticationManager(authenticationManager);

        //http route filter
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter( userRepository, authenticationManager, jwtTokenProvider);
        customAuthenticationFilter.setFilterProcessesUrl("/api/auth/login");
        http.cors().and().csrf().disable().exceptionHandling()
                .authenticationEntryPoint(customExceptionEntryPoint)
                .and().authorizeHttpRequests()
                .antMatchers(AUTH_WHITELIST).permitAll()
                .antMatchers(AUTH_ROUTE_ADMIN).hasAnyRole(RoleConstance.ADMIN.toString())
                .anyRequest().authenticated();
        http.addFilter(customAuthenticationFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(authenticationManager, jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("*");
            }
        };
    }

}
