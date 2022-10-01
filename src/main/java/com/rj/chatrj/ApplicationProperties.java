package com.rj.chatrj;

public class ApplicationProperties {

    private static String url;
    private static String noContextToken;
    private static String systemRoot;

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String url) {
        ApplicationProperties.url = url;
    }

    public static String getNoContextToken() {
        return noContextToken;
    }

    public static void setNoContextToken(String noContextToken) {
        ApplicationProperties.noContextToken = noContextToken;
    }

    public static String getSystemRoot() {
        return systemRoot;
    }

    public static void setSystemRoot(String systemRoot) {
        ApplicationProperties.systemRoot = systemRoot;
    }
}
