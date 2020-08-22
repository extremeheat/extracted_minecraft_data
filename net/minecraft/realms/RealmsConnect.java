package net.minecraft.realms;

import java.net.InetAddress;
import java.net.UnknownHostException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsConnect {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RealmsScreen onlineScreen;
   private volatile boolean aborted;
   private Connection connection;

   public RealmsConnect(RealmsScreen var1) {
      this.onlineScreen = var1;
   }

   public void connect(final String var1, final int var2) {
      Realms.setConnectedToRealms(true);
      Realms.narrateNow(Realms.getLocalizedString("mco.connect.success"));
      (new Thread("Realms-connect-task") {
         public void run() {
            InetAddress var1x = null;

            try {
               var1x = InetAddress.getByName(var1);
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.this.connection = Connection.connectToServer(var1x, var2, Minecraft.getInstance().options.useNativeTransport());
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.this.connection.setListener(new ClientHandshakePacketListenerImpl(RealmsConnect.this.connection, Minecraft.getInstance(), RealmsConnect.this.onlineScreen.getProxy(), (var0) -> {
               }));
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.this.connection.send(new ClientIntentionPacket(var1, var2, ConnectionProtocol.LOGIN));
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.this.connection.send(new ServerboundHelloPacket(Minecraft.getInstance().getUser().getGameProfile()));
            } catch (UnknownHostException var5) {
               Realms.clearResourcePack();
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.LOGGER.error("Couldn't connect to world", var5);
               Realms.setScreen(new DisconnectedRealmsScreen(RealmsConnect.this.onlineScreen, "connect.failed", new TranslatableComponent("disconnect.genericReason", new Object[]{"Unknown host '" + var1 + "'"})));
            } catch (Exception var6) {
               Realms.clearResourcePack();
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.LOGGER.error("Couldn't connect to world", var6);
               String var3 = var6.toString();
               if (var1x != null) {
                  String var4 = var1x + ":" + var2;
                  var3 = var3.replaceAll(var4, "");
               }

               Realms.setScreen(new DisconnectedRealmsScreen(RealmsConnect.this.onlineScreen, "connect.failed", new TranslatableComponent("disconnect.genericReason", new Object[]{var3})));
            }

         }
      }).start();
   }

   public void abort() {
      this.aborted = true;
      if (this.connection != null && this.connection.isConnected()) {
         this.connection.disconnect(new TranslatableComponent("disconnect.genericReason", new Object[0]));
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
