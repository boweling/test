package com.markerhub.common.exception;

import org.springframework.security.core.AuthenticationException;


/**
 * 自定义了一个验证码错误的异常
 */
public class CaptchaException  extends AuthenticationException {
    public CaptchaException(String msg) {
        super(msg);
    }
}
