package net.minecraft.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
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
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Queue;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class Connection extends SimpleChannelInboundHandler<Packet<?>> {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final Marker ROOT_MARKER = MarkerManager.getMarker("NETWORK");
   public static final Marker PACKET_MARKER;
   public static final AttributeKey<ConnectionProtocol> ATTRIBUTE_PROTOCOL;
   public static final LazyLoadedValue<NioEventLoopGroup> NETWORK_WORKER_GROUP;
   public static final LazyLoadedValue<EpollEventLoopGroup> NETWORK_EPOLL_WORKER_GROUP;
   public static final LazyLoadedValue<DefaultEventLoopGroup> LOCAL_WORKER_GROUP;
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
         LOGGER.fatal(var3);
      }

   }

   public void setProtocol(ConnectionProtocol var1) {
      this.channel.attr(ATTRIBUTE_PROTOCOL).set(var1);
      this.channel.config().setAutoRead(true);
      LOGGER.debug("Enabled auto read");
   }

   public void channelInactive(ChannelHandlerContext var1) {
      this.disconnect(new TranslatableComponent("disconnect.endOfStream"));
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
               this.disconnect(new TranslatableComponent("disconnect.timeout"));
            } else {
               TranslatableComponent var4 = new TranslatableComponent("disconnect.genericReason", new Object[]{"Internal Exception: " + var2});
               if (var3) {
                  LOGGER.debug("Failed to sent packet", var2);
                  ConnectionProtocol var5 = this.getCurrentProtocol();
                  Object var6 = var5 == ConnectionProtocol.LOGIN ? new ClientboundLoginDisconnectPacket(var4) : new ClientboundDisconnectPacket(var4);
                  this.send((Packet)var6, (var2x) -> {
                     this.disconnect(var4);
                  });
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
      this.send(var1, (GenericFutureListener)null);
   }

   public void send(Packet<?> var1, @Nullable GenericFutureListener<? extends Future<? super Void>> var2) {
      if (this.isConnected()) {
         this.flushQueue();
         this.sendPacket(var1, var2);
      } else {
         this.queue.add(new Connection.PacketHolder(var1, var2));
      }

   }

   private void sendPacket(Packet<?> var1, @Nullable GenericFutureListener<? extends Future<? super Void>> var2) {
      ConnectionProtocol var3 = ConnectionProtocol.getProtocolForPacket(var1);
      ConnectionProtocol var4 = this.getCurrentProtocol();
      ++this.sentPackets;
      if (var4 != var3) {
         LOGGER.debug("Disabled auto read");
         this.channel.config().setAutoRead(false);
      }

      if (this.channel.eventLoop().inEventLoop()) {
         if (var3 != var4) {
            this.setProtocol(var3);
         }

         ChannelFuture var5 = this.channel.writeAndFlush(var1);
         if (var2 != null) {
            var5.addListener(var2);
         }

         var5.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
      } else {
         this.channel.eventLoop().execute(() -> {
            if (var3 != var4) {
               this.setProtocol(var3);
            }

            ChannelFuture var5 = this.channel.writeAndFlush(var1);
            if (var2 != null) {
               var5.addListener(var2);
            }

            var5.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
         });
      }

   }

   private ConnectionProtocol getCurrentProtocol() {
      return (ConnectionProtocol)this.channel.attr(ATTRIBUTE_PROTOCOL).get();
   }

   private void flushQueue() {
      if (this.channel != null && this.channel.isOpen()) {
         synchronized(this.queue) {
            Connection.PacketHolder var2;
            while((var2 = (Connection.PacketHolder)this.queue.poll()) != null) {
               this.sendPacket(var2.packet, var2.listener);
            }

         }
      }
   }

   public void tick() {
      this.flushQueue();
      if (this.packetListener instanceof ServerLoginPacketListenerImpl) {
         ((ServerLoginPacketListenerImpl)this.packetListener).tick();
      }

      if (this.packetListener instanceof ServerGamePacketListenerImpl) {
         ((ServerGamePacketListenerImpl)this.packetListener).tick();
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

   public static Connection connectToServer(InetAddress var0, int var1, boolean var2) {
      final Connection var3 = new Connection(PacketFlow.CLIENTBOUND);
      Class var4;
      LazyLoadedValue var5;
      if (Epoll.isAvailable() && var2) {
         var4 = EpollSocketChannel.class;
         var5 = NETWORK_EPOLL_WORKER_GROUP;
      } else {
         var4 = NioSocketChannel.class;
         var5 = NETWORK_WORKER_GROUP;
      }

      ((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).group((EventLoopGroup)var5.get())).handler(new ChannelInitializer<Channel>() {
         protected void initChannel(Channel var1) {
            try {
               var1.config().setOption(ChannelOption.TCP_NODELAY, true);
            } catch (ChannelException var3x) {
            }

            var1.pipeline().addLast("timeout", new ReadTimeoutHandler(30)).addLast("splitter", new Varint21FrameDecoder()).addLast("decoder", new PacketDecoder(PacketFlow.CLIENTBOUND)).addLast("prepender", new Varint21LengthFieldPrepender()).addLast("encoder", new PacketEncoder(PacketFlow.SERVERBOUND)).addLast("packet_handler", var3);
         }
      })).channel(var4)).connect(var0, var1).syncUninterruptibly();
      return var3;
   }

   public static Connection connectToLocalServer(SocketAddress var0) {
      final Connection var1 = new Connection(PacketFlow.CLIENTBOUND);
      ((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).group((EventLoopGroup)LOCAL_WORKER_GROUP.get())).handler(new ChannelInitializer<Channel>() {
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

   public void setupCompression(int var1) {
      if (var1 >= 0) {
         if (this.channel.pipeline().get("decompress") instanceof CompressionDecoder) {
            ((CompressionDecoder)this.channel.pipeline().get("decompress")).setThreshold(var1);
         } else {
            this.channel.pipeline().addBefore("decoder", "decompress", new CompressionDecoder(var1));
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
               this.getPacketListener().onDisconnect(new TranslatableComponent("multiplayer.disconnect.generic"));
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

   // $FF: synthetic method
   protected void channelRead0(ChannelHandlerContext var1, Object var2) throws Exception {
      this.channelRead0(var1, (Packet)var2);
   }

   static {
      PACKET_MARKER = MarkerManager.getMarker("NETWORK_PACKETS", ROOT_MARKER);
      ATTRIBUTE_PROTOCOL = AttributeKey.valueOf("protocol");
      NETWORK_WORKER_GROUP = new LazyLoadedValue(() -> {
         return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Client IO #%d").setDaemon(true).build());
      });
      NETWORK_EPOLL_WORKER_GROUP = new LazyLoadedValue(() -> {
         return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
      });
      LOCAL_WORKER_GROUP = new LazyLoadedValue(() -> {
         return new DefaultEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Local Client IO #%d").setDaemon(true).build());
      });
   }

   static class PacketHolder {
      private final Packet<?> packet;
      @Nullable
      private final GenericFutureListener<? extends Future<? super Void>> listener;

      public PacketHolder(Packet<?> var1, @Nullable GenericFutureListener<? extends Future<? super Void>> var2) {
         super();
         this.packet = var1;
         this.listener = var2;
      }
   }
}
