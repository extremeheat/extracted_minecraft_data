package net.minecraft.server.jsonrpc;

import com.google.common.collect.Sets;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.logging.LogUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.server.jsonrpc.security.AuthenticationHandler;
import net.minecraft.server.jsonrpc.websocket.JsonToWebSocketEncoder;
import net.minecraft.server.jsonrpc.websocket.WebSocketToJsonCodec;
import org.slf4j.Logger;

public class ManagementServer {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final HostAndPort hostAndPort;
   final AuthenticationHandler authenticationHandler;
   @Nullable
   private Channel serverChannel;
   private final NioEventLoopGroup nioEventLoopGroup;
   private final Set<Connection> connections = Sets.newIdentityHashSet();

   public ManagementServer(HostAndPort var1, AuthenticationHandler var2) {
      super();
      this.hostAndPort = var1;
      this.authenticationHandler = var2;
      this.nioEventLoopGroup = new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Management server IO #%d").setDaemon(true).build());
   }

   public ManagementServer(HostAndPort var1, AuthenticationHandler var2, NioEventLoopGroup var3) {
      super();
      this.hostAndPort = var1;
      this.authenticationHandler = var2;
      this.nioEventLoopGroup = var3;
   }

   public void onConnected(Connection var1) {
      synchronized(this.connections) {
         this.connections.add(var1);
      }
   }

   public void onDisconnected(Connection var1) {
      synchronized(this.connections) {
         this.connections.remove(var1);
      }
   }

   public void startWithoutTls(MinecraftApi var1) {
      this.start(var1, (SslContext)null);
   }

   public void startWithTls(MinecraftApi var1, SslContext var2) {
      this.start(var1, var2);
   }

   private void start(final MinecraftApi var1, @Nullable final SslContext var2) {
      final JsonRpcLogger var3 = new JsonRpcLogger();
      ChannelFuture var4 = ((ServerBootstrap)((ServerBootstrap)((ServerBootstrap)(new ServerBootstrap()).handler(new LoggingHandler(LogLevel.DEBUG))).channel(NioServerSocketChannel.class)).childHandler(new ChannelInitializer<Channel>() {
         protected void initChannel(Channel var1x) {
            try {
               var1x.config().setOption(ChannelOption.TCP_NODELAY, true);
            } catch (ChannelException var3x) {
            }

            ChannelPipeline var2x = var1x.pipeline();
            if (var2 != null) {
               var2x.addLast(new ChannelHandler[]{var2.newHandler(var1x.alloc())});
            }

            var2x.addLast(new ChannelHandler[]{new HttpServerCodec()}).addLast(new ChannelHandler[]{new HttpObjectAggregator(65536)}).addLast(new ChannelHandler[]{ManagementServer.this.authenticationHandler}).addLast(new ChannelHandler[]{new WebSocketServerProtocolHandler("/")}).addLast(new ChannelHandler[]{new WebSocketToJsonCodec()}).addLast(new ChannelHandler[]{new JsonToWebSocketEncoder()}).addLast(new ChannelHandler[]{new Connection(var1x, ManagementServer.this, var1, var3)});
         }
      }).group(this.nioEventLoopGroup).localAddress(this.hostAndPort.getHost(), this.hostAndPort.getPort())).bind();
      this.serverChannel = var4.channel();
      var4.syncUninterruptibly();
      LOGGER.info("Json-RPC Management connection listening on {}:{}", this.hostAndPort.getHost(), this.getPort());
   }

   public void stop(boolean var1) throws InterruptedException {
      if (this.serverChannel != null) {
         this.serverChannel.close().sync();
         this.serverChannel = null;
      }

      this.connections.clear();
      if (var1) {
         this.nioEventLoopGroup.shutdownGracefully().sync();
      }

   }

   public void tick() {
      this.forEachConnection(Connection::tick);
   }

   public int getPort() {
      return this.serverChannel != null ? ((InetSocketAddress)this.serverChannel.localAddress()).getPort() : this.hostAndPort.getPort();
   }

   void forEachConnection(Consumer<Connection> var1) {
      synchronized(this.connections) {
         this.connections.forEach(var1);
      }
   }
}
