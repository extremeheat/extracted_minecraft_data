package it.unimi.dsi.fastutil.objects;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

public final class ReferenceCollections {
   private ReferenceCollections() {
      super();
   }

   public static <K> ReferenceCollection<K> synchronize(ReferenceCollection<K> var0) {
      return new ReferenceCollections.SynchronizedCollection(var0);
   }

   public static <K> ReferenceCollection<K> synchronize(ReferenceCollection<K> var0, Object var1) {
      return new ReferenceCollections.SynchronizedCollection(var0, var1);
   }

   public static <K> ReferenceCollection<K> unmodifiable(ReferenceCollection<K> var0) {
      return new ReferenceCollections.UnmodifiableCollection(var0);
   }

   public static <K> ReferenceCollection<K> asCollection(ObjectIterable<K> var0) {
      return (ReferenceCollection)(var0 instanceof ReferenceCollection ? (ReferenceCollection)var0 : new ReferenceCollections.IterableCollection(var0));
   }

   public static class IterableCollection<K> extends AbstractReferenceCollection<K> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ObjectIterable<K> iterable;

      protected IterableCollection(ObjectIterable<K> var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.iterable = var1;
         }
      }

      public int size() {
         int var1 = 0;

         for(ObjectIterator var2 = this.iterator(); var2.hasNext(); ++var1) {
            var2.next();
         }

         return var1;
      }

      public boolean isEmpty() {
         return !this.iterable.iterator().hasNext();
      }

      public ObjectIterator<K> iterator() {
         return this.iterable.iterator();
      }
   }

   public static class UnmodifiableCollection<K> implements ReferenceCollection<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ReferenceCollection<K> collection;

      protected UnmodifiableCollection(ReferenceCollection<K> var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.collection = var1;
         }
      }

      public boolean add(K var1) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(Object var1) {
         throw new UnsupportedOperationException();
      }

      public int size() {
         return this.collection.size();
      }

      public boolean isEmpty() {
         return this.collection.isEmpty();
      }

      public boolean contains(Object var1) {
         return this.collection.contains(var1);
      }

      public ObjectIterator<K> iterator() {
         return ObjectIterators.unmodifiable(this.collection.iterator());
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

      public boolean addAll(Collection<? extends K> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> var1) {
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

   public static class SynchronizedCollection<K> implements ReferenceCollection<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ReferenceCollection<K> collection;
      protected final Object sync;

      protected SynchronizedCollection(ReferenceCollection<K> var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.collection = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedCollection(ReferenceCollection<K> var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.collection = var1;
            this.sync = this;
         }
      }

      public boolean add(K var1) {
         synchronized(this.sync) {
            return this.collection.add(var1);
         }
      }

      public boolean contains(Object var1) {
         synchronized(this.sync) {
            return this.collection.contains(var1);
         }
      }

      public boolean remove(Object var1) {
         synchronized(this.sync) {
            return this.collection.remove(var1);
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

      public Object[] toArray() {
         synchronized(this.sync) {
            return this.collection.toArray();
         }
      }

      public <T> T[] toArray(T[] var1) {
         synchronized(this.sync) {
            return this.collection.toArray(var1);
         }
      }

      public ObjectIterator<K> iterator() {
         return this.collection.iterator();
      }

      public boolean addAll(Collection<? extends K> var1) {
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

   public abstract static class EmptyCollection<K> extends AbstractReferenceCollection<K> {
      protected EmptyCollection() {
         super();
      }

      public boolean contains(Object var1) {
         return false;
      }

      public Object[] toArray() {
         return ObjectArrays.EMPTY_ARRAY;
      }

      public ObjectBidirectionalIterator<K> iterator() {
         return ObjectIterators.EMPTY_ITERATOR;
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

      public boolean addAll(Collection<? extends K> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }
   }
}
