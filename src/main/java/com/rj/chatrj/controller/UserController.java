package com.rj.chatrj.controller;


import com.rj.chatrj.Util;
import com.rj.chatrj.model.User;
import com.rj.chatrj.model.UserCredetials;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class UserController {

    private JdbcTemplate jdbcTemplate;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public UserController(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("user")
    public UsernamePasswordAuthenticationToken login(@RequestBody UserCredetials credentials) {

        String userName = credentials.getUsername();
        String password = credentials.getPassword();
        logger.info("user attempts to login" + userName);
        final List<GrantedAuthority> grantedAuths = new ArrayList<>();
        User user = new User();
        try{

            Map<String, Object> sysUser = jdbcTemplate.queryForMap("select * from sys_user where username=?",userName);

            if(!Util.checkPass(password,sysUser.get("password").toString())){
                throw new BadCredentialsException("Incorrect username or password");
            }
            Map<String, Object> userInfo = jdbcTemplate.queryForMap("select * from person where user_id=?",sysUser.get("id"));
            Integer unitId = 0;
            try{
                unitId = Integer.parseInt(sysUser.get("unit_id").toString());
            }catch (Exception e){
                e.printStackTrace();
            }

            String token = Util.generateToken(jdbcTemplate,Integer.parseInt(sysUser.get("id").toString()));
            user = new User(userName,password, unitId,userInfo,token);

            jdbcTemplate.query("select r.code from role_user ru inner join role r on r.id=ru.id_role where ru.id_user=?",(rs, rowNum)->{
                grantedAuths.add(new SimpleGrantedAuthority(rs.getString("code")));
                return 0;
            }, sysUser.get("id"));

        }catch (Exception e) {
            logger.error("login failed for user " + userName + " with " + e.getMessage());
            throw new BadCredentialsException(e.getMessage(), e);
        }


        grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
        logger.info("user successfully logged in " + userName );



        return new UsernamePasswordAuthenticationToken(user, password, grantedAuths);

    }

    private String getJWTToken(String username) {
        String secretKey = "mySecretKey";
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");

        String token = Jwts
                .builder()
                .setId("softtekJWT")
                .setSubject(username)
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 600000))
                .signWith(SignatureAlgorithm.HS512,
                        secretKey.getBytes()).compact();

        return "Bearer " + token;
    }
}
