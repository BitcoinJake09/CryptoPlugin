package com.cryptoplugin.cryptoplugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class NodeWallet {
  private CryptoPlugin cryptoPlugin;
  public String account_id;
  public String address;
  public int walletArray;

  public NodeWallet(String _account_id, int whichWallet) {
    this.account_id = _account_id;
    this.walletArray = whichWallet;
   
    try {
      if (getAccountAddress().equals("[]")) {
       this.address = getNewAccountAddress();
       }
      else {this.address = getAccountAddress();}
      System.out.println("address loaded: " + this.address);
    } catch (Exception e) {
      e.printStackTrace();
      //System.out.println("[address] error.");
    }
  }

  public String getAccountAddress() throws IOException, ParseException {
    try {
      JSONParser parser = new JSONParser();
      final JSONObject jsonObject = new JSONObject();
      JSONArray params = new JSONArray();
      jsonObject.put("jsonrpc", "1.0");
      jsonObject.put("id", "cryptoplugin");
      if (CryptoPlugin.NODES.get(this.walletArray).COINGECKO_CRYPTO.equalsIgnoreCase("DeVault")) {
	jsonObject.put("method", "getaddressesbylabels");
      } else {
      jsonObject.put("method", "getaddressesbyaccount");
            params.add(account_id);
      }

      jsonObject.put("params", params);
      URL url = new URL("http://" + CryptoPlugin.NODES.get(this.walletArray).NODE_HOST + ":" + CryptoPlugin.NODES.get(this.walletArray).NODE_PORT);
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      String userPassword = CryptoPlugin.NODES.get(this.walletArray).NODE_USERNAME + ":" + CryptoPlugin.NODES.get(this.walletArray).NODE_PASSWORD;
      String encoding = Base64.getEncoder().encodeToString(userPassword.getBytes());
      con.setRequestProperty("Authorization", "Basic " + encoding);
      con.setConnectTimeout(5000);
      con.setRequestMethod("POST");
      con.setRequestProperty("User-Agent", "Mozilla/1.22 (compatible; MSIE 2.0; Windows 3.1)");
      con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
      con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
      con.setDoOutput(true);
      OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
      out.write(jsonObject.toString());
      out.close();
            if (CryptoPlugin.NODES.get(this.walletArray).COINGECKO_CRYPTO.equalsIgnoreCase("DeVault")) {
    int responseCode = con.getResponseCode();
    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuffer response = new StringBuffer();
    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();
    parser = new JSONParser();
    JSONObject response_object = (JSONObject) parser.parse(response.toString());
    String tempAddy = response_object.get("result").toString();
    JSONObject fnodesObj = (JSONObject) response_object.get("result");
    String tempaddy = (String) fnodesObj.get(this.account_id); 
    return tempaddy;


      } else {
    int responseCode = con.getResponseCode();
    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuffer response = new StringBuffer();
    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();
    parser = new JSONParser();
    JSONObject response_object = (JSONObject) parser.parse(response.toString());
          if (!(response_object.get("result").toString().equals("[]"))) {
	String subStr = response_object.get("result").toString().substring(response_object.get("result").toString().indexOf("[") + 1,response_object.get("result").toString().indexOf("]"));
      String finalString = subStr.substring(subStr.indexOf("\"") + 1, subStr.lastIndexOf("\""));
            return finalString; // just give them an empty object
          }
            return response_object.get("result").toString(); // just give them an empty object
      }
    } catch (IOException e) {
      System.out.println("address not found for: " + account_id);
      System.out.println("will attempt to create address.");
      // System.out.println(e);
      // Unable to call API?
      return "[]";
    }
  }

  public String getNewAccountAddress() throws IOException, ParseException {
    JSONParser parser = new JSONParser();

    final JSONObject jsonObject = new JSONObject();
    jsonObject.put("jsonrpc", "1.0");
    jsonObject.put("id", "cryptoplugin");
    jsonObject.put("method", "getnewaddress");
    JSONArray params = new JSONArray();
    params.add(account_id);
    jsonObject.put("params", params);
    URL url = new URL("http://" + CryptoPlugin.NODES.get(this.walletArray).NODE_HOST + ":" + CryptoPlugin.NODES.get(this.walletArray).NODE_PORT);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    String userPassword = CryptoPlugin.NODES.get(this.walletArray).NODE_USERNAME + ":" + CryptoPlugin.NODES.get(this.walletArray).NODE_PASSWORD;
    String encoding = Base64.getEncoder().encodeToString(userPassword.getBytes());
    con.setRequestProperty("Authorization", "Basic " + encoding);
    con.setConnectTimeout(5000);
    con.setRequestMethod("POST");
    con.setRequestProperty("User-Agent", "Mozilla/1.22 (compatible; MSIE 2.0; Windows 3.1)");
    con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
    con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
    con.setDoOutput(true);
    OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
    out.write(jsonObject.toString());
    out.close();

    int responseCode = con.getResponseCode();
    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuffer response = new StringBuffer();
    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();
    parser = new JSONParser();
    JSONObject response_object = (JSONObject) parser.parse(response.toString());
    return response_object.get("result").toString();
  }

  public Long getBalance()
      throws IOException, org.json.simple.parser.ParseException {
    try {
      JSONParser parser = new JSONParser();

      final JSONObject jsonObject = new JSONObject();
      jsonObject.put("jsonrpc", "1.0");
      jsonObject.put("id", "cryptoplugin");

      JSONArray params = new JSONArray();
            if (CryptoPlugin.NODES.get(this.walletArray).COINGECKO_CRYPTO.equalsIgnoreCase("DeVault")) {
                  jsonObject.put("method", "getbalance");
            params.add(account_id);
      } else {
            jsonObject.put("method", "getreceivedbyaddress");
      params.add(this.address);
      }
      //System.out.println("Parms: " + params);
      jsonObject.put("params", params);
      URL url = new URL("http://" + CryptoPlugin.NODES.get(this.walletArray).NODE_HOST + ":" + CryptoPlugin.NODES.get(this.walletArray).NODE_PORT);
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      String userPassword = CryptoPlugin.NODES.get(this.walletArray).NODE_USERNAME + ":" + CryptoPlugin.NODES.get(this.walletArray).NODE_PASSWORD;
      String encoding = Base64.getEncoder().encodeToString(userPassword.getBytes());
      con.setRequestProperty("Authorization", "Basic " + encoding);

      con.setRequestMethod("POST");
      con.setRequestProperty("User-Agent", "Mozilla/1.22 (compatible; MSIE 2.0; Windows 3.1)");
      con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
      con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
      con.setDoOutput(true);
      OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
      //System.out.println(jsonObject.toString());
      out.write(jsonObject.toString());
      out.close();

      int responseCode = con.getResponseCode();
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      String inputLine;
      StringBuffer response = new StringBuffer();
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();
      parser = new JSONParser();
      JSONObject response_object = (JSONObject) parser.parse(response.toString());
      //System.out.println(response_object);
      Double d = Double.parseDouble(response_object.get("result").toString().trim()) * CryptoPlugin.NODES.get(this.walletArray).WholeCoin;
      final Long balance = d.longValue();
      return balance;

    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0L;
  }

  public double getGetSpendable()
      throws IOException, org.json.simple.parser.ParseException {
    try {
      JSONParser parser = new JSONParser();
      final JSONObject UTXOs = this.listunspent();
      //System.out.println("listunspent?: "+ UTXOs.toString());
      //System.out.println("UTXOs.toString().get(result)?: "+ UTXOs.get("result").toString());
      JSONArray jsonArray = (JSONArray) parser.parse(UTXOs.get("result").toString());
      //System.out.println("jsonArray.toString()?: "+ jsonArray.toString());
      double[] tempAmount = new double[jsonArray.size()];
      double finalAmount = 0.0;
      String[] tempTXID = new String[jsonArray.size()];
      int[] tempVout = new int[jsonArray.size()];
      for(int i=0;i<jsonArray.size();i++){
        Object obj = jsonArray.get(i);
        JSONObject json = new JSONObject();
        if (obj instanceof JSONObject) {
          json = ((JSONObject) obj);
        }
        tempAmount[i] = Double.parseDouble(json.get("amount").toString());
        finalAmount = finalAmount + Double.parseDouble(json.get("amount").toString());
        tempTXID[i] = json.get("txid").toString();
        tempVout[i] = Integer.parseInt(json.get("vout").toString());
      }
    //System.out.println("UTXO balance: "+ finalAmount);
    return finalAmount;
    
        } catch (Exception e) {
      e.printStackTrace();
    }
    return 0.0;
  }

public double getFee()
      throws IOException, org.json.simple.parser.ParseException {
    try {
      JSONParser parser = new JSONParser();

      final JSONObject jsonObject = new JSONObject();
      jsonObject.put("jsonrpc", "1.0");
      jsonObject.put("id", "cryptoplugin");
      jsonObject.put("method", "estimatefee");
      JSONArray params = new JSONArray();
            if (CryptoPlugin.NODES.get(this.walletArray).COINGECKO_CRYPTO.equalsIgnoreCase("DeVault")) {
            //params.add(account_id);
      } else {
      params.add(CryptoPlugin.NODES.get(this.walletArray).CONFS_TARGET);
      }
      //System.out.println("Parms: " + params);
      jsonObject.put("params", params);
      URL url = new URL("http://" + CryptoPlugin.NODES.get(this.walletArray).NODE_HOST + ":" + CryptoPlugin.NODES.get(this.walletArray).NODE_PORT);
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      String userPassword = CryptoPlugin.NODES.get(this.walletArray).NODE_USERNAME + ":" + CryptoPlugin.NODES.get(this.walletArray).NODE_PASSWORD;
      String encoding = Base64.getEncoder().encodeToString(userPassword.getBytes());
      con.setRequestProperty("Authorization", "Basic " + encoding);

      con.setRequestMethod("POST");
      con.setRequestProperty("User-Agent", "Mozilla/1.22 (compatible; MSIE 2.0; Windows 3.1)");
      con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
      con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
      con.setDoOutput(true);
      OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
      //System.out.println(jsonObject.toString());
      out.write(jsonObject.toString());
      out.close();

      int responseCode = con.getResponseCode();
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      String inputLine;
      StringBuffer response = new StringBuffer();
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();
      parser = new JSONParser();
      JSONObject response_object = (JSONObject) parser.parse(response.toString());
      //System.out.println(response_object);
      Double fee = Double.parseDouble(response_object.get("result").toString());// * 100000000L;
      //final double fee = d.longValue();
      //setFee(fee);
      return fee;

    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0L;
  }

  // @todo: make this just accept the endpoint name and (optional) parameters
  public JSONObject getBlockChainInfo() throws org.json.simple.parser.ParseException {
    JSONParser parser = new JSONParser();

    try {
      final JSONObject jsonObject = new JSONObject();
      jsonObject.put("jsonrpc", "1.0");
      jsonObject.put("id", "CryptoPlugin");
      jsonObject.put("method", "getblockchaininfo");
      JSONArray params = new JSONArray();
      jsonObject.put("params", params);
      // System.out.println("Checking blockchain info...");
      URL url = new URL("http://" + CryptoPlugin.NODES.get(this.walletArray).NODE_HOST + ":" + CryptoPlugin.NODES.get(this.walletArray).NODE_PORT);
      // System.out.println(url.toString());
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      String userPassword = CryptoPlugin.NODES.get(this.walletArray).NODE_USERNAME + ":" + CryptoPlugin.NODES.get(this.walletArray).NODE_PASSWORD;
      String encoding = java.util.Base64.getEncoder().encodeToString(userPassword.getBytes());
      con.setRequestProperty("Authorization", "Basic " + encoding);

      con.setRequestMethod("POST");
      con.setRequestProperty("User-Agent", "CryptoPlugin");
      con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
      con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
      con.setDoOutput(true);
      OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
      out.write(jsonObject.toString());
      out.close();

      int responseCode = con.getResponseCode();
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      String inputLine;
      StringBuffer response = new StringBuffer();
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();
      parser = new JSONParser();
      JSONObject response_object = (JSONObject) parser.parse(response.toString());
      //System.out.println(response_object);
      return new JSONObject(response_object); // just give them an empty object

    } catch (IOException e) {
      System.out.println("problem connecting with " + CryptoPlugin.NODES.get(this.walletArray).CRYPTO_TICKER + " node");
      System.out.println(e);
      // Unable to call API?
    }

    return new JSONObject(); // just give them an empty object
  }

  public JSONObject listunspent() throws org.json.simple.parser.ParseException {
    JSONParser parser = new JSONParser();

    try {
      final JSONObject jsonObject = new JSONObject();
      jsonObject.put("jsonrpc", "1.0");
      jsonObject.put("id", "CryptoPlugin");
      jsonObject.put("method", "listunspent");
      JSONArray params = new JSONArray();
      params.add(6);
      params.add(9999999);
      JSONArray addys = new JSONArray();
        addys.add(this.address);
      params.add(addys);
      params.add(true);
      final JSONObject amt = new JSONObject();
        amt.put("minimumAmount", 0.01);
      params.add(amt);
      jsonObject.put("params", params);
      // System.out.println("Checking blockchain info...");
      URL url = new URL("http://" + CryptoPlugin.NODES.get(this.walletArray).NODE_HOST + ":" + CryptoPlugin.NODES.get(this.walletArray).NODE_PORT);
      // System.out.println(url.toString());
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      String userPassword = CryptoPlugin.NODES.get(this.walletArray).NODE_USERNAME + ":" + CryptoPlugin.NODES.get(this.walletArray).NODE_PASSWORD;
      String encoding = java.util.Base64.getEncoder().encodeToString(userPassword.getBytes());
      con.setRequestProperty("Authorization", "Basic " + encoding);

      con.setRequestMethod("POST");
      con.setRequestProperty("User-Agent", "CryptoPlugin");
      con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
      con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
      con.setDoOutput(true);
      OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
      out.write(jsonObject.toString());
      out.close();

      int responseCode = con.getResponseCode();
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      String inputLine;
      StringBuffer response = new StringBuffer();
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();
      parser = new JSONParser();
      JSONObject response_object = (JSONObject) parser.parse(response.toString());
      //System.out.println(response_object);
      return new JSONObject(response_object); // just give them an empty object

    } catch (IOException e) {
      System.out.println("problem connecting with " + CryptoPlugin.NODES.get(this.walletArray).CRYPTO_TICKER + " node");
      System.out.println(e);
      // Unable to call API?
    }

    return new JSONObject(); // just give them an empty object
  }

  public String dumpprivkey() throws org.json.simple.parser.ParseException {
    JSONParser parser = new JSONParser();

    try {
      final JSONObject jsonObject = new JSONObject();
      jsonObject.put("jsonrpc", "1.0");
      jsonObject.put("id", "CryptoPlugin");
      jsonObject.put("method", "dumpprivkey");
      JSONArray params = new JSONArray();
      params.add(this.address);
      jsonObject.put("params", params);
      // System.out.println("Checking blockchain info...");
      URL url = new URL("http://" + CryptoPlugin.NODES.get(this.walletArray).NODE_HOST + ":" + CryptoPlugin.NODES.get(this.walletArray).NODE_PORT);
      // System.out.println(url.toString());
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      String userPassword = CryptoPlugin.NODES.get(this.walletArray).NODE_USERNAME + ":" + CryptoPlugin.NODES.get(this.walletArray).NODE_PASSWORD;
      String encoding = java.util.Base64.getEncoder().encodeToString(userPassword.getBytes());
      con.setRequestProperty("Authorization", "Basic " + encoding);

      con.setRequestMethod("POST");
      con.setRequestProperty("User-Agent", "CryptoPlugin");
      con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
      con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
      con.setDoOutput(true);
      OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
      out.write(jsonObject.toString());
      out.close();

      int responseCode = con.getResponseCode();
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      String inputLine;
      StringBuffer response = new StringBuffer();
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();
      parser = new JSONParser();
      JSONObject response_object = (JSONObject) parser.parse(response.toString());
      return response_object.get("result").toString(); // just give them an empty object

    } catch (IOException e) {
      System.out.println("problem connecting with " + CryptoPlugin.NODES.get(this.walletArray).CRYPTO_TICKER + " node");
      System.out.println(e);
      // Unable to call API?
    }

    return ""; // just give them an empty object
  }

  public String sendMany(String[] addressArray, Long[] sat) throws IOException, ParseException {
    try {
    double stopSat = 0.0;
    for (int s=0; s < sat.length; s++) {
    	stopSat = stopSat + (sat[s] * CryptoPlugin.NODES.get(this.walletArray).BaseSat);
    }
      JSONParser parser = new JSONParser();
    final JSONObject UTXOs = this.listunspent();
    	//System.out.println("listunspent?: "+ UTXOs.toString());
        	//System.out.println("UTXOs.toString().get(result)?: "+ UTXOs.get("result").toString());
        JSONArray jsonArray = (JSONArray) parser.parse(UTXOs.get("result").toString());
             	//System.out.println("jsonArray.toString()?: "+ jsonArray.toString());
    double[] tempAmount = new double[jsonArray.size()];
    String[] tempTXID = new String[jsonArray.size()];
    int[] tempVout = new int[jsonArray.size()];
        for(int i=0;i<jsonArray.size();i++){
    Object obj = jsonArray.get(i);
    JSONObject json = new JSONObject();
    if (obj instanceof JSONObject) {
          json = ((JSONObject) obj);
    }
    tempAmount[i] = Double.parseDouble(json.get("amount").toString());
    tempTXID[i] = json.get("txid").toString();
    tempVout[i] = Integer.parseInt(json.get("vout").toString());
    }
        
      final JSONObject jsonObject = new JSONObject();
    jsonObject.put("jsonrpc", "1.0");
    jsonObject.put("id", "CryptoPlugin");
    jsonObject.put("method", "createrawtransaction");
    JSONArray params = new JSONArray();
    JSONArray FinalUTXOarray = new JSONArray();
    double totalBalance = 0.0;
    for(int i=0;i<jsonArray.size();i++){
    if (totalBalance < (stopSat + this.getFee())) {
    JSONObject tempObject = new JSONObject();
    tempObject.put("txid" , tempTXID[i]);
    tempObject.put("vout" , tempVout[i]);
    //tempObject.put("amount" , tempAmount[i]);
    //System.out.println("tempTXID[i]: "+tempTXID[i]);
    //System.out.println("tempVout[i]: " + tempVout[i]);
    //System.out.println("tempAmount[i]: " + tempAmount[i]);

    FinalUTXOarray.add(tempObject);
	totalBalance = totalBalance + tempAmount[i];
     }
    }
	params.add(FinalUTXOarray);
	//System.out.println("params: " + params.toString());
	double satsRequested = 0.0;
    final JSONObject addresses = new JSONObject();
      for (int x = 0; x < addressArray.length; x++) {
      //System.out.println("sat["+x+"]: " + sat[x]);
      //System.out.println("cryptoPlugin.baseSat: "+ CryptoPlugin.NODES.get(this.walletArray).GlobalDecimalFormat.format(CryptoPlugin.NODES.get(this.walletArray).BaseSat));
        BigDecimal decimalSat = new BigDecimal(sat[x] * CryptoPlugin.NODES.get(this.walletArray).BaseSat);
        decimalSat = decimalSat.setScale(CryptoPlugin.NODES.get(this.walletArray).CRYPTO_DECIMALS, BigDecimal.ROUND_DOWN);
        //System.out.println("addressArray["+x+"]: "+ addressArray[x] + " decimalSat: "+ decimalSat);
        addresses.put(addressArray[x], decimalSat.doubleValue());
        satsRequested = satsRequested + decimalSat.doubleValue();
    }
    double changeRequest = totalBalance - satsRequested;
    if (CryptoPlugin.NODES.get(this.walletArray).COINGECKO_CRYPTO.equalsIgnoreCase("DeVault")) {
    changeRequest = changeRequest - (getFee());
      } else {
    changeRequest = changeRequest - (getFee() * (0.226));
      }
    BigDecimal changeSat = new BigDecimal(changeRequest);
        changeSat = changeSat.setScale(CryptoPlugin.NODES.get(this.walletArray).CRYPTO_DECIMALS, BigDecimal.ROUND_DOWN);
    addresses.put(this.address, changeSat);
    params.add(addresses);
    //System.out.println("params" + params);
    jsonObject.put("params", params);
      // System.out.println("Checking blockchain info...");
      URL url = new URL("http://" + CryptoPlugin.NODES.get(this.walletArray).NODE_HOST + ":" + CryptoPlugin.NODES.get(this.walletArray).NODE_PORT);
      // System.out.println(url.toString());
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      String userPassword = CryptoPlugin.NODES.get(this.walletArray).NODE_USERNAME + ":" + CryptoPlugin.NODES.get(this.walletArray).NODE_PASSWORD;
      String encoding = Base64.getEncoder().encodeToString(userPassword.getBytes());
      con.setRequestProperty("Authorization", "Basic " + encoding);

      con.setRequestMethod("POST");
      con.setRequestProperty("User-Agent", "Mozilla/1.22 (compatible; MSIE 2.0; Windows 3.1)");
      con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
      con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
      con.setDoOutput(true);
      OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
       //if (true == true) { return "failed"; } //testing
      out.write(jsonObject.toString());
      out.close();

      int responseCode = con.getResponseCode();
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      String inputLine;
      StringBuffer response = new StringBuffer();
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();

      JSONObject response_object = (JSONObject) parser.parse(response.toString());
      //System.out.println("unsigned: " + (String) response_object.get("result"));
      String signed = signrawtransaction((String) response_object.get("result"), tempTXID, tempVout);
      //System.out.println("signed: " + signed);
      String sent = sendrawtransaction(signed);
      return sent;

    } catch (Exception e) {
      e.printStackTrace();
    }
    return "failed";
  }
  
   public String signrawtransaction(String unsigned, String[]  tempTXID, int[] tempVout) throws IOException, ParseException {
   try {
    JSONParser parser = new JSONParser();

    final JSONObject jsonObject = new JSONObject();
    jsonObject.put("jsonrpc", "1.0");
    jsonObject.put("id", "cryptoplugin");
    jsonObject.put("method", "signrawtransaction");
    JSONArray params = new JSONArray();
    params.add(unsigned);
/*    JSONArray FinalUTXOarray = new JSONArray();
    for(int i=0;i<tempTXID.length;i++){
    JSONObject tempObject = new JSONObject();
    tempObject.put("txid" , tempTXID[i]);
    tempObject.put("vout" , tempVout[i]);
    tempObject.put("scriptPubKey" , validateAddress());
    System.out.println("tempTXID[i]: "+tempTXID[i]);
    System.out.println("tempVout[i]: " + tempVout[i]);
    System.out.println("scriptPubKey: " + validateAddress());
    FinalUTXOarray.add(tempObject);
    }
        params.add(FinalUTXOarray);
    String pkey = dumpprivkey();
        params.add(pkey);*/
    jsonObject.put("params", params);
    URL url = new URL("http://" + CryptoPlugin.NODES.get(this.walletArray).NODE_HOST + ":" + CryptoPlugin.NODES.get(this.walletArray).NODE_PORT);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    String userPassword = CryptoPlugin.NODES.get(this.walletArray).NODE_USERNAME + ":" + CryptoPlugin.NODES.get(this.walletArray).NODE_PASSWORD;
    String encoding = Base64.getEncoder().encodeToString(userPassword.getBytes());
    con.setRequestProperty("Authorization", "Basic " + encoding);
    con.setConnectTimeout(5000);
    con.setRequestMethod("POST");
    con.setRequestProperty("User-Agent", "Mozilla/1.22 (compatible; MSIE 2.0; Windows 3.1)");
    con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
    con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
    con.setDoOutput(true);
    OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
    out.write(jsonObject.toString());
    out.close();

    int responseCode = con.getResponseCode();
    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuffer response = new StringBuffer();
    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();
    parser = new JSONParser();
    JSONObject response_object = (JSONObject) parser.parse(response.toString());
    response_object = (JSONObject) response_object.get("result");
    return response_object.get("hex").toString();
        } catch (Exception e) {
      e.printStackTrace();
    }
    return "failed";
  }
  
     public String sendrawtransaction(String signed) throws IOException, ParseException {
     try {
    JSONParser parser = new JSONParser();

    final JSONObject jsonObject = new JSONObject();
    jsonObject.put("jsonrpc", "1.0");
    jsonObject.put("id", "cryptoplugin");
    jsonObject.put("method", "sendrawtransaction");
    JSONArray params = new JSONArray();
    params.add(signed);
    jsonObject.put("params", params);
    URL url = new URL("http://" + CryptoPlugin.NODES.get(this.walletArray).NODE_HOST + ":" + CryptoPlugin.NODES.get(this.walletArray).NODE_PORT);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    String userPassword = CryptoPlugin.NODES.get(this.walletArray).NODE_USERNAME + ":" + CryptoPlugin.NODES.get(this.walletArray).NODE_PASSWORD;
    String encoding = Base64.getEncoder().encodeToString(userPassword.getBytes());
    con.setRequestProperty("Authorization", "Basic " + encoding);
    con.setConnectTimeout(5000);
    con.setRequestMethod("POST");
    con.setRequestProperty("User-Agent", "Mozilla/1.22 (compatible; MSIE 2.0; Windows 3.1)");
    con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
    con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
    con.setDoOutput(true);
    OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
    out.write(jsonObject.toString());
    out.close();

    int responseCode = con.getResponseCode();
    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuffer response = new StringBuffer();
    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();
    parser = new JSONParser();
    JSONObject response_object = (JSONObject) parser.parse(response.toString());
    return response_object.get("result").toString();
        } catch (Exception e) {
      e.printStackTrace();
    }
    return "failed";
  }
      public String validateAddress() throws IOException, ParseException {
     try {
    JSONParser parser = new JSONParser();

    final JSONObject jsonObject = new JSONObject();
    jsonObject.put("jsonrpc", "1.0");
    jsonObject.put("id", "cryptoplugin");
    jsonObject.put("method", "validateaddress");
    JSONArray params = new JSONArray();
    params.add(this.address);
    jsonObject.put("params", params);
    URL url = new URL("http://" + CryptoPlugin.NODES.get(this.walletArray).NODE_HOST + ":" + CryptoPlugin.NODES.get(this.walletArray).NODE_PORT);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    String userPassword = CryptoPlugin.NODES.get(this.walletArray).NODE_USERNAME + ":" + CryptoPlugin.NODES.get(this.walletArray).NODE_PASSWORD;
    String encoding = Base64.getEncoder().encodeToString(userPassword.getBytes());
    con.setRequestProperty("Authorization", "Basic " + encoding);
    con.setConnectTimeout(5000);
    con.setRequestMethod("POST");
    con.setRequestProperty("User-Agent", "Mozilla/1.22 (compatible; MSIE 2.0; Windows 3.1)");
    con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
    con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
    con.setDoOutput(true);
    OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
    out.write(jsonObject.toString());
    out.close();

    int responseCode = con.getResponseCode();
    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuffer response = new StringBuffer();
    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();
    parser = new JSONParser();
    JSONObject response_object = (JSONObject) parser.parse(response.toString());
    response_object = (JSONObject) response_object.get("result");
    return response_object.get("scriptPubKey").toString();
        } catch (Exception e) {
      e.printStackTrace();
    }
    return "failed";
  }
}
