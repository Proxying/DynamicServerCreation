package com.nickdoran.bungee.dynamicservercreation.exceptions;

/**
 * Created by Dr. Nick Doran on 10/12/2017.
 */
public class ServerNameExistException extends DynamicServerCreationException {

    public ServerNameExistException() {
        super("Server name is already in use!");
    }
}
