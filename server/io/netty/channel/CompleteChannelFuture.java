package io.netty.channel;

import io.netty.util.concurrent.CompleteFuture;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

abstract class CompleteChannelFuture extends CompleteFuture<Void> implements ChannelFuture {
   private final Channel channel;

   protected CompleteChannelFuture(Channel var1, EventExecutor var2) {
      super(var2);
      if (var1 == null) {
         throw new NullPointerException("channel");
      } else {
         this.channel = var1;
      }
   }

   protected EventExecutor executor() {
      EventExecutor var1 = super.executor();
      return (EventExecutor)(var1 == null ? this.channel().eventLoop() : var1);
   }

   public ChannelFuture addListener(GenericFutureListener<? extends Future<? super Void>> var1) {
      super.addListener(var1);
      return this;
   }

   public ChannelFuture addListeners(GenericFutureListener<? extends Future<? super Void>>... var1) {
      super.addListeners(var1);
      return this;
   }

   public ChannelFuture removeListener(GenericFutureListener<? extends Future<? super Void>> var1) {
      super.removeListener(var1);
      return this;
   }

   public ChannelFuture removeListeners(GenericFutureListener<? extends Future<? super Void>>... var1) {
      super.removeListeners(var1);
      return this;
   }

   public ChannelFuture syncUninterruptibly() {
      return this;
   }

   public ChannelFuture sync() throws InterruptedException {
      return this;
   }

   public ChannelFuture await() throws InterruptedException {
      return this;
   }

   public ChannelFuture awaitUninterruptibly() {
      return this;
   }

   public Channel channel() {
      return this.channel;
   }

   public Void getNow() {
      return null;
   }

   public boolean isVoid() {
      return false;
   }
}
