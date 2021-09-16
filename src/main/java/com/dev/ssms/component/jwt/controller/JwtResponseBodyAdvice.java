package com.dev.ssms.component.jwt.controller;

import com.dev.ssms.component.constant.AppConstant;
import com.dev.ssms.component.constant.CurrentUser;
import com.dev.ssms.component.jwt.service.JwtTokenUtil;
import com.dev.ssms.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class JwtResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object object, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        User user = CurrentUser.getUser();
        if (user == null) {
            return object;
        } else {
            final String token = jwtTokenUtil.generateToken(user);
            serverHttpResponse.getHeaders().add(AppConstant.JSON_HEADER, token);
            return object;
        }
    }
}
