package net.minecraft.server.rcon.thread;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import net.minecraft.server.ServerInterface;
import net.minecraft.server.rcon.PktUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RconClient extends GenericThread {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int SERVERDATA_AUTH = 3;
   private static final int SERVERDATA_EXECCOMMAND = 2;
   private static final int SERVERDATA_RESPONSE_VALUE = 0;
   private static final int SERVERDATA_AUTH_RESPONSE = 2;
   private static final int SERVERDATA_AUTH_FAILURE = -1;
   private boolean authed;
   private final Socket client;
   private final byte[] buf = new byte[1460];
   private final String rconPassword;
   private final ServerInterface serverInterface;

   RconClient(ServerInterface var1, String var2, Socket var3) {
      super("RCON Client " + var3.getInetAddress());
      this.serverInterface = var1;
      this.client = var3;

      try {
         this.client.setSoTimeout(0);
      } catch (Exception var5) {
         this.running = false;
      }

      this.rconPassword = var2;
   }

   public void run() {
      while(true) {
         try {
            if (this.running) {
               BufferedInputStream var1 = new BufferedInputStream(this.client.getInputStream());
               int var2 = var1.read(this.buf, 0, 1460);
               if (10 > var2) {
                  return;
               }

               byte var3 = 0;
               int var4 = PktUtils.intFromByteArray(this.buf, 0, var2);
               if (var4 == var2 - 4) {
                  int var19 = var3 + 4;
                  int var5 = PktUtils.intFromByteArray(this.buf, var19, var2);
                  var19 += 4;
                  int var6 = PktUtils.intFromByteArray(this.buf, var19);
                  var19 += 4;
                  switch(var6) {
                  case 2:
                     if (this.authed) {
                        String var8 = PktUtils.stringFromByteArray(this.buf, var19, var2);

                        try {
                           this.sendCmdResponse(var5, this.serverInterface.runCommand(var8));
                        } catch (Exception var15) {
                           this.sendCmdResponse(var5, "Error executing: " + var8 + " (" + var15.getMessage() + ")");
                        }
                        continue;
                     }

                     this.sendAuthFailure();
                     continue;
                  case 3:
                     String var7 = PktUtils.stringFromByteArray(this.buf, var19, var2);
                     int var10000 = var19 + var7.length();
                     if (!var7.isEmpty() && var7.equals(this.rconPassword)) {
                        this.authed = true;
                        this.send(var5, 2, "");
                        continue;
                     }

                     this.authed = false;
                     this.sendAuthFailure();
                     continue;
                  default:
                     this.sendCmdResponse(var5, String.format("Unknown request %s", Integer.toHexString(var6)));
                     continue;
                  }
               }

               return;
            }
         } catch (IOException var16) {
         } catch (Exception var17) {
            LOGGER.error("Exception whilst parsing RCON input", var17);
         } finally {
            this.closeSocket();
            LOGGER.info("Thread {} shutting down", this.name);
            this.running = false;
         }

         return;
      }
   }

   private void send(int var1, int var2, String var3) throws IOException {
      ByteArrayOutputStream var4 = new ByteArrayOutputStream(1248);
      DataOutputStream var5 = new DataOutputStream(var4);
      byte[] var6 = var3.getBytes(StandardCharsets.UTF_8);
      var5.writeInt(Integer.reverseBytes(var6.length + 10));
      var5.writeInt(Integer.reverseBytes(var1));
      var5.writeInt(Integer.reverseBytes(var2));
      var5.write(var6);
      var5.write(0);
      var5.write(0);
      this.client.getOutputStream().write(var4.toByteArray());
   }

   private void sendAuthFailure() throws IOException {
      this.send(-1, 2, "");
   }

   private void sendCmdResponse(int var1, String var2) throws IOException {
      int var3 = var2.length();

      do {
         int var4 = 4096 <= var3 ? 4096 : var3;
         this.send(var1, 0, var2.substring(0, var4));
         var2 = var2.substring(var4);
         var3 = var2.length();
      } while(0 != var3);

   }

   public void stop() {
      this.running = false;
      this.closeSocket();
      super.stop();
   }

   private void closeSocket() {
      try {
         this.client.close();
      } catch (IOException var2) {
         LOGGER.warn("Failed to close socket", var2);
      }

   }
}
