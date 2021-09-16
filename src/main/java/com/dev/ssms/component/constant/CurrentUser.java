package com.dev.ssms.component.constant;

import com.dev.ssms.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CurrentUser {
    private static final Logger LOGGER = LogManager.getLogger(CurrentUser.class);

    User user;
    Map<String, String> extras;

    public CurrentUser(User user, Map<String, String> extras) {
        this.user = user;
        this.extras = extras;
    }

    public static void setUser(User user, Map<String, String> extras) {
        LOGGER.info("Enter setUser() in CurrentUser.");
        LOGGER.info("Request : " + user);
        CurrentUser currentUser = new CurrentUser(user, extras);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ADMIN"));
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(currentUser, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

    public static User getUser() {
        LOGGER.info("Enter getUser() in CurrentUser.");
        CurrentUser cUser = getCurrentUser();
        return cUser != null ? cUser.user : null;
    }

    public static void setUser(User user) {
        LOGGER.info("Enter setUser() in CurrentUser.");
        LOGGER.info("Request : " + user);
        CurrentUser.setUser(user, null);
    }

    private static CurrentUser getCurrentUser() {
        LOGGER.info("Enter getCurrentUser() in CurrentUser.");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof CurrentUser) {
            LOGGER.info("Response : " + ((CurrentUser) authentication.getPrincipal()).user);
            return (CurrentUser) authentication.getPrincipal();
        }
        return null;
    }
}