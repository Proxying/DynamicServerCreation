package com.nickdoran.bungee.dynamicservercreation;

import com.google.common.net.InetAddresses;

import java.util.logging.Level;

/**
 * Created by Dr. Nick Doran on 10/12/2017.
 */
public class DscAPI {

    private static DynamicServerCreation plugin = DynamicServerCreation.getInstance();

    public static void registerServer(String serverName, String serverType, String ip, int port) {
        if (DynamicServerCreation.getInstance().doesDynamicServerExist(serverName)) {
            plugin.getLogger().log(Level.WARNING, "That server name is already in use!");
            return;
        }
        boolean isValidIP = InetAddresses.isInetAddress(ip);
        if (!isValidIP) {
            plugin.getLogger().log(Level.WARNING, "Please specify a valid IPv4 Address!");
            return;
        }
        DynamicServer dynamicServer = new DynamicServer(serverName, serverType, InetAddresses.forString(ip), port);
        dynamicServer.injectToProxy();
        DynamicServerCreation.getInstance().addDynamicServer(dynamicServer);
    }

}
