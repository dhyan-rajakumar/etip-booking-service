package com.etip.booking.ApiLoggingInterceptor;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.etip.booking.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class ApiLoggingInterceptor implements HandlerInterceptor {
    private static final Logger log =
            LoggerFactory.getLogger(ApiLoggingInterceptor.class);
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        request.setAttribute("startTime",System.currentTimeMillis());
        return true;
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                              Object handler, ModelAndView modelAndView) throws Exception{
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception e) throws Exception{

        long startTime = (long) request.getAttribute("startTime");
        long executionTime = System.currentTimeMillis() - startTime;

        log.info("API={} Method={} Status={} ExecutionTime={}ms",
                request.getRequestURI(), request.getMethod(),
                response.getStatus(),executionTime);
    }
}
