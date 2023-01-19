package net.minecraft.client.server;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.DefaultUncaughtExceptionHandler;
import org.slf4j.Logger;

public class LanServerDetection {
   static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
   static final Logger LOGGER = LogUtils.getLogger();

   public LanServerDetection() {
      super();
   }

   public static class LanServerDetector extends Thread {
      private final LanServerDetection.LanServerList serverList;
      private final InetAddress pingGroup;
      private final MulticastSocket socket;

      public LanServerDetector(LanServerDetection.LanServerList var1) throws IOException {
         super("LanServerDetector #" + LanServerDetection.UNIQUE_THREAD_ID.incrementAndGet());
         this.serverList = var1;
         this.setDaemon(true);
         this.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LanServerDetection.LOGGER));
         this.socket = new MulticastSocket(4445);
         this.pingGroup = InetAddress.getByName("224.0.2.60");
         this.socket.setSoTimeout(5000);
         this.socket.joinGroup(this.pingGroup);
      }

      @Override
      public void run() {
         byte[] var2 = new byte[1024];

         while(!this.isInterrupted()) {
            DatagramPacket var1 = new DatagramPacket(var2, var2.length);

            try {
               this.socket.receive(var1);
            } catch (SocketTimeoutException var5) {
               continue;
            } catch (IOException var6) {
               LanServerDetection.LOGGER.error("Couldn't ping server", var6);
               break;
            }

            String var3 = new String(var1.getData(), var1.getOffset(), var1.getLength(), StandardCharsets.UTF_8);
            LanServerDetection.LOGGER.debug("{}: {}", var1.getAddress(), var3);
            this.serverList.addServer(var3, var1.getAddress());
         }

         try {
            this.socket.leaveGroup(this.pingGroup);
         } catch (IOException var4) {
         }

         this.socket.close();
      }
   }

   public static class LanServerList {
      private final List<LanServer> servers = Lists.newArrayList();
      private boolean isDirty;

      public LanServerList() {
         super();
      }

      @Nullable
      public synchronized List<LanServer> takeDirtyServers() {
         if (this.isDirty) {
            List var1 = List.copyOf(this.servers);
            this.isDirty = false;
            return var1;
         } else {
            return null;
         }
      }

      public synchronized void addServer(String var1, InetAddress var2) {
         String var3 = LanServerPinger.parseMotd(var1);
         String var4 = LanServerPinger.parseAddress(var1);
         if (var4 != null) {
            var4 = var2.getHostAddress() + ":" + var4;
            boolean var5 = false;

            for(LanServer var7 : this.servers) {
               if (var7.getAddress().equals(var4)) {
                  var7.updatePingTime();
                  var5 = true;
                  break;
               }
            }

            if (!var5) {
               this.servers.add(new LanServer(var3, var4));
               this.isDirty = true;
            }
         }
      }
   }
}
