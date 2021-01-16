package io.netty.channel;

import io.netty.util.concurrent.AbstractFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.concurrent.TimeUnit;

public final class VoidChannelPromise extends AbstractFuture<Void> implements ChannelPromise {
   private final Channel channel;
   private final ChannelFutureListener fireExceptionListener;

   public VoidChannelPromise(Channel var1, boolean var2) {
      super();
      if (var1 == null) {
         throw new NullPointerException("channel");
      } else {
         this.channel = var1;
         if (var2) {
            this.fireExceptionListener = new ChannelFutureListener() {
               public void operationComplete(ChannelFuture var1) throws Exception {
                  Throwable var2 = var1.cause();
                  if (var2 != null) {
                     VoidChannelPromise.this.fireException0(var2);
                  }

               }
            };
         } else {
            this.fireExceptionListener = null;
         }

      }
   }

   public VoidChannelPromise addListener(GenericFutureListener<? extends Future<? super Void>> var1) {
      fail();
      return this;
   }

   public VoidChannelPromise addListeners(GenericFutureListener<? extends Future<? super Void>>... var1) {
      fail();
      return this;
   }

   public VoidChannelPromise removeListener(GenericFutureListener<? extends Future<? super Void>> var1) {
      return this;
   }

   public VoidChannelPromise removeListeners(GenericFutureListener<? extends Future<? super Void>>... var1) {
      return this;
   }

   public VoidChannelPromise await() throws InterruptedException {
      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         return this;
      }
   }

   public boolean await(long var1, TimeUnit var3) {
      fail();
      return false;
   }

   public boolean await(long var1) {
      fail();
      return false;
   }

   public VoidChannelPromise awaitUninterruptibly() {
      fail();
      return this;
   }

   public boolean awaitUninterruptibly(long var1, TimeUnit var3) {
      fail();
      return false;
   }

   public boolean awaitUninterruptibly(long var1) {
      fail();
      return false;
   }

   public Channel channel() {
      return this.channel;
   }

   public boolean isDone() {
      return false;
   }

   public boolean isSuccess() {
      return false;
   }

   public boolean setUncancellable() {
      return true;
   }

   public boolean isCancellable() {
      return false;
   }

   public boolean isCancelled() {
      return false;
   }

   public Throwable cause() {
      return null;
   }

   public VoidChannelPromise sync() {
      fail();
      return this;
   }

   public VoidChannelPromise syncUninterruptibly() {
      fail();
      return this;
   }

   public VoidChannelPromise setFailure(Throwable var1) {
      this.fireException0(var1);
      return this;
   }

   public VoidChannelPromise setSuccess() {
      return this;
   }

   public boolean tryFailure(Throwable var1) {
      this.fireException0(var1);
      return false;
   }

   public boolean cancel(boolean var1) {
      return false;
   }

   public boolean trySuccess() {
      return false;
   }

   private static void fail() {
      throw new IllegalStateException("void future");
   }

   public VoidChannelPromise setSuccess(Void var1) {
      return this;
   }

   public boolean trySuccess(Void var1) {
      return false;
   }

   public Void getNow() {
      return null;
   }

   public ChannelPromise unvoid() {
      DefaultChannelPromise var1 = new DefaultChannelPromise(this.channel);
      if (this.fireExceptionListener != null) {
         var1.addListener(this.fireExceptionListener);
      }

      return var1;
   }

   public boolean isVoid() {
      return true;
   }

   private void fireException0(Throwable var1) {
      if (this.fireExceptionListener != null && this.channel.isRegistered()) {
         this.channel.pipeline().fireExceptionCaught(var1);
      }

   }
}
