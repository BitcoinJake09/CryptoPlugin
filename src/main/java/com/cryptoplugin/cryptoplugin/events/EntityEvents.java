package com.cryptoplugin.cryptoplugin.events;

import com.cryptoplugin.cryptoplugin.CryptoPlugin;
import com.cryptoplugin.cryptoplugin.NodeWallet;
//import com.cryptoplugin.cryptoplugin.User;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;
import java.util.*;

public class EntityEvents implements Listener {
  CryptoPlugin cryptoPlugin;
  private NodeWallet nodeWallet = null;
  StringBuilder rawwelcome = new StringBuilder();

  public EntityEvents(CryptoPlugin plugin) {
    cryptoPlugin = plugin;

    for (String line : cryptoPlugin.getConfig().getStringList("welcomeMessage")) {
      for (ChatColor color : ChatColor.values()) {
        line = line.replaceAll("<" + color.name() + ">", color.toString());
      }
      // add links
      final Pattern pattern = Pattern.compile("<link>(.+?)</link>");
      final Matcher matcher = pattern.matcher(line);
      matcher.find();
      String link = matcher.group(1);
      // Right here we need to replace the link variable with a minecraft-compatible link
      line = line.replaceAll("<link>" + link + "<link>", link);

      rawwelcome.append(line);
    }
  }

  
  @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Called when a player leaves a server
        Player player = event.getPlayer();
	}
    

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) throws ParseException{
    final Player player = event.getPlayer();
    try {
     	for (int x = 0; x < cryptoPlugin.NODES.size(); x++) {
      nodeWallet = new NodeWallet(player.getUniqueId().toString(), x);
      player.sendMessage(ChatColor.YELLOW + "This server runs CryptoPlugin! ");
      player.sendMessage(ChatColor.RED + "USE AT YOUR OWN RISK!");
      player.sendMessage(ChatColor.GREEN + "Your " + cryptoPlugin.NODES.get(nodeWallet.walletArray).CRYPTO_TICKER + " address is: " + cryptoPlugin.NODES.get(nodeWallet.walletArray).ADDRESS_URL + nodeWallet.address);
      System.out.println("Address for: " + player.getName() + " : " + nodeWallet.address);
      }
    } catch (Exception e) {
      e.printStackTrace();
      player.sendMessage(ChatColor.RED + "couldnt load wallet.");
    }
  

    // On dev environment, admin gets op. In production, nobody gets op.

    final String ip = player.getAddress().toString().split("/")[1].split(":")[0];
    System.out.println("User " + player.getName() + "logged in with IP " + ip);
    System.out.println("displayname:" + player.getDisplayName());
    System.out.println("uuid:" + player.getUniqueId().toString());
    cryptoPlugin.whichWallet.put(player.getUniqueId(), 0);
    int tempWhichWallet = (Integer) cryptoPlugin.whichWallet.get(player.getUniqueId());
    System.out.println("wallet #" + tempWhichWallet +" enabled");

/*
	try {
    if (cryptoPlugin.REDIS.exists("nodeAddress"+ player.getUniqueId().toString())) {
	player.sendMessage(ChatColor.GREEN + "Your Deposit address on this server: " + cryptoPlugin.REDIS.get("nodeAddress"+ player.getUniqueId().toString()));

      String url = cryptoPlugin.ADDRESS_URL + cryptoPlugin.REDIS.get("nodeAddress"+ player.getUniqueId().toString());
      player.sendMessage(ChatColor.WHITE + "" + ChatColor.UNDERLINE + url);

    } else {
	cryptoPlugin.REDIS.set("nodeAddress"+ player.getUniqueId().toString(),cryptoPlugin.getAccountAddress(player.getUniqueId().toString()));
	player.sendMessage(ChatColor.GREEN + "Your Deposit address on this server: " + cryptoPlugin.REDIS.get("nodeAddress"+ player.getUniqueId().toString()));

      String url2 = cryptoPlugin.ADDRESS_URL + cryptoPlugin.REDIS.get("nodeAddress"+ player.getUniqueId().toString());
      player.sendMessage(ChatColor.WHITE + "" + ChatColor.UNDERLINE + url2);

	}
	} catch(Exception E) {
		    System.out.println(E);
	}


		try {
		cryptoPlugin.updateScoreboard(player);
		} catch (Exception excep) {
			System.out.println(excep);
		}

*/
    String welcome = rawwelcome.toString();
    welcome = welcome.replace("<name>", player.getName());
    player.sendMessage(welcome);
     

    // Prints the user balance


    
    //CryptoPlugin.REDIS.zincrby("player:login", 1, player.getUniqueId().toString());


  }
}
