package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerNameResolver;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import org.slf4j.Logger;

public class ConnectScreen extends Screen {
   private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
   static final Logger LOGGER = LogUtils.getLogger();
   private static final long NARRATION_DELAY_MS = 2000L;
   public static final Component UNKNOWN_HOST_MESSAGE = Component.translatable("disconnect.genericReason", Component.translatable("disconnect.unknownHost"));
   @Nullable
   volatile Connection connection;
   volatile boolean aborted;
   final Screen parent;
   private Component status = Component.translatable("connect.connecting");
   private long lastNarration = -1L;

   private ConnectScreen(Screen var1) {
      super(GameNarrator.NO_TITLE);
      this.parent = var1;
   }

   public static void startConnecting(Screen var0, Minecraft var1, ServerAddress var2, @Nullable ServerData var3) {
      ConnectScreen var4 = new ConnectScreen(var0);
      var1.clearLevel();
      var1.prepareForMultiplayer();
      var1.setCurrentServer(var3);
      var1.setScreen(var4);
      var4.connect(var1, var2);
   }

   private void connect(final Minecraft var1, final ServerAddress var2) {
      LOGGER.info("Connecting to {}, {}", var2.getHost(), var2.getPort());
      Thread var3 = new Thread("Server Connector #" + UNIQUE_THREAD_ID.incrementAndGet()) {
         @Override
         public void run() {
            InetSocketAddress var1x = null;

            try {
               if (ConnectScreen.this.aborted) {
                  return;
               }

               Optional var2x = ServerNameResolver.DEFAULT.resolveAddress(var2).map(ResolvedServerAddress::asInetSocketAddress);
               if (ConnectScreen.this.aborted) {
                  return;
               }

               if (!var2x.isPresent()) {
                  var1.execute(
                     () -> var1.setScreen(
                           new DisconnectedScreen(ConnectScreen.this.parent, CommonComponents.CONNECT_FAILED, ConnectScreen.UNKNOWN_HOST_MESSAGE)
                        )
                  );
                  return;
               }

               var1x = (InetSocketAddress)var2x.get();
               ConnectScreen.this.connection = Connection.connectToServer(var1x, var1.options.useNativeTransport());
               ConnectScreen.this.connection
                  .setListener(
                     new ClientHandshakePacketListenerImpl(ConnectScreen.this.connection, var1, ConnectScreen.this.parent, ConnectScreen.this::updateStatus)
                  );
               ConnectScreen.this.connection.send(new ClientIntentionPacket(var1x.getHostName(), var1x.getPort(), ConnectionProtocol.LOGIN));
               ConnectScreen.this.connection
                  .send(
                     new ServerboundHelloPacket(
                        var1.getUser().getName(), var1.getProfileKeyPairManager().profilePublicKeyData(), Optional.ofNullable(var1.getUser().getProfileId())
                     )
                  );
            } catch (Exception var6) {
               if (ConnectScreen.this.aborted) {
                  return;
               }

               Throwable var5 = var6.getCause();
               Exception var3;
               if (var5 instanceof Exception var4) {
                  var3 = (Exception)var4;
               } else {
                  var3 = var6;
               }

               ConnectScreen.LOGGER.error("Couldn't connect to server", var6);
               String var7 = var1x == null
                  ? var3.getMessage()
                  : var3.getMessage().replaceAll(var1x.getHostName() + ":" + var1x.getPort(), "").replaceAll(var1x.toString(), "");
               var1.execute(
                  () -> var1.setScreen(
                        new DisconnectedScreen(
                           ConnectScreen.this.parent, CommonComponents.CONNECT_FAILED, Component.translatable("disconnect.genericReason", var7)
                        )
                     )
               );
            }
         }
      };
      var3.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      var3.start();
   }

   private void updateStatus(Component var1) {
      this.status = var1;
   }

   @Override
   public void tick() {
      if (this.connection != null) {
         if (this.connection.isConnected()) {
            this.connection.tick();
         } else {
            this.connection.handleDisconnection();
         }
      }
   }

   @Override
   public boolean shouldCloseOnEsc() {
      return false;
   }

   @Override
   protected void init() {
      this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, CommonComponents.GUI_CANCEL, var1 -> {
         this.aborted = true;
         if (this.connection != null) {
            this.connection.disconnect(Component.translatable("connect.aborted"));
         }

         this.minecraft.setScreen(this.parent);
      }));
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      long var5 = Util.getMillis();
      if (var5 - this.lastNarration > 2000L) {
         this.lastNarration = var5;
         this.minecraft.getNarrator().sayNow(Component.translatable("narrator.joining"));
      }

      drawCenteredString(var1, this.font, this.status, this.width / 2, this.height / 2 - 50, 16777215);
      super.render(var1, var2, var3, var4);
   }
}
