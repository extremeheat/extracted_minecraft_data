package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollections;
import it.unimi.dsi.fastutil.booleans.BooleanSets;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;

public final class Char2BooleanMaps {
   public static final Char2BooleanMaps.EmptyMap EMPTY_MAP = new Char2BooleanMaps.EmptyMap();

   private Char2BooleanMaps() {
      super();
   }

   public static ObjectIterator<Char2BooleanMap.Entry> fastIterator(Char2BooleanMap var0) {
      ObjectSet var1 = var0.char2BooleanEntrySet();
      return var1 instanceof Char2BooleanMap.FastEntrySet ? ((Char2BooleanMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Char2BooleanMap var0, Consumer<? super Char2BooleanMap.Entry> var1) {
      ObjectSet var2 = var0.char2BooleanEntrySet();
      if (var2 instanceof Char2BooleanMap.FastEntrySet) {
         ((Char2BooleanMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Char2BooleanMap.Entry> fastIterable(Char2BooleanMap var0) {
      final ObjectSet var1 = var0.char2BooleanEntrySet();
      return (ObjectIterable)(var1 instanceof Char2BooleanMap.FastEntrySet ? new ObjectIterable<Char2BooleanMap.Entry>() {
         public ObjectIterator<Char2BooleanMap.Entry> iterator() {
            return ((Char2BooleanMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Char2BooleanMap.Entry> var1x) {
            ((Char2BooleanMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Char2BooleanMap singleton(char var0, boolean var1) {
      return new Char2BooleanMaps.Singleton(var0, var1);
   }

   public static Char2BooleanMap singleton(Character var0, Boolean var1) {
      return new Char2BooleanMaps.Singleton(var0, var1);
   }

   public static Char2BooleanMap synchronize(Char2BooleanMap var0) {
      return new Char2BooleanMaps.SynchronizedMap(var0);
   }

   public static Char2BooleanMap synchronize(Char2BooleanMap var0, Object var1) {
      return new Char2BooleanMaps.SynchronizedMap(var0, var1);
   }

   public static Char2BooleanMap unmodifiable(Char2BooleanMap var0) {
      return new Char2BooleanMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Char2BooleanFunctions.UnmodifiableFunction implements Char2BooleanMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2BooleanMap map;
      protected transient ObjectSet<Char2BooleanMap.Entry> entries;
      protected transient CharSet keys;
      protected transient BooleanCollection values;

      protected UnmodifiableMap(Char2BooleanMap var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(boolean var1) {
         return this.map.containsValue(var1);
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      public void putAll(Map<? extends Character, ? extends Boolean> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Char2BooleanMap.Entry> char2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.char2BooleanEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Character, Boolean>> entrySet() {
         return this.char2BooleanEntrySet();
      }

      public CharSet keySet() {
         if (this.keys == null) {
            this.keys = CharSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      public BooleanCollection values() {
         return this.values == null ? BooleanCollections.unmodifiable(this.map.values()) : this.values;
      }

      public boolean isEmpty() {
         return this.map.isEmpty();
      }

      public int hashCode() {
         return this.map.hashCode();
      }

      public boolean equals(Object var1) {
         return var1 == this ? true : this.map.equals(var1);
      }

      public boolean getOrDefault(char var1, boolean var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super Character, ? super Boolean> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Character, ? super Boolean, ? extends Boolean> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean putIfAbsent(char var1, boolean var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(char var1, boolean var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(char var1, boolean var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(char var1, boolean var2, boolean var3) {
         throw new UnsupportedOperationException();
      }

      public boolean computeIfAbsent(char var1, IntPredicate var2) {
         throw new UnsupportedOperationException();
      }

      public boolean computeIfAbsentNullable(char var1, IntFunction<? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      public boolean computeIfAbsentPartial(char var1, Char2BooleanFunction var2) {
         throw new UnsupportedOperationException();
      }

      public boolean computeIfPresent(char var1, BiFunction<? super Character, ? super Boolean, ? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      public boolean compute(char var1, BiFunction<? super Character, ? super Boolean, ? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      public boolean merge(char var1, boolean var2, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean getOrDefault(Object var1, Boolean var2) {
         return this.map.getOrDefault(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public boolean remove(Object var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean replace(Character var1, Boolean var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Character var1, Boolean var2, Boolean var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean putIfAbsent(Character var1, Boolean var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean computeIfAbsent(Character var1, Function<? super Character, ? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean computeIfPresent(Character var1, BiFunction<? super Character, ? super Boolean, ? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean compute(Character var1, BiFunction<? super Character, ? super Boolean, ? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean merge(Character var1, Boolean var2, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Char2BooleanFunctions.SynchronizedFunction implements Char2BooleanMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2BooleanMap map;
      protected transient ObjectSet<Char2BooleanMap.Entry> entries;
      protected transient CharSet keys;
      protected transient BooleanCollection values;

      protected SynchronizedMap(Char2BooleanMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Char2BooleanMap var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(boolean var1) {
         synchronized(this.sync) {
            return this.map.containsValue(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         synchronized(this.sync) {
            return this.map.containsValue(var1);
         }
      }

      public void putAll(Map<? extends Character, ? extends Boolean> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Char2BooleanMap.Entry> char2BooleanEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.char2BooleanEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Character, Boolean>> entrySet() {
         return this.char2BooleanEntrySet();
      }

      public CharSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = CharSets.synchronize(this.map.keySet(), this.sync);
            }

            return this.keys;
         }
      }

      public BooleanCollection values() {
         synchronized(this.sync) {
            return this.values == null ? BooleanCollections.synchronize(this.map.values(), this.sync) : this.values;
         }
      }

      public boolean isEmpty() {
         synchronized(this.sync) {
            return this.map.isEmpty();
         }
      }

      public int hashCode() {
         synchronized(this.sync) {
            return this.map.hashCode();
         }
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            synchronized(this.sync) {
               return this.map.equals(var1);
            }
         }
      }

      private void writeObject(ObjectOutputStream var1) throws IOException {
         synchronized(this.sync) {
            var1.defaultWriteObject();
         }
      }

      public boolean getOrDefault(char var1, boolean var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super Character, ? super Boolean> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Character, ? super Boolean, ? extends Boolean> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public boolean putIfAbsent(char var1, boolean var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(char var1, boolean var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public boolean replace(char var1, boolean var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(char var1, boolean var2, boolean var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      public boolean computeIfAbsent(char var1, IntPredicate var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      public boolean computeIfAbsentNullable(char var1, IntFunction<? extends Boolean> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var2);
         }
      }

      public boolean computeIfAbsentPartial(char var1, Char2BooleanFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var2);
         }
      }

      public boolean computeIfPresent(char var1, BiFunction<? super Character, ? super Boolean, ? extends Boolean> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      public boolean compute(char var1, BiFunction<? super Character, ? super Boolean, ? extends Boolean> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      public boolean merge(char var1, boolean var2, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean getOrDefault(Object var1, Boolean var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean remove(Object var1, Object var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean replace(Character var1, Boolean var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Character var1, Boolean var2, Boolean var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean putIfAbsent(Character var1, Boolean var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean computeIfAbsent(Character var1, Function<? super Character, ? extends Boolean> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean computeIfPresent(Character var1, BiFunction<? super Character, ? super Boolean, ? extends Boolean> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean compute(Character var1, BiFunction<? super Character, ? super Boolean, ? extends Boolean> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean merge(Character var1, Boolean var2, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Char2BooleanFunctions.Singleton implements Char2BooleanMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Char2BooleanMap.Entry> entries;
      protected transient CharSet keys;
      protected transient BooleanCollection values;

      protected Singleton(char var1, boolean var2) {
         super(var1, var2);
      }

      public boolean containsValue(boolean var1) {
         return this.value == var1;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return (Boolean)var1 == this.value;
      }

      public void putAll(Map<? extends Character, ? extends Boolean> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Char2BooleanMap.Entry> char2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractChar2BooleanMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Character, Boolean>> entrySet() {
         return this.char2BooleanEntrySet();
      }

      public CharSet keySet() {
         if (this.keys == null) {
            this.keys = CharSets.singleton(this.key);
         }

         return this.keys;
      }

      public BooleanCollection values() {
         if (this.values == null) {
            this.values = BooleanSets.singleton(this.value);
         }

         return this.values;
      }

      public boolean isEmpty() {
         return false;
      }

      public int hashCode() {
         return this.key ^ (this.value ? 1231 : 1237);
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof Map)) {
            return false;
         } else {
            Map var2 = (Map)var1;
            return var2.size() != 1 ? false : ((Entry)var2.entrySet().iterator().next()).equals(this.entrySet().iterator().next());
         }
      }

      public String toString() {
         return "{" + this.key + "=>" + this.value + "}";
      }
   }

   public static class EmptyMap extends Char2BooleanFunctions.EmptyFunction implements Char2BooleanMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
         super();
      }

      public boolean containsValue(boolean var1) {
         return false;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return false;
      }

      public void putAll(Map<? extends Character, ? extends Boolean> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Char2BooleanMap.Entry> char2BooleanEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public CharSet keySet() {
         return CharSets.EMPTY_SET;
      }

      public BooleanCollection values() {
         return BooleanSets.EMPTY_SET;
      }

      public Object clone() {
         return Char2BooleanMaps.EMPTY_MAP;
      }

      public boolean isEmpty() {
         return true;
      }

      public int hashCode() {
         return 0;
      }

      public boolean equals(Object var1) {
         return !(var1 instanceof Map) ? false : ((Map)var1).isEmpty();
      }

      public String toString() {
         return "{}";
      }
   }
}
