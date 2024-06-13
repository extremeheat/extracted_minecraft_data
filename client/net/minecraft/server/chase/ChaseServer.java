package net.minecraft.server.chase;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ClosedByInterruptException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.server.commands.ChaseCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class ChaseServer {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final String serverBindAddress;
   private final int serverPort;
   private final PlayerList playerList;
   private final int broadcastIntervalMs;
   private volatile boolean wantsToRun;
   @Nullable
   private ServerSocket serverSocket;
   private final CopyOnWriteArrayList<Socket> clientSockets = new CopyOnWriteArrayList<>();

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

      while (this.wantsToRun) {
         if (!this.clientSockets.isEmpty()) {
            ChaseServer.PlayerPosition var2 = this.getPlayerPosition();
            if (var2 != null && !var2.equals(var1)) {
               var1 = var2;
               byte[] var3 = var2.format().getBytes(StandardCharsets.US_ASCII);

               for (Socket var5 : this.clientSockets) {
                  if (!var5.isClosed()) {
                     Util.ioPool().submit(() -> {
                        try {
                           OutputStream var2x = var5.getOutputStream();
                           var2x.write(var3);
                           var2x.flush();
                        } catch (IOException var3x) {
                           LOGGER.info("Remote control client socket got an IO exception and will be closed", var3x);
                           IOUtils.closeQuietly(var5);
                        }
                     });
                  }
               }
            }

            List var7 = this.clientSockets.stream().filter(Socket::isClosed).collect(Collectors.toList());
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
      try {
         while (this.wantsToRun) {
            if (this.serverSocket != null) {
               LOGGER.info("Remote control server is listening for connections on port {}", this.serverPort);
               Socket var1 = this.serverSocket.accept();
               LOGGER.info("Remote control server received client connection on port {}", var1.getPort());
               this.clientSockets.add(var1);
            }
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
   }

   @Nullable
   private ChaseServer.PlayerPosition getPlayerPosition() {
      List var1 = this.playerList.getPlayers();
      if (var1.isEmpty()) {
         return null;
      } else {
         ServerPlayer var2 = (ServerPlayer)var1.get(0);
         String var3 = (String)ChaseCommand.DIMENSION_NAMES.inverse().get(var2.level().dimension());
         return var3 == null ? null : new ChaseServer.PlayerPosition(var3, var2.getX(), var2.getY(), var2.getZ(), var2.getYRot(), var2.getXRot());
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
