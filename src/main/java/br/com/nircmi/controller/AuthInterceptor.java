package br.com.nircmi.controller;

import br.com.nircmi.model.User;
import br.com.nircmi.security.RequiresPermission;
import br.com.nircmi.repository.UserRepository;
import br.com.nircmi.controller.TokenStore;
import br.com.nircmi.security.RequiresPermission;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.method.HandlerMethod;



@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final TokenStore tokenStore;
    private final UserRepository userRepository;

    public AuthInterceptor(TokenStore tokenStore, UserRepository userRepository) {
        this.tokenStore = tokenStore;
        this.userRepository = userRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 2. Extract Token from 'auth_token' Cookie
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("auth_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // 3. Validate Token
        if (token == null || !tokenStore.isValidToken(token)) {
            response.sendRedirect("/login");
            return false;
        }

        User user = userRepository.getUserByID(tokenStore.getTokenValue(token));
        request.setAttribute("currentUser", user);

        return true;
    }
}
