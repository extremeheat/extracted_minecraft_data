package net.minecraft.server.rcon.thread;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.ServerInterface;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import org.slf4j.Logger;

public class RconThread extends GenericThread {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ServerSocket socket;
   private final String rconPassword;
   private final List<RconClient> clients = Lists.newArrayList();
   private final ServerInterface serverInterface;

   private RconThread(ServerInterface var1, ServerSocket var2, String var3) {
      super("RCON Listener");
      this.serverInterface = var1;
      this.socket = var2;
      this.rconPassword = var3;
   }

   private void clearClients() {
      this.clients.removeIf((var0) -> {
         return !var0.isRunning();
      });
   }

   public void run() {
      try {
         while(this.running) {
            try {
               Socket var1 = this.socket.accept();
               RconClient var2 = new RconClient(this.serverInterface, this.rconPassword, var1);
               var2.start();
               this.clients.add(var2);
               this.clearClients();
            } catch (SocketTimeoutException var7) {
               this.clearClients();
            } catch (IOException var8) {
               if (this.running) {
                  LOGGER.info("IO exception: ", var8);
               }
            }
         }
      } finally {
         this.closeSocket(this.socket);
      }

   }

   @Nullable
   public static RconThread create(ServerInterface var0) {
      DedicatedServerProperties var1 = var0.getProperties();
      String var2 = var0.getServerIp();
      if (var2.isEmpty()) {
         var2 = "0.0.0.0";
      }

      int var3 = var1.rconPort;
      if (0 < var3 && 65535 >= var3) {
         String var4 = var1.rconPassword;
         if (var4.isEmpty()) {
            LOGGER.warn("No rcon password set in server.properties, rcon disabled!");
            return null;
         } else {
            try {
               ServerSocket var5 = new ServerSocket(var3, 0, InetAddress.getByName(var2));
               var5.setSoTimeout(500);
               RconThread var6 = new RconThread(var0, var5, var4);
               if (!var6.start()) {
                  return null;
               } else {
                  LOGGER.info("RCON running on {}:{}", var2, var3);
                  return var6;
               }
            } catch (IOException var7) {
               LOGGER.warn("Unable to initialise RCON on {}:{}", new Object[]{var2, var3, var7});
               return null;
            }
         }
      } else {
         LOGGER.warn("Invalid rcon port {} found in server.properties, rcon disabled!", var3);
         return null;
      }
   }

   public void stop() {
      this.running = false;
      this.closeSocket(this.socket);
      super.stop();
      Iterator var1 = this.clients.iterator();

      while(var1.hasNext()) {
         RconClient var2 = (RconClient)var1.next();
         if (var2.isRunning()) {
            var2.stop();
         }
      }

      this.clients.clear();
   }

   private void closeSocket(ServerSocket var1) {
      LOGGER.debug("closeSocket: {}", var1);

      try {
         var1.close();
      } catch (IOException var3) {
         LOGGER.warn("Failed to close socket", var3);
      }

   }
}
