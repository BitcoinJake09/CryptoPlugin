package com.cryptoplugin.cryptoplugin.commands;

import com.cryptoplugin.cryptoplugin.CryptoPlugin;
import com.cryptoplugin.cryptoplugin.NodeWallet;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import java.util.*;

public class WithdrawCommand extends CommandAction {
  private CryptoPlugin cryptoPlugin;
  private NodeWallet nodeWallet = null;

  public WithdrawCommand(CryptoPlugin plugin) {
    cryptoPlugin = plugin;
  }

  public boolean run(
      CommandSender sender, Command cmd, String label, String[] args, final Player player) {
    try {
      nodeWallet = new NodeWallet(player.getUniqueId().toString(), 0);
      if (args[1].equalsIgnoreCase("help") || !(args.length >= 1)) {
        player.sendMessage(
            ChatColor.GREEN
                + "/withdraw <address> <amount> - withdraw is used for External transactions to an address.");
      }
    } catch (Exception e) {
      // e.printStackTrace();
      player.sendMessage(
          ChatColor.GREEN
              + "/withdraw <address> <amount> - withdraw is used for External transactions to an address.");
    }
    if (args.length >= 2) {
        System.out.println("args.length: "+args.length);
        try {
          Long balance = nodeWallet.getBalance();
            Long sat = 0L;
            Long totals = 0L;

            // TODO: Pay to user address
            String[] tempAddy = new String[(args.length/2)];
            Long[] tempSats = new Long[(args.length/2)];
            int f = 0;
            for (String tempStr : args) {
            	System.out.println("args["+f+"]: {" + tempStr +"}");
            	f++;
            }
            f=0;
            for (int z = 0; z < args.length -1; z=z) {
            System.out.println("args["+z+"]: {" + args[z].toString() +"}");
            tempAddy[f] = args[z].toString();
            System.out.println("tempAddy["+f+"]: " + tempAddy[f]);
            sat = cryptoPlugin.convertCoinToSats(Double.parseDouble(args[z+1]));
            System.out.println("args["+(z+1)+"]: " + args[z+1]);
            tempSats[f] = sat;
            totals = totals + sat;
            z = z+2;
            f++;
            }         
            
            


	    if (balance >= totals) {

            String didSend = nodeWallet.sendMany(tempAddy, tempSats);
            //String didSend = "TEST";//test
            if (didSend != "failed") {
             for (int z = 0; z < args.length - 1; z++) {
              player.sendMessage(
                  ChatColor.GREEN
                      + "Your withdraw "
                      + ChatColor.LIGHT_PURPLE
                      + cryptoPlugin.globalDecimalFormat.format((Double.parseDouble(args[z+1])))
                      + " "
                      + CryptoPlugin.NODES.get(nodeWallet.walletArray).CRYPTO_TICKER
                      + ChatColor.GREEN
                      + " to address "
                      + ChatColor.YELLOW
                      + args[z].toString());
               }
               player.sendMessage(
                  ChatColor.GREEN
                      + "Your withdraw tx: "
                      + ChatColor.BLUE
                      + cryptoPlugin.NODES.get(nodeWallet.walletArray).TX_URL
                      + didSend);
            } else {
              player.sendMessage(ChatColor.RED + "withdraw failed.");
              return false;
            }
          } else {
            player.sendMessage(ChatColor.DARK_RED + "Not enough balance");
            return false;
          }
        } catch (Exception e) {
          player.sendMessage(ChatColor.DARK_RED + "Error. Please try again later.");
          System.out.println(e);
          return false;
        }
      } else {
        player.sendMessage("error, are you sure you have the right arguments?");
        return false;
      }
          return true;
    }
  }
