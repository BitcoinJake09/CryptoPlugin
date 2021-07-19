package com.cryptoplugin.cryptoplugin.commands;

import com.cryptoplugin.cryptoplugin.CryptoPlugin;
import com.cryptoplugin.cryptoplugin.NodeWallet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.*;

public class TipCommand extends CommandAction {
  private CryptoPlugin cryptoPlugin;
  private NodeWallet nodeWallet  = null;
  List<Player> sentTo = new ArrayList<>(); 

  public TipCommand(CryptoPlugin plugin) {
    cryptoPlugin = plugin;
  }

  public boolean run(
      CommandSender sender, Command cmd, String label, String[] args, final Player player) {
    try {
          int tempWhichWallet = cryptoPlugin.whichWallet.get(player.getUniqueId());
      nodeWallet = new NodeWallet(player.getUniqueId().toString(), tempWhichWallet);
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
   if (args.length >= 2) {
        System.out.println("args.length: "+args.length);
        try {
          Long balance = Double.valueOf(nodeWallet.getGetSpendable()).longValue();
            Long sat = 0L;
            Long totals = 0L;

            // TODO: Pay to user address
            String[] tempAddy = new String[(args.length/2)];
            Long[] tempSats = new Long[(args.length/2)];
            String[] playerNames = new String[(args.length/2)];
            int f = 0;
            for (String tempStr : args) {
            	System.out.println("args["+f+"]: {" + tempStr +"}");
            	f++;
            }
            f=0;
            for (int z = 0; z < args.length -1; z=z) {
            for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getName().equalsIgnoreCase(args[z])) {
              playerNames[z] = onlinePlayer.getName();
              sentTo.add(onlinePlayer);
              NodeWallet tempWallet = new NodeWallet(onlinePlayer.getUniqueId().toString(), nodeWallet.walletArray);
              tempAddy[f] = tempWallet.address;
              System.out.println("tempAddy["+f+"]: " + tempAddy[f]);
            }
            }
	     sat = CryptoPlugin.NODES.get(nodeWallet.walletArray).convertCoinToSats(Double.parseDouble(args[z+1]));
            System.out.println("args["+(z+1)+"]: " + args[z+1]);
            tempSats[f] = sat;
            totals = totals + sat;
            z = z+2;
            f++;
            }           
            
            

            double tempfee = 0.00;
	    tempfee = cryptoPlugin.NODES.get(nodeWallet.walletArray).txFee;
	    double tempTotal = totals * CryptoPlugin.NODES.get(nodeWallet.walletArray).BaseSat;
	    if ((nodeWallet.getGetSpendable() - tempfee) >= tempTotal) {

            String didSend = nodeWallet.sendMany(tempAddy, tempSats);
            //String didSend = "TEST";//test
            if (didSend != "failed") {
            int namecount = 0;
             for (int z = 0; z < args.length - 1; z=z) {
              player.sendMessage(
                  ChatColor.GREEN
                      + "Your tipped "
                      + ChatColor.LIGHT_PURPLE
                      + CryptoPlugin.NODES.get(nodeWallet.walletArray).GlobalDecimalFormat.format((Double.parseDouble(args[z+1])))
                      + " "
                      + CryptoPlugin.NODES.get(nodeWallet.walletArray).CRYPTO_TICKER
                      + ChatColor.GREEN
                      + " to player "
                      + ChatColor.YELLOW
                      + playerNames[namecount].toString());
                      z = z + 2;
                      namecount++;
               }
               player.sendMessage(
                  ChatColor.GREEN
                      + "Your tip tx: "
                      + ChatColor.BLUE
                      + cryptoPlugin.NODES.get(nodeWallet.walletArray).TX_URL
                      + didSend);
                      
                      
                                  f=0;
            for (int z = 0; z < args.length -1; z=z) {
            for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getName().equalsIgnoreCase(args[z])) {
              double tmpSat = CryptoPlugin.NODES.get(nodeWallet.walletArray).convertSatsToCoin(tempSats[z]);
              onlinePlayer.sendMessage(
                  ChatColor.GREEN
                      + player.getName()
                      + " tipped you "
                      + ChatColor.LIGHT_PURPLE
                      + tmpSat
                      + " "
                      + CryptoPlugin.NODES.get(nodeWallet.walletArray).CRYPTO_TICKER
                      + ChatColor.GREEN
                      + "Your tip tx: "
                      + ChatColor.BLUE
                      + cryptoPlugin.NODES.get(nodeWallet.walletArray).TX_URL
                      + didSend);
                      
                      System.out.println(player.getName()
                      + " tipped you "
                      + CryptoPlugin.NODES.get(nodeWallet.walletArray).GlobalDecimalFormat.format(tmpSat)
                      + " "
                      + CryptoPlugin.NODES.get(nodeWallet.walletArray).CRYPTO_TICKER
                      + "Your tip tx: "
                      + cryptoPlugin.NODES.get(nodeWallet.walletArray).TX_URL
                      + didSend);
              
            }
            }
            z = z+2;
            f++;
            }  
            } else {
              player.sendMessage(ChatColor.RED + "tip failed.");
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









