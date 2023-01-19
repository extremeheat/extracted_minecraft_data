package net.minecraft.client.multiplayer;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerNameResolver;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.status.ClientStatusPacketListener;
import net.minecraft.network.protocol.status.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.network.protocol.status.ServerboundPingRequestPacket;
import net.minecraft.network.protocol.status.ServerboundStatusRequestPacket;
import net.minecraft.util.Mth;
import org.slf4j.Logger;

public class ServerStatusPinger {
   static final Splitter SPLITTER = Splitter.on('\u0000').limit(6);
   static final Logger LOGGER = LogUtils.getLogger();
   private static final Component CANT_CONNECT_MESSAGE = Component.translatable("multiplayer.status.cannot_connect").withStyle(var0 -> var0.withColor(-65536));
   private final List<Connection> connections = Collections.synchronizedList(Lists.newArrayList());

   public ServerStatusPinger() {
      super();
   }

   public void pingServer(final ServerData var1, final Runnable var2) throws UnknownHostException {
      ServerAddress var3 = ServerAddress.parseString(var1.ip);
      Optional var4 = ServerNameResolver.DEFAULT.resolveAddress(var3).map(ResolvedServerAddress::asInetSocketAddress);
      if (!var4.isPresent()) {
         this.onPingFailed(ConnectScreen.UNKNOWN_HOST_MESSAGE, var1);
      } else {
         final InetSocketAddress var5 = (InetSocketAddress)var4.get();
         final Connection var6 = Connection.connectToServer(var5, false);
         this.connections.add(var6);
         var1.motd = Component.translatable("multiplayer.status.pinging");
         var1.ping = -1L;
         var1.playerList = Collections.emptyList();
         var6.setListener(new ClientStatusPacketListener() {
            private boolean success;
            private boolean receivedPing;
            private long pingStart;

            @Override
            public void handleStatusResponse(ClientboundStatusResponsePacket var1x) {
               if (this.receivedPing) {
                  var6.disconnect(Component.translatable("multiplayer.status.unrequested"));
               } else {
                  this.receivedPing = true;
                  ServerStatus var2x = var1x.getStatus();
                  if (var2x.getDescription() != null) {
                     var1.motd = var2x.getDescription();
                  } else {
                     var1.motd = CommonComponents.EMPTY;
                  }

                  if (var2x.getVersion() != null) {
                     var1.version = Component.literal(var2x.getVersion().getName());
                     var1.protocol = var2x.getVersion().getProtocol();
                  } else {
                     var1.version = Component.translatable("multiplayer.status.old");
                     var1.protocol = 0;
                  }

                  if (var2x.getPlayers() != null) {
                     var1.status = ServerStatusPinger.formatPlayerCount(var2x.getPlayers().getNumPlayers(), var2x.getPlayers().getMaxPlayers());
                     var1.players = var2x.getPlayers();
                     ArrayList var3 = Lists.newArrayList();
                     GameProfile[] var4 = var2x.getPlayers().getSample();
                     if (var4 != null && var4.length > 0) {
                        for(GameProfile var8 : var4) {
                           var3.add(Component.literal(var8.getName()));
                        }

                        if (var4.length < var2x.getPlayers().getNumPlayers()) {
                           var3.add(Component.translatable("multiplayer.status.and_more", var2x.getPlayers().getNumPlayers() - var4.length));
                        }

                        var1.playerList = var3;
                     }
                  } else {
                     var1.status = Component.translatable("multiplayer.status.unknown").withStyle(ChatFormatting.DARK_GRAY);
                  }

                  String var10 = var2x.getFavicon();
                  if (var10 != null) {
                     try {
                        var10 = ServerData.parseFavicon(var10);
                     } catch (ParseException var9) {
                        ServerStatusPinger.LOGGER.error("Invalid server icon", var9);
                     }
                  }

                  if (!Objects.equals(var10, var1.getIconB64())) {
                     var1.setIconB64(var10);
                     var2.run();
                  }

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
                  ServerStatusPinger.this.pingLegacyServer(var5, var1);
               }
            }

            @Override
            public boolean isAcceptingMessages() {
               return var6.isConnected();
            }
         });

         try {
            var6.send(new ClientIntentionPacket(var3.getHost(), var3.getPort(), ConnectionProtocol.STATUS));
            var6.send(new ServerboundStatusRequestPacket());
         } catch (Throwable var8) {
            LOGGER.error("Failed to ping server {}", var3, var8);
         }
      }
   }

   void onPingFailed(Component var1, ServerData var2) {
      LOGGER.error("Can't ping {}: {}", var2.ip, var1.getString());
      var2.motd = CANT_CONNECT_MESSAGE;
      var2.status = CommonComponents.EMPTY;
   }

   void pingLegacyServer(final InetSocketAddress var1, final ServerData var2) {
      ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)Connection.NETWORK_WORKER_GROUP.get()))
               .handler(new ChannelInitializer<Channel>() {
                  protected void initChannel(Channel var1x) {
                     try {
                        var1x.config().setOption(ChannelOption.TCP_NODELAY, true);
                     } catch (ChannelException var3) {
                     }
         
                     var1x.pipeline().addLast(new ChannelHandler[]{new SimpleChannelInboundHandler<ByteBuf>() {
                        public void channelActive(ChannelHandlerContext var1x) throws Exception {
                           super.channelActive(var1x);
                           ByteBuf var2x = Unpooled.buffer();
         
                           try {
                              var2x.writeByte(254);
                              var2x.writeByte(1);
                              var2x.writeByte(250);
                              char[] var3 = "MC|PingHost".toCharArray();
                              var2x.writeShort(var3.length);
         
                              for(char var7 : var3) {
                                 var2x.writeChar(var7);
                              }
         
                              var2x.writeShort(7 + 2 * var1.getHostName().length());
                              var2x.writeByte(127);
                              var3 = var1.getHostName().toCharArray();
                              var2x.writeShort(var3.length);
         
                              for(char var15 : var3) {
                                 var2x.writeChar(var15);
                              }
         
                              var2x.writeInt(var1.getPort());
                              var1x.channel().writeAndFlush(var2x).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                           } finally {
                              var2x.release();
                           }
                        }
         
                        protected void channelRead0(ChannelHandlerContext var1x, ByteBuf var2x) {
                           short var3 = var2x.readUnsignedByte();
                           if (var3 == 255) {
                              String var4 = new String(var2x.readBytes(var2x.readShort() * 2).array(), StandardCharsets.UTF_16BE);
                              String[] var5 = (String[])Iterables.toArray(ServerStatusPinger.SPLITTER.split(var4), String.class);
                              if ("\u00a71".equals(var5[0])) {
                                 int var6 = Mth.getInt(var5[1], 0);
                                 String var7 = var5[2];
                                 String var8 = var5[3];
                                 int var9 = Mth.getInt(var5[4], -1);
                                 int var10 = Mth.getInt(var5[5], -1);
                                 var2.protocol = -1;
                                 var2.version = Component.literal(var7);
                                 var2.motd = Component.literal(var8);
                                 var2.status = ServerStatusPinger.formatPlayerCount(var9, var10);
                                 var2.players = new ServerStatus.Players(var10, var9);
                              }
                           }
         
                           var1x.close();
                        }
         
                        public void exceptionCaught(ChannelHandlerContext var1x, Throwable var2x) {
                           var1x.close();
                        }
                     }});
                  }
               }))
            .channel(NioSocketChannel.class))
         .connect(var1.getAddress(), var1.getPort());
   }

   static Component formatPlayerCount(int var0, int var1) {
      return Component.literal(Integer.toString(var0))
         .append(Component.literal("/").withStyle(ChatFormatting.DARK_GRAY))
         .append(Integer.toString(var1))
         .withStyle(ChatFormatting.GRAY);
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
