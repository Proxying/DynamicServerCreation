package com.nickdoran.bungee.dynamicservercreation.exceptions;

/**
 * Created by Dr. Nick Doran on 10/12/2017.
 */
public class InvalidIPv4AddressException extends DynamicServerCreationException {
    public InvalidIPv4AddressException() {
        super("An invalid IPv4 address was specified.");
    }
}
