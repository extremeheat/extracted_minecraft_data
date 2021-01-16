package io.netty.handler.codec;

import io.netty.util.HashingStrategy;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

public class DefaultHeaders<K, V, T extends Headers<K, V, T>> implements Headers<K, V, T> {
   static final int HASH_CODE_SEED = -1028477387;
   private final DefaultHeaders.HeaderEntry<K, V>[] entries;
   protected final DefaultHeaders.HeaderEntry<K, V> head;
   private final byte hashMask;
   private final ValueConverter<V> valueConverter;
   private final DefaultHeaders.NameValidator<K> nameValidator;
   private final HashingStrategy<K> hashingStrategy;
   int size;

   public DefaultHeaders(ValueConverter<V> var1) {
      this(HashingStrategy.JAVA_HASHER, var1);
   }

   public DefaultHeaders(ValueConverter<V> var1, DefaultHeaders.NameValidator<K> var2) {
      this(HashingStrategy.JAVA_HASHER, var1, var2);
   }

   public DefaultHeaders(HashingStrategy<K> var1, ValueConverter<V> var2) {
      this(var1, var2, DefaultHeaders.NameValidator.NOT_NULL);
   }

   public DefaultHeaders(HashingStrategy<K> var1, ValueConverter<V> var2, DefaultHeaders.NameValidator<K> var3) {
      this(var1, var2, var3, 16);
   }

   public DefaultHeaders(HashingStrategy<K> var1, ValueConverter<V> var2, DefaultHeaders.NameValidator<K> var3, int var4) {
      super();
      this.valueConverter = (ValueConverter)ObjectUtil.checkNotNull(var2, "valueConverter");
      this.nameValidator = (DefaultHeaders.NameValidator)ObjectUtil.checkNotNull(var3, "nameValidator");
      this.hashingStrategy = (HashingStrategy)ObjectUtil.checkNotNull(var1, "nameHashingStrategy");
      this.entries = new DefaultHeaders.HeaderEntry[MathUtil.findNextPositivePowerOfTwo(Math.max(2, Math.min(var4, 128)))];
      this.hashMask = (byte)(this.entries.length - 1);
      this.head = new DefaultHeaders.HeaderEntry();
   }

   public V get(K var1) {
      ObjectUtil.checkNotNull(var1, "name");
      int var2 = this.hashingStrategy.hashCode(var1);
      int var3 = this.index(var2);
      DefaultHeaders.HeaderEntry var4 = this.entries[var3];

      Object var5;
      for(var5 = null; var4 != null; var4 = var4.next) {
         if (var4.hash == var2 && this.hashingStrategy.equals(var1, var4.key)) {
            var5 = var4.value;
         }
      }

      return var5;
   }

   public V get(K var1, V var2) {
      Object var3 = this.get(var1);
      return var3 == null ? var2 : var3;
   }

   public V getAndRemove(K var1) {
      int var2 = this.hashingStrategy.hashCode(var1);
      return this.remove0(var2, this.index(var2), ObjectUtil.checkNotNull(var1, "name"));
   }

   public V getAndRemove(K var1, V var2) {
      Object var3 = this.getAndRemove(var1);
      return var3 == null ? var2 : var3;
   }

   public List<V> getAll(K var1) {
      ObjectUtil.checkNotNull(var1, "name");
      LinkedList var2 = new LinkedList();
      int var3 = this.hashingStrategy.hashCode(var1);
      int var4 = this.index(var3);

      for(DefaultHeaders.HeaderEntry var5 = this.entries[var4]; var5 != null; var5 = var5.next) {
         if (var5.hash == var3 && this.hashingStrategy.equals(var1, var5.key)) {
            var2.addFirst(var5.getValue());
         }
      }

      return var2;
   }

   public Iterator<V> valueIterator(K var1) {
      return new DefaultHeaders.ValueIterator(var1);
   }

   public List<V> getAllAndRemove(K var1) {
      List var2 = this.getAll(var1);
      this.remove(var1);
      return var2;
   }

   public boolean contains(K var1) {
      return this.get(var1) != null;
   }

   public boolean containsObject(K var1, Object var2) {
      return this.contains(var1, this.valueConverter.convertObject(ObjectUtil.checkNotNull(var2, "value")));
   }

   public boolean containsBoolean(K var1, boolean var2) {
      return this.contains(var1, this.valueConverter.convertBoolean(var2));
   }

   public boolean containsByte(K var1, byte var2) {
      return this.contains(var1, this.valueConverter.convertByte(var2));
   }

   public boolean containsChar(K var1, char var2) {
      return this.contains(var1, this.valueConverter.convertChar(var2));
   }

   public boolean containsShort(K var1, short var2) {
      return this.contains(var1, this.valueConverter.convertShort(var2));
   }

   public boolean containsInt(K var1, int var2) {
      return this.contains(var1, this.valueConverter.convertInt(var2));
   }

   public boolean containsLong(K var1, long var2) {
      return this.contains(var1, this.valueConverter.convertLong(var2));
   }

   public boolean containsFloat(K var1, float var2) {
      return this.contains(var1, this.valueConverter.convertFloat(var2));
   }

   public boolean containsDouble(K var1, double var2) {
      return this.contains(var1, this.valueConverter.convertDouble(var2));
   }

   public boolean containsTimeMillis(K var1, long var2) {
      return this.contains(var1, this.valueConverter.convertTimeMillis(var2));
   }

   public boolean contains(K var1, V var2) {
      return this.contains(var1, var2, HashingStrategy.JAVA_HASHER);
   }

   public final boolean contains(K var1, V var2, HashingStrategy<? super V> var3) {
      ObjectUtil.checkNotNull(var1, "name");
      int var4 = this.hashingStrategy.hashCode(var1);
      int var5 = this.index(var4);

      for(DefaultHeaders.HeaderEntry var6 = this.entries[var5]; var6 != null; var6 = var6.next) {
         if (var6.hash == var4 && this.hashingStrategy.equals(var1, var6.key) && var3.equals(var2, var6.value)) {
            return true;
         }
      }

      return false;
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.head == this.head.after;
   }

   public Set<K> names() {
      if (this.isEmpty()) {
         return Collections.emptySet();
      } else {
         LinkedHashSet var1 = new LinkedHashSet(this.size());

         for(DefaultHeaders.HeaderEntry var2 = this.head.after; var2 != this.head; var2 = var2.after) {
            var1.add(var2.getKey());
         }

         return var1;
      }
   }

   public T add(K var1, V var2) {
      this.nameValidator.validateName(var1);
      ObjectUtil.checkNotNull(var2, "value");
      int var3 = this.hashingStrategy.hashCode(var1);
      int var4 = this.index(var3);
      this.add0(var3, var4, var1, var2);
      return this.thisT();
   }

   public T add(K var1, Iterable<? extends V> var2) {
      this.nameValidator.validateName(var1);
      int var3 = this.hashingStrategy.hashCode(var1);
      int var4 = this.index(var3);
      Iterator var5 = var2.iterator();

      while(var5.hasNext()) {
         Object var6 = var5.next();
         this.add0(var3, var4, var1, var6);
      }

      return this.thisT();
   }

   public T add(K var1, V... var2) {
      this.nameValidator.validateName(var1);
      int var3 = this.hashingStrategy.hashCode(var1);
      int var4 = this.index(var3);
      Object[] var5 = var2;
      int var6 = var2.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Object var8 = var5[var7];
         this.add0(var3, var4, var1, var8);
      }

      return this.thisT();
   }

   public T addObject(K var1, Object var2) {
      return this.add(var1, this.valueConverter.convertObject(ObjectUtil.checkNotNull(var2, "value")));
   }

   public T addObject(K var1, Iterable<?> var2) {
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         Object var4 = var3.next();
         this.addObject(var1, var4);
      }

      return this.thisT();
   }

   public T addObject(K var1, Object... var2) {
      Object[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Object var6 = var3[var5];
         this.addObject(var1, var6);
      }

      return this.thisT();
   }

   public T addInt(K var1, int var2) {
      return this.add(var1, this.valueConverter.convertInt(var2));
   }

   public T addLong(K var1, long var2) {
      return this.add(var1, this.valueConverter.convertLong(var2));
   }

   public T addDouble(K var1, double var2) {
      return this.add(var1, this.valueConverter.convertDouble(var2));
   }

   public T addTimeMillis(K var1, long var2) {
      return this.add(var1, this.valueConverter.convertTimeMillis(var2));
   }

   public T addChar(K var1, char var2) {
      return this.add(var1, this.valueConverter.convertChar(var2));
   }

   public T addBoolean(K var1, boolean var2) {
      return this.add(var1, this.valueConverter.convertBoolean(var2));
   }

   public T addFloat(K var1, float var2) {
      return this.add(var1, this.valueConverter.convertFloat(var2));
   }

   public T addByte(K var1, byte var2) {
      return this.add(var1, this.valueConverter.convertByte(var2));
   }

   public T addShort(K var1, short var2) {
      return this.add(var1, this.valueConverter.convertShort(var2));
   }

   public T add(Headers<? extends K, ? extends V, ?> var1) {
      if (var1 == this) {
         throw new IllegalArgumentException("can't add to itself.");
      } else {
         this.addImpl(var1);
         return this.thisT();
      }
   }

   protected void addImpl(Headers<? extends K, ? extends V, ?> var1) {
      if (var1 instanceof DefaultHeaders) {
         DefaultHeaders var2 = (DefaultHeaders)var1;
         DefaultHeaders.HeaderEntry var3 = var2.head.after;
         if (var2.hashingStrategy == this.hashingStrategy && var2.nameValidator == this.nameValidator) {
            while(var3 != var2.head) {
               this.add0(var3.hash, this.index(var3.hash), var3.key, var3.value);
               var3 = var3.after;
            }
         } else {
            while(var3 != var2.head) {
               this.add(var3.key, var3.value);
               var3 = var3.after;
            }
         }
      } else {
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            Entry var5 = (Entry)var4.next();
            this.add(var5.getKey(), var5.getValue());
         }
      }

   }

   public T set(K var1, V var2) {
      this.nameValidator.validateName(var1);
      ObjectUtil.checkNotNull(var2, "value");
      int var3 = this.hashingStrategy.hashCode(var1);
      int var4 = this.index(var3);
      this.remove0(var3, var4, var1);
      this.add0(var3, var4, var1, var2);
      return this.thisT();
   }

   public T set(K var1, Iterable<? extends V> var2) {
      this.nameValidator.validateName(var1);
      ObjectUtil.checkNotNull(var2, "values");
      int var3 = this.hashingStrategy.hashCode(var1);
      int var4 = this.index(var3);
      this.remove0(var3, var4, var1);
      Iterator var5 = var2.iterator();

      while(var5.hasNext()) {
         Object var6 = var5.next();
         if (var6 == null) {
            break;
         }

         this.add0(var3, var4, var1, var6);
      }

      return this.thisT();
   }

   public T set(K var1, V... var2) {
      this.nameValidator.validateName(var1);
      ObjectUtil.checkNotNull(var2, "values");
      int var3 = this.hashingStrategy.hashCode(var1);
      int var4 = this.index(var3);
      this.remove0(var3, var4, var1);
      Object[] var5 = var2;
      int var6 = var2.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Object var8 = var5[var7];
         if (var8 == null) {
            break;
         }

         this.add0(var3, var4, var1, var8);
      }

      return this.thisT();
   }

   public T setObject(K var1, Object var2) {
      ObjectUtil.checkNotNull(var2, "value");
      Object var3 = ObjectUtil.checkNotNull(this.valueConverter.convertObject(var2), "convertedValue");
      return this.set(var1, var3);
   }

   public T setObject(K var1, Iterable<?> var2) {
      this.nameValidator.validateName(var1);
      int var3 = this.hashingStrategy.hashCode(var1);
      int var4 = this.index(var3);
      this.remove0(var3, var4, var1);
      Iterator var5 = var2.iterator();

      while(var5.hasNext()) {
         Object var6 = var5.next();
         if (var6 == null) {
            break;
         }

         this.add0(var3, var4, var1, this.valueConverter.convertObject(var6));
      }

      return this.thisT();
   }

   public T setObject(K var1, Object... var2) {
      this.nameValidator.validateName(var1);
      int var3 = this.hashingStrategy.hashCode(var1);
      int var4 = this.index(var3);
      this.remove0(var3, var4, var1);
      Object[] var5 = var2;
      int var6 = var2.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Object var8 = var5[var7];
         if (var8 == null) {
            break;
         }

         this.add0(var3, var4, var1, this.valueConverter.convertObject(var8));
      }

      return this.thisT();
   }

   public T setInt(K var1, int var2) {
      return this.set(var1, this.valueConverter.convertInt(var2));
   }

   public T setLong(K var1, long var2) {
      return this.set(var1, this.valueConverter.convertLong(var2));
   }

   public T setDouble(K var1, double var2) {
      return this.set(var1, this.valueConverter.convertDouble(var2));
   }

   public T setTimeMillis(K var1, long var2) {
      return this.set(var1, this.valueConverter.convertTimeMillis(var2));
   }

   public T setFloat(K var1, float var2) {
      return this.set(var1, this.valueConverter.convertFloat(var2));
   }

   public T setChar(K var1, char var2) {
      return this.set(var1, this.valueConverter.convertChar(var2));
   }

   public T setBoolean(K var1, boolean var2) {
      return this.set(var1, this.valueConverter.convertBoolean(var2));
   }

   public T setByte(K var1, byte var2) {
      return this.set(var1, this.valueConverter.convertByte(var2));
   }

   public T setShort(K var1, short var2) {
      return this.set(var1, this.valueConverter.convertShort(var2));
   }

   public T set(Headers<? extends K, ? extends V, ?> var1) {
      if (var1 != this) {
         this.clear();
         this.addImpl(var1);
      }

      return this.thisT();
   }

   public T setAll(Headers<? extends K, ? extends V, ?> var1) {
      if (var1 != this) {
         Iterator var2 = var1.names().iterator();

         while(var2.hasNext()) {
            Object var3 = var2.next();
            this.remove(var3);
         }

         this.addImpl(var1);
      }

      return this.thisT();
   }

   public boolean remove(K var1) {
      return this.getAndRemove(var1) != null;
   }

   public T clear() {
      Arrays.fill(this.entries, (Object)null);
      this.head.before = this.head.after = this.head;
      this.size = 0;
      return this.thisT();
   }

   public Iterator<Entry<K, V>> iterator() {
      return new DefaultHeaders.HeaderIterator();
   }

   public Boolean getBoolean(K var1) {
      Object var2 = this.get(var1);

      try {
         return var2 != null ? this.valueConverter.convertToBoolean(var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   public boolean getBoolean(K var1, boolean var2) {
      Boolean var3 = this.getBoolean(var1);
      return var3 != null ? var3 : var2;
   }

   public Byte getByte(K var1) {
      Object var2 = this.get(var1);

      try {
         return var2 != null ? this.valueConverter.convertToByte(var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   public byte getByte(K var1, byte var2) {
      Byte var3 = this.getByte(var1);
      return var3 != null ? var3 : var2;
   }

   public Character getChar(K var1) {
      Object var2 = this.get(var1);

      try {
         return var2 != null ? this.valueConverter.convertToChar(var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   public char getChar(K var1, char var2) {
      Character var3 = this.getChar(var1);
      return var3 != null ? var3 : var2;
   }

   public Short getShort(K var1) {
      Object var2 = this.get(var1);

      try {
         return var2 != null ? this.valueConverter.convertToShort(var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   public short getShort(K var1, short var2) {
      Short var3 = this.getShort(var1);
      return var3 != null ? var3 : var2;
   }

   public Integer getInt(K var1) {
      Object var2 = this.get(var1);

      try {
         return var2 != null ? this.valueConverter.convertToInt(var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   public int getInt(K var1, int var2) {
      Integer var3 = this.getInt(var1);
      return var3 != null ? var3 : var2;
   }

   public Long getLong(K var1) {
      Object var2 = this.get(var1);

      try {
         return var2 != null ? this.valueConverter.convertToLong(var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   public long getLong(K var1, long var2) {
      Long var4 = this.getLong(var1);
      return var4 != null ? var4 : var2;
   }

   public Float getFloat(K var1) {
      Object var2 = this.get(var1);

      try {
         return var2 != null ? this.valueConverter.convertToFloat(var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   public float getFloat(K var1, float var2) {
      Float var3 = this.getFloat(var1);
      return var3 != null ? var3 : var2;
   }

   public Double getDouble(K var1) {
      Object var2 = this.get(var1);

      try {
         return var2 != null ? this.valueConverter.convertToDouble(var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   public double getDouble(K var1, double var2) {
      Double var4 = this.getDouble(var1);
      return var4 != null ? var4 : var2;
   }

   public Long getTimeMillis(K var1) {
      Object var2 = this.get(var1);

      try {
         return var2 != null ? this.valueConverter.convertToTimeMillis(var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   public long getTimeMillis(K var1, long var2) {
      Long var4 = this.getTimeMillis(var1);
      return var4 != null ? var4 : var2;
   }

   public Boolean getBooleanAndRemove(K var1) {
      Object var2 = this.getAndRemove(var1);

      try {
         return var2 != null ? this.valueConverter.convertToBoolean(var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   public boolean getBooleanAndRemove(K var1, boolean var2) {
      Boolean var3 = this.getBooleanAndRemove(var1);
      return var3 != null ? var3 : var2;
   }

   public Byte getByteAndRemove(K var1) {
      Object var2 = this.getAndRemove(var1);

      try {
         return var2 != null ? this.valueConverter.convertToByte(var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   public byte getByteAndRemove(K var1, byte var2) {
      Byte var3 = this.getByteAndRemove(var1);
      return var3 != null ? var3 : var2;
   }

   public Character getCharAndRemove(K var1) {
      Object var2 = this.getAndRemove(var1);

      try {
         return var2 != null ? this.valueConverter.convertToChar(var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   public char getCharAndRemove(K var1, char var2) {
      Character var3 = this.getCharAndRemove(var1);
      return var3 != null ? var3 : var2;
   }

   public Short getShortAndRemove(K var1) {
      Object var2 = this.getAndRemove(var1);

      try {
         return var2 != null ? this.valueConverter.convertToShort(var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   public short getShortAndRemove(K var1, short var2) {
      Short var3 = this.getShortAndRemove(var1);
      return var3 != null ? var3 : var2;
   }

   public Integer getIntAndRemove(K var1) {
      Object var2 = this.getAndRemove(var1);

      try {
         return var2 != null ? this.valueConverter.convertToInt(var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   public int getIntAndRemove(K var1, int var2) {
      Integer var3 = this.getIntAndRemove(var1);
      return var3 != null ? var3 : var2;
   }

   public Long getLongAndRemove(K var1) {
      Object var2 = this.getAndRemove(var1);

      try {
         return var2 != null ? this.valueConverter.convertToLong(var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   public long getLongAndRemove(K var1, long var2) {
      Long var4 = this.getLongAndRemove(var1);
      return var4 != null ? var4 : var2;
   }

   public Float getFloatAndRemove(K var1) {
      Object var2 = this.getAndRemove(var1);

      try {
         return var2 != null ? this.valueConverter.convertToFloat(var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   public float getFloatAndRemove(K var1, float var2) {
      Float var3 = this.getFloatAndRemove(var1);
      return var3 != null ? var3 : var2;
   }

   public Double getDoubleAndRemove(K var1) {
      Object var2 = this.getAndRemove(var1);

      try {
         return var2 != null ? this.valueConverter.convertToDouble(var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   public double getDoubleAndRemove(K var1, double var2) {
      Double var4 = this.getDoubleAndRemove(var1);
      return var4 != null ? var4 : var2;
   }

   public Long getTimeMillisAndRemove(K var1) {
      Object var2 = this.getAndRemove(var1);

      try {
         return var2 != null ? this.valueConverter.convertToTimeMillis(var2) : null;
      } catch (RuntimeException var4) {
         return null;
      }
   }

   public long getTimeMillisAndRemove(K var1, long var2) {
      Long var4 = this.getTimeMillisAndRemove(var1);
      return var4 != null ? var4 : var2;
   }

   public boolean equals(Object var1) {
      return !(var1 instanceof Headers) ? false : this.equals((Headers)var1, HashingStrategy.JAVA_HASHER);
   }

   public int hashCode() {
      return this.hashCode(HashingStrategy.JAVA_HASHER);
   }

   public final boolean equals(Headers<K, V, ?> var1, HashingStrategy<V> var2) {
      if (var1.size() != this.size()) {
         return false;
      } else if (this == var1) {
         return true;
      } else {
         Iterator var3 = this.names().iterator();

         while(var3.hasNext()) {
            Object var4 = var3.next();
            List var5 = var1.getAll(var4);
            List var6 = this.getAll(var4);
            if (var5.size() != var6.size()) {
               return false;
            }

            for(int var7 = 0; var7 < var5.size(); ++var7) {
               if (!var2.equals(var5.get(var7), var6.get(var7))) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public final int hashCode(HashingStrategy<V> var1) {
      int var2 = -1028477387;
      Iterator var3 = this.names().iterator();

      while(var3.hasNext()) {
         Object var4 = var3.next();
         var2 = 31 * var2 + this.hashingStrategy.hashCode(var4);
         List var5 = this.getAll(var4);

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            var2 = 31 * var2 + var1.hashCode(var5.get(var6));
         }
      }

      return var2;
   }

   public String toString() {
      return HeadersUtils.toString(this.getClass(), this.iterator(), this.size());
   }

   protected DefaultHeaders.HeaderEntry<K, V> newHeaderEntry(int var1, K var2, V var3, DefaultHeaders.HeaderEntry<K, V> var4) {
      return new DefaultHeaders.HeaderEntry(var1, var2, var3, var4, this.head);
   }

   protected ValueConverter<V> valueConverter() {
      return this.valueConverter;
   }

   private int index(int var1) {
      return var1 & this.hashMask;
   }

   private void add0(int var1, int var2, K var3, V var4) {
      this.entries[var2] = this.newHeaderEntry(var1, var3, var4, this.entries[var2]);
      ++this.size;
   }

   private V remove0(int var1, int var2, K var3) {
      DefaultHeaders.HeaderEntry var4 = this.entries[var2];
      if (var4 == null) {
         return null;
      } else {
         Object var5 = null;

         for(DefaultHeaders.HeaderEntry var6 = var4.next; var6 != null; var6 = var4.next) {
            if (var6.hash == var1 && this.hashingStrategy.equals(var3, var6.key)) {
               var5 = var6.value;
               var4.next = var6.next;
               var6.remove();
               --this.size;
            } else {
               var4 = var6;
            }
         }

         var4 = this.entries[var2];
         if (var4.hash == var1 && this.hashingStrategy.equals(var3, var4.key)) {
            if (var5 == null) {
               var5 = var4.value;
            }

            this.entries[var2] = var4.next;
            var4.remove();
            --this.size;
         }

         return var5;
      }
   }

   private T thisT() {
      return this;
   }

   public DefaultHeaders<K, V, T> copy() {
      DefaultHeaders var1 = new DefaultHeaders(this.hashingStrategy, this.valueConverter, this.nameValidator, this.entries.length);
      var1.addImpl(this);
      return var1;
   }

   protected static class HeaderEntry<K, V> implements Entry<K, V> {
      protected final int hash;
      protected final K key;
      protected V value;
      protected DefaultHeaders.HeaderEntry<K, V> next;
      protected DefaultHeaders.HeaderEntry<K, V> before;
      protected DefaultHeaders.HeaderEntry<K, V> after;

      protected HeaderEntry(int var1, K var2) {
         super();
         this.hash = var1;
         this.key = var2;
      }

      HeaderEntry(int var1, K var2, V var3, DefaultHeaders.HeaderEntry<K, V> var4, DefaultHeaders.HeaderEntry<K, V> var5) {
         super();
         this.hash = var1;
         this.key = var2;
         this.value = var3;
         this.next = var4;
         this.after = var5;
         this.before = var5.before;
         this.pointNeighborsToThis();
      }

      HeaderEntry() {
         super();
         this.hash = -1;
         this.key = null;
         this.before = this.after = this;
      }

      protected final void pointNeighborsToThis() {
         this.before.after = this;
         this.after.before = this;
      }

      public final DefaultHeaders.HeaderEntry<K, V> before() {
         return this.before;
      }

      public final DefaultHeaders.HeaderEntry<K, V> after() {
         return this.after;
      }

      protected void remove() {
         this.before.after = this.after;
         this.after.before = this.before;
      }

      public final K getKey() {
         return this.key;
      }

      public final V getValue() {
         return this.value;
      }

      public final V setValue(V var1) {
         ObjectUtil.checkNotNull(var1, "value");
         Object var2 = this.value;
         this.value = var1;
         return var2;
      }

      public final String toString() {
         return this.key.toString() + '=' + this.value.toString();
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof Entry)) {
            return false;
         } else {
            boolean var10000;
            label38: {
               label27: {
                  Entry var2 = (Entry)var1;
                  if (this.getKey() == null) {
                     if (var2.getKey() != null) {
                        break label27;
                     }
                  } else if (!this.getKey().equals(var2.getKey())) {
                     break label27;
                  }

                  if (this.getValue() == null) {
                     if (var2.getValue() == null) {
                        break label38;
                     }
                  } else if (this.getValue().equals(var2.getValue())) {
                     break label38;
                  }
               }

               var10000 = false;
               return var10000;
            }

            var10000 = true;
            return var10000;
         }
      }

      public int hashCode() {
         return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
      }
   }

   private final class ValueIterator implements Iterator<V> {
      private final K name;
      private final int hash;
      private DefaultHeaders.HeaderEntry<K, V> next;

      ValueIterator(K var2) {
         super();
         this.name = ObjectUtil.checkNotNull(var2, "name");
         this.hash = DefaultHeaders.this.hashingStrategy.hashCode(var2);
         this.calculateNext(DefaultHeaders.this.entries[DefaultHeaders.this.index(this.hash)]);
      }

      public boolean hasNext() {
         return this.next != null;
      }

      public V next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            DefaultHeaders.HeaderEntry var1 = this.next;
            this.calculateNext(this.next.next);
            return var1.value;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException("read only");
      }

      private void calculateNext(DefaultHeaders.HeaderEntry<K, V> var1) {
         while(var1 != null) {
            if (var1.hash == this.hash && DefaultHeaders.this.hashingStrategy.equals(this.name, var1.key)) {
               this.next = var1;
               return;
            }

            var1 = var1.next;
         }

         this.next = null;
      }
   }

   private final class HeaderIterator implements Iterator<Entry<K, V>> {
      private DefaultHeaders.HeaderEntry<K, V> current;

      private HeaderIterator() {
         super();
         this.current = DefaultHeaders.this.head;
      }

      public boolean hasNext() {
         return this.current.after != DefaultHeaders.this.head;
      }

      public Entry<K, V> next() {
         this.current = this.current.after;
         if (this.current == DefaultHeaders.this.head) {
            throw new NoSuchElementException();
         } else {
            return this.current;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException("read only");
      }

      // $FF: synthetic method
      HeaderIterator(Object var2) {
         this();
      }
   }

   public interface NameValidator<K> {
      DefaultHeaders.NameValidator NOT_NULL = new DefaultHeaders.NameValidator() {
         public void validateName(Object var1) {
            ObjectUtil.checkNotNull(var1, "name");
         }
      };

      void validateName(K var1);
   }
}
