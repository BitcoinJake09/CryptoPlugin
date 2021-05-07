// bitcoinjake09 11/9/2019 - a bitcoin tressure hunt in minecraft - cryptoplugin
package com.cryptoplugin.cryptoplugin;

import com.cryptoplugin.cryptoplugin.commands.*;
import com.cryptoplugin.cryptoplugin.events.*;
import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.Date;
import javax.net.ssl.HttpsURLConnection;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import redis.clients.jedis.Jedis;

// Color Table :
// GREEN : Worked, YELLOW : Processing, LIGHT_PURPLE : Any money balance, BLUE : Player name,
// DARK_BLUE UNDERLINE : Link, RED : Server error, DARK_RED : User error, GRAY : Info, DARK_GRAY :
// Clan, DARK_GREEN : Landname

public class CryptoPlugin extends JavaPlugin {
  private NodeWallet nodeWallet = null;
  // TODO: remove env variables not being used anymore
  // Connecting to REDIS
  // Links to the administration account via Environment Variables
  public static final String CRYPTOPLUGIN_ENV =
      System.getenv("CRYPTOPLUGIN_ENV") != null ? System.getenv("CRYPTOPLUGIN_ENV") : "development";
  public static final UUID ADMIN_UUID =
      System.getenv("ADMIN_UUID") != null ? UUID.fromString(System.getenv("ADMIN_UUID")) : null;
  public static final String NODE_HOST =
      System.getenv("NODE_HOST") != null ? System.getenv("NODE_HOST") : null;
  public static final int NODE_PORT =
      System.getenv("NODE_PORT") != null ? Integer.parseInt(System.getenv("NODE_PORT")) : 8332;
  // https://www.cryptonator.com/api/currencies
  public static final String COINGECKO_CRYPTO =
      System.getenv("COINGECKO_CRYPTO") != null ? System.getenv("COINGECKO_CRYPTO") : "bitcoin";
  public static final String CRYPTO_TICKER =
      System.getenv("CRYPTO_TICKER") != null ? System.getenv("CRYPTO_TICKER") : "BTC";
  public static final Long DENOMINATION_FACTOR =
      System.getenv("DENOMINATION_FACTOR") != null
          ? Long.parseLong(System.getenv("DENOMINATION_FACTOR"))
          : 1L;
  public static final Integer CRYPTO_DECIMALS =
      System.getenv("CRYPTO_DECIMALS") != null
          ? Integer.parseInt(System.getenv("CRYPTO_DECIMALS"))
          : 8;
  public static final Integer DISPLAY_DECIMALS =
      System.getenv("DISPLAY_DECIMALS") != null
          ? Integer.parseInt(System.getenv("DISPLAY_DECIMALS"))
          : 8;
  public static final String USD_DECIMALS =
      System.getenv("USD_DECIMALS") != null ? System.getenv("USD_DECIMALS") : "0.00";
  public static final Integer CONFS_TARGET =
      System.getenv("CONFS_TARGET") != null ? Integer.parseInt(System.getenv("CONFS_TARGET")) : 6;
  public static final String DENOMINATION_NAME =
      System.getenv("DENOMINATION_NAME") != null ? System.getenv("DENOMINATION_NAME") : "Sats";
  public static final String NODE_USERNAME = System.getenv("NODE_USERNAME");
  public static final String NODE_PASSWORD = System.getenv("NODE_PASSWORD");
  public static final Double MIN_FEE =
      System.getenv("MIN_FEE") != null ? Double.parseDouble(System.getenv("MIN_FEE")) : 1.2;
  public static final Double MAX_FEE =
      System.getenv("MAX_FEE") != null ? Double.parseDouble(System.getenv("MAX_FEE")) : 100.0;

  public static final String ADDRESS_URL =
      System.getenv("ADDRESS_URL") != null
          ? System.getenv("ADDRESS_URL")
          : "https://www.blockchain.com/btc/address/";

  public static final String TX_URL =
      System.getenv("TX_URL") != null
          ? System.getenv("TX_URL")
          : "https://www.blockchain.com/btc/tx/";

  public static final String PLUGIN_WEBSITE =
      System.getenv("SERVER_WEBSITE") != null
          ? System.getenv("SERVER_WEBSITE")
          : "http://AllAboutBTC.com/CryptoPlugin.html";

  // REDIS: Look for Environment variables on hostname and port, otherwise defaults to
  // localhost:6379
  public static final String REDIS_HOST =
      System.getenv("REDIS_PORT_6379_TCP_ADDR") != null
          ? System.getenv("REDIS_PORT_6379_TCP_ADDR")
          : "localhost";
  public static final Integer REDIS_PORT =
      System.getenv("REDIS_PORT_6379_TCP_PORT") != null
          ? Integer.parseInt(System.getenv("REDIS_PORT_6379_TCP_PORT"))
          : 6379;
  public static final Jedis REDIS = new Jedis(REDIS_HOST, REDIS_PORT);

  public static int rand(int min, int max) {
    return min + (int) (Math.random() * ((max - min) + 1));
  }

  public Long wallet_balance_cache = 0L;
  public Double exRate = 99999999.99;
  public boolean eventsLoaded = false;
  public DecimalFormat globalDecimalFormat = new DecimalFormat("0.00000000");
  public DecimalFormat displayDecimalFormat = new DecimalFormat("0.00000000");
  public Double baseSat = oneSat();
  public Double displaySats = howmanyDisplayDecimals();
  public Long oneCoinSats = wholeCoin();
  // public Long tests = convertCoinToSats(0.00125555); //test   F tempAmount : 125554.99999999997
  // when true, server is closed for maintenance and not allowing players to join in.
  public boolean maintenance_mode = false;
  private Map<String, CommandAction> commands;
  private Map<String, CommandAction> modCommands;
  private Player[] moderators;

  @Override
  public void onEnable() {
    log("[startup] CryptoPlugin starting");
    try {
      if (ADMIN_UUID == null) {
        log("[warning] ADMIN_UUID env variable to is not set.");
      }
      // registers listener classes
      if (eventsLoaded == false) {
        getServer().getPluginManager().registerEvents(new EntityEvents(this), this);
        getServer().getPluginManager().registerEvents(new ServerEvents(this), this);
        eventsLoaded = true;
      }

      System.out.println("[startup] Starting CryptoPlugin");

      // loads config file. If it doesn't exist, creates it.
      getDataFolder().mkdir();
      System.out.println("[startup] checking default config file");

      if (!new java.io.File(getDataFolder(), "config.yml").exists()) {
        saveDefaultConfig();
        System.out.println("[startup] config file does not exist. creating default.");
      }

      if (NODE_HOST != null) {
        System.out.println("[startup] checking " + CRYPTO_TICKER + " node connection");
        nodeWallet = new NodeWallet("CryptoPlugin");
        nodeWallet.getBlockChainInfo();
      }

      // creates scheduled timers (update balances, etc)
      createScheduledTimers();
      commands = new HashMap<String, CommandAction>();
      commands.put("wallet", new WalletCommand(this));
      commands.put("SetFee", new SetFeeCommand(this));
      commands.put("tip", new TipCommand(this));
      commands.put("withdraw", new WithdrawCommand(this));
      modCommands = new HashMap<String, CommandAction>();
      modCommands.put("crashTest", new CrashtestCommand(this));
      modCommands.put("emergencystop", new EmergencystopCommand());

      System.out.println("[startup] finished");

    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("[fatal] plugin enable fails");
      Bukkit.shutdown();
    }
  }

  public static void announce(final String message) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      player.sendMessage(message);
    }
  }

  public void createScheduledTimers() {
    BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
    scheduler.scheduleSyncRepeatingTask(
        this,
        new Runnable() {
          @Override
          public void run() {}
        },
        0,
        7200L);
  }

  public void publish_stats() {
    try {
      nodeWallet = new NodeWallet("CryptoPlugin");
      nodeWallet.getBlockChainInfo();
      // Long balance = getBalance(SERVERDISPLAY_NAME,1); //error here
      // REDIS.set("loot:pool", Long.toString(balance));
      if (System.getenv("ELASTICSEARCH_ENDPOINT") != null) {
        JSONParser parser = new JSONParser();

        final JSONObject jsonObject = new JSONObject();

        // jsonObject.put("balance", balance);
        jsonObject.put("time", new Date().getTime());
        URL url = new URL(System.getenv("ELASTICSEARCH_ENDPOINT") + "-stats/_doc");
        // System.out.println(url.toString());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        con.setDoOutput(true);
        OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
        out.write(jsonObject.toString());
        out.close();

        if (con.getResponseCode() == 200) {

          BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
          String inputLine;
          StringBuffer response = new StringBuffer();

          while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
          }
          in.close();
          // System.out.println(response.toString());
          JSONObject response_object = (JSONObject) parser.parse(response.toString());
          // System.out.println(response_object);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void log(String msg) {
    Bukkit.getLogger().info(msg);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    // we don't allow server commands (yet?)
    if (sender instanceof Player) {
      final Player player = (Player) sender;
      // PLAYER COMMANDS
      for (Map.Entry<String, CommandAction> entry : commands.entrySet()) {
        if (cmd.getName().equalsIgnoreCase(entry.getKey())) {
          entry.getValue().run(sender, cmd, label, args, player);
        }
      }

      // MODERATOR COMMANDS
      for (Map.Entry<String, CommandAction> entry : modCommands.entrySet()) {
        if (cmd.getName().equalsIgnoreCase(entry.getKey())) {
          if (player.getUniqueId().toString() == ADMIN_UUID.toString()) {
            entry.getValue().run(sender, cmd, label, args, player);
          } else {
            sender.sendMessage(
                ChatColor.DARK_RED + "You don't have enough permissions to execute this command!");
          }
        }
      }
    }
    return true;
  }

  public boolean isStringInt(String s) {
    try {
      Integer.parseInt(s);
      return true;
    } catch (NumberFormatException ex) {
      return false;
    }
  }

  public static boolean isStringDouble(String s) {
    try {
      Double.parseDouble(s);
      return true;
    } catch (NumberFormatException ex) {
      return false;
    }
  }

  public void crashtest() {
    this.setEnabled(false);
  }

  public String getExchangeRate(String crypto) {
    String price = exRate.toString();
    String rate = exRate.toString();
    try {

      URL url =
          new URL(
              "https://api.coingecko.com/api/v3/simple/price?ids="
                  + crypto
                  + "&vs_currencies=USD&include_market_cap=false&include_24hr_vol=false&include_24hr_change=false&include_last_updated_at=false");

      // System.out.println(url.toString());
      HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("User-Agent", "Mozilla/1.22 (compatible; MSIE 2.0; Windows 3.1)");
      con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
      StringBuffer response = new StringBuffer();
      if (con.getResponseCode() == 200) {
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
        }
        in.close();
        // System.out.println(response.toString());

        JSONParser parser = new JSONParser();
        final JSONObject jsonobj, jsonobj2;

        jsonobj = (JSONObject) parser.parse(response.toString());
        jsonobj2 = (JSONObject) parser.parse(jsonobj.get(crypto).toString());
        // double val=Double.parseDouble(jsonobj2.get("price").toString());

        // ERROR HERE

        price = jsonobj2.get("usd").toString();
        // System.out.println(crypto + "price: "+price);

      } else {
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        String inputLine;
        response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
        }
        in.close();
        // System.out.println(response.toString());
      }

    } catch (Exception e) {
      System.out.println("[PRICE] problem updating price for " + crypto);
      e.printStackTrace();
      // wallet might be new and it's not listed on the blockchain yet
    }
    if (isStringDouble(price)) {
      return price;
    } else {
      return rate;
    }
  }

  public Long convertCoinToSats(Double wholeCoinAmount) {
    Double tempAmount = wholeCoinAmount;
    Long oneCoin = 1L;
    for (int x = 1; x <= CRYPTO_DECIMALS; x++) {
      // System.out.println(oneCoin);
      // tempAmount=tempAmount*10;
      oneCoin = oneCoin * 10L;
    }
    BigDecimal decimalSat = new BigDecimal(tempAmount * oneCoin);
    // System.out.println("tempAmount : "+decimalSat);
    return (Long.parseLong(decimalSat.toString()));
  }

  public Double convertSatsToCoin(Long satsIn) {
    Long tempAmount = satsIn;
    Double oneCoin = 1.0;
    for (int x = 1; x <= CRYPTO_DECIMALS; x++) {
      // System.out.println(oneCoin);
      // tempAmount=tempAmount*10;
      oneCoin = oneCoin * 0.1;
    }
    BigDecimal decimalSat = new BigDecimal(tempAmount * oneCoin);
    // System.out.println("tempAmount : "+decimalSat);
    return (Double.parseDouble(decimalSat.toString()));
  }

  public Long wholeCoin() {
    Long oneCoin = 1L;
    for (int x = 1; x <= CRYPTO_DECIMALS; x++) {
      // System.out.println(oneCoin);
      oneCoin = oneCoin * 10L;
    }
    System.out.println("total " + DENOMINATION_NAME + " in 1 coin: " + oneCoin);
    return oneCoin;
  }

  public Double oneSat() {
    String DCF = "0.";
    for (int y = 1; y <= CRYPTO_DECIMALS; y++) {
      DCF = DCF + "0";
    }
    System.out.println(DCF);
    DecimalFormat numberFormat = new DecimalFormat(DCF);
    globalDecimalFormat = numberFormat;
    Double oneSats = 1.0;
    for (int x = 1; x <= CRYPTO_DECIMALS; x++) {
      // System.out.println(numberFormat.format(oneSats));
      oneSats = oneSats * 0.1;
    }
    System.out.println("Lowest Crypto Decimal set: " + globalDecimalFormat.format(oneSats));
    return oneSats;
  }

  public Double howmanyDisplayDecimals() {
    String DCF = "0.";
    for (int y = 1; y <= DISPLAY_DECIMALS; y++) {
      DCF = DCF + "0";
    }
    System.out.println(DCF);
    DecimalFormat numberFormat = new DecimalFormat(DCF);
    displayDecimalFormat = numberFormat;
    Double oneSats = 1.0;
    for (int x = 1; x <= DISPLAY_DECIMALS; x++) {
      // System.out.println(numberFormat.format(oneSats));
      oneSats = oneSats * 0.1;
    }
    System.out.println("Lowest Display Decimal set: " + displayDecimalFormat.format(oneSats));
    return oneSats;
  }
  /* CRYPTO_DECIMALS @8
  cryptoplugin | [22:46:35 INFO]: #.00000000
  cryptoplugin | [22:46:35 INFO]: Lowest Decimal set: .00000001
  cryptoplugin | [22:46:35 INFO]: total Sats in 1 coin: 100000000

  CRYPTO_DECIMALS @6
  cryptoplugin | [22:44:49 INFO]: #.000000
  cryptoplugin | [22:44:49 INFO]: Lowest Decimal set: .000001
  cryptoplugin | [22:44:49 INFO]: total Sats in 1 coin: 1000000

  CRYPTO_DECIMALS @2
  cryptoplugin | [22:48:40 INFO]: #.00
  cryptoplugin | [22:48:40 INFO]: Lowest Decimal set: .01
  cryptoplugin | [22:48:40 INFO]: total Sats in 1 coin: 100

  */
} // EOF
