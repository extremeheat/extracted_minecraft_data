package net.minecraft.client.multiplayer;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.status.ClientStatusPacketListener;
import net.minecraft.network.protocol.status.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.network.protocol.status.ServerboundPingRequestPacket;
import net.minecraft.network.protocol.status.ServerboundStatusRequestPacket;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerStatusPinger {
   private static final Splitter SPLITTER = Splitter.on('\u0000').limit(6);
   private static final Logger LOGGER = LogManager.getLogger();
   private final List connections = Collections.synchronizedList(Lists.newArrayList());

   public void pingServer(final ServerData var1) throws UnknownHostException {
      ServerAddress var2 = ServerAddress.parseString(var1.ip);
      final Connection var3 = Connection.connectToServer(InetAddress.getByName(var2.getHost()), var2.getPort(), false);
      this.connections.add(var3);
      var1.motd = I18n.get("multiplayer.status.pinging");
      var1.ping = -1L;
      var1.playerList = null;
      var3.setListener(new ClientStatusPacketListener() {
         private boolean success;
         private boolean receivedPing;
         private long pingStart;

         public void handleStatusResponse(ClientboundStatusResponsePacket var1x) {
            if (this.receivedPing) {
               var3.disconnect(new TranslatableComponent("multiplayer.status.unrequested", new Object[0]));
            } else {
               this.receivedPing = true;
               ServerStatus var2 = var1x.getStatus();
               if (var2.getDescription() != null) {
                  var1.motd = var2.getDescription().getColoredString();
               } else {
                  var1.motd = "";
               }

               if (var2.getVersion() != null) {
                  var1.version = var2.getVersion().getName();
                  var1.protocol = var2.getVersion().getProtocol();
               } else {
                  var1.version = I18n.get("multiplayer.status.old");
                  var1.protocol = 0;
               }

               if (var2.getPlayers() != null) {
                  var1.status = ChatFormatting.GRAY + "" + var2.getPlayers().getNumPlayers() + "" + ChatFormatting.DARK_GRAY + "/" + ChatFormatting.GRAY + var2.getPlayers().getMaxPlayers();
                  if (ArrayUtils.isNotEmpty(var2.getPlayers().getSample())) {
                     StringBuilder var3x = new StringBuilder();
                     GameProfile[] var4 = var2.getPlayers().getSample();
                     int var5 = var4.length;

                     for(int var6 = 0; var6 < var5; ++var6) {
                        GameProfile var7 = var4[var6];
                        if (var3x.length() > 0) {
                           var3x.append("\n");
                        }

                        var3x.append(var7.getName());
                     }

                     if (var2.getPlayers().getSample().length < var2.getPlayers().getNumPlayers()) {
                        if (var3x.length() > 0) {
                           var3x.append("\n");
                        }

                        var3x.append(I18n.get("multiplayer.status.and_more", var2.getPlayers().getNumPlayers() - var2.getPlayers().getSample().length));
                     }

                     var1.playerList = var3x.toString();
                  }
               } else {
                  var1.status = ChatFormatting.DARK_GRAY + I18n.get("multiplayer.status.unknown");
               }

               if (var2.getFavicon() != null) {
                  String var8 = var2.getFavicon();
                  if (var8.startsWith("data:image/png;base64,")) {
                     var1.setIconB64(var8.substring("data:image/png;base64,".length()));
                  } else {
                     ServerStatusPinger.LOGGER.error("Invalid server icon (unknown format)");
                  }
               } else {
                  var1.setIconB64((String)null);
               }

               this.pingStart = Util.getMillis();
               var3.send(new ServerboundPingRequestPacket(this.pingStart));
               this.success = true;
            }
         }

         public void handlePongResponse(ClientboundPongResponsePacket var1x) {
            long var2 = this.pingStart;
            long var4 = Util.getMillis();
            var1.ping = var4 - var2;
            var3.disconnect(new TranslatableComponent("multiplayer.status.finished", new Object[0]));
         }

         public void onDisconnect(Component var1x) {
            if (!this.success) {
               ServerStatusPinger.LOGGER.error("Can't ping {}: {}", var1.ip, var1x.getString());
               var1.motd = ChatFormatting.DARK_RED + I18n.get("multiplayer.status.cannot_connect");
               var1.status = "";
               ServerStatusPinger.this.pingLegacyServer(var1);
            }

         }

         public Connection getConnection() {
            return var3;
         }
      });

      try {
         var3.send(new ClientIntentionPacket(var2.getHost(), var2.getPort(), ConnectionProtocol.STATUS));
         var3.send(new ServerboundStatusRequestPacket());
      } catch (Throwable var5) {
         LOGGER.error(var5);
      }

   }

   private void pingLegacyServer(final ServerData var1) {
      final ServerAddress var2 = ServerAddress.parseString(var1.ip);
      ((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).group((EventLoopGroup)Connection.NETWORK_WORKER_GROUP.get())).handler(new ChannelInitializer() {
         protected void initChannel(Channel var1x) throws Exception {
            try {
               var1x.config().setOption(ChannelOption.TCP_NODELAY, true);
            } catch (ChannelException var3) {
            }

            var1x.pipeline().addLast(new ChannelHandler[]{new SimpleChannelInboundHandler() {
               public void channelActive(ChannelHandlerContext var1x) throws Exception {
                  super.channelActive(var1x);
                  ByteBuf var2x = Unpooled.buffer();

                  try {
                     var2x.writeByte(254);
                     var2x.writeByte(1);
                     var2x.writeByte(250);
                     char[] var3 = "MC|PingHost".toCharArray();
                     var2x.writeShort(var3.length);
                     char[] var4 = var3;
                     int var5 = var3.length;

                     int var6;
                     char var7;
                     for(var6 = 0; var6 < var5; ++var6) {
                        var7 = var4[var6];
                        var2x.writeChar(var7);
                     }

                     var2x.writeShort(7 + 2 * var2.getHost().length());
                     var2x.writeByte(127);
                     var3 = var2.getHost().toCharArray();
                     var2x.writeShort(var3.length);
                     var4 = var3;
                     var5 = var3.length;

                     for(var6 = 0; var6 < var5; ++var6) {
                        var7 = var4[var6];
                        var2x.writeChar(var7);
                     }

                     var2x.writeInt(var2.getPort());
                     var1x.channel().writeAndFlush(var2x).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                  } finally {
                     var2x.release();
                  }
               }

               protected void channelRead0(ChannelHandlerContext var1x, ByteBuf var2x) throws Exception {
                  short var3 = var2x.readUnsignedByte();
                  if (var3 == 255) {
                     String var4 = new String(var2x.readBytes(var2x.readShort() * 2).array(), StandardCharsets.UTF_16BE);
                     String[] var5 = (String[])Iterables.toArray(ServerStatusPinger.SPLITTER.split(var4), String.class);
                     if ("§1".equals(var5[0])) {
                        int var6 = Mth.getInt(var5[1], 0);
                        String var7 = var5[2];
                        String var8 = var5[3];
                        int var9 = Mth.getInt(var5[4], -1);
                        int var10 = Mth.getInt(var5[5], -1);
                        var1.protocol = -1;
                        var1.version = var7;
                        var1.motd = var8;
                        var1.status = ChatFormatting.GRAY + "" + var9 + "" + ChatFormatting.DARK_GRAY + "/" + ChatFormatting.GRAY + var10;
                     }
                  }

                  var1x.close();
               }

               public void exceptionCaught(ChannelHandlerContext var1x, Throwable var2x) throws Exception {
                  var1x.close();
               }

               // $FF: synthetic method
               protected void channelRead0(ChannelHandlerContext var1x, Object var2x) throws Exception {
                  this.channelRead0(var1x, (ByteBuf)var2x);
               }
            }});
         }
      })).channel(NioSocketChannel.class)).connect(var2.getHost(), var2.getPort());
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
               var3.disconnect(new TranslatableComponent("multiplayer.status.cancelled", new Object[0]));
            }
         }

      }
   }
}
