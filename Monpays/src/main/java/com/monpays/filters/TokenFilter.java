package com.monpays.filters;

import com.monpays.services.implementations._generic.stateful_services.AuthenticationRecord;
import com.monpays.utils.JwtUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Order(1024)
@Log
@Component
public class TokenFilter implements Filter {
    static List<String> urlsWithoutToken = List.of(
            "/sign_in",
            "/sign_up",
            "/forbidden",
            "/verify"
    );

    @Autowired
    private AuthenticationRecord authenticationRecord;
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void doFilter
            (ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        log.info("Entered " + getClass().getName());

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        boolean isUrlWithoutToken = isUrlWithoutToken(httpRequest.getRequestURL().toString());

        String ipAddress = getClientIpAddress(httpRequest); // Get the user's IP address

        if (!isUrlWithoutToken && !Objects.equals(httpRequest.getMethod(), "OPTIONS")) {
            try {
                String username = jwtUtils.validateToken(httpRequest.getHeader("token"));
                httpRequest.setAttribute("username", username);
                log.info(username + ", token is valid! IP: " + ipAddress);
            }
            catch (Exception e) {
                ((HttpServletResponse) response).sendError(403, "Token is invalid");
                log.info("Token is invalid! IP: " + ipAddress);
                return;
            }
        }
        else {
            log.info("New request without token. IP: " + ipAddress);
        }

        chain.doFilter(httpRequest, response);
        log.info("Exiting " + getClass().getName());
    }

    private boolean isUrlWithoutToken(String reqUrl) {
        for (String url : urlsWithoutToken) {
            if (reqUrl.endsWith(url)) {
                return true;
            }
        }
        return false;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        /*
         * X-Forwarded-For: Used by proxies and load balancers to pass along the original client's IP address.
         * Proxy-Client-IP: Legacy header used by some proxies to pass the client's IP address.
         * WL-Proxy-Client-IP: Header used by Oracle WebLogic Server's proxy plugin to pass the client's IP address.
         * HTTP_X_FORWARDED_FOR: Another variation of the X-Forwarded-For header.
         */
        List<String> headerNames = Arrays.asList(
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR"
        );

        for (String headerName : headerNames) {
            String ipAddress = getHeaderValue(request, headerName);
            if (!isEmptyOrUnknown(ipAddress)) {
                return ipAddress;
            }
        }

        return request.getRemoteAddr();
    }

    private String getHeaderValue(HttpServletRequest request, String headerName) {
        String headerValue = request.getHeader(headerName);
        return isEmptyOrUnknown(headerValue) ? null : headerValue;
    }

    private boolean isEmptyOrUnknown(String value) {
        return value == null || value.isEmpty() || "unknown".equalsIgnoreCase(value);
    }

}
