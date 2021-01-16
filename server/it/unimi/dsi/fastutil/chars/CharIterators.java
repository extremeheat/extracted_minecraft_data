package it.unimi.dsi.fastutil.chars;

import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.IntPredicate;

public final class CharIterators {
   public static final CharIterators.EmptyIterator EMPTY_ITERATOR = new CharIterators.EmptyIterator();

   private CharIterators() {
      super();
   }

   public static CharListIterator singleton(char var0) {
      return new CharIterators.SingletonIterator(var0);
   }

   public static CharListIterator wrap(char[] var0, int var1, int var2) {
      CharArrays.ensureOffsetLength(var0, var1, var2);
      return new CharIterators.ArrayIterator(var0, var1, var2);
   }

   public static CharListIterator wrap(char[] var0) {
      return new CharIterators.ArrayIterator(var0, 0, var0.length);
   }

   public static int unwrap(CharIterator var0, char[] var1, int var2, int var3) {
      if (var3 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var3 + ") is negative");
      } else if (var2 >= 0 && var2 + var3 <= var1.length) {
         int var4;
         for(var4 = var3; var4-- != 0 && var0.hasNext(); var1[var2++] = var0.nextChar()) {
         }

         return var3 - var4 - 1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static int unwrap(CharIterator var0, char[] var1) {
      return unwrap(var0, var1, 0, var1.length);
   }

   public static char[] unwrap(CharIterator var0, int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var1 + ") is negative");
      } else {
         char[] var2 = new char[16];

         int var3;
         for(var3 = 0; var1-- != 0 && var0.hasNext(); var2[var3++] = var0.nextChar()) {
            if (var3 == var2.length) {
               var2 = CharArrays.grow(var2, var3 + 1);
            }
         }

         return CharArrays.trim(var2, var3);
      }
   }

   public static char[] unwrap(CharIterator var0) {
      return unwrap(var0, 2147483647);
   }

   public static int unwrap(CharIterator var0, CharCollection var1, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var2 + ") is negative");
      } else {
         int var3 = var2;

         while(var3-- != 0 && var0.hasNext()) {
            var1.add(var0.nextChar());
         }

         return var2 - var3 - 1;
      }
   }

   public static long unwrap(CharIterator var0, CharCollection var1) {
      long var2;
      for(var2 = 0L; var0.hasNext(); ++var2) {
         var1.add(var0.nextChar());
      }

      return var2;
   }

   public static int pour(CharIterator var0, CharCollection var1, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var2 + ") is negative");
      } else {
         int var3 = var2;

         while(var3-- != 0 && var0.hasNext()) {
            var1.add(var0.nextChar());
         }

         return var2 - var3 - 1;
      }
   }

   public static int pour(CharIterator var0, CharCollection var1) {
      return pour(var0, var1, 2147483647);
   }

   public static CharList pour(CharIterator var0, int var1) {
      CharArrayList var2 = new CharArrayList();
      pour(var0, var2, var1);
      var2.trim();
      return var2;
   }

   public static CharList pour(CharIterator var0) {
      return pour(var0, 2147483647);
   }

   public static CharIterator asCharIterator(Iterator var0) {
      return (CharIterator)(var0 instanceof CharIterator ? (CharIterator)var0 : new CharIterators.IteratorWrapper(var0));
   }

   public static CharListIterator asCharIterator(ListIterator var0) {
      return (CharListIterator)(var0 instanceof CharListIterator ? (CharListIterator)var0 : new CharIterators.ListIteratorWrapper(var0));
   }

   public static boolean any(CharIterator var0, IntPredicate var1) {
      return indexOf(var0, var1) != -1;
   }

   public static boolean all(CharIterator var0, IntPredicate var1) {
      Objects.requireNonNull(var1);

      while(var0.hasNext()) {
         if (!var1.test(var0.nextChar())) {
            return false;
         }
      }

      return true;
   }

   public static int indexOf(CharIterator var0, IntPredicate var1) {
      Objects.requireNonNull(var1);

      for(int var2 = 0; var0.hasNext(); ++var2) {
         if (var1.test(var0.nextChar())) {
            return var2;
         }
      }

      return -1;
   }

   public static CharListIterator fromTo(char var0, char var1) {
      return new CharIterators.IntervalIterator(var0, var1);
   }

   public static CharIterator concat(CharIterator[] var0) {
      return concat(var0, 0, var0.length);
   }

   public static CharIterator concat(CharIterator[] var0, int var1, int var2) {
      return new CharIterators.IteratorConcatenator(var0, var1, var2);
   }

   public static CharIterator unmodifiable(CharIterator var0) {
      return new CharIterators.UnmodifiableIterator(var0);
   }

   public static CharBidirectionalIterator unmodifiable(CharBidirectionalIterator var0) {
      return new CharIterators.UnmodifiableBidirectionalIterator(var0);
   }

   public static CharListIterator unmodifiable(CharListIterator var0) {
      return new CharIterators.UnmodifiableListIterator(var0);
   }

   public static class UnmodifiableListIterator implements CharListIterator {
      protected final CharListIterator i;

      public UnmodifiableListIterator(CharListIterator var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }

      public char nextChar() {
         return this.i.nextChar();
      }

      public char previousChar() {
         return this.i.previousChar();
      }

      public int nextIndex() {
         return this.i.nextIndex();
      }

      public int previousIndex() {
         return this.i.previousIndex();
      }
   }

   public static class UnmodifiableBidirectionalIterator implements CharBidirectionalIterator {
      protected final CharBidirectionalIterator i;

      public UnmodifiableBidirectionalIterator(CharBidirectionalIterator var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }

      public char nextChar() {
         return this.i.nextChar();
      }

      public char previousChar() {
         return this.i.previousChar();
      }
   }

   public static class UnmodifiableIterator implements CharIterator {
      protected final CharIterator i;

      public UnmodifiableIterator(CharIterator var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public char nextChar() {
         return this.i.nextChar();
      }
   }

   private static class IteratorConcatenator implements CharIterator {
      final CharIterator[] a;
      int offset;
      int length;
      int lastOffset = -1;

      public IteratorConcatenator(CharIterator[] var1, int var2, int var3) {
         super();
         this.a = var1;
         this.offset = var2;
         this.length = var3;
         this.advance();
      }

      private void advance() {
         while(this.length != 0 && !this.a[this.offset].hasNext()) {
            --this.length;
            ++this.offset;
         }

      }

      public boolean hasNext() {
         return this.length > 0;
      }

      public char nextChar() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            char var1 = this.a[this.lastOffset = this.offset].nextChar();
            this.advance();
            return var1;
         }
      }

      public void remove() {
         if (this.lastOffset == -1) {
            throw new IllegalStateException();
         } else {
            this.a[this.lastOffset].remove();
         }
      }

      public int skip(int var1) {
         this.lastOffset = -1;

         int var2;
         for(var2 = 0; var2 < var1 && this.length != 0; ++this.offset) {
            var2 += this.a[this.offset].skip(var1 - var2);
            if (this.a[this.offset].hasNext()) {
               break;
            }

            --this.length;
         }

         return var2;
      }
   }

   private static class IntervalIterator implements CharListIterator {
      private final char from;
      private final char to;
      char curr;

      public IntervalIterator(char var1, char var2) {
         super();
         this.from = this.curr = var1;
         this.to = var2;
      }

      public boolean hasNext() {
         return this.curr < this.to;
      }

      public boolean hasPrevious() {
         return this.curr > this.from;
      }

      public char nextChar() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            char var10002 = this.curr;
            this.curr = (char)(var10002 + 1);
            return var10002;
         }
      }

      public char previousChar() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            return --this.curr;
         }
      }

      public int nextIndex() {
         return this.curr - this.from;
      }

      public int previousIndex() {
         return this.curr - this.from - 1;
      }

      public int skip(int var1) {
         if (this.curr + var1 <= this.to) {
            this.curr = (char)(this.curr + var1);
            return var1;
         } else {
            var1 = this.to - this.curr;
            this.curr = this.to;
            return var1;
         }
      }

      public int back(int var1) {
         if (this.curr - var1 >= this.from) {
            this.curr = (char)(this.curr - var1);
            return var1;
         } else {
            var1 = this.curr - this.from;
            this.curr = this.from;
            return var1;
         }
      }
   }

   private static class ListIteratorWrapper implements CharListIterator {
      final ListIterator<Character> i;

      public ListIteratorWrapper(ListIterator<Character> var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }

      public int nextIndex() {
         return this.i.nextIndex();
      }

      public int previousIndex() {
         return this.i.previousIndex();
      }

      public void set(char var1) {
         this.i.set(var1);
      }

      public void add(char var1) {
         this.i.add(var1);
      }

      public void remove() {
         this.i.remove();
      }

      public char nextChar() {
         return (Character)this.i.next();
      }

      public char previousChar() {
         return (Character)this.i.previous();
      }
   }

   private static class IteratorWrapper implements CharIterator {
      final Iterator<Character> i;

      public IteratorWrapper(Iterator<Character> var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public void remove() {
         this.i.remove();
      }

      public char nextChar() {
         return (Character)this.i.next();
      }
   }

   private static class ArrayIterator implements CharListIterator {
      private final char[] array;
      private final int offset;
      private final int length;
      private int curr;

      public ArrayIterator(char[] var1, int var2, int var3) {
         super();
         this.array = var1;
         this.offset = var2;
         this.length = var3;
      }

      public boolean hasNext() {
         return this.curr < this.length;
      }

      public boolean hasPrevious() {
         return this.curr > 0;
      }

      public char nextChar() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return this.array[this.offset + this.curr++];
         }
      }

      public char previousChar() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            return this.array[this.offset + --this.curr];
         }
      }

      public int skip(int var1) {
         if (var1 <= this.length - this.curr) {
            this.curr += var1;
            return var1;
         } else {
            var1 = this.length - this.curr;
            this.curr = this.length;
            return var1;
         }
      }

      public int back(int var1) {
         if (var1 <= this.curr) {
            this.curr -= var1;
            return var1;
         } else {
            var1 = this.curr;
            this.curr = 0;
            return var1;
         }
      }

      public int nextIndex() {
         return this.curr;
      }

      public int previousIndex() {
         return this.curr - 1;
      }
   }

   private static class SingletonIterator implements CharListIterator {
      private final char element;
      private int curr;

      public SingletonIterator(char var1) {
         super();
         this.element = var1;
      }

      public boolean hasNext() {
         return this.curr == 0;
      }

      public boolean hasPrevious() {
         return this.curr == 1;
      }

      public char nextChar() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = 1;
            return this.element;
         }
      }

      public char previousChar() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            this.curr = 0;
            return this.element;
         }
      }

      public int nextIndex() {
         return this.curr;
      }

      public int previousIndex() {
         return this.curr - 1;
      }
   }

   public static class EmptyIterator implements CharListIterator, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyIterator() {
         super();
      }

      public boolean hasNext() {
         return false;
      }

      public boolean hasPrevious() {
         return false;
      }

      public char nextChar() {
         throw new NoSuchElementException();
      }

      public char previousChar() {
         throw new NoSuchElementException();
      }

      public int nextIndex() {
         return 0;
      }

      public int previousIndex() {
         return -1;
      }

      public int skip(int var1) {
         return 0;
      }

      public int back(int var1) {
         return 0;
      }

      public Object clone() {
         return CharIterators.EMPTY_ITERATOR;
      }

      private Object readResolve() {
         return CharIterators.EMPTY_ITERATOR;
      }
   }
}
