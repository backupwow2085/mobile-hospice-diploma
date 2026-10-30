package ru.edu.qamid.data;

import androidx.test.platform.app.InstrumentationRegistry;

public class Credentials {
    private Credentials() {
    }

    public static String validLogin() {

        return InstrumentationRegistry.getArguments().getString("testLogin", "login2");
    }

    public static String validPassword() {

        return InstrumentationRegistry.getArguments().getString("testPassword", "password2");
    }

    public static final String INVALID_LOGIN = "wrongLogin";
    public static final String INVALID_PASSWORD = "wrongPassword";
}
