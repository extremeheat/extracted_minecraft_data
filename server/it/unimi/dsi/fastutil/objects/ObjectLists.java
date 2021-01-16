package it.unimi.dsi.fastutil.objects;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.RandomAccess;

public final class ObjectLists {
   public static final ObjectLists.EmptyList EMPTY_LIST = new ObjectLists.EmptyList();

   private ObjectLists() {
      super();
   }

   public static <K> ObjectList<K> shuffle(ObjectList<K> var0, Random var1) {
      int var2 = var0.size();

      while(var2-- != 0) {
         int var3 = var1.nextInt(var2 + 1);
         Object var4 = var0.get(var2);
         var0.set(var2, var0.get(var3));
         var0.set(var3, var4);
      }

      return var0;
   }

   public static <K> ObjectList<K> emptyList() {
      return EMPTY_LIST;
   }

   public static <K> ObjectList<K> singleton(K var0) {
      return new ObjectLists.Singleton(var0);
   }

   public static <K> ObjectList<K> synchronize(ObjectList<K> var0) {
      return (ObjectList)(var0 instanceof RandomAccess ? new ObjectLists.SynchronizedRandomAccessList(var0) : new ObjectLists.SynchronizedList(var0));
   }

   public static <K> ObjectList<K> synchronize(ObjectList<K> var0, Object var1) {
      return (ObjectList)(var0 instanceof RandomAccess ? new ObjectLists.SynchronizedRandomAccessList(var0, var1) : new ObjectLists.SynchronizedList(var0, var1));
   }

   public static <K> ObjectList<K> unmodifiable(ObjectList<K> var0) {
      return (ObjectList)(var0 instanceof RandomAccess ? new ObjectLists.UnmodifiableRandomAccessList(var0) : new ObjectLists.UnmodifiableList(var0));
   }

   public static class UnmodifiableRandomAccessList<K> extends ObjectLists.UnmodifiableList<K> implements RandomAccess, Serializable {
      private static final long serialVersionUID = 0L;

      protected UnmodifiableRandomAccessList(ObjectList<K> var1) {
         super(var1);
      }

      public ObjectList<K> subList(int var1, int var2) {
         return new ObjectLists.UnmodifiableRandomAccessList(this.list.subList(var1, var2));
      }
   }

   public static class UnmodifiableList<K> extends ObjectCollections.UnmodifiableCollection<K> implements ObjectList<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ObjectList<K> list;

      protected UnmodifiableList(ObjectList<K> var1) {
         super(var1);
         this.list = var1;
      }

      public K get(int var1) {
         return this.list.get(var1);
      }

      public K set(int var1, K var2) {
         throw new UnsupportedOperationException();
      }

      public void add(int var1, K var2) {
         throw new UnsupportedOperationException();
      }

      public K remove(int var1) {
         throw new UnsupportedOperationException();
      }

      public int indexOf(Object var1) {
         return this.list.indexOf(var1);
      }

      public int lastIndexOf(Object var1) {
         return this.list.lastIndexOf(var1);
      }

      public boolean addAll(int var1, Collection<? extends K> var2) {
         throw new UnsupportedOperationException();
      }

      public void getElements(int var1, Object[] var2, int var3, int var4) {
         this.list.getElements(var1, var2, var3, var4);
      }

      public void removeElements(int var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, K[] var2, int var3, int var4) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, K[] var2) {
         throw new UnsupportedOperationException();
      }

      public void size(int var1) {
         this.list.size(var1);
      }

      public ObjectListIterator<K> listIterator() {
         return ObjectIterators.unmodifiable(this.list.listIterator());
      }

      public ObjectListIterator<K> iterator() {
         return this.listIterator();
      }

      public ObjectListIterator<K> listIterator(int var1) {
         return ObjectIterators.unmodifiable(this.list.listIterator(var1));
      }

      public ObjectList<K> subList(int var1, int var2) {
         return new ObjectLists.UnmodifiableList(this.list.subList(var1, var2));
      }

      public boolean equals(Object var1) {
         return var1 == this ? true : this.collection.equals(var1);
      }

      public int hashCode() {
         return this.collection.hashCode();
      }

      public int compareTo(List<? extends K> var1) {
         return this.list.compareTo(var1);
      }
   }

   public static class SynchronizedRandomAccessList<K> extends ObjectLists.SynchronizedList<K> implements RandomAccess, Serializable {
      private static final long serialVersionUID = 0L;

      protected SynchronizedRandomAccessList(ObjectList<K> var1, Object var2) {
         super(var1, var2);
      }

      protected SynchronizedRandomAccessList(ObjectList<K> var1) {
         super(var1);
      }

      public ObjectList<K> subList(int var1, int var2) {
         synchronized(this.sync) {
            return new ObjectLists.SynchronizedRandomAccessList(this.list.subList(var1, var2), this.sync);
         }
      }
   }

   public static class SynchronizedList<K> extends ObjectCollections.SynchronizedCollection<K> implements ObjectList<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ObjectList<K> list;

      protected SynchronizedList(ObjectList<K> var1, Object var2) {
         super(var1, var2);
         this.list = var1;
      }

      protected SynchronizedList(ObjectList<K> var1) {
         super(var1);
         this.list = var1;
      }

      public K get(int var1) {
         synchronized(this.sync) {
            return this.list.get(var1);
         }
      }

      public K set(int var1, K var2) {
         synchronized(this.sync) {
            return this.list.set(var1, var2);
         }
      }

      public void add(int var1, K var2) {
         synchronized(this.sync) {
            this.list.add(var1, var2);
         }
      }

      public K remove(int var1) {
         synchronized(this.sync) {
            return this.list.remove(var1);
         }
      }

      public int indexOf(Object var1) {
         synchronized(this.sync) {
            return this.list.indexOf(var1);
         }
      }

      public int lastIndexOf(Object var1) {
         synchronized(this.sync) {
            return this.list.lastIndexOf(var1);
         }
      }

      public boolean addAll(int var1, Collection<? extends K> var2) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var2);
         }
      }

      public void getElements(int var1, Object[] var2, int var3, int var4) {
         synchronized(this.sync) {
            this.list.getElements(var1, var2, var3, var4);
         }
      }

      public void removeElements(int var1, int var2) {
         synchronized(this.sync) {
            this.list.removeElements(var1, var2);
         }
      }

      public void addElements(int var1, K[] var2, int var3, int var4) {
         synchronized(this.sync) {
            this.list.addElements(var1, var2, var3, var4);
         }
      }

      public void addElements(int var1, K[] var2) {
         synchronized(this.sync) {
            this.list.addElements(var1, var2);
         }
      }

      public void size(int var1) {
         synchronized(this.sync) {
            this.list.size(var1);
         }
      }

      public ObjectListIterator<K> listIterator() {
         return this.list.listIterator();
      }

      public ObjectListIterator<K> iterator() {
         return this.listIterator();
      }

      public ObjectListIterator<K> listIterator(int var1) {
         return this.list.listIterator(var1);
      }

      public ObjectList<K> subList(int var1, int var2) {
         synchronized(this.sync) {
            return new ObjectLists.SynchronizedList(this.list.subList(var1, var2), this.sync);
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

      public int hashCode() {
         synchronized(this.sync) {
            return this.collection.hashCode();
         }
      }

      public int compareTo(List<? extends K> var1) {
         synchronized(this.sync) {
            return this.list.compareTo(var1);
         }
      }

      private void writeObject(ObjectOutputStream var1) throws IOException {
         synchronized(this.sync) {
            var1.defaultWriteObject();
         }
      }
   }

   public static class Singleton<K> extends AbstractObjectList<K> implements RandomAccess, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final K element;

      protected Singleton(K var1) {
         super();
         this.element = var1;
      }

      public K get(int var1) {
         if (var1 == 0) {
            return this.element;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public boolean remove(Object var1) {
         throw new UnsupportedOperationException();
      }

      public K remove(int var1) {
         throw new UnsupportedOperationException();
      }

      public boolean contains(Object var1) {
         return Objects.equals(var1, this.element);
      }

      public Object[] toArray() {
         Object[] var1 = new Object[]{this.element};
         return var1;
      }

      public ObjectListIterator<K> listIterator() {
         return ObjectIterators.singleton(this.element);
      }

      public ObjectListIterator<K> iterator() {
         return this.listIterator();
      }

      public ObjectListIterator<K> listIterator(int var1) {
         if (var1 <= 1 && var1 >= 0) {
            ObjectListIterator var2 = this.listIterator();
            if (var1 == 1) {
               var2.next();
            }

            return var2;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public ObjectList<K> subList(int var1, int var2) {
         this.ensureIndex(var1);
         this.ensureIndex(var2);
         if (var1 > var2) {
            throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var2 + ")");
         } else {
            return (ObjectList)(var1 == 0 && var2 == 1 ? this : ObjectLists.EMPTY_LIST);
         }
      }

      public boolean addAll(int var1, Collection<? extends K> var2) {
         throw new UnsupportedOperationException();
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

      public int size() {
         return 1;
      }

      public void size(int var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyList<K> extends ObjectCollections.EmptyCollection<K> implements ObjectList<K>, RandomAccess, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyList() {
         super();
      }

      public K get(int var1) {
         throw new IndexOutOfBoundsException();
      }

      public boolean remove(Object var1) {
         throw new UnsupportedOperationException();
      }

      public K remove(int var1) {
         throw new UnsupportedOperationException();
      }

      public void add(int var1, K var2) {
         throw new UnsupportedOperationException();
      }

      public K set(int var1, K var2) {
         throw new UnsupportedOperationException();
      }

      public int indexOf(Object var1) {
         return -1;
      }

      public int lastIndexOf(Object var1) {
         return -1;
      }

      public boolean addAll(int var1, Collection<? extends K> var2) {
         throw new UnsupportedOperationException();
      }

      public ObjectListIterator<K> listIterator() {
         return ObjectIterators.EMPTY_ITERATOR;
      }

      public ObjectListIterator<K> iterator() {
         return ObjectIterators.EMPTY_ITERATOR;
      }

      public ObjectListIterator<K> listIterator(int var1) {
         if (var1 == 0) {
            return ObjectIterators.EMPTY_ITERATOR;
         } else {
            throw new IndexOutOfBoundsException(String.valueOf(var1));
         }
      }

      public ObjectList<K> subList(int var1, int var2) {
         if (var1 == 0 && var2 == 0) {
            return this;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public void getElements(int var1, Object[] var2, int var3, int var4) {
         if (var1 != 0 || var4 != 0 || var3 < 0 || var3 > var2.length) {
            throw new IndexOutOfBoundsException();
         }
      }

      public void removeElements(int var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, K[] var2, int var3, int var4) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, K[] var2) {
         throw new UnsupportedOperationException();
      }

      public void size(int var1) {
         throw new UnsupportedOperationException();
      }

      public int compareTo(List<? extends K> var1) {
         if (var1 == this) {
            return 0;
         } else {
            return var1.isEmpty() ? 0 : -1;
         }
      }

      public Object clone() {
         return ObjectLists.EMPTY_LIST;
      }

      public int hashCode() {
         return 1;
      }

      public boolean equals(Object var1) {
         return var1 instanceof List && ((List)var1).isEmpty();
      }

      public String toString() {
         return "[]";
      }

      private Object readResolve() {
         return ObjectLists.EMPTY_LIST;
      }
   }
}
