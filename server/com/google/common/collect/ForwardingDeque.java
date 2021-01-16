package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Deque;
import java.util.Iterator;

@GwtIncompatible
public abstract class ForwardingDeque<E> extends ForwardingQueue<E> implements Deque<E> {
   protected ForwardingDeque() {
      super();
   }

   protected abstract Deque<E> delegate();

   public void addFirst(E var1) {
      this.delegate().addFirst(var1);
   }

   public void addLast(E var1) {
      this.delegate().addLast(var1);
   }

   public Iterator<E> descendingIterator() {
      return this.delegate().descendingIterator();
   }

   public E getFirst() {
      return this.delegate().getFirst();
   }

   public E getLast() {
      return this.delegate().getLast();
   }

   @CanIgnoreReturnValue
   public boolean offerFirst(E var1) {
      return this.delegate().offerFirst(var1);
   }

   @CanIgnoreReturnValue
   public boolean offerLast(E var1) {
      return this.delegate().offerLast(var1);
   }

   public E peekFirst() {
      return this.delegate().peekFirst();
   }

   public E peekLast() {
      return this.delegate().peekLast();
   }

   @CanIgnoreReturnValue
   public E pollFirst() {
      return this.delegate().pollFirst();
   }

   @CanIgnoreReturnValue
   public E pollLast() {
      return this.delegate().pollLast();
   }

   @CanIgnoreReturnValue
   public E pop() {
      return this.delegate().pop();
   }

   public void push(E var1) {
      this.delegate().push(var1);
   }

   @CanIgnoreReturnValue
   public E removeFirst() {
      return this.delegate().removeFirst();
   }

   @CanIgnoreReturnValue
   public E removeLast() {
      return this.delegate().removeLast();
   }

   @CanIgnoreReturnValue
   public boolean removeFirstOccurrence(Object var1) {
      return this.delegate().removeFirstOccurrence(var1);
   }

   @CanIgnoreReturnValue
   public boolean removeLastOccurrence(Object var1) {
      return this.delegate().removeLastOccurrence(var1);
   }
}
