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
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.server.jsonrpc.security.AuthenticationHandler;
import net.minecraft.server.jsonrpc.websocket.JsonToWebSocketEncoder;
import net.minecraft.server.jsonrpc.websocket.WebSocketToJsonCodec;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class ManagementServer {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final HostAndPort hostAndPort;
   private final AuthenticationHandler authenticationHandler;
   private @Nullable Channel serverChannel;
   private final NioEventLoopGroup nioEventLoopGroup;
   private final Set<Connection> connections = Sets.newIdentityHashSet();

   public ManagementServer(final HostAndPort hostAndPort, final AuthenticationHandler authenticationHandler) {
      super();
      this.hostAndPort = hostAndPort;
      this.authenticationHandler = authenticationHandler;
      this.nioEventLoopGroup = new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Management server IO #%d").setDaemon(true).build());
   }

   public ManagementServer(final HostAndPort hostAndPort, final AuthenticationHandler authenticationHandler, final NioEventLoopGroup nioEventLoopGroup) {
      super();
      this.hostAndPort = hostAndPort;
      this.authenticationHandler = authenticationHandler;
      this.nioEventLoopGroup = nioEventLoopGroup;
   }

   public void onConnected(final Connection connection) {
      synchronized(this.connections) {
         this.connections.add(connection);
      }
   }

   public void onDisconnected(final Connection connection) {
      synchronized(this.connections) {
         this.connections.remove(connection);
      }
   }

   public void startWithoutTls(final MinecraftApi minecraftApi) {
      this.start(minecraftApi, (SslContext)null);
   }

   public void startWithTls(final MinecraftApi minecraftApi, final SslContext sslContext) {
      this.start(minecraftApi, sslContext);
   }

   private void start(final MinecraftApi minecraftApi, final @Nullable SslContext sslContext) {
      final JsonRpcLogger jsonrpcLogger = new JsonRpcLogger();
      ChannelFuture channel = ((ServerBootstrap)((ServerBootstrap)((ServerBootstrap)(new ServerBootstrap()).handler(new LoggingHandler(LogLevel.DEBUG))).channel(NioServerSocketChannel.class)).childHandler(new ChannelInitializer<Channel>() {
         {
            Objects.requireNonNull(ManagementServer.this);
         }

         protected void initChannel(final Channel channel) {
            try {
               channel.config().setOption(ChannelOption.TCP_NODELAY, true);
            } catch (ChannelException var3) {
            }

            ChannelPipeline pipeline = channel.pipeline();
            if (sslContext != null) {
               pipeline.addLast(new ChannelHandler[]{sslContext.newHandler(channel.alloc())});
            }

            pipeline.addLast(new ChannelHandler[]{new HttpServerCodec()}).addLast(new ChannelHandler[]{new HttpObjectAggregator(65536)}).addLast(new ChannelHandler[]{ManagementServer.this.authenticationHandler}).addLast(new ChannelHandler[]{new WebSocketServerProtocolHandler("/")}).addLast(new ChannelHandler[]{new WebSocketFrameAggregator(65536)}).addLast(new ChannelHandler[]{new WebSocketToJsonCodec()}).addLast(new ChannelHandler[]{new JsonToWebSocketEncoder()}).addLast(new ChannelHandler[]{new Connection(channel, ManagementServer.this, minecraftApi, jsonrpcLogger)});
         }
      }).group(this.nioEventLoopGroup).localAddress(this.hostAndPort.getHost(), this.hostAndPort.getPort())).bind();
      this.serverChannel = channel.channel();
      channel.syncUninterruptibly();
      LOGGER.info("Json-RPC Management connection listening on {}:{}", this.hostAndPort.getHost(), this.getPort());
   }

   public void stop(final boolean closeNioEventLoopGroup) throws InterruptedException {
      if (this.serverChannel != null) {
         this.serverChannel.close().sync();
         this.serverChannel = null;
      }

      this.connections.clear();
      if (closeNioEventLoopGroup) {
         this.nioEventLoopGroup.shutdownGracefully().sync();
      }

   }

   public void tick() {
      this.forEachConnection(Connection::tick);
   }

   public int getPort() {
      return this.serverChannel != null ? ((InetSocketAddress)this.serverChannel.localAddress()).getPort() : this.hostAndPort.getPort();
   }

   void forEachConnection(final Consumer<Connection> action) {
      synchronized(this.connections) {
         this.connections.forEach(action);
      }
   }
}
