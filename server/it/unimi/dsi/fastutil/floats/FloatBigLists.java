package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.BigList;
import java.io.Serializable;
import java.util.Collection;
import java.util.Random;

public final class FloatBigLists {
   public static final FloatBigLists.EmptyBigList EMPTY_BIG_LIST = new FloatBigLists.EmptyBigList();

   private FloatBigLists() {
      super();
   }

   public static FloatBigList shuffle(FloatBigList var0, Random var1) {
      long var2 = var0.size64();

      while(var2-- != 0L) {
         long var4 = (var1.nextLong() & 9223372036854775807L) % (var2 + 1L);
         float var6 = var0.getFloat(var2);
         var0.set(var2, var0.getFloat(var4));
         var0.set(var4, var6);
      }

      return var0;
   }

   public static FloatBigList singleton(float var0) {
      return new FloatBigLists.Singleton(var0);
   }

   public static FloatBigList singleton(Object var0) {
      return new FloatBigLists.Singleton((Float)var0);
   }

   public static FloatBigList synchronize(FloatBigList var0) {
      return new FloatBigLists.SynchronizedBigList(var0);
   }

   public static FloatBigList synchronize(FloatBigList var0, Object var1) {
      return new FloatBigLists.SynchronizedBigList(var0, var1);
   }

   public static FloatBigList unmodifiable(FloatBigList var0) {
      return new FloatBigLists.UnmodifiableBigList(var0);
   }

   public static FloatBigList asBigList(FloatList var0) {
      return new FloatBigLists.ListBigList(var0);
   }

   public static class ListBigList extends AbstractFloatBigList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final FloatList list;

      protected ListBigList(FloatList var1) {
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

      public FloatBigListIterator iterator() {
         return FloatBigListIterators.asBigListIterator(this.list.iterator());
      }

      public FloatBigListIterator listIterator() {
         return FloatBigListIterators.asBigListIterator(this.list.listIterator());
      }

      public FloatBigListIterator listIterator(long var1) {
         return FloatBigListIterators.asBigListIterator(this.list.listIterator(this.intIndex(var1)));
      }

      public boolean addAll(long var1, Collection<? extends Float> var3) {
         return this.list.addAll(this.intIndex(var1), (Collection)var3);
      }

      public FloatBigList subList(long var1, long var3) {
         return new FloatBigLists.ListBigList(this.list.subList(this.intIndex(var1), this.intIndex(var3)));
      }

      public boolean contains(float var1) {
         return this.list.contains(var1);
      }

      public float[] toFloatArray() {
         return this.list.toFloatArray();
      }

      public void removeElements(long var1, long var3) {
         this.list.removeElements(this.intIndex(var1), this.intIndex(var3));
      }

      /** @deprecated */
      @Deprecated
      public float[] toFloatArray(float[] var1) {
         return this.list.toArray(var1);
      }

      public boolean addAll(long var1, FloatCollection var3) {
         return this.list.addAll(this.intIndex(var1), var3);
      }

      public boolean addAll(FloatCollection var1) {
         return this.list.addAll(var1);
      }

      public boolean addAll(long var1, FloatBigList var3) {
         return this.list.addAll(this.intIndex(var1), (FloatCollection)var3);
      }

      public boolean addAll(FloatBigList var1) {
         return this.list.addAll(var1);
      }

      public boolean containsAll(FloatCollection var1) {
         return this.list.containsAll(var1);
      }

      public boolean removeAll(FloatCollection var1) {
         return this.list.removeAll(var1);
      }

      public boolean retainAll(FloatCollection var1) {
         return this.list.retainAll(var1);
      }

      public void add(long var1, float var3) {
         this.list.add(this.intIndex(var1), var3);
      }

      public boolean add(float var1) {
         return this.list.add(var1);
      }

      public float getFloat(long var1) {
         return this.list.getFloat(this.intIndex(var1));
      }

      public long indexOf(float var1) {
         return (long)this.list.indexOf(var1);
      }

      public long lastIndexOf(float var1) {
         return (long)this.list.lastIndexOf(var1);
      }

      public float removeFloat(long var1) {
         return this.list.removeFloat(this.intIndex(var1));
      }

      public float set(long var1, float var3) {
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

      public boolean addAll(Collection<? extends Float> var1) {
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

   public static class UnmodifiableBigList extends FloatCollections.UnmodifiableCollection implements FloatBigList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final FloatBigList list;

      protected UnmodifiableBigList(FloatBigList var1) {
         super(var1);
         this.list = var1;
      }

      public float getFloat(long var1) {
         return this.list.getFloat(var1);
      }

      public float set(long var1, float var3) {
         throw new UnsupportedOperationException();
      }

      public void add(long var1, float var3) {
         throw new UnsupportedOperationException();
      }

      public float removeFloat(long var1) {
         throw new UnsupportedOperationException();
      }

      public long indexOf(float var1) {
         return this.list.indexOf(var1);
      }

      public long lastIndexOf(float var1) {
         return this.list.lastIndexOf(var1);
      }

      public boolean addAll(long var1, Collection<? extends Float> var3) {
         throw new UnsupportedOperationException();
      }

      public void getElements(long var1, float[][] var3, long var4, long var6) {
         this.list.getElements(var1, var3, var4, var6);
      }

      public void removeElements(long var1, long var3) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, float[][] var3, long var4, long var6) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, float[][] var3) {
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

      public FloatBigListIterator iterator() {
         return this.listIterator();
      }

      public FloatBigListIterator listIterator() {
         return FloatBigListIterators.unmodifiable(this.list.listIterator());
      }

      public FloatBigListIterator listIterator(long var1) {
         return FloatBigListIterators.unmodifiable(this.list.listIterator(var1));
      }

      public FloatBigList subList(long var1, long var3) {
         return FloatBigLists.unmodifiable(this.list.subList(var1, var3));
      }

      public boolean equals(Object var1) {
         return var1 == this ? true : this.list.equals(var1);
      }

      public int hashCode() {
         return this.list.hashCode();
      }

      public int compareTo(BigList<? extends Float> var1) {
         return this.list.compareTo(var1);
      }

      public boolean addAll(long var1, FloatCollection var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(FloatBigList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, FloatBigList var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float get(long var1) {
         return this.list.get(var1);
      }

      /** @deprecated */
      @Deprecated
      public void add(long var1, Float var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float set(long var1, Float var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float remove(long var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public long indexOf(Object var1) {
         return this.list.indexOf(var1);
      }

      /** @deprecated */
      @Deprecated
      public long lastIndexOf(Object var1) {
         return this.list.lastIndexOf(var1);
      }
   }

   public static class SynchronizedBigList extends FloatCollections.SynchronizedCollection implements FloatBigList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final FloatBigList list;

      protected SynchronizedBigList(FloatBigList var1, Object var2) {
         super(var1, var2);
         this.list = var1;
      }

      protected SynchronizedBigList(FloatBigList var1) {
         super(var1);
         this.list = var1;
      }

      public float getFloat(long var1) {
         synchronized(this.sync) {
            return this.list.getFloat(var1);
         }
      }

      public float set(long var1, float var3) {
         synchronized(this.sync) {
            return this.list.set(var1, var3);
         }
      }

      public void add(long var1, float var3) {
         synchronized(this.sync) {
            this.list.add(var1, var3);
         }
      }

      public float removeFloat(long var1) {
         synchronized(this.sync) {
            return this.list.removeFloat(var1);
         }
      }

      public long indexOf(float var1) {
         synchronized(this.sync) {
            return this.list.indexOf(var1);
         }
      }

      public long lastIndexOf(float var1) {
         synchronized(this.sync) {
            return this.list.lastIndexOf(var1);
         }
      }

      public boolean addAll(long var1, Collection<? extends Float> var3) {
         synchronized(this.sync) {
            return this.list.addAll(var1, (Collection)var3);
         }
      }

      public void getElements(long var1, float[][] var3, long var4, long var6) {
         synchronized(this.sync) {
            this.list.getElements(var1, var3, var4, var6);
         }
      }

      public void removeElements(long var1, long var3) {
         synchronized(this.sync) {
            this.list.removeElements(var1, var3);
         }
      }

      public void addElements(long var1, float[][] var3, long var4, long var6) {
         synchronized(this.sync) {
            this.list.addElements(var1, var3, var4, var6);
         }
      }

      public void addElements(long var1, float[][] var3) {
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

      public FloatBigListIterator iterator() {
         return this.list.listIterator();
      }

      public FloatBigListIterator listIterator() {
         return this.list.listIterator();
      }

      public FloatBigListIterator listIterator(long var1) {
         return this.list.listIterator(var1);
      }

      public FloatBigList subList(long var1, long var3) {
         synchronized(this.sync) {
            return FloatBigLists.synchronize(this.list.subList(var1, var3), this.sync);
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

      public int compareTo(BigList<? extends Float> var1) {
         synchronized(this.sync) {
            return this.list.compareTo(var1);
         }
      }

      public boolean addAll(long var1, FloatCollection var3) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var3);
         }
      }

      public boolean addAll(long var1, FloatBigList var3) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var3);
         }
      }

      public boolean addAll(FloatBigList var1) {
         synchronized(this.sync) {
            return this.list.addAll(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public void add(long var1, Float var3) {
         synchronized(this.sync) {
            this.list.add(var1, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float get(long var1) {
         synchronized(this.sync) {
            return this.list.get(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float set(long var1, Float var3) {
         synchronized(this.sync) {
            return this.list.set(var1, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float remove(long var1) {
         synchronized(this.sync) {
            return this.list.remove(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public long indexOf(Object var1) {
         synchronized(this.sync) {
            return this.list.indexOf(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public long lastIndexOf(Object var1) {
         synchronized(this.sync) {
            return this.list.lastIndexOf(var1);
         }
      }
   }

   public static class Singleton extends AbstractFloatBigList implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final float element;

      protected Singleton(float var1) {
         super();
         this.element = var1;
      }

      public float getFloat(long var1) {
         if (var1 == 0L) {
            return this.element;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public boolean rem(float var1) {
         throw new UnsupportedOperationException();
      }

      public float removeFloat(long var1) {
         throw new UnsupportedOperationException();
      }

      public boolean contains(float var1) {
         return Float.floatToIntBits(var1) == Float.floatToIntBits(this.element);
      }

      public float[] toFloatArray() {
         float[] var1 = new float[]{this.element};
         return var1;
      }

      public FloatBigListIterator listIterator() {
         return FloatBigListIterators.singleton(this.element);
      }

      public FloatBigListIterator listIterator(long var1) {
         if (var1 <= 1L && var1 >= 0L) {
            FloatBigListIterator var3 = this.listIterator();
            if (var1 == 1L) {
               var3.nextFloat();
            }

            return var3;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public FloatBigList subList(long var1, long var3) {
         this.ensureIndex(var1);
         this.ensureIndex(var3);
         if (var1 > var3) {
            throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
         } else {
            return (FloatBigList)(var1 == 0L && var3 == 1L ? this : FloatBigLists.EMPTY_BIG_LIST);
         }
      }

      public boolean addAll(long var1, Collection<? extends Float> var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(Collection<? extends Float> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(FloatBigList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, FloatBigList var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, FloatCollection var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(FloatCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(FloatCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(FloatCollection var1) {
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

   public static class EmptyBigList extends FloatCollections.EmptyCollection implements FloatBigList, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyBigList() {
         super();
      }

      public float getFloat(long var1) {
         throw new IndexOutOfBoundsException();
      }

      public boolean rem(float var1) {
         throw new UnsupportedOperationException();
      }

      public float removeFloat(long var1) {
         throw new UnsupportedOperationException();
      }

      public void add(long var1, float var3) {
         throw new UnsupportedOperationException();
      }

      public float set(long var1, float var3) {
         throw new UnsupportedOperationException();
      }

      public long indexOf(float var1) {
         return -1L;
      }

      public long lastIndexOf(float var1) {
         return -1L;
      }

      public boolean addAll(long var1, Collection<? extends Float> var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(FloatCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(FloatBigList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, FloatCollection var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, FloatBigList var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public void add(long var1, Float var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean add(Float var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float get(long var1) {
         throw new IndexOutOfBoundsException();
      }

      /** @deprecated */
      @Deprecated
      public Float set(long var1, Float var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float remove(long var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public long indexOf(Object var1) {
         return -1L;
      }

      /** @deprecated */
      @Deprecated
      public long lastIndexOf(Object var1) {
         return -1L;
      }

      public FloatBigListIterator listIterator() {
         return FloatBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      public FloatBigListIterator iterator() {
         return FloatBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      public FloatBigListIterator listIterator(long var1) {
         if (var1 == 0L) {
            return FloatBigListIterators.EMPTY_BIG_LIST_ITERATOR;
         } else {
            throw new IndexOutOfBoundsException(String.valueOf(var1));
         }
      }

      public FloatBigList subList(long var1, long var3) {
         if (var1 == 0L && var3 == 0L) {
            return this;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public void getElements(long var1, float[][] var3, long var4, long var6) {
         FloatBigArrays.ensureOffsetLength(var3, var4, var6);
         if (var1 != 0L) {
            throw new IndexOutOfBoundsException();
         }
      }

      public void removeElements(long var1, long var3) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, float[][] var3, long var4, long var6) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, float[][] var3) {
         throw new UnsupportedOperationException();
      }

      public void size(long var1) {
         throw new UnsupportedOperationException();
      }

      public long size64() {
         return 0L;
      }

      public int compareTo(BigList<? extends Float> var1) {
         if (var1 == this) {
            return 0;
         } else {
            return var1.isEmpty() ? 0 : -1;
         }
      }

      public Object clone() {
         return FloatBigLists.EMPTY_BIG_LIST;
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
         return FloatBigLists.EMPTY_BIG_LIST;
      }
   }
}
