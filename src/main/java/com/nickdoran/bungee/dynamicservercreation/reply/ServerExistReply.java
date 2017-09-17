package com.nickdoran.bungee.dynamicservercreation.reply;

/**
 * Created by Dr. Nick Doran on 9/17/2017.
 */
public class ServerExistReply extends AbstractDSCReply {

    private boolean serverExist;

    /**
     *
     * @param serverExist True if the server already exist within Bungee.
     */
    public ServerExistReply(boolean serverExist) {
        this.serverExist = serverExist;
    }

    public boolean doesServerExist() {
        return serverExist;
    }
}
