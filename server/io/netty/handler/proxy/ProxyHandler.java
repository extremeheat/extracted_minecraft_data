package io.netty.handler.proxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.PendingWriteQueue;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.nio.channels.ConnectionPendingException;
import java.util.concurrent.TimeUnit;

public abstract class ProxyHandler extends ChannelDuplexHandler {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ProxyHandler.class);
   private static final long DEFAULT_CONNECT_TIMEOUT_MILLIS = 10000L;
   static final String AUTH_NONE = "none";
   private final SocketAddress proxyAddress;
   private volatile SocketAddress destinationAddress;
   private volatile long connectTimeoutMillis = 10000L;
   private volatile ChannelHandlerContext ctx;
   private PendingWriteQueue pendingWrites;
   private boolean finished;
   private boolean suppressChannelReadComplete;
   private boolean flushedPrematurely;
   private final ProxyHandler.LazyChannelPromise connectPromise = new ProxyHandler.LazyChannelPromise();
   private ScheduledFuture<?> connectTimeoutFuture;
   private final ChannelFutureListener writeListener = new ChannelFutureListener() {
      public void operationComplete(ChannelFuture var1) throws Exception {
         if (!var1.isSuccess()) {
            ProxyHandler.this.setConnectFailure(var1.cause());
         }

      }
   };

   protected ProxyHandler(SocketAddress var1) {
      super();
      if (var1 == null) {
         throw new NullPointerException("proxyAddress");
      } else {
         this.proxyAddress = var1;
      }
   }

   public abstract String protocol();

   public abstract String authScheme();

   public final <T extends SocketAddress> T proxyAddress() {
      return this.proxyAddress;
   }

   public final <T extends SocketAddress> T destinationAddress() {
      return this.destinationAddress;
   }

   public final boolean isConnected() {
      return this.connectPromise.isSuccess();
   }

   public final Future<Channel> connectFuture() {
      return this.connectPromise;
   }

   public final long connectTimeoutMillis() {
      return this.connectTimeoutMillis;
   }

   public final void setConnectTimeoutMillis(long var1) {
      if (var1 <= 0L) {
         var1 = 0L;
      }

      this.connectTimeoutMillis = var1;
   }

   public final void handlerAdded(ChannelHandlerContext var1) throws Exception {
      this.ctx = var1;
      this.addCodec(var1);
      if (var1.channel().isActive()) {
         this.sendInitialMessage(var1);
      }

   }

   protected abstract void addCodec(ChannelHandlerContext var1) throws Exception;

   protected abstract void removeEncoder(ChannelHandlerContext var1) throws Exception;

   protected abstract void removeDecoder(ChannelHandlerContext var1) throws Exception;

   public final void connect(ChannelHandlerContext var1, SocketAddress var2, SocketAddress var3, ChannelPromise var4) throws Exception {
      if (this.destinationAddress != null) {
         var4.setFailure(new ConnectionPendingException());
      } else {
         this.destinationAddress = var2;
         var1.connect(this.proxyAddress, var3, var4);
      }
   }

   public final void channelActive(ChannelHandlerContext var1) throws Exception {
      this.sendInitialMessage(var1);
      var1.fireChannelActive();
   }

   private void sendInitialMessage(ChannelHandlerContext var1) throws Exception {
      long var2 = this.connectTimeoutMillis;
      if (var2 > 0L) {
         this.connectTimeoutFuture = var1.executor().schedule(new Runnable() {
            public void run() {
               if (!ProxyHandler.this.connectPromise.isDone()) {
                  ProxyHandler.this.setConnectFailure(new ProxyConnectException(ProxyHandler.this.exceptionMessage("timeout")));
               }

            }
         }, var2, TimeUnit.MILLISECONDS);
      }

      Object var4 = this.newInitialMessage(var1);
      if (var4 != null) {
         this.sendToProxyServer(var4);
      }

      readIfNeeded(var1);
   }

   protected abstract Object newInitialMessage(ChannelHandlerContext var1) throws Exception;

   protected final void sendToProxyServer(Object var1) {
      this.ctx.writeAndFlush(var1).addListener(this.writeListener);
   }

   public final void channelInactive(ChannelHandlerContext var1) throws Exception {
      if (this.finished) {
         var1.fireChannelInactive();
      } else {
         this.setConnectFailure(new ProxyConnectException(this.exceptionMessage("disconnected")));
      }

   }

   public final void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      if (this.finished) {
         var1.fireExceptionCaught(var2);
      } else {
         this.setConnectFailure(var2);
      }

   }

   public final void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      if (this.finished) {
         this.suppressChannelReadComplete = false;
         var1.fireChannelRead(var2);
      } else {
         this.suppressChannelReadComplete = true;
         Throwable var3 = null;

         try {
            boolean var4 = this.handleResponse(var1, var2);
            if (var4) {
               this.setConnectSuccess();
            }
         } catch (Throwable var8) {
            var3 = var8;
         } finally {
            ReferenceCountUtil.release(var2);
            if (var3 != null) {
               this.setConnectFailure(var3);
            }

         }
      }

   }

   protected abstract boolean handleResponse(ChannelHandlerContext var1, Object var2) throws Exception;

   private void setConnectSuccess() {
      this.finished = true;
      this.cancelConnectTimeoutFuture();
      if (!this.connectPromise.isDone()) {
         boolean var1 = true;
         var1 &= this.safeRemoveEncoder();
         this.ctx.fireUserEventTriggered(new ProxyConnectionEvent(this.protocol(), this.authScheme(), this.proxyAddress, this.destinationAddress));
         var1 &= this.safeRemoveDecoder();
         if (var1) {
            this.writePendingWrites();
            if (this.flushedPrematurely) {
               this.ctx.flush();
            }

            this.connectPromise.trySuccess(this.ctx.channel());
         } else {
            ProxyConnectException var2 = new ProxyConnectException("failed to remove all codec handlers added by the proxy handler; bug?");
            this.failPendingWritesAndClose(var2);
         }
      }

   }

   private boolean safeRemoveDecoder() {
      try {
         this.removeDecoder(this.ctx);
         return true;
      } catch (Exception var2) {
         logger.warn("Failed to remove proxy decoders:", (Throwable)var2);
         return false;
      }
   }

   private boolean safeRemoveEncoder() {
      try {
         this.removeEncoder(this.ctx);
         return true;
      } catch (Exception var2) {
         logger.warn("Failed to remove proxy encoders:", (Throwable)var2);
         return false;
      }
   }

   private void setConnectFailure(Throwable var1) {
      this.finished = true;
      this.cancelConnectTimeoutFuture();
      if (!this.connectPromise.isDone()) {
         if (!(var1 instanceof ProxyConnectException)) {
            var1 = new ProxyConnectException(this.exceptionMessage(((Throwable)var1).toString()), (Throwable)var1);
         }

         this.safeRemoveDecoder();
         this.safeRemoveEncoder();
         this.failPendingWritesAndClose((Throwable)var1);
      }

   }

   private void failPendingWritesAndClose(Throwable var1) {
      this.failPendingWrites(var1);
      this.connectPromise.tryFailure(var1);
      this.ctx.fireExceptionCaught(var1);
      this.ctx.close();
   }

   private void cancelConnectTimeoutFuture() {
      if (this.connectTimeoutFuture != null) {
         this.connectTimeoutFuture.cancel(false);
         this.connectTimeoutFuture = null;
      }

   }

   protected final String exceptionMessage(String var1) {
      if (var1 == null) {
         var1 = "";
      }

      StringBuilder var2 = (new StringBuilder(128 + var1.length())).append(this.protocol()).append(", ").append(this.authScheme()).append(", ").append(this.proxyAddress).append(" => ").append(this.destinationAddress);
      if (!var1.isEmpty()) {
         var2.append(", ").append(var1);
      }

      return var2.toString();
   }

   public final void channelReadComplete(ChannelHandlerContext var1) throws Exception {
      if (this.suppressChannelReadComplete) {
         this.suppressChannelReadComplete = false;
         readIfNeeded(var1);
      } else {
         var1.fireChannelReadComplete();
      }

   }

   public final void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      if (this.finished) {
         this.writePendingWrites();
         var1.write(var2, var3);
      } else {
         this.addPendingWrite(var1, var2, var3);
      }

   }

   public final void flush(ChannelHandlerContext var1) throws Exception {
      if (this.finished) {
         this.writePendingWrites();
         var1.flush();
      } else {
         this.flushedPrematurely = true;
      }

   }

   private static void readIfNeeded(ChannelHandlerContext var0) {
      if (!var0.channel().config().isAutoRead()) {
         var0.read();
      }

   }

   private void writePendingWrites() {
      if (this.pendingWrites != null) {
         this.pendingWrites.removeAndWriteAll();
         this.pendingWrites = null;
      }

   }

   private void failPendingWrites(Throwable var1) {
      if (this.pendingWrites != null) {
         this.pendingWrites.removeAndFailAll(var1);
         this.pendingWrites = null;
      }

   }

   private void addPendingWrite(ChannelHandlerContext var1, Object var2, ChannelPromise var3) {
      PendingWriteQueue var4 = this.pendingWrites;
      if (var4 == null) {
         this.pendingWrites = var4 = new PendingWriteQueue(var1);
      }

      var4.add(var2, var3);
   }

   private final class LazyChannelPromise extends DefaultPromise<Channel> {
      private LazyChannelPromise() {
         super();
      }

      protected EventExecutor executor() {
         if (ProxyHandler.this.ctx == null) {
            throw new IllegalStateException();
         } else {
            return ProxyHandler.this.ctx.executor();
         }
      }

      // $FF: synthetic method
      LazyChannelPromise(Object var2) {
         this();
      }
   }
}
