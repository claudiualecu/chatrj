package com.rj.chatrj;


import com.rj.chatrj.model.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JWTAuthorizationFilter extends OncePerRequestFilter {
    private final String HEADER = "Authorization";
    private final String PREFIX = "Bearer ";
    private final String SECRET = "mySecretKey";
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JWTAuthorizationFilter(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            if (checkJWTToken(request, response)) {
                User user = validateToken(request);
                if (user.getUsername() != null) {
                    setUpSpringAuthentication(user, getUserRoles(user.getId()));
                } else {
                    SecurityContextHolder.clearContext();
                }
            }else {
                SecurityContextHolder.clearContext();
            }
            chain.doFilter(request, response);
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | IllegalAccessException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            return;
        }
    }

    private User validateToken(HttpServletRequest request) throws IllegalAccessException {
        User user = new User();
        String jwtToken = request.getHeader(HEADER).replace(PREFIX, "");
        Integer userId = jdbcTemplate.queryForObject("select user_id from token where token=? and expires>current_timestamp",Integer.class, jwtToken);
        if(userId!=null){
            Map<String, Object> sysUser = jdbcTemplate.queryForMap("select * from sys_user where id=?",userId);
            Map<String, Object> userInfo = jdbcTemplate.queryForMap("select * from person where user_id=?",userId);
            user.setId(Integer.parseInt(sysUser.get("id").toString()));
            user.setUsername(sysUser.get("username").toString());
            user.setPassword(sysUser.get("password").toString());
            user.setToken(jwtToken);
            user.setUnitId(Integer.parseInt(sysUser.get("unit_id").toString()));
            user.setUserInfo(userInfo);
        }else{
            throw new IllegalAccessException("Invalid token");
        }
        return user;
    }

    /**
     * Authentication method in Spring flow
     *
     * @param user
     * @param grantedAuth
     */
    private void setUpSpringAuthentication(User user, List<GrantedAuthority> grantedAuth) {
        @SuppressWarnings("unchecked")
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, user.getPassword(), grantedAuth);
        SecurityContextHolder.getContext().setAuthentication(auth);

    }

    private boolean checkJWTToken(HttpServletRequest request, HttpServletResponse res) {
        String authenticationHeader = request.getHeader(HEADER);
        if (authenticationHeader == null || !authenticationHeader.startsWith(PREFIX))
            return false;
        return true;
    }

    private List<GrantedAuthority> getUserRoles (Integer userId){
        List<GrantedAuthority> userRoles = new ArrayList<>();
        jdbcTemplate.query("select r.code from role_user ru inner join role r on r.id=ru.id_role where ru.id_user=?",(rs, rowNum)->{
            userRoles.add(new SimpleGrantedAuthority(rs.getString("code")));
            return 0;
        }, userId);
        return userRoles;
    }
}
