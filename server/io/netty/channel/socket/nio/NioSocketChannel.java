package io.netty.channel.socket.nio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.FileRegion;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.nio.AbstractNioByteChannel;
import io.netty.channel.nio.AbstractNioChannel;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.socket.DefaultSocketChannelConfig;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.Executor;

public class NioSocketChannel extends AbstractNioByteChannel implements SocketChannel {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioSocketChannel.class);
   private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();
   private final SocketChannelConfig config;

   private static java.nio.channels.SocketChannel newSocket(SelectorProvider var0) {
      try {
         return var0.openSocketChannel();
      } catch (IOException var2) {
         throw new ChannelException("Failed to open a socket.", var2);
      }
   }

   public NioSocketChannel() {
      this(DEFAULT_SELECTOR_PROVIDER);
   }

   public NioSocketChannel(SelectorProvider var1) {
      this(newSocket(var1));
   }

   public NioSocketChannel(java.nio.channels.SocketChannel var1) {
      this((Channel)null, var1);
   }

   public NioSocketChannel(Channel var1, java.nio.channels.SocketChannel var2) {
      super(var1, var2);
      this.config = new NioSocketChannel.NioSocketChannelConfig(this, var2.socket());
   }

   public ServerSocketChannel parent() {
      return (ServerSocketChannel)super.parent();
   }

   public SocketChannelConfig config() {
      return this.config;
   }

   protected java.nio.channels.SocketChannel javaChannel() {
      return (java.nio.channels.SocketChannel)super.javaChannel();
   }

   public boolean isActive() {
      java.nio.channels.SocketChannel var1 = this.javaChannel();
      return var1.isOpen() && var1.isConnected();
   }

   public boolean isOutputShutdown() {
      return this.javaChannel().socket().isOutputShutdown() || !this.isActive();
   }

   public boolean isInputShutdown() {
      return this.javaChannel().socket().isInputShutdown() || !this.isActive();
   }

   public boolean isShutdown() {
      Socket var1 = this.javaChannel().socket();
      return var1.isInputShutdown() && var1.isOutputShutdown() || !this.isActive();
   }

   public InetSocketAddress localAddress() {
      return (InetSocketAddress)super.localAddress();
   }

   public InetSocketAddress remoteAddress() {
      return (InetSocketAddress)super.remoteAddress();
   }

   protected final void doShutdownOutput() throws Exception {
      if (PlatformDependent.javaVersion() >= 7) {
         this.javaChannel().shutdownOutput();
      } else {
         this.javaChannel().socket().shutdownOutput();
      }

   }

   public ChannelFuture shutdownOutput() {
      return this.shutdownOutput(this.newPromise());
   }

   public ChannelFuture shutdownOutput(final ChannelPromise var1) {
      NioEventLoop var2 = this.eventLoop();
      if (var2.inEventLoop()) {
         ((AbstractChannel.AbstractUnsafe)this.unsafe()).shutdownOutput(var1);
      } else {
         var2.execute(new Runnable() {
            public void run() {
               ((AbstractChannel.AbstractUnsafe)NioSocketChannel.this.unsafe()).shutdownOutput(var1);
            }
         });
      }

      return var1;
   }

   public ChannelFuture shutdownInput() {
      return this.shutdownInput(this.newPromise());
   }

   protected boolean isInputShutdown0() {
      return this.isInputShutdown();
   }

   public ChannelFuture shutdownInput(final ChannelPromise var1) {
      NioEventLoop var2 = this.eventLoop();
      if (var2.inEventLoop()) {
         this.shutdownInput0(var1);
      } else {
         var2.execute(new Runnable() {
            public void run() {
               NioSocketChannel.this.shutdownInput0(var1);
            }
         });
      }

      return var1;
   }

   public ChannelFuture shutdown() {
      return this.shutdown(this.newPromise());
   }

   public ChannelFuture shutdown(final ChannelPromise var1) {
      ChannelFuture var2 = this.shutdownOutput();
      if (var2.isDone()) {
         this.shutdownOutputDone(var2, var1);
      } else {
         var2.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture var1x) throws Exception {
               NioSocketChannel.this.shutdownOutputDone(var1x, var1);
            }
         });
      }

      return var1;
   }

   private void shutdownOutputDone(final ChannelFuture var1, final ChannelPromise var2) {
      ChannelFuture var3 = this.shutdownInput();
      if (var3.isDone()) {
         shutdownDone(var1, var3, var2);
      } else {
         var3.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture var1x) throws Exception {
               NioSocketChannel.shutdownDone(var1, var1x, var2);
            }
         });
      }

   }

   private static void shutdownDone(ChannelFuture var0, ChannelFuture var1, ChannelPromise var2) {
      Throwable var3 = var0.cause();
      Throwable var4 = var1.cause();
      if (var3 != null) {
         if (var4 != null) {
            logger.debug("Exception suppressed because a previous exception occurred.", var4);
         }

         var2.setFailure(var3);
      } else if (var4 != null) {
         var2.setFailure(var4);
      } else {
         var2.setSuccess();
      }

   }

   private void shutdownInput0(ChannelPromise var1) {
      try {
         this.shutdownInput0();
         var1.setSuccess();
      } catch (Throwable var3) {
         var1.setFailure(var3);
      }

   }

   private void shutdownInput0() throws Exception {
      if (PlatformDependent.javaVersion() >= 7) {
         this.javaChannel().shutdownInput();
      } else {
         this.javaChannel().socket().shutdownInput();
      }

   }

   protected SocketAddress localAddress0() {
      return this.javaChannel().socket().getLocalSocketAddress();
   }

   protected SocketAddress remoteAddress0() {
      return this.javaChannel().socket().getRemoteSocketAddress();
   }

   protected void doBind(SocketAddress var1) throws Exception {
      this.doBind0(var1);
   }

   private void doBind0(SocketAddress var1) throws Exception {
      if (PlatformDependent.javaVersion() >= 7) {
         SocketUtils.bind(this.javaChannel(), var1);
      } else {
         SocketUtils.bind(this.javaChannel().socket(), var1);
      }

   }

   protected boolean doConnect(SocketAddress var1, SocketAddress var2) throws Exception {
      if (var2 != null) {
         this.doBind0(var2);
      }

      boolean var3 = false;

      boolean var5;
      try {
         boolean var4 = SocketUtils.connect(this.javaChannel(), var1);
         if (!var4) {
            this.selectionKey().interestOps(8);
         }

         var3 = true;
         var5 = var4;
      } finally {
         if (!var3) {
            this.doClose();
         }

      }

      return var5;
   }

   protected void doFinishConnect() throws Exception {
      if (!this.javaChannel().finishConnect()) {
         throw new Error();
      }
   }

   protected void doDisconnect() throws Exception {
      this.doClose();
   }

   protected void doClose() throws Exception {
      super.doClose();
      this.javaChannel().close();
   }

   protected int doReadBytes(ByteBuf var1) throws Exception {
      RecvByteBufAllocator.Handle var2 = this.unsafe().recvBufAllocHandle();
      var2.attemptedBytesRead(var1.writableBytes());
      return var1.writeBytes((ScatteringByteChannel)this.javaChannel(), var2.attemptedBytesRead());
   }

   protected int doWriteBytes(ByteBuf var1) throws Exception {
      int var2 = var1.readableBytes();
      return var1.readBytes((GatheringByteChannel)this.javaChannel(), var2);
   }

   protected long doWriteFileRegion(FileRegion var1) throws Exception {
      long var2 = var1.transferred();
      return var1.transferTo(this.javaChannel(), var2);
   }

   private void adjustMaxBytesPerGatheringWrite(int var1, int var2, int var3) {
      if (var1 == var2) {
         if (var1 << 1 > var3) {
            ((NioSocketChannel.NioSocketChannelConfig)this.config).setMaxBytesPerGatheringWrite(var1 << 1);
         }
      } else if (var1 > 4096 && var2 < var1 >>> 1) {
         ((NioSocketChannel.NioSocketChannelConfig)this.config).setMaxBytesPerGatheringWrite(var1 >>> 1);
      }

   }

   protected void doWrite(ChannelOutboundBuffer var1) throws Exception {
      java.nio.channels.SocketChannel var2 = this.javaChannel();
      int var3 = this.config().getWriteSpinCount();

      do {
         if (var1.isEmpty()) {
            this.clearOpWrite();
            return;
         }

         int var4 = ((NioSocketChannel.NioSocketChannelConfig)this.config).getMaxBytesPerGatheringWrite();
         ByteBuffer[] var5 = var1.nioBuffers(1024, (long)var4);
         int var6 = var1.nioBufferCount();
         switch(var6) {
         case 0:
            var3 -= this.doWrite0(var1);
            break;
         case 1:
            ByteBuffer var7 = var5[0];
            int var8 = var7.remaining();
            int var9 = var2.write(var7);
            if (var9 <= 0) {
               this.incompleteWrite(true);
               return;
            }

            this.adjustMaxBytesPerGatheringWrite(var8, var9, var4);
            var1.removeBytes((long)var9);
            --var3;
            break;
         default:
            long var11 = var1.nioBufferSize();
            long var12 = var2.write(var5, 0, var6);
            if (var12 <= 0L) {
               this.incompleteWrite(true);
               return;
            }

            this.adjustMaxBytesPerGatheringWrite((int)var11, (int)var12, var4);
            var1.removeBytes(var12);
            --var3;
         }
      } while(var3 > 0);

      this.incompleteWrite(var3 < 0);
   }

   protected AbstractNioChannel.AbstractNioUnsafe newUnsafe() {
      return new NioSocketChannel.NioSocketChannelUnsafe();
   }

   private final class NioSocketChannelConfig extends DefaultSocketChannelConfig {
      private volatile int maxBytesPerGatheringWrite;

      private NioSocketChannelConfig(NioSocketChannel var2, Socket var3) {
         super(var2, var3);
         this.maxBytesPerGatheringWrite = 2147483647;
         this.calculateMaxBytesPerGatheringWrite();
      }

      protected void autoReadCleared() {
         NioSocketChannel.this.clearReadPending();
      }

      public NioSocketChannel.NioSocketChannelConfig setSendBufferSize(int var1) {
         super.setSendBufferSize(var1);
         this.calculateMaxBytesPerGatheringWrite();
         return this;
      }

      void setMaxBytesPerGatheringWrite(int var1) {
         this.maxBytesPerGatheringWrite = var1;
      }

      int getMaxBytesPerGatheringWrite() {
         return this.maxBytesPerGatheringWrite;
      }

      private void calculateMaxBytesPerGatheringWrite() {
         int var1 = this.getSendBufferSize() << 1;
         if (var1 > 0) {
            this.setMaxBytesPerGatheringWrite(this.getSendBufferSize() << 1);
         }

      }

      // $FF: synthetic method
      NioSocketChannelConfig(NioSocketChannel var2, Socket var3, Object var4) {
         this(var2, var3);
      }
   }

   private final class NioSocketChannelUnsafe extends AbstractNioByteChannel.NioByteUnsafe {
      private NioSocketChannelUnsafe() {
         super();
      }

      protected Executor prepareToClose() {
         try {
            if (NioSocketChannel.this.javaChannel().isOpen() && NioSocketChannel.this.config().getSoLinger() > 0) {
               NioSocketChannel.this.doDeregister();
               return GlobalEventExecutor.INSTANCE;
            }
         } catch (Throwable var2) {
         }

         return null;
      }

      // $FF: synthetic method
      NioSocketChannelUnsafe(Object var2) {
         this();
      }
   }
}
