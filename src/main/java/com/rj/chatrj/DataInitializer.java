package com.rj.chatrj;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;


@Component
public class DataInitializer {

    @Value("${custom.settings.main.url}")
    private String url;

    @Value("${custom.settings.main.noContextToken}")
    private String noContextToken;

    @Value("${application.mode.dev:false}")
    private boolean devMode;

    @Value("${custom.settings.system.systemRoot}")
    private String systemRoot;

    @Value("${token.valability:3600000}")
    private long tokenValability;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void initializeApplicationProperties() {
        ApplicationProperties.setUrl(url);
        ApplicationProperties.setNoContextToken(noContextToken);
        ApplicationProperties.setSystemRoot(systemRoot);
        ApplicationProperties.setTokenValability(tokenValability);
    }

    public void initializeSystemProperties() {
        System.setProperty("system.root", systemRoot != null ? systemRoot : "C:/");
    }

}
