package net.minecraft.client.network;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.multiplayer.ThreadLanServerPing;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LanServerDetector {
   private static final AtomicInteger field_148551_a = new AtomicInteger(0);
   private static final Logger field_148550_b = LogManager.getLogger();

   public static class ThreadLanServerFind extends Thread {
      private final LanServerDetector.LanServerList field_77500_a;
      private final InetAddress field_77498_b;
      private final MulticastSocket field_77499_c;

      public ThreadLanServerFind(LanServerDetector.LanServerList var1) throws IOException {
         super("LanServerDetector #" + LanServerDetector.field_148551_a.incrementAndGet());
         this.field_77500_a = var1;
         this.setDaemon(true);
         this.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LanServerDetector.field_148550_b));
         this.field_77499_c = new MulticastSocket(4445);
         this.field_77498_b = InetAddress.getByName("224.0.2.60");
         this.field_77499_c.setSoTimeout(5000);
         this.field_77499_c.joinGroup(this.field_77498_b);
      }

      public void run() {
         byte[] var2 = new byte[1024];

         while(!this.isInterrupted()) {
            DatagramPacket var1 = new DatagramPacket(var2, var2.length);

            try {
               this.field_77499_c.receive(var1);
            } catch (SocketTimeoutException var5) {
               continue;
            } catch (IOException var6) {
               LanServerDetector.field_148550_b.error("Couldn't ping server", var6);
               break;
            }

            String var3 = new String(var1.getData(), var1.getOffset(), var1.getLength(), StandardCharsets.UTF_8);
            LanServerDetector.field_148550_b.debug("{}: {}", var1.getAddress(), var3);
            this.field_77500_a.func_77551_a(var3, var1.getAddress());
         }

         try {
            this.field_77499_c.leaveGroup(this.field_77498_b);
         } catch (IOException var4) {
         }

         this.field_77499_c.close();
      }
   }

   public static class LanServerList {
      private final List<LanServerInfo> field_77555_b = Lists.newArrayList();
      private boolean field_77556_a;

      public LanServerList() {
         super();
      }

      public synchronized boolean func_77553_a() {
         return this.field_77556_a;
      }

      public synchronized void func_77552_b() {
         this.field_77556_a = false;
      }

      public synchronized List<LanServerInfo> func_77554_c() {
         return Collections.unmodifiableList(this.field_77555_b);
      }

      public synchronized void func_77551_a(String var1, InetAddress var2) {
         String var3 = ThreadLanServerPing.func_77524_a(var1);
         String var4 = ThreadLanServerPing.func_77523_b(var1);
         if (var4 != null) {
            var4 = var2.getHostAddress() + ":" + var4;
            boolean var5 = false;
            Iterator var6 = this.field_77555_b.iterator();

            while(var6.hasNext()) {
               LanServerInfo var7 = (LanServerInfo)var6.next();
               if (var7.func_77488_b().equals(var4)) {
                  var7.func_77489_c();
                  var5 = true;
                  break;
               }
            }

            if (!var5) {
               this.field_77555_b.add(new LanServerInfo(var3, var4));
               this.field_77556_a = true;
            }

         }
      }
   }
}
