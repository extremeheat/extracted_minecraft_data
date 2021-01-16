package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Map.Entry;

public final class Double2BooleanSortedMaps {
   public static final Double2BooleanSortedMaps.EmptySortedMap EMPTY_MAP = new Double2BooleanSortedMaps.EmptySortedMap();

   private Double2BooleanSortedMaps() {
      super();
   }

   public static Comparator<? super Entry<Double, ?>> entryComparator(DoubleComparator var0) {
      return (var1, var2) -> {
         return var0.compare((Double)var1.getKey(), (Double)var2.getKey());
      };
   }

   public static ObjectBidirectionalIterator<Double2BooleanMap.Entry> fastIterator(Double2BooleanSortedMap var0) {
      ObjectSortedSet var1 = var0.double2BooleanEntrySet();
      return var1 instanceof Double2BooleanSortedMap.FastSortedEntrySet ? ((Double2BooleanSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static ObjectBidirectionalIterable<Double2BooleanMap.Entry> fastIterable(Double2BooleanSortedMap var0) {
      ObjectSortedSet var1 = var0.double2BooleanEntrySet();
      Object var2;
      if (var1 instanceof Double2BooleanSortedMap.FastSortedEntrySet) {
         Double2BooleanSortedMap.FastSortedEntrySet var10000 = (Double2BooleanSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Double2BooleanSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static Double2BooleanSortedMap singleton(Double var0, Boolean var1) {
      return new Double2BooleanSortedMaps.Singleton(var0, var1);
   }

   public static Double2BooleanSortedMap singleton(Double var0, Boolean var1, DoubleComparator var2) {
      return new Double2BooleanSortedMaps.Singleton(var0, var1, var2);
   }

   public static Double2BooleanSortedMap singleton(double var0, boolean var2) {
      return new Double2BooleanSortedMaps.Singleton(var0, var2);
   }

   public static Double2BooleanSortedMap singleton(double var0, boolean var2, DoubleComparator var3) {
      return new Double2BooleanSortedMaps.Singleton(var0, var2, var3);
   }

   public static Double2BooleanSortedMap synchronize(Double2BooleanSortedMap var0) {
      return new Double2BooleanSortedMaps.SynchronizedSortedMap(var0);
   }

   public static Double2BooleanSortedMap synchronize(Double2BooleanSortedMap var0, Object var1) {
      return new Double2BooleanSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static Double2BooleanSortedMap unmodifiable(Double2BooleanSortedMap var0) {
      return new Double2BooleanSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap extends Double2BooleanMaps.UnmodifiableMap implements Double2BooleanSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2BooleanSortedMap sortedMap;

      protected UnmodifiableSortedMap(Double2BooleanSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public DoubleComparator comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Double2BooleanMap.Entry> double2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.double2BooleanEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Double, Boolean>> entrySet() {
         return this.double2BooleanEntrySet();
      }

      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (DoubleSortedSet)this.keys;
      }

      public Double2BooleanSortedMap subMap(double var1, double var3) {
         return new Double2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var3));
      }

      public Double2BooleanSortedMap headMap(double var1) {
         return new Double2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Double2BooleanSortedMap tailMap(double var1) {
         return new Double2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }

      public double firstDoubleKey() {
         return this.sortedMap.firstDoubleKey();
      }

      public double lastDoubleKey() {
         return this.sortedMap.lastDoubleKey();
      }

      /** @deprecated */
      @Deprecated
      public Double firstKey() {
         return this.sortedMap.firstKey();
      }

      /** @deprecated */
      @Deprecated
      public Double lastKey() {
         return this.sortedMap.lastKey();
      }

      /** @deprecated */
      @Deprecated
      public Double2BooleanSortedMap subMap(Double var1, Double var2) {
         return new Double2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public Double2BooleanSortedMap headMap(Double var1) {
         return new Double2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      /** @deprecated */
      @Deprecated
      public Double2BooleanSortedMap tailMap(Double var1) {
         return new Double2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }
   }

   public static class SynchronizedSortedMap extends Double2BooleanMaps.SynchronizedMap implements Double2BooleanSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2BooleanSortedMap sortedMap;

      protected SynchronizedSortedMap(Double2BooleanSortedMap var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Double2BooleanSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public DoubleComparator comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Double2BooleanMap.Entry> double2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.double2BooleanEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Double, Boolean>> entrySet() {
         return this.double2BooleanEntrySet();
      }

      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (DoubleSortedSet)this.keys;
      }

      public Double2BooleanSortedMap subMap(double var1, double var3) {
         return new Double2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var3), this.sync);
      }

      public Double2BooleanSortedMap headMap(double var1) {
         return new Double2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Double2BooleanSortedMap tailMap(double var1) {
         return new Double2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }

      public double firstDoubleKey() {
         synchronized(this.sync) {
            return this.sortedMap.firstDoubleKey();
         }
      }

      public double lastDoubleKey() {
         synchronized(this.sync) {
            return this.sortedMap.lastDoubleKey();
         }
      }

      /** @deprecated */
      @Deprecated
      public Double firstKey() {
         synchronized(this.sync) {
            return this.sortedMap.firstKey();
         }
      }

      /** @deprecated */
      @Deprecated
      public Double lastKey() {
         synchronized(this.sync) {
            return this.sortedMap.lastKey();
         }
      }

      /** @deprecated */
      @Deprecated
      public Double2BooleanSortedMap subMap(Double var1, Double var2) {
         return new Double2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Double2BooleanSortedMap headMap(Double var1) {
         return new Double2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Double2BooleanSortedMap tailMap(Double var1) {
         return new Double2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }
   }

   public static class Singleton extends Double2BooleanMaps.Singleton implements Double2BooleanSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final DoubleComparator comparator;

      protected Singleton(double var1, boolean var3, DoubleComparator var4) {
         super(var1, var3);
         this.comparator = var4;
      }

      protected Singleton(double var1, boolean var3) {
         this(var1, var3, (DoubleComparator)null);
      }

      final int compare(double var1, double var3) {
         return this.comparator == null ? Double.compare(var1, var3) : this.comparator.compare(var1, var3);
      }

      public DoubleComparator comparator() {
         return this.comparator;
      }

      public ObjectSortedSet<Double2BooleanMap.Entry> double2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractDouble2BooleanMap.BasicEntry(this.key, this.value), Double2BooleanSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Double, Boolean>> entrySet() {
         return this.double2BooleanEntrySet();
      }

      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSortedSets.singleton(this.key, this.comparator);
         }

         return (DoubleSortedSet)this.keys;
      }

      public Double2BooleanSortedMap subMap(double var1, double var3) {
         return (Double2BooleanSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var3) < 0 ? this : Double2BooleanSortedMaps.EMPTY_MAP);
      }

      public Double2BooleanSortedMap headMap(double var1) {
         return (Double2BooleanSortedMap)(this.compare(this.key, var1) < 0 ? this : Double2BooleanSortedMaps.EMPTY_MAP);
      }

      public Double2BooleanSortedMap tailMap(double var1) {
         return (Double2BooleanSortedMap)(this.compare(var1, this.key) <= 0 ? this : Double2BooleanSortedMaps.EMPTY_MAP);
      }

      public double firstDoubleKey() {
         return this.key;
      }

      public double lastDoubleKey() {
         return this.key;
      }

      /** @deprecated */
      @Deprecated
      public Double2BooleanSortedMap headMap(Double var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Double2BooleanSortedMap tailMap(Double var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Double2BooleanSortedMap subMap(Double var1, Double var2) {
         return this.subMap(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public Double firstKey() {
         return this.firstDoubleKey();
      }

      /** @deprecated */
      @Deprecated
      public Double lastKey() {
         return this.lastDoubleKey();
      }
   }

   public static class EmptySortedMap extends Double2BooleanMaps.EmptyMap implements Double2BooleanSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public DoubleComparator comparator() {
         return null;
      }

      public ObjectSortedSet<Double2BooleanMap.Entry> double2BooleanEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Double, Boolean>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public DoubleSortedSet keySet() {
         return DoubleSortedSets.EMPTY_SET;
      }

      public Double2BooleanSortedMap subMap(double var1, double var3) {
         return Double2BooleanSortedMaps.EMPTY_MAP;
      }

      public Double2BooleanSortedMap headMap(double var1) {
         return Double2BooleanSortedMaps.EMPTY_MAP;
      }

      public Double2BooleanSortedMap tailMap(double var1) {
         return Double2BooleanSortedMaps.EMPTY_MAP;
      }

      public double firstDoubleKey() {
         throw new NoSuchElementException();
      }

      public double lastDoubleKey() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Double2BooleanSortedMap headMap(Double var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Double2BooleanSortedMap tailMap(Double var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Double2BooleanSortedMap subMap(Double var1, Double var2) {
         return this.subMap(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public Double firstKey() {
         return this.firstDoubleKey();
      }

      /** @deprecated */
      @Deprecated
      public Double lastKey() {
         return this.lastDoubleKey();
      }
   }
}
