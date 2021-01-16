package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ForwardingQueue;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@CanIgnoreReturnValue
@GwtIncompatible
public abstract class ForwardingBlockingQueue<E> extends ForwardingQueue<E> implements BlockingQueue<E> {
   protected ForwardingBlockingQueue() {
      super();
   }

   protected abstract BlockingQueue<E> delegate();

   public int drainTo(Collection<? super E> var1, int var2) {
      return this.delegate().drainTo(var1, var2);
   }

   public int drainTo(Collection<? super E> var1) {
      return this.delegate().drainTo(var1);
   }

   public boolean offer(E var1, long var2, TimeUnit var4) throws InterruptedException {
      return this.delegate().offer(var1, var2, var4);
   }

   public E poll(long var1, TimeUnit var3) throws InterruptedException {
      return this.delegate().poll(var1, var3);
   }

   public void put(E var1) throws InterruptedException {
      this.delegate().put(var1);
   }

   public int remainingCapacity() {
      return this.delegate().remainingCapacity();
   }

   public E take() throws InterruptedException {
      return this.delegate().take();
   }
}
