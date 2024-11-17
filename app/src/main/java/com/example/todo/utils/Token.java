package com.example.todo.utils;


public class Token {
    public static String createToken(int userId, String username) {
        long expirationTime = System.currentTimeMillis() + (24*60*60*30 * 1000);
        return userId + ":"+ username + ":" + expirationTime;
    }


    public static boolean isTokenValid(String token) {
        String[] tokenParts = token.split(":");
        if (tokenParts.length == 3) {
            long expirationTime = Long.parseLong(tokenParts[2]);
            return System.currentTimeMillis() < expirationTime;
        }
        return false;
    }
    public static String getUserId(String token) {
        String[] tokenParts = token.split(":");
        return tokenParts[0];
    }
    public static String getUserNane(String token) {
        String[] tokenParts = token.split(":");
        return tokenParts[1];
    }
}
