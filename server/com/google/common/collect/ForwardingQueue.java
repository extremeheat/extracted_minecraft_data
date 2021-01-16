package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.NoSuchElementException;
import java.util.Queue;

@GwtCompatible
public abstract class ForwardingQueue<E> extends ForwardingCollection<E> implements Queue<E> {
   protected ForwardingQueue() {
      super();
   }

   protected abstract Queue<E> delegate();

   @CanIgnoreReturnValue
   public boolean offer(E var1) {
      return this.delegate().offer(var1);
   }

   @CanIgnoreReturnValue
   public E poll() {
      return this.delegate().poll();
   }

   @CanIgnoreReturnValue
   public E remove() {
      return this.delegate().remove();
   }

   public E peek() {
      return this.delegate().peek();
   }

   public E element() {
      return this.delegate().element();
   }

   protected boolean standardOffer(E var1) {
      try {
         return this.add(var1);
      } catch (IllegalStateException var3) {
         return false;
      }
   }

   protected E standardPeek() {
      try {
         return this.element();
      } catch (NoSuchElementException var2) {
         return null;
      }
   }

   protected E standardPoll() {
      try {
         return this.remove();
      } catch (NoSuchElementException var2) {
         return null;
      }
   }
}
