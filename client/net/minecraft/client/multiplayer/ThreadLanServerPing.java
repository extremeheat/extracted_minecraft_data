package net.minecraft.client.multiplayer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadLanServerPing extends Thread {
   private static final AtomicInteger field_148658_a = new AtomicInteger(0);
   private static final Logger field_148657_b = LogManager.getLogger();
   private final String field_77528_b;
   private final DatagramSocket field_77529_c;
   private boolean field_77526_d = true;
   private final String field_77527_e;

   public ThreadLanServerPing(String var1, String var2) throws IOException {
      super("LanServerPinger #" + field_148658_a.incrementAndGet());
      this.field_77528_b = var1;
      this.field_77527_e = var2;
      this.setDaemon(true);
      this.field_77529_c = new DatagramSocket();
   }

   public void run() {
      String var1 = func_77525_a(this.field_77528_b, this.field_77527_e);
      byte[] var2 = var1.getBytes();

      while(!this.isInterrupted() && this.field_77526_d) {
         try {
            InetAddress var3 = InetAddress.getByName("224.0.2.60");
            DatagramPacket var4 = new DatagramPacket(var2, var2.length, var3, 4445);
            this.field_77529_c.send(var4);
         } catch (IOException var6) {
            field_148657_b.warn("LanServerPinger: " + var6.getMessage());
            break;
         }

         try {
            sleep(1500L);
         } catch (InterruptedException var5) {
         }
      }

   }

   public void interrupt() {
      super.interrupt();
      this.field_77526_d = false;
   }

   public static String func_77525_a(String var0, String var1) {
      return "[MOTD]" + var0 + "[/MOTD][AD]" + var1 + "[/AD]";
   }

   public static String func_77524_a(String var0) {
      int var1 = var0.indexOf("[MOTD]");
      if (var1 < 0) {
         return "missing no";
      } else {
         int var2 = var0.indexOf("[/MOTD]", var1 + "[MOTD]".length());
         return var2 < var1 ? "missing no" : var0.substring(var1 + "[MOTD]".length(), var2);
      }
   }

   public static String func_77523_b(String var0) {
      int var1 = var0.indexOf("[/MOTD]");
      if (var1 < 0) {
         return null;
      } else {
         int var2 = var0.indexOf("[/MOTD]", var1 + "[/MOTD]".length());
         if (var2 >= 0) {
            return null;
         } else {
            int var3 = var0.indexOf("[AD]", var1 + "[/MOTD]".length());
            if (var3 < 0) {
               return null;
            } else {
               int var4 = var0.indexOf("[/AD]", var3 + "[AD]".length());
               return var4 < var3 ? null : var0.substring(var3 + "[AD]".length(), var4);
            }
         }
      }
   }
}
