package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.BigList;
import java.io.Serializable;
import java.util.Collection;
import java.util.Random;

public final class DoubleBigLists {
   public static final DoubleBigLists.EmptyBigList EMPTY_BIG_LIST = new DoubleBigLists.EmptyBigList();

   private DoubleBigLists() {
      super();
   }

   public static DoubleBigList shuffle(DoubleBigList var0, Random var1) {
      long var2 = var0.size64();

      while(var2-- != 0L) {
         long var4 = (var1.nextLong() & 9223372036854775807L) % (var2 + 1L);
         double var6 = var0.getDouble(var2);
         var0.set(var2, var0.getDouble(var4));
         var0.set(var4, var6);
      }

      return var0;
   }

   public static DoubleBigList singleton(double var0) {
      return new DoubleBigLists.Singleton(var0);
   }

   public static DoubleBigList singleton(Object var0) {
      return new DoubleBigLists.Singleton((Double)var0);
   }

   public static DoubleBigList synchronize(DoubleBigList var0) {
      return new DoubleBigLists.SynchronizedBigList(var0);
   }

   public static DoubleBigList synchronize(DoubleBigList var0, Object var1) {
      return new DoubleBigLists.SynchronizedBigList(var0, var1);
   }

   public static DoubleBigList unmodifiable(DoubleBigList var0) {
      return new DoubleBigLists.UnmodifiableBigList(var0);
   }

   public static DoubleBigList asBigList(DoubleList var0) {
      return new DoubleBigLists.ListBigList(var0);
   }

   public static class ListBigList extends AbstractDoubleBigList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final DoubleList list;

      protected ListBigList(DoubleList var1) {
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

      public DoubleBigListIterator iterator() {
         return DoubleBigListIterators.asBigListIterator(this.list.iterator());
      }

      public DoubleBigListIterator listIterator() {
         return DoubleBigListIterators.asBigListIterator(this.list.listIterator());
      }

      public DoubleBigListIterator listIterator(long var1) {
         return DoubleBigListIterators.asBigListIterator(this.list.listIterator(this.intIndex(var1)));
      }

      public boolean addAll(long var1, Collection<? extends Double> var3) {
         return this.list.addAll(this.intIndex(var1), (Collection)var3);
      }

      public DoubleBigList subList(long var1, long var3) {
         return new DoubleBigLists.ListBigList(this.list.subList(this.intIndex(var1), this.intIndex(var3)));
      }

      public boolean contains(double var1) {
         return this.list.contains(var1);
      }

      public double[] toDoubleArray() {
         return this.list.toDoubleArray();
      }

      public void removeElements(long var1, long var3) {
         this.list.removeElements(this.intIndex(var1), this.intIndex(var3));
      }

      /** @deprecated */
      @Deprecated
      public double[] toDoubleArray(double[] var1) {
         return this.list.toArray(var1);
      }

      public boolean addAll(long var1, DoubleCollection var3) {
         return this.list.addAll(this.intIndex(var1), var3);
      }

      public boolean addAll(DoubleCollection var1) {
         return this.list.addAll(var1);
      }

      public boolean addAll(long var1, DoubleBigList var3) {
         return this.list.addAll(this.intIndex(var1), (DoubleCollection)var3);
      }

      public boolean addAll(DoubleBigList var1) {
         return this.list.addAll(var1);
      }

      public boolean containsAll(DoubleCollection var1) {
         return this.list.containsAll(var1);
      }

      public boolean removeAll(DoubleCollection var1) {
         return this.list.removeAll(var1);
      }

      public boolean retainAll(DoubleCollection var1) {
         return this.list.retainAll(var1);
      }

      public void add(long var1, double var3) {
         this.list.add(this.intIndex(var1), var3);
      }

      public boolean add(double var1) {
         return this.list.add(var1);
      }

      public double getDouble(long var1) {
         return this.list.getDouble(this.intIndex(var1));
      }

      public long indexOf(double var1) {
         return (long)this.list.indexOf(var1);
      }

      public long lastIndexOf(double var1) {
         return (long)this.list.lastIndexOf(var1);
      }

      public double removeDouble(long var1) {
         return this.list.removeDouble(this.intIndex(var1));
      }

      public double set(long var1, double var3) {
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

      public boolean addAll(Collection<? extends Double> var1) {
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

   public static class UnmodifiableBigList extends DoubleCollections.UnmodifiableCollection implements DoubleBigList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final DoubleBigList list;

      protected UnmodifiableBigList(DoubleBigList var1) {
         super(var1);
         this.list = var1;
      }

      public double getDouble(long var1) {
         return this.list.getDouble(var1);
      }

      public double set(long var1, double var3) {
         throw new UnsupportedOperationException();
      }

      public void add(long var1, double var3) {
         throw new UnsupportedOperationException();
      }

      public double removeDouble(long var1) {
         throw new UnsupportedOperationException();
      }

      public long indexOf(double var1) {
         return this.list.indexOf(var1);
      }

      public long lastIndexOf(double var1) {
         return this.list.lastIndexOf(var1);
      }

      public boolean addAll(long var1, Collection<? extends Double> var3) {
         throw new UnsupportedOperationException();
      }

      public void getElements(long var1, double[][] var3, long var4, long var6) {
         this.list.getElements(var1, var3, var4, var6);
      }

      public void removeElements(long var1, long var3) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, double[][] var3, long var4, long var6) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, double[][] var3) {
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

      public DoubleBigListIterator iterator() {
         return this.listIterator();
      }

      public DoubleBigListIterator listIterator() {
         return DoubleBigListIterators.unmodifiable(this.list.listIterator());
      }

      public DoubleBigListIterator listIterator(long var1) {
         return DoubleBigListIterators.unmodifiable(this.list.listIterator(var1));
      }

      public DoubleBigList subList(long var1, long var3) {
         return DoubleBigLists.unmodifiable(this.list.subList(var1, var3));
      }

      public boolean equals(Object var1) {
         return var1 == this ? true : this.list.equals(var1);
      }

      public int hashCode() {
         return this.list.hashCode();
      }

      public int compareTo(BigList<? extends Double> var1) {
         return this.list.compareTo(var1);
      }

      public boolean addAll(long var1, DoubleCollection var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(DoubleBigList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, DoubleBigList var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double get(long var1) {
         return this.list.get(var1);
      }

      /** @deprecated */
      @Deprecated
      public void add(long var1, Double var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double set(long var1, Double var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double remove(long var1) {
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

   public static class SynchronizedBigList extends DoubleCollections.SynchronizedCollection implements DoubleBigList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final DoubleBigList list;

      protected SynchronizedBigList(DoubleBigList var1, Object var2) {
         super(var1, var2);
         this.list = var1;
      }

      protected SynchronizedBigList(DoubleBigList var1) {
         super(var1);
         this.list = var1;
      }

      public double getDouble(long var1) {
         synchronized(this.sync) {
            return this.list.getDouble(var1);
         }
      }

      public double set(long var1, double var3) {
         synchronized(this.sync) {
            return this.list.set(var1, var3);
         }
      }

      public void add(long var1, double var3) {
         synchronized(this.sync) {
            this.list.add(var1, var3);
         }
      }

      public double removeDouble(long var1) {
         synchronized(this.sync) {
            return this.list.removeDouble(var1);
         }
      }

      public long indexOf(double var1) {
         synchronized(this.sync) {
            return this.list.indexOf(var1);
         }
      }

      public long lastIndexOf(double var1) {
         synchronized(this.sync) {
            return this.list.lastIndexOf(var1);
         }
      }

      public boolean addAll(long var1, Collection<? extends Double> var3) {
         synchronized(this.sync) {
            return this.list.addAll(var1, (Collection)var3);
         }
      }

      public void getElements(long var1, double[][] var3, long var4, long var6) {
         synchronized(this.sync) {
            this.list.getElements(var1, var3, var4, var6);
         }
      }

      public void removeElements(long var1, long var3) {
         synchronized(this.sync) {
            this.list.removeElements(var1, var3);
         }
      }

      public void addElements(long var1, double[][] var3, long var4, long var6) {
         synchronized(this.sync) {
            this.list.addElements(var1, var3, var4, var6);
         }
      }

      public void addElements(long var1, double[][] var3) {
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

      public DoubleBigListIterator iterator() {
         return this.list.listIterator();
      }

      public DoubleBigListIterator listIterator() {
         return this.list.listIterator();
      }

      public DoubleBigListIterator listIterator(long var1) {
         return this.list.listIterator(var1);
      }

      public DoubleBigList subList(long var1, long var3) {
         synchronized(this.sync) {
            return DoubleBigLists.synchronize(this.list.subList(var1, var3), this.sync);
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

      public int compareTo(BigList<? extends Double> var1) {
         synchronized(this.sync) {
            return this.list.compareTo(var1);
         }
      }

      public boolean addAll(long var1, DoubleCollection var3) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var3);
         }
      }

      public boolean addAll(long var1, DoubleBigList var3) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var3);
         }
      }

      public boolean addAll(DoubleBigList var1) {
         synchronized(this.sync) {
            return this.list.addAll(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public void add(long var1, Double var3) {
         synchronized(this.sync) {
            this.list.add(var1, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double get(long var1) {
         synchronized(this.sync) {
            return this.list.get(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double set(long var1, Double var3) {
         synchronized(this.sync) {
            return this.list.set(var1, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double remove(long var1) {
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

   public static class Singleton extends AbstractDoubleBigList implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final double element;

      protected Singleton(double var1) {
         super();
         this.element = var1;
      }

      public double getDouble(long var1) {
         if (var1 == 0L) {
            return this.element;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public boolean rem(double var1) {
         throw new UnsupportedOperationException();
      }

      public double removeDouble(long var1) {
         throw new UnsupportedOperationException();
      }

      public boolean contains(double var1) {
         return Double.doubleToLongBits(var1) == Double.doubleToLongBits(this.element);
      }

      public double[] toDoubleArray() {
         double[] var1 = new double[]{this.element};
         return var1;
      }

      public DoubleBigListIterator listIterator() {
         return DoubleBigListIterators.singleton(this.element);
      }

      public DoubleBigListIterator listIterator(long var1) {
         if (var1 <= 1L && var1 >= 0L) {
            DoubleBigListIterator var3 = this.listIterator();
            if (var1 == 1L) {
               var3.nextDouble();
            }

            return var3;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public DoubleBigList subList(long var1, long var3) {
         this.ensureIndex(var1);
         this.ensureIndex(var3);
         if (var1 > var3) {
            throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
         } else {
            return (DoubleBigList)(var1 == 0L && var3 == 1L ? this : DoubleBigLists.EMPTY_BIG_LIST);
         }
      }

      public boolean addAll(long var1, Collection<? extends Double> var3) {
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

      public boolean addAll(DoubleBigList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, DoubleBigList var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, DoubleCollection var3) {
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

   public static class EmptyBigList extends DoubleCollections.EmptyCollection implements DoubleBigList, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyBigList() {
         super();
      }

      public double getDouble(long var1) {
         throw new IndexOutOfBoundsException();
      }

      public boolean rem(double var1) {
         throw new UnsupportedOperationException();
      }

      public double removeDouble(long var1) {
         throw new UnsupportedOperationException();
      }

      public void add(long var1, double var3) {
         throw new UnsupportedOperationException();
      }

      public double set(long var1, double var3) {
         throw new UnsupportedOperationException();
      }

      public long indexOf(double var1) {
         return -1L;
      }

      public long lastIndexOf(double var1) {
         return -1L;
      }

      public boolean addAll(long var1, Collection<? extends Double> var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(DoubleCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(DoubleBigList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, DoubleCollection var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, DoubleBigList var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public void add(long var1, Double var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean add(Double var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double get(long var1) {
         throw new IndexOutOfBoundsException();
      }

      /** @deprecated */
      @Deprecated
      public Double set(long var1, Double var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double remove(long var1) {
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

      public DoubleBigListIterator listIterator() {
         return DoubleBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      public DoubleBigListIterator iterator() {
         return DoubleBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      public DoubleBigListIterator listIterator(long var1) {
         if (var1 == 0L) {
            return DoubleBigListIterators.EMPTY_BIG_LIST_ITERATOR;
         } else {
            throw new IndexOutOfBoundsException(String.valueOf(var1));
         }
      }

      public DoubleBigList subList(long var1, long var3) {
         if (var1 == 0L && var3 == 0L) {
            return this;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public void getElements(long var1, double[][] var3, long var4, long var6) {
         DoubleBigArrays.ensureOffsetLength(var3, var4, var6);
         if (var1 != 0L) {
            throw new IndexOutOfBoundsException();
         }
      }

      public void removeElements(long var1, long var3) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, double[][] var3, long var4, long var6) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, double[][] var3) {
         throw new UnsupportedOperationException();
      }

      public void size(long var1) {
         throw new UnsupportedOperationException();
      }

      public long size64() {
         return 0L;
      }

      public int compareTo(BigList<? extends Double> var1) {
         if (var1 == this) {
            return 0;
         } else {
            return var1.isEmpty() ? 0 : -1;
         }
      }

      public Object clone() {
         return DoubleBigLists.EMPTY_BIG_LIST;
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
         return DoubleBigLists.EMPTY_BIG_LIST;
      }
   }
}
