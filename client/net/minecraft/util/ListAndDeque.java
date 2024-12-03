package net.minecraft.util;

import java.io.Serializable;
import java.util.Deque;
import java.util.List;
import java.util.RandomAccess;
import java.util.SequencedCollection;
import javax.annotation.Nullable;

public interface ListAndDeque<T> extends Serializable, Cloneable, Deque<T>, List<T>, RandomAccess {
   ListAndDeque<T> reversed();

   T getFirst();

   T getLast();

   void addFirst(T var1);

   void addLast(T var1);

   T removeFirst();

   T removeLast();

   default boolean offer(T var1) {
      return this.offerLast(var1);
   }

   default T remove() {
      return (T)this.removeFirst();
   }

   @Nullable
   default T poll() {
      return (T)this.pollFirst();
   }

   default T element() {
      return (T)this.getFirst();
   }

   @Nullable
   default T peek() {
      return (T)this.peekFirst();
   }

   default void push(T var1) {
      this.addFirst(var1);
   }

   default T pop() {
      return (T)this.removeFirst();
   }

   // $FF: synthetic method
   default List reversed() {
      return this.reversed();
   }

   // $FF: synthetic method
   default SequencedCollection reversed() {
      return this.reversed();
   }

   // $FF: synthetic method
   default Deque reversed() {
      return this.reversed();
   }
}
