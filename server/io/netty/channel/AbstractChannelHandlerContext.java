package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.DefaultAttributeMap;
import io.netty.util.Recycler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ResourceLeakHint;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.OrderedEventExecutor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PromiseNotificationUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

abstract class AbstractChannelHandlerContext extends DefaultAttributeMap implements ChannelHandlerContext, ResourceLeakHint {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractChannelHandlerContext.class);
   volatile AbstractChannelHandlerContext next;
   volatile AbstractChannelHandlerContext prev;
   private static final AtomicIntegerFieldUpdater<AbstractChannelHandlerContext> HANDLER_STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(AbstractChannelHandlerContext.class, "handlerState");
   private static final int ADD_PENDING = 1;
   private static final int ADD_COMPLETE = 2;
   private static final int REMOVE_COMPLETE = 3;
   private static final int INIT = 0;
   private final boolean inbound;
   private final boolean outbound;
   private final DefaultChannelPipeline pipeline;
   private final String name;
   private final boolean ordered;
   final EventExecutor executor;
   private ChannelFuture succeededFuture;
   private Runnable invokeChannelReadCompleteTask;
   private Runnable invokeReadTask;
   private Runnable invokeChannelWritableStateChangedTask;
   private Runnable invokeFlushTask;
   private volatile int handlerState = 0;

   AbstractChannelHandlerContext(DefaultChannelPipeline var1, EventExecutor var2, String var3, boolean var4, boolean var5) {
      super();
      this.name = (String)ObjectUtil.checkNotNull(var3, "name");
      this.pipeline = var1;
      this.executor = var2;
      this.inbound = var4;
      this.outbound = var5;
      this.ordered = var2 == null || var2 instanceof OrderedEventExecutor;
   }

   public Channel channel() {
      return this.pipeline.channel();
   }

   public ChannelPipeline pipeline() {
      return this.pipeline;
   }

   public ByteBufAllocator alloc() {
      return this.channel().config().getAllocator();
   }

   public EventExecutor executor() {
      return (EventExecutor)(this.executor == null ? this.channel().eventLoop() : this.executor);
   }

   public String name() {
      return this.name;
   }

   public ChannelHandlerContext fireChannelRegistered() {
      invokeChannelRegistered(this.findContextInbound());
      return this;
   }

   static void invokeChannelRegistered(final AbstractChannelHandlerContext var0) {
      EventExecutor var1 = var0.executor();
      if (var1.inEventLoop()) {
         var0.invokeChannelRegistered();
      } else {
         var1.execute(new Runnable() {
            public void run() {
               var0.invokeChannelRegistered();
            }
         });
      }

   }

   private void invokeChannelRegistered() {
      if (this.invokeHandler()) {
         try {
            ((ChannelInboundHandler)this.handler()).channelRegistered(this);
         } catch (Throwable var2) {
            this.notifyHandlerException(var2);
         }
      } else {
         this.fireChannelRegistered();
      }

   }

   public ChannelHandlerContext fireChannelUnregistered() {
      invokeChannelUnregistered(this.findContextInbound());
      return this;
   }

   static void invokeChannelUnregistered(final AbstractChannelHandlerContext var0) {
      EventExecutor var1 = var0.executor();
      if (var1.inEventLoop()) {
         var0.invokeChannelUnregistered();
      } else {
         var1.execute(new Runnable() {
            public void run() {
               var0.invokeChannelUnregistered();
            }
         });
      }

   }

   private void invokeChannelUnregistered() {
      if (this.invokeHandler()) {
         try {
            ((ChannelInboundHandler)this.handler()).channelUnregistered(this);
         } catch (Throwable var2) {
            this.notifyHandlerException(var2);
         }
      } else {
         this.fireChannelUnregistered();
      }

   }

   public ChannelHandlerContext fireChannelActive() {
      invokeChannelActive(this.findContextInbound());
      return this;
   }

   static void invokeChannelActive(final AbstractChannelHandlerContext var0) {
      EventExecutor var1 = var0.executor();
      if (var1.inEventLoop()) {
         var0.invokeChannelActive();
      } else {
         var1.execute(new Runnable() {
            public void run() {
               var0.invokeChannelActive();
            }
         });
      }

   }

   private void invokeChannelActive() {
      if (this.invokeHandler()) {
         try {
            ((ChannelInboundHandler)this.handler()).channelActive(this);
         } catch (Throwable var2) {
            this.notifyHandlerException(var2);
         }
      } else {
         this.fireChannelActive();
      }

   }

   public ChannelHandlerContext fireChannelInactive() {
      invokeChannelInactive(this.findContextInbound());
      return this;
   }

   static void invokeChannelInactive(final AbstractChannelHandlerContext var0) {
      EventExecutor var1 = var0.executor();
      if (var1.inEventLoop()) {
         var0.invokeChannelInactive();
      } else {
         var1.execute(new Runnable() {
            public void run() {
               var0.invokeChannelInactive();
            }
         });
      }

   }

   private void invokeChannelInactive() {
      if (this.invokeHandler()) {
         try {
            ((ChannelInboundHandler)this.handler()).channelInactive(this);
         } catch (Throwable var2) {
            this.notifyHandlerException(var2);
         }
      } else {
         this.fireChannelInactive();
      }

   }

   public ChannelHandlerContext fireExceptionCaught(Throwable var1) {
      invokeExceptionCaught(this.next, var1);
      return this;
   }

   static void invokeExceptionCaught(final AbstractChannelHandlerContext var0, final Throwable var1) {
      ObjectUtil.checkNotNull(var1, "cause");
      EventExecutor var2 = var0.executor();
      if (var2.inEventLoop()) {
         var0.invokeExceptionCaught(var1);
      } else {
         try {
            var2.execute(new Runnable() {
               public void run() {
                  var0.invokeExceptionCaught(var1);
               }
            });
         } catch (Throwable var4) {
            if (logger.isWarnEnabled()) {
               logger.warn("Failed to submit an exceptionCaught() event.", var4);
               logger.warn("The exceptionCaught() event that was failed to submit was:", var1);
            }
         }
      }

   }

   private void invokeExceptionCaught(Throwable var1) {
      if (this.invokeHandler()) {
         try {
            this.handler().exceptionCaught(this, var1);
         } catch (Throwable var3) {
            if (logger.isDebugEnabled()) {
               logger.debug("An exception {}was thrown by a user handler's exceptionCaught() method while handling the following exception:", ThrowableUtil.stackTraceToString(var3), var1);
            } else if (logger.isWarnEnabled()) {
               logger.warn("An exception '{}' [enable DEBUG level for full stacktrace] was thrown by a user handler's exceptionCaught() method while handling the following exception:", var3, var1);
            }
         }
      } else {
         this.fireExceptionCaught(var1);
      }

   }

   public ChannelHandlerContext fireUserEventTriggered(Object var1) {
      invokeUserEventTriggered(this.findContextInbound(), var1);
      return this;
   }

   static void invokeUserEventTriggered(final AbstractChannelHandlerContext var0, final Object var1) {
      ObjectUtil.checkNotNull(var1, "event");
      EventExecutor var2 = var0.executor();
      if (var2.inEventLoop()) {
         var0.invokeUserEventTriggered(var1);
      } else {
         var2.execute(new Runnable() {
            public void run() {
               var0.invokeUserEventTriggered(var1);
            }
         });
      }

   }

   private void invokeUserEventTriggered(Object var1) {
      if (this.invokeHandler()) {
         try {
            ((ChannelInboundHandler)this.handler()).userEventTriggered(this, var1);
         } catch (Throwable var3) {
            this.notifyHandlerException(var3);
         }
      } else {
         this.fireUserEventTriggered(var1);
      }

   }

   public ChannelHandlerContext fireChannelRead(Object var1) {
      invokeChannelRead(this.findContextInbound(), var1);
      return this;
   }

   static void invokeChannelRead(final AbstractChannelHandlerContext var0, Object var1) {
      final Object var2 = var0.pipeline.touch(ObjectUtil.checkNotNull(var1, "msg"), var0);
      EventExecutor var3 = var0.executor();
      if (var3.inEventLoop()) {
         var0.invokeChannelRead(var2);
      } else {
         var3.execute(new Runnable() {
            public void run() {
               var0.invokeChannelRead(var2);
            }
         });
      }

   }

   private void invokeChannelRead(Object var1) {
      if (this.invokeHandler()) {
         try {
            ((ChannelInboundHandler)this.handler()).channelRead(this, var1);
         } catch (Throwable var3) {
            this.notifyHandlerException(var3);
         }
      } else {
         this.fireChannelRead(var1);
      }

   }

   public ChannelHandlerContext fireChannelReadComplete() {
      invokeChannelReadComplete(this.findContextInbound());
      return this;
   }

   static void invokeChannelReadComplete(final AbstractChannelHandlerContext var0) {
      EventExecutor var1 = var0.executor();
      if (var1.inEventLoop()) {
         var0.invokeChannelReadComplete();
      } else {
         Runnable var2 = var0.invokeChannelReadCompleteTask;
         if (var2 == null) {
            var0.invokeChannelReadCompleteTask = var2 = new Runnable() {
               public void run() {
                  var0.invokeChannelReadComplete();
               }
            };
         }

         var1.execute(var2);
      }

   }

   private void invokeChannelReadComplete() {
      if (this.invokeHandler()) {
         try {
            ((ChannelInboundHandler)this.handler()).channelReadComplete(this);
         } catch (Throwable var2) {
            this.notifyHandlerException(var2);
         }
      } else {
         this.fireChannelReadComplete();
      }

   }

   public ChannelHandlerContext fireChannelWritabilityChanged() {
      invokeChannelWritabilityChanged(this.findContextInbound());
      return this;
   }

   static void invokeChannelWritabilityChanged(final AbstractChannelHandlerContext var0) {
      EventExecutor var1 = var0.executor();
      if (var1.inEventLoop()) {
         var0.invokeChannelWritabilityChanged();
      } else {
         Runnable var2 = var0.invokeChannelWritableStateChangedTask;
         if (var2 == null) {
            var0.invokeChannelWritableStateChangedTask = var2 = new Runnable() {
               public void run() {
                  var0.invokeChannelWritabilityChanged();
               }
            };
         }

         var1.execute(var2);
      }

   }

   private void invokeChannelWritabilityChanged() {
      if (this.invokeHandler()) {
         try {
            ((ChannelInboundHandler)this.handler()).channelWritabilityChanged(this);
         } catch (Throwable var2) {
            this.notifyHandlerException(var2);
         }
      } else {
         this.fireChannelWritabilityChanged();
      }

   }

   public ChannelFuture bind(SocketAddress var1) {
      return this.bind(var1, this.newPromise());
   }

   public ChannelFuture connect(SocketAddress var1) {
      return this.connect(var1, this.newPromise());
   }

   public ChannelFuture connect(SocketAddress var1, SocketAddress var2) {
      return this.connect(var1, var2, this.newPromise());
   }

   public ChannelFuture disconnect() {
      return this.disconnect(this.newPromise());
   }

   public ChannelFuture close() {
      return this.close(this.newPromise());
   }

   public ChannelFuture deregister() {
      return this.deregister(this.newPromise());
   }

   public ChannelFuture bind(final SocketAddress var1, final ChannelPromise var2) {
      if (var1 == null) {
         throw new NullPointerException("localAddress");
      } else if (this.isNotValidPromise(var2, false)) {
         return var2;
      } else {
         final AbstractChannelHandlerContext var3 = this.findContextOutbound();
         EventExecutor var4 = var3.executor();
         if (var4.inEventLoop()) {
            var3.invokeBind(var1, var2);
         } else {
            safeExecute(var4, new Runnable() {
               public void run() {
                  var3.invokeBind(var1, var2);
               }
            }, var2, (Object)null);
         }

         return var2;
      }
   }

   private void invokeBind(SocketAddress var1, ChannelPromise var2) {
      if (this.invokeHandler()) {
         try {
            ((ChannelOutboundHandler)this.handler()).bind(this, var1, var2);
         } catch (Throwable var4) {
            notifyOutboundHandlerException(var4, var2);
         }
      } else {
         this.bind(var1, var2);
      }

   }

   public ChannelFuture connect(SocketAddress var1, ChannelPromise var2) {
      return this.connect(var1, (SocketAddress)null, var2);
   }

   public ChannelFuture connect(final SocketAddress var1, final SocketAddress var2, final ChannelPromise var3) {
      if (var1 == null) {
         throw new NullPointerException("remoteAddress");
      } else if (this.isNotValidPromise(var3, false)) {
         return var3;
      } else {
         final AbstractChannelHandlerContext var4 = this.findContextOutbound();
         EventExecutor var5 = var4.executor();
         if (var5.inEventLoop()) {
            var4.invokeConnect(var1, var2, var3);
         } else {
            safeExecute(var5, new Runnable() {
               public void run() {
                  var4.invokeConnect(var1, var2, var3);
               }
            }, var3, (Object)null);
         }

         return var3;
      }
   }

   private void invokeConnect(SocketAddress var1, SocketAddress var2, ChannelPromise var3) {
      if (this.invokeHandler()) {
         try {
            ((ChannelOutboundHandler)this.handler()).connect(this, var1, var2, var3);
         } catch (Throwable var5) {
            notifyOutboundHandlerException(var5, var3);
         }
      } else {
         this.connect(var1, var2, var3);
      }

   }

   public ChannelFuture disconnect(final ChannelPromise var1) {
      if (this.isNotValidPromise(var1, false)) {
         return var1;
      } else {
         final AbstractChannelHandlerContext var2 = this.findContextOutbound();
         EventExecutor var3 = var2.executor();
         if (var3.inEventLoop()) {
            if (!this.channel().metadata().hasDisconnect()) {
               var2.invokeClose(var1);
            } else {
               var2.invokeDisconnect(var1);
            }
         } else {
            safeExecute(var3, new Runnable() {
               public void run() {
                  if (!AbstractChannelHandlerContext.this.channel().metadata().hasDisconnect()) {
                     var2.invokeClose(var1);
                  } else {
                     var2.invokeDisconnect(var1);
                  }

               }
            }, var1, (Object)null);
         }

         return var1;
      }
   }

   private void invokeDisconnect(ChannelPromise var1) {
      if (this.invokeHandler()) {
         try {
            ((ChannelOutboundHandler)this.handler()).disconnect(this, var1);
         } catch (Throwable var3) {
            notifyOutboundHandlerException(var3, var1);
         }
      } else {
         this.disconnect(var1);
      }

   }

   public ChannelFuture close(final ChannelPromise var1) {
      if (this.isNotValidPromise(var1, false)) {
         return var1;
      } else {
         final AbstractChannelHandlerContext var2 = this.findContextOutbound();
         EventExecutor var3 = var2.executor();
         if (var3.inEventLoop()) {
            var2.invokeClose(var1);
         } else {
            safeExecute(var3, new Runnable() {
               public void run() {
                  var2.invokeClose(var1);
               }
            }, var1, (Object)null);
         }

         return var1;
      }
   }

   private void invokeClose(ChannelPromise var1) {
      if (this.invokeHandler()) {
         try {
            ((ChannelOutboundHandler)this.handler()).close(this, var1);
         } catch (Throwable var3) {
            notifyOutboundHandlerException(var3, var1);
         }
      } else {
         this.close(var1);
      }

   }

   public ChannelFuture deregister(final ChannelPromise var1) {
      if (this.isNotValidPromise(var1, false)) {
         return var1;
      } else {
         final AbstractChannelHandlerContext var2 = this.findContextOutbound();
         EventExecutor var3 = var2.executor();
         if (var3.inEventLoop()) {
            var2.invokeDeregister(var1);
         } else {
            safeExecute(var3, new Runnable() {
               public void run() {
                  var2.invokeDeregister(var1);
               }
            }, var1, (Object)null);
         }

         return var1;
      }
   }

   private void invokeDeregister(ChannelPromise var1) {
      if (this.invokeHandler()) {
         try {
            ((ChannelOutboundHandler)this.handler()).deregister(this, var1);
         } catch (Throwable var3) {
            notifyOutboundHandlerException(var3, var1);
         }
      } else {
         this.deregister(var1);
      }

   }

   public ChannelHandlerContext read() {
      final AbstractChannelHandlerContext var1 = this.findContextOutbound();
      EventExecutor var2 = var1.executor();
      if (var2.inEventLoop()) {
         var1.invokeRead();
      } else {
         Runnable var3 = var1.invokeReadTask;
         if (var3 == null) {
            var1.invokeReadTask = var3 = new Runnable() {
               public void run() {
                  var1.invokeRead();
               }
            };
         }

         var2.execute(var3);
      }

      return this;
   }

   private void invokeRead() {
      if (this.invokeHandler()) {
         try {
            ((ChannelOutboundHandler)this.handler()).read(this);
         } catch (Throwable var2) {
            this.notifyHandlerException(var2);
         }
      } else {
         this.read();
      }

   }

   public ChannelFuture write(Object var1) {
      return this.write(var1, this.newPromise());
   }

   public ChannelFuture write(Object var1, ChannelPromise var2) {
      if (var1 == null) {
         throw new NullPointerException("msg");
      } else {
         try {
            if (this.isNotValidPromise(var2, true)) {
               ReferenceCountUtil.release(var1);
               return var2;
            }
         } catch (RuntimeException var4) {
            ReferenceCountUtil.release(var1);
            throw var4;
         }

         this.write(var1, false, var2);
         return var2;
      }
   }

   private void invokeWrite(Object var1, ChannelPromise var2) {
      if (this.invokeHandler()) {
         this.invokeWrite0(var1, var2);
      } else {
         this.write(var1, var2);
      }

   }

   private void invokeWrite0(Object var1, ChannelPromise var2) {
      try {
         ((ChannelOutboundHandler)this.handler()).write(this, var1, var2);
      } catch (Throwable var4) {
         notifyOutboundHandlerException(var4, var2);
      }

   }

   public ChannelHandlerContext flush() {
      final AbstractChannelHandlerContext var1 = this.findContextOutbound();
      EventExecutor var2 = var1.executor();
      if (var2.inEventLoop()) {
         var1.invokeFlush();
      } else {
         Runnable var3 = var1.invokeFlushTask;
         if (var3 == null) {
            var1.invokeFlushTask = var3 = new Runnable() {
               public void run() {
                  var1.invokeFlush();
               }
            };
         }

         safeExecute(var2, var3, this.channel().voidPromise(), (Object)null);
      }

      return this;
   }

   private void invokeFlush() {
      if (this.invokeHandler()) {
         this.invokeFlush0();
      } else {
         this.flush();
      }

   }

   private void invokeFlush0() {
      try {
         ((ChannelOutboundHandler)this.handler()).flush(this);
      } catch (Throwable var2) {
         this.notifyHandlerException(var2);
      }

   }

   public ChannelFuture writeAndFlush(Object var1, ChannelPromise var2) {
      if (var1 == null) {
         throw new NullPointerException("msg");
      } else if (this.isNotValidPromise(var2, true)) {
         ReferenceCountUtil.release(var1);
         return var2;
      } else {
         this.write(var1, true, var2);
         return var2;
      }
   }

   private void invokeWriteAndFlush(Object var1, ChannelPromise var2) {
      if (this.invokeHandler()) {
         this.invokeWrite0(var1, var2);
         this.invokeFlush0();
      } else {
         this.writeAndFlush(var1, var2);
      }

   }

   private void write(Object var1, boolean var2, ChannelPromise var3) {
      AbstractChannelHandlerContext var4 = this.findContextOutbound();
      Object var5 = this.pipeline.touch(var1, var4);
      EventExecutor var6 = var4.executor();
      if (var6.inEventLoop()) {
         if (var2) {
            var4.invokeWriteAndFlush(var5, var3);
         } else {
            var4.invokeWrite(var5, var3);
         }
      } else {
         Object var7;
         if (var2) {
            var7 = AbstractChannelHandlerContext.WriteAndFlushTask.newInstance(var4, var5, var3);
         } else {
            var7 = AbstractChannelHandlerContext.WriteTask.newInstance(var4, var5, var3);
         }

         safeExecute(var6, (Runnable)var7, var3, var5);
      }

   }

   public ChannelFuture writeAndFlush(Object var1) {
      return this.writeAndFlush(var1, this.newPromise());
   }

   private static void notifyOutboundHandlerException(Throwable var0, ChannelPromise var1) {
      PromiseNotificationUtil.tryFailure(var1, var0, var1 instanceof VoidChannelPromise ? null : logger);
   }

   private void notifyHandlerException(Throwable var1) {
      if (inExceptionCaught(var1)) {
         if (logger.isWarnEnabled()) {
            logger.warn("An exception was thrown by a user handler while handling an exceptionCaught event", var1);
         }

      } else {
         this.invokeExceptionCaught(var1);
      }
   }

   private static boolean inExceptionCaught(Throwable var0) {
      do {
         StackTraceElement[] var1 = var0.getStackTrace();
         if (var1 != null) {
            StackTraceElement[] var2 = var1;
            int var3 = var1.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               StackTraceElement var5 = var2[var4];
               if (var5 == null) {
                  break;
               }

               if ("exceptionCaught".equals(var5.getMethodName())) {
                  return true;
               }
            }
         }

         var0 = var0.getCause();
      } while(var0 != null);

      return false;
   }

   public ChannelPromise newPromise() {
      return new DefaultChannelPromise(this.channel(), this.executor());
   }

   public ChannelProgressivePromise newProgressivePromise() {
      return new DefaultChannelProgressivePromise(this.channel(), this.executor());
   }

   public ChannelFuture newSucceededFuture() {
      Object var1 = this.succeededFuture;
      if (var1 == null) {
         this.succeededFuture = (ChannelFuture)(var1 = new SucceededChannelFuture(this.channel(), this.executor()));
      }

      return (ChannelFuture)var1;
   }

   public ChannelFuture newFailedFuture(Throwable var1) {
      return new FailedChannelFuture(this.channel(), this.executor(), var1);
   }

   private boolean isNotValidPromise(ChannelPromise var1, boolean var2) {
      if (var1 == null) {
         throw new NullPointerException("promise");
      } else if (var1.isDone()) {
         if (var1.isCancelled()) {
            return true;
         } else {
            throw new IllegalArgumentException("promise already done: " + var1);
         }
      } else if (var1.channel() != this.channel()) {
         throw new IllegalArgumentException(String.format("promise.channel does not match: %s (expected: %s)", var1.channel(), this.channel()));
      } else if (var1.getClass() == DefaultChannelPromise.class) {
         return false;
      } else if (!var2 && var1 instanceof VoidChannelPromise) {
         throw new IllegalArgumentException(StringUtil.simpleClassName(VoidChannelPromise.class) + " not allowed for this operation");
      } else if (var1 instanceof AbstractChannel.CloseFuture) {
         throw new IllegalArgumentException(StringUtil.simpleClassName(AbstractChannel.CloseFuture.class) + " not allowed in a pipeline");
      } else {
         return false;
      }
   }

   private AbstractChannelHandlerContext findContextInbound() {
      AbstractChannelHandlerContext var1 = this;

      do {
         var1 = var1.next;
      } while(!var1.inbound);

      return var1;
   }

   private AbstractChannelHandlerContext findContextOutbound() {
      AbstractChannelHandlerContext var1 = this;

      do {
         var1 = var1.prev;
      } while(!var1.outbound);

      return var1;
   }

   public ChannelPromise voidPromise() {
      return this.channel().voidPromise();
   }

   final void setRemoved() {
      this.handlerState = 3;
   }

   final void setAddComplete() {
      int var1;
      do {
         var1 = this.handlerState;
      } while(var1 != 3 && !HANDLER_STATE_UPDATER.compareAndSet(this, var1, 2));

   }

   final void setAddPending() {
      boolean var1 = HANDLER_STATE_UPDATER.compareAndSet(this, 0, 1);

      assert var1;

   }

   private boolean invokeHandler() {
      int var1 = this.handlerState;
      return var1 == 2 || !this.ordered && var1 == 1;
   }

   public boolean isRemoved() {
      return this.handlerState == 3;
   }

   public <T> Attribute<T> attr(AttributeKey<T> var1) {
      return this.channel().attr(var1);
   }

   public <T> boolean hasAttr(AttributeKey<T> var1) {
      return this.channel().hasAttr(var1);
   }

   private static void safeExecute(EventExecutor var0, Runnable var1, ChannelPromise var2, Object var3) {
      try {
         var0.execute(var1);
      } catch (Throwable var9) {
         Throwable var4 = var9;

         try {
            var2.setFailure(var4);
         } finally {
            if (var3 != null) {
               ReferenceCountUtil.release(var3);
            }

         }
      }

   }

   public String toHintString() {
      return '\'' + this.name + "' will handle the message from this point.";
   }

   public String toString() {
      return StringUtil.simpleClassName(ChannelHandlerContext.class) + '(' + this.name + ", " + this.channel() + ')';
   }

   static final class WriteAndFlushTask extends AbstractChannelHandlerContext.AbstractWriteTask {
      private static final Recycler<AbstractChannelHandlerContext.WriteAndFlushTask> RECYCLER = new Recycler<AbstractChannelHandlerContext.WriteAndFlushTask>() {
         protected AbstractChannelHandlerContext.WriteAndFlushTask newObject(Recycler.Handle<AbstractChannelHandlerContext.WriteAndFlushTask> var1) {
            return new AbstractChannelHandlerContext.WriteAndFlushTask(var1);
         }
      };

      private static AbstractChannelHandlerContext.WriteAndFlushTask newInstance(AbstractChannelHandlerContext var0, Object var1, ChannelPromise var2) {
         AbstractChannelHandlerContext.WriteAndFlushTask var3 = (AbstractChannelHandlerContext.WriteAndFlushTask)RECYCLER.get();
         init(var3, var0, var1, var2);
         return var3;
      }

      private WriteAndFlushTask(Recycler.Handle<AbstractChannelHandlerContext.WriteAndFlushTask> var1) {
         super(var1, null);
      }

      public void write(AbstractChannelHandlerContext var1, Object var2, ChannelPromise var3) {
         super.write(var1, var2, var3);
         var1.invokeFlush();
      }

      // $FF: synthetic method
      WriteAndFlushTask(Recycler.Handle var1, Object var2) {
         this(var1);
      }
   }

   static final class WriteTask extends AbstractChannelHandlerContext.AbstractWriteTask implements SingleThreadEventLoop.NonWakeupRunnable {
      private static final Recycler<AbstractChannelHandlerContext.WriteTask> RECYCLER = new Recycler<AbstractChannelHandlerContext.WriteTask>() {
         protected AbstractChannelHandlerContext.WriteTask newObject(Recycler.Handle<AbstractChannelHandlerContext.WriteTask> var1) {
            return new AbstractChannelHandlerContext.WriteTask(var1);
         }
      };

      private static AbstractChannelHandlerContext.WriteTask newInstance(AbstractChannelHandlerContext var0, Object var1, ChannelPromise var2) {
         AbstractChannelHandlerContext.WriteTask var3 = (AbstractChannelHandlerContext.WriteTask)RECYCLER.get();
         init(var3, var0, var1, var2);
         return var3;
      }

      private WriteTask(Recycler.Handle<AbstractChannelHandlerContext.WriteTask> var1) {
         super(var1, null);
      }

      // $FF: synthetic method
      WriteTask(Recycler.Handle var1, Object var2) {
         this(var1);
      }
   }

   abstract static class AbstractWriteTask implements Runnable {
      private static final boolean ESTIMATE_TASK_SIZE_ON_SUBMIT = SystemPropertyUtil.getBoolean("io.netty.transport.estimateSizeOnSubmit", true);
      private static final int WRITE_TASK_OVERHEAD = SystemPropertyUtil.getInt("io.netty.transport.writeTaskSizeOverhead", 48);
      private final Recycler.Handle<AbstractChannelHandlerContext.AbstractWriteTask> handle;
      private AbstractChannelHandlerContext ctx;
      private Object msg;
      private ChannelPromise promise;
      private int size;

      private AbstractWriteTask(Recycler.Handle<? extends AbstractChannelHandlerContext.AbstractWriteTask> var1) {
         super();
         this.handle = var1;
      }

      protected static void init(AbstractChannelHandlerContext.AbstractWriteTask var0, AbstractChannelHandlerContext var1, Object var2, ChannelPromise var3) {
         var0.ctx = var1;
         var0.msg = var2;
         var0.promise = var3;
         if (ESTIMATE_TASK_SIZE_ON_SUBMIT) {
            var0.size = var1.pipeline.estimatorHandle().size(var2) + WRITE_TASK_OVERHEAD;
            var1.pipeline.incrementPendingOutboundBytes((long)var0.size);
         } else {
            var0.size = 0;
         }

      }

      public final void run() {
         try {
            if (ESTIMATE_TASK_SIZE_ON_SUBMIT) {
               this.ctx.pipeline.decrementPendingOutboundBytes((long)this.size);
            }

            this.write(this.ctx, this.msg, this.promise);
         } finally {
            this.ctx = null;
            this.msg = null;
            this.promise = null;
            this.handle.recycle(this);
         }

      }

      protected void write(AbstractChannelHandlerContext var1, Object var2, ChannelPromise var3) {
         var1.invokeWrite(var2, var3);
      }

      // $FF: synthetic method
      AbstractWriteTask(Recycler.Handle var1, Object var2) {
         this(var1);
      }
   }
}
