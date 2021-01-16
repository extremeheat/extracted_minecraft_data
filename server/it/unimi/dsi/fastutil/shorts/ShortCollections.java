package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.function.IntPredicate;

public final class ShortCollections {
   private ShortCollections() {
      super();
   }

   public static ShortCollection synchronize(ShortCollection var0) {
      return new ShortCollections.SynchronizedCollection(var0);
   }

   public static ShortCollection synchronize(ShortCollection var0, Object var1) {
      return new ShortCollections.SynchronizedCollection(var0, var1);
   }

   public static ShortCollection unmodifiable(ShortCollection var0) {
      return new ShortCollections.UnmodifiableCollection(var0);
   }

   public static ShortCollection asCollection(ShortIterable var0) {
      return (ShortCollection)(var0 instanceof ShortCollection ? (ShortCollection)var0 : new ShortCollections.IterableCollection(var0));
   }

   public static class IterableCollection extends AbstractShortCollection implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortIterable iterable;

      protected IterableCollection(ShortIterable var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.iterable = var1;
         }
      }

      public int size() {
         int var1 = 0;

         for(ShortIterator var2 = this.iterator(); var2.hasNext(); ++var1) {
            var2.nextShort();
         }

         return var1;
      }

      public boolean isEmpty() {
         return !this.iterable.iterator().hasNext();
      }

      public ShortIterator iterator() {
         return this.iterable.iterator();
      }
   }

   public static class UnmodifiableCollection implements ShortCollection, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortCollection collection;

      protected UnmodifiableCollection(ShortCollection var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.collection = var1;
         }
      }

      public boolean add(short var1) {
         throw new UnsupportedOperationException();
      }

      public boolean rem(short var1) {
         throw new UnsupportedOperationException();
      }

      public int size() {
         return this.collection.size();
      }

      public boolean isEmpty() {
         return this.collection.isEmpty();
      }

      public boolean contains(short var1) {
         return this.collection.contains(var1);
      }

      public ShortIterator iterator() {
         return ShortIterators.unmodifiable(this.collection.iterator());
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

      public boolean addAll(Collection<? extends Short> var1) {
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
      public boolean add(Short var1) {
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

      public short[] toShortArray() {
         return this.collection.toShortArray();
      }

      /** @deprecated */
      @Deprecated
      public short[] toShortArray(short[] var1) {
         return this.toArray(var1);
      }

      public short[] toArray(short[] var1) {
         return this.collection.toArray(var1);
      }

      public boolean containsAll(ShortCollection var1) {
         return this.collection.containsAll(var1);
      }

      public boolean addAll(ShortCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(ShortCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(ShortCollection var1) {
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

   public static class SynchronizedCollection implements ShortCollection, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortCollection collection;
      protected final Object sync;

      protected SynchronizedCollection(ShortCollection var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.collection = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedCollection(ShortCollection var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.collection = var1;
            this.sync = this;
         }
      }

      public boolean add(short var1) {
         synchronized(this.sync) {
            return this.collection.add(var1);
         }
      }

      public boolean contains(short var1) {
         synchronized(this.sync) {
            return this.collection.contains(var1);
         }
      }

      public boolean rem(short var1) {
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

      public short[] toShortArray() {
         synchronized(this.sync) {
            return this.collection.toShortArray();
         }
      }

      public Object[] toArray() {
         synchronized(this.sync) {
            return this.collection.toArray();
         }
      }

      /** @deprecated */
      @Deprecated
      public short[] toShortArray(short[] var1) {
         return this.toArray(var1);
      }

      public short[] toArray(short[] var1) {
         synchronized(this.sync) {
            return this.collection.toArray(var1);
         }
      }

      public boolean addAll(ShortCollection var1) {
         synchronized(this.sync) {
            return this.collection.addAll(var1);
         }
      }

      public boolean containsAll(ShortCollection var1) {
         synchronized(this.sync) {
            return this.collection.containsAll(var1);
         }
      }

      public boolean removeAll(ShortCollection var1) {
         synchronized(this.sync) {
            return this.collection.removeAll(var1);
         }
      }

      public boolean removeIf(IntPredicate var1) {
         synchronized(this.sync) {
            return this.collection.removeIf(var1);
         }
      }

      public boolean retainAll(ShortCollection var1) {
         synchronized(this.sync) {
            return this.collection.retainAll(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean add(Short var1) {
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

      public ShortIterator iterator() {
         return this.collection.iterator();
      }

      public boolean addAll(Collection<? extends Short> var1) {
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

   public abstract static class EmptyCollection extends AbstractShortCollection {
      protected EmptyCollection() {
         super();
      }

      public boolean contains(short var1) {
         return false;
      }

      public Object[] toArray() {
         return ObjectArrays.EMPTY_ARRAY;
      }

      public ShortBidirectionalIterator iterator() {
         return ShortIterators.EMPTY_ITERATOR;
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

      public boolean addAll(Collection<? extends Short> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(ShortCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(ShortCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(ShortCollection var1) {
         throw new UnsupportedOperationException();
      }
   }
}
