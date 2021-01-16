package io.netty.channel;

import io.netty.util.concurrent.DefaultProgressivePromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class DefaultChannelProgressivePromise extends DefaultProgressivePromise<Void> implements ChannelProgressivePromise, ChannelFlushPromiseNotifier.FlushCheckpoint {
   private final Channel channel;
   private long checkpoint;

   public DefaultChannelProgressivePromise(Channel var1) {
      super();
      this.channel = var1;
   }

   public DefaultChannelProgressivePromise(Channel var1, EventExecutor var2) {
      super(var2);
      this.channel = var1;
   }

   protected EventExecutor executor() {
      EventExecutor var1 = super.executor();
      return (EventExecutor)(var1 == null ? this.channel().eventLoop() : var1);
   }

   public Channel channel() {
      return this.channel;
   }

   public ChannelProgressivePromise setSuccess() {
      return this.setSuccess((Void)null);
   }

   public ChannelProgressivePromise setSuccess(Void var1) {
      super.setSuccess(var1);
      return this;
   }

   public boolean trySuccess() {
      return this.trySuccess((Object)null);
   }

   public ChannelProgressivePromise setFailure(Throwable var1) {
      super.setFailure(var1);
      return this;
   }

   public ChannelProgressivePromise setProgress(long var1, long var3) {
      super.setProgress(var1, var3);
      return this;
   }

   public ChannelProgressivePromise addListener(GenericFutureListener<? extends Future<? super Void>> var1) {
      super.addListener(var1);
      return this;
   }

   public ChannelProgressivePromise addListeners(GenericFutureListener<? extends Future<? super Void>>... var1) {
      super.addListeners(var1);
      return this;
   }

   public ChannelProgressivePromise removeListener(GenericFutureListener<? extends Future<? super Void>> var1) {
      super.removeListener(var1);
      return this;
   }

   public ChannelProgressivePromise removeListeners(GenericFutureListener<? extends Future<? super Void>>... var1) {
      super.removeListeners(var1);
      return this;
   }

   public ChannelProgressivePromise sync() throws InterruptedException {
      super.sync();
      return this;
   }

   public ChannelProgressivePromise syncUninterruptibly() {
      super.syncUninterruptibly();
      return this;
   }

   public ChannelProgressivePromise await() throws InterruptedException {
      super.await();
      return this;
   }

   public ChannelProgressivePromise awaitUninterruptibly() {
      super.awaitUninterruptibly();
      return this;
   }

   public long flushCheckpoint() {
      return this.checkpoint;
   }

   public void flushCheckpoint(long var1) {
      this.checkpoint = var1;
   }

   public ChannelProgressivePromise promise() {
      return this;
   }

   protected void checkDeadLock() {
      if (this.channel().isRegistered()) {
         super.checkDeadLock();
      }

   }

   public ChannelProgressivePromise unvoid() {
      return this;
   }

   public boolean isVoid() {
      return false;
   }
}
