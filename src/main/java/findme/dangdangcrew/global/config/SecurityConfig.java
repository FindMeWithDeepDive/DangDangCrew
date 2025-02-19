package findme.dangdangcrew.global.config;

import findme.dangdangcrew.user.repository.UserRepository;
import findme.dangdangcrew.user.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new CustomUserDetailsService(userRepository);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtTokenFilter jwtTokenFilter = new JwtTokenFilter(jwtTokenProvider, userDetailsService); // 필터 직접 생성

        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/actuator/**",
                                "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**",
                                "/api/v1/**", "/ws/**","/test/**"
                        ).permitAll()
                        .requestMatchers("/api/v1/users/logout").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class) // 필터 직접 추가
                .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }

}
