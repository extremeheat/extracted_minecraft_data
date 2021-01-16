package it.unimi.dsi.fastutil.bytes;

import java.io.Serializable;
import java.util.NoSuchElementException;

public final class ByteSortedSets {
   public static final ByteSortedSets.EmptySet EMPTY_SET = new ByteSortedSets.EmptySet();

   private ByteSortedSets() {
      super();
   }

   public static ByteSortedSet singleton(byte var0) {
      return new ByteSortedSets.Singleton(var0);
   }

   public static ByteSortedSet singleton(byte var0, ByteComparator var1) {
      return new ByteSortedSets.Singleton(var0, var1);
   }

   public static ByteSortedSet singleton(Object var0) {
      return new ByteSortedSets.Singleton((Byte)var0);
   }

   public static ByteSortedSet singleton(Object var0, ByteComparator var1) {
      return new ByteSortedSets.Singleton((Byte)var0, var1);
   }

   public static ByteSortedSet synchronize(ByteSortedSet var0) {
      return new ByteSortedSets.SynchronizedSortedSet(var0);
   }

   public static ByteSortedSet synchronize(ByteSortedSet var0, Object var1) {
      return new ByteSortedSets.SynchronizedSortedSet(var0, var1);
   }

   public static ByteSortedSet unmodifiable(ByteSortedSet var0) {
      return new ByteSortedSets.UnmodifiableSortedSet(var0);
   }

   public static class UnmodifiableSortedSet extends ByteSets.UnmodifiableSet implements ByteSortedSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ByteSortedSet sortedSet;

      protected UnmodifiableSortedSet(ByteSortedSet var1) {
         super(var1);
         this.sortedSet = var1;
      }

      public ByteComparator comparator() {
         return this.sortedSet.comparator();
      }

      public ByteSortedSet subSet(byte var1, byte var2) {
         return new ByteSortedSets.UnmodifiableSortedSet(this.sortedSet.subSet(var1, var2));
      }

      public ByteSortedSet headSet(byte var1) {
         return new ByteSortedSets.UnmodifiableSortedSet(this.sortedSet.headSet(var1));
      }

      public ByteSortedSet tailSet(byte var1) {
         return new ByteSortedSets.UnmodifiableSortedSet(this.sortedSet.tailSet(var1));
      }

      public ByteBidirectionalIterator iterator() {
         return ByteIterators.unmodifiable(this.sortedSet.iterator());
      }

      public ByteBidirectionalIterator iterator(byte var1) {
         return ByteIterators.unmodifiable(this.sortedSet.iterator(var1));
      }

      public byte firstByte() {
         return this.sortedSet.firstByte();
      }

      public byte lastByte() {
         return this.sortedSet.lastByte();
      }

      /** @deprecated */
      @Deprecated
      public Byte first() {
         return this.sortedSet.first();
      }

      /** @deprecated */
      @Deprecated
      public Byte last() {
         return this.sortedSet.last();
      }

      /** @deprecated */
      @Deprecated
      public ByteSortedSet subSet(Byte var1, Byte var2) {
         return new ByteSortedSets.UnmodifiableSortedSet(this.sortedSet.subSet(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public ByteSortedSet headSet(Byte var1) {
         return new ByteSortedSets.UnmodifiableSortedSet(this.sortedSet.headSet(var1));
      }

      /** @deprecated */
      @Deprecated
      public ByteSortedSet tailSet(Byte var1) {
         return new ByteSortedSets.UnmodifiableSortedSet(this.sortedSet.tailSet(var1));
      }
   }

   public static class SynchronizedSortedSet extends ByteSets.SynchronizedSet implements ByteSortedSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ByteSortedSet sortedSet;

      protected SynchronizedSortedSet(ByteSortedSet var1, Object var2) {
         super(var1, var2);
         this.sortedSet = var1;
      }

      protected SynchronizedSortedSet(ByteSortedSet var1) {
         super(var1);
         this.sortedSet = var1;
      }

      public ByteComparator comparator() {
         synchronized(this.sync) {
            return this.sortedSet.comparator();
         }
      }

      public ByteSortedSet subSet(byte var1, byte var2) {
         return new ByteSortedSets.SynchronizedSortedSet(this.sortedSet.subSet(var1, var2), this.sync);
      }

      public ByteSortedSet headSet(byte var1) {
         return new ByteSortedSets.SynchronizedSortedSet(this.sortedSet.headSet(var1), this.sync);
      }

      public ByteSortedSet tailSet(byte var1) {
         return new ByteSortedSets.SynchronizedSortedSet(this.sortedSet.tailSet(var1), this.sync);
      }

      public ByteBidirectionalIterator iterator() {
         return this.sortedSet.iterator();
      }

      public ByteBidirectionalIterator iterator(byte var1) {
         return this.sortedSet.iterator(var1);
      }

      public byte firstByte() {
         synchronized(this.sync) {
            return this.sortedSet.firstByte();
         }
      }

      public byte lastByte() {
         synchronized(this.sync) {
            return this.sortedSet.lastByte();
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte first() {
         synchronized(this.sync) {
            return this.sortedSet.first();
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte last() {
         synchronized(this.sync) {
            return this.sortedSet.last();
         }
      }

      /** @deprecated */
      @Deprecated
      public ByteSortedSet subSet(Byte var1, Byte var2) {
         return new ByteSortedSets.SynchronizedSortedSet(this.sortedSet.subSet(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public ByteSortedSet headSet(Byte var1) {
         return new ByteSortedSets.SynchronizedSortedSet(this.sortedSet.headSet(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public ByteSortedSet tailSet(Byte var1) {
         return new ByteSortedSets.SynchronizedSortedSet(this.sortedSet.tailSet(var1), this.sync);
      }
   }

   public static class Singleton extends ByteSets.Singleton implements ByteSortedSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      final ByteComparator comparator;

      protected Singleton(byte var1, ByteComparator var2) {
         super(var1);
         this.comparator = var2;
      }

      private Singleton(byte var1) {
         this(var1, (ByteComparator)null);
      }

      final int compare(byte var1, byte var2) {
         return this.comparator == null ? Byte.compare(var1, var2) : this.comparator.compare(var1, var2);
      }

      public ByteBidirectionalIterator iterator(byte var1) {
         ByteListIterator var2 = this.iterator();
         if (this.compare(this.element, var1) <= 0) {
            var2.nextByte();
         }

         return var2;
      }

      public ByteComparator comparator() {
         return this.comparator;
      }

      public ByteSortedSet subSet(byte var1, byte var2) {
         return (ByteSortedSet)(this.compare(var1, this.element) <= 0 && this.compare(this.element, var2) < 0 ? this : ByteSortedSets.EMPTY_SET);
      }

      public ByteSortedSet headSet(byte var1) {
         return (ByteSortedSet)(this.compare(this.element, var1) < 0 ? this : ByteSortedSets.EMPTY_SET);
      }

      public ByteSortedSet tailSet(byte var1) {
         return (ByteSortedSet)(this.compare(var1, this.element) <= 0 ? this : ByteSortedSets.EMPTY_SET);
      }

      public byte firstByte() {
         return this.element;
      }

      public byte lastByte() {
         return this.element;
      }

      /** @deprecated */
      @Deprecated
      public ByteSortedSet subSet(Byte var1, Byte var2) {
         return this.subSet(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public ByteSortedSet headSet(Byte var1) {
         return this.headSet(var1);
      }

      /** @deprecated */
      @Deprecated
      public ByteSortedSet tailSet(Byte var1) {
         return this.tailSet(var1);
      }

      /** @deprecated */
      @Deprecated
      public Byte first() {
         return this.element;
      }

      /** @deprecated */
      @Deprecated
      public Byte last() {
         return this.element;
      }

      // $FF: synthetic method
      Singleton(byte var1, Object var2) {
         this(var1);
      }
   }

   public static class EmptySet extends ByteSets.EmptySet implements ByteSortedSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
         super();
      }

      public ByteBidirectionalIterator iterator(byte var1) {
         return ByteIterators.EMPTY_ITERATOR;
      }

      public ByteSortedSet subSet(byte var1, byte var2) {
         return ByteSortedSets.EMPTY_SET;
      }

      public ByteSortedSet headSet(byte var1) {
         return ByteSortedSets.EMPTY_SET;
      }

      public ByteSortedSet tailSet(byte var1) {
         return ByteSortedSets.EMPTY_SET;
      }

      public byte firstByte() {
         throw new NoSuchElementException();
      }

      public byte lastByte() {
         throw new NoSuchElementException();
      }

      public ByteComparator comparator() {
         return null;
      }

      /** @deprecated */
      @Deprecated
      public ByteSortedSet subSet(Byte var1, Byte var2) {
         return ByteSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ByteSortedSet headSet(Byte var1) {
         return ByteSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ByteSortedSet tailSet(Byte var1) {
         return ByteSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public Byte first() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Byte last() {
         throw new NoSuchElementException();
      }

      public Object clone() {
         return ByteSortedSets.EMPTY_SET;
      }

      private Object readResolve() {
         return ByteSortedSets.EMPTY_SET;
      }
   }
}
