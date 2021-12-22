package net.minecraft.server.chase;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ClosedByInterruptException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.server.commands.ChaseCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChaseServer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final String serverBindAddress;
   private final int serverPort;
   private final PlayerList playerList;
   private final int broadcastIntervalMs;
   private volatile boolean wantsToRun;
   @Nullable
   private ServerSocket serverSocket;
   private final CopyOnWriteArrayList<Socket> clientSockets = new CopyOnWriteArrayList();

   public ChaseServer(String var1, int var2, PlayerList var3, int var4) {
      super();
      this.serverBindAddress = var1;
      this.serverPort = var2;
      this.playerList = var3;
      this.broadcastIntervalMs = var4;
   }

   public void start() throws IOException {
      if (this.serverSocket != null && !this.serverSocket.isClosed()) {
         LOGGER.warn("Remote control server was asked to start, but it is already running. Will ignore.");
      } else {
         this.wantsToRun = true;
         this.serverSocket = new ServerSocket(this.serverPort, 50, InetAddress.getByName(this.serverBindAddress));
         Thread var1 = new Thread(this::runAcceptor, "chase-server-acceptor");
         var1.setDaemon(true);
         var1.start();
         Thread var2 = new Thread(this::runSender, "chase-server-sender");
         var2.setDaemon(true);
         var2.start();
      }
   }

   private void runSender() {
      ChaseServer.PlayerPosition var1 = null;

      while(this.wantsToRun) {
         if (!this.clientSockets.isEmpty()) {
            ChaseServer.PlayerPosition var2 = this.getPlayerPosition();
            if (var2 != null && !var2.equals(var1)) {
               var1 = var2;
               byte[] var3 = var2.format().getBytes(StandardCharsets.US_ASCII);
               Iterator var4 = this.clientSockets.iterator();

               while(var4.hasNext()) {
                  Socket var5 = (Socket)var4.next();
                  if (!var5.isClosed()) {
                     Util.ioPool().submit(() -> {
                        try {
                           OutputStream var2 = var5.getOutputStream();
                           var2.write(var3);
                           var2.flush();
                        } catch (IOException var3x) {
                           LOGGER.info("Remote control client socket got an IO exception and will be closed", var3x);
                           IOUtils.closeQuietly(var5);
                        }

                     });
                  }
               }
            }

            List var7 = (List)this.clientSockets.stream().filter(Socket::isClosed).collect(Collectors.toList());
            this.clientSockets.removeAll(var7);
         }

         if (this.wantsToRun) {
            try {
               Thread.sleep((long)this.broadcastIntervalMs);
            } catch (InterruptedException var6) {
            }
         }
      }

   }

   public void stop() {
      this.wantsToRun = false;
      IOUtils.closeQuietly(this.serverSocket);
      this.serverSocket = null;
   }

   private void runAcceptor() {
      while(true) {
         try {
            if (this.wantsToRun) {
               if (this.serverSocket != null) {
                  LOGGER.info("Remote control server is listening for connections on port {}", this.serverPort);
                  Socket var1 = this.serverSocket.accept();
                  LOGGER.info("Remote control server received client connection on port {}", var1.getPort());
                  this.clientSockets.add(var1);
               }
               continue;
            }
         } catch (ClosedByInterruptException var6) {
            if (this.wantsToRun) {
               LOGGER.info("Remote control server closed by interrupt");
            }
         } catch (IOException var7) {
            if (this.wantsToRun) {
               LOGGER.error("Remote control server closed because of an IO exception", var7);
            }
         } finally {
            IOUtils.closeQuietly(this.serverSocket);
         }

         LOGGER.info("Remote control server is now stopped");
         this.wantsToRun = false;
         return;
      }
   }

   @Nullable
   private ChaseServer.PlayerPosition getPlayerPosition() {
      List var1 = this.playerList.getPlayers();
      if (var1.isEmpty()) {
         return null;
      } else {
         ServerPlayer var2 = (ServerPlayer)var1.get(0);
         String var3 = (String)ChaseCommand.DIMENSION_NAMES.inverse().get(var2.getLevel().dimension());
         return var3 == null ? null : new ChaseServer.PlayerPosition(var3, var2.getX(), var2.getY(), var2.getZ(), var2.getYRot(), var2.getXRot());
      }
   }

   private static record PlayerPosition(String a, double b, double c, double d, float e, float f) {
      private final String dimensionName;
      // $FF: renamed from: x double
      private final double field_511;
      // $FF: renamed from: y double
      private final double field_512;
      // $FF: renamed from: z double
      private final double field_513;
      private final float yRot;
      private final float xRot;

      PlayerPosition(String var1, double var2, double var4, double var6, float var8, float var9) {
         super();
         this.dimensionName = var1;
         this.field_511 = var2;
         this.field_512 = var4;
         this.field_513 = var6;
         this.yRot = var8;
         this.xRot = var9;
      }

      String format() {
         return String.format(Locale.ROOT, "t %s %.2f %.2f %.2f %.2f %.2f\n", this.dimensionName, this.field_511, this.field_512, this.field_513, this.yRot, this.xRot);
      }

      public String dimensionName() {
         return this.dimensionName;
      }

      // $FF: renamed from: x () double
      public double method_125() {
         return this.field_511;
      }

      // $FF: renamed from: y () double
      public double method_126() {
         return this.field_512;
      }

      // $FF: renamed from: z () double
      public double method_127() {
         return this.field_513;
      }

      public float yRot() {
         return this.yRot;
      }

      public float xRot() {
         return this.xRot;
      }
   }
}
