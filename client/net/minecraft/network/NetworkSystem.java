package net.minecraft.network;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.network.NetHandlerHandshakeMemory;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LazyLoadBase;
import net.minecraft.util.text.TextComponentString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkSystem {
   private static final Logger field_151275_b = LogManager.getLogger();
   public static final LazyLoadBase<NioEventLoopGroup> field_151276_c = new LazyLoadBase(() -> {
      return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Server IO #%d").setDaemon(true).build());
   });
   public static final LazyLoadBase<EpollEventLoopGroup> field_181141_b = new LazyLoadBase(() -> {
      return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build());
   });
   private final MinecraftServer field_151273_d;
   public volatile boolean field_151277_a;
   private final List<ChannelFuture> field_151274_e = Collections.synchronizedList(Lists.newArrayList());
   private final List<NetworkManager> field_151272_f = Collections.synchronizedList(Lists.newArrayList());

   public NetworkSystem(MinecraftServer var1) {
      super();
      this.field_151273_d = var1;
      this.field_151277_a = true;
   }

   public void func_151265_a(@Nullable InetAddress var1, int var2) throws IOException {
      synchronized(this.field_151274_e) {
         Class var4;
         LazyLoadBase var5;
         if (Epoll.isAvailable() && this.field_151273_d.func_181035_ah()) {
            var4 = EpollServerSocketChannel.class;
            var5 = field_181141_b;
            field_151275_b.info("Using epoll channel type");
         } else {
            var4 = NioServerSocketChannel.class;
            var5 = field_151276_c;
            field_151275_b.info("Using default channel type");
         }

         this.field_151274_e.add(((ServerBootstrap)((ServerBootstrap)(new ServerBootstrap()).channel(var4)).childHandler(new ChannelInitializer<Channel>() {
            protected void initChannel(Channel var1) throws Exception {
               try {
                  var1.config().setOption(ChannelOption.TCP_NODELAY, true);
               } catch (ChannelException var3) {
               }

               var1.pipeline().addLast("timeout", new ReadTimeoutHandler(30)).addLast("legacy_query", new LegacyPingHandler(NetworkSystem.this)).addLast("splitter", new NettyVarint21FrameDecoder()).addLast("decoder", new NettyPacketDecoder(EnumPacketDirection.SERVERBOUND)).addLast("prepender", new NettyVarint21FrameEncoder()).addLast("encoder", new NettyPacketEncoder(EnumPacketDirection.CLIENTBOUND));
               NetworkManager var2 = new NetworkManager(EnumPacketDirection.SERVERBOUND);
               NetworkSystem.this.field_151272_f.add(var2);
               var1.pipeline().addLast("packet_handler", var2);
               var2.func_150719_a(new NetHandlerHandshakeTCP(NetworkSystem.this.field_151273_d, var2));
            }
         }).group((EventLoopGroup)var5.func_179281_c()).localAddress(var1, var2)).bind().syncUninterruptibly());
      }
   }

   public SocketAddress func_151270_a() {
      ChannelFuture var1;
      synchronized(this.field_151274_e) {
         var1 = ((ServerBootstrap)((ServerBootstrap)(new ServerBootstrap()).channel(LocalServerChannel.class)).childHandler(new ChannelInitializer<Channel>() {
            protected void initChannel(Channel var1) throws Exception {
               NetworkManager var2 = new NetworkManager(EnumPacketDirection.SERVERBOUND);
               var2.func_150719_a(new NetHandlerHandshakeMemory(NetworkSystem.this.field_151273_d, var2));
               NetworkSystem.this.field_151272_f.add(var2);
               var1.pipeline().addLast("packet_handler", var2);
            }
         }).group((EventLoopGroup)field_151276_c.func_179281_c()).localAddress(LocalAddress.ANY)).bind().syncUninterruptibly();
         this.field_151274_e.add(var1);
      }

      return var1.channel().localAddress();
   }

   public void func_151268_b() {
      this.field_151277_a = false;
      Iterator var1 = this.field_151274_e.iterator();

      while(var1.hasNext()) {
         ChannelFuture var2 = (ChannelFuture)var1.next();

         try {
            var2.channel().close().sync();
         } catch (InterruptedException var4) {
            field_151275_b.error("Interrupted whilst closing channel");
         }
      }

   }

   public void func_151269_c() {
      synchronized(this.field_151272_f) {
         Iterator var2 = this.field_151272_f.iterator();

         while(true) {
            while(true) {
               NetworkManager var3;
               do {
                  if (!var2.hasNext()) {
                     return;
                  }

                  var3 = (NetworkManager)var2.next();
               } while(var3.func_179291_h());

               if (var3.func_150724_d()) {
                  try {
                     var3.func_74428_b();
                  } catch (Exception var8) {
                     if (var3.func_150731_c()) {
                        CrashReport var10 = CrashReport.func_85055_a(var8, "Ticking memory connection");
                        CrashReportCategory var6 = var10.func_85058_a("Ticking connection");
                        var6.func_189529_a("Connection", var3::toString);
                        throw new ReportedException(var10);
                     }

                     field_151275_b.warn("Failed to handle packet for {}", var3.func_74430_c(), var8);
                     TextComponentString var5 = new TextComponentString("Internal server error");
                     var3.func_201058_a(new SPacketDisconnect(var5), (var2x) -> {
                        var3.func_150718_a(var5);
                     });
                     var3.func_150721_g();
                  }
               } else {
                  var2.remove();
                  var3.func_179293_l();
               }
            }
         }
      }
   }

   public MinecraftServer func_151267_d() {
      return this.field_151273_d;
   }
}
