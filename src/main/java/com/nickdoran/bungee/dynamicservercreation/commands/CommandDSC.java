package com.nickdoran.bungee.dynamicservercreation.commands;

import com.google.common.net.InetAddresses;
import com.nickdoran.bungee.dynamicservercreation.DscAPI;
import com.nickdoran.bungee.dynamicservercreation.DynamicServer;
import com.nickdoran.bungee.dynamicservercreation.DynamicServerCreation;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Dr. Nick Doran on 9/17/2017.
 */
public class CommandDSC extends Command {

    public CommandDSC(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(new ComponentBuilder("Add Server: /dsc <serverName> <serverType> <ip> <port>").create());
            return;
        }

        if (args.length > 0) {
            if (args.length == 4) {
                String serverName = args[0];
                if (DynamicServerCreation.getInstance().doesDynamicServerExist(serverName)) {
                    sender.sendMessage(new ComponentBuilder("That server name is already in use!").create());
                    return;
                }
                String serverType = args[1];
                String ip = args[2];
                boolean isValidIP = InetAddresses.isInetAddress(ip);
                if (!isValidIP) {
                    sender.sendMessage(new ComponentBuilder("Please specify a valid IPv4 address!").create());
                    return;
                }
                int port;
                try {
                    port = Integer.valueOf(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(new ComponentBuilder("The port you entered is not numerical!").create());
                    return;
                }
                DscAPI.registerServer(serverName, serverType, ip, port);
            }
        }

    }
}
