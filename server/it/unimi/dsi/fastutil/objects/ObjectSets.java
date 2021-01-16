package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

public final class ObjectSets {
   public static final ObjectSets.EmptySet EMPTY_SET = new ObjectSets.EmptySet();

   private ObjectSets() {
      super();
   }

   public static <K> ObjectSet<K> emptySet() {
      return EMPTY_SET;
   }

   public static <K> ObjectSet<K> singleton(K var0) {
      return new ObjectSets.Singleton(var0);
   }

   public static <K> ObjectSet<K> synchronize(ObjectSet<K> var0) {
      return new ObjectSets.SynchronizedSet(var0);
   }

   public static <K> ObjectSet<K> synchronize(ObjectSet<K> var0, Object var1) {
      return new ObjectSets.SynchronizedSet(var0, var1);
   }

   public static <K> ObjectSet<K> unmodifiable(ObjectSet<K> var0) {
      return new ObjectSets.UnmodifiableSet(var0);
   }

   public static class UnmodifiableSet<K> extends ObjectCollections.UnmodifiableCollection<K> implements ObjectSet<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected UnmodifiableSet(ObjectSet<K> var1) {
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

   public static class SynchronizedSet<K> extends ObjectCollections.SynchronizedCollection<K> implements ObjectSet<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected SynchronizedSet(ObjectSet<K> var1, Object var2) {
         super(var1, var2);
      }

      protected SynchronizedSet(ObjectSet<K> var1) {
         super(var1);
      }

      public boolean remove(Object var1) {
         synchronized(this.sync) {
            return this.collection.remove(var1);
         }
      }
   }

   public static class Singleton<K> extends AbstractObjectSet<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final K element;

      protected Singleton(K var1) {
         super();
         this.element = var1;
      }

      public boolean contains(Object var1) {
         return Objects.equals(var1, this.element);
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

   public static class EmptySet<K> extends ObjectCollections.EmptyCollection<K> implements ObjectSet<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
         super();
      }

      public boolean remove(Object var1) {
         throw new UnsupportedOperationException();
      }

      public Object clone() {
         return ObjectSets.EMPTY_SET;
      }

      public boolean equals(Object var1) {
         return var1 instanceof Set && ((Set)var1).isEmpty();
      }

      private Object readResolve() {
         return ObjectSets.EMPTY_SET;
      }
   }
}
