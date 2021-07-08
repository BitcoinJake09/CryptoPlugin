package com.cryptoplugin.cryptoplugin.commands;

import com.cryptoplugin.cryptoplugin.CryptoPlugin;
import com.cryptoplugin.cryptoplugin.NodeWallet;
import java.math.BigDecimal;
import java.text.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WalletsCommand extends CommandAction {
  private CryptoPlugin cryptoPlugin;
  private NodeWallet nodeWallet = null;

  public WalletsCommand(CryptoPlugin plugin) {
    cryptoPlugin = plugin;
  }

  public boolean run(
      CommandSender sender, Command cmd, String label, String[] args, Player player) {
    try {
    if (args.length < 1) {
      for (int x = 0; x < cryptoPlugin.NODES.size(); x++) {
      //int tempWhichWallet = cryptoPlugin.whichWallet.get(x);
      nodeWallet = new NodeWallet(player.getUniqueId().toString(), x);
      String address = nodeWallet.address;
      player.sendMessage(ChatColor.WHITE + "Wallet: #" + ChatColor.BLUE + (x+1) + ChatColor.WHITE + " Ticker: " + ChatColor.BLUE + cryptoPlugin.NODES.get(nodeWallet.walletArray).CRYPTO_TICKER);
      player.sendMessage(ChatColor.GREEN + "Your "+cryptoPlugin.NODES.get(nodeWallet.walletArray).COINGECKO_CRYPTO+" address on this server: ");
      player.sendMessage(ChatColor.GOLD + "" + address);
      String url = cryptoPlugin.NODES.get(x).ADDRESS_URL + address;
      player.sendMessage(ChatColor.WHITE + "" + ChatColor.UNDERLINE + url);

      Double playerCoinBalance = nodeWallet.getGetSpendable();

      player.sendMessage(
          ChatColor.GREEN
              + "balance: " + ChatColor.WHITE + "" + CryptoPlugin.NODES.get(nodeWallet.walletArray).GlobalDecimalFormat.format(playerCoinBalance));

      DecimalFormat df = new DecimalFormat(CryptoPlugin.NODES.get(nodeWallet.walletArray).USD_DECIMALS);

      player.sendMessage(
          ChatColor.GREEN
              + "1 "
              + cryptoPlugin.NODES.get(nodeWallet.walletArray).COINGECKO_CRYPTO
              + " = $" + ChatColor.WHITE + "" + cryptoPlugin.NODES.get(nodeWallet.walletArray).exRate);
            
                  double USDbalance = (cryptoPlugin.NODES.get(nodeWallet.walletArray).exRate * playerCoinBalance);
     player.sendMessage(
          ChatColor.GREEN
              + CryptoPlugin.NODES.get(nodeWallet.walletArray).COINGECKO_CRYPTO
              + " USD Value: $" + ChatColor.WHITE + ""  + df.format(USDbalance));
              
      double tempfee = cryptoPlugin.NODES.get(nodeWallet.walletArray).txFee;
      player.sendMessage(
          ChatColor.GREEN
              + CryptoPlugin.NODES.get(nodeWallet.walletArray).COINGECKO_CRYPTO
              + " fee Estimates: " + ChatColor.WHITE + ""  + CryptoPlugin.NODES.get(nodeWallet.walletArray).txFee);

      player.sendMessage(
          ChatColor.AQUA
              + "~~~~~~~~~~~~~~~~~~~~~~~~");
	}
      player.sendMessage(
          ChatColor.GOLD
              + "for more info /wallets help");
	} // end (args.length < 1)
	else if (args.length == 1) {
		if (args[0].equalsIgnoreCase("list")) {
		          for (int z = 0; z < cryptoPlugin.NODES.size(); z++) {
		          	player.sendMessage(ChatColor.WHITE + "Wallet: #" + ChatColor.BLUE + (z+1) + ChatColor.WHITE + " Ticker: " + ChatColor.BLUE + cryptoPlugin.NODES.get(z).CRYPTO_TICKER);
		          }
		}
		if (args[0].equalsIgnoreCase("help")) {
	      player.sendMessage(ChatColor.GOLD + "/wallets - Displays ALL wallets infos.");
	      player.sendMessage(ChatColor.GOLD + "/wallets list - Displays each wallets # and Ticker.");
      player.sendMessage(
          ChatColor.GOLD
              + "/wallets change < # or TICKER > - used to change the players active wallet.");
      player.sendMessage(
          ChatColor.GREEN
              + "Example 1: " + ChatColor.WHITE + "/wallets change " + 1);
      player.sendMessage(
          ChatColor.GREEN
              + "Example 2: " + ChatColor.WHITE + "/wallets change " + cryptoPlugin.NODES.get(0).CRYPTO_TICKER);
		}
	}
	else if (args.length >= 2) {
	    if (args[0].equalsIgnoreCase("change")) {
	    if (isStringInt(args[1])){
	    	int walletNum = Integer.parseInt(args[1]) - 1;
	    	if (walletNum < cryptoPlugin.NODES.size()) {
	        cryptoPlugin.whichWallet.put(player.getUniqueId(), walletNum);
	        } else {
	         player.sendMessage(
          ChatColor.GREEN + "wallet not found, please select a # = to or less than: " + (cryptoPlugin.NODES.size() - 1) + " or select the cryptos ticker");
	        }
                return true;
	    } else {
      for (int y = 0; y < cryptoPlugin.NODES.size(); y++) {
      	    if (args[1].equalsIgnoreCase(cryptoPlugin.NODES.get(y).CRYPTO_TICKER)) {
      	            NodeWallet tempWallet = new NodeWallet("CryptoPlugin", y);
		cryptoPlugin.whichWallet.put(player.getUniqueId(), tempWallet.walletArray);
        return true;
        }
	    }
	    	         player.sendMessage(
          ChatColor.GREEN + "wallet not found, please select a # = to or less than: " + (cryptoPlugin.NODES.size() - 1) + " or select the cryptos ticker");
	    }
	    } //end change
	} // end (args.length >= 2)
    } catch (Exception e) {
      e.printStackTrace();
      player.sendMessage(ChatColor.RED + "There was a problem reading data. try again soon.");
    }

    return true;
  }
  
  public boolean isStringInt(String s)
{
    try
    {
        Integer.parseInt(s);
        return true;
    } catch (NumberFormatException ex)
    {
        return false;
    }
}
}
