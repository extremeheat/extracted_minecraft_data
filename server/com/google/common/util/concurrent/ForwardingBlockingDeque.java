package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ForwardingDeque;
import java.util.Collection;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;

@GwtIncompatible
public abstract class ForwardingBlockingDeque<E> extends ForwardingDeque<E> implements BlockingDeque<E> {
   protected ForwardingBlockingDeque() {
      super();
   }

   protected abstract BlockingDeque<E> delegate();

   public int remainingCapacity() {
      return this.delegate().remainingCapacity();
   }

   public void putFirst(E var1) throws InterruptedException {
      this.delegate().putFirst(var1);
   }

   public void putLast(E var1) throws InterruptedException {
      this.delegate().putLast(var1);
   }

   public boolean offerFirst(E var1, long var2, TimeUnit var4) throws InterruptedException {
      return this.delegate().offerFirst(var1, var2, var4);
   }

   public boolean offerLast(E var1, long var2, TimeUnit var4) throws InterruptedException {
      return this.delegate().offerLast(var1, var2, var4);
   }

   public E takeFirst() throws InterruptedException {
      return this.delegate().takeFirst();
   }

   public E takeLast() throws InterruptedException {
      return this.delegate().takeLast();
   }

   public E pollFirst(long var1, TimeUnit var3) throws InterruptedException {
      return this.delegate().pollFirst(var1, var3);
   }

   public E pollLast(long var1, TimeUnit var3) throws InterruptedException {
      return this.delegate().pollLast(var1, var3);
   }

   public void put(E var1) throws InterruptedException {
      this.delegate().put(var1);
   }

   public boolean offer(E var1, long var2, TimeUnit var4) throws InterruptedException {
      return this.delegate().offer(var1, var2, var4);
   }

   public E take() throws InterruptedException {
      return this.delegate().take();
   }

   public E poll(long var1, TimeUnit var3) throws InterruptedException {
      return this.delegate().poll(var1, var3);
   }

   public int drainTo(Collection<? super E> var1) {
      return this.delegate().drainTo(var1);
   }

   public int drainTo(Collection<? super E> var1, int var2) {
      return this.delegate().drainTo(var1, var2);
   }
}
