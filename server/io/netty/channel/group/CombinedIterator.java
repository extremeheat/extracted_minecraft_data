package io.netty.channel.group;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class CombinedIterator<E> implements Iterator<E> {
   private final Iterator<E> i1;
   private final Iterator<E> i2;
   private Iterator<E> currentIterator;

   CombinedIterator(Iterator<E> var1, Iterator<E> var2) {
      super();
      if (var1 == null) {
         throw new NullPointerException("i1");
      } else if (var2 == null) {
         throw new NullPointerException("i2");
      } else {
         this.i1 = var1;
         this.i2 = var2;
         this.currentIterator = var1;
      }
   }

   public boolean hasNext() {
      while(!this.currentIterator.hasNext()) {
         if (this.currentIterator != this.i1) {
            return false;
         }

         this.currentIterator = this.i2;
      }

      return true;
   }

   public E next() {
      while(true) {
         try {
            return this.currentIterator.next();
         } catch (NoSuchElementException var2) {
            if (this.currentIterator != this.i1) {
               throw var2;
            }

            this.currentIterator = this.i2;
         }
      }
   }

   public void remove() {
      this.currentIterator.remove();
   }
}
