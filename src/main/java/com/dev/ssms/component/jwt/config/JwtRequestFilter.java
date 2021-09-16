package com.dev.ssms.component.jwt.config;

import com.dev.ssms.component.constant.AppConstant;
import com.dev.ssms.component.constant.CurrentUser;
import com.dev.ssms.component.enums.Status;
import com.dev.ssms.component.jwt.service.JwtTokenUtil;
import com.dev.ssms.exception.BadResponseException;
import com.dev.ssms.exception.ExpiredTokenException;
import com.dev.ssms.exception.UnauthorizedException;
import com.dev.ssms.model.User;
import com.dev.ssms.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter implements HandlerInterceptor {


    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public JwtRequestFilter(UserRepository userRepository, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException, ExpiredJwtException {

        final String requestTokenHeader = request.getHeader(AppConstant.JSON_HEADER);
        String email = null;
        String jwtToken = null;

        if (requestTokenHeader == null) {
            chain.doFilter(request, response);
            return;
        }

        // JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
        else if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ") || !requestTokenHeader.startsWith("Bearer ")) {
            if (requestTokenHeader.startsWith("Bearer ")) {
                jwtToken = requestTokenHeader.substring(7);
            } else {
                jwtToken = requestTokenHeader;
            }
            try {
                email = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (ExpiredJwtException e) {
                throw new ExpiredTokenException("Token Has Been Expired");
            } catch (Exception e) {
                throw new UnauthorizedException("Authentication token invalid");
            }
        }

        // Once we get the token validate it.
        try {
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = this.userRepository.findByEmailAndStatus(email, Status.APPROVED.getStatusSeq());
                if (jwtTokenUtil.validateToken(jwtToken, user)) {
                    CurrentUser.setUser(user);
                    chain.doFilter(request, response);
                }
            }
        } catch (Exception e) {
            throw new BadResponseException(e.getMessage());
        }
    }
}
