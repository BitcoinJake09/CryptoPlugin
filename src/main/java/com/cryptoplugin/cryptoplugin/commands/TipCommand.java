package com.cryptoplugin.cryptoplugin.commands;

import com.cryptoplugin.cryptoplugin.CryptoPlugin;
import com.cryptoplugin.cryptoplugin.NodeWallet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TipCommand extends CommandAction {
  private CryptoPlugin cryptoPlugin;
  private NodeWallet nodeWallet  = null;
  private NodeWallet nodeWallet2 = null; 

  public TipCommand(CryptoPlugin plugin) {
    cryptoPlugin = plugin;
  }

  public boolean run(
      CommandSender sender, Command cmd, String label, String[] args, final Player player) {
    try {
      nodeWallet = new NodeWallet(player.getUniqueId().toString(), 0);
      if (args[0].equalsIgnoreCase("help") || !(args.length >= 1)) {
        player.sendMessage(
            ChatColor.GREEN
                + "/tip <playername>  <amount> - Tip is used for player to player transactions.");
      }
    } catch (Exception e) {
      // e.printStackTrace();
      player.sendMessage(
          ChatColor.GREEN
              + "/tip <playername> <amount> - Tip is used for player to player transactions.");
    }

    // int MAX_SEND = 10000; // to be multiplied by DENOMINATION_FACTOR
    if (args.length > 1) {
      Long sat = cryptoPlugin.convertCoinToSats(Double.parseDouble(args[1]));
      Long totals = 0L;
      for (char c : sat.toString().toCharArray()) {
        if (!Character.isDigit(c)) return false;
      }


      if (sat != 0) {


              try {
                Long balance = nodeWallet.getBalance();

		String[] tempAddy = new String[(args.length/2)];
		String[] playerNames = new String[(args.length/2)];
            Long[] tempSats = new Long[(args.length/2)];
            int f = 0;
            for (String tempStr : args) {
            	System.out.println("args["+f+"]: {" + tempStr +"}");
            	f++;
            }
            f=0;
            for (int z = 0; z < args.length -1; z=z) {
            System.out.println("args["+z+"]: {" + args[z].toString() +"}");
                    for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
          if (onlinePlayer.getName().equalsIgnoreCase(args[z])) {
            if (!args[z].equalsIgnoreCase(player.getDisplayName())) {
            playerNames[z] = onlinePlayer.getName();
            NodeWallet tempWallet = new NodeWallet(onlinePlayer.getUniqueId().toString(), 0);
            tempAddy[f] = tempWallet.address;
            }
            }
            }
            System.out.println("tempAddy["+f+"]: " + tempAddy[f]);
            sat = cryptoPlugin.convertCoinToSats(Double.parseDouble(args[z+1]));
            System.out.println("args["+(z+1)+"]: " + args[z+1]);
            tempSats[f] = sat;
            totals = totals + sat;
            z = z+2;
            f++;
            }         

                if (balance >= totals) {
                  // TODO: Pay to user address
                  String didSend = nodeWallet.sendMany(tempAddy, tempSats);
                  //String didSend = "failed"; //test
                  if (didSend != "failed") {
                   for (int z = 0; z < args.length -1; z++) {
                    player.sendMessage(
                        ChatColor.GREEN
                            + "You sent "
                            + ChatColor.LIGHT_PURPLE
                            + cryptoPlugin.globalDecimalFormat.format(tempSats[z+1])
                            + " "
                            + CryptoPlugin.NODES.get(nodeWallet.walletArray).CRYPTO_TICKER
                            + ChatColor.GREEN
                            + " to player "
                            + ChatColor.BLUE
                            + playerNames[z]);
                   
              	for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
             	if (playerNames[z] == onlinePlayer.getName()) {
                    onlinePlayer.sendMessage(
                        ChatColor.GREEN
                            + "You got "
                            + ChatColor.LIGHT_PURPLE
                            + cryptoPlugin.globalDecimalFormat.format(tempSats[z+1])
                            + " "
                            + CryptoPlugin.NODES.get(nodeWallet.walletArray).CRYPTO_TICKER
                            + ChatColor.GREEN
                            + " from user "
                            + ChatColor.BLUE
                            + player.getName()
                            + ChatColor.BLUE
                            + " "
                            + cryptoPlugin.NODES.get(nodeWallet.walletArray).TX_URL
                            + didSend);
                    }
                    }
                    player.sendMessage(
                        ChatColor.GREEN
                            + "TXID: "
				+ ChatColor.BLUE
                            + cryptoPlugin.NODES.get(0).TX_URL
                            + didSend);
                    }
                  } else {
                    player.sendMessage(ChatColor.RED + "Tip failed.");
                  }
                } else {
                  player.sendMessage(ChatColor.DARK_RED + "Not enough balance");
                }
              } catch (Exception e) {
                player.sendMessage(ChatColor.DARK_RED + "Error. Please try again later.");
                System.out.println(e);
              }
      } else {
        player.sendMessage("error sending that amount.");
              return false;
      }
    } else {
      return false;
    }

    return true;
  }
}
