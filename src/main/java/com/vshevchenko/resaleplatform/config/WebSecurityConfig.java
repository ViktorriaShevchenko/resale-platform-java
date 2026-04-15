package com.vshevchenko.resaleplatform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import com.vshevchenko.resaleplatform.filter.BasicAuthCorsFilter;
import com.vshevchenko.resaleplatform.service.CustomUserDetailsService;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Конфигурация безопасности приложения.
 * <p>
 * Настраивает:
 * <ul>
 *   <li>Правила доступа к эндпоинтам</li>
 *   <li>Basic-аутентификацию</li>
 *   <li>CORS настройки</li>
 *   <li>Кастомный UserDetailsService для загрузки пользователей из БД</li>
 * </ul>
 * </p>
 *
 * @author ViktorriaShevchenko
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private static final String[] AUTH_WHITELIST = {
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/webjars/**",
            "/login",
            "/register",
            "/images/**"
    };

    private final CustomUserDetailsService userDetailsService;

    public WebSecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Настраивает цепочку фильтров безопасности.
     *
     * @param http объект HttpSecurity для настройки
     * @return настроенная цепочка фильтров
     * @throws Exception если возникает ошибка конфигурации
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeHttpRequests(
                        authorization ->
                                authorization
                                        .mvcMatchers(HttpMethod.GET, "/ads").permitAll()
                                        .mvcMatchers(HttpMethod.GET, "/ads/{id}").permitAll()
                                        .mvcMatchers(HttpMethod.GET, "/ads/{id}/comments").permitAll()
                                        .mvcMatchers(AUTH_WHITELIST).permitAll()
                                        .mvcMatchers(HttpMethod.POST, "/ads/**").hasAnyRole("USER", "ADMIN")
                                        .mvcMatchers(HttpMethod.PATCH, "/ads/**").hasAnyRole("USER", "ADMIN")
                                        .mvcMatchers(HttpMethod.DELETE, "/ads/**").hasAnyRole("USER", "ADMIN")
                                        .mvcMatchers("/users/**").hasAnyRole("USER", "ADMIN")
                                        .anyRequest().authenticated())
                .cors()
                .and()
                .httpBasic(withDefaults())
                .userDetailsService(userDetailsService)
                .addFilterAfter(new BasicAuthCorsFilter(), BasicAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Создает бин для кодирования паролей.
     *
     * @return кодировщик паролей BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
