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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CryptoPlugin extends JavaPlugin {
  private NodeWallet nodeWallet = null;
  public static final List<Node> NODES = new ArrayList<>();
  public static final HashMap<UUID, Integer> whichWallet = new HashMap<UUID, Integer>();

  boolean useJSONnodes = false;
  boolean loadENVnode = true;
  public boolean eventsLoaded = false;
  public boolean maintenance_mode = false;
  
  public static final String CRYPTOPLUGIN_ENV =
      System.getenv("CRYPTOPLUGIN_ENV") != null ? System.getenv("CRYPTOPLUGIN_ENV") : "development";
  public static UUID ADMIN_UUID =
      System.getenv("ADMIN_UUID") != null ? UUID.fromString(System.getenv("ADMIN_UUID")) : null;
  
  public static final String PLUGIN_WEBSITE = "https://github.com/BitcoinJake09/CryptoPlugin";


  public static int rand(int min, int max) {
    return min + (int) (Math.random() * ((max - min) + 1));
  }

  public Long wallet_balance_cache = 0L;
  public Double exRate = 99999999.99;
  public static DecimalFormat globalDecimalFormat = new DecimalFormat("0.00000000");
  public static DecimalFormat displayDecimalFormat = new DecimalFormat("0.00000000");
  public static Double baseSat ;//= oneSat();
  public Double displaySats ;//= howmanyDisplayDecimals();
  public Long oneCoinSats ;//= wholeCoin();

  private Map<String, CommandAction> commands;
  private Map<String, CommandAction> modCommands;
  private Player[] moderators;

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
  	useJSONnodes = loadJSONnodes();
  	loadENVnode = loadENVnode();
      if (NODES.get(0).NODE_HOST != null) {
      	for (int x = 0; x < NODES.size(); x++) {
        System.out.println("[startup] checking " + NODES.get(x).CRYPTO_TICKER + " node connection");
        baseSat = oneSat();
	displaySats = howmanyDisplayDecimals();
	oneCoinSats = wholeCoin();
          System.out.println("baseSat: "+ globalDecimalFormat.format(baseSat));
        nodeWallet = new NodeWallet("CryptoPlugin", x);
        nodeWallet.getBlockChainInfo();
        }
      }

      // creates scheduled timers (update balances, etc)
      createScheduledTimers();
      commands = new HashMap<String, CommandAction>();
      commands.put("wallet", new WalletCommand(this));
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

  public boolean loadJSONnodes() {
  JSONParser jsonParser = new JSONParser();
  System.out.println("[CryptoPlugin] attempting to load node json files.");
          try {
		File nodeDir = new File(System.getProperty("user.dir") + "/plugins/CryptoPlugin/nodes/");
		if(nodeDir.isDirectory() && nodeDir.list().length == 0) {
			          return false; //Directory is empty 
		} else {

File[] nodeList = nodeDir.listFiles();

for (int i = 0; i < nodeList.length; i++) {
  if (nodeList[i].isFile()) {
    System.out.println("File " + nodeList[i].getName());
  } else if (nodeList[i].isDirectory()) {
    System.out.println("Directory " + nodeList[i].getName());
  }
}
        for (int n = 0; n < nodeList.length; n++) {
  

          FileReader reader = new FileReader(nodeList[n]);
            //Read JSON file
            Object obj = jsonParser.parse(reader);
 
            JSONArray nodeData = (JSONArray) obj;
            System.out.println(nodeData);
            final String tempName = nodeList[n].getName().substring(0, nodeList[n].getName().lastIndexOf('.'));
            final int whichNode = n;
            nodeData.forEach( fnodes -> parseJSONnodes( (JSONObject) fnodes, tempName, whichNode ) );
            }
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
           System.out.println("[CryptoPlugin] no node json files found, will default to ENV variables.");
          return false;
        } catch (IOException e) {
	   System.out.println("[CryptoPlugin] error reading json files, will default to ENV variables.");
            e.printStackTrace();
		return false;
        } catch (ParseException e) {
        System.out.println("[CryptoPlugin] error parsing json files, will default to ENV variables.");
            e.printStackTrace();
		return false;
        }
  }
  private static void parseJSONnodes(JSONObject fnodes, String fileName, int nodeCount) {
  	NODES.add(new Node());
  	//NODES.set(0, new Node());
	JSONObject fnodesObj = (JSONObject) fnodes.get(fileName);
          String tempNode_host = (String) fnodesObj.get("NODE_HOST");    
	Integer tempNode_port = Integer.parseInt((String) fnodesObj.get("NODE_PORT"));  
	String tempNode_username = (String) fnodesObj.get("NODE_USERNAME");  
	String tempNode_password = (String) fnodesObj.get("NODE_PASSWORD");  
        NODES.get(nodeCount).setNode(tempNode_host, tempNode_port, tempNode_username, tempNode_password);
        

	String tempCoingecko_crypto = (String) fnodesObj.get("COINGECKO_CRYPTO");    
	String tempCrypto_ticker = (String) fnodesObj.get("CRYPTO_TICKER");  
	String tempUSD_decimals = (String) fnodesObj.get("USD_DECIMALS");  
	String tempDenomination_name = (String) fnodesObj.get("DENOMINATION_NAME");  
	String tempAddress_url = (String) fnodesObj.get("ADDRESS_URL");    
	String tempTX_url = (String) fnodesObj.get("TX_URL");  
	Integer tempCrypto_decimals = Integer.parseInt((String) fnodesObj.get("CRYPTO_DECIMALS"));
	Integer tempDisplay_decimals = Integer.parseInt((String) fnodesObj.get("DISPLAY_DECIMALS"));  
	Integer tempConfs_taget = Integer.parseInt((String) fnodesObj.get("CONFS_TARGET"));  
	long tempDenomination_factor = Long.valueOf((String) fnodesObj.get("DENOMINATION_FACTOR"));  
        NODES.get(nodeCount).config(tempCoingecko_crypto, tempCrypto_ticker, tempUSD_decimals, tempDenomination_name, tempAddress_url, tempTX_url, tempCrypto_decimals, tempDisplay_decimals, tempConfs_taget, tempDenomination_factor);
  
  }
  public boolean loadENVnode() {
    if (!useJSONnodes) {
     	NODES.add(new Node());
  ADMIN_UUID =
      System.getenv("ADMIN_UUID") != null ? UUID.fromString(System.getenv("ADMIN_UUID")) : null;
  String tempNode_host =
      System.getenv("NODE_HOST") != null ? System.getenv("NODE_HOST") : null;
  Integer tempNode_port =
      System.getenv("NODE_PORT") != null ? Integer.parseInt(System.getenv("NODE_PORT")) : 8332;
  String tempNode_username = System.getenv("NODE_USERNAME");
  String tempNode_password = System.getenv("NODE_PASSWORD");

        NODES.get(0).setNode(tempNode_host, tempNode_port, tempNode_username, tempNode_password);

  String tempCoingecko_crypto =
      System.getenv("COINGECKO_CRYPTO") != null ? System.getenv("COINGECKO_CRYPTO") : "bitcoin";
  String tempCrypto_ticker =
      System.getenv("CRYPTO_TICKER") != null ? System.getenv("CRYPTO_TICKER") : "BTC";
  Long tempDenomination_factor =
      System.getenv("DENOMINATION_FACTOR") != null
          ? Long.parseLong(System.getenv("DENOMINATION_FACTOR"))
          : 1L;
  Integer tempCrypto_decimals =
      System.getenv("CRYPTO_DECIMALS") != null
          ? Integer.parseInt(System.getenv("CRYPTO_DECIMALS"))
          : 8;
  Integer tempDisplay_decimals =
      System.getenv("DISPLAY_DECIMALS") != null
          ? Integer.parseInt(System.getenv("DISPLAY_DECIMALS"))
          : 8;
  String tempUSD_decimals =
      System.getenv("USD_DECIMALS") != null ? System.getenv("USD_DECIMALS") : "0.00";
  Integer tempConfs_taget =
      System.getenv("CONFS_TARGET") != null ? Integer.parseInt(System.getenv("CONFS_TARGET")) : 6;
  String tempDenomination_name =
      System.getenv("DENOMINATION_NAME") != null ? System.getenv("DENOMINATION_NAME") : "Sats";

  String tempAddress_url =
      System.getenv("ADDRESS_URL") != null
          ? System.getenv("ADDRESS_URL")
          : "https://www.blockchain.com/btc/address/";
  String tempTX_url =
      System.getenv("TX_URL") != null
          ? System.getenv("TX_URL")
          : "https://www.blockchain.com/btc/tx/";
          
          NODES.get(0).config(tempCoingecko_crypto, tempCrypto_ticker, tempUSD_decimals, tempDenomination_name, tempAddress_url, tempTX_url, tempCrypto_decimals, tempDisplay_decimals, tempConfs_taget, tempDenomination_factor);
          
          return true;
    }
              return false;
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
      nodeWallet = new NodeWallet("CryptoPlugin", 0);
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
    for (int x = 1; x <= NODES.get(0).CRYPTO_DECIMALS; x++) {
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
    for (int x = 1; x <= NODES.get(0).CRYPTO_DECIMALS; x++) {
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
    for (int x = 1; x <= NODES.get(0).CRYPTO_DECIMALS; x++) {
      // System.out.println(oneCoin);
      oneCoin = oneCoin * 10L;
    }
    System.out.println("total " + NODES.get(0).DENOMINATION_NAME + " in 1 coin: " + oneCoin);
    return oneCoin;
  }

  public static Double oneSat() {
    String DCF = "0.";
    for (int y = 1; y <= NODES.get(0).CRYPTO_DECIMALS; y++) {
      DCF = DCF + "0";
    }
    System.out.println(DCF);
    DecimalFormat numberFormat = new DecimalFormat(DCF);
    globalDecimalFormat = numberFormat;
    Double oneSats = 1.0;
    for (int x = 1; x <= NODES.get(0).CRYPTO_DECIMALS; x++) {
      System.out.println(numberFormat.format(oneSats));
      oneSats = oneSats * 0.1;
    }
    System.out.println("Lowest Crypto Decimal set: " + globalDecimalFormat.format(oneSats));
    return Double.parseDouble(globalDecimalFormat.format(oneSats));
  }

  public Double howmanyDisplayDecimals() {
    String DCF = "0.";
    for (int y = 1; y <= NODES.get(0).DISPLAY_DECIMALS; y++) {
      DCF = DCF + "0";
    }
    System.out.println(DCF);
    DecimalFormat numberFormat = new DecimalFormat(DCF);
    displayDecimalFormat = numberFormat;
    Double oneSats = 1.0;
    for (int x = 1; x <= NODES.get(0).DISPLAY_DECIMALS; x++) {
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
