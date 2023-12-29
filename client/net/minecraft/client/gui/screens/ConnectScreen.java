package net.minecraft.client.gui.screens;

import com.mojang.logging.LogUtils;
import io.netty.channel.ChannelFuture;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.chat.report.ReportEnvironment;
import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerNameResolver;
import net.minecraft.client.quickplay.QuickPlay;
import net.minecraft.client.quickplay.QuickPlayLog;
import net.minecraft.client.resources.server.ServerPackManager;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import org.slf4j.Logger;

public class ConnectScreen extends Screen {
   private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
   static final Logger LOGGER = LogUtils.getLogger();
   private static final long NARRATION_DELAY_MS = 2000L;
   public static final Component ABORT_CONNECTION = Component.translatable("connect.aborted");
   public static final Component UNKNOWN_HOST_MESSAGE = Component.translatable("disconnect.genericReason", Component.translatable("disconnect.unknownHost"));
   @Nullable
   volatile Connection connection;
   @Nullable
   ChannelFuture channelFuture;
   volatile boolean aborted;
   final Screen parent;
   private Component status = Component.translatable("connect.connecting");
   private long lastNarration = -1L;
   final Component connectFailedTitle;

   private ConnectScreen(Screen var1, Component var2) {
      super(GameNarrator.NO_TITLE);
      this.parent = var1;
      this.connectFailedTitle = var2;
   }

   public static void startConnecting(Screen var0, Minecraft var1, ServerAddress var2, ServerData var3, boolean var4) {
      if (var1.screen instanceof ConnectScreen) {
         LOGGER.error("Attempt to connect while already connecting");
      } else {
         ConnectScreen var5 = new ConnectScreen(var0, var4 ? QuickPlay.ERROR_TITLE : CommonComponents.CONNECT_FAILED);
         var1.disconnect();
         var1.prepareForMultiplayer();
         var1.updateReportEnvironment(ReportEnvironment.thirdParty(var3 != null ? var3.ip : var2.getHost()));
         var1.quickPlayLog().setWorldData(QuickPlayLog.Type.MULTIPLAYER, var3.ip, var3.name);
         var1.setScreen(var5);
         var5.connect(var1, var2, var3);
      }
   }

   private void connect(final Minecraft var1, final ServerAddress var2, @Nullable final ServerData var3) {
      LOGGER.info("Connecting to {}, {}", var2.getHost(), var2.getPort());
      Thread var4 = new Thread("Server Connector #" + UNIQUE_THREAD_ID.incrementAndGet()) {
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

               if (var2x.isEmpty()) {
                  var1.execute(
                     () -> var1.setScreen(
                           new DisconnectedScreen(ConnectScreen.this.parent, ConnectScreen.this.connectFailedTitle, ConnectScreen.UNKNOWN_HOST_MESSAGE)
                        )
                  );
                  return;
               }

               var1x = (InetSocketAddress)var2x.get();
               Connection var10;
               synchronized(ConnectScreen.this) {
                  if (ConnectScreen.this.aborted) {
                     return;
                  }

                  var10 = new Connection(PacketFlow.CLIENTBOUND);
                  var10.setBandwidthLogger(var1.getDebugOverlay().getBandwidthLogger());
                  ConnectScreen.this.channelFuture = Connection.connect(var1x, var1.options.useNativeTransport(), var10);
               }

               ConnectScreen.this.channelFuture.syncUninterruptibly();
               synchronized(ConnectScreen.this) {
                  if (ConnectScreen.this.aborted) {
                     var10.disconnect(ConnectScreen.ABORT_CONNECTION);
                     return;
                  }

                  ConnectScreen.this.connection = var10;
                  var1.getDownloadedPackSource()
                     .configureForServerControl(
                        var10, var3 != null ? convertPackStatus(var3.getResourcePackStatus()) : ServerPackManager.PackPromptStatus.PENDING
                     );
               }

               ConnectScreen.this.connection
                  .initiateServerboundPlayConnection(
                     var1x.getHostName(),
                     var1x.getPort(),
                     new ClientHandshakePacketListenerImpl(
                        ConnectScreen.this.connection, var1, var3, ConnectScreen.this.parent, false, null, ConnectScreen.this::updateStatus
                     )
                  );
               ConnectScreen.this.connection.send(new ServerboundHelloPacket(var1.getUser().getName(), var1.getUser().getProfileId()));
            } catch (Exception var9) {
               if (ConnectScreen.this.aborted) {
                  return;
               }

               Throwable var5 = var9.getCause();
               Exception var3x;
               if (var5 instanceof Exception var4) {
                  var3x = (Exception)var4;
               } else {
                  var3x = var9;
               }

               ConnectScreen.LOGGER.error("Couldn't connect to server", var9);
               String var11 = var1x == null
                  ? var3x.getMessage()
                  : var3x.getMessage().replaceAll(var1x.getHostName() + ":" + var1x.getPort(), "").replaceAll(var1x.toString(), "");
               var1.execute(
                  () -> var1.setScreen(
                        new DisconnectedScreen(
                           ConnectScreen.this.parent, ConnectScreen.this.connectFailedTitle, Component.translatable("disconnect.genericReason", var11)
                        )
                     )
               );
            }
         }

         private static ServerPackManager.PackPromptStatus convertPackStatus(ServerData.ServerPackStatus var0) {
            return switch(var0) {
               case ENABLED -> ServerPackManager.PackPromptStatus.ALLOWED;
               case DISABLED -> ServerPackManager.PackPromptStatus.DECLINED;
               case PROMPT -> ServerPackManager.PackPromptStatus.PENDING;
            };
         }
      };
      var4.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      var4.start();
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
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, var1 -> {
         synchronized(this) {
            this.aborted = true;
            if (this.channelFuture != null) {
               this.channelFuture.cancel(true);
               this.channelFuture = null;
            }

            if (this.connection != null) {
               this.connection.disconnect(ABORT_CONNECTION);
            }
         }

         this.minecraft.setScreen(this.parent);
      }).bounds(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20).build());
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      long var5 = Util.getMillis();
      if (var5 - this.lastNarration > 2000L) {
         this.lastNarration = var5;
         this.minecraft.getNarrator().sayNow(Component.translatable("narrator.joining"));
      }

      var1.drawCenteredString(this.font, this.status, this.width / 2, this.height / 2 - 50, 16777215);
   }
}
