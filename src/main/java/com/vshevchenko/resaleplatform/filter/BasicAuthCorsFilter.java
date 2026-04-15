package com.vshevchenko.resaleplatform.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Фильтр для добавления CORS-заголовков к ответам.
 * <p>
 * Добавляет заголовок Access-Control-Allow-Credentials: true
 * для поддержки кросс-доменных запросов с учетными данными.
 * </p>
 *
 * @author ViktorriaShevchenko
 * @version 1.0
 */
@Component
public class BasicAuthCorsFilter extends OncePerRequestFilter {

    /**
     * Добавляет CORS-заголовки к каждому ответу и пропускает запрос дальше по цепочке.
     *
     * @param httpServletRequest входящий запрос
     * @param httpServletResponse исходящий ответ
     * @param filterChain цепочка фильтров
     * @throws ServletException если возникает ошибка сервлета
     * @throws IOException если возникает ошибка ввода-вывода
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        httpServletResponse.addHeader("Access-Control-Allow-Credentials", "true");
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
