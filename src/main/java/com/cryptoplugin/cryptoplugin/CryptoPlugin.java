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
  public boolean nodesLoaded = false;
  
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
          System.out.println("baseSat: "+ NODES.get(x).GlobalDecimalFormat.format(NODES.get(x).BaseSat));
        nodeWallet = new NodeWallet("CryptoPlugin", x);
        NODES.get(x).setTxFee(nodeWallet.getFee());
        nodeWallet.getBlockChainInfo();
              nodesLoaded = true;
        }
      }

      // creates scheduled timers (update balances, etc)
      createScheduledTimers();
      commands = new HashMap<String, CommandAction>();
      commands.put("wallet", new WalletCommand(this));
      commands.put("wallets", new WalletsCommand(this));
      commands.put("tip", new TipCommand(this));
      commands.put("withdraw", new WithdrawCommand(this));
      commands.put("Backupkey", new BackupkeyCommand(this));
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
    System.out.println("File Found " + nodeList[i].getName());
  } else if (nodeList[i].isDirectory()) {
    System.out.println("Directory " + nodeList[i].getName());
  }
}
        for (int n = 0; n < nodeList.length; n++) {
  

          FileReader reader = new FileReader(nodeList[n]);
            //Read JSON file
            Object obj = jsonParser.parse(reader);
 
            JSONArray nodeData = (JSONArray) obj;
            //System.out.println("nodeData: " + nodeData);

            final String tempName = nodeList[n].getName().substring(0, nodeList[n].getName().lastIndexOf('.'));
            final int whichNode = n;
            	    //System.out.println("nodeData.get(#): " + nodeData.get(n));
	    //System.out.println("nodeData.get($): " + nodeData.get(tempName));
	    nodeData.forEach( fnodes -> parseJSONnodes( (JSONObject) fnodes, tempName, whichNode ) );
	            //parseJSONnodes( (JSONObject) nodeData.get(n), tempName, n );

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
	     //System.out.println("nodetest :" + nodeCount);
	JSONObject fnodesObj = (JSONObject) fnodes.get(fileName);
          String tempNode_host = (String) fnodesObj.get("NODE_HOST") != null ? fnodesObj.get("NODE_HOST").toString() : NODES.get(nodeCount).NODE_HOST;    
          String tempNode_port1 = (String) fnodesObj.get("NODE_PORT") != null ? fnodesObj.get("NODE_PORT").toString() : String.valueOf(NODES.get(nodeCount).NODE_PORT);            
	Integer tempNode_port = Integer.parseInt(tempNode_port1);  
	String tempNode_username = (String) fnodesObj.get("NODE_USERNAME") != null ? fnodesObj.get("NODE_USERNAME").toString() : NODES.get(nodeCount).NODE_USERNAME;  
	String tempNode_password = (String) fnodesObj.get("NODE_PASSWORD") != null ? fnodesObj.get("NODE_PASSWORD").toString() : NODES.get(nodeCount).NODE_PASSWORD;  
        NODES.get(nodeCount).setNode(tempNode_host, tempNode_port, tempNode_username, tempNode_password);
        

	String tempCoingecko_crypto = (String) fnodesObj.get("COINGECKO_CRYPTO") != null ? fnodesObj.get("COINGECKO_CRYPTO").toString() : NODES.get(nodeCount).COINGECKO_CRYPTO;    
	String tempCrypto_ticker = (String) fnodesObj.get("CRYPTO_TICKER") != null ? fnodesObj.get("CRYPTO_TICKER").toString() : NODES.get(nodeCount).CRYPTO_TICKER;  
	String tempP_Flag = (String) fnodesObj.get("P_FLAG") != null ? fnodesObj.get("P_FLAG").toString() : NODES.get(nodeCount).P_FLAG;  
	String tempUSD_decimals = (String) fnodesObj.get("USD_DECIMALS") != null ? fnodesObj.get("USD_DECIMALS").toString() : NODES.get(nodeCount).USD_DECIMALS;  
	String tempDenomination_name = (String) fnodesObj.get("DENOMINATION_NAME") != null ? fnodesObj.get("DENOMINATION_NAME").toString() : NODES.get(nodeCount).DENOMINATION_NAME; 
	String tempAddress_url = (String) fnodesObj.get("ADDRESS_URL") != null ? fnodesObj.get("ADDRESS_URL").toString() : NODES.get(nodeCount).ADDRESS_URL;   
	String tempTX_url = (String) fnodesObj.get("TX_URL") != null ? fnodesObj.get("TX_URL").toString() : NODES.get(nodeCount).TX_URL;
          String tempCrypto_decimals1 = (String) fnodesObj.get("CRYPTO_DECIMALS") != null ? fnodesObj.get("CRYPTO_DECIMALS").toString() : String.valueOf(NODES.get(nodeCount).CRYPTO_DECIMALS);	 
	Integer tempCrypto_decimals = Integer.parseInt(tempCrypto_decimals1);
	          String tempDisplay_decimals1 = (String) fnodesObj.get("DISPLAY_DECIMALS") != null ? fnodesObj.get("DISPLAY_DECIMALS").toString() : String.valueOf(NODES.get(nodeCount).DISPLAY_DECIMALS);
	Integer tempDisplay_decimals = Integer.parseInt(tempDisplay_decimals1);  
	          String tempConfs_taget1 = (String) fnodesObj.get("CONFS_TARGET") != null ? fnodesObj.get("CONFS_TARGET").toString() : String.valueOf(NODES.get(nodeCount).CONFS_TARGET);
	Integer tempConfs_taget = Integer.parseInt(tempConfs_taget1);
		          String tempDenomination_factor1 = (String) fnodesObj.get("DENOMINATION_FACTOR") != null ? fnodesObj.get("DENOMINATION_FACTOR").toString() : String.valueOf(NODES.get(nodeCount).DENOMINATION_FACTOR);  
	long tempDenomination_factor = Long.valueOf(tempDenomination_factor1);  
        NODES.get(nodeCount).config(tempCoingecko_crypto, tempCrypto_ticker, tempP_Flag, tempUSD_decimals, tempDenomination_name, tempAddress_url, tempTX_url, tempCrypto_decimals, tempDisplay_decimals, tempConfs_taget, tempDenomination_factor);
	NODES.get(nodeCount).setExRate(Double.parseDouble(getExchangeRate(NODES.get(nodeCount).COINGECKO_CRYPTO)));
  }
  public boolean loadENVnode() {
    if (!useJSONnodes) {
     	NODES.add(new Node());
  ADMIN_UUID =
      System.getenv("ADMIN_UUID") != null ? UUID.fromString(System.getenv("ADMIN_UUID")) : null;
  String tempNode_host =
      System.getenv("NODE_HOST") != null ? System.getenv("NODE_HOST") : NODES.get(0).NODE_HOST;
  Integer tempNode_port =
      System.getenv("NODE_PORT") != null ? Integer.parseInt(System.getenv("NODE_PORT")) : NODES.get(0).NODE_PORT;
  String tempNode_username = System.getenv("NODE_USERNAME") != null ? System.getenv("NODE_USERNAME") : NODES.get(0).NODE_USERNAME;
  String tempNode_password = System.getenv("NODE_PASSWORD") != null ? System.getenv("NODE_PASSWORD") : NODES.get(0).NODE_PASSWORD;

        NODES.get(0).setNode(tempNode_host, tempNode_port, tempNode_username, tempNode_password);

  String tempCoingecko_crypto =
      System.getenv("COINGECKO_CRYPTO") != null ? System.getenv("COINGECKO_CRYPTO") : NODES.get(0).COINGECKO_CRYPTO;
  String tempCrypto_ticker =
      System.getenv("CRYPTO_TICKER") != null ? System.getenv("CRYPTO_TICKER") : NODES.get(0).CRYPTO_TICKER;
  String tempP_Flag =
      System.getenv("P_FLAG") != null ? System.getenv("P_FLAG") : NODES.get(0).P_FLAG;
  Long tempDenomination_factor =
      System.getenv("DENOMINATION_FACTOR") != null
          ? Long.parseLong(System.getenv("DENOMINATION_FACTOR"))
          : NODES.get(0).DENOMINATION_FACTOR;
  Integer tempCrypto_decimals =
      System.getenv("CRYPTO_DECIMALS") != null
          ? Integer.parseInt(System.getenv("CRYPTO_DECIMALS"))
          : NODES.get(0).CRYPTO_DECIMALS;
  Integer tempDisplay_decimals =
      System.getenv("DISPLAY_DECIMALS") != null
          ? Integer.parseInt(System.getenv("DISPLAY_DECIMALS"))
          : NODES.get(0).DISPLAY_DECIMALS;
  String tempUSD_decimals =
      System.getenv("USD_DECIMALS") != null ? System.getenv("USD_DECIMALS") : NODES.get(0).USD_DECIMALS;
  Integer tempConfs_taget =
      System.getenv("CONFS_TARGET") != null ? Integer.parseInt(System.getenv("CONFS_TARGET")) : NODES.get(0).CONFS_TARGET;
  String tempDenomination_name =
      System.getenv("DENOMINATION_NAME") != null ? System.getenv("DENOMINATION_NAME") : NODES.get(0).DENOMINATION_NAME;

  String tempAddress_url =
      System.getenv("ADDRESS_URL") != null
          ? System.getenv("ADDRESS_URL")
          : NODES.get(0).ADDRESS_URL;
  String tempTX_url =
      System.getenv("TX_URL") != null
          ? System.getenv("TX_URL")
          : NODES.get(0).TX_URL;
          
          NODES.get(0).config(tempCoingecko_crypto, tempCrypto_ticker, tempP_Flag, tempUSD_decimals, tempDenomination_name, tempAddress_url, tempTX_url, tempCrypto_decimals, tempDisplay_decimals, tempConfs_taget, tempDenomination_factor);
	NODES.get(0).setExRate(Double.parseDouble(getExchangeRate(NODES.get(0).COINGECKO_CRYPTO)));
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
          public void run() {
          try {
          if (nodesLoaded == true){
		for (int x = 0; x < NODES.size(); x++) {
		        nodeWallet = new NodeWallet("CryptoPlugin", x);
		 	NODES.get(x).setExRate(Double.parseDouble(getExchangeRate(NODES.get(x).COINGECKO_CRYPTO)));
		 	         System.out.println("[CryptoPlugin][exRate]["+NODES.get(x).COINGECKO_CRYPTO+"]: "+NODES.get(x).exRate);
                       NODES.get(x).setTxFee(nodeWallet.getFee());
		 	         System.out.println("[CryptoPlugin][txFee]["+NODES.get(x).COINGECKO_CRYPTO+"]: "+NODES.get(x).GlobalDecimalFormat.format(NODES.get(x).txFee));
		 	         
		}
          }
              } catch (Exception e) {
      e.printStackTrace();
    }
          }
        },
        0,
        18000L);
  }

  public void publish_stats() {
    try {
          	for (int x = 0; x < NODES.size(); x++) {
      nodeWallet = new NodeWallet("CryptoPlugin", x);
      nodeWallet.getBlockChainInfo();
      }
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

  public static String getExchangeRate(String crypto) {
    String price = "0.00";
    //String rate = exRate.toString();
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
      return price;
    }
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
