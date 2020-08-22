package net.minecraft.client.gui.screens;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConnectScreen extends Screen {
   private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
   private static final Logger LOGGER = LogManager.getLogger();
   private Connection connection;
   private boolean aborted;
   private final Screen parent;
   private Component status = new TranslatableComponent("connect.connecting", new Object[0]);
   private long lastNarration = -1L;

   public ConnectScreen(Screen var1, Minecraft var2, ServerData var3) {
      super(NarratorChatListener.NO_TITLE);
      this.minecraft = var2;
      this.parent = var1;
      ServerAddress var4 = ServerAddress.parseString(var3.ip);
      var2.clearLevel();
      var2.setCurrentServer(var3);
      this.connect(var4.getHost(), var4.getPort());
   }

   public ConnectScreen(Screen var1, Minecraft var2, String var3, int var4) {
      super(NarratorChatListener.NO_TITLE);
      this.minecraft = var2;
      this.parent = var1;
      var2.clearLevel();
      this.connect(var3, var4);
   }

   private void connect(final String var1, final int var2) {
      LOGGER.info("Connecting to {}, {}", var1, var2);
      Thread var3 = new Thread("Server Connector #" + UNIQUE_THREAD_ID.incrementAndGet()) {
         public void run() {
            InetAddress var1x = null;

            try {
               if (ConnectScreen.this.aborted) {
                  return;
               }

               var1x = InetAddress.getByName(var1);
               ConnectScreen.this.connection = Connection.connectToServer(var1x, var2, ConnectScreen.this.minecraft.options.useNativeTransport());
               ConnectScreen.this.connection.setListener(new ClientHandshakePacketListenerImpl(ConnectScreen.this.connection, ConnectScreen.this.minecraft, ConnectScreen.this.parent, (var1xx) -> {
                  ConnectScreen.this.updateStatus(var1xx);
               }));
               ConnectScreen.this.connection.send(new ClientIntentionPacket(var1, var2, ConnectionProtocol.LOGIN));
               ConnectScreen.this.connection.send(new ServerboundHelloPacket(ConnectScreen.this.minecraft.getUser().getGameProfile()));
            } catch (UnknownHostException var4) {
               if (ConnectScreen.this.aborted) {
                  return;
               }

               ConnectScreen.LOGGER.error("Couldn't connect to server", var4);
               ConnectScreen.this.minecraft.execute(() -> {
                  ConnectScreen.this.minecraft.setScreen(new DisconnectedScreen(ConnectScreen.this.parent, "connect.failed", new TranslatableComponent("disconnect.genericReason", new Object[]{"Unknown host"})));
               });
            } catch (Exception var5) {
               if (ConnectScreen.this.aborted) {
                  return;
               }

               ConnectScreen.LOGGER.error("Couldn't connect to server", var5);
               String var3 = var1x == null ? var5.toString() : var5.toString().replaceAll(var1x + ":" + var2, "");
               ConnectScreen.this.minecraft.execute(() -> {
                  ConnectScreen.this.minecraft.setScreen(new DisconnectedScreen(ConnectScreen.this.parent, "connect.failed", new TranslatableComponent("disconnect.genericReason", new Object[]{var3})));
               });
            }

         }
      };
      var3.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      var3.start();
   }

   private void updateStatus(Component var1) {
      this.status = var1;
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

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected void init() {
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, I18n.get("gui.cancel"), (var1) -> {
         this.aborted = true;
         if (this.connection != null) {
            this.connection.disconnect(new TranslatableComponent("connect.aborted", new Object[0]));
         }

         this.minecraft.setScreen(this.parent);
      }));
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      long var4 = Util.getMillis();
      if (var4 - this.lastNarration > 2000L) {
         this.lastNarration = var4;
         NarratorChatListener.INSTANCE.sayNow((new TranslatableComponent("narrator.joining", new Object[0])).getString());
      }

      this.drawCenteredString(this.font, this.status.getColoredString(), this.width / 2, this.height / 2 - 50, 16777215);
      super.render(var1, var2, var3);
   }
}
