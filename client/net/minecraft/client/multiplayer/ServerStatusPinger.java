package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerNameResolver;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket;
import net.minecraft.network.protocol.status.ClientStatusPacketListener;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.network.protocol.status.ServerboundStatusRequestPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.EventLoopGroupHolder;
import net.minecraft.server.players.NameAndId;
import net.minecraft.util.debugchart.LocalSampleLogger;
import org.slf4j.Logger;

public class ServerStatusPinger {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component CANT_CONNECT_MESSAGE = Component.translatable("multiplayer.status.cannot_connect").withColor(-65536);
   private final List<Connection> connections = Collections.synchronizedList(Lists.newArrayList());

   public ServerStatusPinger() {
      super();
   }

   public void pingServer(final ServerData var1, final Runnable var2, final Runnable var3, final EventLoopGroupHolder var4) throws UnknownHostException {
      final ServerAddress var5 = ServerAddress.parseString(var1.ip);
      Optional var6 = ServerNameResolver.DEFAULT.resolveAddress(var5).map(ResolvedServerAddress::asInetSocketAddress);
      if (var6.isEmpty()) {
         this.onPingFailed(ConnectScreen.UNKNOWN_HOST_MESSAGE, var1);
      } else {
         final InetSocketAddress var7 = (InetSocketAddress)var6.get();
         final Connection var8 = Connection.connectToServer(var7, var4, (LocalSampleLogger)null);
         this.connections.add(var8);
         var1.motd = Component.translatable("multiplayer.status.pinging");
         var1.playerList = Collections.emptyList();
         ClientStatusPacketListener var9 = new ClientStatusPacketListener() {
            private boolean success;
            private boolean receivedPing;
            private long pingStart;

            public void handleStatusResponse(ClientboundStatusResponsePacket var1x) {
               if (this.receivedPing) {
                  var8.disconnect((Component)Component.translatable("multiplayer.status.unrequested"));
               } else {
                  this.receivedPing = true;
                  ServerStatus var2x = var1x.status();
                  var1.motd = var2x.description();
                  var2x.version().ifPresentOrElse((var1xx) -> {
                     var1.version = Component.literal(var1xx.name());
                     var1.protocol = var1xx.protocol();
                  }, () -> {
                     var1.version = Component.translatable("multiplayer.status.old");
                     var1.protocol = 0;
                  });
                  var2x.players().ifPresentOrElse((var1xx) -> {
                     var1.status = ServerStatusPinger.formatPlayerCount(var1xx.online(), var1xx.max());
                     var1.players = var1xx;
                     if (!var1xx.sample().isEmpty()) {
                        ArrayList var2x = new ArrayList(var1xx.sample().size());

                        for(NameAndId var4x : var1xx.sample()) {
                           MutableComponent var5x;
                           if (var4x.equals(MinecraftServer.ANONYMOUS_PLAYER_PROFILE)) {
                              var5x = Component.translatable("multiplayer.status.anonymous_player");
                           } else {
                              var5x = Component.literal(var4x.name());
                           }

                           var2x.add(var5x);
                        }

                        if (var1xx.sample().size() < var1xx.online()) {
                           var2x.add(Component.translatable("multiplayer.status.and_more", var1xx.online() - var1xx.sample().size()));
                        }

                        var1.playerList = var2x;
                     } else {
                        var1.playerList = List.of();
                     }

                  }, () -> var1.status = Component.translatable("multiplayer.status.unknown").withStyle(ChatFormatting.DARK_GRAY));
                  var2x.favicon().ifPresent((var2xx) -> {
                     if (!Arrays.equals(var2xx.iconBytes(), var1.getIconBytes())) {
                        var1.setIconBytes(ServerData.validateIcon(var2xx.iconBytes()));
                        var2.run();
                     }

                  });
                  this.pingStart = Util.getMillis();
                  var8.send(new ServerboundPingRequestPacket(this.pingStart));
                  this.success = true;
               }
            }

            public void handlePongResponse(ClientboundPongResponsePacket var1x) {
               long var2x = this.pingStart;
               long var4x = Util.getMillis();
               var1.ping = var4x - var2x;
               var8.disconnect((Component)Component.translatable("multiplayer.status.finished"));
               var3.run();
            }

            public void onDisconnect(DisconnectionDetails var1x) {
               if (!this.success) {
                  ServerStatusPinger.this.onPingFailed(var1x.reason(), var1);
                  ServerStatusPinger.this.pingLegacyServer(var7, var5, var1, var4);
               }

            }

            public boolean isAcceptingMessages() {
               return var8.isConnected();
            }
         };

         try {
            var8.initiateServerboundStatusConnection(var5.getHost(), var5.getPort(), var9);
            var8.send(ServerboundStatusRequestPacket.INSTANCE);
         } catch (Throwable var11) {
            LOGGER.error("Failed to ping server {}", var5, var11);
         }

      }
   }

   void onPingFailed(Component var1, ServerData var2) {
      LOGGER.error("Can't ping {}: {}", var2.ip, var1.getString());
      var2.motd = CANT_CONNECT_MESSAGE;
      var2.status = CommonComponents.EMPTY;
   }

   void pingLegacyServer(InetSocketAddress var1, final ServerAddress var2, final ServerData var3, EventLoopGroupHolder var4) {
      ((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).group(var4.eventLoopGroup())).handler(new ChannelInitializer<Channel>() {
         protected void initChannel(Channel var1) {
            try {
               var1.config().setOption(ChannelOption.TCP_NODELAY, true);
            } catch (ChannelException var3x) {
            }

            var1.pipeline().addLast(new ChannelHandler[]{new LegacyServerPinger(var2, (var1x, var2x, var3xx, var4, var5) -> {
               var3.setState(ServerData.State.INCOMPATIBLE);
               var3.version = Component.literal(var2x);
               var3.motd = Component.literal(var3xx);
               var3.status = ServerStatusPinger.formatPlayerCount(var4, var5);
               var3.players = new ServerStatus.Players(var5, var4, List.of());
            })});
         }
      })).channel(var4.channelCls())).connect(var1.getAddress(), var1.getPort());
   }

   public static Component formatPlayerCount(int var0, int var1) {
      MutableComponent var2 = Component.literal(Integer.toString(var0)).withStyle(ChatFormatting.GRAY);
      MutableComponent var3 = Component.literal(Integer.toString(var1)).withStyle(ChatFormatting.GRAY);
      return Component.translatable("multiplayer.status.player_count", var2, var3).withStyle(ChatFormatting.DARK_GRAY);
   }

   public void tick() {
      synchronized(this.connections) {
         Iterator var2 = this.connections.iterator();

         while(var2.hasNext()) {
            Connection var3 = (Connection)var2.next();
            if (var3.isConnected()) {
               var3.tick();
            } else {
               var2.remove();
               var3.handleDisconnection();
            }
         }

      }
   }

   public void removeAll() {
      synchronized(this.connections) {
         Iterator var2 = this.connections.iterator();

         while(var2.hasNext()) {
            Connection var3 = (Connection)var2.next();
            if (var3.isConnected()) {
               var2.remove();
               var3.disconnect((Component)Component.translatable("multiplayer.status.cancelled"));
            }
         }

      }
   }
}
