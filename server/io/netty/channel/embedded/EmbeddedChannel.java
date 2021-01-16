package io.netty.channel.embedded;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.DefaultChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.Queue;

public class EmbeddedChannel extends AbstractChannel {
   private static final SocketAddress LOCAL_ADDRESS = new EmbeddedSocketAddress();
   private static final SocketAddress REMOTE_ADDRESS = new EmbeddedSocketAddress();
   private static final ChannelHandler[] EMPTY_HANDLERS = new ChannelHandler[0];
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(EmbeddedChannel.class);
   private static final ChannelMetadata METADATA_NO_DISCONNECT = new ChannelMetadata(false);
   private static final ChannelMetadata METADATA_DISCONNECT = new ChannelMetadata(true);
   private final EmbeddedEventLoop loop;
   private final ChannelFutureListener recordExceptionListener;
   private final ChannelMetadata metadata;
   private final ChannelConfig config;
   private Queue<Object> inboundMessages;
   private Queue<Object> outboundMessages;
   private Throwable lastException;
   private EmbeddedChannel.State state;

   public EmbeddedChannel() {
      this(EMPTY_HANDLERS);
   }

   public EmbeddedChannel(ChannelId var1) {
      this(var1, EMPTY_HANDLERS);
   }

   public EmbeddedChannel(ChannelHandler... var1) {
      this(EmbeddedChannelId.INSTANCE, var1);
   }

   public EmbeddedChannel(boolean var1, ChannelHandler... var2) {
      this(EmbeddedChannelId.INSTANCE, var1, var2);
   }

   public EmbeddedChannel(boolean var1, boolean var2, ChannelHandler... var3) {
      this(EmbeddedChannelId.INSTANCE, var1, var2, var3);
   }

   public EmbeddedChannel(ChannelId var1, ChannelHandler... var2) {
      this(var1, false, var2);
   }

   public EmbeddedChannel(ChannelId var1, boolean var2, ChannelHandler... var3) {
      this(var1, true, var2, var3);
   }

   public EmbeddedChannel(ChannelId var1, boolean var2, boolean var3, ChannelHandler... var4) {
      super((Channel)null, var1);
      this.loop = new EmbeddedEventLoop();
      this.recordExceptionListener = new ChannelFutureListener() {
         public void operationComplete(ChannelFuture var1) throws Exception {
            EmbeddedChannel.this.recordException(var1);
         }
      };
      this.metadata = metadata(var3);
      this.config = new DefaultChannelConfig(this);
      this.setup(var2, var4);
   }

   public EmbeddedChannel(ChannelId var1, boolean var2, ChannelConfig var3, ChannelHandler... var4) {
      super((Channel)null, var1);
      this.loop = new EmbeddedEventLoop();
      this.recordExceptionListener = new ChannelFutureListener() {
         public void operationComplete(ChannelFuture var1) throws Exception {
            EmbeddedChannel.this.recordException(var1);
         }
      };
      this.metadata = metadata(var2);
      this.config = (ChannelConfig)ObjectUtil.checkNotNull(var3, "config");
      this.setup(true, var4);
   }

   private static ChannelMetadata metadata(boolean var0) {
      return var0 ? METADATA_DISCONNECT : METADATA_NO_DISCONNECT;
   }

   private void setup(boolean var1, final ChannelHandler... var2) {
      ObjectUtil.checkNotNull(var2, "handlers");
      ChannelPipeline var3 = this.pipeline();
      var3.addLast(new ChannelInitializer<Channel>() {
         protected void initChannel(Channel var1) throws Exception {
            ChannelPipeline var2x = var1.pipeline();
            ChannelHandler[] var3 = var2;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               ChannelHandler var6 = var3[var5];
               if (var6 == null) {
                  break;
               }

               var2x.addLast(var6);
            }

         }
      });
      if (var1) {
         ChannelFuture var4 = this.loop.register((Channel)this);

         assert var4.isDone();
      }

   }

   public void register() throws Exception {
      ChannelFuture var1 = this.loop.register((Channel)this);

      assert var1.isDone();

      Throwable var2 = var1.cause();
      if (var2 != null) {
         PlatformDependent.throwException(var2);
      }

   }

   protected final DefaultChannelPipeline newChannelPipeline() {
      return new EmbeddedChannel.EmbeddedChannelPipeline(this);
   }

   public ChannelMetadata metadata() {
      return this.metadata;
   }

   public ChannelConfig config() {
      return this.config;
   }

   public boolean isOpen() {
      return this.state != EmbeddedChannel.State.CLOSED;
   }

   public boolean isActive() {
      return this.state == EmbeddedChannel.State.ACTIVE;
   }

   public Queue<Object> inboundMessages() {
      if (this.inboundMessages == null) {
         this.inboundMessages = new ArrayDeque();
      }

      return this.inboundMessages;
   }

   /** @deprecated */
   @Deprecated
   public Queue<Object> lastInboundBuffer() {
      return this.inboundMessages();
   }

   public Queue<Object> outboundMessages() {
      if (this.outboundMessages == null) {
         this.outboundMessages = new ArrayDeque();
      }

      return this.outboundMessages;
   }

   /** @deprecated */
   @Deprecated
   public Queue<Object> lastOutboundBuffer() {
      return this.outboundMessages();
   }

   public <T> T readInbound() {
      Object var1 = poll(this.inboundMessages);
      if (var1 != null) {
         ReferenceCountUtil.touch(var1, "Caller of readInbound() will handle the message from this point");
      }

      return var1;
   }

   public <T> T readOutbound() {
      Object var1 = poll(this.outboundMessages);
      if (var1 != null) {
         ReferenceCountUtil.touch(var1, "Caller of readOutbound() will handle the message from this point.");
      }

      return var1;
   }

   public boolean writeInbound(Object... var1) {
      this.ensureOpen();
      if (var1.length == 0) {
         return isNotEmpty(this.inboundMessages);
      } else {
         ChannelPipeline var2 = this.pipeline();
         Object[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Object var6 = var3[var5];
            var2.fireChannelRead(var6);
         }

         this.flushInbound(false, this.voidPromise());
         return isNotEmpty(this.inboundMessages);
      }
   }

   public ChannelFuture writeOneInbound(Object var1) {
      return this.writeOneInbound(var1, this.newPromise());
   }

   public ChannelFuture writeOneInbound(Object var1, ChannelPromise var2) {
      if (this.checkOpen(true)) {
         this.pipeline().fireChannelRead(var1);
      }

      return this.checkException(var2);
   }

   public EmbeddedChannel flushInbound() {
      this.flushInbound(true, this.voidPromise());
      return this;
   }

   private ChannelFuture flushInbound(boolean var1, ChannelPromise var2) {
      if (this.checkOpen(var1)) {
         this.pipeline().fireChannelReadComplete();
         this.runPendingTasks();
      }

      return this.checkException(var2);
   }

   public boolean writeOutbound(Object... var1) {
      this.ensureOpen();
      if (var1.length == 0) {
         return isNotEmpty(this.outboundMessages);
      } else {
         RecyclableArrayList var2 = RecyclableArrayList.newInstance(var1.length);

         try {
            Object[] var3 = var1;
            int var4 = var1.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               Object var6 = var3[var5];
               if (var6 == null) {
                  break;
               }

               var2.add(this.write(var6));
            }

            this.flushOutbound0();
            int var10 = var2.size();

            for(var4 = 0; var4 < var10; ++var4) {
               ChannelFuture var12 = (ChannelFuture)var2.get(var4);
               if (var12.isDone()) {
                  this.recordException(var12);
               } else {
                  var12.addListener(this.recordExceptionListener);
               }
            }

            this.checkException();
            boolean var11 = isNotEmpty(this.outboundMessages);
            return var11;
         } finally {
            var2.recycle();
         }
      }
   }

   public ChannelFuture writeOneOutbound(Object var1) {
      return this.writeOneOutbound(var1, this.newPromise());
   }

   public ChannelFuture writeOneOutbound(Object var1, ChannelPromise var2) {
      return this.checkOpen(true) ? this.write(var1, var2) : this.checkException(var2);
   }

   public EmbeddedChannel flushOutbound() {
      if (this.checkOpen(true)) {
         this.flushOutbound0();
      }

      this.checkException(this.voidPromise());
      return this;
   }

   private void flushOutbound0() {
      this.runPendingTasks();
      this.flush();
   }

   public boolean finish() {
      return this.finish(false);
   }

   public boolean finishAndReleaseAll() {
      return this.finish(true);
   }

   private boolean finish(boolean var1) {
      this.close();

      boolean var2;
      try {
         this.checkException();
         var2 = isNotEmpty(this.inboundMessages) || isNotEmpty(this.outboundMessages);
      } finally {
         if (var1) {
            releaseAll(this.inboundMessages);
            releaseAll(this.outboundMessages);
         }

      }

      return var2;
   }

   public boolean releaseInbound() {
      return releaseAll(this.inboundMessages);
   }

   public boolean releaseOutbound() {
      return releaseAll(this.outboundMessages);
   }

   private static boolean releaseAll(Queue<Object> var0) {
      if (!isNotEmpty(var0)) {
         return false;
      } else {
         while(true) {
            Object var1 = var0.poll();
            if (var1 == null) {
               return true;
            }

            ReferenceCountUtil.release(var1);
         }
      }
   }

   private void finishPendingTasks(boolean var1) {
      this.runPendingTasks();
      if (var1) {
         this.loop.cancelScheduledTasks();
      }

   }

   public final ChannelFuture close() {
      return this.close(this.newPromise());
   }

   public final ChannelFuture disconnect() {
      return this.disconnect(this.newPromise());
   }

   public final ChannelFuture close(ChannelPromise var1) {
      this.runPendingTasks();
      ChannelFuture var2 = super.close(var1);
      this.finishPendingTasks(true);
      return var2;
   }

   public final ChannelFuture disconnect(ChannelPromise var1) {
      ChannelFuture var2 = super.disconnect(var1);
      this.finishPendingTasks(!this.metadata.hasDisconnect());
      return var2;
   }

   private static boolean isNotEmpty(Queue<Object> var0) {
      return var0 != null && !var0.isEmpty();
   }

   private static Object poll(Queue<Object> var0) {
      return var0 != null ? var0.poll() : null;
   }

   public void runPendingTasks() {
      try {
         this.loop.runTasks();
      } catch (Exception var3) {
         this.recordException((Throwable)var3);
      }

      try {
         this.loop.runScheduledTasks();
      } catch (Exception var2) {
         this.recordException((Throwable)var2);
      }

   }

   public long runScheduledPendingTasks() {
      try {
         return this.loop.runScheduledTasks();
      } catch (Exception var2) {
         this.recordException((Throwable)var2);
         return this.loop.nextScheduledTask();
      }
   }

   private void recordException(ChannelFuture var1) {
      if (!var1.isSuccess()) {
         this.recordException(var1.cause());
      }

   }

   private void recordException(Throwable var1) {
      if (this.lastException == null) {
         this.lastException = var1;
      } else {
         logger.warn("More than one exception was raised. Will report only the first one and log others.", var1);
      }

   }

   private ChannelFuture checkException(ChannelPromise var1) {
      Throwable var2 = this.lastException;
      if (var2 != null) {
         this.lastException = null;
         if (var1.isVoid()) {
            PlatformDependent.throwException(var2);
         }

         return var1.setFailure(var2);
      } else {
         return var1.setSuccess();
      }
   }

   public void checkException() {
      this.checkException(this.voidPromise());
   }

   private boolean checkOpen(boolean var1) {
      if (!this.isOpen()) {
         if (var1) {
            this.recordException((Throwable)(new ClosedChannelException()));
         }

         return false;
      } else {
         return true;
      }
   }

   protected final void ensureOpen() {
      if (!this.checkOpen(true)) {
         this.checkException();
      }

   }

   protected boolean isCompatible(EventLoop var1) {
      return var1 instanceof EmbeddedEventLoop;
   }

   protected SocketAddress localAddress0() {
      return this.isActive() ? LOCAL_ADDRESS : null;
   }

   protected SocketAddress remoteAddress0() {
      return this.isActive() ? REMOTE_ADDRESS : null;
   }

   protected void doRegister() throws Exception {
      this.state = EmbeddedChannel.State.ACTIVE;
   }

   protected void doBind(SocketAddress var1) throws Exception {
   }

   protected void doDisconnect() throws Exception {
      if (!this.metadata.hasDisconnect()) {
         this.doClose();
      }

   }

   protected void doClose() throws Exception {
      this.state = EmbeddedChannel.State.CLOSED;
   }

   protected void doBeginRead() throws Exception {
   }

   protected AbstractChannel.AbstractUnsafe newUnsafe() {
      return new EmbeddedChannel.EmbeddedUnsafe();
   }

   public Channel.Unsafe unsafe() {
      return ((EmbeddedChannel.EmbeddedUnsafe)super.unsafe()).wrapped;
   }

   protected void doWrite(ChannelOutboundBuffer var1) throws Exception {
      while(true) {
         Object var2 = var1.current();
         if (var2 == null) {
            return;
         }

         ReferenceCountUtil.retain(var2);
         this.handleOutboundMessage(var2);
         var1.remove();
      }
   }

   protected void handleOutboundMessage(Object var1) {
      this.outboundMessages().add(var1);
   }

   protected void handleInboundMessage(Object var1) {
      this.inboundMessages().add(var1);
   }

   private final class EmbeddedChannelPipeline extends DefaultChannelPipeline {
      EmbeddedChannelPipeline(EmbeddedChannel var2) {
         super(var2);
      }

      protected void onUnhandledInboundException(Throwable var1) {
         EmbeddedChannel.this.recordException(var1);
      }

      protected void onUnhandledInboundMessage(Object var1) {
         EmbeddedChannel.this.handleInboundMessage(var1);
      }
   }

   private final class EmbeddedUnsafe extends AbstractChannel.AbstractUnsafe {
      final Channel.Unsafe wrapped;

      private EmbeddedUnsafe() {
         super();
         this.wrapped = new Channel.Unsafe() {
            public RecvByteBufAllocator.Handle recvBufAllocHandle() {
               return EmbeddedUnsafe.this.recvBufAllocHandle();
            }

            public SocketAddress localAddress() {
               return EmbeddedUnsafe.this.localAddress();
            }

            public SocketAddress remoteAddress() {
               return EmbeddedUnsafe.this.remoteAddress();
            }

            public void register(EventLoop var1, ChannelPromise var2) {
               EmbeddedUnsafe.this.register(var1, var2);
               EmbeddedChannel.this.runPendingTasks();
            }

            public void bind(SocketAddress var1, ChannelPromise var2) {
               EmbeddedUnsafe.this.bind(var1, var2);
               EmbeddedChannel.this.runPendingTasks();
            }

            public void connect(SocketAddress var1, SocketAddress var2, ChannelPromise var3) {
               EmbeddedUnsafe.this.connect(var1, var2, var3);
               EmbeddedChannel.this.runPendingTasks();
            }

            public void disconnect(ChannelPromise var1) {
               EmbeddedUnsafe.this.disconnect(var1);
               EmbeddedChannel.this.runPendingTasks();
            }

            public void close(ChannelPromise var1) {
               EmbeddedUnsafe.this.close(var1);
               EmbeddedChannel.this.runPendingTasks();
            }

            public void closeForcibly() {
               EmbeddedUnsafe.this.closeForcibly();
               EmbeddedChannel.this.runPendingTasks();
            }

            public void deregister(ChannelPromise var1) {
               EmbeddedUnsafe.this.deregister(var1);
               EmbeddedChannel.this.runPendingTasks();
            }

            public void beginRead() {
               EmbeddedUnsafe.this.beginRead();
               EmbeddedChannel.this.runPendingTasks();
            }

            public void write(Object var1, ChannelPromise var2) {
               EmbeddedUnsafe.this.write(var1, var2);
               EmbeddedChannel.this.runPendingTasks();
            }

            public void flush() {
               EmbeddedUnsafe.this.flush();
               EmbeddedChannel.this.runPendingTasks();
            }

            public ChannelPromise voidPromise() {
               return EmbeddedUnsafe.this.voidPromise();
            }

            public ChannelOutboundBuffer outboundBuffer() {
               return EmbeddedUnsafe.this.outboundBuffer();
            }
         };
      }

      public void connect(SocketAddress var1, SocketAddress var2, ChannelPromise var3) {
         this.safeSetSuccess(var3);
      }

      // $FF: synthetic method
      EmbeddedUnsafe(Object var2) {
         this();
      }
   }

   private static enum State {
      OPEN,
      ACTIVE,
      CLOSED;

      private State() {
      }
   }
}
