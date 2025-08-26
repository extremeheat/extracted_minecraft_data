package net.minecraft.server.jsonrpc;

import com.google.common.collect.Sets;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.logging.LogUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.jsonrpc.internalapi.MinecraftAllowListService;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.server.jsonrpc.internalapi.MinecraftBanListService;
import net.minecraft.server.jsonrpc.internalapi.MinecraftGameRuleService;
import net.minecraft.server.jsonrpc.internalapi.MinecraftOperatorListService;
import net.minecraft.server.jsonrpc.internalapi.MinecraftPlayerListService;
import net.minecraft.server.jsonrpc.internalapi.MinecraftServerSettingsService;
import net.minecraft.server.jsonrpc.internalapi.MinecraftServerStateService;
import net.minecraft.server.jsonrpc.websocket.JsonToWebSocketEncoder;
import net.minecraft.server.jsonrpc.websocket.WebSocketToJsonCodec;
import org.slf4j.Logger;

public class ManagementServer {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final HostAndPort hostAndPort;
   private final Set<Connection> connections = Sets.newIdentityHashSet();

   public ManagementServer(HostAndPort var1) {
      super();
      this.hostAndPort = var1;
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

   public void start(DedicatedServer var1) {
      final JsonRpcLogger var2 = new JsonRpcLogger();
      final MinecraftApi var3 = getApiService(var1, var2);
      var1.notificationManager().registerService(new JsonRpcNotificationService(var3, this));
      LOGGER.info("Json-RPC Management connection listening on " + String.valueOf(this.hostAndPort));
      ((ServerBootstrap)((ServerBootstrap)((ServerBootstrap)(new ServerBootstrap()).handler(new LoggingHandler(LogLevel.DEBUG))).channel(NioServerSocketChannel.class)).childHandler(new ChannelInitializer<Channel>() {
         protected void initChannel(Channel var1) {
            try {
               var1.config().setOption(ChannelOption.TCP_NODELAY, true);
            } catch (ChannelException var3x) {
            }

            var1.pipeline().addLast(new ChannelHandler[]{new HttpServerCodec()}).addLast(new ChannelHandler[]{new HttpObjectAggregator(65536)}).addLast(new ChannelHandler[]{new WebSocketServerProtocolHandler("/")}).addLast(new ChannelHandler[]{new WebSocketToJsonCodec()}).addLast(new ChannelHandler[]{new JsonToWebSocketEncoder()}).addLast(new ChannelHandler[]{new Connection(var1, ManagementServer.this, var3, var2)});
         }
      }).group(new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Management server IO #%d").setDaemon(true).build())).localAddress(this.hostAndPort.getHost(), this.hostAndPort.getPort())).bind().syncUninterruptibly();
   }

   private static MinecraftApi getApiService(DedicatedServer var0, JsonRpcLogger var1) {
      MinecraftAllowListService var2 = new MinecraftAllowListService(var0, var1);
      MinecraftBanListService var3 = new MinecraftBanListService(var0, var1);
      MinecraftPlayerListService var4 = new MinecraftPlayerListService(var0, var1);
      MinecraftGameRuleService var5 = new MinecraftGameRuleService(var0, var1);
      MinecraftOperatorListService var6 = new MinecraftOperatorListService(var0, var1);
      MinecraftServerSettingsService var7 = new MinecraftServerSettingsService(var0, var1);
      MinecraftServerStateService var8 = new MinecraftServerStateService(var0, var1);
      return new MinecraftApi(var2, var3, var4, var5, var6, var7, var8, var0);
   }

   public void tick() {
      this.forEachConnection(Connection::tick);
   }

   void forEachConnection(Consumer<Connection> var1) {
      synchronized(this.connections) {
         this.connections.forEach(var1);
      }
   }
}
