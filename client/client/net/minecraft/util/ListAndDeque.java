package net.minecraft.util;

import java.io.Serializable;
import java.util.Deque;
import java.util.List;
import java.util.RandomAccess;
import javax.annotation.Nullable;

public interface ListAndDeque<T> extends Serializable, Cloneable, Deque<T>, List<T>, RandomAccess {
   ListAndDeque<T> reversed();

   @Override
   T getFirst();

   @Override
   T getLast();

   @Override
   void addFirst(T var1);

   @Override
   void addLast(T var1);

   @Override
   T removeFirst();

   @Override
   T removeLast();

   @Override
   default boolean offer(T var1) {
      return this.offerLast((T)var1);
   }

   @Override
   default T remove() {
      return this.removeFirst();
   }

   @Nullable
   @Override
   default T poll() {
      return this.pollFirst();
   }

   @Override
   default T element() {
      return this.getFirst();
   }

   @Nullable
   @Override
   default T peek() {
      return this.peekFirst();
   }

   @Override
   default void push(T var1) {
      this.addFirst((T)var1);
   }

   @Override
   default T pop() {
      return this.removeFirst();
   }
}
