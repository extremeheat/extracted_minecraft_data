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

public final class Char2BooleanSortedMaps {
   public static final Char2BooleanSortedMaps.EmptySortedMap EMPTY_MAP = new Char2BooleanSortedMaps.EmptySortedMap();

   private Char2BooleanSortedMaps() {
      super();
   }

   public static Comparator<? super Entry<Character, ?>> entryComparator(CharComparator var0) {
      return (var1, var2) -> {
         return var0.compare((Character)var1.getKey(), (Character)var2.getKey());
      };
   }

   public static ObjectBidirectionalIterator<Char2BooleanMap.Entry> fastIterator(Char2BooleanSortedMap var0) {
      ObjectSortedSet var1 = var0.char2BooleanEntrySet();
      return var1 instanceof Char2BooleanSortedMap.FastSortedEntrySet ? ((Char2BooleanSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static ObjectBidirectionalIterable<Char2BooleanMap.Entry> fastIterable(Char2BooleanSortedMap var0) {
      ObjectSortedSet var1 = var0.char2BooleanEntrySet();
      Object var2;
      if (var1 instanceof Char2BooleanSortedMap.FastSortedEntrySet) {
         Char2BooleanSortedMap.FastSortedEntrySet var10000 = (Char2BooleanSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Char2BooleanSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static Char2BooleanSortedMap singleton(Character var0, Boolean var1) {
      return new Char2BooleanSortedMaps.Singleton(var0, var1);
   }

   public static Char2BooleanSortedMap singleton(Character var0, Boolean var1, CharComparator var2) {
      return new Char2BooleanSortedMaps.Singleton(var0, var1, var2);
   }

   public static Char2BooleanSortedMap singleton(char var0, boolean var1) {
      return new Char2BooleanSortedMaps.Singleton(var0, var1);
   }

   public static Char2BooleanSortedMap singleton(char var0, boolean var1, CharComparator var2) {
      return new Char2BooleanSortedMaps.Singleton(var0, var1, var2);
   }

   public static Char2BooleanSortedMap synchronize(Char2BooleanSortedMap var0) {
      return new Char2BooleanSortedMaps.SynchronizedSortedMap(var0);
   }

   public static Char2BooleanSortedMap synchronize(Char2BooleanSortedMap var0, Object var1) {
      return new Char2BooleanSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static Char2BooleanSortedMap unmodifiable(Char2BooleanSortedMap var0) {
      return new Char2BooleanSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap extends Char2BooleanMaps.UnmodifiableMap implements Char2BooleanSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2BooleanSortedMap sortedMap;

      protected UnmodifiableSortedMap(Char2BooleanSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public CharComparator comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Char2BooleanMap.Entry> char2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.char2BooleanEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Character, Boolean>> entrySet() {
         return this.char2BooleanEntrySet();
      }

      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (CharSortedSet)this.keys;
      }

      public Char2BooleanSortedMap subMap(char var1, char var2) {
         return new Char2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      public Char2BooleanSortedMap headMap(char var1) {
         return new Char2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Char2BooleanSortedMap tailMap(char var1) {
         return new Char2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
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
      public Char2BooleanSortedMap subMap(Character var1, Character var2) {
         return new Char2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public Char2BooleanSortedMap headMap(Character var1) {
         return new Char2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      /** @deprecated */
      @Deprecated
      public Char2BooleanSortedMap tailMap(Character var1) {
         return new Char2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }
   }

   public static class SynchronizedSortedMap extends Char2BooleanMaps.SynchronizedMap implements Char2BooleanSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2BooleanSortedMap sortedMap;

      protected SynchronizedSortedMap(Char2BooleanSortedMap var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Char2BooleanSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public CharComparator comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Char2BooleanMap.Entry> char2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.char2BooleanEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Character, Boolean>> entrySet() {
         return this.char2BooleanEntrySet();
      }

      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (CharSortedSet)this.keys;
      }

      public Char2BooleanSortedMap subMap(char var1, char var2) {
         return new Char2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      public Char2BooleanSortedMap headMap(char var1) {
         return new Char2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Char2BooleanSortedMap tailMap(char var1) {
         return new Char2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
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
      public Char2BooleanSortedMap subMap(Character var1, Character var2) {
         return new Char2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Char2BooleanSortedMap headMap(Character var1) {
         return new Char2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Char2BooleanSortedMap tailMap(Character var1) {
         return new Char2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }
   }

   public static class Singleton extends Char2BooleanMaps.Singleton implements Char2BooleanSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final CharComparator comparator;

      protected Singleton(char var1, boolean var2, CharComparator var3) {
         super(var1, var2);
         this.comparator = var3;
      }

      protected Singleton(char var1, boolean var2) {
         this(var1, var2, (CharComparator)null);
      }

      final int compare(char var1, char var2) {
         return this.comparator == null ? Character.compare(var1, var2) : this.comparator.compare(var1, var2);
      }

      public CharComparator comparator() {
         return this.comparator;
      }

      public ObjectSortedSet<Char2BooleanMap.Entry> char2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractChar2BooleanMap.BasicEntry(this.key, this.value), Char2BooleanSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Character, Boolean>> entrySet() {
         return this.char2BooleanEntrySet();
      }

      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.singleton(this.key, this.comparator);
         }

         return (CharSortedSet)this.keys;
      }

      public Char2BooleanSortedMap subMap(char var1, char var2) {
         return (Char2BooleanSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var2) < 0 ? this : Char2BooleanSortedMaps.EMPTY_MAP);
      }

      public Char2BooleanSortedMap headMap(char var1) {
         return (Char2BooleanSortedMap)(this.compare(this.key, var1) < 0 ? this : Char2BooleanSortedMaps.EMPTY_MAP);
      }

      public Char2BooleanSortedMap tailMap(char var1) {
         return (Char2BooleanSortedMap)(this.compare(var1, this.key) <= 0 ? this : Char2BooleanSortedMaps.EMPTY_MAP);
      }

      public char firstCharKey() {
         return this.key;
      }

      public char lastCharKey() {
         return this.key;
      }

      /** @deprecated */
      @Deprecated
      public Char2BooleanSortedMap headMap(Character var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Char2BooleanSortedMap tailMap(Character var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Char2BooleanSortedMap subMap(Character var1, Character var2) {
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

   public static class EmptySortedMap extends Char2BooleanMaps.EmptyMap implements Char2BooleanSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public CharComparator comparator() {
         return null;
      }

      public ObjectSortedSet<Char2BooleanMap.Entry> char2BooleanEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Character, Boolean>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public CharSortedSet keySet() {
         return CharSortedSets.EMPTY_SET;
      }

      public Char2BooleanSortedMap subMap(char var1, char var2) {
         return Char2BooleanSortedMaps.EMPTY_MAP;
      }

      public Char2BooleanSortedMap headMap(char var1) {
         return Char2BooleanSortedMaps.EMPTY_MAP;
      }

      public Char2BooleanSortedMap tailMap(char var1) {
         return Char2BooleanSortedMaps.EMPTY_MAP;
      }

      public char firstCharKey() {
         throw new NoSuchElementException();
      }

      public char lastCharKey() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Char2BooleanSortedMap headMap(Character var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Char2BooleanSortedMap tailMap(Character var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Char2BooleanSortedMap subMap(Character var1, Character var2) {
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
