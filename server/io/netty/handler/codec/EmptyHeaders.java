package io.netty.handler.codec;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public class EmptyHeaders<K, V, T extends Headers<K, V, T>> implements Headers<K, V, T> {
   public EmptyHeaders() {
      super();
   }

   public V get(K var1) {
      return null;
   }

   public V get(K var1, V var2) {
      return var2;
   }

   public V getAndRemove(K var1) {
      return null;
   }

   public V getAndRemove(K var1, V var2) {
      return var2;
   }

   public List<V> getAll(K var1) {
      return Collections.emptyList();
   }

   public List<V> getAllAndRemove(K var1) {
      return Collections.emptyList();
   }

   public Boolean getBoolean(K var1) {
      return null;
   }

   public boolean getBoolean(K var1, boolean var2) {
      return var2;
   }

   public Byte getByte(K var1) {
      return null;
   }

   public byte getByte(K var1, byte var2) {
      return var2;
   }

   public Character getChar(K var1) {
      return null;
   }

   public char getChar(K var1, char var2) {
      return var2;
   }

   public Short getShort(K var1) {
      return null;
   }

   public short getShort(K var1, short var2) {
      return var2;
   }

   public Integer getInt(K var1) {
      return null;
   }

   public int getInt(K var1, int var2) {
      return var2;
   }

   public Long getLong(K var1) {
      return null;
   }

   public long getLong(K var1, long var2) {
      return var2;
   }

   public Float getFloat(K var1) {
      return null;
   }

   public float getFloat(K var1, float var2) {
      return var2;
   }

   public Double getDouble(K var1) {
      return null;
   }

   public double getDouble(K var1, double var2) {
      return var2;
   }

   public Long getTimeMillis(K var1) {
      return null;
   }

   public long getTimeMillis(K var1, long var2) {
      return var2;
   }

   public Boolean getBooleanAndRemove(K var1) {
      return null;
   }

   public boolean getBooleanAndRemove(K var1, boolean var2) {
      return var2;
   }

   public Byte getByteAndRemove(K var1) {
      return null;
   }

   public byte getByteAndRemove(K var1, byte var2) {
      return var2;
   }

   public Character getCharAndRemove(K var1) {
      return null;
   }

   public char getCharAndRemove(K var1, char var2) {
      return var2;
   }

   public Short getShortAndRemove(K var1) {
      return null;
   }

   public short getShortAndRemove(K var1, short var2) {
      return var2;
   }

   public Integer getIntAndRemove(K var1) {
      return null;
   }

   public int getIntAndRemove(K var1, int var2) {
      return var2;
   }

   public Long getLongAndRemove(K var1) {
      return null;
   }

   public long getLongAndRemove(K var1, long var2) {
      return var2;
   }

   public Float getFloatAndRemove(K var1) {
      return null;
   }

   public float getFloatAndRemove(K var1, float var2) {
      return var2;
   }

   public Double getDoubleAndRemove(K var1) {
      return null;
   }

   public double getDoubleAndRemove(K var1, double var2) {
      return var2;
   }

   public Long getTimeMillisAndRemove(K var1) {
      return null;
   }

   public long getTimeMillisAndRemove(K var1, long var2) {
      return var2;
   }

   public boolean contains(K var1) {
      return false;
   }

   public boolean contains(K var1, V var2) {
      return false;
   }

   public boolean containsObject(K var1, Object var2) {
      return false;
   }

   public boolean containsBoolean(K var1, boolean var2) {
      return false;
   }

   public boolean containsByte(K var1, byte var2) {
      return false;
   }

   public boolean containsChar(K var1, char var2) {
      return false;
   }

   public boolean containsShort(K var1, short var2) {
      return false;
   }

   public boolean containsInt(K var1, int var2) {
      return false;
   }

   public boolean containsLong(K var1, long var2) {
      return false;
   }

   public boolean containsFloat(K var1, float var2) {
      return false;
   }

   public boolean containsDouble(K var1, double var2) {
      return false;
   }

   public boolean containsTimeMillis(K var1, long var2) {
      return false;
   }

   public int size() {
      return 0;
   }

   public boolean isEmpty() {
      return true;
   }

   public Set<K> names() {
      return Collections.emptySet();
   }

   public T add(K var1, V var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T add(K var1, Iterable<? extends V> var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T add(K var1, V... var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T addObject(K var1, Object var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T addObject(K var1, Iterable<?> var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T addObject(K var1, Object... var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T addBoolean(K var1, boolean var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T addByte(K var1, byte var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T addChar(K var1, char var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T addShort(K var1, short var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T addInt(K var1, int var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T addLong(K var1, long var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T addFloat(K var1, float var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T addDouble(K var1, double var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T addTimeMillis(K var1, long var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T add(Headers<? extends K, ? extends V, ?> var1) {
      throw new UnsupportedOperationException("read only");
   }

   public T set(K var1, V var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T set(K var1, Iterable<? extends V> var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T set(K var1, V... var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T setObject(K var1, Object var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T setObject(K var1, Iterable<?> var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T setObject(K var1, Object... var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T setBoolean(K var1, boolean var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T setByte(K var1, byte var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T setChar(K var1, char var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T setShort(K var1, short var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T setInt(K var1, int var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T setLong(K var1, long var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T setFloat(K var1, float var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T setDouble(K var1, double var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T setTimeMillis(K var1, long var2) {
      throw new UnsupportedOperationException("read only");
   }

   public T set(Headers<? extends K, ? extends V, ?> var1) {
      throw new UnsupportedOperationException("read only");
   }

   public T setAll(Headers<? extends K, ? extends V, ?> var1) {
      throw new UnsupportedOperationException("read only");
   }

   public boolean remove(K var1) {
      return false;
   }

   public T clear() {
      return this.thisT();
   }

   public Iterator<V> valueIterator(K var1) {
      List var2 = Collections.emptyList();
      return var2.iterator();
   }

   public Iterator<Entry<K, V>> iterator() {
      List var1 = Collections.emptyList();
      return var1.iterator();
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Headers)) {
         return false;
      } else {
         Headers var2 = (Headers)var1;
         return this.isEmpty() && var2.isEmpty();
      }
   }

   public int hashCode() {
      return -1028477387;
   }

   public String toString() {
      return this.getClass().getSimpleName() + '[' + ']';
   }

   private T thisT() {
      return this;
   }
}
