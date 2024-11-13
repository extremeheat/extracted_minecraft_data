package net.minecraft.network;

import com.google.common.base.Suppliers;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.logging.LogUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.flow.FlowControlHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.BundlerInfo;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.handshake.ClientIntent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.handshake.HandshakeProtocols;
import net.minecraft.network.protocol.handshake.ServerHandshakePacketListener;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.network.protocol.login.LoginProtocols;
import net.minecraft.network.protocol.status.ClientStatusPacketListener;
import net.minecraft.network.protocol.status.StatusProtocols;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.util.Mth;
import net.minecraft.util.debugchart.LocalSampleLogger;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Connection extends SimpleChannelInboundHandler<Packet<?>> {
   private static final float AVERAGE_PACKETS_SMOOTHING = 0.75F;
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Marker ROOT_MARKER = MarkerFactory.getMarker("NETWORK");
   public static final Marker PACKET_MARKER = (Marker)Util.make(MarkerFactory.getMarker("NETWORK_PACKETS"), (var0) -> var0.add(ROOT_MARKER));
   public static final Marker PACKET_RECEIVED_MARKER = (Marker)Util.make(MarkerFactory.getMarker("PACKET_RECEIVED"), (var0) -> var0.add(PACKET_MARKER));
   public static final Marker PACKET_SENT_MARKER = (Marker)Util.make(MarkerFactory.getMarker("PACKET_SENT"), (var0) -> var0.add(PACKET_MARKER));
   public static final Supplier<NioEventLoopGroup> NETWORK_WORKER_GROUP = Suppliers.memoize(() -> new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Client IO #%d").setDaemon(true).build()));
   public static final Supplier<EpollEventLoopGroup> NETWORK_EPOLL_WORKER_GROUP = Suppliers.memoize(() -> new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build()));
   public static final Supplier<DefaultEventLoopGroup> LOCAL_WORKER_GROUP = Suppliers.memoize(() -> new DefaultEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Local Client IO #%d").setDaemon(true).build()));
   private static final ProtocolInfo<ServerHandshakePacketListener> INITIAL_PROTOCOL;
   private final PacketFlow receiving;
   private volatile boolean sendLoginDisconnect = true;
   private final Queue<Consumer<Connection>> pendingActions = Queues.newConcurrentLinkedQueue();
   private Channel channel;
   private SocketAddress address;
   @Nullable
   private volatile PacketListener disconnectListener;
   @Nullable
   private volatile PacketListener packetListener;
   @Nullable
   private DisconnectionDetails disconnectionDetails;
   private boolean encrypted;
   private boolean disconnectionHandled;
   private int receivedPackets;
   private int sentPackets;
   private float averageReceivedPackets;
   private float averageSentPackets;
   private int tickCount;
   private boolean handlingFault;
   @Nullable
   private volatile DisconnectionDetails delayedDisconnect;
   @Nullable
   BandwidthDebugMonitor bandwidthDebugMonitor;

   public Connection(PacketFlow var1) {
      super();
      this.receiving = var1;
   }

   public void channelActive(ChannelHandlerContext var1) throws Exception {
      super.channelActive(var1);
      this.channel = var1.channel();
      this.address = this.channel.remoteAddress();
      if (this.delayedDisconnect != null) {
         this.disconnect(this.delayedDisconnect);
      }

   }

   public void channelInactive(ChannelHandlerContext var1) {
      this.disconnect((Component)Component.translatable("disconnect.endOfStream"));
   }

   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) {
      if (var2 instanceof SkipPacketException) {
         LOGGER.debug("Skipping packet due to errors", var2.getCause());
      } else {
         boolean var3 = !this.handlingFault;
         this.handlingFault = true;
         if (this.channel.isOpen()) {
            if (var2 instanceof TimeoutException) {
               LOGGER.debug("Timeout", var2);
               this.disconnect((Component)Component.translatable("disconnect.timeout"));
            } else {
               MutableComponent var4 = Component.translatable("disconnect.genericReason", "Internal Exception: " + String.valueOf(var2));
               PacketListener var6 = this.packetListener;
               DisconnectionDetails var5;
               if (var6 != null) {
                  var5 = var6.createDisconnectionInfo(var4, var2);
               } else {
                  var5 = new DisconnectionDetails(var4);
               }

               if (var3) {
                  LOGGER.debug("Failed to sent packet", var2);
                  if (this.getSending() == PacketFlow.CLIENTBOUND) {
                     Object var7 = this.sendLoginDisconnect ? new ClientboundLoginDisconnectPacket(var4) : new ClientboundDisconnectPacket(var4);
                     this.send((Packet)var7, PacketSendListener.thenRun(() -> this.disconnect(var5)));
                  } else {
                     this.disconnect(var5);
                  }

                  this.setReadOnly();
               } else {
                  LOGGER.debug("Double fault", var2);
                  this.disconnect(var5);
               }
            }

         }
      }
   }

   protected void channelRead0(ChannelHandlerContext var1, Packet<?> var2) {
      if (this.channel.isOpen()) {
         PacketListener var3 = this.packetListener;
         if (var3 == null) {
            throw new IllegalStateException("Received a packet before the packet listener was initialized");
         } else {
            if (var3.shouldHandleMessage(var2)) {
               try {
                  genericsFtw(var2, var3);
               } catch (RunningOnDifferentThreadException var5) {
               } catch (RejectedExecutionException var6) {
                  this.disconnect((Component)Component.translatable("multiplayer.disconnect.server_shutdown"));
               } catch (ClassCastException var7) {
                  LOGGER.error("Received {} that couldn't be processed", var2.getClass(), var7);
                  this.disconnect((Component)Component.translatable("multiplayer.disconnect.invalid_packet"));
               }

               ++this.receivedPackets;
            }

         }
      }
   }

   private static <T extends PacketListener> void genericsFtw(Packet<T> var0, PacketListener var1) {
      var0.handle(var1);
   }

   private void validateListener(ProtocolInfo<?> var1, PacketListener var2) {
      Validate.notNull(var2, "packetListener", new Object[0]);
      PacketFlow var3 = var2.flow();
      if (var3 != this.receiving) {
         String var5 = String.valueOf(this.receiving);
         throw new IllegalStateException("Trying to set listener for wrong side: connection is " + var5 + ", but listener is " + String.valueOf(var3));
      } else {
         ConnectionProtocol var4 = var2.protocol();
         if (var1.id() != var4) {
            String var10002 = String.valueOf(var4);
            throw new IllegalStateException("Listener protocol (" + var10002 + ") does not match requested one " + String.valueOf(var1));
         }
      }
   }

   private static void syncAfterConfigurationChange(ChannelFuture var0) {
      try {
         var0.syncUninterruptibly();
      } catch (Exception var2) {
         if (var2 instanceof ClosedChannelException) {
            LOGGER.info("Connection closed during protocol change");
         } else {
            throw var2;
         }
      }
   }

   public <T extends PacketListener> void setupInboundProtocol(ProtocolInfo<T> var1, T var2) {
      this.validateListener(var1, var2);
      if (var1.flow() != this.getReceiving()) {
         throw new IllegalStateException("Invalid inbound protocol: " + String.valueOf(var1.id()));
      } else {
         this.packetListener = var2;
         this.disconnectListener = null;
         UnconfiguredPipelineHandler.InboundConfigurationTask var3 = UnconfiguredPipelineHandler.setupInboundProtocol(var1);
         BundlerInfo var4 = var1.bundlerInfo();
         if (var4 != null) {
            PacketBundlePacker var5 = new PacketBundlePacker(var4);
            var3 = var3.andThen((var1x) -> var1x.pipeline().addAfter("decoder", "bundler", var5));
         }

         syncAfterConfigurationChange(this.channel.writeAndFlush(var3));
      }
   }

   public void setupOutboundProtocol(ProtocolInfo<?> var1) {
      if (var1.flow() != this.getSending()) {
         throw new IllegalStateException("Invalid outbound protocol: " + String.valueOf(var1.id()));
      } else {
         UnconfiguredPipelineHandler.OutboundConfigurationTask var2 = UnconfiguredPipelineHandler.setupOutboundProtocol(var1);
         BundlerInfo var3 = var1.bundlerInfo();
         if (var3 != null) {
            PacketBundleUnpacker var4 = new PacketBundleUnpacker(var3);
            var2 = var2.andThen((var1x) -> var1x.pipeline().addAfter("encoder", "unbundler", var4));
         }

         boolean var5 = var1.id() == ConnectionProtocol.LOGIN;
         syncAfterConfigurationChange(this.channel.writeAndFlush(var2.andThen((var2x) -> this.sendLoginDisconnect = var5)));
      }
   }

   public void setListenerForServerboundHandshake(PacketListener var1) {
      if (this.packetListener != null) {
         throw new IllegalStateException("Listener already set");
      } else if (this.receiving == PacketFlow.SERVERBOUND && var1.flow() == PacketFlow.SERVERBOUND && var1.protocol() == INITIAL_PROTOCOL.id()) {
         this.packetListener = var1;
      } else {
         throw new IllegalStateException("Invalid initial listener");
      }
   }

   public void initiateServerboundStatusConnection(String var1, int var2, ClientStatusPacketListener var3) {
      this.initiateServerboundConnection(var1, var2, StatusProtocols.SERVERBOUND, StatusProtocols.CLIENTBOUND, var3, ClientIntent.STATUS);
   }

   public void initiateServerboundPlayConnection(String var1, int var2, ClientLoginPacketListener var3) {
      this.initiateServerboundConnection(var1, var2, LoginProtocols.SERVERBOUND, LoginProtocols.CLIENTBOUND, var3, ClientIntent.LOGIN);
   }

   public <S extends ServerboundPacketListener, C extends ClientboundPacketListener> void initiateServerboundPlayConnection(String var1, int var2, ProtocolInfo<S> var3, ProtocolInfo<C> var4, C var5, boolean var6) {
      this.initiateServerboundConnection(var1, var2, var3, var4, var5, var6 ? ClientIntent.TRANSFER : ClientIntent.LOGIN);
   }

   private <S extends ServerboundPacketListener, C extends ClientboundPacketListener> void initiateServerboundConnection(String var1, int var2, ProtocolInfo<S> var3, ProtocolInfo<C> var4, C var5, ClientIntent var6) {
      if (var3.id() != var4.id()) {
         throw new IllegalStateException("Mismatched initial protocols");
      } else {
         this.disconnectListener = var5;
         this.runOnceConnected((var7) -> {
            this.setupInboundProtocol(var4, var5);
            var7.sendPacket(new ClientIntentionPacket(SharedConstants.getCurrentVersion().getProtocolVersion(), var1, var2, var6), (PacketSendListener)null, true);
            this.setupOutboundProtocol(var3);
         });
      }
   }

   public void send(Packet<?> var1) {
      this.send(var1, (PacketSendListener)null);
   }

   public void send(Packet<?> var1, @Nullable PacketSendListener var2) {
      this.send(var1, var2, true);
   }

   public void send(Packet<?> var1, @Nullable PacketSendListener var2, boolean var3) {
      if (this.isConnected()) {
         this.flushQueue();
         this.sendPacket(var1, var2, var3);
      } else {
         this.pendingActions.add((Consumer)(var3x) -> var3x.sendPacket(var1, var2, var3));
      }

   }

   public void runOnceConnected(Consumer<Connection> var1) {
      if (this.isConnected()) {
         this.flushQueue();
         var1.accept(this);
      } else {
         this.pendingActions.add(var1);
      }

   }

   private void sendPacket(Packet<?> var1, @Nullable PacketSendListener var2, boolean var3) {
      ++this.sentPackets;
      if (this.channel.eventLoop().inEventLoop()) {
         this.doSendPacket(var1, var2, var3);
      } else {
         this.channel.eventLoop().execute(() -> this.doSendPacket(var1, var2, var3));
      }

   }

   private void doSendPacket(Packet<?> var1, @Nullable PacketSendListener var2, boolean var3) {
      ChannelFuture var4 = var3 ? this.channel.writeAndFlush(var1) : this.channel.write(var1);
      if (var2 != null) {
         var4.addListener((var2x) -> {
            if (var2x.isSuccess()) {
               var2.onSuccess();
            } else {
               Packet var3 = var2.onFailure();
               if (var3 != null) {
                  ChannelFuture var4 = this.channel.writeAndFlush(var3);
                  var4.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
               }
            }

         });
      }

      var4.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
   }

   public void flushChannel() {
      if (this.isConnected()) {
         this.flush();
      } else {
         this.pendingActions.add(Connection::flush);
      }

   }

   private void flush() {
      if (this.channel.eventLoop().inEventLoop()) {
         this.channel.flush();
      } else {
         this.channel.eventLoop().execute(() -> this.channel.flush());
      }

   }

   private void flushQueue() {
      if (this.channel != null && this.channel.isOpen()) {
         synchronized(this.pendingActions) {
            Consumer var2;
            while((var2 = (Consumer)this.pendingActions.poll()) != null) {
               var2.accept(this);
            }

         }
      }
   }

   public void tick() {
      this.flushQueue();
      PacketListener var2 = this.packetListener;
      if (var2 instanceof TickablePacketListener var1) {
         var1.tick();
      }

      if (!this.isConnected() && !this.disconnectionHandled) {
         this.handleDisconnection();
      }

      if (this.channel != null) {
         this.channel.flush();
      }

      if (this.tickCount++ % 20 == 0) {
         this.tickSecond();
      }

      if (this.bandwidthDebugMonitor != null) {
         this.bandwidthDebugMonitor.tick();
      }

   }

   protected void tickSecond() {
      this.averageSentPackets = Mth.lerp(0.75F, (float)this.sentPackets, this.averageSentPackets);
      this.averageReceivedPackets = Mth.lerp(0.75F, (float)this.receivedPackets, this.averageReceivedPackets);
      this.sentPackets = 0;
      this.receivedPackets = 0;
   }

   public SocketAddress getRemoteAddress() {
      return this.address;
   }

   public String getLoggableAddress(boolean var1) {
      if (this.address == null) {
         return "local";
      } else {
         return var1 ? this.address.toString() : "IP hidden";
      }
   }

   public void disconnect(Component var1) {
      this.disconnect(new DisconnectionDetails(var1));
   }

   public void disconnect(DisconnectionDetails var1) {
      if (this.channel == null) {
         this.delayedDisconnect = var1;
      }

      if (this.isConnected()) {
         this.channel.close().awaitUninterruptibly();
         this.disconnectionDetails = var1;
      }

   }

   public boolean isMemoryConnection() {
      return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
   }

   public PacketFlow getReceiving() {
      return this.receiving;
   }

   public PacketFlow getSending() {
      return this.receiving.getOpposite();
   }

   public static Connection connectToServer(InetSocketAddress var0, boolean var1, @Nullable LocalSampleLogger var2) {
      Connection var3 = new Connection(PacketFlow.CLIENTBOUND);
      if (var2 != null) {
         var3.setBandwidthLogger(var2);
      }

      ChannelFuture var4 = connect(var0, var1, var3);
      var4.syncUninterruptibly();
      return var3;
   }

   public static ChannelFuture connect(InetSocketAddress var0, boolean var1, final Connection var2) {
      Class var3;
      EventLoopGroup var4;
      if (Epoll.isAvailable() && var1) {
         var3 = EpollSocketChannel.class;
         var4 = (EventLoopGroup)NETWORK_EPOLL_WORKER_GROUP.get();
      } else {
         var3 = NioSocketChannel.class;
         var4 = (EventLoopGroup)NETWORK_WORKER_GROUP.get();
      }

      return ((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).group(var4)).handler(new ChannelInitializer<Channel>() {
         protected void initChannel(Channel var1) {
            try {
               var1.config().setOption(ChannelOption.TCP_NODELAY, true);
            } catch (ChannelException var3) {
            }

            ChannelPipeline var2x = var1.pipeline().addLast("timeout", new ReadTimeoutHandler(30));
            Connection.configureSerialization(var2x, PacketFlow.CLIENTBOUND, false, var2.bandwidthDebugMonitor);
            var2.configurePacketHandler(var2x);
         }
      })).channel(var3)).connect(var0.getAddress(), var0.getPort());
   }

   private static String outboundHandlerName(boolean var0) {
      return var0 ? "encoder" : "outbound_config";
   }

   private static String inboundHandlerName(boolean var0) {
      return var0 ? "decoder" : "inbound_config";
   }

   public void configurePacketHandler(ChannelPipeline var1) {
      var1.addLast("hackfix", new ChannelOutboundHandlerAdapter() {
         public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
            super.write(var1, var2, var3);
         }
      }).addLast("packet_handler", this);
   }

   public static void configureSerialization(ChannelPipeline var0, PacketFlow var1, boolean var2, @Nullable BandwidthDebugMonitor var3) {
      PacketFlow var4 = var1.getOpposite();
      boolean var5 = var1 == PacketFlow.SERVERBOUND;
      boolean var6 = var4 == PacketFlow.SERVERBOUND;
      var0.addLast("splitter", createFrameDecoder(var3, var2)).addLast(new ChannelHandler[]{new FlowControlHandler()}).addLast(inboundHandlerName(var5), (ChannelHandler)(var5 ? new PacketDecoder(INITIAL_PROTOCOL) : new UnconfiguredPipelineHandler.Inbound())).addLast("prepender", createFrameEncoder(var2)).addLast(outboundHandlerName(var6), (ChannelHandler)(var6 ? new PacketEncoder(INITIAL_PROTOCOL) : new UnconfiguredPipelineHandler.Outbound()));
   }

   private static ChannelOutboundHandler createFrameEncoder(boolean var0) {
      return (ChannelOutboundHandler)(var0 ? new NoOpFrameEncoder() : new Varint21LengthFieldPrepender());
   }

   private static ChannelInboundHandler createFrameDecoder(@Nullable BandwidthDebugMonitor var0, boolean var1) {
      if (!var1) {
         return new Varint21FrameDecoder(var0);
      } else {
         return (ChannelInboundHandler)(var0 != null ? new MonitorFrameDecoder(var0) : new NoOpFrameDecoder());
      }
   }

   public static void configureInMemoryPipeline(ChannelPipeline var0, PacketFlow var1) {
      configureSerialization(var0, var1, true, (BandwidthDebugMonitor)null);
   }

   public static Connection connectToLocalServer(SocketAddress var0) {
      final Connection var1 = new Connection(PacketFlow.CLIENTBOUND);
      ((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).group((EventLoopGroup)LOCAL_WORKER_GROUP.get())).handler(new ChannelInitializer<Channel>() {
         protected void initChannel(Channel var1x) {
            ChannelPipeline var2 = var1x.pipeline();
            Connection.configureInMemoryPipeline(var2, PacketFlow.CLIENTBOUND);
            var1.configurePacketHandler(var2);
         }
      })).channel(LocalChannel.class)).connect(var0).syncUninterruptibly();
      return var1;
   }

   public void setEncryptionKey(Cipher var1, Cipher var2) {
      this.encrypted = true;
      this.channel.pipeline().addBefore("splitter", "decrypt", new CipherDecoder(var1));
      this.channel.pipeline().addBefore("prepender", "encrypt", new CipherEncoder(var2));
   }

   public boolean isEncrypted() {
      return this.encrypted;
   }

   public boolean isConnected() {
      return this.channel != null && this.channel.isOpen();
   }

   public boolean isConnecting() {
      return this.channel == null;
   }

   @Nullable
   public PacketListener getPacketListener() {
      return this.packetListener;
   }

   @Nullable
   public DisconnectionDetails getDisconnectionDetails() {
      return this.disconnectionDetails;
   }

   public void setReadOnly() {
      if (this.channel != null) {
         this.channel.config().setAutoRead(false);
      }

   }

   public void setupCompression(int var1, boolean var2) {
      if (var1 >= 0) {
         ChannelHandler var4 = this.channel.pipeline().get("decompress");
         if (var4 instanceof CompressionDecoder) {
            CompressionDecoder var3 = (CompressionDecoder)var4;
            var3.setThreshold(var1, var2);
         } else {
            this.channel.pipeline().addAfter("splitter", "decompress", new CompressionDecoder(var1, var2));
         }

         var4 = this.channel.pipeline().get("compress");
         if (var4 instanceof CompressionEncoder) {
            CompressionEncoder var5 = (CompressionEncoder)var4;
            var5.setThreshold(var1);
         } else {
            this.channel.pipeline().addAfter("prepender", "compress", new CompressionEncoder(var1));
         }
      } else {
         if (this.channel.pipeline().get("decompress") instanceof CompressionDecoder) {
            this.channel.pipeline().remove("decompress");
         }

         if (this.channel.pipeline().get("compress") instanceof CompressionEncoder) {
            this.channel.pipeline().remove("compress");
         }
      }

   }

   public void handleDisconnection() {
      if (this.channel != null && !this.channel.isOpen()) {
         if (this.disconnectionHandled) {
            LOGGER.warn("handleDisconnection() called twice");
         } else {
            this.disconnectionHandled = true;
            PacketListener var1 = this.getPacketListener();
            PacketListener var2 = var1 != null ? var1 : this.disconnectListener;
            if (var2 != null) {
               DisconnectionDetails var3 = (DisconnectionDetails)Objects.requireNonNullElseGet(this.getDisconnectionDetails(), () -> new DisconnectionDetails(Component.translatable("multiplayer.disconnect.generic")));
               var2.onDisconnect(var3);
            }

         }
      }
   }

   public float getAverageReceivedPackets() {
      return this.averageReceivedPackets;
   }

   public float getAverageSentPackets() {
      return this.averageSentPackets;
   }

   public void setBandwidthLogger(LocalSampleLogger var1) {
      this.bandwidthDebugMonitor = new BandwidthDebugMonitor(var1);
   }

   // $FF: synthetic method
   protected void channelRead0(final ChannelHandlerContext var1, final Object var2) throws Exception {
      this.channelRead0(var1, (Packet)var2);
   }

   static {
      INITIAL_PROTOCOL = HandshakeProtocols.SERVERBOUND;
   }
}
