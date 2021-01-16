package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;

public class CombinedChannelDuplexHandler<I extends ChannelInboundHandler, O extends ChannelOutboundHandler> extends ChannelDuplexHandler {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(CombinedChannelDuplexHandler.class);
   private CombinedChannelDuplexHandler.DelegatingChannelHandlerContext inboundCtx;
   private CombinedChannelDuplexHandler.DelegatingChannelHandlerContext outboundCtx;
   private volatile boolean handlerAdded;
   private I inboundHandler;
   private O outboundHandler;

   protected CombinedChannelDuplexHandler() {
      super();
      this.ensureNotSharable();
   }

   public CombinedChannelDuplexHandler(I var1, O var2) {
      super();
      this.ensureNotSharable();
      this.init(var1, var2);
   }

   protected final void init(I var1, O var2) {
      this.validate(var1, var2);
      this.inboundHandler = var1;
      this.outboundHandler = var2;
   }

   private void validate(I var1, O var2) {
      if (this.inboundHandler != null) {
         throw new IllegalStateException("init() can not be invoked if " + CombinedChannelDuplexHandler.class.getSimpleName() + " was constructed with non-default constructor.");
      } else if (var1 == null) {
         throw new NullPointerException("inboundHandler");
      } else if (var2 == null) {
         throw new NullPointerException("outboundHandler");
      } else if (var1 instanceof ChannelOutboundHandler) {
         throw new IllegalArgumentException("inboundHandler must not implement " + ChannelOutboundHandler.class.getSimpleName() + " to get combined.");
      } else if (var2 instanceof ChannelInboundHandler) {
         throw new IllegalArgumentException("outboundHandler must not implement " + ChannelInboundHandler.class.getSimpleName() + " to get combined.");
      }
   }

   protected final I inboundHandler() {
      return this.inboundHandler;
   }

   protected final O outboundHandler() {
      return this.outboundHandler;
   }

   private void checkAdded() {
      if (!this.handlerAdded) {
         throw new IllegalStateException("handler not added to pipeline yet");
      }
   }

   public final void removeInboundHandler() {
      this.checkAdded();
      this.inboundCtx.remove();
   }

   public final void removeOutboundHandler() {
      this.checkAdded();
      this.outboundCtx.remove();
   }

   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      if (this.inboundHandler == null) {
         throw new IllegalStateException("init() must be invoked before being added to a " + ChannelPipeline.class.getSimpleName() + " if " + CombinedChannelDuplexHandler.class.getSimpleName() + " was constructed with the default constructor.");
      } else {
         this.outboundCtx = new CombinedChannelDuplexHandler.DelegatingChannelHandlerContext(var1, this.outboundHandler);
         this.inboundCtx = new CombinedChannelDuplexHandler.DelegatingChannelHandlerContext(var1, this.inboundHandler) {
            public ChannelHandlerContext fireExceptionCaught(Throwable var1) {
               if (!CombinedChannelDuplexHandler.this.outboundCtx.removed) {
                  try {
                     CombinedChannelDuplexHandler.this.outboundHandler.exceptionCaught(CombinedChannelDuplexHandler.this.outboundCtx, var1);
                  } catch (Throwable var3) {
                     if (CombinedChannelDuplexHandler.logger.isDebugEnabled()) {
                        CombinedChannelDuplexHandler.logger.debug("An exception {}was thrown by a user handler's exceptionCaught() method while handling the following exception:", ThrowableUtil.stackTraceToString(var3), var1);
                     } else if (CombinedChannelDuplexHandler.logger.isWarnEnabled()) {
                        CombinedChannelDuplexHandler.logger.warn("An exception '{}' [enable DEBUG level for full stacktrace] was thrown by a user handler's exceptionCaught() method while handling the following exception:", var3, var1);
                     }
                  }
               } else {
                  super.fireExceptionCaught(var1);
               }

               return this;
            }
         };
         this.handlerAdded = true;

         try {
            this.inboundHandler.handlerAdded(this.inboundCtx);
         } finally {
            this.outboundHandler.handlerAdded(this.outboundCtx);
         }

      }
   }

   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      try {
         this.inboundCtx.remove();
      } finally {
         this.outboundCtx.remove();
      }

   }

   public void channelRegistered(ChannelHandlerContext var1) throws Exception {
      assert var1 == this.inboundCtx.ctx;

      if (!this.inboundCtx.removed) {
         this.inboundHandler.channelRegistered(this.inboundCtx);
      } else {
         this.inboundCtx.fireChannelRegistered();
      }

   }

   public void channelUnregistered(ChannelHandlerContext var1) throws Exception {
      assert var1 == this.inboundCtx.ctx;

      if (!this.inboundCtx.removed) {
         this.inboundHandler.channelUnregistered(this.inboundCtx);
      } else {
         this.inboundCtx.fireChannelUnregistered();
      }

   }

   public void channelActive(ChannelHandlerContext var1) throws Exception {
      assert var1 == this.inboundCtx.ctx;

      if (!this.inboundCtx.removed) {
         this.inboundHandler.channelActive(this.inboundCtx);
      } else {
         this.inboundCtx.fireChannelActive();
      }

   }

   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      assert var1 == this.inboundCtx.ctx;

      if (!this.inboundCtx.removed) {
         this.inboundHandler.channelInactive(this.inboundCtx);
      } else {
         this.inboundCtx.fireChannelInactive();
      }

   }

   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      assert var1 == this.inboundCtx.ctx;

      if (!this.inboundCtx.removed) {
         this.inboundHandler.exceptionCaught(this.inboundCtx, var2);
      } else {
         this.inboundCtx.fireExceptionCaught(var2);
      }

   }

   public void userEventTriggered(ChannelHandlerContext var1, Object var2) throws Exception {
      assert var1 == this.inboundCtx.ctx;

      if (!this.inboundCtx.removed) {
         this.inboundHandler.userEventTriggered(this.inboundCtx, var2);
      } else {
         this.inboundCtx.fireUserEventTriggered(var2);
      }

   }

   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      assert var1 == this.inboundCtx.ctx;

      if (!this.inboundCtx.removed) {
         this.inboundHandler.channelRead(this.inboundCtx, var2);
      } else {
         this.inboundCtx.fireChannelRead(var2);
      }

   }

   public void channelReadComplete(ChannelHandlerContext var1) throws Exception {
      assert var1 == this.inboundCtx.ctx;

      if (!this.inboundCtx.removed) {
         this.inboundHandler.channelReadComplete(this.inboundCtx);
      } else {
         this.inboundCtx.fireChannelReadComplete();
      }

   }

   public void channelWritabilityChanged(ChannelHandlerContext var1) throws Exception {
      assert var1 == this.inboundCtx.ctx;

      if (!this.inboundCtx.removed) {
         this.inboundHandler.channelWritabilityChanged(this.inboundCtx);
      } else {
         this.inboundCtx.fireChannelWritabilityChanged();
      }

   }

   public void bind(ChannelHandlerContext var1, SocketAddress var2, ChannelPromise var3) throws Exception {
      assert var1 == this.outboundCtx.ctx;

      if (!this.outboundCtx.removed) {
         this.outboundHandler.bind(this.outboundCtx, var2, var3);
      } else {
         this.outboundCtx.bind(var2, var3);
      }

   }

   public void connect(ChannelHandlerContext var1, SocketAddress var2, SocketAddress var3, ChannelPromise var4) throws Exception {
      assert var1 == this.outboundCtx.ctx;

      if (!this.outboundCtx.removed) {
         this.outboundHandler.connect(this.outboundCtx, var2, var3, var4);
      } else {
         this.outboundCtx.connect(var3, var4);
      }

   }

   public void disconnect(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      assert var1 == this.outboundCtx.ctx;

      if (!this.outboundCtx.removed) {
         this.outboundHandler.disconnect(this.outboundCtx, var2);
      } else {
         this.outboundCtx.disconnect(var2);
      }

   }

   public void close(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      assert var1 == this.outboundCtx.ctx;

      if (!this.outboundCtx.removed) {
         this.outboundHandler.close(this.outboundCtx, var2);
      } else {
         this.outboundCtx.close(var2);
      }

   }

   public void deregister(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      assert var1 == this.outboundCtx.ctx;

      if (!this.outboundCtx.removed) {
         this.outboundHandler.deregister(this.outboundCtx, var2);
      } else {
         this.outboundCtx.deregister(var2);
      }

   }

   public void read(ChannelHandlerContext var1) throws Exception {
      assert var1 == this.outboundCtx.ctx;

      if (!this.outboundCtx.removed) {
         this.outboundHandler.read(this.outboundCtx);
      } else {
         this.outboundCtx.read();
      }

   }

   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      assert var1 == this.outboundCtx.ctx;

      if (!this.outboundCtx.removed) {
         this.outboundHandler.write(this.outboundCtx, var2, var3);
      } else {
         this.outboundCtx.write(var2, var3);
      }

   }

   public void flush(ChannelHandlerContext var1) throws Exception {
      assert var1 == this.outboundCtx.ctx;

      if (!this.outboundCtx.removed) {
         this.outboundHandler.flush(this.outboundCtx);
      } else {
         this.outboundCtx.flush();
      }

   }

   private static class DelegatingChannelHandlerContext implements ChannelHandlerContext {
      private final ChannelHandlerContext ctx;
      private final ChannelHandler handler;
      boolean removed;

      DelegatingChannelHandlerContext(ChannelHandlerContext var1, ChannelHandler var2) {
         super();
         this.ctx = var1;
         this.handler = var2;
      }

      public Channel channel() {
         return this.ctx.channel();
      }

      public EventExecutor executor() {
         return this.ctx.executor();
      }

      public String name() {
         return this.ctx.name();
      }

      public ChannelHandler handler() {
         return this.ctx.handler();
      }

      public boolean isRemoved() {
         return this.removed || this.ctx.isRemoved();
      }

      public ChannelHandlerContext fireChannelRegistered() {
         this.ctx.fireChannelRegistered();
         return this;
      }

      public ChannelHandlerContext fireChannelUnregistered() {
         this.ctx.fireChannelUnregistered();
         return this;
      }

      public ChannelHandlerContext fireChannelActive() {
         this.ctx.fireChannelActive();
         return this;
      }

      public ChannelHandlerContext fireChannelInactive() {
         this.ctx.fireChannelInactive();
         return this;
      }

      public ChannelHandlerContext fireExceptionCaught(Throwable var1) {
         this.ctx.fireExceptionCaught(var1);
         return this;
      }

      public ChannelHandlerContext fireUserEventTriggered(Object var1) {
         this.ctx.fireUserEventTriggered(var1);
         return this;
      }

      public ChannelHandlerContext fireChannelRead(Object var1) {
         this.ctx.fireChannelRead(var1);
         return this;
      }

      public ChannelHandlerContext fireChannelReadComplete() {
         this.ctx.fireChannelReadComplete();
         return this;
      }

      public ChannelHandlerContext fireChannelWritabilityChanged() {
         this.ctx.fireChannelWritabilityChanged();
         return this;
      }

      public ChannelFuture bind(SocketAddress var1) {
         return this.ctx.bind(var1);
      }

      public ChannelFuture connect(SocketAddress var1) {
         return this.ctx.connect(var1);
      }

      public ChannelFuture connect(SocketAddress var1, SocketAddress var2) {
         return this.ctx.connect(var1, var2);
      }

      public ChannelFuture disconnect() {
         return this.ctx.disconnect();
      }

      public ChannelFuture close() {
         return this.ctx.close();
      }

      public ChannelFuture deregister() {
         return this.ctx.deregister();
      }

      public ChannelFuture bind(SocketAddress var1, ChannelPromise var2) {
         return this.ctx.bind(var1, var2);
      }

      public ChannelFuture connect(SocketAddress var1, ChannelPromise var2) {
         return this.ctx.connect(var1, var2);
      }

      public ChannelFuture connect(SocketAddress var1, SocketAddress var2, ChannelPromise var3) {
         return this.ctx.connect(var1, var2, var3);
      }

      public ChannelFuture disconnect(ChannelPromise var1) {
         return this.ctx.disconnect(var1);
      }

      public ChannelFuture close(ChannelPromise var1) {
         return this.ctx.close(var1);
      }

      public ChannelFuture deregister(ChannelPromise var1) {
         return this.ctx.deregister(var1);
      }

      public ChannelHandlerContext read() {
         this.ctx.read();
         return this;
      }

      public ChannelFuture write(Object var1) {
         return this.ctx.write(var1);
      }

      public ChannelFuture write(Object var1, ChannelPromise var2) {
         return this.ctx.write(var1, var2);
      }

      public ChannelHandlerContext flush() {
         this.ctx.flush();
         return this;
      }

      public ChannelFuture writeAndFlush(Object var1, ChannelPromise var2) {
         return this.ctx.writeAndFlush(var1, var2);
      }

      public ChannelFuture writeAndFlush(Object var1) {
         return this.ctx.writeAndFlush(var1);
      }

      public ChannelPipeline pipeline() {
         return this.ctx.pipeline();
      }

      public ByteBufAllocator alloc() {
         return this.ctx.alloc();
      }

      public ChannelPromise newPromise() {
         return this.ctx.newPromise();
      }

      public ChannelProgressivePromise newProgressivePromise() {
         return this.ctx.newProgressivePromise();
      }

      public ChannelFuture newSucceededFuture() {
         return this.ctx.newSucceededFuture();
      }

      public ChannelFuture newFailedFuture(Throwable var1) {
         return this.ctx.newFailedFuture(var1);
      }

      public ChannelPromise voidPromise() {
         return this.ctx.voidPromise();
      }

      public <T> Attribute<T> attr(AttributeKey<T> var1) {
         return this.ctx.channel().attr(var1);
      }

      public <T> boolean hasAttr(AttributeKey<T> var1) {
         return this.ctx.channel().hasAttr(var1);
      }

      final void remove() {
         EventExecutor var1 = this.executor();
         if (var1.inEventLoop()) {
            this.remove0();
         } else {
            var1.execute(new Runnable() {
               public void run() {
                  DelegatingChannelHandlerContext.this.remove0();
               }
            });
         }

      }

      private void remove0() {
         if (!this.removed) {
            this.removed = true;

            try {
               this.handler.handlerRemoved(this);
            } catch (Throwable var2) {
               this.fireExceptionCaught(new ChannelPipelineException(this.handler.getClass().getName() + ".handlerRemoved() has thrown an exception.", var2));
            }
         }

      }
   }
}
