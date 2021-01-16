package it.unimi.dsi.fastutil.floats;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.RandomAccess;

public final class FloatLists {
   public static final FloatLists.EmptyList EMPTY_LIST = new FloatLists.EmptyList();

   private FloatLists() {
      super();
   }

   public static FloatList shuffle(FloatList var0, Random var1) {
      int var2 = var0.size();

      while(var2-- != 0) {
         int var3 = var1.nextInt(var2 + 1);
         float var4 = var0.getFloat(var2);
         var0.set(var2, var0.getFloat(var3));
         var0.set(var3, var4);
      }

      return var0;
   }

   public static FloatList singleton(float var0) {
      return new FloatLists.Singleton(var0);
   }

   public static FloatList singleton(Object var0) {
      return new FloatLists.Singleton((Float)var0);
   }

   public static FloatList synchronize(FloatList var0) {
      return (FloatList)(var0 instanceof RandomAccess ? new FloatLists.SynchronizedRandomAccessList(var0) : new FloatLists.SynchronizedList(var0));
   }

   public static FloatList synchronize(FloatList var0, Object var1) {
      return (FloatList)(var0 instanceof RandomAccess ? new FloatLists.SynchronizedRandomAccessList(var0, var1) : new FloatLists.SynchronizedList(var0, var1));
   }

   public static FloatList unmodifiable(FloatList var0) {
      return (FloatList)(var0 instanceof RandomAccess ? new FloatLists.UnmodifiableRandomAccessList(var0) : new FloatLists.UnmodifiableList(var0));
   }

   public static class UnmodifiableRandomAccessList extends FloatLists.UnmodifiableList implements RandomAccess, Serializable {
      private static final long serialVersionUID = 0L;

      protected UnmodifiableRandomAccessList(FloatList var1) {
         super(var1);
      }

      public FloatList subList(int var1, int var2) {
         return new FloatLists.UnmodifiableRandomAccessList(this.list.subList(var1, var2));
      }
   }

   public static class UnmodifiableList extends FloatCollections.UnmodifiableCollection implements FloatList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final FloatList list;

      protected UnmodifiableList(FloatList var1) {
         super(var1);
         this.list = var1;
      }

      public float getFloat(int var1) {
         return this.list.getFloat(var1);
      }

      public float set(int var1, float var2) {
         throw new UnsupportedOperationException();
      }

      public void add(int var1, float var2) {
         throw new UnsupportedOperationException();
      }

      public float removeFloat(int var1) {
         throw new UnsupportedOperationException();
      }

      public int indexOf(float var1) {
         return this.list.indexOf(var1);
      }

      public int lastIndexOf(float var1) {
         return this.list.lastIndexOf(var1);
      }

      public boolean addAll(int var1, Collection<? extends Float> var2) {
         throw new UnsupportedOperationException();
      }

      public void getElements(int var1, float[] var2, int var3, int var4) {
         this.list.getElements(var1, var2, var3, var4);
      }

      public void removeElements(int var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, float[] var2, int var3, int var4) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, float[] var2) {
         throw new UnsupportedOperationException();
      }

      public void size(int var1) {
         this.list.size(var1);
      }

      public FloatListIterator listIterator() {
         return FloatIterators.unmodifiable(this.list.listIterator());
      }

      public FloatListIterator iterator() {
         return this.listIterator();
      }

      public FloatListIterator listIterator(int var1) {
         return FloatIterators.unmodifiable(this.list.listIterator(var1));
      }

      public FloatList subList(int var1, int var2) {
         return new FloatLists.UnmodifiableList(this.list.subList(var1, var2));
      }

      public boolean equals(Object var1) {
         return var1 == this ? true : this.collection.equals(var1);
      }

      public int hashCode() {
         return this.collection.hashCode();
      }

      public int compareTo(List<? extends Float> var1) {
         return this.list.compareTo(var1);
      }

      public boolean addAll(int var1, FloatCollection var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(FloatList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, FloatList var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float get(int var1) {
         return this.list.get(var1);
      }

      /** @deprecated */
      @Deprecated
      public void add(int var1, Float var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float set(int var1, Float var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float remove(int var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public int indexOf(Object var1) {
         return this.list.indexOf(var1);
      }

      /** @deprecated */
      @Deprecated
      public int lastIndexOf(Object var1) {
         return this.list.lastIndexOf(var1);
      }
   }

   public static class SynchronizedRandomAccessList extends FloatLists.SynchronizedList implements RandomAccess, Serializable {
      private static final long serialVersionUID = 0L;

      protected SynchronizedRandomAccessList(FloatList var1, Object var2) {
         super(var1, var2);
      }

      protected SynchronizedRandomAccessList(FloatList var1) {
         super(var1);
      }

      public FloatList subList(int var1, int var2) {
         synchronized(this.sync) {
            return new FloatLists.SynchronizedRandomAccessList(this.list.subList(var1, var2), this.sync);
         }
      }
   }

   public static class SynchronizedList extends FloatCollections.SynchronizedCollection implements FloatList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final FloatList list;

      protected SynchronizedList(FloatList var1, Object var2) {
         super(var1, var2);
         this.list = var1;
      }

      protected SynchronizedList(FloatList var1) {
         super(var1);
         this.list = var1;
      }

      public float getFloat(int var1) {
         synchronized(this.sync) {
            return this.list.getFloat(var1);
         }
      }

      public float set(int var1, float var2) {
         synchronized(this.sync) {
            return this.list.set(var1, var2);
         }
      }

      public void add(int var1, float var2) {
         synchronized(this.sync) {
            this.list.add(var1, var2);
         }
      }

      public float removeFloat(int var1) {
         synchronized(this.sync) {
            return this.list.removeFloat(var1);
         }
      }

      public int indexOf(float var1) {
         synchronized(this.sync) {
            return this.list.indexOf(var1);
         }
      }

      public int lastIndexOf(float var1) {
         synchronized(this.sync) {
            return this.list.lastIndexOf(var1);
         }
      }

      public boolean addAll(int var1, Collection<? extends Float> var2) {
         synchronized(this.sync) {
            return this.list.addAll(var1, (Collection)var2);
         }
      }

      public void getElements(int var1, float[] var2, int var3, int var4) {
         synchronized(this.sync) {
            this.list.getElements(var1, var2, var3, var4);
         }
      }

      public void removeElements(int var1, int var2) {
         synchronized(this.sync) {
            this.list.removeElements(var1, var2);
         }
      }

      public void addElements(int var1, float[] var2, int var3, int var4) {
         synchronized(this.sync) {
            this.list.addElements(var1, var2, var3, var4);
         }
      }

      public void addElements(int var1, float[] var2) {
         synchronized(this.sync) {
            this.list.addElements(var1, var2);
         }
      }

      public void size(int var1) {
         synchronized(this.sync) {
            this.list.size(var1);
         }
      }

      public FloatListIterator listIterator() {
         return this.list.listIterator();
      }

      public FloatListIterator iterator() {
         return this.listIterator();
      }

      public FloatListIterator listIterator(int var1) {
         return this.list.listIterator(var1);
      }

      public FloatList subList(int var1, int var2) {
         synchronized(this.sync) {
            return new FloatLists.SynchronizedList(this.list.subList(var1, var2), this.sync);
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

      public int compareTo(List<? extends Float> var1) {
         synchronized(this.sync) {
            return this.list.compareTo(var1);
         }
      }

      public boolean addAll(int var1, FloatCollection var2) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var2);
         }
      }

      public boolean addAll(int var1, FloatList var2) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var2);
         }
      }

      public boolean addAll(FloatList var1) {
         synchronized(this.sync) {
            return this.list.addAll(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float get(int var1) {
         synchronized(this.sync) {
            return this.list.get(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public void add(int var1, Float var2) {
         synchronized(this.sync) {
            this.list.add(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float set(int var1, Float var2) {
         synchronized(this.sync) {
            return this.list.set(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float remove(int var1) {
         synchronized(this.sync) {
            return this.list.remove(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public int indexOf(Object var1) {
         synchronized(this.sync) {
            return this.list.indexOf(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public int lastIndexOf(Object var1) {
         synchronized(this.sync) {
            return this.list.lastIndexOf(var1);
         }
      }

      private void writeObject(ObjectOutputStream var1) throws IOException {
         synchronized(this.sync) {
            var1.defaultWriteObject();
         }
      }
   }

   public static class Singleton extends AbstractFloatList implements RandomAccess, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final float element;

      protected Singleton(float var1) {
         super();
         this.element = var1;
      }

      public float getFloat(int var1) {
         if (var1 == 0) {
            return this.element;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public boolean rem(float var1) {
         throw new UnsupportedOperationException();
      }

      public float removeFloat(int var1) {
         throw new UnsupportedOperationException();
      }

      public boolean contains(float var1) {
         return Float.floatToIntBits(var1) == Float.floatToIntBits(this.element);
      }

      public float[] toFloatArray() {
         float[] var1 = new float[]{this.element};
         return var1;
      }

      public FloatListIterator listIterator() {
         return FloatIterators.singleton(this.element);
      }

      public FloatListIterator iterator() {
         return this.listIterator();
      }

      public FloatListIterator listIterator(int var1) {
         if (var1 <= 1 && var1 >= 0) {
            FloatListIterator var2 = this.listIterator();
            if (var1 == 1) {
               var2.nextFloat();
            }

            return var2;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public FloatList subList(int var1, int var2) {
         this.ensureIndex(var1);
         this.ensureIndex(var2);
         if (var1 > var2) {
            throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var2 + ")");
         } else {
            return (FloatList)(var1 == 0 && var2 == 1 ? this : FloatLists.EMPTY_LIST);
         }
      }

      public boolean addAll(int var1, Collection<? extends Float> var2) {
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

      public boolean addAll(FloatList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, FloatList var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, FloatCollection var2) {
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

   public static class EmptyList extends FloatCollections.EmptyCollection implements FloatList, RandomAccess, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyList() {
         super();
      }

      public float getFloat(int var1) {
         throw new IndexOutOfBoundsException();
      }

      public boolean rem(float var1) {
         throw new UnsupportedOperationException();
      }

      public float removeFloat(int var1) {
         throw new UnsupportedOperationException();
      }

      public void add(int var1, float var2) {
         throw new UnsupportedOperationException();
      }

      public float set(int var1, float var2) {
         throw new UnsupportedOperationException();
      }

      public int indexOf(float var1) {
         return -1;
      }

      public int lastIndexOf(float var1) {
         return -1;
      }

      public boolean addAll(int var1, Collection<? extends Float> var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(FloatList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, FloatCollection var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, FloatList var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public void add(int var1, Float var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float get(int var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean add(Float var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float set(int var1, Float var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float remove(int var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public int indexOf(Object var1) {
         return -1;
      }

      /** @deprecated */
      @Deprecated
      public int lastIndexOf(Object var1) {
         return -1;
      }

      public FloatListIterator listIterator() {
         return FloatIterators.EMPTY_ITERATOR;
      }

      public FloatListIterator iterator() {
         return FloatIterators.EMPTY_ITERATOR;
      }

      public FloatListIterator listIterator(int var1) {
         if (var1 == 0) {
            return FloatIterators.EMPTY_ITERATOR;
         } else {
            throw new IndexOutOfBoundsException(String.valueOf(var1));
         }
      }

      public FloatList subList(int var1, int var2) {
         if (var1 == 0 && var2 == 0) {
            return this;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public void getElements(int var1, float[] var2, int var3, int var4) {
         if (var1 != 0 || var4 != 0 || var3 < 0 || var3 > var2.length) {
            throw new IndexOutOfBoundsException();
         }
      }

      public void removeElements(int var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, float[] var2, int var3, int var4) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, float[] var2) {
         throw new UnsupportedOperationException();
      }

      public void size(int var1) {
         throw new UnsupportedOperationException();
      }

      public int compareTo(List<? extends Float> var1) {
         if (var1 == this) {
            return 0;
         } else {
            return var1.isEmpty() ? 0 : -1;
         }
      }

      public Object clone() {
         return FloatLists.EMPTY_LIST;
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
         return FloatLists.EMPTY_LIST;
      }
   }
}
