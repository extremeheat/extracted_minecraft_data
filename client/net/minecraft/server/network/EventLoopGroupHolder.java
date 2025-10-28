package net.minecraft.server.network;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.IoHandlerFactory;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollIoHandler;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueIoHandler;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalIoHandler;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.concurrent.ThreadFactory;
import org.jspecify.annotations.Nullable;

public abstract class EventLoopGroupHolder {
   private static final EventLoopGroupHolder NIO = new EventLoopGroupHolder("NIO", NioSocketChannel.class, NioServerSocketChannel.class) {
      protected IoHandlerFactory ioHandlerFactory() {
         return NioIoHandler.newFactory();
      }
   };
   private static final EventLoopGroupHolder EPOLL = new EventLoopGroupHolder("Epoll", EpollSocketChannel.class, EpollServerSocketChannel.class) {
      protected IoHandlerFactory ioHandlerFactory() {
         return EpollIoHandler.newFactory();
      }
   };
   private static final EventLoopGroupHolder KQUEUE = new EventLoopGroupHolder("Kqueue", KQueueSocketChannel.class, KQueueServerSocketChannel.class) {
      protected IoHandlerFactory ioHandlerFactory() {
         return KQueueIoHandler.newFactory();
      }
   };
   private static final EventLoopGroupHolder LOCAL = new EventLoopGroupHolder("Local", LocalChannel.class, LocalServerChannel.class) {
      protected IoHandlerFactory ioHandlerFactory() {
         return LocalIoHandler.newFactory();
      }
   };
   private final String type;
   private final Class<? extends Channel> channelCls;
   private final Class<? extends ServerChannel> serverChannelCls;
   private volatile @Nullable EventLoopGroup group;

   public static EventLoopGroupHolder remote(boolean var0) {
      if (var0) {
         if (KQueue.isAvailable()) {
            return KQUEUE;
         }

         if (Epoll.isAvailable()) {
            return EPOLL;
         }
      }

      return NIO;
   }

   public static EventLoopGroupHolder local() {
      return LOCAL;
   }

   EventLoopGroupHolder(String var1, Class<? extends Channel> var2, Class<? extends ServerChannel> var3) {
      super();
      this.type = var1;
      this.channelCls = var2;
      this.serverChannelCls = var3;
   }

   private ThreadFactory createThreadFactory() {
      return (new ThreadFactoryBuilder()).setNameFormat("Netty " + this.type + " IO #%d").setDaemon(true).build();
   }

   protected abstract IoHandlerFactory ioHandlerFactory();

   private EventLoopGroup createEventLoopGroup() {
      return new MultiThreadIoEventLoopGroup(this.createThreadFactory(), this.ioHandlerFactory());
   }

   public EventLoopGroup eventLoopGroup() {
      EventLoopGroup var1 = this.group;
      if (var1 == null) {
         synchronized(this) {
            var1 = this.group;
            if (var1 == null) {
               var1 = this.createEventLoopGroup();
               this.group = var1;
            }
         }
      }

      return var1;
   }

   public Class<? extends Channel> channelCls() {
      return this.channelCls;
   }

   public Class<? extends ServerChannel> serverChannelCls() {
      return this.serverChannelCls;
   }
}
