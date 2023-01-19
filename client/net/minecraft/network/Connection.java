package net.minecraft.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.logging.LogUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
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
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.RejectedExecutionException;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.util.Mth;
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
   public static final AttributeKey<ConnectionProtocol> ATTRIBUTE_PROTOCOL = AttributeKey.valueOf("protocol");
   public static final LazyLoadedValue<NioEventLoopGroup> NETWORK_WORKER_GROUP = new LazyLoadedValue(
      () -> new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build())
   );
   public static final LazyLoadedValue<EpollEventLoopGroup> NETWORK_EPOLL_WORKER_GROUP = new LazyLoadedValue(
      () -> new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build())
   );
   public static final LazyLoadedValue<DefaultEventLoopGroup> LOCAL_WORKER_GROUP = new LazyLoadedValue(
      () -> new DefaultEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build())
   );
   private final PacketFlow receiving;
   private final Queue<Connection.PacketHolder> queue = Queues.newConcurrentLinkedQueue();
   private Channel channel;
   private SocketAddress address;
   private PacketListener packetListener;
   private Component disconnectedReason;
   private boolean encrypted;
   private boolean disconnectionHandled;
   private int receivedPackets;
   private int sentPackets;
   private float averageReceivedPackets;
   private float averageSentPackets;
   private int tickCount;
   private boolean handlingFault;

   public Connection(PacketFlow var1) {
      super();
      this.receiving = var1;
   }

   public void channelActive(ChannelHandlerContext var1) throws Exception {
      super.channelActive(var1);
      this.channel = var1.channel();
      this.address = this.channel.remoteAddress();

      try {
         this.setProtocol(ConnectionProtocol.HANDSHAKING);
      } catch (Throwable var3) {
         LOGGER.error(LogUtils.FATAL_MARKER, "Failed to change protocol to handshake", var3);
      }
   }

   public void setProtocol(ConnectionProtocol var1) {
      this.channel.attr(ATTRIBUTE_PROTOCOL).set(var1);
      this.channel.config().setAutoRead(true);
      LOGGER.debug("Enabled auto read");
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
                  ConnectionProtocol var5 = this.getCurrentProtocol();
                  Object var6 = var5 == ConnectionProtocol.LOGIN ? new ClientboundLoginDisconnectPacket(var4) : new ClientboundDisconnectPacket(var4);
                  this.send((Packet<?>)var6, PacketSendListener.thenRun(() -> this.disconnect(var4)));
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
         try {
            genericsFtw(var2, this.packetListener);
         } catch (RunningOnDifferentThreadException var4) {
         } catch (RejectedExecutionException var5) {
            this.disconnect(Component.translatable("multiplayer.disconnect.server_shutdown"));
         } catch (ClassCastException var6) {
            LOGGER.error("Received {} that couldn't be processed", var2.getClass(), var6);
            this.disconnect(Component.translatable("multiplayer.disconnect.invalid_packet"));
         }

         ++this.receivedPackets;
      }
   }

   private static <T extends PacketListener> void genericsFtw(Packet<T> var0, PacketListener var1) {
      var0.handle(var1);
   }

   public void setListener(PacketListener var1) {
      Validate.notNull(var1, "packetListener", new Object[0]);
      this.packetListener = var1;
   }

   public void send(Packet<?> var1) {
      this.send(var1, null);
   }

   public void send(Packet<?> var1, @Nullable PacketSendListener var2) {
      if (this.isConnected()) {
         this.flushQueue();
         this.sendPacket(var1, var2);
      } else {
         this.queue.add(new Connection.PacketHolder(var1, var2));
      }
   }

   private void sendPacket(Packet<?> var1, @Nullable PacketSendListener var2) {
      ConnectionProtocol var3 = ConnectionProtocol.getProtocolForPacket(var1);
      ConnectionProtocol var4 = this.getCurrentProtocol();
      ++this.sentPackets;
      if (var4 != var3) {
         LOGGER.debug("Disabled auto read");
         this.channel.config().setAutoRead(false);
      }

      if (this.channel.eventLoop().inEventLoop()) {
         this.doSendPacket(var1, var2, var3, var4);
      } else {
         this.channel.eventLoop().execute(() -> this.doSendPacket(var1, var2, var3, var4));
      }
   }

   private void doSendPacket(Packet<?> var1, @Nullable PacketSendListener var2, ConnectionProtocol var3, ConnectionProtocol var4) {
      if (var3 != var4) {
         this.setProtocol(var3);
      }

      ChannelFuture var5 = this.channel.writeAndFlush(var1);
      if (var2 != null) {
         var5.addListener(var2x -> {
            if (var2x.isSuccess()) {
               var2.onSuccess();
            } else {
               Packet var3x = var2.onFailure();
               if (var3x != null) {
                  ChannelFuture var4x = this.channel.writeAndFlush(var3x);
                  var4x.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
               }
            }
         });
      }

      var5.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
   }

   private ConnectionProtocol getCurrentProtocol() {
      return (ConnectionProtocol)this.channel.attr(ATTRIBUTE_PROTOCOL).get();
   }

   private void flushQueue() {
      if (this.channel != null && this.channel.isOpen()) {
         synchronized(this.queue) {
            Connection.PacketHolder var2;
            while((var2 = this.queue.poll()) != null) {
               this.sendPacket(var2.packet, var2.listener);
            }
         }
      }
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
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

   public void disconnect(Component var1) {
      if (this.channel.isOpen()) {
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

   public static Connection connectToServer(InetSocketAddress var0, boolean var1) {
      final Connection var2 = new Connection(PacketFlow.CLIENTBOUND);
      Class<EpollSocketChannel> var3;
      LazyLoadedValue var4;
      if (Epoll.isAvailable() && var1) {
         var3 = EpollSocketChannel.class;
         var4 = NETWORK_EPOLL_WORKER_GROUP;
      } else {
         var3 = NioSocketChannel.class;
         var4 = NETWORK_WORKER_GROUP;
      }

      ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)var4.get()))
               .handler(
                  new ChannelInitializer<Channel>() {
                     protected void initChannel(Channel var1) {
                        try {
                           var1.config().setOption(ChannelOption.TCP_NODELAY, true);
                        } catch (ChannelException var3) {
                        }
            
                        var1.pipeline()
                           .addLast("timeout", new ReadTimeoutHandler(30))
                           .addLast("splitter", new Varint21FrameDecoder())
                           .addLast("decoder", new PacketDecoder(PacketFlow.CLIENTBOUND))
                           .addLast("prepender", new Varint21LengthFieldPrepender())
                           .addLast("encoder", new PacketEncoder(PacketFlow.SERVERBOUND))
                           .addLast("packet_handler", var2);
                     }
                  }
               ))
            .channel(var3))
         .connect(var0.getAddress(), var0.getPort())
         .syncUninterruptibly();
      return var2;
   }

   public static Connection connectToLocalServer(SocketAddress var0) {
      final Connection var1 = new Connection(PacketFlow.CLIENTBOUND);
      ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)LOCAL_WORKER_GROUP.get())).handler(new ChannelInitializer<Channel>() {
         protected void initChannel(Channel var1x) {
            var1x.pipeline().addLast("packet_handler", var1);
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

   public PacketListener getPacketListener() {
      return this.packetListener;
   }

   @Nullable
   public Component getDisconnectedReason() {
      return this.disconnectedReason;
   }

   public void setReadOnly() {
      this.channel.config().setAutoRead(false);
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
            if (this.getDisconnectedReason() != null) {
               this.getPacketListener().onDisconnect(this.getDisconnectedReason());
            } else if (this.getPacketListener() != null) {
               this.getPacketListener().onDisconnect(Component.translatable("multiplayer.disconnect.generic"));
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

   static class PacketHolder {
      final Packet<?> packet;
      @Nullable
      final PacketSendListener listener;

      public PacketHolder(Packet<?> var1, @Nullable PacketSendListener var2) {
         super();
         this.packet = var1;
         this.listener = var2;
      }
   }
}
