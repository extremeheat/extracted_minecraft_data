package it.unimi.dsi.fastutil.doubles;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.RandomAccess;

public final class DoubleLists {
   public static final DoubleLists.EmptyList EMPTY_LIST = new DoubleLists.EmptyList();

   private DoubleLists() {
      super();
   }

   public static DoubleList shuffle(DoubleList var0, Random var1) {
      int var2 = var0.size();

      while(var2-- != 0) {
         int var3 = var1.nextInt(var2 + 1);
         double var4 = var0.getDouble(var2);
         var0.set(var2, var0.getDouble(var3));
         var0.set(var3, var4);
      }

      return var0;
   }

   public static DoubleList singleton(double var0) {
      return new DoubleLists.Singleton(var0);
   }

   public static DoubleList singleton(Object var0) {
      return new DoubleLists.Singleton((Double)var0);
   }

   public static DoubleList synchronize(DoubleList var0) {
      return (DoubleList)(var0 instanceof RandomAccess ? new DoubleLists.SynchronizedRandomAccessList(var0) : new DoubleLists.SynchronizedList(var0));
   }

   public static DoubleList synchronize(DoubleList var0, Object var1) {
      return (DoubleList)(var0 instanceof RandomAccess ? new DoubleLists.SynchronizedRandomAccessList(var0, var1) : new DoubleLists.SynchronizedList(var0, var1));
   }

   public static DoubleList unmodifiable(DoubleList var0) {
      return (DoubleList)(var0 instanceof RandomAccess ? new DoubleLists.UnmodifiableRandomAccessList(var0) : new DoubleLists.UnmodifiableList(var0));
   }

   public static class UnmodifiableRandomAccessList extends DoubleLists.UnmodifiableList implements RandomAccess, Serializable {
      private static final long serialVersionUID = 0L;

      protected UnmodifiableRandomAccessList(DoubleList var1) {
         super(var1);
      }

      public DoubleList subList(int var1, int var2) {
         return new DoubleLists.UnmodifiableRandomAccessList(this.list.subList(var1, var2));
      }
   }

   public static class UnmodifiableList extends DoubleCollections.UnmodifiableCollection implements DoubleList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final DoubleList list;

      protected UnmodifiableList(DoubleList var1) {
         super(var1);
         this.list = var1;
      }

      public double getDouble(int var1) {
         return this.list.getDouble(var1);
      }

      public double set(int var1, double var2) {
         throw new UnsupportedOperationException();
      }

      public void add(int var1, double var2) {
         throw new UnsupportedOperationException();
      }

      public double removeDouble(int var1) {
         throw new UnsupportedOperationException();
      }

      public int indexOf(double var1) {
         return this.list.indexOf(var1);
      }

      public int lastIndexOf(double var1) {
         return this.list.lastIndexOf(var1);
      }

      public boolean addAll(int var1, Collection<? extends Double> var2) {
         throw new UnsupportedOperationException();
      }

      public void getElements(int var1, double[] var2, int var3, int var4) {
         this.list.getElements(var1, var2, var3, var4);
      }

      public void removeElements(int var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, double[] var2, int var3, int var4) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, double[] var2) {
         throw new UnsupportedOperationException();
      }

      public void size(int var1) {
         this.list.size(var1);
      }

      public DoubleListIterator listIterator() {
         return DoubleIterators.unmodifiable(this.list.listIterator());
      }

      public DoubleListIterator iterator() {
         return this.listIterator();
      }

      public DoubleListIterator listIterator(int var1) {
         return DoubleIterators.unmodifiable(this.list.listIterator(var1));
      }

      public DoubleList subList(int var1, int var2) {
         return new DoubleLists.UnmodifiableList(this.list.subList(var1, var2));
      }

      public boolean equals(Object var1) {
         return var1 == this ? true : this.collection.equals(var1);
      }

      public int hashCode() {
         return this.collection.hashCode();
      }

      public int compareTo(List<? extends Double> var1) {
         return this.list.compareTo(var1);
      }

      public boolean addAll(int var1, DoubleCollection var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(DoubleList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, DoubleList var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double get(int var1) {
         return this.list.get(var1);
      }

      /** @deprecated */
      @Deprecated
      public void add(int var1, Double var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double set(int var1, Double var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double remove(int var1) {
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

   public static class SynchronizedRandomAccessList extends DoubleLists.SynchronizedList implements RandomAccess, Serializable {
      private static final long serialVersionUID = 0L;

      protected SynchronizedRandomAccessList(DoubleList var1, Object var2) {
         super(var1, var2);
      }

      protected SynchronizedRandomAccessList(DoubleList var1) {
         super(var1);
      }

      public DoubleList subList(int var1, int var2) {
         synchronized(this.sync) {
            return new DoubleLists.SynchronizedRandomAccessList(this.list.subList(var1, var2), this.sync);
         }
      }
   }

   public static class SynchronizedList extends DoubleCollections.SynchronizedCollection implements DoubleList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final DoubleList list;

      protected SynchronizedList(DoubleList var1, Object var2) {
         super(var1, var2);
         this.list = var1;
      }

      protected SynchronizedList(DoubleList var1) {
         super(var1);
         this.list = var1;
      }

      public double getDouble(int var1) {
         synchronized(this.sync) {
            return this.list.getDouble(var1);
         }
      }

      public double set(int var1, double var2) {
         synchronized(this.sync) {
            return this.list.set(var1, var2);
         }
      }

      public void add(int var1, double var2) {
         synchronized(this.sync) {
            this.list.add(var1, var2);
         }
      }

      public double removeDouble(int var1) {
         synchronized(this.sync) {
            return this.list.removeDouble(var1);
         }
      }

      public int indexOf(double var1) {
         synchronized(this.sync) {
            return this.list.indexOf(var1);
         }
      }

      public int lastIndexOf(double var1) {
         synchronized(this.sync) {
            return this.list.lastIndexOf(var1);
         }
      }

      public boolean addAll(int var1, Collection<? extends Double> var2) {
         synchronized(this.sync) {
            return this.list.addAll(var1, (Collection)var2);
         }
      }

      public void getElements(int var1, double[] var2, int var3, int var4) {
         synchronized(this.sync) {
            this.list.getElements(var1, var2, var3, var4);
         }
      }

      public void removeElements(int var1, int var2) {
         synchronized(this.sync) {
            this.list.removeElements(var1, var2);
         }
      }

      public void addElements(int var1, double[] var2, int var3, int var4) {
         synchronized(this.sync) {
            this.list.addElements(var1, var2, var3, var4);
         }
      }

      public void addElements(int var1, double[] var2) {
         synchronized(this.sync) {
            this.list.addElements(var1, var2);
         }
      }

      public void size(int var1) {
         synchronized(this.sync) {
            this.list.size(var1);
         }
      }

      public DoubleListIterator listIterator() {
         return this.list.listIterator();
      }

      public DoubleListIterator iterator() {
         return this.listIterator();
      }

      public DoubleListIterator listIterator(int var1) {
         return this.list.listIterator(var1);
      }

      public DoubleList subList(int var1, int var2) {
         synchronized(this.sync) {
            return new DoubleLists.SynchronizedList(this.list.subList(var1, var2), this.sync);
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

      public int compareTo(List<? extends Double> var1) {
         synchronized(this.sync) {
            return this.list.compareTo(var1);
         }
      }

      public boolean addAll(int var1, DoubleCollection var2) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var2);
         }
      }

      public boolean addAll(int var1, DoubleList var2) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var2);
         }
      }

      public boolean addAll(DoubleList var1) {
         synchronized(this.sync) {
            return this.list.addAll(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double get(int var1) {
         synchronized(this.sync) {
            return this.list.get(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public void add(int var1, Double var2) {
         synchronized(this.sync) {
            this.list.add(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double set(int var1, Double var2) {
         synchronized(this.sync) {
            return this.list.set(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double remove(int var1) {
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

   public static class Singleton extends AbstractDoubleList implements RandomAccess, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final double element;

      protected Singleton(double var1) {
         super();
         this.element = var1;
      }

      public double getDouble(int var1) {
         if (var1 == 0) {
            return this.element;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public boolean rem(double var1) {
         throw new UnsupportedOperationException();
      }

      public double removeDouble(int var1) {
         throw new UnsupportedOperationException();
      }

      public boolean contains(double var1) {
         return Double.doubleToLongBits(var1) == Double.doubleToLongBits(this.element);
      }

      public double[] toDoubleArray() {
         double[] var1 = new double[]{this.element};
         return var1;
      }

      public DoubleListIterator listIterator() {
         return DoubleIterators.singleton(this.element);
      }

      public DoubleListIterator iterator() {
         return this.listIterator();
      }

      public DoubleListIterator listIterator(int var1) {
         if (var1 <= 1 && var1 >= 0) {
            DoubleListIterator var2 = this.listIterator();
            if (var1 == 1) {
               var2.nextDouble();
            }

            return var2;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public DoubleList subList(int var1, int var2) {
         this.ensureIndex(var1);
         this.ensureIndex(var2);
         if (var1 > var2) {
            throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var2 + ")");
         } else {
            return (DoubleList)(var1 == 0 && var2 == 1 ? this : DoubleLists.EMPTY_LIST);
         }
      }

      public boolean addAll(int var1, Collection<? extends Double> var2) {
         throw new UnsupportedOperationException();
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

      public boolean addAll(DoubleList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, DoubleList var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, DoubleCollection var2) {
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

   public static class EmptyList extends DoubleCollections.EmptyCollection implements DoubleList, RandomAccess, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyList() {
         super();
      }

      public double getDouble(int var1) {
         throw new IndexOutOfBoundsException();
      }

      public boolean rem(double var1) {
         throw new UnsupportedOperationException();
      }

      public double removeDouble(int var1) {
         throw new UnsupportedOperationException();
      }

      public void add(int var1, double var2) {
         throw new UnsupportedOperationException();
      }

      public double set(int var1, double var2) {
         throw new UnsupportedOperationException();
      }

      public int indexOf(double var1) {
         return -1;
      }

      public int lastIndexOf(double var1) {
         return -1;
      }

      public boolean addAll(int var1, Collection<? extends Double> var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(DoubleList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, DoubleCollection var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, DoubleList var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public void add(int var1, Double var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double get(int var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean add(Double var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double set(int var1, Double var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double remove(int var1) {
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

      public DoubleListIterator listIterator() {
         return DoubleIterators.EMPTY_ITERATOR;
      }

      public DoubleListIterator iterator() {
         return DoubleIterators.EMPTY_ITERATOR;
      }

      public DoubleListIterator listIterator(int var1) {
         if (var1 == 0) {
            return DoubleIterators.EMPTY_ITERATOR;
         } else {
            throw new IndexOutOfBoundsException(String.valueOf(var1));
         }
      }

      public DoubleList subList(int var1, int var2) {
         if (var1 == 0 && var2 == 0) {
            return this;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public void getElements(int var1, double[] var2, int var3, int var4) {
         if (var1 != 0 || var4 != 0 || var3 < 0 || var3 > var2.length) {
            throw new IndexOutOfBoundsException();
         }
      }

      public void removeElements(int var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, double[] var2, int var3, int var4) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, double[] var2) {
         throw new UnsupportedOperationException();
      }

      public void size(int var1) {
         throw new UnsupportedOperationException();
      }

      public int compareTo(List<? extends Double> var1) {
         if (var1 == this) {
            return 0;
         } else {
            return var1.isEmpty() ? 0 : -1;
         }
      }

      public Object clone() {
         return DoubleLists.EMPTY_LIST;
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
         return DoubleLists.EMPTY_LIST;
      }
   }
}
