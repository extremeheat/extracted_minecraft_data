package io.netty.handler.codec.http2;

import io.netty.handler.codec.CharSequenceValueConverter;
import io.netty.handler.codec.Headers;
import io.netty.util.AsciiString;
import io.netty.util.HashingStrategy;
import io.netty.util.internal.EmptyArrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

public final class ReadOnlyHttp2Headers implements Http2Headers {
   private static final byte PSEUDO_HEADER_TOKEN = 58;
   private final AsciiString[] pseudoHeaders;
   private final AsciiString[] otherHeaders;

   public static ReadOnlyHttp2Headers trailers(boolean var0, AsciiString... var1) {
      return new ReadOnlyHttp2Headers(var0, EmptyArrays.EMPTY_ASCII_STRINGS, var1);
   }

   public static ReadOnlyHttp2Headers clientHeaders(boolean var0, AsciiString var1, AsciiString var2, AsciiString var3, AsciiString var4, AsciiString... var5) {
      return new ReadOnlyHttp2Headers(var0, new AsciiString[]{Http2Headers.PseudoHeaderName.METHOD.value(), var1, Http2Headers.PseudoHeaderName.PATH.value(), var2, Http2Headers.PseudoHeaderName.SCHEME.value(), var3, Http2Headers.PseudoHeaderName.AUTHORITY.value(), var4}, var5);
   }

   public static ReadOnlyHttp2Headers serverHeaders(boolean var0, AsciiString var1, AsciiString... var2) {
      return new ReadOnlyHttp2Headers(var0, new AsciiString[]{Http2Headers.PseudoHeaderName.STATUS.value(), var1}, var2);
   }

   private ReadOnlyHttp2Headers(boolean var1, AsciiString[] var2, AsciiString... var3) {
      super();

      assert (var2.length & 1) == 0;

      if ((var3.length & 1) != 0) {
         throw newInvalidArraySizeException();
      } else {
         if (var1) {
            validateHeaders(var2, var3);
         }

         this.pseudoHeaders = var2;
         this.otherHeaders = var3;
      }
   }

   private static IllegalArgumentException newInvalidArraySizeException() {
      return new IllegalArgumentException("pseudoHeaders and otherHeaders must be arrays of [name, value] pairs");
   }

   private static void validateHeaders(AsciiString[] var0, AsciiString... var1) {
      for(int var2 = 1; var2 < var0.length; var2 += 2) {
         if (var0[var2] == null) {
            throw new IllegalArgumentException("pseudoHeaders value at index " + var2 + " is null");
         }
      }

      boolean var6 = false;
      int var3 = var1.length - 1;

      for(int var4 = 0; var4 < var3; var4 += 2) {
         AsciiString var5 = var1[var4];
         DefaultHttp2Headers.HTTP2_NAME_VALIDATOR.validateName(var5);
         if (!var6 && !var5.isEmpty() && var5.byteAt(0) != 58) {
            var6 = true;
         } else if (var6 && !var5.isEmpty() && var5.byteAt(0) == 58) {
            throw new IllegalArgumentException("otherHeaders name at index " + var4 + " is a pseudo header that appears after non-pseudo headers.");
         }

         if (var1[var4 + 1] == null) {
            throw new IllegalArgumentException("otherHeaders value at index " + (var4 + 1) + " is null");
         }
      }

   }

   private AsciiString get0(CharSequence var1) {
      int var2 = AsciiString.hashCode(var1);
      int var3 = this.pseudoHeaders.length - 1;

      int var4;
      for(var4 = 0; var4 < var3; var4 += 2) {
         AsciiString var5 = this.pseudoHeaders[var4];
         if (var5.hashCode() == var2 && var5.contentEqualsIgnoreCase(var1)) {
            return this.pseudoHeaders[var4 + 1];
         }
      }

      var4 = this.otherHeaders.length - 1;

      for(int var7 = 0; var7 < var4; var7 += 2) {
         AsciiString var6 = this.otherHeaders[var7];
         if (var6.hashCode() == var2 && var6.contentEqualsIgnoreCase(var1)) {
            return this.otherHeaders[var7 + 1];
         }
      }

      return null;
   }

   public CharSequence get(CharSequence var1) {
      return this.get0(var1);
   }

   public CharSequence get(CharSequence var1, CharSequence var2) {
      CharSequence var3 = this.get(var1);
      return var3 != null ? var3 : var2;
   }

   public CharSequence getAndRemove(CharSequence var1) {
      throw new UnsupportedOperationException("read only");
   }

   public CharSequence getAndRemove(CharSequence var1, CharSequence var2) {
      throw new UnsupportedOperationException("read only");
   }

   public List<CharSequence> getAll(CharSequence var1) {
      int var2 = AsciiString.hashCode(var1);
      ArrayList var3 = new ArrayList();
      int var4 = this.pseudoHeaders.length - 1;

      int var5;
      for(var5 = 0; var5 < var4; var5 += 2) {
         AsciiString var6 = this.pseudoHeaders[var5];
         if (var6.hashCode() == var2 && var6.contentEqualsIgnoreCase(var1)) {
            var3.add(this.pseudoHeaders[var5 + 1]);
         }
      }

      var5 = this.otherHeaders.length - 1;

      for(int var8 = 0; var8 < var5; var8 += 2) {
         AsciiString var7 = this.otherHeaders[var8];
         if (var7.hashCode() == var2 && var7.contentEqualsIgnoreCase(var1)) {
            var3.add(this.otherHeaders[var8 + 1]);
         }
      }

      return var3;
   }

   public List<CharSequence> getAllAndRemove(CharSequence var1) {
      throw new UnsupportedOperationException("read only");
   }

   public Boolean getBoolean(CharSequence var1) {
      AsciiString var2 = this.get0(var1);
      return var2 != null ? CharSequenceValueConverter.INSTANCE.convertToBoolean((CharSequence)var2) : null;
   }

   public boolean getBoolean(CharSequence var1, boolean var2) {
      Boolean var3 = this.getBoolean(var1);
      return var3 != null ? var3 : var2;
   }

   public Byte getByte(CharSequence var1) {
      AsciiString var2 = this.get0(var1);
      return var2 != null ? CharSequenceValueConverter.INSTANCE.convertToByte((CharSequence)var2) : null;
   }

   public byte getByte(CharSequence var1, byte var2) {
      Byte var3 = this.getByte(var1);
      return var3 != null ? var3 : var2;
   }

   public Character getChar(CharSequence var1) {
      AsciiString var2 = this.get0(var1);
      return var2 != null ? CharSequenceValueConverter.INSTANCE.convertToChar((CharSequence)var2) : null;
   }

   public char getChar(CharSequence var1, char var2) {
      Character var3 = this.getChar(var1);
      return var3 != null ? var3 : var2;
   }

   public Short getShort(CharSequence var1) {
      AsciiString var2 = this.get0(var1);
      return var2 != null ? CharSequenceValueConverter.INSTANCE.convertToShort((CharSequence)var2) : null;
   }

   public short getShort(CharSequence var1, short var2) {
      Short var3 = this.getShort(var1);
      return var3 != null ? var3 : var2;
   }

   public Integer getInt(CharSequence var1) {
      AsciiString var2 = this.get0(var1);
      return var2 != null ? CharSequenceValueConverter.INSTANCE.convertToInt((CharSequence)var2) : null;
   }

   public int getInt(CharSequence var1, int var2) {
      Integer var3 = this.getInt(var1);
      return var3 != null ? var3 : var2;
   }

   public Long getLong(CharSequence var1) {
      AsciiString var2 = this.get0(var1);
      return var2 != null ? CharSequenceValueConverter.INSTANCE.convertToLong((CharSequence)var2) : null;
   }

   public long getLong(CharSequence var1, long var2) {
      Long var4 = this.getLong(var1);
      return var4 != null ? var4 : var2;
   }

   public Float getFloat(CharSequence var1) {
      AsciiString var2 = this.get0(var1);
      return var2 != null ? CharSequenceValueConverter.INSTANCE.convertToFloat((CharSequence)var2) : null;
   }

   public float getFloat(CharSequence var1, float var2) {
      Float var3 = this.getFloat(var1);
      return var3 != null ? var3 : var2;
   }

   public Double getDouble(CharSequence var1) {
      AsciiString var2 = this.get0(var1);
      return var2 != null ? CharSequenceValueConverter.INSTANCE.convertToDouble((CharSequence)var2) : null;
   }

   public double getDouble(CharSequence var1, double var2) {
      Double var4 = this.getDouble(var1);
      return var4 != null ? var4 : var2;
   }

   public Long getTimeMillis(CharSequence var1) {
      AsciiString var2 = this.get0(var1);
      return var2 != null ? CharSequenceValueConverter.INSTANCE.convertToTimeMillis((CharSequence)var2) : null;
   }

   public long getTimeMillis(CharSequence var1, long var2) {
      Long var4 = this.getTimeMillis(var1);
      return var4 != null ? var4 : var2;
   }

   public Boolean getBooleanAndRemove(CharSequence var1) {
      throw new UnsupportedOperationException("read only");
   }

   public boolean getBooleanAndRemove(CharSequence var1, boolean var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Byte getByteAndRemove(CharSequence var1) {
      throw new UnsupportedOperationException("read only");
   }

   public byte getByteAndRemove(CharSequence var1, byte var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Character getCharAndRemove(CharSequence var1) {
      throw new UnsupportedOperationException("read only");
   }

   public char getCharAndRemove(CharSequence var1, char var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Short getShortAndRemove(CharSequence var1) {
      throw new UnsupportedOperationException("read only");
   }

   public short getShortAndRemove(CharSequence var1, short var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Integer getIntAndRemove(CharSequence var1) {
      throw new UnsupportedOperationException("read only");
   }

   public int getIntAndRemove(CharSequence var1, int var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Long getLongAndRemove(CharSequence var1) {
      throw new UnsupportedOperationException("read only");
   }

   public long getLongAndRemove(CharSequence var1, long var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Float getFloatAndRemove(CharSequence var1) {
      throw new UnsupportedOperationException("read only");
   }

   public float getFloatAndRemove(CharSequence var1, float var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Double getDoubleAndRemove(CharSequence var1) {
      throw new UnsupportedOperationException("read only");
   }

   public double getDoubleAndRemove(CharSequence var1, double var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Long getTimeMillisAndRemove(CharSequence var1) {
      throw new UnsupportedOperationException("read only");
   }

   public long getTimeMillisAndRemove(CharSequence var1, long var2) {
      throw new UnsupportedOperationException("read only");
   }

   public boolean contains(CharSequence var1) {
      return this.get(var1) != null;
   }

   public boolean contains(CharSequence var1, CharSequence var2) {
      return this.contains(var1, var2, false);
   }

   public boolean containsObject(CharSequence var1, Object var2) {
      return var2 instanceof CharSequence ? this.contains(var1, (CharSequence)var2) : this.contains((CharSequence)var1, (CharSequence)var2.toString());
   }

   public boolean containsBoolean(CharSequence var1, boolean var2) {
      return this.contains((CharSequence)var1, (CharSequence)String.valueOf(var2));
   }

   public boolean containsByte(CharSequence var1, byte var2) {
      return this.contains((CharSequence)var1, (CharSequence)String.valueOf(var2));
   }

   public boolean containsChar(CharSequence var1, char var2) {
      return this.contains((CharSequence)var1, (CharSequence)String.valueOf(var2));
   }

   public boolean containsShort(CharSequence var1, short var2) {
      return this.contains((CharSequence)var1, (CharSequence)String.valueOf(var2));
   }

   public boolean containsInt(CharSequence var1, int var2) {
      return this.contains((CharSequence)var1, (CharSequence)String.valueOf(var2));
   }

   public boolean containsLong(CharSequence var1, long var2) {
      return this.contains((CharSequence)var1, (CharSequence)String.valueOf(var2));
   }

   public boolean containsFloat(CharSequence var1, float var2) {
      return false;
   }

   public boolean containsDouble(CharSequence var1, double var2) {
      return this.contains((CharSequence)var1, (CharSequence)String.valueOf(var2));
   }

   public boolean containsTimeMillis(CharSequence var1, long var2) {
      return this.contains((CharSequence)var1, (CharSequence)String.valueOf(var2));
   }

   public int size() {
      return this.pseudoHeaders.length + this.otherHeaders.length >>> 1;
   }

   public boolean isEmpty() {
      return this.pseudoHeaders.length == 0 && this.otherHeaders.length == 0;
   }

   public Set<CharSequence> names() {
      if (this.isEmpty()) {
         return Collections.emptySet();
      } else {
         LinkedHashSet var1 = new LinkedHashSet(this.size());
         int var2 = this.pseudoHeaders.length - 1;

         int var3;
         for(var3 = 0; var3 < var2; var3 += 2) {
            var1.add(this.pseudoHeaders[var3]);
         }

         var3 = this.otherHeaders.length - 1;

         for(int var4 = 0; var4 < var3; var4 += 2) {
            var1.add(this.otherHeaders[var4]);
         }

         return var1;
      }
   }

   public Http2Headers add(CharSequence var1, CharSequence var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers add(CharSequence var1, Iterable<? extends CharSequence> var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers add(CharSequence var1, CharSequence... var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers addObject(CharSequence var1, Object var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers addObject(CharSequence var1, Iterable<?> var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers addObject(CharSequence var1, Object... var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers addBoolean(CharSequence var1, boolean var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers addByte(CharSequence var1, byte var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers addChar(CharSequence var1, char var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers addShort(CharSequence var1, short var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers addInt(CharSequence var1, int var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers addLong(CharSequence var1, long var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers addFloat(CharSequence var1, float var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers addDouble(CharSequence var1, double var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers addTimeMillis(CharSequence var1, long var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers add(Headers<? extends CharSequence, ? extends CharSequence, ?> var1) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers set(CharSequence var1, CharSequence var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers set(CharSequence var1, Iterable<? extends CharSequence> var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers set(CharSequence var1, CharSequence... var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers setObject(CharSequence var1, Object var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers setObject(CharSequence var1, Iterable<?> var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers setObject(CharSequence var1, Object... var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers setBoolean(CharSequence var1, boolean var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers setByte(CharSequence var1, byte var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers setChar(CharSequence var1, char var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers setShort(CharSequence var1, short var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers setInt(CharSequence var1, int var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers setLong(CharSequence var1, long var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers setFloat(CharSequence var1, float var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers setDouble(CharSequence var1, double var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers setTimeMillis(CharSequence var1, long var2) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers set(Headers<? extends CharSequence, ? extends CharSequence, ?> var1) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers setAll(Headers<? extends CharSequence, ? extends CharSequence, ?> var1) {
      throw new UnsupportedOperationException("read only");
   }

   public boolean remove(CharSequence var1) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers clear() {
      throw new UnsupportedOperationException("read only");
   }

   public Iterator<Entry<CharSequence, CharSequence>> iterator() {
      return new ReadOnlyHttp2Headers.ReadOnlyIterator();
   }

   public Iterator<CharSequence> valueIterator(CharSequence var1) {
      return new ReadOnlyHttp2Headers.ReadOnlyValueIterator(var1);
   }

   public Http2Headers method(CharSequence var1) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers scheme(CharSequence var1) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers authority(CharSequence var1) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers path(CharSequence var1) {
      throw new UnsupportedOperationException("read only");
   }

   public Http2Headers status(CharSequence var1) {
      throw new UnsupportedOperationException("read only");
   }

   public CharSequence method() {
      return this.get((CharSequence)Http2Headers.PseudoHeaderName.METHOD.value());
   }

   public CharSequence scheme() {
      return this.get((CharSequence)Http2Headers.PseudoHeaderName.SCHEME.value());
   }

   public CharSequence authority() {
      return this.get((CharSequence)Http2Headers.PseudoHeaderName.AUTHORITY.value());
   }

   public CharSequence path() {
      return this.get((CharSequence)Http2Headers.PseudoHeaderName.PATH.value());
   }

   public CharSequence status() {
      return this.get((CharSequence)Http2Headers.PseudoHeaderName.STATUS.value());
   }

   public boolean contains(CharSequence var1, CharSequence var2, boolean var3) {
      int var4 = AsciiString.hashCode(var1);
      HashingStrategy var5 = var3 ? AsciiString.CASE_INSENSITIVE_HASHER : AsciiString.CASE_SENSITIVE_HASHER;
      int var6 = var5.hashCode(var2);
      return contains(var1, var4, var2, var6, var5, this.otherHeaders) || contains(var1, var4, var2, var6, var5, this.pseudoHeaders);
   }

   private static boolean contains(CharSequence var0, int var1, CharSequence var2, int var3, HashingStrategy<CharSequence> var4, AsciiString[] var5) {
      int var6 = var5.length - 1;

      for(int var7 = 0; var7 < var6; var7 += 2) {
         AsciiString var8 = var5[var7];
         AsciiString var9 = var5[var7 + 1];
         if (var8.hashCode() == var1 && var9.hashCode() == var3 && var8.contentEqualsIgnoreCase(var0) && var4.equals(var9, var2)) {
            return true;
         }
      }

      return false;
   }

   public String toString() {
      StringBuilder var1 = (new StringBuilder(this.getClass().getSimpleName())).append('[');
      String var2 = "";

      for(Iterator var3 = this.iterator(); var3.hasNext(); var2 = ", ") {
         Entry var4 = (Entry)var3.next();
         var1.append(var2);
         var1.append((CharSequence)var4.getKey()).append(": ").append((CharSequence)var4.getValue());
      }

      return var1.append(']').toString();
   }

   private final class ReadOnlyIterator implements Entry<CharSequence, CharSequence>, Iterator<Entry<CharSequence, CharSequence>> {
      private int i;
      private AsciiString[] current;
      private AsciiString key;
      private AsciiString value;

      private ReadOnlyIterator() {
         super();
         this.current = ReadOnlyHttp2Headers.this.pseudoHeaders.length != 0 ? ReadOnlyHttp2Headers.this.pseudoHeaders : ReadOnlyHttp2Headers.this.otherHeaders;
      }

      public boolean hasNext() {
         return this.i != this.current.length;
      }

      public Entry<CharSequence, CharSequence> next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.key = this.current[this.i];
            this.value = this.current[this.i + 1];
            this.i += 2;
            if (this.i == this.current.length && this.current == ReadOnlyHttp2Headers.this.pseudoHeaders) {
               this.current = ReadOnlyHttp2Headers.this.otherHeaders;
               this.i = 0;
            }

            return this;
         }
      }

      public CharSequence getKey() {
         return this.key;
      }

      public CharSequence getValue() {
         return this.value;
      }

      public CharSequence setValue(CharSequence var1) {
         throw new UnsupportedOperationException("read only");
      }

      public void remove() {
         throw new UnsupportedOperationException("read only");
      }

      public String toString() {
         return this.key.toString() + '=' + this.value.toString();
      }

      // $FF: synthetic method
      ReadOnlyIterator(Object var2) {
         this();
      }
   }

   private final class ReadOnlyValueIterator implements Iterator<CharSequence> {
      private int i;
      private final int nameHash;
      private final CharSequence name;
      private AsciiString[] current;
      private AsciiString next;

      ReadOnlyValueIterator(CharSequence var2) {
         super();
         this.current = ReadOnlyHttp2Headers.this.pseudoHeaders.length != 0 ? ReadOnlyHttp2Headers.this.pseudoHeaders : ReadOnlyHttp2Headers.this.otherHeaders;
         this.nameHash = AsciiString.hashCode(var2);
         this.name = var2;
         this.calculateNext();
      }

      public boolean hasNext() {
         return this.next != null;
      }

      public CharSequence next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            AsciiString var1 = this.next;
            this.calculateNext();
            return var1;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException("read only");
      }

      private void calculateNext() {
         while(this.i < this.current.length) {
            AsciiString var1 = this.current[this.i];
            if (var1.hashCode() == this.nameHash && var1.contentEqualsIgnoreCase(this.name)) {
               this.next = this.current[this.i + 1];
               this.i += 2;
               return;
            }

            this.i += 2;
         }

         if (this.i >= this.current.length && this.current == ReadOnlyHttp2Headers.this.pseudoHeaders) {
            this.i = 0;
            this.current = ReadOnlyHttp2Headers.this.otherHeaders;
            this.calculateNext();
         } else {
            this.next = null;
         }

      }
   }
}
