package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.function.LongPredicate;

public final class LongCollections {
   private LongCollections() {
      super();
   }

   public static LongCollection synchronize(LongCollection var0) {
      return new LongCollections.SynchronizedCollection(var0);
   }

   public static LongCollection synchronize(LongCollection var0, Object var1) {
      return new LongCollections.SynchronizedCollection(var0, var1);
   }

   public static LongCollection unmodifiable(LongCollection var0) {
      return new LongCollections.UnmodifiableCollection(var0);
   }

   public static LongCollection asCollection(LongIterable var0) {
      return (LongCollection)(var0 instanceof LongCollection ? (LongCollection)var0 : new LongCollections.IterableCollection(var0));
   }

   public static class IterableCollection extends AbstractLongCollection implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongIterable iterable;

      protected IterableCollection(LongIterable var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.iterable = var1;
         }
      }

      public int size() {
         int var1 = 0;

         for(LongIterator var2 = this.iterator(); var2.hasNext(); ++var1) {
            var2.nextLong();
         }

         return var1;
      }

      public boolean isEmpty() {
         return !this.iterable.iterator().hasNext();
      }

      public LongIterator iterator() {
         return this.iterable.iterator();
      }
   }

   public static class UnmodifiableCollection implements LongCollection, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongCollection collection;

      protected UnmodifiableCollection(LongCollection var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.collection = var1;
         }
      }

      public boolean add(long var1) {
         throw new UnsupportedOperationException();
      }

      public boolean rem(long var1) {
         throw new UnsupportedOperationException();
      }

      public int size() {
         return this.collection.size();
      }

      public boolean isEmpty() {
         return this.collection.isEmpty();
      }

      public boolean contains(long var1) {
         return this.collection.contains(var1);
      }

      public LongIterator iterator() {
         return LongIterators.unmodifiable(this.collection.iterator());
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      public <T> T[] toArray(T[] var1) {
         return this.collection.toArray(var1);
      }

      public Object[] toArray() {
         return this.collection.toArray();
      }

      public boolean containsAll(Collection<?> var1) {
         return this.collection.containsAll(var1);
      }

      public boolean addAll(Collection<? extends Long> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean add(Long var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean contains(Object var1) {
         return this.collection.contains(var1);
      }

      /** @deprecated */
      @Deprecated
      public boolean remove(Object var1) {
         throw new UnsupportedOperationException();
      }

      public long[] toLongArray() {
         return this.collection.toLongArray();
      }

      /** @deprecated */
      @Deprecated
      public long[] toLongArray(long[] var1) {
         return this.toArray(var1);
      }

      public long[] toArray(long[] var1) {
         return this.collection.toArray(var1);
      }

      public boolean containsAll(LongCollection var1) {
         return this.collection.containsAll(var1);
      }

      public boolean addAll(LongCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(LongCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(LongCollection var1) {
         throw new UnsupportedOperationException();
      }

      public String toString() {
         return this.collection.toString();
      }

      public int hashCode() {
         return this.collection.hashCode();
      }

      public boolean equals(Object var1) {
         return var1 == this ? true : this.collection.equals(var1);
      }
   }

   public static class SynchronizedCollection implements LongCollection, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongCollection collection;
      protected final Object sync;

      protected SynchronizedCollection(LongCollection var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.collection = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedCollection(LongCollection var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.collection = var1;
            this.sync = this;
         }
      }

      public boolean add(long var1) {
         synchronized(this.sync) {
            return this.collection.add(var1);
         }
      }

      public boolean contains(long var1) {
         synchronized(this.sync) {
            return this.collection.contains(var1);
         }
      }

      public boolean rem(long var1) {
         synchronized(this.sync) {
            return this.collection.rem(var1);
         }
      }

      public int size() {
         synchronized(this.sync) {
            return this.collection.size();
         }
      }

      public boolean isEmpty() {
         synchronized(this.sync) {
            return this.collection.isEmpty();
         }
      }

      public long[] toLongArray() {
         synchronized(this.sync) {
            return this.collection.toLongArray();
         }
      }

      public Object[] toArray() {
         synchronized(this.sync) {
            return this.collection.toArray();
         }
      }

      /** @deprecated */
      @Deprecated
      public long[] toLongArray(long[] var1) {
         return this.toArray(var1);
      }

      public long[] toArray(long[] var1) {
         synchronized(this.sync) {
            return this.collection.toArray(var1);
         }
      }

      public boolean addAll(LongCollection var1) {
         synchronized(this.sync) {
            return this.collection.addAll(var1);
         }
      }

      public boolean containsAll(LongCollection var1) {
         synchronized(this.sync) {
            return this.collection.containsAll(var1);
         }
      }

      public boolean removeAll(LongCollection var1) {
         synchronized(this.sync) {
            return this.collection.removeAll(var1);
         }
      }

      public boolean removeIf(LongPredicate var1) {
         synchronized(this.sync) {
            return this.collection.removeIf(var1);
         }
      }

      public boolean retainAll(LongCollection var1) {
         synchronized(this.sync) {
            return this.collection.retainAll(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean add(Long var1) {
         synchronized(this.sync) {
            return this.collection.add(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean contains(Object var1) {
         synchronized(this.sync) {
            return this.collection.contains(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean remove(Object var1) {
         synchronized(this.sync) {
            return this.collection.remove(var1);
         }
      }

      public <T> T[] toArray(T[] var1) {
         synchronized(this.sync) {
            return this.collection.toArray(var1);
         }
      }

      public LongIterator iterator() {
         return this.collection.iterator();
      }

      public boolean addAll(Collection<? extends Long> var1) {
         synchronized(this.sync) {
            return this.collection.addAll(var1);
         }
      }

      public boolean containsAll(Collection<?> var1) {
         synchronized(this.sync) {
            return this.collection.containsAll(var1);
         }
      }

      public boolean removeAll(Collection<?> var1) {
         synchronized(this.sync) {
            return this.collection.removeAll(var1);
         }
      }

      public boolean retainAll(Collection<?> var1) {
         synchronized(this.sync) {
            return this.collection.retainAll(var1);
         }
      }

      public void clear() {
         synchronized(this.sync) {
            this.collection.clear();
         }
      }

      public String toString() {
         synchronized(this.sync) {
            return this.collection.toString();
         }
      }

      public int hashCode() {
         synchronized(this.sync) {
            return this.collection.hashCode();
         }
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            synchronized(this.sync) {
               return this.collection.equals(var1);
            }
         }
      }

      private void writeObject(ObjectOutputStream var1) throws IOException {
         synchronized(this.sync) {
            var1.defaultWriteObject();
         }
      }
   }

   public abstract static class EmptyCollection extends AbstractLongCollection {
      protected EmptyCollection() {
         super();
      }

      public boolean contains(long var1) {
         return false;
      }

      public Object[] toArray() {
         return ObjectArrays.EMPTY_ARRAY;
      }

      public LongBidirectionalIterator iterator() {
         return LongIterators.EMPTY_ITERATOR;
      }

      public int size() {
         return 0;
      }

      public void clear() {
      }

      public int hashCode() {
         return 0;
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            return !(var1 instanceof Collection) ? false : ((Collection)var1).isEmpty();
         }
      }

      public boolean addAll(Collection<? extends Long> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(LongCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(LongCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(LongCollection var1) {
         throw new UnsupportedOperationException();
      }
   }
}
