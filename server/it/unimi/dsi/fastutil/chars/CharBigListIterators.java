package it.unimi.dsi.fastutil.chars;

import java.io.Serializable;
import java.util.NoSuchElementException;

public final class CharBigListIterators {
   public static final CharBigListIterators.EmptyBigListIterator EMPTY_BIG_LIST_ITERATOR = new CharBigListIterators.EmptyBigListIterator();

   private CharBigListIterators() {
      super();
   }

   public static CharBigListIterator singleton(char var0) {
      return new CharBigListIterators.SingletonBigListIterator(var0);
   }

   public static CharBigListIterator unmodifiable(CharBigListIterator var0) {
      return new CharBigListIterators.UnmodifiableBigListIterator(var0);
   }

   public static CharBigListIterator asBigListIterator(CharListIterator var0) {
      return new CharBigListIterators.BigListIteratorListIterator(var0);
   }

   public static class BigListIteratorListIterator implements CharBigListIterator {
      protected final CharListIterator i;

      protected BigListIteratorListIterator(CharListIterator var1) {
         super();
         this.i = var1;
      }

      private int intDisplacement(long var1) {
         if (var1 >= -2147483648L && var1 <= 2147483647L) {
            return (int)var1;
         } else {
            throw new IndexOutOfBoundsException("This big iterator is restricted to 32-bit displacements");
         }
      }

      public void set(char var1) {
         this.i.set(var1);
      }

      public void add(char var1) {
         this.i.add(var1);
      }

      public int back(int var1) {
         return this.i.back(var1);
      }

      public long back(long var1) {
         return (long)this.i.back(this.intDisplacement(var1));
      }

      public void remove() {
         this.i.remove();
      }

      public int skip(int var1) {
         return this.i.skip(var1);
      }

      public long skip(long var1) {
         return (long)this.i.skip(this.intDisplacement(var1));
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

      public long nextIndex() {
         return (long)this.i.nextIndex();
      }

      public long previousIndex() {
         return (long)this.i.previousIndex();
      }
   }

   public static class UnmodifiableBigListIterator implements CharBigListIterator {
      protected final CharBigListIterator i;

      public UnmodifiableBigListIterator(CharBigListIterator var1) {
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

      public long nextIndex() {
         return this.i.nextIndex();
      }

      public long previousIndex() {
         return this.i.previousIndex();
      }
   }

   private static class SingletonBigListIterator implements CharBigListIterator {
      private final char element;
      private int curr;

      public SingletonBigListIterator(char var1) {
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

      public long nextIndex() {
         return (long)this.curr;
      }

      public long previousIndex() {
         return (long)(this.curr - 1);
      }
   }

   public static class EmptyBigListIterator implements CharBigListIterator, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyBigListIterator() {
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

      public long nextIndex() {
         return 0L;
      }

      public long previousIndex() {
         return -1L;
      }

      public long skip(long var1) {
         return 0L;
      }

      public long back(long var1) {
         return 0L;
      }

      public Object clone() {
         return CharBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      private Object readResolve() {
         return CharBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }
   }
}
