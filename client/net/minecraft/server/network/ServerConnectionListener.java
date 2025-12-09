package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.local.LocalAddress;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.network.BandwidthDebugMonitor;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.RateKickingConnection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.server.MinecraftServer;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class ServerConnectionListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   final MinecraftServer server;
   public volatile boolean running;
   private final List<ChannelFuture> channels = Collections.synchronizedList(Lists.newArrayList());
   final List<Connection> connections = Collections.synchronizedList(Lists.newArrayList());

   public ServerConnectionListener(MinecraftServer var1) {
      super();
      this.server = var1;
      this.running = true;
   }

   public void startTcpServerListener(@Nullable InetAddress var1, int var2) throws IOException {
      synchronized(this.channels) {
         EventLoopGroupHolder var4 = EventLoopGroupHolder.remote(this.server.useNativeTransport());
         this.channels.add(((ServerBootstrap)((ServerBootstrap)(new ServerBootstrap()).channel(var4.serverChannelCls())).childHandler(new ChannelInitializer<Channel>() {
            protected void initChannel(Channel var1) {
               try {
                  var1.config().setOption(ChannelOption.TCP_NODELAY, true);
               } catch (ChannelException var5) {
               }

               ChannelPipeline var2 = var1.pipeline().addLast("timeout", new ReadTimeoutHandler(30));
               if (ServerConnectionListener.this.server.repliesToStatus()) {
                  var2.addLast("legacy_query", new LegacyQueryHandler(ServerConnectionListener.this.getServer()));
               }

               Connection.configureSerialization(var2, PacketFlow.SERVERBOUND, false, (BandwidthDebugMonitor)null);
               int var3 = ServerConnectionListener.this.server.getRateLimitPacketsPerSecond();
               Object var4 = var3 > 0 ? new RateKickingConnection(var3) : new Connection(PacketFlow.SERVERBOUND);
               ServerConnectionListener.this.connections.add(var4);
               ((Connection)var4).configurePacketHandler(var2);
               ((Connection)var4).setListenerForServerboundHandshake(new ServerHandshakePacketListenerImpl(ServerConnectionListener.this.server, (Connection)var4));
            }
         }).group(var4.eventLoopGroup()).localAddress(var1, var2)).bind().syncUninterruptibly());
      }
   }

   public SocketAddress startMemoryChannel() {
      ChannelFuture var1;
      synchronized(this.channels) {
         var1 = ((ServerBootstrap)((ServerBootstrap)(new ServerBootstrap()).channel(EventLoopGroupHolder.local().serverChannelCls())).childHandler(new ChannelInitializer<Channel>() {
            protected void initChannel(Channel var1) {
               Connection var2 = new Connection(PacketFlow.SERVERBOUND);
               var2.setListenerForServerboundHandshake(new MemoryServerHandshakePacketListenerImpl(ServerConnectionListener.this.server, var2));
               ServerConnectionListener.this.connections.add(var2);
               ChannelPipeline var3 = var1.pipeline();
               Connection.configureInMemoryPipeline(var3, PacketFlow.SERVERBOUND);
               if (SharedConstants.DEBUG_FAKE_LATENCY_MS > 0) {
                  var3.addLast("latency", new LatencySimulator(SharedConstants.DEBUG_FAKE_LATENCY_MS, SharedConstants.DEBUG_FAKE_JITTER_MS));
               }

               var2.configurePacketHandler(var3);
            }
         }).group(EventLoopGroupHolder.local().eventLoopGroup()).localAddress(LocalAddress.ANY)).bind().syncUninterruptibly();
         this.channels.add(var1);
      }

      return var1.channel().localAddress();
   }

   public void stop() {
      this.running = false;

      for(ChannelFuture var2 : this.channels) {
         try {
            var2.channel().close().sync();
         } catch (InterruptedException var4) {
            LOGGER.error("Interrupted whilst closing channel");
         }
      }

   }

   public void tick() {
      synchronized(this.connections) {
         Iterator var2 = this.connections.iterator();

         while(var2.hasNext()) {
            Connection var3 = (Connection)var2.next();
            if (!var3.isConnecting()) {
               if (var3.isConnected()) {
                  try {
                     var3.tick();
                  } catch (Exception var7) {
                     if (var3.isMemoryConnection()) {
                        throw new ReportedException(CrashReport.forThrowable(var7, "Ticking memory connection"));
                     }

                     LOGGER.warn("Failed to handle packet for {}", var3.getLoggableAddress(this.server.logIPs()), var7);
                     MutableComponent var5 = Component.literal("Internal server error");
                     var3.send(new ClientboundDisconnectPacket(var5), PacketSendListener.thenRun(() -> var3.disconnect(var5)));
                     var3.setReadOnly();
                  }
               } else {
                  var2.remove();
                  var3.handleDisconnection();
               }
            }
         }

      }
   }

   public MinecraftServer getServer() {
      return this.server;
   }

   public List<Connection> getConnections() {
      return this.connections;
   }

   static class LatencySimulator extends ChannelInboundHandlerAdapter {
      private static final Timer TIMER = new HashedWheelTimer();
      private final int delay;
      private final int jitter;
      private final List<DelayedMessage> queuedMessages = Lists.newArrayList();

      public LatencySimulator(int var1, int var2) {
         super();
         this.delay = var1;
         this.jitter = var2;
      }

      public void channelRead(ChannelHandlerContext var1, Object var2) {
         this.delayDownstream(var1, var2);
      }

      private void delayDownstream(ChannelHandlerContext var1, Object var2) {
         int var3 = this.delay + (int)(Math.random() * (double)this.jitter);
         this.queuedMessages.add(new DelayedMessage(var1, var2));
         TIMER.newTimeout(this::onTimeout, (long)var3, TimeUnit.MILLISECONDS);
      }

      private void onTimeout(Timeout var1) {
         DelayedMessage var2 = (DelayedMessage)this.queuedMessages.remove(0);
         var2.ctx.fireChannelRead(var2.msg);
      }

      static class DelayedMessage {
         public final ChannelHandlerContext ctx;
         public final Object msg;

         public DelayedMessage(ChannelHandlerContext var1, Object var2) {
            super();
            this.ctx = var1;
            this.msg = var2;
         }
      }
   }
}
