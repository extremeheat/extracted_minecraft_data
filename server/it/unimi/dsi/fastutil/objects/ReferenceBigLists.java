package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.BigList;
import java.io.Serializable;
import java.util.Collection;
import java.util.Random;

public final class ReferenceBigLists {
   public static final ReferenceBigLists.EmptyBigList EMPTY_BIG_LIST = new ReferenceBigLists.EmptyBigList();

   private ReferenceBigLists() {
      super();
   }

   public static <K> ReferenceBigList<K> shuffle(ReferenceBigList<K> var0, Random var1) {
      long var2 = var0.size64();

      while(var2-- != 0L) {
         long var4 = (var1.nextLong() & 9223372036854775807L) % (var2 + 1L);
         Object var6 = var0.get(var2);
         var0.set(var2, var0.get(var4));
         var0.set(var4, var6);
      }

      return var0;
   }

   public static <K> ReferenceBigList<K> emptyList() {
      return EMPTY_BIG_LIST;
   }

   public static <K> ReferenceBigList<K> singleton(K var0) {
      return new ReferenceBigLists.Singleton(var0);
   }

   public static <K> ReferenceBigList<K> synchronize(ReferenceBigList<K> var0) {
      return new ReferenceBigLists.SynchronizedBigList(var0);
   }

   public static <K> ReferenceBigList<K> synchronize(ReferenceBigList<K> var0, Object var1) {
      return new ReferenceBigLists.SynchronizedBigList(var0, var1);
   }

   public static <K> ReferenceBigList<K> unmodifiable(ReferenceBigList<K> var0) {
      return new ReferenceBigLists.UnmodifiableBigList(var0);
   }

   public static <K> ReferenceBigList<K> asBigList(ReferenceList<K> var0) {
      return new ReferenceBigLists.ListBigList(var0);
   }

   public static class ListBigList<K> extends AbstractReferenceBigList<K> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final ReferenceList<K> list;

      protected ListBigList(ReferenceList<K> var1) {
         super();
         this.list = var1;
      }

      private int intIndex(long var1) {
         if (var1 >= 2147483647L) {
            throw new IndexOutOfBoundsException("This big list is restricted to 32-bit indices");
         } else {
            return (int)var1;
         }
      }

      public long size64() {
         return (long)this.list.size();
      }

      public void size(long var1) {
         this.list.size(this.intIndex(var1));
      }

      public ObjectBigListIterator<K> iterator() {
         return ObjectBigListIterators.asBigListIterator(this.list.iterator());
      }

      public ObjectBigListIterator<K> listIterator() {
         return ObjectBigListIterators.asBigListIterator(this.list.listIterator());
      }

      public ObjectBigListIterator<K> listIterator(long var1) {
         return ObjectBigListIterators.asBigListIterator(this.list.listIterator(this.intIndex(var1)));
      }

      public boolean addAll(long var1, Collection<? extends K> var3) {
         return this.list.addAll(this.intIndex(var1), var3);
      }

      public ReferenceBigList<K> subList(long var1, long var3) {
         return new ReferenceBigLists.ListBigList(this.list.subList(this.intIndex(var1), this.intIndex(var3)));
      }

      public boolean contains(Object var1) {
         return this.list.contains(var1);
      }

      public Object[] toArray() {
         return this.list.toArray();
      }

      public void removeElements(long var1, long var3) {
         this.list.removeElements(this.intIndex(var1), this.intIndex(var3));
      }

      public void add(long var1, K var3) {
         this.list.add(this.intIndex(var1), var3);
      }

      public boolean add(K var1) {
         return this.list.add(var1);
      }

      public K get(long var1) {
         return this.list.get(this.intIndex(var1));
      }

      public long indexOf(Object var1) {
         return (long)this.list.indexOf(var1);
      }

      public long lastIndexOf(Object var1) {
         return (long)this.list.lastIndexOf(var1);
      }

      public K remove(long var1) {
         return this.list.remove(this.intIndex(var1));
      }

      public K set(long var1, K var3) {
         return this.list.set(this.intIndex(var1), var3);
      }

      public boolean isEmpty() {
         return this.list.isEmpty();
      }

      public <T> T[] toArray(T[] var1) {
         return this.list.toArray(var1);
      }

      public boolean containsAll(Collection<?> var1) {
         return this.list.containsAll(var1);
      }

      public boolean addAll(Collection<? extends K> var1) {
         return this.list.addAll(var1);
      }

      public boolean removeAll(Collection<?> var1) {
         return this.list.removeAll(var1);
      }

      public boolean retainAll(Collection<?> var1) {
         return this.list.retainAll(var1);
      }

      public void clear() {
         this.list.clear();
      }

      public int hashCode() {
         return this.list.hashCode();
      }
   }

   public static class UnmodifiableBigList<K> extends ReferenceCollections.UnmodifiableCollection<K> implements ReferenceBigList<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ReferenceBigList<K> list;

      protected UnmodifiableBigList(ReferenceBigList<K> var1) {
         super(var1);
         this.list = var1;
      }

      public K get(long var1) {
         return this.list.get(var1);
      }

      public K set(long var1, K var3) {
         throw new UnsupportedOperationException();
      }

      public void add(long var1, K var3) {
         throw new UnsupportedOperationException();
      }

      public K remove(long var1) {
         throw new UnsupportedOperationException();
      }

      public long indexOf(Object var1) {
         return this.list.indexOf(var1);
      }

      public long lastIndexOf(Object var1) {
         return this.list.lastIndexOf(var1);
      }

      public boolean addAll(long var1, Collection<? extends K> var3) {
         throw new UnsupportedOperationException();
      }

      public void getElements(long var1, Object[][] var3, long var4, long var6) {
         this.list.getElements(var1, var3, var4, var6);
      }

      public void removeElements(long var1, long var3) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, K[][] var3, long var4, long var6) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, K[][] var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public void size(long var1) {
         this.list.size(var1);
      }

      public long size64() {
         return this.list.size64();
      }

      public ObjectBigListIterator<K> iterator() {
         return this.listIterator();
      }

      public ObjectBigListIterator<K> listIterator() {
         return ObjectBigListIterators.unmodifiable(this.list.listIterator());
      }

      public ObjectBigListIterator<K> listIterator(long var1) {
         return ObjectBigListIterators.unmodifiable(this.list.listIterator(var1));
      }

      public ReferenceBigList<K> subList(long var1, long var3) {
         return ReferenceBigLists.unmodifiable(this.list.subList(var1, var3));
      }

      public boolean equals(Object var1) {
         return var1 == this ? true : this.list.equals(var1);
      }

      public int hashCode() {
         return this.list.hashCode();
      }
   }

   public static class SynchronizedBigList<K> extends ReferenceCollections.SynchronizedCollection<K> implements ReferenceBigList<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ReferenceBigList<K> list;

      protected SynchronizedBigList(ReferenceBigList<K> var1, Object var2) {
         super(var1, var2);
         this.list = var1;
      }

      protected SynchronizedBigList(ReferenceBigList<K> var1) {
         super(var1);
         this.list = var1;
      }

      public K get(long var1) {
         synchronized(this.sync) {
            return this.list.get(var1);
         }
      }

      public K set(long var1, K var3) {
         synchronized(this.sync) {
            return this.list.set(var1, var3);
         }
      }

      public void add(long var1, K var3) {
         synchronized(this.sync) {
            this.list.add(var1, var3);
         }
      }

      public K remove(long var1) {
         synchronized(this.sync) {
            return this.list.remove(var1);
         }
      }

      public long indexOf(Object var1) {
         synchronized(this.sync) {
            return this.list.indexOf(var1);
         }
      }

      public long lastIndexOf(Object var1) {
         synchronized(this.sync) {
            return this.list.lastIndexOf(var1);
         }
      }

      public boolean addAll(long var1, Collection<? extends K> var3) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var3);
         }
      }

      public void getElements(long var1, Object[][] var3, long var4, long var6) {
         synchronized(this.sync) {
            this.list.getElements(var1, var3, var4, var6);
         }
      }

      public void removeElements(long var1, long var3) {
         synchronized(this.sync) {
            this.list.removeElements(var1, var3);
         }
      }

      public void addElements(long var1, K[][] var3, long var4, long var6) {
         synchronized(this.sync) {
            this.list.addElements(var1, var3, var4, var6);
         }
      }

      public void addElements(long var1, K[][] var3) {
         synchronized(this.sync) {
            this.list.addElements(var1, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public void size(long var1) {
         synchronized(this.sync) {
            this.list.size(var1);
         }
      }

      public long size64() {
         synchronized(this.sync) {
            return this.list.size64();
         }
      }

      public ObjectBigListIterator<K> iterator() {
         return this.list.listIterator();
      }

      public ObjectBigListIterator<K> listIterator() {
         return this.list.listIterator();
      }

      public ObjectBigListIterator<K> listIterator(long var1) {
         return this.list.listIterator(var1);
      }

      public ReferenceBigList<K> subList(long var1, long var3) {
         synchronized(this.sync) {
            return ReferenceBigLists.synchronize(this.list.subList(var1, var3), this.sync);
         }
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            synchronized(this.sync) {
               return this.list.equals(var1);
            }
         }
      }

      public int hashCode() {
         synchronized(this.sync) {
            return this.list.hashCode();
         }
      }
   }

   public static class Singleton<K> extends AbstractReferenceBigList<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final K element;

      protected Singleton(K var1) {
         super();
         this.element = var1;
      }

      public K get(long var1) {
         if (var1 == 0L) {
            return this.element;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public boolean remove(Object var1) {
         throw new UnsupportedOperationException();
      }

      public K remove(long var1) {
         throw new UnsupportedOperationException();
      }

      public boolean contains(Object var1) {
         return var1 == this.element;
      }

      public Object[] toArray() {
         Object[] var1 = new Object[]{this.element};
         return var1;
      }

      public ObjectBigListIterator<K> listIterator() {
         return ObjectBigListIterators.singleton(this.element);
      }

      public ObjectBigListIterator<K> listIterator(long var1) {
         if (var1 <= 1L && var1 >= 0L) {
            ObjectBigListIterator var3 = this.listIterator();
            if (var1 == 1L) {
               var3.next();
            }

            return var3;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public ReferenceBigList<K> subList(long var1, long var3) {
         this.ensureIndex(var1);
         this.ensureIndex(var3);
         if (var1 > var3) {
            throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
         } else {
            return (ReferenceBigList)(var1 == 0L && var3 == 1L ? this : ReferenceBigLists.EMPTY_BIG_LIST);
         }
      }

      public boolean addAll(long var1, Collection<? extends K> var3) {
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

      public void clear() {
         throw new UnsupportedOperationException();
      }

      public long size64() {
         return 1L;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyBigList<K> extends ReferenceCollections.EmptyCollection<K> implements ReferenceBigList<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyBigList() {
         super();
      }

      public K get(long var1) {
         throw new IndexOutOfBoundsException();
      }

      public boolean remove(Object var1) {
         throw new UnsupportedOperationException();
      }

      public K remove(long var1) {
         throw new UnsupportedOperationException();
      }

      public void add(long var1, K var3) {
         throw new UnsupportedOperationException();
      }

      public K set(long var1, K var3) {
         throw new UnsupportedOperationException();
      }

      public long indexOf(Object var1) {
         return -1L;
      }

      public long lastIndexOf(Object var1) {
         return -1L;
      }

      public boolean addAll(long var1, Collection<? extends K> var3) {
         throw new UnsupportedOperationException();
      }

      public ObjectBigListIterator<K> listIterator() {
         return ObjectBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      public ObjectBigListIterator<K> iterator() {
         return ObjectBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      public ObjectBigListIterator<K> listIterator(long var1) {
         if (var1 == 0L) {
            return ObjectBigListIterators.EMPTY_BIG_LIST_ITERATOR;
         } else {
            throw new IndexOutOfBoundsException(String.valueOf(var1));
         }
      }

      public ReferenceBigList<K> subList(long var1, long var3) {
         if (var1 == 0L && var3 == 0L) {
            return this;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public void getElements(long var1, Object[][] var3, long var4, long var6) {
         ObjectBigArrays.ensureOffsetLength(var3, var4, var6);
         if (var1 != 0L) {
            throw new IndexOutOfBoundsException();
         }
      }

      public void removeElements(long var1, long var3) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, K[][] var3, long var4, long var6) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, K[][] var3) {
         throw new UnsupportedOperationException();
      }

      public void size(long var1) {
         throw new UnsupportedOperationException();
      }

      public long size64() {
         return 0L;
      }

      public Object clone() {
         return ReferenceBigLists.EMPTY_BIG_LIST;
      }

      public int hashCode() {
         return 1;
      }

      public boolean equals(Object var1) {
         return var1 instanceof BigList && ((BigList)var1).isEmpty();
      }

      public String toString() {
         return "[]";
      }

      private Object readResolve() {
         return ReferenceBigLists.EMPTY_BIG_LIST;
      }
   }
}
