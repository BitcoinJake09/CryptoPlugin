package com.cryptoplugin.cryptoplugin.commands;

import com.cryptoplugin.cryptoplugin.CryptoPlugin;
import com.cryptoplugin.cryptoplugin.NodeWallet;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WithdrawCommand extends CommandAction {
  private CryptoPlugin cryptoPlugin;
  private NodeWallet nodeWallet = null;

  public WithdrawCommand(CryptoPlugin plugin) {
    cryptoPlugin = plugin;
  }

  public boolean run(
      CommandSender sender, Command cmd, String label, String[] args, final Player player) {
    try {
      nodeWallet = new NodeWallet(player.getUniqueId().toString());
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
    if (args.length == 2) {
      final Long sat = cryptoPlugin.convertCoinToSats(Double.parseDouble(args[1]));
      for (char c : sat.toString().toCharArray()) {
        if (!Character.isDigit(c)) return false;
      }
      if (args[1].length() > 10) {
        // maximum send is 10 digits
        return false;
      }

      if (sat != 0) {

        if (!args[0].equalsIgnoreCase(player.getDisplayName())) {
          try {

            Long balance =
                nodeWallet.getBalance(player.getUniqueId().toString(), cryptoPlugin.CONFS_TARGET);

            if (balance >= sat) {
              // TODO: Pay to user address
              boolean setFee =
                  nodeWallet.setSatByte(
                      player.getUniqueId().toString(),
                      Double.parseDouble(
                          CryptoPlugin.REDIS.get("txFee" + player.getUniqueId().toString())));
              String didSend =
                  nodeWallet.sendToAddress(
                      player.getUniqueId().toString(), args[0].toString(), sat);
              if (didSend != "failed") {
                player.sendMessage(
                    ChatColor.GREEN
                        + "Your withdraw "
                        + ChatColor.LIGHT_PURPLE
                        + cryptoPlugin.globalDecimalFormat.format(
                            cryptoPlugin.convertSatsToCoin(sat))
                        + " "
                        + CryptoPlugin.CRYPTO_TICKER
                        + ChatColor.GREEN
                        + " to address "
                        + ChatColor.YELLOW
                        + args[0].toString()
                        + ChatColor.BLUE
                        + " "
                        + cryptoPlugin.TX_URL
                        + didSend);
              } else {
                player.sendMessage(ChatColor.RED + "withdraw failed.");
              }
            } else {
              player.sendMessage(ChatColor.DARK_RED + "Not enough balance");
            }
          } catch (Exception e) {
            player.sendMessage(ChatColor.DARK_RED + "Error. Please try again later.");
            System.out.println(e);
          }
        }
      } else {
        player.sendMessage("error with that amount.");
      }
    } else {
      return false;
    }

    return true;
  }
}
