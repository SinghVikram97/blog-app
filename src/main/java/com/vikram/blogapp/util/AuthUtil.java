package com.vikram.blogapp.util;

import com.vikram.blogapp.entities.Role;

public class AuthUtil {
    public static boolean isSameUserOrAdmin(String emailFromJWT, String roleFromJWT, String emailFromRequest) {
        return isAdmin(roleFromJWT) && isSameUser(emailFromJWT, emailFromRequest);
    }

    public static boolean isAdmin(String role) {
        return Role.ROLE_ADMIN.toString().equals(role);
    }

    public static boolean isSameUser(String emailFromJWT, String emailFromRequest) {
        return emailFromJWT.equals(emailFromRequest);
    }

}
