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

public final class Char2FloatSortedMaps {
   public static final Char2FloatSortedMaps.EmptySortedMap EMPTY_MAP = new Char2FloatSortedMaps.EmptySortedMap();

   private Char2FloatSortedMaps() {
      super();
   }

   public static Comparator<? super Entry<Character, ?>> entryComparator(CharComparator var0) {
      return (var1, var2) -> {
         return var0.compare((Character)var1.getKey(), (Character)var2.getKey());
      };
   }

   public static ObjectBidirectionalIterator<Char2FloatMap.Entry> fastIterator(Char2FloatSortedMap var0) {
      ObjectSortedSet var1 = var0.char2FloatEntrySet();
      return var1 instanceof Char2FloatSortedMap.FastSortedEntrySet ? ((Char2FloatSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static ObjectBidirectionalIterable<Char2FloatMap.Entry> fastIterable(Char2FloatSortedMap var0) {
      ObjectSortedSet var1 = var0.char2FloatEntrySet();
      Object var2;
      if (var1 instanceof Char2FloatSortedMap.FastSortedEntrySet) {
         Char2FloatSortedMap.FastSortedEntrySet var10000 = (Char2FloatSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Char2FloatSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static Char2FloatSortedMap singleton(Character var0, Float var1) {
      return new Char2FloatSortedMaps.Singleton(var0, var1);
   }

   public static Char2FloatSortedMap singleton(Character var0, Float var1, CharComparator var2) {
      return new Char2FloatSortedMaps.Singleton(var0, var1, var2);
   }

   public static Char2FloatSortedMap singleton(char var0, float var1) {
      return new Char2FloatSortedMaps.Singleton(var0, var1);
   }

   public static Char2FloatSortedMap singleton(char var0, float var1, CharComparator var2) {
      return new Char2FloatSortedMaps.Singleton(var0, var1, var2);
   }

   public static Char2FloatSortedMap synchronize(Char2FloatSortedMap var0) {
      return new Char2FloatSortedMaps.SynchronizedSortedMap(var0);
   }

   public static Char2FloatSortedMap synchronize(Char2FloatSortedMap var0, Object var1) {
      return new Char2FloatSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static Char2FloatSortedMap unmodifiable(Char2FloatSortedMap var0) {
      return new Char2FloatSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap extends Char2FloatMaps.UnmodifiableMap implements Char2FloatSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2FloatSortedMap sortedMap;

      protected UnmodifiableSortedMap(Char2FloatSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public CharComparator comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Char2FloatMap.Entry> char2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.char2FloatEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Character, Float>> entrySet() {
         return this.char2FloatEntrySet();
      }

      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (CharSortedSet)this.keys;
      }

      public Char2FloatSortedMap subMap(char var1, char var2) {
         return new Char2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      public Char2FloatSortedMap headMap(char var1) {
         return new Char2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Char2FloatSortedMap tailMap(char var1) {
         return new Char2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
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
      public Char2FloatSortedMap subMap(Character var1, Character var2) {
         return new Char2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public Char2FloatSortedMap headMap(Character var1) {
         return new Char2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      /** @deprecated */
      @Deprecated
      public Char2FloatSortedMap tailMap(Character var1) {
         return new Char2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }
   }

   public static class SynchronizedSortedMap extends Char2FloatMaps.SynchronizedMap implements Char2FloatSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2FloatSortedMap sortedMap;

      protected SynchronizedSortedMap(Char2FloatSortedMap var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Char2FloatSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public CharComparator comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Char2FloatMap.Entry> char2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.char2FloatEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Character, Float>> entrySet() {
         return this.char2FloatEntrySet();
      }

      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (CharSortedSet)this.keys;
      }

      public Char2FloatSortedMap subMap(char var1, char var2) {
         return new Char2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      public Char2FloatSortedMap headMap(char var1) {
         return new Char2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Char2FloatSortedMap tailMap(char var1) {
         return new Char2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
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
      public Char2FloatSortedMap subMap(Character var1, Character var2) {
         return new Char2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Char2FloatSortedMap headMap(Character var1) {
         return new Char2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Char2FloatSortedMap tailMap(Character var1) {
         return new Char2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }
   }

   public static class Singleton extends Char2FloatMaps.Singleton implements Char2FloatSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final CharComparator comparator;

      protected Singleton(char var1, float var2, CharComparator var3) {
         super(var1, var2);
         this.comparator = var3;
      }

      protected Singleton(char var1, float var2) {
         this(var1, var2, (CharComparator)null);
      }

      final int compare(char var1, char var2) {
         return this.comparator == null ? Character.compare(var1, var2) : this.comparator.compare(var1, var2);
      }

      public CharComparator comparator() {
         return this.comparator;
      }

      public ObjectSortedSet<Char2FloatMap.Entry> char2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractChar2FloatMap.BasicEntry(this.key, this.value), Char2FloatSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Character, Float>> entrySet() {
         return this.char2FloatEntrySet();
      }

      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.singleton(this.key, this.comparator);
         }

         return (CharSortedSet)this.keys;
      }

      public Char2FloatSortedMap subMap(char var1, char var2) {
         return (Char2FloatSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var2) < 0 ? this : Char2FloatSortedMaps.EMPTY_MAP);
      }

      public Char2FloatSortedMap headMap(char var1) {
         return (Char2FloatSortedMap)(this.compare(this.key, var1) < 0 ? this : Char2FloatSortedMaps.EMPTY_MAP);
      }

      public Char2FloatSortedMap tailMap(char var1) {
         return (Char2FloatSortedMap)(this.compare(var1, this.key) <= 0 ? this : Char2FloatSortedMaps.EMPTY_MAP);
      }

      public char firstCharKey() {
         return this.key;
      }

      public char lastCharKey() {
         return this.key;
      }

      /** @deprecated */
      @Deprecated
      public Char2FloatSortedMap headMap(Character var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Char2FloatSortedMap tailMap(Character var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Char2FloatSortedMap subMap(Character var1, Character var2) {
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

   public static class EmptySortedMap extends Char2FloatMaps.EmptyMap implements Char2FloatSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public CharComparator comparator() {
         return null;
      }

      public ObjectSortedSet<Char2FloatMap.Entry> char2FloatEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Character, Float>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public CharSortedSet keySet() {
         return CharSortedSets.EMPTY_SET;
      }

      public Char2FloatSortedMap subMap(char var1, char var2) {
         return Char2FloatSortedMaps.EMPTY_MAP;
      }

      public Char2FloatSortedMap headMap(char var1) {
         return Char2FloatSortedMaps.EMPTY_MAP;
      }

      public Char2FloatSortedMap tailMap(char var1) {
         return Char2FloatSortedMaps.EMPTY_MAP;
      }

      public char firstCharKey() {
         throw new NoSuchElementException();
      }

      public char lastCharKey() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Char2FloatSortedMap headMap(Character var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Char2FloatSortedMap tailMap(Character var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Char2FloatSortedMap subMap(Character var1, Character var2) {
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
