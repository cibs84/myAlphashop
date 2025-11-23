package com.alphashop.jwt_auth_service.security.constants;

public class PublicRoutes {
    public static final String LOGIN = "/api/authentication/login";
    public static final String REFRESH = "/api/authentication/refresh";
    public static final String LOGOUT = "/api/authentication/logout";
    public static final String PUBLIC_ROUTES = "/api/authentication/public-routes";

    public static String[] all() {
        return new String[] { LOGIN, REFRESH, LOGOUT, PUBLIC_ROUTES };
    }
}
