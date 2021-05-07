package com.cryptoplugin.cryptoplugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class NodeWallet {
  private CryptoPlugin cryptoPlugin;
  public String account_id;
  public String address;

  public NodeWallet(String _account_id) {
    this.account_id = _account_id;
    try {
      if (getAccountAddress() == null) this.address = getNewAccountAddress();
      else this.address = getAccountAddress();
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("[address] error.");
    }
  }

  public String getAccountAddress() throws IOException, ParseException {
    try {
      JSONParser parser = new JSONParser();

      final JSONObject jsonObject = new JSONObject();
      jsonObject.put("jsonrpc", "1.0");
      jsonObject.put("id", "cryptoplugin");
      jsonObject.put("method", "getaddressesbyaccount");
      JSONArray params = new JSONArray();
      params.add(account_id);
      if (CryptoPlugin.CRYPTOPLUGIN_ENV == "development")
        System.out.println("[getaddressesbyaccount] " + account_id);
      jsonObject.put("params", params);
      URL url = new URL("http://" + CryptoPlugin.NODE_HOST + ":" + CryptoPlugin.NODE_PORT);
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      String userPassword = CryptoPlugin.NODE_USERNAME + ":" + CryptoPlugin.NODE_PASSWORD;
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
      System.out.println(response_object);
      String subStr =
          response_object
              .get("result")
              .toString()
              .substring(
                  response_object.get("result").toString().indexOf("[") + 1,
                  response_object.get("result").toString().indexOf("]"));

      String finalString = subStr.substring(subStr.indexOf("\"") + 1, subStr.lastIndexOf("\""));
      return finalString; // just give them an empty object

    } catch (IOException e) {
      System.out.println("address not found for: " + account_id);
      System.out.println("will attempt to create address.");
      // System.out.println(e);
      // Unable to call API?
      return null;
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
    if (CryptoPlugin.CRYPTOPLUGIN_ENV == "development")
      System.out.println("[getnewaddress] " + account_id);
    jsonObject.put("params", params);
    URL url = new URL("http://" + CryptoPlugin.NODE_HOST + ":" + CryptoPlugin.NODE_PORT);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    String userPassword = CryptoPlugin.NODE_USERNAME + ":" + CryptoPlugin.NODE_PASSWORD;
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
    System.out.println(response_object);
    return response_object.get("result").toString();
  }

  public Long getBalance(int confirmations)
      throws IOException, org.json.simple.parser.ParseException {
    try {
      JSONParser parser = new JSONParser();

      final JSONObject jsonObject = new JSONObject();
      jsonObject.put("jsonrpc", "1.0");
      jsonObject.put("id", "cryptoplugin");
      jsonObject.put("method", "getreceivedbyaddress");
      JSONArray params = new JSONArray();
      params.add(address);
      params.add(confirmations);
      System.out.println("Parms: " + params);
      jsonObject.put("params", params);
      URL url = new URL("http://" + CryptoPlugin.NODE_HOST + ":" + CryptoPlugin.NODE_PORT);
      System.out.println(url.toString());
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      String userPassword = CryptoPlugin.NODE_USERNAME + ":" + CryptoPlugin.NODE_PASSWORD;
      String encoding = Base64.getEncoder().encodeToString(userPassword.getBytes());
      con.setRequestProperty("Authorization", "Basic " + encoding);

      con.setRequestMethod("POST");
      con.setRequestProperty("User-Agent", "Mozilla/1.22 (compatible; MSIE 2.0; Windows 3.1)");
      con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
      con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
      con.setDoOutput(true);
      OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
      System.out.println(jsonObject.toString());
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
      System.out.println(response_object);
      Double d = Double.parseDouble(response_object.get("result").toString().trim()) * 100000000L;
      final Long balance = d.longValue();
      System.out.println(balance);
      return balance;

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
      System.out.println("http://" + CryptoPlugin.NODE_HOST + ":" + CryptoPlugin.NODE_PORT);
      URL url = new URL("http://" + CryptoPlugin.NODE_HOST + ":" + CryptoPlugin.NODE_PORT);
      // System.out.println(url.toString());
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      String userPassword = CryptoPlugin.NODE_USERNAME + ":" + CryptoPlugin.NODE_PASSWORD;
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
      System.out.println(response_object);
      return new JSONObject(response_object); // just give them an empty object

    } catch (IOException e) {
      System.out.println("problem connecting with " + CryptoPlugin.CRYPTO_TICKER + " node");
      System.out.println(e);
      // Unable to call API?
    }

    return new JSONObject(); // just give them an empty object
  }

  public Long getReceivedByAddress(String account_id, int confirmations)
      throws IOException, org.json.simple.parser.ParseException {
    try {
      String address = CryptoPlugin.REDIS.get("nodeAddress" + account_id);
      JSONParser parser = new JSONParser();

      final JSONObject jsonObject = new JSONObject();
      jsonObject.put("jsonrpc", "1.0");
      jsonObject.put("id", "CryptoPlugin");
      jsonObject.put("method", "getreceivedbyaddress");
      JSONArray params = new JSONArray();
      params.add(address);
      params.add(confirmations);
      // System.out.println("Parms: " + params);
      jsonObject.put("params", params);
      URL url = new URL("http://" + CryptoPlugin.NODE_HOST + ":" + CryptoPlugin.NODE_PORT);
      // System.out.println(url.toString());
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      String userPassword = CryptoPlugin.NODE_USERNAME + ":" + CryptoPlugin.NODE_PASSWORD;
      String encoding = Base64.getEncoder().encodeToString(userPassword.getBytes());
      con.setRequestProperty("Authorization", "Basic " + encoding);

      con.setRequestMethod("POST");
      con.setRequestProperty("User-Agent", "Mozilla/1.22 (compatible; MSIE 2.0; Windows 3.1)");
      con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
      con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
      con.setDoOutput(true);
      OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
      // System.out.println(jsonObject.toString());
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
      System.out.println(response_object);

      Double d =
          Double.parseDouble(response_object.get("result").toString().trim())
              * cryptoPlugin.oneCoinSats;
      final Long balance = d.longValue();
      // System.out.println(balance);
      return balance;

    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0L;
  }

  public Long getBalance(String account_id, int confirmations)
      throws IOException, org.json.simple.parser.ParseException {
    try {
      // String address = REDIS.get("nodeAddress" + account_id);
      JSONParser parser = new JSONParser();

      final JSONObject jsonObject = new JSONObject();
      jsonObject.put("jsonrpc", "1.0");
      jsonObject.put("id", "CryptoPlugin");
      jsonObject.put("method", "getbalance");
      JSONArray params = new JSONArray();
      params.add("*");
      params.add(confirmations);
      // System.out.println("Parms: " + params);
      jsonObject.put("params", params);
      URL url = new URL("http://" + CryptoPlugin.NODE_HOST + ":" + CryptoPlugin.NODE_PORT);
      // System.out.println(url.toString());
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      String userPassword = CryptoPlugin.NODE_USERNAME + ":" + CryptoPlugin.NODE_PASSWORD;
      String encoding = Base64.getEncoder().encodeToString(userPassword.getBytes());
      con.setRequestProperty("Authorization", "Basic " + encoding);

      con.setRequestMethod("POST");
      con.setRequestProperty("User-Agent", "Mozilla/1.22 (compatible; MSIE 2.0; Windows 3.1)");
      con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
      con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
      con.setDoOutput(true);
      OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
      // System.out.println(jsonObject.toString());
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
      System.out.println(response_object);
      Double d =
          Double.parseDouble(response_object.get("result").toString().trim())
              * cryptoPlugin.oneCoinSats;
      final Long balance = d.longValue();
      // System.out.println(balance);
      return balance;

    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0L;
  }

  public String getAccountAddress(String account_id) throws IOException, ParseException {
    try {
      JSONParser parser = new JSONParser();

      final JSONObject jsonObject = new JSONObject();
      jsonObject.put("jsonrpc", "1.0");
      jsonObject.put("id", "CryptoPlugin");
      jsonObject.put("method", "getaddressesbylabel");
      JSONArray params = new JSONArray();
      params.add(account_id);
      jsonObject.put("params", params);
      URL url = new URL("http://" + CryptoPlugin.NODE_HOST + ":" + CryptoPlugin.NODE_PORT);
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      String userPassword = CryptoPlugin.NODE_USERNAME + ":" + CryptoPlugin.NODE_PASSWORD;
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
      System.out.println(response_object);
      return null;

    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public String sendToAddress(String account_id, String address, Long sat)
      throws IOException, ParseException {
    try {
      JSONParser parser = new JSONParser();

      final JSONObject jsonObject = new JSONObject();
      jsonObject.put("jsonrpc", "1.0");
      jsonObject.put("id", "CryptoPlugin");
      jsonObject.put("method", "sendtoaddress");
      JSONArray params = new JSONArray();
      params.add(address);
      // System.out.println(sat);
      BigDecimal decimalSat = new BigDecimal(sat * cryptoPlugin.baseSat);
      decimalSat = decimalSat.setScale(CryptoPlugin.CRYPTO_DECIMALS, BigDecimal.ROUND_DOWN);
      // System.out.println(decimalSat);
      params.add(decimalSat);
      params.add("CryptoPlugin");
      params.add("CryptoPlugin");
      params.add(false);
      params.add(false);
      params.add(CryptoPlugin.CONFS_TARGET);
      // System.out.println(params);
      jsonObject.put("params", params);
      // System.out.println("Checking blockchain info...");
      URL url = new URL("http://" + CryptoPlugin.NODE_HOST + ":" + CryptoPlugin.NODE_PORT);
      // System.out.println(url.toString());
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      String userPassword = CryptoPlugin.NODE_USERNAME + ":" + CryptoPlugin.NODE_PASSWORD;
      String encoding = Base64.getEncoder().encodeToString(userPassword.getBytes());
      con.setRequestProperty("Authorization", "Basic " + encoding);

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
      System.out.println(response_object);
      // System.out.println(response_object);
      return (String) response_object.get("result");

    } catch (Exception e) {
      e.printStackTrace();
    }
    return "failed";
  }

  public String sendMany(String account_id, String address1, String address2, Long sat1, Long sat2)
      throws IOException, ParseException {
    try {
      JSONParser parser = new JSONParser();

      final JSONObject jsonObject = new JSONObject();
      jsonObject.put("jsonrpc", "1.0");
      jsonObject.put("id", "CryptoPlugin");
      jsonObject.put("method", "sendmany");
      JSONArray params = new JSONArray();
      params.add("");
      final JSONObject addresses = new JSONObject();

      // System.out.println(sat1);
      BigDecimal decimalSat1 = new BigDecimal(sat1 * cryptoPlugin.baseSat);
      decimalSat1 = decimalSat1.setScale(CryptoPlugin.CRYPTO_DECIMALS, BigDecimal.ROUND_DOWN);
      // System.out.println(decimalSat1);
      addresses.put(address1, decimalSat1);

      // System.out.println(sat2);
      BigDecimal decimalSat2 = new BigDecimal(sat2 * cryptoPlugin.baseSat);
      decimalSat2 = decimalSat2.setScale(CryptoPlugin.CRYPTO_DECIMALS, BigDecimal.ROUND_DOWN);
      // System.out.println(decimalSat2);
      addresses.put(address2, decimalSat2);
      params.add(addresses);

      params.add(CryptoPlugin.CONFS_TARGET);
      params.add("CryptoPlugin"); // the comment :p

      // System.out.println(params);
      jsonObject.put("params", params);
      // System.out.println("Checking blockchain info...");
      URL url = new URL("http://" + CryptoPlugin.NODE_HOST + ":" + CryptoPlugin.NODE_PORT);
      // System.out.println(url.toString());
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      String userPassword = CryptoPlugin.NODE_USERNAME + ":" + CryptoPlugin.NODE_PASSWORD;
      String encoding = Base64.getEncoder().encodeToString(userPassword.getBytes());
      con.setRequestProperty("Authorization", "Basic " + encoding);

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
      System.out.println(response_object);
      return (String) response_object.get("result");

    } catch (Exception e) {
      e.printStackTrace();
    }
    return "failed";
  }

  public boolean setSatByte(String account_id, double sats) throws IOException, ParseException {
    try {
      JSONParser parser = new JSONParser();
      final JSONObject jsonObject = new JSONObject();
      jsonObject.put("jsonrpc", "1.0");
      jsonObject.put("id", "CryptoPlugin");
      jsonObject.put("method", "settxfee");
      JSONArray params = new JSONArray();
      // System.out.println(sat);
      BigDecimal decimalSat = new BigDecimal(sats * 0.00001);
      decimalSat = decimalSat.setScale(CryptoPlugin.CRYPTO_DECIMALS, BigDecimal.ROUND_DOWN);
      // System.out.println(decimalSat);
      params.add(decimalSat);
      System.out.println(account_id + " set fee to: " + decimalSat);
      // System.out.println(params);
      jsonObject.put("params", params);
      // System.out.println("Checking blockchain info...");
      URL url = new URL("http://" + CryptoPlugin.NODE_HOST + ":" + CryptoPlugin.NODE_PORT);
      // System.out.println(url.toString());
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      String userPassword = CryptoPlugin.NODE_USERNAME + ":" + CryptoPlugin.NODE_PASSWORD;
      String encoding = Base64.getEncoder().encodeToString(userPassword.getBytes());
      con.setRequestProperty("Authorization", "Basic " + encoding);

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
      System.out.println(response_object);
      return (boolean) response_object.get("result");

    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
}

/*
{
    System.out.println("getbalance: " + account_id);
    System.out.println("balanceaddress: " + address);
    try {
      JSONParser parser = new JSONParser();
      final JSONObject jsonObject = new JSONObject();
      jsonObject.put("jsonrpc", "1.0");
      jsonObject.put("id", "cryptoplugin");
      jsonObject.put("method", "getbalance");
      JSONArray params = new JSONArray();
      params.add("*");
      params.add(confirmations);
      //params.add(confirmations);
      jsonObject.put("params", params);
      URL url = new URL("http://" + CryptoPlugin.BITCOIN_NODE_HOST + ":" + CryptoPlugin.BITCOIN_NODE_PORT + "/wallet/" + account_id);
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setConnectTimeout(5000);
      String userPassword = CryptoPlugin.BITCOIN_NODE_USERNAME + ":" + CryptoPlugin.BITCOIN_NODE_PASSWORD;
      String encoding = Base64.getEncoder().encodeToString(userPassword.getBytes());
      con.setRequestProperty("Authorization", "Basic " + encoding);

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
      JSONObject response_object = (JSONObject) parser.parse(response.toString());
      Double d = Double.parseDouble(response_object.get("result").toString().trim()) * 100000000L;

      final Long balance = d.longValue();
      return balance;
    } catch (Exception e) {
      System.out.println(e);
      return Long.valueOf(0);
    }
  }
*/
