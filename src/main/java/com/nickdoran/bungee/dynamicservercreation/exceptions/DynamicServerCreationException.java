package com.nickdoran.bungee.dynamicservercreation.exceptions;

/**
 * Created by Dr. Nick Doran on 10/12/2017.
 */
public class DynamicServerCreationException extends RuntimeException {

    public DynamicServerCreationException(String message) {
        super(message);
    }

    public DynamicServerCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DynamicServerCreationException(Throwable cause) {
        super(cause);
    }

    public DynamicServerCreationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
