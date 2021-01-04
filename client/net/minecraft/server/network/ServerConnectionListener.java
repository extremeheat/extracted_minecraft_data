package net.minecraft.server.network;

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
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketDecoder;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.Varint21FrameDecoder;
import net.minecraft.network.Varint21LengthFieldPrepender;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LazyLoadedValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerConnectionListener {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final LazyLoadedValue<NioEventLoopGroup> SERVER_EVENT_GROUP = new LazyLoadedValue(() -> {
      return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Server IO #%d").setDaemon(true).build());
   });
   public static final LazyLoadedValue<EpollEventLoopGroup> SERVER_EPOLL_EVENT_GROUP = new LazyLoadedValue(() -> {
      return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build());
   });
   private final MinecraftServer server;
   public volatile boolean running;
   private final List<ChannelFuture> channels = Collections.synchronizedList(Lists.newArrayList());
   private final List<Connection> connections = Collections.synchronizedList(Lists.newArrayList());

   public ServerConnectionListener(MinecraftServer var1) {
      super();
      this.server = var1;
      this.running = true;
   }

   public void startTcpServerListener(@Nullable InetAddress var1, int var2) throws IOException {
      synchronized(this.channels) {
         Class var4;
         LazyLoadedValue var5;
         if (Epoll.isAvailable() && this.server.isEpollEnabled()) {
            var4 = EpollServerSocketChannel.class;
            var5 = SERVER_EPOLL_EVENT_GROUP;
            LOGGER.info("Using epoll channel type");
         } else {
            var4 = NioServerSocketChannel.class;
            var5 = SERVER_EVENT_GROUP;
            LOGGER.info("Using default channel type");
         }

         this.channels.add(((ServerBootstrap)((ServerBootstrap)(new ServerBootstrap()).channel(var4)).childHandler(new ChannelInitializer<Channel>() {
            protected void initChannel(Channel var1) throws Exception {
               try {
                  var1.config().setOption(ChannelOption.TCP_NODELAY, true);
               } catch (ChannelException var3) {
               }

               var1.pipeline().addLast("timeout", new ReadTimeoutHandler(30)).addLast("legacy_query", new LegacyQueryHandler(ServerConnectionListener.this)).addLast("splitter", new Varint21FrameDecoder()).addLast("decoder", new PacketDecoder(PacketFlow.SERVERBOUND)).addLast("prepender", new Varint21LengthFieldPrepender()).addLast("encoder", new PacketEncoder(PacketFlow.CLIENTBOUND));
               Connection var2 = new Connection(PacketFlow.SERVERBOUND);
               ServerConnectionListener.this.connections.add(var2);
               var1.pipeline().addLast("packet_handler", var2);
               var2.setListener(new ServerHandshakePacketListenerImpl(ServerConnectionListener.this.server, var2));
            }
         }).group((EventLoopGroup)var5.get()).localAddress(var1, var2)).bind().syncUninterruptibly());
      }
   }

   public SocketAddress startMemoryChannel() {
      ChannelFuture var1;
      synchronized(this.channels) {
         var1 = ((ServerBootstrap)((ServerBootstrap)(new ServerBootstrap()).channel(LocalServerChannel.class)).childHandler(new ChannelInitializer<Channel>() {
            protected void initChannel(Channel var1) throws Exception {
               Connection var2 = new Connection(PacketFlow.SERVERBOUND);
               var2.setListener(new MemoryServerHandshakePacketListenerImpl(ServerConnectionListener.this.server, var2));
               ServerConnectionListener.this.connections.add(var2);
               var1.pipeline().addLast("packet_handler", var2);
            }
         }).group((EventLoopGroup)SERVER_EVENT_GROUP.get()).localAddress(LocalAddress.ANY)).bind().syncUninterruptibly();
         this.channels.add(var1);
      }

      return var1.channel().localAddress();
   }

   public void stop() {
      this.running = false;
      Iterator var1 = this.channels.iterator();

      while(var1.hasNext()) {
         ChannelFuture var2 = (ChannelFuture)var1.next();

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

         while(true) {
            while(true) {
               Connection var3;
               do {
                  if (!var2.hasNext()) {
                     return;
                  }

                  var3 = (Connection)var2.next();
               } while(var3.isConnecting());

               if (var3.isConnected()) {
                  try {
                     var3.tick();
                  } catch (Exception var8) {
                     if (var3.isMemoryConnection()) {
                        CrashReport var10 = CrashReport.forThrowable(var8, "Ticking memory connection");
                        CrashReportCategory var6 = var10.addCategory("Ticking connection");
                        var6.setDetail("Connection", var3::toString);
                        throw new ReportedException(var10);
                     }

                     LOGGER.warn("Failed to handle packet for {}", var3.getRemoteAddress(), var8);
                     TextComponent var5 = new TextComponent("Internal server error");
                     var3.send(new ClientboundDisconnectPacket(var5), (var2x) -> {
                        var3.disconnect(var5);
                     });
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
}
