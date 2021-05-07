This plugin is for player crypto wallet functionality, the end goal is to have a easily configurable plugin to allow players to recieve, withdraw, and tip ingame.
this is a 100% free to use plugin and has no additional fees added to it. use at your own risk.

How to run (for testing and development)
=========================================

to rename easy use the renameScript.sh
open and Change the "CHANGEME"s accordingly

1. run make
--------------

2. create variables.env and write your configuration
-----------------------------------------------------
An example configuration would be:

```
spigot:
  container_name: cryptoplugin
  build: .
  ports:
    - "25565:25565"
  env_file:
    - variables.env
  environment:
    - ADMIN_UUID=bceeaefc-9590-4233-a858-d3eb933121ec
    - ADDRESS_URL=https://blockchair.com/dogecoin/address/
    - TX_URL=https://blockchair.com/dogecoin/transaction/
    - NODE_HOST=192.168.x.x
    - NODE_PORT=22555
    - NODE_USERNAME=ABC123
    - NODE_PASSWORD=ABC123
    - COINGECKO_CRYPTO=dogecoin
    - CRYPTO_TICKER=DOGE
    - DISPLAY_DECIMALS=4
    - DENOMINATION_NAME=Shibe
  links:
    - redis
redis:
  image: redis
```

3. run docker-compose up
--------------------------

port to docker
----------------------
sudo iptables -t nat -L -n

sudo iptables -t nat -A POSTROUTING --source 172.17.0.3 --destination 172.17.0.3 -p tcp --dport 25565 -j MASQUERADE

----------------------
to be added to the HUB with other servers enable Bungee and give the HUB operator the IP:PORT and Name to display
Configuring your Spigot servers for BungeeCordPermalink

    On your Spigot servers, navigate to the Spigot directory and open spigot.yml.

    Change bungeecord: false to bungeecord: true. Save and exit.

    Open server.properties.

    Change online-mode=true to online-mode=false. Save and exit.

    Restart the Spigot servers.

How to run without docker

=========================================
1. Download latest spigot jar
2. Start server with ```java -jar latest.jar```
    Restart the Spigot servers.
3. Copy CryptoPlugin.jar to plugin folder
4. Set EULA.txt to true
5. Edit and run setenv.sh script
6. Make sure you have Bitcoin and Redis server running.
7. Edit server.properties to point to your local IP
8. Start server with ```java -jar latest.jar```

![](https://media.discordapp.net/attachments/803851819750129685/840074868111179786/unknown.png?width=802&height=451)
