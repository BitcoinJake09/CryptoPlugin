package com.cryptoplugin.cryptoplugin.commands;

import com.cryptoplugin.cryptoplugin.CryptoPlugin;
import com.cryptoplugin.cryptoplugin.NodeWallet;
import java.util.*;
import org.bukkit.*;
import org.bukkit.ChatColor;
import org.bukkit.advancement.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetFeeCommand extends CommandAction {
  private CryptoPlugin cryptoPlugin;
  private NodeWallet nodeWallet = null;

  public SetFeeCommand(CryptoPlugin plugin) {
    this.cryptoPlugin = plugin;
  }

  public boolean run(
      CommandSender sender, Command cmd, String label, String[] args, Player player) {
    nodeWallet = new NodeWallet(player.getUniqueId().toString());
    if (args.length == 1) {
      if (args[0].equalsIgnoreCase("help")) {
        sender.sendMessage(
            ChatColor.GREEN
                + "please use a "
                + cryptoPlugin.DENOMINATION_NAME
                + " amount between "
                + cryptoPlugin.MIN_FEE
                + " - "
                + cryptoPlugin.MAX_FEE
                + " "
                + cryptoPlugin.DENOMINATION_NAME
                + "s/byte.");
        sender.sendMessage(ChatColor.GREEN + "/setfee <#>");
        return false;
      }
      try {
        if ((cryptoPlugin.isStringDouble(args[0])) || (cryptoPlugin.isStringInt(args[0]))) {
          if ((Double.parseDouble(args[0]) <= cryptoPlugin.MAX_FEE)
              && (Double.parseDouble(args[0]) >= 1.2)) {
            boolean setFee =
                nodeWallet.setSatByte(player.getUniqueId().toString(), Double.parseDouble(args[0]));
            System.out.println(
                "set to " + args[0] + "" + cryptoPlugin.DENOMINATION_NAME + "s/byte: " + setFee);
            CryptoPlugin.REDIS.set("txFee" + player.getUniqueId().toString(), args[0]);
            sender.sendMessage(
                ChatColor.GREEN
                    + "Your wallet fee has been set to "
                    + args[0]
                    + ""
                    + cryptoPlugin.DENOMINATION_NAME
                    + "s/b");
            return true;
          } else {
            sender.sendMessage(
                ChatColor.RED
                    + "failed, please use a "
                    + cryptoPlugin.DENOMINATION_NAME
                    + " amount between "
                    + cryptoPlugin.MIN_FEE
                    + " - "
                    + cryptoPlugin.MAX_FEE
                    + " "
                    + cryptoPlugin.DENOMINATION_NAME
                    + "s/byte.");
            sender.sendMessage(ChatColor.GREEN + "/setfee <#>");
            return false;
          }
        } else {
          sender.sendMessage(
              ChatColor.RED
                  + "failed, please use a "
                  + cryptoPlugin.DENOMINATION_NAME
                  + " amount between "
                  + cryptoPlugin.MIN_FEE
                  + " - "
                  + cryptoPlugin.MAX_FEE
                  + " "
                  + cryptoPlugin.DENOMINATION_NAME
                  + "s/byte.");
          sender.sendMessage(ChatColor.GREEN + "/setfee <#>");
          return false;
        }
      } catch (Exception e) {
        // e.printStackTrace();
        player.sendMessage(ChatColor.RED + "There was a problem updating your fee.");
      }
    } else if (args.length == 0) {
      sender.sendMessage(
          ChatColor.GREEN
              + "please use a "
              + cryptoPlugin.DENOMINATION_NAME
              + " amount between "
              + cryptoPlugin.MIN_FEE
              + " - "
              + cryptoPlugin.MAX_FEE
              + " "
              + cryptoPlugin.DENOMINATION_NAME
              + "s/byte.");
      sender.sendMessage(ChatColor.GREEN + "/setfee <#>");
      return false;
    }
    return true;
  }
}
