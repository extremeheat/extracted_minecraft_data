package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

public final class BooleanCollections {
   private BooleanCollections() {
      super();
   }

   public static BooleanCollection synchronize(BooleanCollection var0) {
      return new BooleanCollections.SynchronizedCollection(var0);
   }

   public static BooleanCollection synchronize(BooleanCollection var0, Object var1) {
      return new BooleanCollections.SynchronizedCollection(var0, var1);
   }

   public static BooleanCollection unmodifiable(BooleanCollection var0) {
      return new BooleanCollections.UnmodifiableCollection(var0);
   }

   public static BooleanCollection asCollection(BooleanIterable var0) {
      return (BooleanCollection)(var0 instanceof BooleanCollection ? (BooleanCollection)var0 : new BooleanCollections.IterableCollection(var0));
   }

   public static class IterableCollection extends AbstractBooleanCollection implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final BooleanIterable iterable;

      protected IterableCollection(BooleanIterable var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.iterable = var1;
         }
      }

      public int size() {
         int var1 = 0;

         for(BooleanIterator var2 = this.iterator(); var2.hasNext(); ++var1) {
            var2.nextBoolean();
         }

         return var1;
      }

      public boolean isEmpty() {
         return !this.iterable.iterator().hasNext();
      }

      public BooleanIterator iterator() {
         return this.iterable.iterator();
      }
   }

   public static class UnmodifiableCollection implements BooleanCollection, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final BooleanCollection collection;

      protected UnmodifiableCollection(BooleanCollection var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.collection = var1;
         }
      }

      public boolean add(boolean var1) {
         throw new UnsupportedOperationException();
      }

      public boolean rem(boolean var1) {
         throw new UnsupportedOperationException();
      }

      public int size() {
         return this.collection.size();
      }

      public boolean isEmpty() {
         return this.collection.isEmpty();
      }

      public boolean contains(boolean var1) {
         return this.collection.contains(var1);
      }

      public BooleanIterator iterator() {
         return BooleanIterators.unmodifiable(this.collection.iterator());
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

      public boolean addAll(Collection<? extends Boolean> var1) {
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
      public boolean add(Boolean var1) {
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

      public boolean[] toBooleanArray() {
         return this.collection.toBooleanArray();
      }

      /** @deprecated */
      @Deprecated
      public boolean[] toBooleanArray(boolean[] var1) {
         return this.toArray(var1);
      }

      public boolean[] toArray(boolean[] var1) {
         return this.collection.toArray(var1);
      }

      public boolean containsAll(BooleanCollection var1) {
         return this.collection.containsAll(var1);
      }

      public boolean addAll(BooleanCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(BooleanCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(BooleanCollection var1) {
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

   public static class SynchronizedCollection implements BooleanCollection, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final BooleanCollection collection;
      protected final Object sync;

      protected SynchronizedCollection(BooleanCollection var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.collection = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedCollection(BooleanCollection var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.collection = var1;
            this.sync = this;
         }
      }

      public boolean add(boolean var1) {
         synchronized(this.sync) {
            return this.collection.add(var1);
         }
      }

      public boolean contains(boolean var1) {
         synchronized(this.sync) {
            return this.collection.contains(var1);
         }
      }

      public boolean rem(boolean var1) {
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

      public boolean[] toBooleanArray() {
         synchronized(this.sync) {
            return this.collection.toBooleanArray();
         }
      }

      public Object[] toArray() {
         synchronized(this.sync) {
            return this.collection.toArray();
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean[] toBooleanArray(boolean[] var1) {
         return this.toArray(var1);
      }

      public boolean[] toArray(boolean[] var1) {
         synchronized(this.sync) {
            return this.collection.toArray(var1);
         }
      }

      public boolean addAll(BooleanCollection var1) {
         synchronized(this.sync) {
            return this.collection.addAll(var1);
         }
      }

      public boolean containsAll(BooleanCollection var1) {
         synchronized(this.sync) {
            return this.collection.containsAll(var1);
         }
      }

      public boolean removeAll(BooleanCollection var1) {
         synchronized(this.sync) {
            return this.collection.removeAll(var1);
         }
      }

      public boolean retainAll(BooleanCollection var1) {
         synchronized(this.sync) {
            return this.collection.retainAll(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean add(Boolean var1) {
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

      public BooleanIterator iterator() {
         return this.collection.iterator();
      }

      public boolean addAll(Collection<? extends Boolean> var1) {
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

   public abstract static class EmptyCollection extends AbstractBooleanCollection {
      protected EmptyCollection() {
         super();
      }

      public boolean contains(boolean var1) {
         return false;
      }

      public Object[] toArray() {
         return ObjectArrays.EMPTY_ARRAY;
      }

      public BooleanBidirectionalIterator iterator() {
         return BooleanIterators.EMPTY_ITERATOR;
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

      public boolean addAll(Collection<? extends Boolean> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(BooleanCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(BooleanCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(BooleanCollection var1) {
         throw new UnsupportedOperationException();
      }
   }
}
