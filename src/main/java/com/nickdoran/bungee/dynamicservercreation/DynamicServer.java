package com.nickdoran.bungee.dynamicservercreation;

import com.google.common.net.InetAddresses;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.logging.Level;

/**
 * Created by Dr. Nick Doran on 9/17/2017.
 */
public class DynamicServer {

    private String serverName;
    private String serverType;
    private InetAddress inet4Address;
    private int port;

    private boolean injected = false;

    public DynamicServer(String serverName, String serverType, String inet4Address, int port) {
        this(serverName, serverType, InetAddresses.forString(inet4Address), port);
    }

    public DynamicServer(String serverName, String serverType, InetAddress inet4Address, int port) {
        this.serverName = serverName;
        this.serverType = serverType;
        this.inet4Address = inet4Address;
        this.port = port;
    }

    public String getServerName() {
        return serverName;
    }

    public String getServerType() {
        return serverType;
    }

    public InetAddress getInetAddress() {
        return inet4Address;
    }

    public int getPort() {
        return port;
    }

    public void injectToProxy() {
        if (!injected) {
            ServerInfo info = DynamicServerCreation.getInstance().getProxy().constructServerInfo(
                    getServerName(),
                    new InetSocketAddress(getInetAddress().getHostAddress(), getPort()),
                    getServerType(),
                    false);

            DynamicServerCreation.getInstance().getProxy().getServers().put(info.getName(), info);
            injected = true;
            DynamicServerCreation.getInstance().getLogger().log(Level.INFO, "Successfully injected: {0} into proxy!", getServerName());
        }
    }
}
