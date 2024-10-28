package net.minecraft.realms;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.RealmsServer;
import java.net.InetSocketAddress;
import java.time.Duration;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.TransferState;
import net.minecraft.client.multiplayer.chat.report.ReportEnvironment;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.quickplay.QuickPlayLog;
import net.minecraft.client.resources.server.ServerPackManager;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import org.slf4j.Logger;

public class RealmsConnect {
   static final Logger LOGGER = LogUtils.getLogger();
   final Screen onlineScreen;
   volatile boolean aborted;
   @Nullable
   Connection connection;

   public RealmsConnect(Screen var1) {
      super();
      this.onlineScreen = var1;
   }

   public void connect(final RealmsServer var1, ServerAddress var2) {
      final Minecraft var3 = Minecraft.getInstance();
      var3.prepareForMultiplayer();
      var3.getNarrator().sayNow((Component)Component.translatable("mco.connect.success"));
      final String var4 = var2.getHost();
      final int var5 = var2.getPort();
      (new Thread("Realms-connect-task") {
         public void run() {
            InetSocketAddress var1x = null;

            try {
               var1x = new InetSocketAddress(var4, var5);
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.this.connection = Connection.connectToServer(var1x, var3.options.useNativeTransport(), var3.getDebugOverlay().getBandwidthLogger());
               if (RealmsConnect.this.aborted) {
                  return;
               }

               ClientHandshakePacketListenerImpl var2 = new ClientHandshakePacketListenerImpl(RealmsConnect.this.connection, var3, var1.toServerData(var4), RealmsConnect.this.onlineScreen, false, (Duration)null, (var0) -> {
               }, (TransferState)null);
               if (var1.isMinigameActive()) {
                  var2.setMinigameName(var1.minigameName);
               }

               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.this.connection.initiateServerboundPlayConnection(var4, var5, var2);
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.this.connection.send(new ServerboundHelloPacket(var3.getUser().getName(), var3.getUser().getProfileId()));
               var3.updateReportEnvironment(ReportEnvironment.realm(var1));
               var3.quickPlayLog().setWorldData(QuickPlayLog.Type.REALMS, String.valueOf(var1.id), var1.name);
               var3.getDownloadedPackSource().configureForServerControl(RealmsConnect.this.connection, ServerPackManager.PackPromptStatus.ALLOWED);
            } catch (Exception var5x) {
               var3.getDownloadedPackSource().cleanupAfterDisconnect();
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.LOGGER.error("Couldn't connect to world", var5x);
               String var3x = var5x.toString();
               if (var1x != null) {
                  String var10000 = String.valueOf(var1x);
                  String var4x = var10000 + ":" + var5;
                  var3x = var3x.replaceAll(var4x, "");
               }

               DisconnectedRealmsScreen var6 = new DisconnectedRealmsScreen(RealmsConnect.this.onlineScreen, CommonComponents.CONNECT_FAILED, Component.translatable("disconnect.genericReason", var3x));
               var3.execute(() -> {
                  var3.setScreen(var6);
               });
            }

         }
      }).start();
   }

   public void abort() {
      this.aborted = true;
      if (this.connection != null && this.connection.isConnected()) {
         this.connection.disconnect((Component)Component.translatable("disconnect.genericReason"));
         this.connection.handleDisconnection();
      }

   }

   public void tick() {
      if (this.connection != null) {
         if (this.connection.isConnected()) {
            this.connection.tick();
         } else {
            this.connection.handleDisconnection();
         }
      }

   }
}
