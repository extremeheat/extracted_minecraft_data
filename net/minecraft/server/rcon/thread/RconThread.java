package net.minecraft.server.rcon.thread;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.server.ServerInterface;
import net.minecraft.server.dedicated.DedicatedServerProperties;

public class RconThread extends GenericThread {
   private final int port;
   private String serverIp;
   private ServerSocket socket;
   private final String rconPassword;
   private Map clients;

   public RconThread(ServerInterface var1) {
      super(var1, "RCON Listener");
      DedicatedServerProperties var2 = var1.getProperties();
      this.port = var2.rconPort;
      this.rconPassword = var2.rconPassword;
      this.serverIp = var1.getServerIp();
      if (this.serverIp.isEmpty()) {
         this.serverIp = "0.0.0.0";
      }

      this.initClients();
      this.socket = null;
   }

   private void initClients() {
      this.clients = Maps.newHashMap();
   }

   private void clearClients() {
      Iterator var1 = this.clients.entrySet().iterator();

      while(var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         if (!((RconClient)var2.getValue()).isRunning()) {
            var1.remove();
         }
      }

   }

   public void run() {
      this.info("RCON running on " + this.serverIp + ":" + this.port);

      try {
         while(this.running) {
            try {
               Socket var1 = this.socket.accept();
               var1.setSoTimeout(500);
               RconClient var2 = new RconClient(this.serverInterface, this.rconPassword, var1);
               var2.start();
               this.clients.put(var1.getRemoteSocketAddress(), var2);
               this.clearClients();
            } catch (SocketTimeoutException var7) {
               this.clearClients();
            } catch (IOException var8) {
               if (this.running) {
                  this.info("IO: " + var8.getMessage());
               }
            }
         }
      } finally {
         this.closeSocket(this.socket);
      }

   }

   public void start() {
      if (this.rconPassword.isEmpty()) {
         this.warn("No rcon password set in server.properties, rcon disabled!");
      } else if (0 < this.port && 65535 >= this.port) {
         if (!this.running) {
            try {
               this.socket = new ServerSocket(this.port, 0, InetAddress.getByName(this.serverIp));
               this.socket.setSoTimeout(500);
               super.start();
            } catch (IOException var2) {
               this.warn("Unable to initialise rcon on " + this.serverIp + ":" + this.port + " : " + var2.getMessage());
            }

         }
      } else {
         this.warn("Invalid rcon port " + this.port + " found in server.properties, rcon disabled!");
      }
   }

   public void stop() {
      super.stop();
      Iterator var1 = this.clients.entrySet().iterator();

      while(var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         ((RconClient)var2.getValue()).stop();
      }

      this.closeSocket(this.socket);
      this.initClients();
   }
}
