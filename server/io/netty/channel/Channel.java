package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.util.AttributeMap;
import java.net.SocketAddress;

public interface Channel extends AttributeMap, ChannelOutboundInvoker, Comparable<Channel> {
   ChannelId id();

   EventLoop eventLoop();

   Channel parent();

   ChannelConfig config();

   boolean isOpen();

   boolean isRegistered();

   boolean isActive();

   ChannelMetadata metadata();

   SocketAddress localAddress();

   SocketAddress remoteAddress();

   ChannelFuture closeFuture();

   boolean isWritable();

   long bytesBeforeUnwritable();

   long bytesBeforeWritable();

   Channel.Unsafe unsafe();

   ChannelPipeline pipeline();

   ByteBufAllocator alloc();

   Channel read();

   Channel flush();

   public interface Unsafe {
      RecvByteBufAllocator.Handle recvBufAllocHandle();

      SocketAddress localAddress();

      SocketAddress remoteAddress();

      void register(EventLoop var1, ChannelPromise var2);

      void bind(SocketAddress var1, ChannelPromise var2);

      void connect(SocketAddress var1, SocketAddress var2, ChannelPromise var3);

      void disconnect(ChannelPromise var1);

      void close(ChannelPromise var1);

      void closeForcibly();

      void deregister(ChannelPromise var1);

      void beginRead();

      void write(Object var1, ChannelPromise var2);

      void flush();

      ChannelPromise voidPromise();

      ChannelOutboundBuffer outboundBuffer();
   }
}
