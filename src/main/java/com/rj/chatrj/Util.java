package com.rj.chatrj;

import com.rj.chatrj.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.sql.DataSource;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class Util {


    private JdbcTemplate jdbcTemplate;


    private DataSource dataSource;

    @Autowired
    public Util(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    private static String SECRET_KEY = "cox_damigeana_pomana";


    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    public static boolean checkPass(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    public static String generateToken(JdbcTemplate db, Integer userId) throws SQLException, ClassNotFoundException {
        String token = createJWT(userId, ApplicationProperties.getTokenValability());
        java.sql.Timestamp expires = new java.sql.Timestamp(System.currentTimeMillis() + ApplicationProperties.getTokenValability());
        db.update("insert into token(token, user_id, expires) values (?,?,?)",token, userId, expires);
        return token;
    }

    public static String createJWT(Integer id, long ttlMillis) {

        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(id.toString())
                .signWith(signatureAlgorithm, signingKey);

        //if it has been specified, let's add the expiration
        if (ttlMillis > 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    public static Claims decodeJWT(String jwt) {
        //This line will throw an exception if it is not a signed JWS (as expected)
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
                .parseClaimsJws(jwt).getBody();
        return claims;
    }


    public static Integer getIntegerOrNullFromResultSet(ResultSet rs, String col) throws Exception {
        Integer ret = null;

        Object object = rs.getObject(col);
        if (object != null) {
            try {
                ret = rs.getInt(col);
            } catch (Exception e) {
                ret = null;
            }
        }

        return ret;
    }

    public static boolean getBooleanFromResultSet(ResultSet rs, String col) throws Exception {
        boolean ret = false;

        Object object = rs.getObject(col);
        if (object != null) {
            try {
                ret = rs.getString(col).equals("1") || rs.getString(col).equals("true");
            } catch (Exception e) {
                ret = false;
            }
        }

        return ret;
    }

    public static Integer castToIntegerOrReturnNull(String number) throws Exception {
        Integer ret = null;
        try {
            ret = Integer.parseInt(number);
        } catch (Exception e) {
            ret = null;
        }
        return ret;
    }

    public static synchronized Pair<String, String> getMessageAndStackTrace(Throwable th) {
        String message = th.getMessage();
        if (th.getCause() != null && th.getCause().getMessage() != null) {
            message = th.getCause().getMessage();
        }
        if (message == null) {
            message = "Unknown error";
        }

        StringBuilder sb = new StringBuilder();
        for (StackTraceElement ste : th.getStackTrace()) {
            sb.append(ste).append("\n");
        }

        return new Pair<>(message, sb.toString());
    }

    public static Integer nextSeqVal(JdbcTemplate db, String seqName) {
        return db.queryForObject("select next value for " + seqName + " seq ", Integer.class);
    }


    public static Integer getUserIdFromAuth(Authentication authentication){
        return Integer.parseInt(((User) ((UsernamePasswordAuthenticationToken) authentication).getPrincipal()).getUserInfo().get("user_id").toString());
    }


}
