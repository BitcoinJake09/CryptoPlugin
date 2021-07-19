package com.cryptoplugin.cryptoplugin.commands;

import com.cryptoplugin.cryptoplugin.CryptoPlugin;
import com.cryptoplugin.cryptoplugin.NodeWallet;
import java.math.BigDecimal;
import java.text.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackupkeyCommand extends CommandAction {
  private CryptoPlugin cryptoPlugin;
  private NodeWallet nodeWallet = null;

  public BackupkeyCommand(CryptoPlugin plugin) {
    cryptoPlugin = plugin;
  }

  public boolean run(
      CommandSender sender, Command cmd, String label, String[] args, Player player) {
    try {
      int tempWhichWallet = cryptoPlugin.whichWallet.get(player.getUniqueId());
      nodeWallet = new NodeWallet(player.getUniqueId().toString(), tempWhichWallet);
      String address = nodeWallet.address;
      System.out.print(player.getUniqueId().toString() + " [ADDRESS]: " + address);
      if (args.length == 0) {
      player.sendMessage(ChatColor.AQUA + "To backup your key to "+ cryptoPlugin.NODES.get(nodeWallet.walletArray).CRYPTO_TICKER + " address: " + ChatColor.GOLD + "" + address);
      player.sendMessage(ChatColor.GREEN + "Use command: " + ChatColor.WHITE +"/Backupkey <true> " + ChatColor.GREEN + " - will display your private key to backup yourself.");
      player.sendMessage(ChatColor.RED + "DO NOT USE THIS COMMAND WHILE STREAMING OR ANYTIME YOU WOULDNT WANT SOMEONE TO SEE SOMETHING PRIVATE!");
	} else if ((args.length >= 1) && (args[0].equalsIgnoreCase("TRUE"))) {
		String keyURL = "https://private.key="+nodeWallet.dumpprivkey();
	      player.sendMessage(ChatColor.AQUA + "Your "+ cryptoPlugin.NODES.get(nodeWallet.walletArray).CRYPTO_TICKER +" privatekey is: " + ChatColor.WHITE + "" + ChatColor.UNDERLINE + keyURL);
              player.sendMessage(ChatColor.RED + "please copy and DO NOT OPEN IN BROWSER!!!");
	} else {
	player.sendMessage(ChatColor.AQUA + "To backup your key to "+ cryptoPlugin.NODES.get(nodeWallet.walletArray).CRYPTO_TICKER + " address: " + ChatColor.GOLD + "" + address);
      player.sendMessage(ChatColor.GREEN + "Use command: " + ChatColor.WHITE +"/Backupkey <true> " + ChatColor.GREEN + " - will display your private key to backup yourself.");
      player.sendMessage(ChatColor.RED + "DO NOT USE THIS COMMAND WHILE STREAMING OR ANYTIME YOU WOULDNT WANT SOMEONE TO SEE SOMETHING PRIVATE!");
	}
    } catch (Exception e) {
      e.printStackTrace();
      player.sendMessage(ChatColor.RED + "There was a problem reading data. try again soon.");
    }

    return true;
  }
}
