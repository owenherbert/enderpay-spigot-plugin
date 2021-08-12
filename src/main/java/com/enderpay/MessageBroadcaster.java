package com.enderpay;

import org.bukkit.Bukkit;

public class MessageBroadcaster {

    public static String prefix = "[Enderpay] ";

    public static void toConsole(String message) {

        // send message to console
        Bukkit.getConsoleSender().sendMessage(prefix + message);
    }

}
