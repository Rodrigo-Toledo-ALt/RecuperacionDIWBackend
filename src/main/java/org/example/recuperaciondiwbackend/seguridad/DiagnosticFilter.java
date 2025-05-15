package org.example.recuperaciondiwbackend.seguridad;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DiagnosticFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Log detailed request information for debugging
        log.info("DiagnosticFilter: ======= DETAILED REQUEST INFO =======");
        log.info("DiagnosticFilter: Procesando solicitud a URI: {}", request.getRequestURI());
        log.info("DiagnosticFilter: Método HTTP: {}", request.getMethod());
        log.info("DiagnosticFilter: Servlet Path: {}", request.getServletPath());
        log.info("DiagnosticFilter: Context Path: {}", request.getContextPath());
        log.info("DiagnosticFilter: Path Info: {}", request.getPathInfo());
        log.info("DiagnosticFilter: Query String: {}", request.getQueryString());
        log.info("DiagnosticFilter: Request URL: {}", request.getRequestURL());
        
        // Log all request headers
        log.info("DiagnosticFilter: === HEADERS ===");
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            log.info("DiagnosticFilter: Header {} = {}", headerName, request.getHeader(headerName));
        }
        
        // Log request parameters
        log.info("DiagnosticFilter: === PARAMETERS ===");
        java.util.Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            log.info("DiagnosticFilter: Parameter {} = {}", paramName, request.getParameter(paramName));
        }
        
        // Continue with the filter chain
        log.info("DiagnosticFilter: === STARTING FILTER CHAIN ===");
        filterChain.doFilter(request, response);
        
        // Log the response
        log.info("DiagnosticFilter: === RESPONSE ===");
        log.info("DiagnosticFilter: Response Status: {}", response.getStatus());
        response.getHeaderNames().forEach(name -> 
            log.info("DiagnosticFilter: Response Header {} = {}", name, response.getHeader(name))
        );
        log.info("DiagnosticFilter: ======= END OF REQUEST PROCESSING =======");
    }
}