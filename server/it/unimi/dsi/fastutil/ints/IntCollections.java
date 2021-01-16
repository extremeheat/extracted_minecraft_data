package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.function.IntPredicate;

public final class IntCollections {
   private IntCollections() {
      super();
   }

   public static IntCollection synchronize(IntCollection var0) {
      return new IntCollections.SynchronizedCollection(var0);
   }

   public static IntCollection synchronize(IntCollection var0, Object var1) {
      return new IntCollections.SynchronizedCollection(var0, var1);
   }

   public static IntCollection unmodifiable(IntCollection var0) {
      return new IntCollections.UnmodifiableCollection(var0);
   }

   public static IntCollection asCollection(IntIterable var0) {
      return (IntCollection)(var0 instanceof IntCollection ? (IntCollection)var0 : new IntCollections.IterableCollection(var0));
   }

   public static class IterableCollection extends AbstractIntCollection implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final IntIterable iterable;

      protected IterableCollection(IntIterable var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.iterable = var1;
         }
      }

      public int size() {
         int var1 = 0;

         for(IntIterator var2 = this.iterator(); var2.hasNext(); ++var1) {
            var2.nextInt();
         }

         return var1;
      }

      public boolean isEmpty() {
         return !this.iterable.iterator().hasNext();
      }

      public IntIterator iterator() {
         return this.iterable.iterator();
      }
   }

   public static class UnmodifiableCollection implements IntCollection, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final IntCollection collection;

      protected UnmodifiableCollection(IntCollection var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.collection = var1;
         }
      }

      public boolean add(int var1) {
         throw new UnsupportedOperationException();
      }

      public boolean rem(int var1) {
         throw new UnsupportedOperationException();
      }

      public int size() {
         return this.collection.size();
      }

      public boolean isEmpty() {
         return this.collection.isEmpty();
      }

      public boolean contains(int var1) {
         return this.collection.contains(var1);
      }

      public IntIterator iterator() {
         return IntIterators.unmodifiable(this.collection.iterator());
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

      public boolean addAll(Collection<? extends Integer> var1) {
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
      public boolean add(Integer var1) {
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

      public int[] toIntArray() {
         return this.collection.toIntArray();
      }

      /** @deprecated */
      @Deprecated
      public int[] toIntArray(int[] var1) {
         return this.toArray(var1);
      }

      public int[] toArray(int[] var1) {
         return this.collection.toArray(var1);
      }

      public boolean containsAll(IntCollection var1) {
         return this.collection.containsAll(var1);
      }

      public boolean addAll(IntCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(IntCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(IntCollection var1) {
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

   public static class SynchronizedCollection implements IntCollection, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final IntCollection collection;
      protected final Object sync;

      protected SynchronizedCollection(IntCollection var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.collection = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedCollection(IntCollection var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.collection = var1;
            this.sync = this;
         }
      }

      public boolean add(int var1) {
         synchronized(this.sync) {
            return this.collection.add(var1);
         }
      }

      public boolean contains(int var1) {
         synchronized(this.sync) {
            return this.collection.contains(var1);
         }
      }

      public boolean rem(int var1) {
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

      public int[] toIntArray() {
         synchronized(this.sync) {
            return this.collection.toIntArray();
         }
      }

      public Object[] toArray() {
         synchronized(this.sync) {
            return this.collection.toArray();
         }
      }

      /** @deprecated */
      @Deprecated
      public int[] toIntArray(int[] var1) {
         return this.toArray(var1);
      }

      public int[] toArray(int[] var1) {
         synchronized(this.sync) {
            return this.collection.toArray(var1);
         }
      }

      public boolean addAll(IntCollection var1) {
         synchronized(this.sync) {
            return this.collection.addAll(var1);
         }
      }

      public boolean containsAll(IntCollection var1) {
         synchronized(this.sync) {
            return this.collection.containsAll(var1);
         }
      }

      public boolean removeAll(IntCollection var1) {
         synchronized(this.sync) {
            return this.collection.removeAll(var1);
         }
      }

      public boolean removeIf(IntPredicate var1) {
         synchronized(this.sync) {
            return this.collection.removeIf(var1);
         }
      }

      public boolean retainAll(IntCollection var1) {
         synchronized(this.sync) {
            return this.collection.retainAll(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean add(Integer var1) {
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

      public IntIterator iterator() {
         return this.collection.iterator();
      }

      public boolean addAll(Collection<? extends Integer> var1) {
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

   public abstract static class EmptyCollection extends AbstractIntCollection {
      protected EmptyCollection() {
         super();
      }

      public boolean contains(int var1) {
         return false;
      }

      public Object[] toArray() {
         return ObjectArrays.EMPTY_ARRAY;
      }

      public IntBidirectionalIterator iterator() {
         return IntIterators.EMPTY_ITERATOR;
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

      public boolean addAll(Collection<? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(IntCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(IntCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(IntCollection var1) {
         throw new UnsupportedOperationException();
      }
   }
}
