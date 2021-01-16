package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

public final class ReferenceSets {
   public static final ReferenceSets.EmptySet EMPTY_SET = new ReferenceSets.EmptySet();

   private ReferenceSets() {
      super();
   }

   public static <K> ReferenceSet<K> emptySet() {
      return EMPTY_SET;
   }

   public static <K> ReferenceSet<K> singleton(K var0) {
      return new ReferenceSets.Singleton(var0);
   }

   public static <K> ReferenceSet<K> synchronize(ReferenceSet<K> var0) {
      return new ReferenceSets.SynchronizedSet(var0);
   }

   public static <K> ReferenceSet<K> synchronize(ReferenceSet<K> var0, Object var1) {
      return new ReferenceSets.SynchronizedSet(var0, var1);
   }

   public static <K> ReferenceSet<K> unmodifiable(ReferenceSet<K> var0) {
      return new ReferenceSets.UnmodifiableSet(var0);
   }

   public static class UnmodifiableSet<K> extends ReferenceCollections.UnmodifiableCollection<K> implements ReferenceSet<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected UnmodifiableSet(ReferenceSet<K> var1) {
         super(var1);
      }

      public boolean remove(Object var1) {
         throw new UnsupportedOperationException();
      }

      public boolean equals(Object var1) {
         return var1 == this ? true : this.collection.equals(var1);
      }

      public int hashCode() {
         return this.collection.hashCode();
      }
   }

   public static class SynchronizedSet<K> extends ReferenceCollections.SynchronizedCollection<K> implements ReferenceSet<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected SynchronizedSet(ReferenceSet<K> var1, Object var2) {
         super(var1, var2);
      }

      protected SynchronizedSet(ReferenceSet<K> var1) {
         super(var1);
      }

      public boolean remove(Object var1) {
         synchronized(this.sync) {
            return this.collection.remove(var1);
         }
      }
   }

   public static class Singleton<K> extends AbstractReferenceSet<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final K element;

      protected Singleton(K var1) {
         super();
         this.element = var1;
      }

      public boolean contains(Object var1) {
         return var1 == this.element;
      }

      public boolean remove(Object var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectListIterator<K> iterator() {
         return ObjectIterators.singleton(this.element);
      }

      public int size() {
         return 1;
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

      public Object clone() {
         return this;
      }
   }

   public static class EmptySet<K> extends ReferenceCollections.EmptyCollection<K> implements ReferenceSet<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
         super();
      }

      public boolean remove(Object var1) {
         throw new UnsupportedOperationException();
      }

      public Object clone() {
         return ReferenceSets.EMPTY_SET;
      }

      public boolean equals(Object var1) {
         return var1 instanceof Set && ((Set)var1).isEmpty();
      }

      private Object readResolve() {
         return ReferenceSets.EMPTY_SET;
      }
   }
}
