package com.example.userservice.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        log.info(">> {} {}", req.getMethod(), req.getRequestURI());
        return true; // продолжить обработку
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception ex) {
        log.info("<< {} {} [status={}]", req.getMethod(), req.getRequestURI(), res.getStatus());
        if (ex != null) {
            log.warn("Handler exception: {}", ex.toString());
        }
    }
}