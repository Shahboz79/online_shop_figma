package com.example.onlineshopfigma.security;

import javax.security.sasl.AuthenticationException;

public class UserNotActivateException extends AuthenticationException {

    public UserNotActivateException(String explanation) {
        super(explanation);
    }

    public UserNotActivateException(String message, Throwable throwable) {
        super(message,throwable);
    }
}
