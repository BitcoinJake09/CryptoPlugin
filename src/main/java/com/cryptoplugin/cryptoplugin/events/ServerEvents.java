package com.cryptoplugin.cryptoplugin.events;

import com.cryptoplugin.cryptoplugin.CryptoPlugin;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerEvents implements Listener {
  CryptoPlugin cryptoPlugin;

  public ServerEvents(CryptoPlugin plugin) {
    cryptoPlugin = plugin;
  }

  @EventHandler
  public void onServerListPing(ServerListPingEvent event) {

    event.setMotd(ChatColor.GOLD + ChatColor.BOLD.toString() + " - This server runs CryptoPlugin");
  }
}
