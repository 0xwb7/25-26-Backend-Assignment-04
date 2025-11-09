package org.example.simplejwtexample.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.simplejwtexample.constants.Constants;
import org.example.simplejwtexample.exception.BadRequestException;
import org.example.simplejwtexample.exception.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private final TokenProvider tokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String token = tokenProvider.resolveToken(httpServletRequest);

        if (StringUtils.hasText(token)) {
            if (!tokenProvider.validateToken(token)) {
                setErrorResponse(httpServletResponse, HttpStatus.UNAUTHORIZED.value(), ErrorMessage.INVALID_TOKEN.getMessage());
                return;
            }
            try {
                Authentication authentication = tokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (BadRequestException e) {
                ((HttpServletResponse) response).setStatus(HttpStatus.BAD_REQUEST.value());
                response.setContentType(Constants.CONTENT_TYPE);
                response.getWriter().write(Constants.MESSAGE_INTRO + e.getMessage() + Constants.MESSAGE_OUTRO);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private void setErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(Constants.CONTENT_TYPE);
        response.getWriter().write(Constants.MESSAGE_INTRO + message + Constants.MESSAGE_OUTRO);
    }
}
