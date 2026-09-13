package net.minecraft.network;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ReportedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkSystem {
   private static final Logger field_151275_b = LogManager.getLogger();
   private static final NioEventLoopGroup field_151276_c = new NioEventLoopGroup(
      0, new ThreadFactoryBuilder().setNameFormat("Netty IO #%d").setDaemon(true).build()
   );
   private final MinecraftServer field_151273_d;
   public volatile boolean field_151277_a;
   private final List field_151274_e = Collections.synchronizedList(new ArrayList());
   private final List field_151272_f = Collections.synchronizedList(new ArrayList());

   public NetworkSystem(MinecraftServer var1) {
      super();
      this.field_151273_d = var1;
      this.field_151277_a = true;
   }

   public void func_151265_a(InetAddress var1, int var2) {
      synchronized(this.field_151274_e) {
         this.field_151274_e
            .add(
               ((ServerBootstrap)((ServerBootstrap)new ServerBootstrap().channel(NioServerSocketChannel.class))
                     .childHandler(new NetworkSystem$1(this))
                     .group(field_151276_c)
                     .localAddress(var1, var2))
                  .bind()
                  .syncUninterruptibly()
            );
      }
   }

   public SocketAddress func_151270_a() {
      ChannelFuture var1;
      synchronized(this.field_151274_e) {
         var1 = ((ServerBootstrap)((ServerBootstrap)new ServerBootstrap().channel(LocalServerChannel.class))
               .childHandler(new NetworkSystem$2(this))
               .group(field_151276_c)
               .localAddress(LocalAddress.ANY))
            .bind()
            .syncUninterruptibly();
         this.field_151274_e.add(var1);
      }

      return var1.channel().localAddress();
   }

   public void func_151268_b() {
      this.field_151277_a = false;

      for(ChannelFuture var2 : this.field_151274_e) {
         var2.channel().close().syncUninterruptibly();
      }
   }

   public void func_151269_c() {
      synchronized(this.field_151272_f) {
         Iterator var2 = this.field_151272_f.iterator();

         while(var2.hasNext()) {
            NetworkManager var3 = (NetworkManager)var2.next();
            if (!var3.func_150724_d()) {
               var2.remove();
               if (var3.func_150730_f() != null) {
                  var3.func_150729_e().func_147231_a(var3.func_150730_f());
               } else if (var3.func_150729_e() != null) {
                  var3.func_150729_e().func_147231_a(new ChatComponentText("Disconnected"));
               }
            } else {
               try {
                  var3.func_74428_b();
               } catch (Exception var8) {
                  if (var3.func_150731_c()) {
                     CrashReport var10 = CrashReport.func_85055_a(var8, "Ticking memory connection");
                     CrashReportCategory var6 = var10.func_85058_a("Ticking connection");
                     var6.func_71500_a("Connection", new NetworkSystem$3(this, var3));
                     throw new ReportedException(var10);
                  }

                  field_151275_b.warn("Failed to handle packet for " + var3.func_74430_c(), var8);
                  ChatComponentText var5 = new ChatComponentText("Internal server error");
                  var3.func_150725_a(new S40PacketDisconnect(var5), new NetworkSystem$4(this, var3, var5));
                  var3.func_150721_g();
               }
            }
         }
      }
   }

   public MinecraftServer func_151267_d() {
      return this.field_151273_d;
   }
}
