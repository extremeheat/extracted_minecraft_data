package io.netty.handler.codec.http;

import io.netty.handler.codec.CharSequenceValueConverter;
import io.netty.util.AsciiString;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;

public final class ReadOnlyHttpHeaders extends HttpHeaders {
   private final CharSequence[] nameValuePairs;

   public ReadOnlyHttpHeaders(boolean var1, CharSequence... var2) {
      super();
      if ((var2.length & 1) != 0) {
         throw newInvalidArraySizeException();
      } else {
         if (var1) {
            validateHeaders(var2);
         }

         this.nameValuePairs = var2;
      }
   }

   private static IllegalArgumentException newInvalidArraySizeException() {
      return new IllegalArgumentException("nameValuePairs must be arrays of [name, value] pairs");
   }

   private static void validateHeaders(CharSequence... var0) {
      for(int var1 = 0; var1 < var0.length; var1 += 2) {
         DefaultHttpHeaders.HttpNameValidator.validateName(var0[var1]);
      }

   }

   private CharSequence get0(CharSequence var1) {
      int var2 = AsciiString.hashCode(var1);

      for(int var3 = 0; var3 < this.nameValuePairs.length; var3 += 2) {
         CharSequence var4 = this.nameValuePairs[var3];
         if (AsciiString.hashCode(var4) == var2 && AsciiString.contentEqualsIgnoreCase(var4, var1)) {
            return this.nameValuePairs[var3 + 1];
         }
      }

      return null;
   }

   public String get(String var1) {
      CharSequence var2 = this.get0(var1);
      return var2 == null ? null : var2.toString();
   }

   public Integer getInt(CharSequence var1) {
      CharSequence var2 = this.get0(var1);
      return var2 == null ? null : CharSequenceValueConverter.INSTANCE.convertToInt(var2);
   }

   public int getInt(CharSequence var1, int var2) {
      CharSequence var3 = this.get0(var1);
      return var3 == null ? var2 : CharSequenceValueConverter.INSTANCE.convertToInt(var3);
   }

   public Short getShort(CharSequence var1) {
      CharSequence var2 = this.get0(var1);
      return var2 == null ? null : CharSequenceValueConverter.INSTANCE.convertToShort(var2);
   }

   public short getShort(CharSequence var1, short var2) {
      CharSequence var3 = this.get0(var1);
      return var3 == null ? var2 : CharSequenceValueConverter.INSTANCE.convertToShort(var3);
   }

   public Long getTimeMillis(CharSequence var1) {
      CharSequence var2 = this.get0(var1);
      return var2 == null ? null : CharSequenceValueConverter.INSTANCE.convertToTimeMillis(var2);
   }

   public long getTimeMillis(CharSequence var1, long var2) {
      CharSequence var4 = this.get0(var1);
      return var4 == null ? var2 : CharSequenceValueConverter.INSTANCE.convertToTimeMillis(var4);
   }

   public List<String> getAll(String var1) {
      if (this.isEmpty()) {
         return Collections.emptyList();
      } else {
         int var2 = AsciiString.hashCode(var1);
         ArrayList var3 = new ArrayList(4);

         for(int var4 = 0; var4 < this.nameValuePairs.length; var4 += 2) {
            CharSequence var5 = this.nameValuePairs[var4];
            if (AsciiString.hashCode(var5) == var2 && AsciiString.contentEqualsIgnoreCase(var5, var1)) {
               var3.add(this.nameValuePairs[var4 + 1].toString());
            }
         }

         return var3;
      }
   }

   public List<Entry<String, String>> entries() {
      if (this.isEmpty()) {
         return Collections.emptyList();
      } else {
         ArrayList var1 = new ArrayList(this.size());

         for(int var2 = 0; var2 < this.nameValuePairs.length; var2 += 2) {
            var1.add(new SimpleImmutableEntry(this.nameValuePairs[var2].toString(), this.nameValuePairs[var2 + 1].toString()));
         }

         return var1;
      }
   }

   public boolean contains(String var1) {
      return this.get0(var1) != null;
   }

   public boolean contains(String var1, String var2, boolean var3) {
      return this.containsValue(var1, var2, var3);
   }

   public boolean containsValue(CharSequence var1, CharSequence var2, boolean var3) {
      int var4;
      if (var3) {
         for(var4 = 0; var4 < this.nameValuePairs.length; var4 += 2) {
            if (AsciiString.contentEqualsIgnoreCase(this.nameValuePairs[var4], var1) && AsciiString.contentEqualsIgnoreCase(this.nameValuePairs[var4 + 1], var2)) {
               return true;
            }
         }
      } else {
         for(var4 = 0; var4 < this.nameValuePairs.length; var4 += 2) {
            if (AsciiString.contentEqualsIgnoreCase(this.nameValuePairs[var4], var1) && AsciiString.contentEquals(this.nameValuePairs[var4 + 1], var2)) {
               return true;
            }
         }
      }

      return false;
   }

   public Iterator<String> valueStringIterator(CharSequence var1) {
      return new ReadOnlyHttpHeaders.ReadOnlyStringValueIterator(var1);
   }

   public Iterator<CharSequence> valueCharSequenceIterator(CharSequence var1) {
      return new ReadOnlyHttpHeaders.ReadOnlyValueIterator(var1);
   }

   public Iterator<Entry<String, String>> iterator() {
      return new ReadOnlyHttpHeaders.ReadOnlyStringIterator();
   }

   public Iterator<Entry<CharSequence, CharSequence>> iteratorCharSequence() {
      return new ReadOnlyHttpHeaders.ReadOnlyIterator();
   }

   public boolean isEmpty() {
      return this.nameValuePairs.length == 0;
   }

   public int size() {
      return this.nameValuePairs.length >>> 1;
   }

   public Set<String> names() {
      if (this.isEmpty()) {
         return Collections.emptySet();
      } else {
         LinkedHashSet var1 = new LinkedHashSet(this.size());

         for(int var2 = 0; var2 < this.nameValuePairs.length; var2 += 2) {
            var1.add(this.nameValuePairs[var2].toString());
         }

         return var1;
      }
   }

   public HttpHeaders add(String var1, Object var2) {
      throw new UnsupportedOperationException("read only");
   }

   public HttpHeaders add(String var1, Iterable<?> var2) {
      throw new UnsupportedOperationException("read only");
   }

   public HttpHeaders addInt(CharSequence var1, int var2) {
      throw new UnsupportedOperationException("read only");
   }

   public HttpHeaders addShort(CharSequence var1, short var2) {
      throw new UnsupportedOperationException("read only");
   }

   public HttpHeaders set(String var1, Object var2) {
      throw new UnsupportedOperationException("read only");
   }

   public HttpHeaders set(String var1, Iterable<?> var2) {
      throw new UnsupportedOperationException("read only");
   }

   public HttpHeaders setInt(CharSequence var1, int var2) {
      throw new UnsupportedOperationException("read only");
   }

   public HttpHeaders setShort(CharSequence var1, short var2) {
      throw new UnsupportedOperationException("read only");
   }

   public HttpHeaders remove(String var1) {
      throw new UnsupportedOperationException("read only");
   }

   public HttpHeaders clear() {
      throw new UnsupportedOperationException("read only");
   }

   private final class ReadOnlyValueIterator implements Iterator<CharSequence> {
      private final CharSequence name;
      private final int nameHash;
      private int nextNameIndex;

      ReadOnlyValueIterator(CharSequence var2) {
         super();
         this.name = var2;
         this.nameHash = AsciiString.hashCode(var2);
         this.nextNameIndex = this.findNextValue();
      }

      public boolean hasNext() {
         return this.nextNameIndex != -1;
      }

      public CharSequence next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            CharSequence var1 = ReadOnlyHttpHeaders.this.nameValuePairs[this.nextNameIndex + 1];
            this.nextNameIndex = this.findNextValue();
            return var1;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException("read only");
      }

      private int findNextValue() {
         for(int var1 = this.nextNameIndex; var1 < ReadOnlyHttpHeaders.this.nameValuePairs.length; var1 += 2) {
            CharSequence var2 = ReadOnlyHttpHeaders.this.nameValuePairs[var1];
            if (this.nameHash == AsciiString.hashCode(var2) && AsciiString.contentEqualsIgnoreCase(this.name, var2)) {
               return var1;
            }
         }

         return -1;
      }
   }

   private final class ReadOnlyStringValueIterator implements Iterator<String> {
      private final CharSequence name;
      private final int nameHash;
      private int nextNameIndex;

      ReadOnlyStringValueIterator(CharSequence var2) {
         super();
         this.name = var2;
         this.nameHash = AsciiString.hashCode(var2);
         this.nextNameIndex = this.findNextValue();
      }

      public boolean hasNext() {
         return this.nextNameIndex != -1;
      }

      public String next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            String var1 = ReadOnlyHttpHeaders.this.nameValuePairs[this.nextNameIndex + 1].toString();
            this.nextNameIndex = this.findNextValue();
            return var1;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException("read only");
      }

      private int findNextValue() {
         for(int var1 = this.nextNameIndex; var1 < ReadOnlyHttpHeaders.this.nameValuePairs.length; var1 += 2) {
            CharSequence var2 = ReadOnlyHttpHeaders.this.nameValuePairs[var1];
            if (this.nameHash == AsciiString.hashCode(var2) && AsciiString.contentEqualsIgnoreCase(this.name, var2)) {
               return var1;
            }
         }

         return -1;
      }
   }

   private final class ReadOnlyStringIterator implements Entry<String, String>, Iterator<Entry<String, String>> {
      private String key;
      private String value;
      private int nextNameIndex;

      private ReadOnlyStringIterator() {
         super();
      }

      public boolean hasNext() {
         return this.nextNameIndex != ReadOnlyHttpHeaders.this.nameValuePairs.length;
      }

      public Entry<String, String> next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.key = ReadOnlyHttpHeaders.this.nameValuePairs[this.nextNameIndex].toString();
            this.value = ReadOnlyHttpHeaders.this.nameValuePairs[this.nextNameIndex + 1].toString();
            this.nextNameIndex += 2;
            return this;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException("read only");
      }

      public String getKey() {
         return this.key;
      }

      public String getValue() {
         return this.value;
      }

      public String setValue(String var1) {
         throw new UnsupportedOperationException("read only");
      }

      public String toString() {
         return this.key + '=' + this.value;
      }

      // $FF: synthetic method
      ReadOnlyStringIterator(Object var2) {
         this();
      }
   }

   private final class ReadOnlyIterator implements Entry<CharSequence, CharSequence>, Iterator<Entry<CharSequence, CharSequence>> {
      private CharSequence key;
      private CharSequence value;
      private int nextNameIndex;

      private ReadOnlyIterator() {
         super();
      }

      public boolean hasNext() {
         return this.nextNameIndex != ReadOnlyHttpHeaders.this.nameValuePairs.length;
      }

      public Entry<CharSequence, CharSequence> next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.key = ReadOnlyHttpHeaders.this.nameValuePairs[this.nextNameIndex];
            this.value = ReadOnlyHttpHeaders.this.nameValuePairs[this.nextNameIndex + 1];
            this.nextNameIndex += 2;
            return this;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException("read only");
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

      public String toString() {
         return this.key.toString() + '=' + this.value.toString();
      }

      // $FF: synthetic method
      ReadOnlyIterator(Object var2) {
         this();
      }
   }
}
