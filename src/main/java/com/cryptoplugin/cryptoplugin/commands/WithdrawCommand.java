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
              int tempWhichWallet = cryptoPlugin.whichWallet.get(player.getUniqueId());
      nodeWallet = new NodeWallet(player.getUniqueId().toString(), tempWhichWallet);
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
          Long balance = Double.valueOf(nodeWallet.getGetSpendable()).longValue();
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
            sat = CryptoPlugin.NODES.get(nodeWallet.walletArray).convertCoinToSats(Double.parseDouble(args[z+1]));
            System.out.println("args["+(z+1)+"]: " + args[z+1]);
            tempSats[f] = sat;
            totals = totals + sat;
            z = z+2;
            f++;
            }         
            
            

            double tempfee = 0.00;
            if (CryptoPlugin.NODES.get(nodeWallet.walletArray).COINGECKO_CRYPTO.equalsIgnoreCase("DeVault")) {
    tempfee = nodeWallet.getFee();
      } else {
    tempfee = nodeWallet.getFee() * (0.226);
      }
      	    System.out.println("balance" + balance);
	    System.out.println("nodeWallet.getFee(): " + nodeWallet.getFee());
            System.out.println("balance - nodeWallet.getFee(): " + (balance - nodeWallet.getFee()));
	    System.out.println("tempfee " + tempfee);
	    System.out.println("nodeWallet.getGetSpendable() - tempfee " + (nodeWallet.getGetSpendable() - tempfee));
	    System.out.println("totals.doubleValue() " + totals.doubleValue());
	    System.out.println("totals " + (totals * CryptoPlugin.NODES.get(nodeWallet.walletArray).BaseSat));
	    double tempTotal = totals * CryptoPlugin.NODES.get(nodeWallet.walletArray).BaseSat;
	    if ((nodeWallet.getGetSpendable() - tempfee) >= tempTotal) {

            String didSend = nodeWallet.sendMany(tempAddy, tempSats);
            //String didSend = "TEST";//test
            if (didSend != "failed") {
             for (int z = 0; z < args.length - 1; z=z) {
              player.sendMessage(
                  ChatColor.GREEN
                      + "Your withdraw "
                      + ChatColor.LIGHT_PURPLE
                      + CryptoPlugin.NODES.get(nodeWallet.walletArray).GlobalDecimalFormat.format((Double.parseDouble(args[z+1])))
                      + " "
                      + CryptoPlugin.NODES.get(nodeWallet.walletArray).CRYPTO_TICKER
                      + ChatColor.GREEN
                      + " to address "
                      + ChatColor.YELLOW
                      + args[z].toString());
                      z = z + 2;
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
