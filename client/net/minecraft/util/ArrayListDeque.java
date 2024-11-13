package net.minecraft.util;

import com.google.common.annotations.VisibleForTesting;
import java.util.AbstractList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.SequencedCollection;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;

public class ArrayListDeque<T> extends AbstractList<T> implements ListAndDeque<T> {
   private static final int MIN_GROWTH = 1;
   private Object[] contents;
   private int head;
   private int size;

   public ArrayListDeque() {
      this(1);
   }

   public ArrayListDeque(int var1) {
      super();
      this.contents = new Object[var1];
      this.head = 0;
      this.size = 0;
   }

   public int size() {
      return this.size;
   }

   @VisibleForTesting
   public int capacity() {
      return this.contents.length;
   }

   private int getIndex(int var1) {
      return (var1 + this.head) % this.contents.length;
   }

   public T get(int var1) {
      this.verifyIndexInRange(var1);
      return (T)this.getInner(this.getIndex(var1));
   }

   private static void verifyIndexInRange(int var0, int var1) {
      if (var0 < 0 || var0 >= var1) {
         throw new IndexOutOfBoundsException(var0);
      }
   }

   private void verifyIndexInRange(int var1) {
      verifyIndexInRange(var1, this.size);
   }

   private T getInner(int var1) {
      return (T)this.contents[var1];
   }

   public T set(int var1, T var2) {
      this.verifyIndexInRange(var1);
      Objects.requireNonNull(var2);
      int var3 = this.getIndex(var1);
      Object var4 = this.getInner(var3);
      this.contents[var3] = var2;
      return (T)var4;
   }

   public void add(int var1, T var2) {
      verifyIndexInRange(var1, this.size + 1);
      Objects.requireNonNull(var2);
      if (this.size == this.contents.length) {
         this.grow();
      }

      int var3 = this.getIndex(var1);
      if (var1 == this.size) {
         this.contents[var3] = var2;
      } else if (var1 == 0) {
         --this.head;
         if (this.head < 0) {
            this.head += this.contents.length;
         }

         this.contents[this.getIndex(0)] = var2;
      } else {
         for(int var4 = this.size - 1; var4 >= var1; --var4) {
            this.contents[this.getIndex(var4 + 1)] = this.contents[this.getIndex(var4)];
         }

         this.contents[var3] = var2;
      }

      ++this.modCount;
      ++this.size;
   }

   private void grow() {
      int var1 = this.contents.length + Math.max(this.contents.length >> 1, 1);
      Object[] var2 = new Object[var1];
      this.copyCount(var2, this.size);
      this.head = 0;
      this.contents = var2;
   }

   public T remove(int var1) {
      this.verifyIndexInRange(var1);
      int var2 = this.getIndex(var1);
      Object var3 = this.getInner(var2);
      if (var1 == 0) {
         this.contents[var2] = null;
         ++this.head;
      } else if (var1 == this.size - 1) {
         this.contents[var2] = null;
      } else {
         for(int var4 = var1 + 1; var4 < this.size; ++var4) {
            this.contents[this.getIndex(var4 - 1)] = this.get(var4);
         }

         this.contents[this.getIndex(this.size - 1)] = null;
      }

      ++this.modCount;
      --this.size;
      return (T)var3;
   }

   public boolean removeIf(Predicate<? super T> var1) {
      int var2 = 0;

      for(int var3 = 0; var3 < this.size; ++var3) {
         Object var4 = this.get(var3);
         if (var1.test(var4)) {
            ++var2;
         } else if (var2 != 0) {
            this.contents[this.getIndex(var3 - var2)] = var4;
            this.contents[this.getIndex(var3)] = null;
         }
      }

      this.modCount += var2;
      this.size -= var2;
      return var2 != 0;
   }

   private void copyCount(Object[] var1, int var2) {
      for(int var3 = 0; var3 < var2; ++var3) {
         var1[var3] = this.get(var3);
      }

   }

   public void replaceAll(UnaryOperator<T> var1) {
      for(int var2 = 0; var2 < this.size; ++var2) {
         int var3 = this.getIndex(var2);
         this.contents[var3] = Objects.requireNonNull(var1.apply(this.getInner(var2)));
      }

   }

   public void forEach(Consumer<? super T> var1) {
      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.accept(this.get(var2));
      }

   }

   public void addFirst(T var1) {
      this.add(0, var1);
   }

   public void addLast(T var1) {
      this.add(this.size, var1);
   }

   public boolean offerFirst(T var1) {
      this.addFirst(var1);
      return true;
   }

   public boolean offerLast(T var1) {
      this.addLast(var1);
      return true;
   }

   public T removeFirst() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return (T)this.remove(0);
      }
   }

   public T removeLast() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return (T)this.remove(this.size - 1);
      }
   }

   public ListAndDeque<T> reversed() {
      return new ReversedView(this);
   }

   @Nullable
   public T pollFirst() {
      return (T)(this.size == 0 ? null : this.removeFirst());
   }

   @Nullable
   public T pollLast() {
      return (T)(this.size == 0 ? null : this.removeLast());
   }

   public T getFirst() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return (T)this.get(0);
      }
   }

   public T getLast() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return (T)this.get(this.size - 1);
      }
   }

   @Nullable
   public T peekFirst() {
      return (T)(this.size == 0 ? null : this.getFirst());
   }

   @Nullable
   public T peekLast() {
      return (T)(this.size == 0 ? null : this.getLast());
   }

   public boolean removeFirstOccurrence(Object var1) {
      for(int var2 = 0; var2 < this.size; ++var2) {
         Object var3 = this.get(var2);
         if (Objects.equals(var1, var3)) {
            this.remove(var2);
            return true;
         }
      }

      return false;
   }

   public boolean removeLastOccurrence(Object var1) {
      for(int var2 = this.size - 1; var2 >= 0; --var2) {
         Object var3 = this.get(var2);
         if (Objects.equals(var1, var3)) {
            this.remove(var2);
            return true;
         }
      }

      return false;
   }

   public Iterator<T> descendingIterator() {
      return new DescendingIterator();
   }

   // $FF: synthetic method
   public List reversed() {
      return this.reversed();
   }

   // $FF: synthetic method
   public SequencedCollection reversed() {
      return this.reversed();
   }

   // $FF: synthetic method
   public Deque reversed() {
      return this.reversed();
   }

   class DescendingIterator implements Iterator<T> {
      private int index = ArrayListDeque.this.size() - 1;

      public DescendingIterator() {
         super();
      }

      public boolean hasNext() {
         return this.index >= 0;
      }

      public T next() {
         return (T)ArrayListDeque.this.get(this.index--);
      }

      public void remove() {
         ArrayListDeque.this.remove(this.index + 1);
      }
   }

   class ReversedView extends AbstractList<T> implements ListAndDeque<T> {
      private final ArrayListDeque<T> source;

      public ReversedView(final ArrayListDeque<T> var2) {
         super();
         this.source = var2;
      }

      public ListAndDeque<T> reversed() {
         return this.source;
      }

      public T getFirst() {
         return this.source.getLast();
      }

      public T getLast() {
         return this.source.getFirst();
      }

      public void addFirst(T var1) {
         this.source.addLast(var1);
      }

      public void addLast(T var1) {
         this.source.addFirst(var1);
      }

      public boolean offerFirst(T var1) {
         return this.source.offerLast(var1);
      }

      public boolean offerLast(T var1) {
         return this.source.offerFirst(var1);
      }

      public T pollFirst() {
         return this.source.pollLast();
      }

      public T pollLast() {
         return this.source.pollFirst();
      }

      public T peekFirst() {
         return this.source.peekLast();
      }

      public T peekLast() {
         return this.source.peekFirst();
      }

      public T removeFirst() {
         return this.source.removeLast();
      }

      public T removeLast() {
         return this.source.removeFirst();
      }

      public boolean removeFirstOccurrence(Object var1) {
         return this.source.removeLastOccurrence(var1);
      }

      public boolean removeLastOccurrence(Object var1) {
         return this.source.removeFirstOccurrence(var1);
      }

      public Iterator<T> descendingIterator() {
         return this.source.iterator();
      }

      public int size() {
         return this.source.size();
      }

      public boolean isEmpty() {
         return this.source.isEmpty();
      }

      public boolean contains(Object var1) {
         return this.source.contains(var1);
      }

      public T get(int var1) {
         return this.source.get(this.reverseIndex(var1));
      }

      public T set(int var1, T var2) {
         return this.source.set(this.reverseIndex(var1), var2);
      }

      public void add(int var1, T var2) {
         this.source.add(this.reverseIndex(var1) + 1, var2);
      }

      public T remove(int var1) {
         return this.source.remove(this.reverseIndex(var1));
      }

      public int indexOf(Object var1) {
         return this.reverseIndex(this.source.lastIndexOf(var1));
      }

      public int lastIndexOf(Object var1) {
         return this.reverseIndex(this.source.indexOf(var1));
      }

      public List<T> subList(int var1, int var2) {
         return this.source.subList(this.reverseIndex(var2) + 1, this.reverseIndex(var1) + 1).reversed();
      }

      public Iterator<T> iterator() {
         return this.source.descendingIterator();
      }

      public void clear() {
         this.source.clear();
      }

      private int reverseIndex(int var1) {
         return var1 == -1 ? -1 : this.source.size() - 1 - var1;
      }

      // $FF: synthetic method
      public List reversed() {
         return this.reversed();
      }

      // $FF: synthetic method
      public SequencedCollection reversed() {
         return this.reversed();
      }

      // $FF: synthetic method
      public Deque reversed() {
         return this.reversed();
      }
   }
}
