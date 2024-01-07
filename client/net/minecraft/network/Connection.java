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
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
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
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
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
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.handshake.ClientIntent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.network.protocol.status.ClientStatusPacketListener;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.util.Mth;
import net.minecraft.util.SampleLogger;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Connection extends SimpleChannelInboundHandler<Packet<?>> {
   private static final float AVERAGE_PACKETS_SMOOTHING = 0.75F;
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Marker ROOT_MARKER = MarkerFactory.getMarker("NETWORK");
   public static final Marker PACKET_MARKER = Util.make(MarkerFactory.getMarker("NETWORK_PACKETS"), var0 -> var0.add(ROOT_MARKER));
   public static final Marker PACKET_RECEIVED_MARKER = Util.make(MarkerFactory.getMarker("PACKET_RECEIVED"), var0 -> var0.add(PACKET_MARKER));
   public static final Marker PACKET_SENT_MARKER = Util.make(MarkerFactory.getMarker("PACKET_SENT"), var0 -> var0.add(PACKET_MARKER));
   public static final AttributeKey<ConnectionProtocol.CodecData<?>> ATTRIBUTE_SERVERBOUND_PROTOCOL = AttributeKey.valueOf("serverbound_protocol");
   public static final AttributeKey<ConnectionProtocol.CodecData<?>> ATTRIBUTE_CLIENTBOUND_PROTOCOL = AttributeKey.valueOf("clientbound_protocol");
   public static final Supplier<NioEventLoopGroup> NETWORK_WORKER_GROUP = Suppliers.memoize(
      () -> new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build())
   );
   public static final Supplier<EpollEventLoopGroup> NETWORK_EPOLL_WORKER_GROUP = Suppliers.memoize(
      () -> new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build())
   );
   public static final Supplier<DefaultEventLoopGroup> LOCAL_WORKER_GROUP = Suppliers.memoize(
      () -> new DefaultEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build())
   );
   private final PacketFlow receiving;
   private final Queue<Consumer<Connection>> pendingActions = Queues.newConcurrentLinkedQueue();
   private Channel channel;
   private SocketAddress address;
   @Nullable
   private volatile PacketListener disconnectListener;
   @Nullable
   private volatile PacketListener packetListener;
   @Nullable
   private Component disconnectedReason;
   private boolean encrypted;
   private boolean disconnectionHandled;
   private int receivedPackets;
   private int sentPackets;
   private float averageReceivedPackets;
   private float averageSentPackets;
   private int tickCount;
   private boolean handlingFault;
   @Nullable
   private volatile Component delayedDisconnect;
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

   public static void setInitialProtocolAttributes(Channel var0) {
      var0.attr(ATTRIBUTE_SERVERBOUND_PROTOCOL).set(ConnectionProtocol.HANDSHAKING.codec(PacketFlow.SERVERBOUND));
      var0.attr(ATTRIBUTE_CLIENTBOUND_PROTOCOL).set(ConnectionProtocol.HANDSHAKING.codec(PacketFlow.CLIENTBOUND));
   }

   public void channelInactive(ChannelHandlerContext var1) {
      this.disconnect(Component.translatable("disconnect.endOfStream"));
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
               this.disconnect(Component.translatable("disconnect.timeout"));
            } else {
               MutableComponent var4 = Component.translatable("disconnect.genericReason", "Internal Exception: " + var2);
               if (var3) {
                  LOGGER.debug("Failed to sent packet", var2);
                  if (this.getSending() == PacketFlow.CLIENTBOUND) {
                     ConnectionProtocol var5 = ((ConnectionProtocol.CodecData)this.channel.attr(ATTRIBUTE_CLIENTBOUND_PROTOCOL).get()).protocol();
                     Object var6 = var5 == ConnectionProtocol.LOGIN ? new ClientboundLoginDisconnectPacket(var4) : new ClientboundDisconnectPacket(var4);
                     this.send((Packet<?>)var6, PacketSendListener.thenRun(() -> this.disconnect(var4)));
                  } else {
                     this.disconnect(var4);
                  }

                  this.setReadOnly();
               } else {
                  LOGGER.debug("Double fault", var2);
                  this.disconnect(var4);
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
                  this.disconnect(Component.translatable("multiplayer.disconnect.server_shutdown"));
               } catch (ClassCastException var7) {
                  LOGGER.error("Received {} that couldn't be processed", var2.getClass(), var7);
                  this.disconnect(Component.translatable("multiplayer.disconnect.invalid_packet"));
               }

               ++this.receivedPackets;
            }
         }
      }
   }

   private static <T extends PacketListener> void genericsFtw(Packet<T> var0, PacketListener var1) {
      var0.handle(var1);
   }

   public void suspendInboundAfterProtocolChange() {
      this.channel.config().setAutoRead(false);
   }

   public void resumeInboundAfterProtocolChange() {
      this.channel.config().setAutoRead(true);
   }

   public void setListener(PacketListener var1) {
      Validate.notNull(var1, "packetListener", new Object[0]);
      PacketFlow var2 = var1.flow();
      if (var2 != this.receiving) {
         throw new IllegalStateException("Trying to set listener for wrong side: connection is " + this.receiving + ", but listener is " + var2);
      } else {
         ConnectionProtocol var3 = var1.protocol();
         ConnectionProtocol var4 = ((ConnectionProtocol.CodecData)this.channel.attr(getProtocolKey(var2)).get()).protocol();
         if (var4 != var3) {
            throw new IllegalStateException("Trying to set listener for protocol " + var3.id() + ", but current " + var2 + " protocol is " + var4.id());
         } else {
            this.packetListener = var1;
            this.disconnectListener = null;
         }
      }
   }

   public void setListenerForServerboundHandshake(PacketListener var1) {
      if (this.packetListener != null) {
         throw new IllegalStateException("Listener already set");
      } else if (this.receiving == PacketFlow.SERVERBOUND && var1.flow() == PacketFlow.SERVERBOUND && var1.protocol() == ConnectionProtocol.HANDSHAKING) {
         this.packetListener = var1;
      } else {
         throw new IllegalStateException("Invalid initial listener");
      }
   }

   public void initiateServerboundStatusConnection(String var1, int var2, ClientStatusPacketListener var3) {
      this.initiateServerboundConnection(var1, var2, var3, ClientIntent.STATUS);
   }

   public void initiateServerboundPlayConnection(String var1, int var2, ClientLoginPacketListener var3) {
      this.initiateServerboundConnection(var1, var2, var3, ClientIntent.LOGIN);
   }

   private void initiateServerboundConnection(String var1, int var2, PacketListener var3, ClientIntent var4) {
      this.disconnectListener = var3;
      this.runOnceConnected(var5 -> {
         var5.setClientboundProtocolAfterHandshake(var4);
         this.setListener(var3);
         var5.sendPacket(new ClientIntentionPacket(SharedConstants.getCurrentVersion().getProtocolVersion(), var1, var2, var4), null, true);
      });
   }

   public void setClientboundProtocolAfterHandshake(ClientIntent var1) {
      this.channel.attr(ATTRIBUTE_CLIENTBOUND_PROTOCOL).set(var1.protocol().codec(PacketFlow.CLIENTBOUND));
   }

   public void send(Packet<?> var1) {
      this.send(var1, null);
   }

   public void send(Packet<?> var1, @Nullable PacketSendListener var2) {
      this.send(var1, var2, true);
   }

   public void send(Packet<?> var1, @Nullable PacketSendListener var2, boolean var3) {
      if (this.isConnected()) {
         this.flushQueue();
         this.sendPacket(var1, var2, var3);
      } else {
         this.pendingActions.add(var3x -> var3x.sendPacket(var1, var2, var3));
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
         var4.addListener(var2x -> {
            if (var2x.isSuccess()) {
               var2.onSuccess();
            } else {
               Packet var3xx = var2.onFailure();
               if (var3xx != null) {
                  ChannelFuture var4xx = this.channel.writeAndFlush(var3xx);
                  var4xx.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
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

   private static AttributeKey<ConnectionProtocol.CodecData<?>> getProtocolKey(PacketFlow var0) {
      return switch(var0) {
         case CLIENTBOUND -> ATTRIBUTE_CLIENTBOUND_PROTOCOL;
         case SERVERBOUND -> ATTRIBUTE_SERVERBOUND_PROTOCOL;
      };
   }

   private void flushQueue() {
      if (this.channel != null && this.channel.isOpen()) {
         synchronized(this.pendingActions) {
            Consumer var2;
            while((var2 = this.pendingActions.poll()) != null) {
               var2.accept(this);
            }
         }
      }
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
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
      if (this.channel == null) {
         this.delayedDisconnect = var1;
      }

      if (this.isConnected()) {
         this.channel.close().awaitUninterruptibly();
         this.disconnectedReason = var1;
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

   public static Connection connectToServer(InetSocketAddress var0, boolean var1, @Nullable SampleLogger var2) {
      Connection var3 = new Connection(PacketFlow.CLIENTBOUND);
      if (var2 != null) {
         var3.setBandwidthLogger(var2);
      }

      ChannelFuture var4 = connect(var0, var1, var3);
      var4.syncUninterruptibly();
      return var3;
   }

   public static ChannelFuture connect(InetSocketAddress var0, boolean var1, final Connection var2) {
      Class<EpollSocketChannel> var3;
      EventLoopGroup var4;
      if (Epoll.isAvailable() && var1) {
         var3 = EpollSocketChannel.class;
         var4 = (EventLoopGroup)NETWORK_EPOLL_WORKER_GROUP.get();
      } else {
         var3 = NioSocketChannel.class;
         var4 = (EventLoopGroup)NETWORK_WORKER_GROUP.get();
      }

      return ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group(var4)).handler(new ChannelInitializer<Channel>() {
         protected void initChannel(Channel var1) {
            Connection.setInitialProtocolAttributes(var1);

            try {
               var1.config().setOption(ChannelOption.TCP_NODELAY, true);
            } catch (ChannelException var3) {
            }

            ChannelPipeline var2x = var1.pipeline().addLast("timeout", new ReadTimeoutHandler(30));
            Connection.configureSerialization(var2x, PacketFlow.CLIENTBOUND, var2.bandwidthDebugMonitor);
            var2.configurePacketHandler(var2x);
         }
      })).channel(var3)).connect(var0.getAddress(), var0.getPort());
   }

   public static void configureSerialization(ChannelPipeline var0, PacketFlow var1, @Nullable BandwidthDebugMonitor var2) {
      PacketFlow var3 = var1.getOpposite();
      AttributeKey var4 = getProtocolKey(var1);
      AttributeKey var5 = getProtocolKey(var3);
      var0.addLast("splitter", new Varint21FrameDecoder(var2))
         .addLast("decoder", new PacketDecoder(var4))
         .addLast("prepender", new Varint21LengthFieldPrepender())
         .addLast("encoder", new PacketEncoder(var5))
         .addLast("unbundler", new PacketBundleUnpacker(var5))
         .addLast("bundler", new PacketBundlePacker(var4));
   }

   public void configurePacketHandler(ChannelPipeline var1) {
      var1.addLast(new ChannelHandler[]{new FlowControlHandler()}).addLast("packet_handler", this);
   }

   private static void configureInMemoryPacketValidation(ChannelPipeline var0, PacketFlow var1) {
      PacketFlow var2 = var1.getOpposite();
      AttributeKey var3 = getProtocolKey(var1);
      AttributeKey var4 = getProtocolKey(var2);
      var0.addLast("validator", new PacketFlowValidator(var3, var4));
   }

   public static void configureInMemoryPipeline(ChannelPipeline var0, PacketFlow var1) {
      configureInMemoryPacketValidation(var0, var1);
   }

   public static Connection connectToLocalServer(SocketAddress var0) {
      final Connection var1 = new Connection(PacketFlow.CLIENTBOUND);
      ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)LOCAL_WORKER_GROUP.get())).handler(new ChannelInitializer<Channel>() {
         protected void initChannel(Channel var1x) {
            Connection.setInitialProtocolAttributes(var1x);
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
   public Component getDisconnectedReason() {
      return this.disconnectedReason;
   }

   public void setReadOnly() {
      if (this.channel != null) {
         this.channel.config().setAutoRead(false);
      }
   }

   public void setupCompression(int var1, boolean var2) {
      if (var1 >= 0) {
         if (this.channel.pipeline().get("decompress") instanceof CompressionDecoder) {
            ((CompressionDecoder)this.channel.pipeline().get("decompress")).setThreshold(var1, var2);
         } else {
            this.channel.pipeline().addBefore("decoder", "decompress", new CompressionDecoder(var1, var2));
         }

         if (this.channel.pipeline().get("compress") instanceof CompressionEncoder) {
            ((CompressionEncoder)this.channel.pipeline().get("compress")).setThreshold(var1);
         } else {
            this.channel.pipeline().addBefore("encoder", "compress", new CompressionEncoder(var1));
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
               Component var3 = Objects.requireNonNullElseGet(this.getDisconnectedReason(), () -> Component.translatable("multiplayer.disconnect.generic"));
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

   public void setBandwidthLogger(SampleLogger var1) {
      this.bandwidthDebugMonitor = new BandwidthDebugMonitor(var1);
   }
}
