package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.function.DoublePredicate;

public final class DoubleCollections {
   private DoubleCollections() {
      super();
   }

   public static DoubleCollection synchronize(DoubleCollection var0) {
      return new DoubleCollections.SynchronizedCollection(var0);
   }

   public static DoubleCollection synchronize(DoubleCollection var0, Object var1) {
      return new DoubleCollections.SynchronizedCollection(var0, var1);
   }

   public static DoubleCollection unmodifiable(DoubleCollection var0) {
      return new DoubleCollections.UnmodifiableCollection(var0);
   }

   public static DoubleCollection asCollection(DoubleIterable var0) {
      return (DoubleCollection)(var0 instanceof DoubleCollection ? (DoubleCollection)var0 : new DoubleCollections.IterableCollection(var0));
   }

   public static class IterableCollection extends AbstractDoubleCollection implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final DoubleIterable iterable;

      protected IterableCollection(DoubleIterable var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.iterable = var1;
         }
      }

      public int size() {
         int var1 = 0;

         for(DoubleIterator var2 = this.iterator(); var2.hasNext(); ++var1) {
            var2.nextDouble();
         }

         return var1;
      }

      public boolean isEmpty() {
         return !this.iterable.iterator().hasNext();
      }

      public DoubleIterator iterator() {
         return this.iterable.iterator();
      }
   }

   public static class UnmodifiableCollection implements DoubleCollection, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final DoubleCollection collection;

      protected UnmodifiableCollection(DoubleCollection var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.collection = var1;
         }
      }

      public boolean add(double var1) {
         throw new UnsupportedOperationException();
      }

      public boolean rem(double var1) {
         throw new UnsupportedOperationException();
      }

      public int size() {
         return this.collection.size();
      }

      public boolean isEmpty() {
         return this.collection.isEmpty();
      }

      public boolean contains(double var1) {
         return this.collection.contains(var1);
      }

      public DoubleIterator iterator() {
         return DoubleIterators.unmodifiable(this.collection.iterator());
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

      public boolean addAll(Collection<? extends Double> var1) {
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
      public boolean add(Double var1) {
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

      public double[] toDoubleArray() {
         return this.collection.toDoubleArray();
      }

      /** @deprecated */
      @Deprecated
      public double[] toDoubleArray(double[] var1) {
         return this.toArray(var1);
      }

      public double[] toArray(double[] var1) {
         return this.collection.toArray(var1);
      }

      public boolean containsAll(DoubleCollection var1) {
         return this.collection.containsAll(var1);
      }

      public boolean addAll(DoubleCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(DoubleCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(DoubleCollection var1) {
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

   public static class SynchronizedCollection implements DoubleCollection, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final DoubleCollection collection;
      protected final Object sync;

      protected SynchronizedCollection(DoubleCollection var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.collection = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedCollection(DoubleCollection var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.collection = var1;
            this.sync = this;
         }
      }

      public boolean add(double var1) {
         synchronized(this.sync) {
            return this.collection.add(var1);
         }
      }

      public boolean contains(double var1) {
         synchronized(this.sync) {
            return this.collection.contains(var1);
         }
      }

      public boolean rem(double var1) {
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

      public double[] toDoubleArray() {
         synchronized(this.sync) {
            return this.collection.toDoubleArray();
         }
      }

      public Object[] toArray() {
         synchronized(this.sync) {
            return this.collection.toArray();
         }
      }

      /** @deprecated */
      @Deprecated
      public double[] toDoubleArray(double[] var1) {
         return this.toArray(var1);
      }

      public double[] toArray(double[] var1) {
         synchronized(this.sync) {
            return this.collection.toArray(var1);
         }
      }

      public boolean addAll(DoubleCollection var1) {
         synchronized(this.sync) {
            return this.collection.addAll(var1);
         }
      }

      public boolean containsAll(DoubleCollection var1) {
         synchronized(this.sync) {
            return this.collection.containsAll(var1);
         }
      }

      public boolean removeAll(DoubleCollection var1) {
         synchronized(this.sync) {
            return this.collection.removeAll(var1);
         }
      }

      public boolean removeIf(DoublePredicate var1) {
         synchronized(this.sync) {
            return this.collection.removeIf(var1);
         }
      }

      public boolean retainAll(DoubleCollection var1) {
         synchronized(this.sync) {
            return this.collection.retainAll(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean add(Double var1) {
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

      public DoubleIterator iterator() {
         return this.collection.iterator();
      }

      public boolean addAll(Collection<? extends Double> var1) {
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

   public abstract static class EmptyCollection extends AbstractDoubleCollection {
      protected EmptyCollection() {
         super();
      }

      public boolean contains(double var1) {
         return false;
      }

      public Object[] toArray() {
         return ObjectArrays.EMPTY_ARRAY;
      }

      public DoubleBidirectionalIterator iterator() {
         return DoubleIterators.EMPTY_ITERATOR;
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

      public boolean addAll(Collection<? extends Double> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(DoubleCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(DoubleCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(DoubleCollection var1) {
         throw new UnsupportedOperationException();
      }
   }
}
