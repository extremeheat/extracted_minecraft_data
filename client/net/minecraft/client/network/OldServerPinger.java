package net.minecraft.client.network;

import com.google.common.base.Charsets;
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OldServerPinger {
   private static final Splitter field_147230_a = Splitter.on('\u0000').limit(6);
   private static final Logger field_147228_b = LogManager.getLogger();
   private final List<NetworkManager> field_147229_c = Collections.synchronizedList(Lists.newArrayList());

   public OldServerPinger() {
      super();
   }

   public void func_147224_a(final ServerData var1) throws UnknownHostException {
      ServerAddress var2 = ServerAddress.func_78860_a(var1.field_78845_b);
      final NetworkManager var3 = NetworkManager.func_181124_a(InetAddress.getByName(var2.func_78861_a()), var2.func_78864_b(), false);
      this.field_147229_c.add(var3);
      var1.field_78843_d = "Pinging...";
      var1.field_78844_e = -1L;
      var1.field_147412_i = null;
      var3.func_150719_a(new INetHandlerStatusClient() {
         private boolean field_147403_d = false;
         private boolean field_183009_e = false;
         private long field_175092_e = 0L;

         public void func_147397_a(S00PacketServerInfo var1x) {
            if (this.field_183009_e) {
               var3.func_150718_a(new ChatComponentText("Received unrequested status"));
            } else {
               this.field_183009_e = true;
               ServerStatusResponse var2 = var1x.func_149294_c();
               if (var2.func_151317_a() != null) {
                  var1.field_78843_d = var2.func_151317_a().func_150254_d();
               } else {
                  var1.field_78843_d = "";
               }

               if (var2.func_151322_c() != null) {
                  var1.field_82822_g = var2.func_151322_c().func_151303_a();
                  var1.field_82821_f = var2.func_151322_c().func_151304_b();
               } else {
                  var1.field_82822_g = "Old";
                  var1.field_82821_f = 0;
               }

               if (var2.func_151318_b() != null) {
                  var1.field_78846_c = EnumChatFormatting.GRAY + "" + var2.func_151318_b().func_151333_b() + "" + EnumChatFormatting.DARK_GRAY + "/" + EnumChatFormatting.GRAY + var2.func_151318_b().func_151332_a();
                  if (ArrayUtils.isNotEmpty(var2.func_151318_b().func_151331_c())) {
                     StringBuilder var3x = new StringBuilder();
                     GameProfile[] var4 = var2.func_151318_b().func_151331_c();
                     int var5 = var4.length;

                     for(int var6 = 0; var6 < var5; ++var6) {
                        GameProfile var7 = var4[var6];
                        if (var3x.length() > 0) {
                           var3x.append("\n");
                        }

                        var3x.append(var7.getName());
                     }

                     if (var2.func_151318_b().func_151331_c().length < var2.func_151318_b().func_151333_b()) {
                        if (var3x.length() > 0) {
                           var3x.append("\n");
                        }

                        var3x.append("... and ").append(var2.func_151318_b().func_151333_b() - var2.func_151318_b().func_151331_c().length).append(" more ...");
                     }

                     var1.field_147412_i = var3x.toString();
                  }
               } else {
                  var1.field_78846_c = EnumChatFormatting.DARK_GRAY + "???";
               }

               if (var2.func_151316_d() != null) {
                  String var8 = var2.func_151316_d();
                  if (var8.startsWith("data:image/png;base64,")) {
                     var1.func_147407_a(var8.substring("data:image/png;base64,".length()));
                  } else {
                     OldServerPinger.field_147228_b.error("Invalid server icon (unknown format)");
                  }
               } else {
                  var1.func_147407_a((String)null);
               }

               this.field_175092_e = Minecraft.func_71386_F();
               var3.func_179290_a(new C01PacketPing(this.field_175092_e));
               this.field_147403_d = true;
            }
         }

         public void func_147398_a(S01PacketPong var1x) {
            long var2 = this.field_175092_e;
            long var4 = Minecraft.func_71386_F();
            var1.field_78844_e = var4 - var2;
            var3.func_150718_a(new ChatComponentText("Finished"));
         }

         public void func_147231_a(IChatComponent var1x) {
            if (!this.field_147403_d) {
               OldServerPinger.field_147228_b.error("Can't ping " + var1.field_78845_b + ": " + var1x.func_150260_c());
               var1.field_78843_d = EnumChatFormatting.DARK_RED + "Can't connect to server.";
               var1.field_78846_c = "";
               OldServerPinger.this.func_147225_b(var1);
            }

         }
      });

      try {
         var3.func_179290_a(new C00Handshake(47, var2.func_78861_a(), var2.func_78864_b(), EnumConnectionState.STATUS));
         var3.func_179290_a(new C00PacketServerQuery());
      } catch (Throwable var5) {
         field_147228_b.error(var5);
      }

   }

   private void func_147225_b(final ServerData var1) {
      final ServerAddress var2 = ServerAddress.func_78860_a(var1.field_78845_b);
      ((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).group((EventLoopGroup)NetworkManager.field_179295_d.func_179281_c())).handler(new ChannelInitializer<Channel>() {
         protected void initChannel(Channel var1x) throws Exception {
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
                     char[] var4 = var3;
                     int var5 = var3.length;

                     int var6;
                     char var7;
                     for(var6 = 0; var6 < var5; ++var6) {
                        var7 = var4[var6];
                        var2x.writeChar(var7);
                     }

                     var2x.writeShort(7 + 2 * var2.func_78861_a().length());
                     var2x.writeByte(127);
                     var3 = var2.func_78861_a().toCharArray();
                     var2x.writeShort(var3.length);
                     var4 = var3;
                     var5 = var3.length;

                     for(var6 = 0; var6 < var5; ++var6) {
                        var7 = var4[var6];
                        var2x.writeChar(var7);
                     }

                     var2x.writeInt(var2.func_78864_b());
                     var1x.channel().writeAndFlush(var2x).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                  } finally {
                     var2x.release();
                  }
               }

               protected void channelRead0(ChannelHandlerContext var1x, ByteBuf var2x) throws Exception {
                  short var3 = var2x.readUnsignedByte();
                  if (var3 == 255) {
                     String var4 = new String(var2x.readBytes(var2x.readShort() * 2).array(), Charsets.UTF_16BE);
                     String[] var5 = (String[])Iterables.toArray(OldServerPinger.field_147230_a.split(var4), String.class);
                     if ("\u00a71".equals(var5[0])) {
                        int var6 = MathHelper.func_82715_a(var5[1], 0);
                        String var7 = var5[2];
                        String var8 = var5[3];
                        int var9 = MathHelper.func_82715_a(var5[4], -1);
                        int var10 = MathHelper.func_82715_a(var5[5], -1);
                        var1.field_82821_f = -1;
                        var1.field_82822_g = var7;
                        var1.field_78843_d = var8;
                        var1.field_78846_c = EnumChatFormatting.GRAY + "" + var9 + "" + EnumChatFormatting.DARK_GRAY + "/" + EnumChatFormatting.GRAY + var10;
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
      })).channel(NioSocketChannel.class)).connect(var2.func_78861_a(), var2.func_78864_b());
   }

   public void func_147223_a() {
      synchronized(this.field_147229_c) {
         Iterator var2 = this.field_147229_c.iterator();

         while(var2.hasNext()) {
            NetworkManager var3 = (NetworkManager)var2.next();
            if (var3.func_150724_d()) {
               var3.func_74428_b();
            } else {
               var2.remove();
               var3.func_179293_l();
            }
         }

      }
   }

   public void func_147226_b() {
      synchronized(this.field_147229_c) {
         Iterator var2 = this.field_147229_c.iterator();

         while(var2.hasNext()) {
            NetworkManager var3 = (NetworkManager)var2.next();
            if (var3.func_150724_d()) {
               var2.remove();
               var3.func_150718_a(new ChatComponentText("Cancelled"));
            }
         }

      }
   }
}
