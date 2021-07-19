package com.cryptoplugin.cryptoplugin;

import java.text.*;
import java.math.BigDecimal;

public class Node {
    public String NODE_HOST, COINGECKO_CRYPTO, CRYPTO_TICKER, USD_DECIMALS, DENOMINATION_NAME, NODE_USERNAME, NODE_PASSWORD, ADDRESS_URL, TX_URL, P_FLAG;
    public int NODE_PORT, CRYPTO_DECIMALS, DISPLAY_DECIMALS, CONFS_TARGET;
    public Long DENOMINATION_FACTOR, WholeCoin;
    public double BaseSat, DisplaySats, exRate, txFee;
    public DecimalFormat GlobalDecimalFormat = new DecimalFormat("0.00000000");
    public DecimalFormat DisplayDecimalFormat = new DecimalFormat("0.00000000");
    public Node() {

    }
    public void DefaultNode(){
	 this.NODE_HOST="localhost";
        this.NODE_PORT=18333;
        this.NODE_USERNAME="testuser";
        this.NODE_PASSWORD="testpass";
        this.COINGECKO_CRYPTO="bitcoin";
        this.CRYPTO_TICKER="BTC";
        this.P_FLAG="BTC";
        this.USD_DECIMALS="0.00";
        this.DENOMINATION_NAME="Sats";
        this.ADDRESS_URL="https://www.blockchain.com/btc/address/";
        this.TX_URL="https://www.blockchain.com/btc/tx/";
        this.CRYPTO_DECIMALS=8;
        this.DISPLAY_DECIMALS=8;
        this.CONFS_TARGET=6;
        this.DENOMINATION_FACTOR=1L;
        wholeCoin();
        oneSat();
        howmanyDisplayDecimals();
    }
    public void setNode(String node_host, int node_port, String node_username, String node_password) {
        this.NODE_HOST=node_host;
        this.NODE_PORT=node_port;
        this.NODE_USERNAME=node_username;
        this.NODE_PASSWORD=node_password;
    }
    public void setExRate(double exrate) {
        DecimalFormat df = new DecimalFormat(USD_DECIMALS);
        this.exRate=exrate;
        this.exRate = Double.parseDouble(df.format(this.exRate));
    }
    public void setTxFee(double txfee) {
        this.txFee=txfee;
        if (this.CRYPTO_TICKER.equalsIgnoreCase("DOGE")){this.txFee=txfee * 0.1;}
        this.txFee = Double.parseDouble(GlobalDecimalFormat.format(this.txFee));
    }
    public void config(String coingecko_crypto, String crypto_ticker, String p_flag, String usd_decimals, String denomination_name, String address_url, String tx_url, int crypto_decimals, int display_decimals, int conf_target, Long denomination_factor) {
        this.COINGECKO_CRYPTO=coingecko_crypto;
        this.CRYPTO_TICKER=crypto_ticker;
        this.P_FLAG=p_flag;
        this.USD_DECIMALS=usd_decimals;
        this.DENOMINATION_NAME=denomination_name;
        this.ADDRESS_URL=address_url;
        this.TX_URL=tx_url;
        this.CRYPTO_DECIMALS=crypto_decimals;
        this.DISPLAY_DECIMALS=display_decimals;
        this.CONFS_TARGET=conf_target;
        this.DENOMINATION_FACTOR=denomination_factor;
        wholeCoin();
        oneSat();
        howmanyDisplayDecimals();
    }
    
   public void wholeCoin() {
    Long oneCoin = 1L;
    for (int x = 1; x <= this.CRYPTO_DECIMALS; x++) {
      // System.out.println(oneCoin);
      oneCoin = oneCoin * 10L;
    }
    //System.out.println("total " + this.DENOMINATION_NAME + " in 1 coin: " + oneCoin);
    this.WholeCoin = oneCoin;
  }

  public void oneSat() {
    String DCF = "0.";
    for (int y = 1; y <= this.CRYPTO_DECIMALS; y++) {
      DCF = DCF + "0";
    }
    //System.out.println(DCF);
    DecimalFormat numberFormat = new DecimalFormat(DCF);
    this.GlobalDecimalFormat = numberFormat;
    Double oneSats = 1.0;
    for (int x = 1; x <= this.CRYPTO_DECIMALS; x++) {
      //System.out.println(numberFormat.format(oneSats));
      oneSats = oneSats * 0.1;
    }
    //System.out.println("Lowest Crypto Decimal set: " + globalDecimalFormat.format(oneSats));
    this.BaseSat = Double.parseDouble(this.GlobalDecimalFormat.format(oneSats));
  }
  
    public void howmanyDisplayDecimals() {
    String DCF = "0.";
    for (int y = 1; y <= this.DISPLAY_DECIMALS; y++) {
      DCF = DCF + "0";
    }
    //System.out.println(DCF);
    DecimalFormat numberFormat = new DecimalFormat(DCF);
    this.DisplayDecimalFormat = numberFormat;
    Double oneSats = 1.0;
    for (int x = 1; x <= this.DISPLAY_DECIMALS; x++) {
      // System.out.println(numberFormat.format(oneSats));
      oneSats = oneSats * 0.1;
    }
    //System.out.println("Lowest Display Decimal set: " + displayDecimalFormat.format(oneSats));
    DisplaySats = oneSats;
  }
  
    public Long convertCoinToSats(Double wholeCoinAmount) {
    Double tempAmount = wholeCoinAmount;
    Long oneCoin = 1L;
    for (int x = 1; x <= this.CRYPTO_DECIMALS; x++) {
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
    for (int x = 1; x <= this.CRYPTO_DECIMALS; x++) {
      // System.out.println(oneCoin);
      // tempAmount=tempAmount*10;
      oneCoin = oneCoin * 0.1;
    }
    BigDecimal decimalSat = new BigDecimal(tempAmount * oneCoin);
    // System.out.println("tempAmount : "+decimalSat);
    return (Double.parseDouble(decimalSat.toString()));
  } 

}
