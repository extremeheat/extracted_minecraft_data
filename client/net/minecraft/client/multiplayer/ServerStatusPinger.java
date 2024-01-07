package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
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
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.status.ClientStatusPacketListener;
import net.minecraft.network.protocol.status.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.network.protocol.status.ServerboundPingRequestPacket;
import net.minecraft.network.protocol.status.ServerboundStatusRequestPacket;
import org.slf4j.Logger;

public class ServerStatusPinger {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component CANT_CONNECT_MESSAGE = Component.translatable("multiplayer.status.cannot_connect").withColor(-65536);
   private final List<Connection> connections = Collections.synchronizedList(Lists.newArrayList());

   public ServerStatusPinger() {
      super();
   }

   public void pingServer(final ServerData var1, final Runnable var2) throws UnknownHostException {
      final ServerAddress var3 = ServerAddress.parseString(var1.ip);
      Optional var4 = ServerNameResolver.DEFAULT.resolveAddress(var3).map(ResolvedServerAddress::asInetSocketAddress);
      if (var4.isEmpty()) {
         this.onPingFailed(ConnectScreen.UNKNOWN_HOST_MESSAGE, var1);
      } else {
         final InetSocketAddress var5 = (InetSocketAddress)var4.get();
         final Connection var6 = Connection.connectToServer(var5, false, null);
         this.connections.add(var6);
         var1.motd = Component.translatable("multiplayer.status.pinging");
         var1.ping = -1L;
         var1.playerList = Collections.emptyList();
         ClientStatusPacketListener var7 = new ClientStatusPacketListener() {
            private boolean success;
            private boolean receivedPing;
            private long pingStart;

            @Override
            public void handleStatusResponse(ClientboundStatusResponsePacket var1x) {
               if (this.receivedPing) {
                  var6.disconnect(Component.translatable("multiplayer.status.unrequested"));
               } else {
                  this.receivedPing = true;
                  ServerStatus var2x = var1x.status();
                  var1.motd = var2x.description();
                  var2x.version().ifPresentOrElse(var1xxx -> {
                     var1.version = Component.literal(var1xxx.name());
                     var1.protocol = var1xxx.protocol();
                  }, () -> {
                     var1.version = Component.translatable("multiplayer.status.old");
                     var1.protocol = 0;
                  });
                  var2x.players().ifPresentOrElse(var1xxx -> {
                     var1.status = ServerStatusPinger.formatPlayerCount(var1xxx.online(), var1xxx.max());
                     var1.players = var1xxx;
                     if (!var1xxx.sample().isEmpty()) {
                        ArrayList var2xxxx = new ArrayList(var1xxx.sample().size());

                        for(GameProfile var4 : var1xxx.sample()) {
                           var2xxxx.add(Component.literal(var4.getName()));
                        }

                        if (var1xxx.sample().size() < var1xxx.online()) {
                           var2xxxx.add(Component.translatable("multiplayer.status.and_more", var1xxx.online() - var1xxx.sample().size()));
                        }

                        var1.playerList = var2xxxx;
                     } else {
                        var1.playerList = List.of();
                     }
                  }, () -> var1.status = Component.translatable("multiplayer.status.unknown").withStyle(ChatFormatting.DARK_GRAY));
                  var2x.favicon().ifPresent(var2xxx -> {
                     if (!Arrays.equals(var2xxx.iconBytes(), var1.getIconBytes())) {
                        var1.setIconBytes(ServerData.validateIcon(var2xxx.iconBytes()));
                        var2.run();
                     }
                  });
                  this.pingStart = Util.getMillis();
                  var6.send(new ServerboundPingRequestPacket(this.pingStart));
                  this.success = true;
               }
            }

            @Override
            public void handlePongResponse(ClientboundPongResponsePacket var1x) {
               long var2x = this.pingStart;
               long var4 = Util.getMillis();
               var1.ping = var4 - var2x;
               var6.disconnect(Component.translatable("multiplayer.status.finished"));
            }

            @Override
            public void onDisconnect(Component var1x) {
               if (!this.success) {
                  ServerStatusPinger.this.onPingFailed(var1x, var1);
                  ServerStatusPinger.this.pingLegacyServer(var5, var3, var1);
               }
            }

            @Override
            public boolean isAcceptingMessages() {
               return var6.isConnected();
            }
         };

         try {
            var6.initiateServerboundStatusConnection(var3.getHost(), var3.getPort(), var7);
            var6.send(new ServerboundStatusRequestPacket());
         } catch (Throwable var9) {
            LOGGER.error("Failed to ping server {}", var3, var9);
         }
      }
   }

   void onPingFailed(Component var1, ServerData var2) {
      LOGGER.error("Can't ping {}: {}", var2.ip, var1.getString());
      var2.motd = CANT_CONNECT_MESSAGE;
      var2.status = CommonComponents.EMPTY;
   }

   void pingLegacyServer(InetSocketAddress var1, final ServerAddress var2, final ServerData var3) {
      ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)Connection.NETWORK_WORKER_GROUP.get()))
               .handler(new ChannelInitializer<Channel>() {
                  protected void initChannel(Channel var1) {
                     try {
                        var1.config().setOption(ChannelOption.TCP_NODELAY, true);
                     } catch (ChannelException var3x) {
                     }
         
                     var1.pipeline().addLast(new ChannelHandler[]{new LegacyServerPinger(var2, (var1x, var2xx, var3xxx, var4, var5) -> {
                        var3.protocol = -1;
                        var3.version = Component.literal(var2xx);
                        var3.motd = Component.literal(var3xxx);
                        var3.status = ServerStatusPinger.formatPlayerCount(var4, var5);
                        var3.players = new ServerStatus.Players(var5, var4, List.of());
                     })});
                  }
               }))
            .channel(NioSocketChannel.class))
         .connect(var1.getAddress(), var1.getPort());
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
               var3.disconnect(Component.translatable("multiplayer.status.cancelled"));
            }
         }
      }
   }
}
