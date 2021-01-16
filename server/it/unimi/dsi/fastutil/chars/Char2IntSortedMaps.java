package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Map.Entry;

public final class Char2IntSortedMaps {
   public static final Char2IntSortedMaps.EmptySortedMap EMPTY_MAP = new Char2IntSortedMaps.EmptySortedMap();

   private Char2IntSortedMaps() {
      super();
   }

   public static Comparator<? super Entry<Character, ?>> entryComparator(CharComparator var0) {
      return (var1, var2) -> {
         return var0.compare((Character)var1.getKey(), (Character)var2.getKey());
      };
   }

   public static ObjectBidirectionalIterator<Char2IntMap.Entry> fastIterator(Char2IntSortedMap var0) {
      ObjectSortedSet var1 = var0.char2IntEntrySet();
      return var1 instanceof Char2IntSortedMap.FastSortedEntrySet ? ((Char2IntSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static ObjectBidirectionalIterable<Char2IntMap.Entry> fastIterable(Char2IntSortedMap var0) {
      ObjectSortedSet var1 = var0.char2IntEntrySet();
      Object var2;
      if (var1 instanceof Char2IntSortedMap.FastSortedEntrySet) {
         Char2IntSortedMap.FastSortedEntrySet var10000 = (Char2IntSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Char2IntSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static Char2IntSortedMap singleton(Character var0, Integer var1) {
      return new Char2IntSortedMaps.Singleton(var0, var1);
   }

   public static Char2IntSortedMap singleton(Character var0, Integer var1, CharComparator var2) {
      return new Char2IntSortedMaps.Singleton(var0, var1, var2);
   }

   public static Char2IntSortedMap singleton(char var0, int var1) {
      return new Char2IntSortedMaps.Singleton(var0, var1);
   }

   public static Char2IntSortedMap singleton(char var0, int var1, CharComparator var2) {
      return new Char2IntSortedMaps.Singleton(var0, var1, var2);
   }

   public static Char2IntSortedMap synchronize(Char2IntSortedMap var0) {
      return new Char2IntSortedMaps.SynchronizedSortedMap(var0);
   }

   public static Char2IntSortedMap synchronize(Char2IntSortedMap var0, Object var1) {
      return new Char2IntSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static Char2IntSortedMap unmodifiable(Char2IntSortedMap var0) {
      return new Char2IntSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap extends Char2IntMaps.UnmodifiableMap implements Char2IntSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2IntSortedMap sortedMap;

      protected UnmodifiableSortedMap(Char2IntSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public CharComparator comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Char2IntMap.Entry> char2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.char2IntEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Character, Integer>> entrySet() {
         return this.char2IntEntrySet();
      }

      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (CharSortedSet)this.keys;
      }

      public Char2IntSortedMap subMap(char var1, char var2) {
         return new Char2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      public Char2IntSortedMap headMap(char var1) {
         return new Char2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Char2IntSortedMap tailMap(char var1) {
         return new Char2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }

      public char firstCharKey() {
         return this.sortedMap.firstCharKey();
      }

      public char lastCharKey() {
         return this.sortedMap.lastCharKey();
      }

      /** @deprecated */
      @Deprecated
      public Character firstKey() {
         return this.sortedMap.firstKey();
      }

      /** @deprecated */
      @Deprecated
      public Character lastKey() {
         return this.sortedMap.lastKey();
      }

      /** @deprecated */
      @Deprecated
      public Char2IntSortedMap subMap(Character var1, Character var2) {
         return new Char2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public Char2IntSortedMap headMap(Character var1) {
         return new Char2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      /** @deprecated */
      @Deprecated
      public Char2IntSortedMap tailMap(Character var1) {
         return new Char2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }
   }

   public static class SynchronizedSortedMap extends Char2IntMaps.SynchronizedMap implements Char2IntSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2IntSortedMap sortedMap;

      protected SynchronizedSortedMap(Char2IntSortedMap var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Char2IntSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public CharComparator comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Char2IntMap.Entry> char2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.char2IntEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Character, Integer>> entrySet() {
         return this.char2IntEntrySet();
      }

      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (CharSortedSet)this.keys;
      }

      public Char2IntSortedMap subMap(char var1, char var2) {
         return new Char2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      public Char2IntSortedMap headMap(char var1) {
         return new Char2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Char2IntSortedMap tailMap(char var1) {
         return new Char2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }

      public char firstCharKey() {
         synchronized(this.sync) {
            return this.sortedMap.firstCharKey();
         }
      }

      public char lastCharKey() {
         synchronized(this.sync) {
            return this.sortedMap.lastCharKey();
         }
      }

      /** @deprecated */
      @Deprecated
      public Character firstKey() {
         synchronized(this.sync) {
            return this.sortedMap.firstKey();
         }
      }

      /** @deprecated */
      @Deprecated
      public Character lastKey() {
         synchronized(this.sync) {
            return this.sortedMap.lastKey();
         }
      }

      /** @deprecated */
      @Deprecated
      public Char2IntSortedMap subMap(Character var1, Character var2) {
         return new Char2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Char2IntSortedMap headMap(Character var1) {
         return new Char2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Char2IntSortedMap tailMap(Character var1) {
         return new Char2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }
   }

   public static class Singleton extends Char2IntMaps.Singleton implements Char2IntSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final CharComparator comparator;

      protected Singleton(char var1, int var2, CharComparator var3) {
         super(var1, var2);
         this.comparator = var3;
      }

      protected Singleton(char var1, int var2) {
         this(var1, var2, (CharComparator)null);
      }

      final int compare(char var1, char var2) {
         return this.comparator == null ? Character.compare(var1, var2) : this.comparator.compare(var1, var2);
      }

      public CharComparator comparator() {
         return this.comparator;
      }

      public ObjectSortedSet<Char2IntMap.Entry> char2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractChar2IntMap.BasicEntry(this.key, this.value), Char2IntSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Character, Integer>> entrySet() {
         return this.char2IntEntrySet();
      }

      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.singleton(this.key, this.comparator);
         }

         return (CharSortedSet)this.keys;
      }

      public Char2IntSortedMap subMap(char var1, char var2) {
         return (Char2IntSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var2) < 0 ? this : Char2IntSortedMaps.EMPTY_MAP);
      }

      public Char2IntSortedMap headMap(char var1) {
         return (Char2IntSortedMap)(this.compare(this.key, var1) < 0 ? this : Char2IntSortedMaps.EMPTY_MAP);
      }

      public Char2IntSortedMap tailMap(char var1) {
         return (Char2IntSortedMap)(this.compare(var1, this.key) <= 0 ? this : Char2IntSortedMaps.EMPTY_MAP);
      }

      public char firstCharKey() {
         return this.key;
      }

      public char lastCharKey() {
         return this.key;
      }

      /** @deprecated */
      @Deprecated
      public Char2IntSortedMap headMap(Character var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Char2IntSortedMap tailMap(Character var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Char2IntSortedMap subMap(Character var1, Character var2) {
         return this.subMap(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public Character firstKey() {
         return this.firstCharKey();
      }

      /** @deprecated */
      @Deprecated
      public Character lastKey() {
         return this.lastCharKey();
      }
   }

   public static class EmptySortedMap extends Char2IntMaps.EmptyMap implements Char2IntSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public CharComparator comparator() {
         return null;
      }

      public ObjectSortedSet<Char2IntMap.Entry> char2IntEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Character, Integer>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public CharSortedSet keySet() {
         return CharSortedSets.EMPTY_SET;
      }

      public Char2IntSortedMap subMap(char var1, char var2) {
         return Char2IntSortedMaps.EMPTY_MAP;
      }

      public Char2IntSortedMap headMap(char var1) {
         return Char2IntSortedMaps.EMPTY_MAP;
      }

      public Char2IntSortedMap tailMap(char var1) {
         return Char2IntSortedMaps.EMPTY_MAP;
      }

      public char firstCharKey() {
         throw new NoSuchElementException();
      }

      public char lastCharKey() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Char2IntSortedMap headMap(Character var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Char2IntSortedMap tailMap(Character var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Char2IntSortedMap subMap(Character var1, Character var2) {
         return this.subMap(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public Character firstKey() {
         return this.firstCharKey();
      }

      /** @deprecated */
      @Deprecated
      public Character lastKey() {
         return this.lastCharKey();
      }
   }
}
