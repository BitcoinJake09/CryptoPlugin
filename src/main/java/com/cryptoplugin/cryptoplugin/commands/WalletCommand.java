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
      nodeWallet = new NodeWallet(player.getUniqueId().toString());
      String address = nodeWallet.address;
      System.out.print(player.getUniqueId().toString() + " [ADDRESS]: " + address);
      player.sendMessage(ChatColor.GREEN + "/wallet - Displays your wallet info.");
      player.sendMessage(
          ChatColor.GREEN
              + "/tip <amount> <playername> - Tip is used for player to player transactions.");
      player.sendMessage(
          ChatColor.GREEN
              + "/withdraw <amount> <address> - withdraw is used for External transactions to an address.");

      player.sendMessage(ChatColor.GREEN + "Your Deposit address on this server: " + address);
      String url = cryptoPlugin.ADDRESS_URL + address;
      player.sendMessage(ChatColor.WHITE + "" + ChatColor.UNDERLINE + url);

      Double playerCoinBalance6 =
          (Double)
              (BigDecimal.valueOf(nodeWallet.getBalance(cryptoPlugin.CONFS_TARGET)).doubleValue()
                  * cryptoPlugin.baseSat);

      player.sendMessage(
          ChatColor.GREEN
              + "wallet balance with "
              + cryptoPlugin.CONFS_TARGET
              + "-conf+: "
              + cryptoPlugin.globalDecimalFormat.format(playerCoinBalance6));

      DecimalFormat df = new DecimalFormat(CryptoPlugin.USD_DECIMALS);

      player.sendMessage(
          ChatColor.GREEN
              + "1 "
              + cryptoPlugin.COINGECKO_CRYPTO
              + " = $"
              + df.format(
                  Double.parseDouble(cryptoPlugin.getExchangeRate(cryptoPlugin.COINGECKO_CRYPTO))));

    } catch (Exception e) {
      e.printStackTrace();
      player.sendMessage(ChatColor.RED + "There was a problem reading data. try again soon.");
    }

    return true;
  }
}
