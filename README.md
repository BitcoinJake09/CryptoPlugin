This plugin is for player crypto wallet functionality, the end goal is to have a easily configurable plugin to allow players to recieve, withdraw, and tip ingame.
this is a 100% free to use plugin and has no additional fees added to it. use at your own risk.



========================================

To compile CryptoPlugin:
1. git clone https://github.com/BitcoinJake09/CryptoPlugin
2. cd CryptoPlugin
3. mvn clean compile assembly:single

.jar file will be in target folder.

To run compiled Jar:
=========================================
1. Download latest spigot jar
2. Start server with ```java -jar latest.jar```
    Restart the Spigot servers.
3. Copy CryptoPlugin.jar to plugin folder
4. Set EULA.txt to true
5. Edit and run setenv.sh script or json files
6. Make sure you have a crypto node running.
7. Edit server.properties to point to your local IP
8. Start server with 
    java -jar latest.jar
    
    
Example json file for node setup:
doge.json:

[{"doge":{
"NODE_HOST":"192.168.x.x",
"NODE_PORT":"xxxxx",
"NODE_USERNAME":"xxxxxxx",
"NODE_PASSWORD":"xxxxxxxxxxxxxxxx",
"COINGECKO_CRYPTO":"dogecoin",
"CRYPTO_TICKER":"DOGE",
"USD_DECIMALS":"0.0000",
"DENOMINATION_NAME":"shibes",
"ADDRESS_URL":"https://blockchair.com/dogecoin/address/",
"TX_URL":"https://blockchair.com/dogecoin/transaction/",
"CRYPTO_DECIMALS":"8",
"DISPLAY_DECIMALS":"4",
"CONFS_TARGET":"6",
"DENOMINATION_FACTOR":"1"}}]

json files should be in the folder plugins/CryptoPlugin/nodes/

![](https://media.discordapp.net/attachments/419294985419096064/846255863877468160/2021-05-24_01.17.54.png?width=802&height=451)
![](https://media.discordapp.net/attachments/419294985419096064/846257822316494858/2021-05-24_01.25.59.png?width=802&height=451)
![](https://media.discordapp.net/attachments/419294985419096064/847268770099626014/2021-05-24_22.32.04.png?width=842&height=451)



docker is no longer needed, redis is no longer used, no databases are used because the nodes themselves are the databases.
docker can be used(https://github.com/BitcoinJake09/CryptoPlugin/blob/main/Docker-readme), but the preferred way to run CryptoPlugin will be by downloading the stable release jar and configuring your .json file for your nodes.
it can be build from source and ran that way also.
