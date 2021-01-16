package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;

public class Char2ReferenceOpenCustomHashMap<V> extends AbstractChar2ReferenceMap<V> implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient char[] key;
   protected transient V[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected CharHash.Strategy strategy;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Char2ReferenceMap.FastEntrySet<V> entries;
   protected transient CharSet keys;
   protected transient ReferenceCollection<V> values;

   public Char2ReferenceOpenCustomHashMap(int var1, float var2, CharHash.Strategy var3) {
      super();
      this.strategy = var3;
      if (var2 > 0.0F && var2 <= 1.0F) {
         if (var1 < 0) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
         } else {
            this.f = var2;
            this.minN = this.n = HashCommon.arraySize(var1, var2);
            this.mask = this.n - 1;
            this.maxFill = HashCommon.maxFill(this.n, var2);
            this.key = new char[this.n + 1];
            this.value = new Object[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public Char2ReferenceOpenCustomHashMap(int var1, CharHash.Strategy var2) {
      this(var1, 0.75F, var2);
   }

   public Char2ReferenceOpenCustomHashMap(CharHash.Strategy var1) {
      this(16, 0.75F, var1);
   }

   public Char2ReferenceOpenCustomHashMap(Map<? extends Character, ? extends V> var1, float var2, CharHash.Strategy var3) {
      this(var1.size(), var2, var3);
      this.putAll(var1);
   }

   public Char2ReferenceOpenCustomHashMap(Map<? extends Character, ? extends V> var1, CharHash.Strategy var2) {
      this(var1, 0.75F, var2);
   }

   public Char2ReferenceOpenCustomHashMap(Char2ReferenceMap<V> var1, float var2, CharHash.Strategy var3) {
      this(var1.size(), var2, var3);
      this.putAll(var1);
   }

   public Char2ReferenceOpenCustomHashMap(Char2ReferenceMap<V> var1, CharHash.Strategy var2) {
      this(var1, 0.75F, var2);
   }

   public Char2ReferenceOpenCustomHashMap(char[] var1, V[] var2, float var3, CharHash.Strategy var4) {
      this(var1.length, var3, var4);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var5 = 0; var5 < var1.length; ++var5) {
            this.put(var1[var5], var2[var5]);
         }

      }
   }

   public Char2ReferenceOpenCustomHashMap(char[] var1, V[] var2, CharHash.Strategy var3) {
      this(var1, var2, 0.75F, var3);
   }

   public CharHash.Strategy strategy() {
      return this.strategy;
   }

   private int realSize() {
      return this.containsNullKey ? this.size - 1 : this.size;
   }

   private void ensureCapacity(int var1) {
      int var2 = HashCommon.arraySize(var1, this.f);
      if (var2 > this.n) {
         this.rehash(var2);
      }

   }

   private void tryCapacity(long var1) {
      int var3 = (int)Math.min(1073741824L, Math.max(2L, HashCommon.nextPowerOfTwo((long)Math.ceil((double)((float)var1 / this.f)))));
      if (var3 > this.n) {
         this.rehash(var3);
      }

   }

   private V removeEntry(int var1) {
      Object var2 = this.value[var1];
      this.value[var1] = null;
      --this.size;
      this.shiftKeys(var1);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var2;
   }

   private V removeNullEntry() {
      this.containsNullKey = false;
      Object var1 = this.value[this.n];
      this.value[this.n] = null;
      --this.size;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var1;
   }

   public void putAll(Map<? extends Character, ? extends V> var1) {
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var1.size());
      } else {
         this.tryCapacity((long)(this.size() + var1.size()));
      }

      super.putAll(var1);
   }

   private int find(char var1) {
      if (this.strategy.equals(var1, '\u0000')) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         char[] var3 = this.key;
         char var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == 0) {
            return -(var4 + 1);
         } else if (this.strategy.equals(var1, var2)) {
            return var4;
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (this.strategy.equals(var1, var2)) {
                  return var4;
               }
            }

            return -(var4 + 1);
         }
      }
   }

   private void insert(int var1, char var2, V var3) {
      if (var1 == this.n) {
         this.containsNullKey = true;
      }

      this.key[var1] = var2;
      this.value[var1] = var3;
      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

   }

   public V put(char var1, V var2) {
      int var3 = this.find(var1);
      if (var3 < 0) {
         this.insert(-var3 - 1, var1, var2);
         return this.defRetValue;
      } else {
         Object var4 = this.value[var3];
         this.value[var3] = var2;
         return var4;
      }
   }

   protected final void shiftKeys(int var1) {
      char[] var5 = this.key;

      while(true) {
         int var2 = var1;
         var1 = var1 + 1 & this.mask;

         char var4;
         while(true) {
            if ((var4 = var5[var1]) == 0) {
               var5[var2] = 0;
               this.value[var2] = null;
               return;
            }

            int var3 = HashCommon.mix(this.strategy.hashCode(var4)) & this.mask;
            if (var2 <= var1) {
               if (var2 >= var3 || var3 > var1) {
                  break;
               }
            } else if (var2 >= var3 && var3 > var1) {
               break;
            }

            var1 = var1 + 1 & this.mask;
         }

         var5[var2] = var4;
         this.value[var2] = this.value[var1];
      }
   }

   public V remove(char var1) {
      if (this.strategy.equals(var1, '\u0000')) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         char[] var3 = this.key;
         char var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (this.strategy.equals(var1, var2)) {
            return this.removeEntry(var4);
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (this.strategy.equals(var1, var2)) {
                  return this.removeEntry(var4);
               }
            }

            return this.defRetValue;
         }
      }
   }

   public V get(char var1) {
      if (this.strategy.equals(var1, '\u0000')) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         char[] var3 = this.key;
         char var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (this.strategy.equals(var1, var2)) {
            return this.value[var4];
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (this.strategy.equals(var1, var2)) {
                  return this.value[var4];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public boolean containsKey(char var1) {
      if (this.strategy.equals(var1, '\u0000')) {
         return this.containsNullKey;
      } else {
         char[] var3 = this.key;
         char var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == 0) {
            return false;
         } else if (this.strategy.equals(var1, var2)) {
            return true;
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (this.strategy.equals(var1, var2)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public boolean containsValue(Object var1) {
      Object[] var2 = this.value;
      char[] var3 = this.key;
      if (this.containsNullKey && var2[this.n] == var1) {
         return true;
      } else {
         int var4 = this.n;

         do {
            if (var4-- == 0) {
               return false;
            }
         } while(var3[var4] == 0 || var2[var4] != var1);

         return true;
      }
   }

   public V getOrDefault(char var1, V var2) {
      if (this.strategy.equals(var1, '\u0000')) {
         return this.containsNullKey ? this.value[this.n] : var2;
      } else {
         char[] var4 = this.key;
         char var3;
         int var5;
         if ((var3 = var4[var5 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == 0) {
            return var2;
         } else if (this.strategy.equals(var1, var3)) {
            return this.value[var5];
         } else {
            while((var3 = var4[var5 = var5 + 1 & this.mask]) != 0) {
               if (this.strategy.equals(var1, var3)) {
                  return this.value[var5];
               }
            }

            return var2;
         }
      }
   }

   public V putIfAbsent(char var1, V var2) {
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         this.insert(-var3 - 1, var1, var2);
         return this.defRetValue;
      }
   }

   public boolean remove(char var1, Object var2) {
      if (this.strategy.equals(var1, '\u0000')) {
         if (this.containsNullKey && var2 == this.value[this.n]) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         char[] var4 = this.key;
         char var3;
         int var5;
         if ((var3 = var4[var5 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == 0) {
            return false;
         } else if (this.strategy.equals(var1, var3) && var2 == this.value[var5]) {
            this.removeEntry(var5);
            return true;
         } else {
            do {
               if ((var3 = var4[var5 = var5 + 1 & this.mask]) == 0) {
                  return false;
               }
            } while(!this.strategy.equals(var1, var3) || var2 != this.value[var5]);

            this.removeEntry(var5);
            return true;
         }
      }
   }

   public boolean replace(char var1, V var2, V var3) {
      int var4 = this.find(var1);
      if (var4 >= 0 && var2 == this.value[var4]) {
         this.value[var4] = var3;
         return true;
      } else {
         return false;
      }
   }

   public V replace(char var1, V var2) {
      int var3 = this.find(var1);
      if (var3 < 0) {
         return this.defRetValue;
      } else {
         Object var4 = this.value[var3];
         this.value[var3] = var2;
         return var4;
      }
   }

   public V computeIfAbsent(char var1, IntFunction<? extends V> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         Object var4 = var2.apply(var1);
         this.insert(-var3 - 1, var1, var4);
         return var4;
      }
   }

   public V computeIfPresent(char var1, BiFunction<? super Character, ? super V, ? extends V> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 < 0) {
         return this.defRetValue;
      } else {
         Object var4 = var2.apply(var1, this.value[var3]);
         if (var4 == null) {
            if (this.strategy.equals(var1, '\u0000')) {
               this.removeNullEntry();
            } else {
               this.removeEntry(var3);
            }

            return this.defRetValue;
         } else {
            return this.value[var3] = var4;
         }
      }
   }

   public V compute(char var1, BiFunction<? super Character, ? super V, ? extends V> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      Object var4 = var2.apply(var1, var3 >= 0 ? this.value[var3] : null);
      if (var4 == null) {
         if (var3 >= 0) {
            if (this.strategy.equals(var1, '\u0000')) {
               this.removeNullEntry();
            } else {
               this.removeEntry(var3);
            }
         }

         return this.defRetValue;
      } else if (var3 < 0) {
         this.insert(-var3 - 1, var1, var4);
         return var4;
      } else {
         return this.value[var3] = var4;
      }
   }

   public V merge(char var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      if (var4 >= 0 && this.value[var4] != null) {
         Object var5 = var3.apply(this.value[var4], var2);
         if (var5 == null) {
            if (this.strategy.equals(var1, '\u0000')) {
               this.removeNullEntry();
            } else {
               this.removeEntry(var4);
            }

            return this.defRetValue;
         } else {
            return this.value[var4] = var5;
         }
      } else if (var2 == null) {
         return this.defRetValue;
      } else {
         this.insert(-var4 - 1, var1, var2);
         return var2;
      }
   }

   public void clear() {
      if (this.size != 0) {
         this.size = 0;
         this.containsNullKey = false;
         Arrays.fill(this.key, '\u0000');
         Arrays.fill(this.value, (Object)null);
      }
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public Char2ReferenceMap.FastEntrySet<V> char2ReferenceEntrySet() {
      if (this.entries == null) {
         this.entries = new Char2ReferenceOpenCustomHashMap.MapEntrySet();
      }

      return this.entries;
   }

   public CharSet keySet() {
      if (this.keys == null) {
         this.keys = new Char2ReferenceOpenCustomHashMap.KeySet();
      }

      return this.keys;
   }

   public ReferenceCollection<V> values() {
      if (this.values == null) {
         this.values = new AbstractReferenceCollection<V>() {
            public ObjectIterator<V> iterator() {
               return Char2ReferenceOpenCustomHashMap.this.new ValueIterator();
            }

            public int size() {
               return Char2ReferenceOpenCustomHashMap.this.size;
            }

            public boolean contains(Object var1) {
               return Char2ReferenceOpenCustomHashMap.this.containsValue(var1);
            }

            public void clear() {
               Char2ReferenceOpenCustomHashMap.this.clear();
            }

            public void forEach(Consumer<? super V> var1) {
               if (Char2ReferenceOpenCustomHashMap.this.containsNullKey) {
                  var1.accept(Char2ReferenceOpenCustomHashMap.this.value[Char2ReferenceOpenCustomHashMap.this.n]);
               }

               int var2 = Char2ReferenceOpenCustomHashMap.this.n;

               while(var2-- != 0) {
                  if (Char2ReferenceOpenCustomHashMap.this.key[var2] != 0) {
                     var1.accept(Char2ReferenceOpenCustomHashMap.this.value[var2]);
                  }
               }

            }
         };
      }

      return this.values;
   }

   public boolean trim() {
      int var1 = HashCommon.arraySize(this.size, this.f);
      if (var1 < this.n && this.size <= HashCommon.maxFill(var1, this.f)) {
         try {
            this.rehash(var1);
            return true;
         } catch (OutOfMemoryError var3) {
            return false;
         }
      } else {
         return true;
      }
   }

   public boolean trim(int var1) {
      int var2 = HashCommon.nextPowerOfTwo((int)Math.ceil((double)((float)var1 / this.f)));
      if (var2 < var1 && this.size <= HashCommon.maxFill(var2, this.f)) {
         try {
            this.rehash(var2);
            return true;
         } catch (OutOfMemoryError var4) {
            return false;
         }
      } else {
         return true;
      }
   }

   protected void rehash(int var1) {
      char[] var2 = this.key;
      Object[] var3 = this.value;
      int var4 = var1 - 1;
      char[] var5 = new char[var1 + 1];
      Object[] var6 = new Object[var1 + 1];
      int var7 = this.n;

      int var8;
      for(int var9 = this.realSize(); var9-- != 0; var6[var8] = var3[var7]) {
         do {
            --var7;
         } while(var2[var7] == 0);

         if (var5[var8 = HashCommon.mix(this.strategy.hashCode(var2[var7])) & var4] != 0) {
            while(var5[var8 = var8 + 1 & var4] != 0) {
            }
         }

         var5[var8] = var2[var7];
      }

      var6[var1] = var3[this.n];
      this.n = var1;
      this.mask = var4;
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.key = var5;
      this.value = var6;
   }

   public Char2ReferenceOpenCustomHashMap<V> clone() {
      Char2ReferenceOpenCustomHashMap var1;
      try {
         var1 = (Char2ReferenceOpenCustomHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.keys = null;
      var1.values = null;
      var1.entries = null;
      var1.containsNullKey = this.containsNullKey;
      var1.key = (char[])this.key.clone();
      var1.value = (Object[])this.value.clone();
      var1.strategy = this.strategy;
      return var1;
   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.realSize();
      int var3 = 0;

      for(boolean var4 = false; var2-- != 0; ++var3) {
         while(this.key[var3] == 0) {
            ++var3;
         }

         int var5 = this.strategy.hashCode(this.key[var3]);
         if (this != this.value[var3]) {
            var5 ^= this.value[var3] == null ? 0 : System.identityHashCode(this.value[var3]);
         }

         var1 += var5;
      }

      if (this.containsNullKey) {
         var1 += this.value[this.n] == null ? 0 : System.identityHashCode(this.value[this.n]);
      }

      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      char[] var2 = this.key;
      Object[] var3 = this.value;
      Char2ReferenceOpenCustomHashMap.MapIterator var4 = new Char2ReferenceOpenCustomHashMap.MapIterator();
      var1.defaultWriteObject();
      int var5 = this.size;

      while(var5-- != 0) {
         int var6 = var4.nextEntry();
         var1.writeChar(var2[var6]);
         var1.writeObject(var3[var6]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      char[] var2 = this.key = new char[this.n + 1];
      Object[] var3 = this.value = new Object[this.n + 1];

      Object var5;
      int var7;
      for(int var6 = this.size; var6-- != 0; var3[var7] = var5) {
         char var4 = var1.readChar();
         var5 = var1.readObject();
         if (this.strategy.equals(var4, '\u0000')) {
            var7 = this.n;
            this.containsNullKey = true;
         } else {
            for(var7 = HashCommon.mix(this.strategy.hashCode(var4)) & this.mask; var2[var7] != 0; var7 = var7 + 1 & this.mask) {
            }
         }

         var2[var7] = var4;
      }

   }

   private void checkTable() {
   }

   private final class ValueIterator extends Char2ReferenceOpenCustomHashMap<V>.MapIterator implements ObjectIterator<V> {
      public ValueIterator() {
         super(null);
      }

      public V next() {
         return Char2ReferenceOpenCustomHashMap.this.value[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractCharSet {
      private KeySet() {
         super();
      }

      public CharIterator iterator() {
         return Char2ReferenceOpenCustomHashMap.this.new KeyIterator();
      }

      public void forEach(IntConsumer var1) {
         if (Char2ReferenceOpenCustomHashMap.this.containsNullKey) {
            var1.accept(Char2ReferenceOpenCustomHashMap.this.key[Char2ReferenceOpenCustomHashMap.this.n]);
         }

         int var2 = Char2ReferenceOpenCustomHashMap.this.n;

         while(var2-- != 0) {
            char var3 = Char2ReferenceOpenCustomHashMap.this.key[var2];
            if (var3 != 0) {
               var1.accept(var3);
            }
         }

      }

      public int size() {
         return Char2ReferenceOpenCustomHashMap.this.size;
      }

      public boolean contains(char var1) {
         return Char2ReferenceOpenCustomHashMap.this.containsKey(var1);
      }

      public boolean remove(char var1) {
         int var2 = Char2ReferenceOpenCustomHashMap.this.size;
         Char2ReferenceOpenCustomHashMap.this.remove(var1);
         return Char2ReferenceOpenCustomHashMap.this.size != var2;
      }

      public void clear() {
         Char2ReferenceOpenCustomHashMap.this.clear();
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Char2ReferenceOpenCustomHashMap<V>.MapIterator implements CharIterator {
      public KeyIterator() {
         super(null);
      }

      public char nextChar() {
         return Char2ReferenceOpenCustomHashMap.this.key[this.nextEntry()];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Char2ReferenceMap.Entry<V>> implements Char2ReferenceMap.FastEntrySet<V> {
      private MapEntrySet() {
         super();
      }

      public ObjectIterator<Char2ReferenceMap.Entry<V>> iterator() {
         return Char2ReferenceOpenCustomHashMap.this.new EntryIterator();
      }

      public ObjectIterator<Char2ReferenceMap.Entry<V>> fastIterator() {
         return Char2ReferenceOpenCustomHashMap.this.new FastEntryIterator();
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Character) {
               char var3 = (Character)var2.getKey();
               Object var4 = var2.getValue();
               if (Char2ReferenceOpenCustomHashMap.this.strategy.equals(var3, '\u0000')) {
                  return Char2ReferenceOpenCustomHashMap.this.containsNullKey && Char2ReferenceOpenCustomHashMap.this.value[Char2ReferenceOpenCustomHashMap.this.n] == var4;
               } else {
                  char[] var6 = Char2ReferenceOpenCustomHashMap.this.key;
                  char var5;
                  int var7;
                  if ((var5 = var6[var7 = HashCommon.mix(Char2ReferenceOpenCustomHashMap.this.strategy.hashCode(var3)) & Char2ReferenceOpenCustomHashMap.this.mask]) == 0) {
                     return false;
                  } else if (Char2ReferenceOpenCustomHashMap.this.strategy.equals(var3, var5)) {
                     return Char2ReferenceOpenCustomHashMap.this.value[var7] == var4;
                  } else {
                     while((var5 = var6[var7 = var7 + 1 & Char2ReferenceOpenCustomHashMap.this.mask]) != 0) {
                        if (Char2ReferenceOpenCustomHashMap.this.strategy.equals(var3, var5)) {
                           return Char2ReferenceOpenCustomHashMap.this.value[var7] == var4;
                        }
                     }

                     return false;
                  }
               }
            } else {
               return false;
            }
         }
      }

      public boolean remove(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Character) {
               char var3 = (Character)var2.getKey();
               Object var4 = var2.getValue();
               if (Char2ReferenceOpenCustomHashMap.this.strategy.equals(var3, '\u0000')) {
                  if (Char2ReferenceOpenCustomHashMap.this.containsNullKey && Char2ReferenceOpenCustomHashMap.this.value[Char2ReferenceOpenCustomHashMap.this.n] == var4) {
                     Char2ReferenceOpenCustomHashMap.this.removeNullEntry();
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  char[] var6 = Char2ReferenceOpenCustomHashMap.this.key;
                  char var5;
                  int var7;
                  if ((var5 = var6[var7 = HashCommon.mix(Char2ReferenceOpenCustomHashMap.this.strategy.hashCode(var3)) & Char2ReferenceOpenCustomHashMap.this.mask]) == 0) {
                     return false;
                  } else if (Char2ReferenceOpenCustomHashMap.this.strategy.equals(var5, var3)) {
                     if (Char2ReferenceOpenCustomHashMap.this.value[var7] == var4) {
                        Char2ReferenceOpenCustomHashMap.this.removeEntry(var7);
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     do {
                        if ((var5 = var6[var7 = var7 + 1 & Char2ReferenceOpenCustomHashMap.this.mask]) == 0) {
                           return false;
                        }
                     } while(!Char2ReferenceOpenCustomHashMap.this.strategy.equals(var5, var3) || Char2ReferenceOpenCustomHashMap.this.value[var7] != var4);

                     Char2ReferenceOpenCustomHashMap.this.removeEntry(var7);
                     return true;
                  }
               }
            } else {
               return false;
            }
         }
      }

      public int size() {
         return Char2ReferenceOpenCustomHashMap.this.size;
      }

      public void clear() {
         Char2ReferenceOpenCustomHashMap.this.clear();
      }

      public void forEach(Consumer<? super Char2ReferenceMap.Entry<V>> var1) {
         if (Char2ReferenceOpenCustomHashMap.this.containsNullKey) {
            var1.accept(new AbstractChar2ReferenceMap.BasicEntry(Char2ReferenceOpenCustomHashMap.this.key[Char2ReferenceOpenCustomHashMap.this.n], Char2ReferenceOpenCustomHashMap.this.value[Char2ReferenceOpenCustomHashMap.this.n]));
         }

         int var2 = Char2ReferenceOpenCustomHashMap.this.n;

         while(var2-- != 0) {
            if (Char2ReferenceOpenCustomHashMap.this.key[var2] != 0) {
               var1.accept(new AbstractChar2ReferenceMap.BasicEntry(Char2ReferenceOpenCustomHashMap.this.key[var2], Char2ReferenceOpenCustomHashMap.this.value[var2]));
            }
         }

      }

      public void fastForEach(Consumer<? super Char2ReferenceMap.Entry<V>> var1) {
         AbstractChar2ReferenceMap.BasicEntry var2 = new AbstractChar2ReferenceMap.BasicEntry();
         if (Char2ReferenceOpenCustomHashMap.this.containsNullKey) {
            var2.key = Char2ReferenceOpenCustomHashMap.this.key[Char2ReferenceOpenCustomHashMap.this.n];
            var2.value = Char2ReferenceOpenCustomHashMap.this.value[Char2ReferenceOpenCustomHashMap.this.n];
            var1.accept(var2);
         }

         int var3 = Char2ReferenceOpenCustomHashMap.this.n;

         while(var3-- != 0) {
            if (Char2ReferenceOpenCustomHashMap.this.key[var3] != 0) {
               var2.key = Char2ReferenceOpenCustomHashMap.this.key[var3];
               var2.value = Char2ReferenceOpenCustomHashMap.this.value[var3];
               var1.accept(var2);
            }
         }

      }

      // $FF: synthetic method
      MapEntrySet(Object var2) {
         this();
      }
   }

   private class FastEntryIterator extends Char2ReferenceOpenCustomHashMap<V>.MapIterator implements ObjectIterator<Char2ReferenceMap.Entry<V>> {
      private final Char2ReferenceOpenCustomHashMap<V>.MapEntry entry;

      private FastEntryIterator() {
         super(null);
         this.entry = Char2ReferenceOpenCustomHashMap.this.new MapEntry();
      }

      public Char2ReferenceOpenCustomHashMap<V>.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      // $FF: synthetic method
      FastEntryIterator(Object var2) {
         this();
      }
   }

   private class EntryIterator extends Char2ReferenceOpenCustomHashMap<V>.MapIterator implements ObjectIterator<Char2ReferenceMap.Entry<V>> {
      private Char2ReferenceOpenCustomHashMap<V>.MapEntry entry;

      private EntryIterator() {
         super(null);
      }

      public Char2ReferenceOpenCustomHashMap<V>.MapEntry next() {
         return this.entry = Char2ReferenceOpenCustomHashMap.this.new MapEntry(this.nextEntry());
      }

      public void remove() {
         super.remove();
         this.entry.index = -1;
      }

      // $FF: synthetic method
      EntryIterator(Object var2) {
         this();
      }
   }

   private class MapIterator {
      int pos;
      int last;
      int c;
      boolean mustReturnNullKey;
      CharArrayList wrapped;

      private MapIterator() {
         super();
         this.pos = Char2ReferenceOpenCustomHashMap.this.n;
         this.last = -1;
         this.c = Char2ReferenceOpenCustomHashMap.this.size;
         this.mustReturnNullKey = Char2ReferenceOpenCustomHashMap.this.containsNullKey;
      }

      public boolean hasNext() {
         return this.c != 0;
      }

      public int nextEntry() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            --this.c;
            if (this.mustReturnNullKey) {
               this.mustReturnNullKey = false;
               return this.last = Char2ReferenceOpenCustomHashMap.this.n;
            } else {
               char[] var1 = Char2ReferenceOpenCustomHashMap.this.key;

               while(--this.pos >= 0) {
                  if (var1[this.pos] != 0) {
                     return this.last = this.pos;
                  }
               }

               this.last = -2147483648;
               char var2 = this.wrapped.getChar(-this.pos - 1);

               int var3;
               for(var3 = HashCommon.mix(Char2ReferenceOpenCustomHashMap.this.strategy.hashCode(var2)) & Char2ReferenceOpenCustomHashMap.this.mask; !Char2ReferenceOpenCustomHashMap.this.strategy.equals(var2, var1[var3]); var3 = var3 + 1 & Char2ReferenceOpenCustomHashMap.this.mask) {
               }

               return var3;
            }
         }
      }

      private void shiftKeys(int var1) {
         char[] var5 = Char2ReferenceOpenCustomHashMap.this.key;

         while(true) {
            int var2 = var1;
            var1 = var1 + 1 & Char2ReferenceOpenCustomHashMap.this.mask;

            char var4;
            while(true) {
               if ((var4 = var5[var1]) == 0) {
                  var5[var2] = 0;
                  Char2ReferenceOpenCustomHashMap.this.value[var2] = null;
                  return;
               }

               int var3 = HashCommon.mix(Char2ReferenceOpenCustomHashMap.this.strategy.hashCode(var4)) & Char2ReferenceOpenCustomHashMap.this.mask;
               if (var2 <= var1) {
                  if (var2 >= var3 || var3 > var1) {
                     break;
                  }
               } else if (var2 >= var3 && var3 > var1) {
                  break;
               }

               var1 = var1 + 1 & Char2ReferenceOpenCustomHashMap.this.mask;
            }

            if (var1 < var2) {
               if (this.wrapped == null) {
                  this.wrapped = new CharArrayList(2);
               }

               this.wrapped.add(var5[var1]);
            }

            var5[var2] = var4;
            Char2ReferenceOpenCustomHashMap.this.value[var2] = Char2ReferenceOpenCustomHashMap.this.value[var1];
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == Char2ReferenceOpenCustomHashMap.this.n) {
               Char2ReferenceOpenCustomHashMap.this.containsNullKey = false;
               Char2ReferenceOpenCustomHashMap.this.value[Char2ReferenceOpenCustomHashMap.this.n] = null;
            } else {
               if (this.pos < 0) {
                  Char2ReferenceOpenCustomHashMap.this.remove(this.wrapped.getChar(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            --Char2ReferenceOpenCustomHashMap.this.size;
            this.last = -1;
         }
      }

      public int skip(int var1) {
         int var2 = var1;

         while(var2-- != 0 && this.hasNext()) {
            this.nextEntry();
         }

         return var1 - var2 - 1;
      }

      // $FF: synthetic method
      MapIterator(Object var2) {
         this();
      }
   }

   final class MapEntry implements Char2ReferenceMap.Entry<V>, java.util.Map.Entry<Character, V> {
      int index;

      MapEntry(int var2) {
         super();
         this.index = var2;
      }

      MapEntry() {
         super();
      }

      public char getCharKey() {
         return Char2ReferenceOpenCustomHashMap.this.key[this.index];
      }

      public V getValue() {
         return Char2ReferenceOpenCustomHashMap.this.value[this.index];
      }

      public V setValue(V var1) {
         Object var2 = Char2ReferenceOpenCustomHashMap.this.value[this.index];
         Char2ReferenceOpenCustomHashMap.this.value[this.index] = var1;
         return var2;
      }

      /** @deprecated */
      @Deprecated
      public Character getKey() {
         return Char2ReferenceOpenCustomHashMap.this.key[this.index];
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            return Char2ReferenceOpenCustomHashMap.this.strategy.equals(Char2ReferenceOpenCustomHashMap.this.key[this.index], (Character)var2.getKey()) && Char2ReferenceOpenCustomHashMap.this.value[this.index] == var2.getValue();
         }
      }

      public int hashCode() {
         return Char2ReferenceOpenCustomHashMap.this.strategy.hashCode(Char2ReferenceOpenCustomHashMap.this.key[this.index]) ^ (Char2ReferenceOpenCustomHashMap.this.value[this.index] == null ? 0 : System.identityHashCode(Char2ReferenceOpenCustomHashMap.this.value[this.index]));
      }

      public String toString() {
         return Char2ReferenceOpenCustomHashMap.this.key[this.index] + "=>" + Char2ReferenceOpenCustomHashMap.this.value[this.index];
      }
   }
}
