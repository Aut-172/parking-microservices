package com.demo.authservice.filter;

import com.demo.common.util.JsonUtils;
import com.demo.common.util.JwtUtils;
import com.demo.common.entity.User;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        // 登录接口不需要验证
        if (!(uri.equals("/auth/login")||uri.equals("/auth/register"))) {
            try {
                validateToken(request);
            } catch (Exception e) {
                // 验证失败，返回 401
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"msg\":\"无效的token\"}");
                return;
            }
        }
        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }

    private void validateToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            token = request.getParameter("Authorization");
        }
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("token为空");
        }

        Claims claims;
        try {
            claims = JwtUtils.parseJWT(token);
        } catch (Exception e) {
            throw new RuntimeException("token校验失败");
        }

        String subject = claims.getSubject();
        User loginUser = JsonUtils.fromJson(subject, User.class);
        if (loginUser == null) {
            throw new RuntimeException("用户信息解析失败");
        }

        // 根据角色构建权限列表
        List<GrantedAuthority> authorities = new ArrayList<>();
        Integer role = loginUser.getRole();
        if (role != null) {
            if (role == 0) {
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            } else if (role == 1) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
        }

        // 使用ID作为认证主体的名称
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginUser.getId(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}