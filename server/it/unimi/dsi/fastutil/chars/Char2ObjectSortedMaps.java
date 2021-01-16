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

public final class Char2ObjectSortedMaps {
   public static final Char2ObjectSortedMaps.EmptySortedMap EMPTY_MAP = new Char2ObjectSortedMaps.EmptySortedMap();

   private Char2ObjectSortedMaps() {
      super();
   }

   public static Comparator<? super Entry<Character, ?>> entryComparator(CharComparator var0) {
      return (var1, var2) -> {
         return var0.compare((Character)var1.getKey(), (Character)var2.getKey());
      };
   }

   public static <V> ObjectBidirectionalIterator<Char2ObjectMap.Entry<V>> fastIterator(Char2ObjectSortedMap<V> var0) {
      ObjectSortedSet var1 = var0.char2ObjectEntrySet();
      return var1 instanceof Char2ObjectSortedMap.FastSortedEntrySet ? ((Char2ObjectSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <V> ObjectBidirectionalIterable<Char2ObjectMap.Entry<V>> fastIterable(Char2ObjectSortedMap<V> var0) {
      ObjectSortedSet var1 = var0.char2ObjectEntrySet();
      Object var2;
      if (var1 instanceof Char2ObjectSortedMap.FastSortedEntrySet) {
         Char2ObjectSortedMap.FastSortedEntrySet var10000 = (Char2ObjectSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Char2ObjectSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static <V> Char2ObjectSortedMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> Char2ObjectSortedMap<V> singleton(Character var0, V var1) {
      return new Char2ObjectSortedMaps.Singleton(var0, var1);
   }

   public static <V> Char2ObjectSortedMap<V> singleton(Character var0, V var1, CharComparator var2) {
      return new Char2ObjectSortedMaps.Singleton(var0, var1, var2);
   }

   public static <V> Char2ObjectSortedMap<V> singleton(char var0, V var1) {
      return new Char2ObjectSortedMaps.Singleton(var0, var1);
   }

   public static <V> Char2ObjectSortedMap<V> singleton(char var0, V var1, CharComparator var2) {
      return new Char2ObjectSortedMaps.Singleton(var0, var1, var2);
   }

   public static <V> Char2ObjectSortedMap<V> synchronize(Char2ObjectSortedMap<V> var0) {
      return new Char2ObjectSortedMaps.SynchronizedSortedMap(var0);
   }

   public static <V> Char2ObjectSortedMap<V> synchronize(Char2ObjectSortedMap<V> var0, Object var1) {
      return new Char2ObjectSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static <V> Char2ObjectSortedMap<V> unmodifiable(Char2ObjectSortedMap<V> var0) {
      return new Char2ObjectSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap<V> extends Char2ObjectMaps.UnmodifiableMap<V> implements Char2ObjectSortedMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2ObjectSortedMap<V> sortedMap;

      protected UnmodifiableSortedMap(Char2ObjectSortedMap<V> var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public CharComparator comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Char2ObjectMap.Entry<V>> char2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.char2ObjectEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Character, V>> entrySet() {
         return this.char2ObjectEntrySet();
      }

      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (CharSortedSet)this.keys;
      }

      public Char2ObjectSortedMap<V> subMap(char var1, char var2) {
         return new Char2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      public Char2ObjectSortedMap<V> headMap(char var1) {
         return new Char2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Char2ObjectSortedMap<V> tailMap(char var1) {
         return new Char2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
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
      public Char2ObjectSortedMap<V> subMap(Character var1, Character var2) {
         return new Char2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public Char2ObjectSortedMap<V> headMap(Character var1) {
         return new Char2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      /** @deprecated */
      @Deprecated
      public Char2ObjectSortedMap<V> tailMap(Character var1) {
         return new Char2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }
   }

   public static class SynchronizedSortedMap<V> extends Char2ObjectMaps.SynchronizedMap<V> implements Char2ObjectSortedMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2ObjectSortedMap<V> sortedMap;

      protected SynchronizedSortedMap(Char2ObjectSortedMap<V> var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Char2ObjectSortedMap<V> var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public CharComparator comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Char2ObjectMap.Entry<V>> char2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.char2ObjectEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Character, V>> entrySet() {
         return this.char2ObjectEntrySet();
      }

      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (CharSortedSet)this.keys;
      }

      public Char2ObjectSortedMap<V> subMap(char var1, char var2) {
         return new Char2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      public Char2ObjectSortedMap<V> headMap(char var1) {
         return new Char2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Char2ObjectSortedMap<V> tailMap(char var1) {
         return new Char2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
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
      public Char2ObjectSortedMap<V> subMap(Character var1, Character var2) {
         return new Char2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Char2ObjectSortedMap<V> headMap(Character var1) {
         return new Char2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Char2ObjectSortedMap<V> tailMap(Character var1) {
         return new Char2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }
   }

   public static class Singleton<V> extends Char2ObjectMaps.Singleton<V> implements Char2ObjectSortedMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final CharComparator comparator;

      protected Singleton(char var1, V var2, CharComparator var3) {
         super(var1, var2);
         this.comparator = var3;
      }

      protected Singleton(char var1, V var2) {
         this(var1, var2, (CharComparator)null);
      }

      final int compare(char var1, char var2) {
         return this.comparator == null ? Character.compare(var1, var2) : this.comparator.compare(var1, var2);
      }

      public CharComparator comparator() {
         return this.comparator;
      }

      public ObjectSortedSet<Char2ObjectMap.Entry<V>> char2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractChar2ObjectMap.BasicEntry(this.key, this.value), Char2ObjectSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Character, V>> entrySet() {
         return this.char2ObjectEntrySet();
      }

      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.singleton(this.key, this.comparator);
         }

         return (CharSortedSet)this.keys;
      }

      public Char2ObjectSortedMap<V> subMap(char var1, char var2) {
         return (Char2ObjectSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var2) < 0 ? this : Char2ObjectSortedMaps.EMPTY_MAP);
      }

      public Char2ObjectSortedMap<V> headMap(char var1) {
         return (Char2ObjectSortedMap)(this.compare(this.key, var1) < 0 ? this : Char2ObjectSortedMaps.EMPTY_MAP);
      }

      public Char2ObjectSortedMap<V> tailMap(char var1) {
         return (Char2ObjectSortedMap)(this.compare(var1, this.key) <= 0 ? this : Char2ObjectSortedMaps.EMPTY_MAP);
      }

      public char firstCharKey() {
         return this.key;
      }

      public char lastCharKey() {
         return this.key;
      }

      /** @deprecated */
      @Deprecated
      public Char2ObjectSortedMap<V> headMap(Character var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Char2ObjectSortedMap<V> tailMap(Character var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Char2ObjectSortedMap<V> subMap(Character var1, Character var2) {
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

   public static class EmptySortedMap<V> extends Char2ObjectMaps.EmptyMap<V> implements Char2ObjectSortedMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public CharComparator comparator() {
         return null;
      }

      public ObjectSortedSet<Char2ObjectMap.Entry<V>> char2ObjectEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Character, V>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public CharSortedSet keySet() {
         return CharSortedSets.EMPTY_SET;
      }

      public Char2ObjectSortedMap<V> subMap(char var1, char var2) {
         return Char2ObjectSortedMaps.EMPTY_MAP;
      }

      public Char2ObjectSortedMap<V> headMap(char var1) {
         return Char2ObjectSortedMaps.EMPTY_MAP;
      }

      public Char2ObjectSortedMap<V> tailMap(char var1) {
         return Char2ObjectSortedMaps.EMPTY_MAP;
      }

      public char firstCharKey() {
         throw new NoSuchElementException();
      }

      public char lastCharKey() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Char2ObjectSortedMap<V> headMap(Character var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Char2ObjectSortedMap<V> tailMap(Character var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Char2ObjectSortedMap<V> subMap(Character var1, Character var2) {
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
