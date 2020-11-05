package net.minecraft.realms;

import com.mojang.realmsclient.dto.RealmsServer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsConnect {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Screen onlineScreen;
   private volatile boolean aborted;
   private Connection connection;

   public RealmsConnect(Screen var1) {
      super();
      this.onlineScreen = var1;
   }

   public void connect(final RealmsServer var1, final String var2, final int var3) {
      final Minecraft var4 = Minecraft.getInstance();
      var4.setConnectedToRealms(true);
      NarrationHelper.now(I18n.get("mco.connect.success"));
      (new Thread("Realms-connect-task") {
         public void run() {
            InetAddress var1x = null;

            try {
               var1x = InetAddress.getByName(var2);
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.this.connection = Connection.connectToServer(var1x, var3, var4.options.useNativeTransport());
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.this.connection.setListener(new ClientHandshakePacketListenerImpl(RealmsConnect.this.connection, var4, RealmsConnect.this.onlineScreen, (var0) -> {
               }));
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.this.connection.send(new ClientIntentionPacket(var2, var3, ConnectionProtocol.LOGIN));
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.this.connection.send(new ServerboundHelloPacket(var4.getUser().getGameProfile()));
               var4.setCurrentServer(var1.toServerData(var2));
            } catch (UnknownHostException var5) {
               var4.getClientPackSource().clearServerPack();
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.LOGGER.error("Couldn't connect to world", var5);
               DisconnectedRealmsScreen var7 = new DisconnectedRealmsScreen(RealmsConnect.this.onlineScreen, CommonComponents.CONNECT_FAILED, new TranslatableComponent("disconnect.genericReason", new Object[]{"Unknown host '" + var2 + "'"}));
               var4.execute(() -> {
                  var4.setScreen(var7);
               });
            } catch (Exception var6) {
               var4.getClientPackSource().clearServerPack();
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.LOGGER.error("Couldn't connect to world", var6);
               String var3x = var6.toString();
               if (var1x != null) {
                  String var4x = var1x + ":" + var3;
                  var3x = var3x.replaceAll(var4x, "");
               }

               DisconnectedRealmsScreen var8 = new DisconnectedRealmsScreen(RealmsConnect.this.onlineScreen, CommonComponents.CONNECT_FAILED, new TranslatableComponent("disconnect.genericReason", new Object[]{var3x}));
               var4.execute(() -> {
                  var4.setScreen(var8);
               });
            }

         }
      }).start();
   }

   public void abort() {
      this.aborted = true;
      if (this.connection != null && this.connection.isConnected()) {
         this.connection.disconnect(new TranslatableComponent("disconnect.genericReason"));
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
