package com.cryptoplugin.cryptoplugin.commands;

import com.cryptoplugin.cryptoplugin.CryptoPlugin;
import com.cryptoplugin.cryptoplugin.NodeWallet;
import java.math.BigDecimal;
import java.text.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WalletCommand extends CommandAction {
  private CryptoPlugin cryptoPlugin;
  private NodeWallet nodeWallet = null;

  public WalletCommand(CryptoPlugin plugin) {
    cryptoPlugin = plugin;
  }

  public boolean run(
      CommandSender sender, Command cmd, String label, String[] args, Player player) {
    try {
      int tempWhichWallet = cryptoPlugin.whichWallet.get(player.getUniqueId());
      nodeWallet = new NodeWallet(player.getUniqueId().toString(), tempWhichWallet);
      String address = nodeWallet.address;
      System.out.print(player.getUniqueId().toString() + " [ADDRESS]: " + address);
      player.sendMessage(ChatColor.GREEN + "/wallet - Displays your wallet info.");
      player.sendMessage(
          ChatColor.GREEN
              + "/tip <playername> <amount> - Tip is used for player to player transactions.");
      player.sendMessage(
          ChatColor.GREEN
              + "/withdraw <address> <amount> - withdraw is used for External transactions to an address.");
	      player.sendMessage(ChatColor.GOLD + "/wallets - Displays ALL wallets infos.");
      player.sendMessage(
          ChatColor.GOLD
              + "/wallets change < # || TICKER > - used to change the players active wallet.");
      player.sendMessage(ChatColor.GREEN + "Your "+cryptoPlugin.NODES.get(nodeWallet.walletArray).COINGECKO_CRYPTO+" address on this server: ");
      player.sendMessage(ChatColor.GOLD + "" + address);
      String url = cryptoPlugin.NODES.get(cryptoPlugin.whichWallet.get(player.getUniqueId())).ADDRESS_URL + address;
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

    } catch (Exception e) {
      e.printStackTrace();
      player.sendMessage(ChatColor.RED + "There was a problem reading data. try again soon.");
    }

    return true;
  }
}
