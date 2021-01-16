package io.netty.channel.group;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.BlockingOperationException;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ImmediateEventExecutor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

final class DefaultChannelGroupFuture extends DefaultPromise<Void> implements ChannelGroupFuture {
   private final ChannelGroup group;
   private final Map<Channel, ChannelFuture> futures;
   private int successCount;
   private int failureCount;
   private final ChannelFutureListener childListener = new ChannelFutureListener() {
      public void operationComplete(ChannelFuture var1) throws Exception {
         boolean var2 = var1.isSuccess();
         boolean var3;
         synchronized(DefaultChannelGroupFuture.this) {
            if (var2) {
               DefaultChannelGroupFuture.this.successCount++;
            } else {
               DefaultChannelGroupFuture.this.failureCount++;
            }

            var3 = DefaultChannelGroupFuture.this.successCount + DefaultChannelGroupFuture.this.failureCount == DefaultChannelGroupFuture.this.futures.size();

            assert DefaultChannelGroupFuture.this.successCount + DefaultChannelGroupFuture.this.failureCount <= DefaultChannelGroupFuture.this.futures.size();
         }

         if (var3) {
            if (DefaultChannelGroupFuture.this.failureCount > 0) {
               ArrayList var4 = new ArrayList(DefaultChannelGroupFuture.this.failureCount);
               Iterator var5 = DefaultChannelGroupFuture.this.futures.values().iterator();

               while(var5.hasNext()) {
                  ChannelFuture var6 = (ChannelFuture)var5.next();
                  if (!var6.isSuccess()) {
                     var4.add(new DefaultChannelGroupFuture.DefaultEntry(var6.channel(), var6.cause()));
                  }
               }

               DefaultChannelGroupFuture.this.setFailure0(new ChannelGroupException(var4));
            } else {
               DefaultChannelGroupFuture.this.setSuccess0();
            }
         }

      }
   };

   DefaultChannelGroupFuture(ChannelGroup var1, Collection<ChannelFuture> var2, EventExecutor var3) {
      super(var3);
      if (var1 == null) {
         throw new NullPointerException("group");
      } else if (var2 == null) {
         throw new NullPointerException("futures");
      } else {
         this.group = var1;
         LinkedHashMap var4 = new LinkedHashMap();
         Iterator var5 = var2.iterator();

         ChannelFuture var6;
         while(var5.hasNext()) {
            var6 = (ChannelFuture)var5.next();
            var4.put(var6.channel(), var6);
         }

         this.futures = Collections.unmodifiableMap(var4);
         var5 = this.futures.values().iterator();

         while(var5.hasNext()) {
            var6 = (ChannelFuture)var5.next();
            var6.addListener(this.childListener);
         }

         if (this.futures.isEmpty()) {
            this.setSuccess0();
         }

      }
   }

   DefaultChannelGroupFuture(ChannelGroup var1, Map<Channel, ChannelFuture> var2, EventExecutor var3) {
      super(var3);
      this.group = var1;
      this.futures = Collections.unmodifiableMap(var2);
      Iterator var4 = this.futures.values().iterator();

      while(var4.hasNext()) {
         ChannelFuture var5 = (ChannelFuture)var4.next();
         var5.addListener(this.childListener);
      }

      if (this.futures.isEmpty()) {
         this.setSuccess0();
      }

   }

   public ChannelGroup group() {
      return this.group;
   }

   public ChannelFuture find(Channel var1) {
      return (ChannelFuture)this.futures.get(var1);
   }

   public Iterator<ChannelFuture> iterator() {
      return this.futures.values().iterator();
   }

   public synchronized boolean isPartialSuccess() {
      return this.successCount != 0 && this.successCount != this.futures.size();
   }

   public synchronized boolean isPartialFailure() {
      return this.failureCount != 0 && this.failureCount != this.futures.size();
   }

   public DefaultChannelGroupFuture addListener(GenericFutureListener<? extends Future<? super Void>> var1) {
      super.addListener(var1);
      return this;
   }

   public DefaultChannelGroupFuture addListeners(GenericFutureListener<? extends Future<? super Void>>... var1) {
      super.addListeners(var1);
      return this;
   }

   public DefaultChannelGroupFuture removeListener(GenericFutureListener<? extends Future<? super Void>> var1) {
      super.removeListener(var1);
      return this;
   }

   public DefaultChannelGroupFuture removeListeners(GenericFutureListener<? extends Future<? super Void>>... var1) {
      super.removeListeners(var1);
      return this;
   }

   public DefaultChannelGroupFuture await() throws InterruptedException {
      super.await();
      return this;
   }

   public DefaultChannelGroupFuture awaitUninterruptibly() {
      super.awaitUninterruptibly();
      return this;
   }

   public DefaultChannelGroupFuture syncUninterruptibly() {
      super.syncUninterruptibly();
      return this;
   }

   public DefaultChannelGroupFuture sync() throws InterruptedException {
      super.sync();
      return this;
   }

   public ChannelGroupException cause() {
      return (ChannelGroupException)super.cause();
   }

   private void setSuccess0() {
      super.setSuccess((Object)null);
   }

   private void setFailure0(ChannelGroupException var1) {
      super.setFailure(var1);
   }

   public DefaultChannelGroupFuture setSuccess(Void var1) {
      throw new IllegalStateException();
   }

   public boolean trySuccess(Void var1) {
      throw new IllegalStateException();
   }

   public DefaultChannelGroupFuture setFailure(Throwable var1) {
      throw new IllegalStateException();
   }

   public boolean tryFailure(Throwable var1) {
      throw new IllegalStateException();
   }

   protected void checkDeadLock() {
      EventExecutor var1 = this.executor();
      if (var1 != null && var1 != ImmediateEventExecutor.INSTANCE && var1.inEventLoop()) {
         throw new BlockingOperationException();
      }
   }

   private static final class DefaultEntry<K, V> implements Entry<K, V> {
      private final K key;
      private final V value;

      DefaultEntry(K var1, V var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public K getKey() {
         return this.key;
      }

      public V getValue() {
         return this.value;
      }

      public V setValue(V var1) {
         throw new UnsupportedOperationException("read-only");
      }
   }
}
