package io.netty.util.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class EmptyPriorityQueue<T> implements PriorityQueue<T> {
   private static final PriorityQueue<Object> INSTANCE = new EmptyPriorityQueue();

   private EmptyPriorityQueue() {
      super();
   }

   public static <V> EmptyPriorityQueue<V> instance() {
      return (EmptyPriorityQueue)INSTANCE;
   }

   public boolean removeTyped(T var1) {
      return false;
   }

   public boolean containsTyped(T var1) {
      return false;
   }

   public void priorityChanged(T var1) {
   }

   public int size() {
      return 0;
   }

   public boolean isEmpty() {
      return true;
   }

   public boolean contains(Object var1) {
      return false;
   }

   public Iterator<T> iterator() {
      return Collections.emptyList().iterator();
   }

   public Object[] toArray() {
      return EmptyArrays.EMPTY_OBJECTS;
   }

   public <T1> T1[] toArray(T1[] var1) {
      if (var1.length > 0) {
         var1[0] = null;
      }

      return var1;
   }

   public boolean add(T var1) {
      return false;
   }

   public boolean remove(Object var1) {
      return false;
   }

   public boolean containsAll(Collection<?> var1) {
      return false;
   }

   public boolean addAll(Collection<? extends T> var1) {
      return false;
   }

   public boolean removeAll(Collection<?> var1) {
      return false;
   }

   public boolean retainAll(Collection<?> var1) {
      return false;
   }

   public void clear() {
   }

   public void clearIgnoringIndexes() {
   }

   public boolean equals(Object var1) {
      return var1 instanceof PriorityQueue && ((PriorityQueue)var1).isEmpty();
   }

   public int hashCode() {
      return 0;
   }

   public boolean offer(T var1) {
      return false;
   }

   public T remove() {
      throw new NoSuchElementException();
   }

   public T poll() {
      return null;
   }

   public T element() {
      throw new NoSuchElementException();
   }

   public T peek() {
      return null;
   }

   public String toString() {
      return EmptyPriorityQueue.class.getSimpleName();
   }
}
