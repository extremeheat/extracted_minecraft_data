package io.netty.channel.pool;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoop;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ThrowableUtil;
import java.util.Deque;

public class SimpleChannelPool implements ChannelPool {
   private static final AttributeKey<SimpleChannelPool> POOL_KEY = AttributeKey.newInstance("channelPool");
   private static final IllegalStateException FULL_EXCEPTION = (IllegalStateException)ThrowableUtil.unknownStackTrace(new IllegalStateException("ChannelPool full"), SimpleChannelPool.class, "releaseAndOffer(...)");
   private final Deque<Channel> deque;
   private final ChannelPoolHandler handler;
   private final ChannelHealthChecker healthCheck;
   private final Bootstrap bootstrap;
   private final boolean releaseHealthCheck;
   private final boolean lastRecentUsed;

   public SimpleChannelPool(Bootstrap var1, ChannelPoolHandler var2) {
      this(var1, var2, ChannelHealthChecker.ACTIVE);
   }

   public SimpleChannelPool(Bootstrap var1, ChannelPoolHandler var2, ChannelHealthChecker var3) {
      this(var1, var2, var3, true);
   }

   public SimpleChannelPool(Bootstrap var1, ChannelPoolHandler var2, ChannelHealthChecker var3, boolean var4) {
      this(var1, var2, var3, var4, true);
   }

   public SimpleChannelPool(Bootstrap var1, final ChannelPoolHandler var2, ChannelHealthChecker var3, boolean var4, boolean var5) {
      super();
      this.deque = PlatformDependent.newConcurrentDeque();
      this.handler = (ChannelPoolHandler)ObjectUtil.checkNotNull(var2, "handler");
      this.healthCheck = (ChannelHealthChecker)ObjectUtil.checkNotNull(var3, "healthCheck");
      this.releaseHealthCheck = var4;
      this.bootstrap = ((Bootstrap)ObjectUtil.checkNotNull(var1, "bootstrap")).clone();
      this.bootstrap.handler(new ChannelInitializer<Channel>() {
         protected void initChannel(Channel var1) throws Exception {
            assert var1.eventLoop().inEventLoop();

            var2.channelCreated(var1);
         }
      });
      this.lastRecentUsed = var5;
   }

   protected Bootstrap bootstrap() {
      return this.bootstrap;
   }

   protected ChannelPoolHandler handler() {
      return this.handler;
   }

   protected ChannelHealthChecker healthChecker() {
      return this.healthCheck;
   }

   protected boolean releaseHealthCheck() {
      return this.releaseHealthCheck;
   }

   public final Future<Channel> acquire() {
      return this.acquire(this.bootstrap.config().group().next().newPromise());
   }

   public Future<Channel> acquire(Promise<Channel> var1) {
      ObjectUtil.checkNotNull(var1, "promise");
      return this.acquireHealthyFromPoolOrNew(var1);
   }

   private Future<Channel> acquireHealthyFromPoolOrNew(final Promise<Channel> var1) {
      try {
         final Channel var2 = this.pollChannel();
         if (var2 == null) {
            Bootstrap var6 = this.bootstrap.clone();
            var6.attr(POOL_KEY, this);
            ChannelFuture var4 = this.connectChannel(var6);
            if (var4.isDone()) {
               this.notifyConnect(var4, var1);
            } else {
               var4.addListener(new ChannelFutureListener() {
                  public void operationComplete(ChannelFuture var1x) throws Exception {
                     SimpleChannelPool.this.notifyConnect(var1x, var1);
                  }
               });
            }

            return var1;
         }

         EventLoop var3 = var2.eventLoop();
         if (var3.inEventLoop()) {
            this.doHealthCheck(var2, var1);
         } else {
            var3.execute(new Runnable() {
               public void run() {
                  SimpleChannelPool.this.doHealthCheck(var2, var1);
               }
            });
         }
      } catch (Throwable var5) {
         var1.tryFailure(var5);
      }

      return var1;
   }

   private void notifyConnect(ChannelFuture var1, Promise<Channel> var2) {
      if (var1.isSuccess()) {
         Channel var3 = var1.channel();
         if (!var2.trySuccess(var3)) {
            this.release(var3);
         }
      } else {
         var2.tryFailure(var1.cause());
      }

   }

   private void doHealthCheck(final Channel var1, final Promise<Channel> var2) {
      assert var1.eventLoop().inEventLoop();

      Future var3 = this.healthCheck.isHealthy(var1);
      if (var3.isDone()) {
         this.notifyHealthCheck(var3, var1, var2);
      } else {
         var3.addListener(new FutureListener<Boolean>() {
            public void operationComplete(Future<Boolean> var1x) throws Exception {
               SimpleChannelPool.this.notifyHealthCheck(var1x, var1, var2);
            }
         });
      }

   }

   private void notifyHealthCheck(Future<Boolean> var1, Channel var2, Promise<Channel> var3) {
      assert var2.eventLoop().inEventLoop();

      if (var1.isSuccess()) {
         if ((Boolean)var1.getNow()) {
            try {
               var2.attr(POOL_KEY).set(this);
               this.handler.channelAcquired(var2);
               var3.setSuccess(var2);
            } catch (Throwable var5) {
               closeAndFail(var2, var5, var3);
            }
         } else {
            closeChannel(var2);
            this.acquireHealthyFromPoolOrNew(var3);
         }
      } else {
         closeChannel(var2);
         this.acquireHealthyFromPoolOrNew(var3);
      }

   }

   protected ChannelFuture connectChannel(Bootstrap var1) {
      return var1.connect();
   }

   public final Future<Void> release(Channel var1) {
      return this.release(var1, var1.eventLoop().newPromise());
   }

   public Future<Void> release(final Channel var1, final Promise<Void> var2) {
      ObjectUtil.checkNotNull(var1, "channel");
      ObjectUtil.checkNotNull(var2, "promise");

      try {
         EventLoop var3 = var1.eventLoop();
         if (var3.inEventLoop()) {
            this.doReleaseChannel(var1, var2);
         } else {
            var3.execute(new Runnable() {
               public void run() {
                  SimpleChannelPool.this.doReleaseChannel(var1, var2);
               }
            });
         }
      } catch (Throwable var4) {
         closeAndFail(var1, var4, var2);
      }

      return var2;
   }

   private void doReleaseChannel(Channel var1, Promise<Void> var2) {
      assert var1.eventLoop().inEventLoop();

      if (var1.attr(POOL_KEY).getAndSet((Object)null) != this) {
         closeAndFail(var1, new IllegalArgumentException("Channel " + var1 + " was not acquired from this ChannelPool"), var2);
      } else {
         try {
            if (this.releaseHealthCheck) {
               this.doHealthCheckOnRelease(var1, var2);
            } else {
               this.releaseAndOffer(var1, var2);
            }
         } catch (Throwable var4) {
            closeAndFail(var1, var4, var2);
         }
      }

   }

   private void doHealthCheckOnRelease(final Channel var1, final Promise<Void> var2) throws Exception {
      final Future var3 = this.healthCheck.isHealthy(var1);
      if (var3.isDone()) {
         this.releaseAndOfferIfHealthy(var1, var2, var3);
      } else {
         var3.addListener(new FutureListener<Boolean>() {
            public void operationComplete(Future<Boolean> var1x) throws Exception {
               SimpleChannelPool.this.releaseAndOfferIfHealthy(var1, var2, var3);
            }
         });
      }

   }

   private void releaseAndOfferIfHealthy(Channel var1, Promise<Void> var2, Future<Boolean> var3) throws Exception {
      if ((Boolean)var3.getNow()) {
         this.releaseAndOffer(var1, var2);
      } else {
         this.handler.channelReleased(var1);
         var2.setSuccess((Object)null);
      }

   }

   private void releaseAndOffer(Channel var1, Promise<Void> var2) throws Exception {
      if (this.offerChannel(var1)) {
         this.handler.channelReleased(var1);
         var2.setSuccess((Object)null);
      } else {
         closeAndFail(var1, FULL_EXCEPTION, var2);
      }

   }

   private static void closeChannel(Channel var0) {
      var0.attr(POOL_KEY).getAndSet((Object)null);
      var0.close();
   }

   private static void closeAndFail(Channel var0, Throwable var1, Promise<?> var2) {
      closeChannel(var0);
      var2.tryFailure(var1);
   }

   protected Channel pollChannel() {
      return this.lastRecentUsed ? (Channel)this.deque.pollLast() : (Channel)this.deque.pollFirst();
   }

   protected boolean offerChannel(Channel var1) {
      return this.deque.offer(var1);
   }

   public void close() {
      while(true) {
         Channel var1 = this.pollChannel();
         if (var1 == null) {
            return;
         }

         var1.close();
      }
   }
}
