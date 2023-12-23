package com.vikram.blogapp.constants;

import java.util.Collections;
import java.util.List;

public final class Constants {
    public static final String PAGINATION_DEFAULT_PAGE_NUMBER = "0";
    public static final String PAGINATION_DEFAULT_PAGE_SIZE = "10";
    public static final String PAGINATION_DEFAULT_SORT_BY = "id";

    public static final String PAGINATION_DEFAULT_SORT_DIR = "asc";

    public static final int JWT_EXPIRY = 1000*60*24; // 1 day

    public static final String MDC_USERNAME_KEY = "username";

    public static final String MDC_ROLE_KEY = "role";

    public static final List<String> WHITELISTED_ENDPOINTS = List.of(
            "/api/auth/register",
            "/api/auth/login");


    private Constants() {
    }
}
