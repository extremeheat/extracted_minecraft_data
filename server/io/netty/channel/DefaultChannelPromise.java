package io.netty.channel;

import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;

public class DefaultChannelPromise extends DefaultPromise<Void> implements ChannelPromise, ChannelFlushPromiseNotifier.FlushCheckpoint {
   private final Channel channel;
   private long checkpoint;

   public DefaultChannelPromise(Channel var1) {
      super();
      this.channel = (Channel)ObjectUtil.checkNotNull(var1, "channel");
   }

   public DefaultChannelPromise(Channel var1, EventExecutor var2) {
      super(var2);
      this.channel = (Channel)ObjectUtil.checkNotNull(var1, "channel");
   }

   protected EventExecutor executor() {
      EventExecutor var1 = super.executor();
      return (EventExecutor)(var1 == null ? this.channel().eventLoop() : var1);
   }

   public Channel channel() {
      return this.channel;
   }

   public ChannelPromise setSuccess() {
      return this.setSuccess((Void)null);
   }

   public ChannelPromise setSuccess(Void var1) {
      super.setSuccess(var1);
      return this;
   }

   public boolean trySuccess() {
      return this.trySuccess((Object)null);
   }

   public ChannelPromise setFailure(Throwable var1) {
      super.setFailure(var1);
      return this;
   }

   public ChannelPromise addListener(GenericFutureListener<? extends Future<? super Void>> var1) {
      super.addListener(var1);
      return this;
   }

   public ChannelPromise addListeners(GenericFutureListener<? extends Future<? super Void>>... var1) {
      super.addListeners(var1);
      return this;
   }

   public ChannelPromise removeListener(GenericFutureListener<? extends Future<? super Void>> var1) {
      super.removeListener(var1);
      return this;
   }

   public ChannelPromise removeListeners(GenericFutureListener<? extends Future<? super Void>>... var1) {
      super.removeListeners(var1);
      return this;
   }

   public ChannelPromise sync() throws InterruptedException {
      super.sync();
      return this;
   }

   public ChannelPromise syncUninterruptibly() {
      super.syncUninterruptibly();
      return this;
   }

   public ChannelPromise await() throws InterruptedException {
      super.await();
      return this;
   }

   public ChannelPromise awaitUninterruptibly() {
      super.awaitUninterruptibly();
      return this;
   }

   public long flushCheckpoint() {
      return this.checkpoint;
   }

   public void flushCheckpoint(long var1) {
      this.checkpoint = var1;
   }

   public ChannelPromise promise() {
      return this;
   }

   protected void checkDeadLock() {
      if (this.channel().isRegistered()) {
         super.checkDeadLock();
      }

   }

   public ChannelPromise unvoid() {
      return this;
   }

   public boolean isVoid() {
      return false;
   }
}
