package com.johnmartin.coaching.security.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.johnmartin.coaching.utilities.LoggerUtility;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Class<RequestLoggingFilter> clazz = RequestLoggingFilter.class;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        long start = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - start;
            LoggerUtility.d(clazz,
                            request.getMethod() + " " + request.getRequestURI() + " " + response.getStatus() + " ("
                                   + duration + "ms)");
        }
    }
}
