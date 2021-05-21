package com.cryptoplugin.cryptoplugin;

public class Node {
    public String NODE_HOST, COINGECKO_CRYPTO, CRYPTO_TICKER, USD_DECIMALS, DENOMINATION_NAME, NODE_USERNAME, NODE_PASSWORD, ADDRESS_URL, TX_URL;
    public int NODE_PORT, CRYPTO_DECIMALS, DISPLAY_DECIMALS, CONFS_TARGET;
    public Long DENOMINATION_FACTOR;
    public Node() {
        this.NODE_HOST="localhost";
        this.NODE_PORT=18333;
        this.NODE_USERNAME="testuser";
        this.NODE_PASSWORD="testpass";
        this.COINGECKO_CRYPTO="bitcoin";
        this.CRYPTO_TICKER="BTC";
        this.USD_DECIMALS="0.00";
        this.DENOMINATION_NAME="Sats";
        this.ADDRESS_URL="https://www.blockchain.com/btc/address/";
        this.TX_URL="https://www.blockchain.com/btc/tx/";
        this.CRYPTO_DECIMALS=8;
        this.DISPLAY_DECIMALS=8;
        this.CONFS_TARGET=6;
        this.DENOMINATION_FACTOR=1L;
    }
    public void setNode(String node_host, int node_port, String node_username, String node_password) {
        this.NODE_HOST=node_host;
        this.NODE_PORT=node_port;
        this.NODE_USERNAME=node_username;
        this.NODE_PASSWORD=node_password;
    }
    public void config(String coingecko_crypto, String crypto_ticker, String usd_decimals, String denomination_name, String address_url, String tx_url, int crypto_decimals, int display_decimals, int conf_target, Long denomination_factor) {
        this.COINGECKO_CRYPTO=coingecko_crypto;
        this.CRYPTO_TICKER=crypto_ticker;
        this.USD_DECIMALS=usd_decimals;
        this.DENOMINATION_NAME=denomination_name;
        this.ADDRESS_URL=address_url;
        this.TX_URL=tx_url;
        this.CRYPTO_DECIMALS=crypto_decimals;
        this.DISPLAY_DECIMALS=display_decimals;
        this.CONFS_TARGET=conf_target;
        this.DENOMINATION_FACTOR=denomination_factor;
    }
}
