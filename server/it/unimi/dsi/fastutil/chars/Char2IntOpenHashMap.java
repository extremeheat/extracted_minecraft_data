package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
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
import java.util.function.IntUnaryOperator;

public class Char2IntOpenHashMap extends AbstractChar2IntMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient char[] key;
   protected transient int[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Char2IntMap.FastEntrySet entries;
   protected transient CharSet keys;
   protected transient IntCollection values;

   public Char2IntOpenHashMap(int var1, float var2) {
      super();
      if (var2 > 0.0F && var2 <= 1.0F) {
         if (var1 < 0) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
         } else {
            this.f = var2;
            this.minN = this.n = HashCommon.arraySize(var1, var2);
            this.mask = this.n - 1;
            this.maxFill = HashCommon.maxFill(this.n, var2);
            this.key = new char[this.n + 1];
            this.value = new int[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public Char2IntOpenHashMap(int var1) {
      this(var1, 0.75F);
   }

   public Char2IntOpenHashMap() {
      this(16, 0.75F);
   }

   public Char2IntOpenHashMap(Map<? extends Character, ? extends Integer> var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Char2IntOpenHashMap(Map<? extends Character, ? extends Integer> var1) {
      this(var1, 0.75F);
   }

   public Char2IntOpenHashMap(Char2IntMap var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Char2IntOpenHashMap(Char2IntMap var1) {
      this(var1, 0.75F);
   }

   public Char2IntOpenHashMap(char[] var1, int[] var2, float var3) {
      this(var1.length, var3);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            this.put(var1[var4], var2[var4]);
         }

      }
   }

   public Char2IntOpenHashMap(char[] var1, int[] var2) {
      this(var1, var2, 0.75F);
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

   private int removeEntry(int var1) {
      int var2 = this.value[var1];
      --this.size;
      this.shiftKeys(var1);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var2;
   }

   private int removeNullEntry() {
      this.containsNullKey = false;
      int var1 = this.value[this.n];
      --this.size;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var1;
   }

   public void putAll(Map<? extends Character, ? extends Integer> var1) {
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var1.size());
      } else {
         this.tryCapacity((long)(this.size() + var1.size()));
      }

      super.putAll(var1);
   }

   private int find(char var1) {
      if (var1 == 0) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         char[] var3 = this.key;
         char var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(var1) & this.mask]) == 0) {
            return -(var4 + 1);
         } else if (var1 == var2) {
            return var4;
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (var1 == var2) {
                  return var4;
               }
            }

            return -(var4 + 1);
         }
      }
   }

   private void insert(int var1, char var2, int var3) {
      if (var1 == this.n) {
         this.containsNullKey = true;
      }

      this.key[var1] = var2;
      this.value[var1] = var3;
      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

   }

   public int put(char var1, int var2) {
      int var3 = this.find(var1);
      if (var3 < 0) {
         this.insert(-var3 - 1, var1, var2);
         return this.defRetValue;
      } else {
         int var4 = this.value[var3];
         this.value[var3] = var2;
         return var4;
      }
   }

   private int addToValue(int var1, int var2) {
      int var3 = this.value[var1];
      this.value[var1] = var3 + var2;
      return var3;
   }

   public int addTo(char var1, int var2) {
      int var3;
      if (var1 == 0) {
         if (this.containsNullKey) {
            return this.addToValue(this.n, var2);
         }

         var3 = this.n;
         this.containsNullKey = true;
      } else {
         char[] var5 = this.key;
         char var4;
         if ((var4 = var5[var3 = HashCommon.mix(var1) & this.mask]) != 0) {
            if (var4 == var1) {
               return this.addToValue(var3, var2);
            }

            while((var4 = var5[var3 = var3 + 1 & this.mask]) != 0) {
               if (var4 == var1) {
                  return this.addToValue(var3, var2);
               }
            }
         }
      }

      this.key[var3] = var1;
      this.value[var3] = this.defRetValue + var2;
      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

      return this.defRetValue;
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
               return;
            }

            int var3 = HashCommon.mix(var4) & this.mask;
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

   public int remove(char var1) {
      if (var1 == 0) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         char[] var3 = this.key;
         char var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(var1) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (var1 == var2) {
            return this.removeEntry(var4);
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (var1 == var2) {
                  return this.removeEntry(var4);
               }
            }

            return this.defRetValue;
         }
      }
   }

   public int get(char var1) {
      if (var1 == 0) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         char[] var3 = this.key;
         char var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(var1) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (var1 == var2) {
            return this.value[var4];
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (var1 == var2) {
                  return this.value[var4];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public boolean containsKey(char var1) {
      if (var1 == 0) {
         return this.containsNullKey;
      } else {
         char[] var3 = this.key;
         char var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(var1) & this.mask]) == 0) {
            return false;
         } else if (var1 == var2) {
            return true;
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (var1 == var2) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public boolean containsValue(int var1) {
      int[] var2 = this.value;
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

   public int getOrDefault(char var1, int var2) {
      if (var1 == 0) {
         return this.containsNullKey ? this.value[this.n] : var2;
      } else {
         char[] var4 = this.key;
         char var3;
         int var5;
         if ((var3 = var4[var5 = HashCommon.mix(var1) & this.mask]) == 0) {
            return var2;
         } else if (var1 == var3) {
            return this.value[var5];
         } else {
            while((var3 = var4[var5 = var5 + 1 & this.mask]) != 0) {
               if (var1 == var3) {
                  return this.value[var5];
               }
            }

            return var2;
         }
      }
   }

   public int putIfAbsent(char var1, int var2) {
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         this.insert(-var3 - 1, var1, var2);
         return this.defRetValue;
      }
   }

   public boolean remove(char var1, int var2) {
      if (var1 == 0) {
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
         if ((var3 = var4[var5 = HashCommon.mix(var1) & this.mask]) == 0) {
            return false;
         } else if (var1 == var3 && var2 == this.value[var5]) {
            this.removeEntry(var5);
            return true;
         } else {
            do {
               if ((var3 = var4[var5 = var5 + 1 & this.mask]) == 0) {
                  return false;
               }
            } while(var1 != var3 || var2 != this.value[var5]);

            this.removeEntry(var5);
            return true;
         }
      }
   }

   public boolean replace(char var1, int var2, int var3) {
      int var4 = this.find(var1);
      if (var4 >= 0 && var2 == this.value[var4]) {
         this.value[var4] = var3;
         return true;
      } else {
         return false;
      }
   }

   public int replace(char var1, int var2) {
      int var3 = this.find(var1);
      if (var3 < 0) {
         return this.defRetValue;
      } else {
         int var4 = this.value[var3];
         this.value[var3] = var2;
         return var4;
      }
   }

   public int computeIfAbsent(char var1, IntUnaryOperator var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         int var4 = var2.applyAsInt(var1);
         this.insert(-var3 - 1, var1, var4);
         return var4;
      }
   }

   public int computeIfAbsentNullable(char var1, IntFunction<? extends Integer> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         Integer var4 = (Integer)var2.apply(var1);
         if (var4 == null) {
            return this.defRetValue;
         } else {
            int var5 = var4;
            this.insert(-var3 - 1, var1, var5);
            return var5;
         }
      }
   }

   public int computeIfPresent(char var1, BiFunction<? super Character, ? super Integer, ? extends Integer> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 < 0) {
         return this.defRetValue;
      } else {
         Integer var4 = (Integer)var2.apply(var1, this.value[var3]);
         if (var4 == null) {
            if (var1 == 0) {
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

   public int compute(char var1, BiFunction<? super Character, ? super Integer, ? extends Integer> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      Integer var4 = (Integer)var2.apply(var1, var3 >= 0 ? this.value[var3] : null);
      if (var4 == null) {
         if (var3 >= 0) {
            if (var1 == 0) {
               this.removeNullEntry();
            } else {
               this.removeEntry(var3);
            }
         }

         return this.defRetValue;
      } else {
         int var5 = var4;
         if (var3 < 0) {
            this.insert(-var3 - 1, var1, var5);
            return var5;
         } else {
            return this.value[var3] = var5;
         }
      }
   }

   public int merge(char var1, int var2, BiFunction<? super Integer, ? super Integer, ? extends Integer> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      if (var4 < 0) {
         this.insert(-var4 - 1, var1, var2);
         return var2;
      } else {
         Integer var5 = (Integer)var3.apply(this.value[var4], var2);
         if (var5 == null) {
            if (var1 == 0) {
               this.removeNullEntry();
            } else {
               this.removeEntry(var4);
            }

            return this.defRetValue;
         } else {
            return this.value[var4] = var5;
         }
      }
   }

   public void clear() {
      if (this.size != 0) {
         this.size = 0;
         this.containsNullKey = false;
         Arrays.fill(this.key, '\u0000');
      }
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public Char2IntMap.FastEntrySet char2IntEntrySet() {
      if (this.entries == null) {
         this.entries = new Char2IntOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   public CharSet keySet() {
      if (this.keys == null) {
         this.keys = new Char2IntOpenHashMap.KeySet();
      }

      return this.keys;
   }

   public IntCollection values() {
      if (this.values == null) {
         this.values = new AbstractIntCollection() {
            public IntIterator iterator() {
               return Char2IntOpenHashMap.this.new ValueIterator();
            }

            public int size() {
               return Char2IntOpenHashMap.this.size;
            }

            public boolean contains(int var1) {
               return Char2IntOpenHashMap.this.containsValue(var1);
            }

            public void clear() {
               Char2IntOpenHashMap.this.clear();
            }

            public void forEach(IntConsumer var1) {
               if (Char2IntOpenHashMap.this.containsNullKey) {
                  var1.accept(Char2IntOpenHashMap.this.value[Char2IntOpenHashMap.this.n]);
               }

               int var2 = Char2IntOpenHashMap.this.n;

               while(var2-- != 0) {
                  if (Char2IntOpenHashMap.this.key[var2] != 0) {
                     var1.accept(Char2IntOpenHashMap.this.value[var2]);
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
      int[] var3 = this.value;
      int var4 = var1 - 1;
      char[] var5 = new char[var1 + 1];
      int[] var6 = new int[var1 + 1];
      int var7 = this.n;

      int var8;
      for(int var9 = this.realSize(); var9-- != 0; var6[var8] = var3[var7]) {
         do {
            --var7;
         } while(var2[var7] == 0);

         if (var5[var8 = HashCommon.mix(var2[var7]) & var4] != 0) {
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

   public Char2IntOpenHashMap clone() {
      Char2IntOpenHashMap var1;
      try {
         var1 = (Char2IntOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.keys = null;
      var1.values = null;
      var1.entries = null;
      var1.containsNullKey = this.containsNullKey;
      var1.key = (char[])this.key.clone();
      var1.value = (int[])this.value.clone();
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

         char var5 = this.key[var3];
         int var6 = var5 ^ this.value[var3];
         var1 += var6;
      }

      if (this.containsNullKey) {
         var1 += this.value[this.n];
      }

      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      char[] var2 = this.key;
      int[] var3 = this.value;
      Char2IntOpenHashMap.MapIterator var4 = new Char2IntOpenHashMap.MapIterator();
      var1.defaultWriteObject();
      int var5 = this.size;

      while(var5-- != 0) {
         int var6 = var4.nextEntry();
         var1.writeChar(var2[var6]);
         var1.writeInt(var3[var6]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      char[] var2 = this.key = new char[this.n + 1];
      int[] var3 = this.value = new int[this.n + 1];

      int var5;
      int var7;
      for(int var6 = this.size; var6-- != 0; var3[var7] = var5) {
         char var4 = var1.readChar();
         var5 = var1.readInt();
         if (var4 == 0) {
            var7 = this.n;
            this.containsNullKey = true;
         } else {
            for(var7 = HashCommon.mix(var4) & this.mask; var2[var7] != 0; var7 = var7 + 1 & this.mask) {
            }
         }

         var2[var7] = var4;
      }

   }

   private void checkTable() {
   }

   private final class ValueIterator extends Char2IntOpenHashMap.MapIterator implements IntIterator {
      public ValueIterator() {
         super(null);
      }

      public int nextInt() {
         return Char2IntOpenHashMap.this.value[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractCharSet {
      private KeySet() {
         super();
      }

      public CharIterator iterator() {
         return Char2IntOpenHashMap.this.new KeyIterator();
      }

      public void forEach(IntConsumer var1) {
         if (Char2IntOpenHashMap.this.containsNullKey) {
            var1.accept(Char2IntOpenHashMap.this.key[Char2IntOpenHashMap.this.n]);
         }

         int var2 = Char2IntOpenHashMap.this.n;

         while(var2-- != 0) {
            char var3 = Char2IntOpenHashMap.this.key[var2];
            if (var3 != 0) {
               var1.accept(var3);
            }
         }

      }

      public int size() {
         return Char2IntOpenHashMap.this.size;
      }

      public boolean contains(char var1) {
         return Char2IntOpenHashMap.this.containsKey(var1);
      }

      public boolean remove(char var1) {
         int var2 = Char2IntOpenHashMap.this.size;
         Char2IntOpenHashMap.this.remove(var1);
         return Char2IntOpenHashMap.this.size != var2;
      }

      public void clear() {
         Char2IntOpenHashMap.this.clear();
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Char2IntOpenHashMap.MapIterator implements CharIterator {
      public KeyIterator() {
         super(null);
      }

      public char nextChar() {
         return Char2IntOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Char2IntMap.Entry> implements Char2IntMap.FastEntrySet {
      private MapEntrySet() {
         super();
      }

      public ObjectIterator<Char2IntMap.Entry> iterator() {
         return Char2IntOpenHashMap.this.new EntryIterator();
      }

      public ObjectIterator<Char2IntMap.Entry> fastIterator() {
         return Char2IntOpenHashMap.this.new FastEntryIterator();
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Character) {
               if (var2.getValue() != null && var2.getValue() instanceof Integer) {
                  char var3 = (Character)var2.getKey();
                  int var4 = (Integer)var2.getValue();
                  if (var3 == 0) {
                     return Char2IntOpenHashMap.this.containsNullKey && Char2IntOpenHashMap.this.value[Char2IntOpenHashMap.this.n] == var4;
                  } else {
                     char[] var6 = Char2IntOpenHashMap.this.key;
                     char var5;
                     int var7;
                     if ((var5 = var6[var7 = HashCommon.mix(var3) & Char2IntOpenHashMap.this.mask]) == 0) {
                        return false;
                     } else if (var3 == var5) {
                        return Char2IntOpenHashMap.this.value[var7] == var4;
                     } else {
                        while((var5 = var6[var7 = var7 + 1 & Char2IntOpenHashMap.this.mask]) != 0) {
                           if (var3 == var5) {
                              return Char2IntOpenHashMap.this.value[var7] == var4;
                           }
                        }

                        return false;
                     }
                  }
               } else {
                  return false;
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
               if (var2.getValue() != null && var2.getValue() instanceof Integer) {
                  char var3 = (Character)var2.getKey();
                  int var4 = (Integer)var2.getValue();
                  if (var3 == 0) {
                     if (Char2IntOpenHashMap.this.containsNullKey && Char2IntOpenHashMap.this.value[Char2IntOpenHashMap.this.n] == var4) {
                        Char2IntOpenHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     char[] var6 = Char2IntOpenHashMap.this.key;
                     char var5;
                     int var7;
                     if ((var5 = var6[var7 = HashCommon.mix(var3) & Char2IntOpenHashMap.this.mask]) == 0) {
                        return false;
                     } else if (var5 == var3) {
                        if (Char2IntOpenHashMap.this.value[var7] == var4) {
                           Char2IntOpenHashMap.this.removeEntry(var7);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        do {
                           if ((var5 = var6[var7 = var7 + 1 & Char2IntOpenHashMap.this.mask]) == 0) {
                              return false;
                           }
                        } while(var5 != var3 || Char2IntOpenHashMap.this.value[var7] != var4);

                        Char2IntOpenHashMap.this.removeEntry(var7);
                        return true;
                     }
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      public int size() {
         return Char2IntOpenHashMap.this.size;
      }

      public void clear() {
         Char2IntOpenHashMap.this.clear();
      }

      public void forEach(Consumer<? super Char2IntMap.Entry> var1) {
         if (Char2IntOpenHashMap.this.containsNullKey) {
            var1.accept(new AbstractChar2IntMap.BasicEntry(Char2IntOpenHashMap.this.key[Char2IntOpenHashMap.this.n], Char2IntOpenHashMap.this.value[Char2IntOpenHashMap.this.n]));
         }

         int var2 = Char2IntOpenHashMap.this.n;

         while(var2-- != 0) {
            if (Char2IntOpenHashMap.this.key[var2] != 0) {
               var1.accept(new AbstractChar2IntMap.BasicEntry(Char2IntOpenHashMap.this.key[var2], Char2IntOpenHashMap.this.value[var2]));
            }
         }

      }

      public void fastForEach(Consumer<? super Char2IntMap.Entry> var1) {
         AbstractChar2IntMap.BasicEntry var2 = new AbstractChar2IntMap.BasicEntry();
         if (Char2IntOpenHashMap.this.containsNullKey) {
            var2.key = Char2IntOpenHashMap.this.key[Char2IntOpenHashMap.this.n];
            var2.value = Char2IntOpenHashMap.this.value[Char2IntOpenHashMap.this.n];
            var1.accept(var2);
         }

         int var3 = Char2IntOpenHashMap.this.n;

         while(var3-- != 0) {
            if (Char2IntOpenHashMap.this.key[var3] != 0) {
               var2.key = Char2IntOpenHashMap.this.key[var3];
               var2.value = Char2IntOpenHashMap.this.value[var3];
               var1.accept(var2);
            }
         }

      }

      // $FF: synthetic method
      MapEntrySet(Object var2) {
         this();
      }
   }

   private class FastEntryIterator extends Char2IntOpenHashMap.MapIterator implements ObjectIterator<Char2IntMap.Entry> {
      private final Char2IntOpenHashMap.MapEntry entry;

      private FastEntryIterator() {
         super(null);
         this.entry = Char2IntOpenHashMap.this.new MapEntry();
      }

      public Char2IntOpenHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      // $FF: synthetic method
      FastEntryIterator(Object var2) {
         this();
      }
   }

   private class EntryIterator extends Char2IntOpenHashMap.MapIterator implements ObjectIterator<Char2IntMap.Entry> {
      private Char2IntOpenHashMap.MapEntry entry;

      private EntryIterator() {
         super(null);
      }

      public Char2IntOpenHashMap.MapEntry next() {
         return this.entry = Char2IntOpenHashMap.this.new MapEntry(this.nextEntry());
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
         this.pos = Char2IntOpenHashMap.this.n;
         this.last = -1;
         this.c = Char2IntOpenHashMap.this.size;
         this.mustReturnNullKey = Char2IntOpenHashMap.this.containsNullKey;
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
               return this.last = Char2IntOpenHashMap.this.n;
            } else {
               char[] var1 = Char2IntOpenHashMap.this.key;

               while(--this.pos >= 0) {
                  if (var1[this.pos] != 0) {
                     return this.last = this.pos;
                  }
               }

               this.last = -2147483648;
               char var2 = this.wrapped.getChar(-this.pos - 1);

               int var3;
               for(var3 = HashCommon.mix(var2) & Char2IntOpenHashMap.this.mask; var2 != var1[var3]; var3 = var3 + 1 & Char2IntOpenHashMap.this.mask) {
               }

               return var3;
            }
         }
      }

      private void shiftKeys(int var1) {
         char[] var5 = Char2IntOpenHashMap.this.key;

         while(true) {
            int var2 = var1;
            var1 = var1 + 1 & Char2IntOpenHashMap.this.mask;

            char var4;
            while(true) {
               if ((var4 = var5[var1]) == 0) {
                  var5[var2] = 0;
                  return;
               }

               int var3 = HashCommon.mix(var4) & Char2IntOpenHashMap.this.mask;
               if (var2 <= var1) {
                  if (var2 >= var3 || var3 > var1) {
                     break;
                  }
               } else if (var2 >= var3 && var3 > var1) {
                  break;
               }

               var1 = var1 + 1 & Char2IntOpenHashMap.this.mask;
            }

            if (var1 < var2) {
               if (this.wrapped == null) {
                  this.wrapped = new CharArrayList(2);
               }

               this.wrapped.add(var5[var1]);
            }

            var5[var2] = var4;
            Char2IntOpenHashMap.this.value[var2] = Char2IntOpenHashMap.this.value[var1];
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == Char2IntOpenHashMap.this.n) {
               Char2IntOpenHashMap.this.containsNullKey = false;
            } else {
               if (this.pos < 0) {
                  Char2IntOpenHashMap.this.remove(this.wrapped.getChar(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            --Char2IntOpenHashMap.this.size;
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

   final class MapEntry implements Char2IntMap.Entry, java.util.Map.Entry<Character, Integer> {
      int index;

      MapEntry(int var2) {
         super();
         this.index = var2;
      }

      MapEntry() {
         super();
      }

      public char getCharKey() {
         return Char2IntOpenHashMap.this.key[this.index];
      }

      public int getIntValue() {
         return Char2IntOpenHashMap.this.value[this.index];
      }

      public int setValue(int var1) {
         int var2 = Char2IntOpenHashMap.this.value[this.index];
         Char2IntOpenHashMap.this.value[this.index] = var1;
         return var2;
      }

      /** @deprecated */
      @Deprecated
      public Character getKey() {
         return Char2IntOpenHashMap.this.key[this.index];
      }

      /** @deprecated */
      @Deprecated
      public Integer getValue() {
         return Char2IntOpenHashMap.this.value[this.index];
      }

      /** @deprecated */
      @Deprecated
      public Integer setValue(Integer var1) {
         return this.setValue(var1);
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            return Char2IntOpenHashMap.this.key[this.index] == (Character)var2.getKey() && Char2IntOpenHashMap.this.value[this.index] == (Integer)var2.getValue();
         }
      }

      public int hashCode() {
         return Char2IntOpenHashMap.this.key[this.index] ^ Char2IntOpenHashMap.this.value[this.index];
      }

      public String toString() {
         return Char2IntOpenHashMap.this.key[this.index] + "=>" + Char2IntOpenHashMap.this.value[this.index];
      }
   }
}
