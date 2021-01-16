package io.netty.util;

import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;

public final class AsciiString implements CharSequence, Comparable<CharSequence> {
   public static final AsciiString EMPTY_STRING = cached("");
   private static final char MAX_CHAR_VALUE = '\u00ff';
   public static final int INDEX_NOT_FOUND = -1;
   private final byte[] value;
   private final int offset;
   private final int length;
   private int hash;
   private String string;
   public static final HashingStrategy<CharSequence> CASE_INSENSITIVE_HASHER = new HashingStrategy<CharSequence>() {
      public int hashCode(CharSequence var1) {
         return AsciiString.hashCode(var1);
      }

      public boolean equals(CharSequence var1, CharSequence var2) {
         return AsciiString.contentEqualsIgnoreCase(var1, var2);
      }
   };
   public static final HashingStrategy<CharSequence> CASE_SENSITIVE_HASHER = new HashingStrategy<CharSequence>() {
      public int hashCode(CharSequence var1) {
         return AsciiString.hashCode(var1);
      }

      public boolean equals(CharSequence var1, CharSequence var2) {
         return AsciiString.contentEquals(var1, var2);
      }
   };

   public AsciiString(byte[] var1) {
      this(var1, true);
   }

   public AsciiString(byte[] var1, boolean var2) {
      this((byte[])var1, 0, var1.length, var2);
   }

   public AsciiString(byte[] var1, int var2, int var3, boolean var4) {
      super();
      if (var4) {
         this.value = Arrays.copyOfRange(var1, var2, var2 + var3);
         this.offset = 0;
      } else {
         if (MathUtil.isOutOfBounds(var2, var3, var1.length)) {
            throw new IndexOutOfBoundsException("expected: 0 <= start(" + var2 + ") <= start + length(" + var3 + ") <= value.length(" + var1.length + ')');
         }

         this.value = var1;
         this.offset = var2;
      }

      this.length = var3;
   }

   public AsciiString(ByteBuffer var1) {
      this(var1, true);
   }

   public AsciiString(ByteBuffer var1, boolean var2) {
      this(var1, var1.position(), var1.remaining(), var2);
   }

   public AsciiString(ByteBuffer var1, int var2, int var3, boolean var4) {
      super();
      if (MathUtil.isOutOfBounds(var2, var3, var1.capacity())) {
         throw new IndexOutOfBoundsException("expected: 0 <= start(" + var2 + ") <= start + length(" + var3 + ") <= value.capacity(" + var1.capacity() + ')');
      } else {
         int var5;
         if (var1.hasArray()) {
            if (var4) {
               var5 = var1.arrayOffset() + var2;
               this.value = Arrays.copyOfRange(var1.array(), var5, var5 + var3);
               this.offset = 0;
            } else {
               this.value = var1.array();
               this.offset = var2;
            }
         } else {
            this.value = new byte[var3];
            var5 = var1.position();
            var1.get(this.value, 0, var3);
            var1.position(var5);
            this.offset = 0;
         }

         this.length = var3;
      }
   }

   public AsciiString(char[] var1) {
      this((char[])var1, 0, var1.length);
   }

   public AsciiString(char[] var1, int var2, int var3) {
      super();
      if (MathUtil.isOutOfBounds(var2, var3, var1.length)) {
         throw new IndexOutOfBoundsException("expected: 0 <= start(" + var2 + ") <= start + length(" + var3 + ") <= value.length(" + var1.length + ')');
      } else {
         this.value = new byte[var3];
         int var4 = 0;

         for(int var5 = var2; var4 < var3; ++var5) {
            this.value[var4] = c2b(var1[var5]);
            ++var4;
         }

         this.offset = 0;
         this.length = var3;
      }
   }

   public AsciiString(char[] var1, Charset var2) {
      this((char[])var1, var2, 0, var1.length);
   }

   public AsciiString(char[] var1, Charset var2, int var3, int var4) {
      super();
      CharBuffer var5 = CharBuffer.wrap(var1, var3, var4);
      CharsetEncoder var6 = CharsetUtil.encoder(var2);
      ByteBuffer var7 = ByteBuffer.allocate((int)(var6.maxBytesPerChar() * (float)var4));
      var6.encode(var5, var7, true);
      int var8 = var7.arrayOffset();
      this.value = Arrays.copyOfRange(var7.array(), var8, var8 + var7.position());
      this.offset = 0;
      this.length = this.value.length;
   }

   public AsciiString(CharSequence var1) {
      this((CharSequence)var1, 0, var1.length());
   }

   public AsciiString(CharSequence var1, int var2, int var3) {
      super();
      if (MathUtil.isOutOfBounds(var2, var3, var1.length())) {
         throw new IndexOutOfBoundsException("expected: 0 <= start(" + var2 + ") <= start + length(" + var3 + ") <= value.length(" + var1.length() + ')');
      } else {
         this.value = new byte[var3];
         int var4 = 0;

         for(int var5 = var2; var4 < var3; ++var5) {
            this.value[var4] = c2b(var1.charAt(var5));
            ++var4;
         }

         this.offset = 0;
         this.length = var3;
      }
   }

   public AsciiString(CharSequence var1, Charset var2) {
      this((CharSequence)var1, var2, 0, var1.length());
   }

   public AsciiString(CharSequence var1, Charset var2, int var3, int var4) {
      super();
      CharBuffer var5 = CharBuffer.wrap(var1, var3, var3 + var4);
      CharsetEncoder var6 = CharsetUtil.encoder(var2);
      ByteBuffer var7 = ByteBuffer.allocate((int)(var6.maxBytesPerChar() * (float)var4));
      var6.encode(var5, var7, true);
      int var8 = var7.arrayOffset();
      this.value = Arrays.copyOfRange(var7.array(), var8, var8 + var7.position());
      this.offset = 0;
      this.length = this.value.length;
   }

   public int forEachByte(ByteProcessor var1) throws Exception {
      return this.forEachByte0(0, this.length(), var1);
   }

   public int forEachByte(int var1, int var2, ByteProcessor var3) throws Exception {
      if (MathUtil.isOutOfBounds(var1, var2, this.length())) {
         throw new IndexOutOfBoundsException("expected: 0 <= index(" + var1 + ") <= start + length(" + var2 + ") <= length(" + this.length() + ')');
      } else {
         return this.forEachByte0(var1, var2, var3);
      }
   }

   private int forEachByte0(int var1, int var2, ByteProcessor var3) throws Exception {
      int var4 = this.offset + var1 + var2;

      for(int var5 = this.offset + var1; var5 < var4; ++var5) {
         if (!var3.process(this.value[var5])) {
            return var5 - this.offset;
         }
      }

      return -1;
   }

   public int forEachByteDesc(ByteProcessor var1) throws Exception {
      return this.forEachByteDesc0(0, this.length(), var1);
   }

   public int forEachByteDesc(int var1, int var2, ByteProcessor var3) throws Exception {
      if (MathUtil.isOutOfBounds(var1, var2, this.length())) {
         throw new IndexOutOfBoundsException("expected: 0 <= index(" + var1 + ") <= start + length(" + var2 + ") <= length(" + this.length() + ')');
      } else {
         return this.forEachByteDesc0(var1, var2, var3);
      }
   }

   private int forEachByteDesc0(int var1, int var2, ByteProcessor var3) throws Exception {
      int var4 = this.offset + var1;

      for(int var5 = this.offset + var1 + var2 - 1; var5 >= var4; --var5) {
         if (!var3.process(this.value[var5])) {
            return var5 - this.offset;
         }
      }

      return -1;
   }

   public byte byteAt(int var1) {
      if (var1 >= 0 && var1 < this.length) {
         return PlatformDependent.hasUnsafe() ? PlatformDependent.getByte(this.value, var1 + this.offset) : this.value[var1 + this.offset];
      } else {
         throw new IndexOutOfBoundsException("index: " + var1 + " must be in the range [0," + this.length + ")");
      }
   }

   public boolean isEmpty() {
      return this.length == 0;
   }

   public int length() {
      return this.length;
   }

   public void arrayChanged() {
      this.string = null;
      this.hash = 0;
   }

   public byte[] array() {
      return this.value;
   }

   public int arrayOffset() {
      return this.offset;
   }

   public boolean isEntireArrayUsed() {
      return this.offset == 0 && this.length == this.value.length;
   }

   public byte[] toByteArray() {
      return this.toByteArray(0, this.length());
   }

   public byte[] toByteArray(int var1, int var2) {
      return Arrays.copyOfRange(this.value, var1 + this.offset, var2 + this.offset);
   }

   public void copy(int var1, byte[] var2, int var3, int var4) {
      if (MathUtil.isOutOfBounds(var1, var4, this.length())) {
         throw new IndexOutOfBoundsException("expected: 0 <= srcIdx(" + var1 + ") <= srcIdx + length(" + var4 + ") <= srcLen(" + this.length() + ')');
      } else {
         System.arraycopy(this.value, var1 + this.offset, ObjectUtil.checkNotNull(var2, "dst"), var3, var4);
      }
   }

   public char charAt(int var1) {
      return b2c(this.byteAt(var1));
   }

   public boolean contains(CharSequence var1) {
      return this.indexOf(var1) >= 0;
   }

   public int compareTo(CharSequence var1) {
      if (this == var1) {
         return 0;
      } else {
         int var3 = this.length();
         int var4 = var1.length();
         int var5 = Math.min(var3, var4);
         int var6 = 0;

         for(int var7 = this.arrayOffset(); var6 < var5; ++var7) {
            int var2 = b2c(this.value[var7]) - var1.charAt(var6);
            if (var2 != 0) {
               return var2;
            }

            ++var6;
         }

         return var3 - var4;
      }
   }

   public AsciiString concat(CharSequence var1) {
      int var2 = this.length();
      int var3 = var1.length();
      if (var3 == 0) {
         return this;
      } else if (var1.getClass() == AsciiString.class) {
         AsciiString var7 = (AsciiString)var1;
         if (this.isEmpty()) {
            return var7;
         } else {
            byte[] var8 = new byte[var2 + var3];
            System.arraycopy(this.value, this.arrayOffset(), var8, 0, var2);
            System.arraycopy(var7.value, var7.arrayOffset(), var8, var2, var3);
            return new AsciiString(var8, false);
         }
      } else if (this.isEmpty()) {
         return new AsciiString(var1);
      } else {
         byte[] var4 = new byte[var2 + var3];
         System.arraycopy(this.value, this.arrayOffset(), var4, 0, var2);
         int var5 = var2;

         for(int var6 = 0; var5 < var4.length; ++var6) {
            var4[var5] = c2b(var1.charAt(var6));
            ++var5;
         }

         return new AsciiString(var4, false);
      }
   }

   public boolean endsWith(CharSequence var1) {
      int var2 = var1.length();
      return this.regionMatches(this.length() - var2, var1, 0, var2);
   }

   public boolean contentEqualsIgnoreCase(CharSequence var1) {
      if (var1 != null && var1.length() == this.length()) {
         int var3;
         if (var1.getClass() == AsciiString.class) {
            AsciiString var5 = (AsciiString)var1;
            var3 = this.arrayOffset();

            for(int var4 = var5.arrayOffset(); var3 < this.length(); ++var4) {
               if (!equalsIgnoreCase(this.value[var3], var5.value[var4])) {
                  return false;
               }

               ++var3;
            }

            return true;
         } else {
            int var2 = this.arrayOffset();

            for(var3 = 0; var2 < this.length(); ++var3) {
               if (!equalsIgnoreCase(b2c(this.value[var2]), var1.charAt(var3))) {
                  return false;
               }

               ++var2;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public char[] toCharArray() {
      return this.toCharArray(0, this.length());
   }

   public char[] toCharArray(int var1, int var2) {
      int var3 = var2 - var1;
      if (var3 == 0) {
         return EmptyArrays.EMPTY_CHARS;
      } else if (MathUtil.isOutOfBounds(var1, var3, this.length())) {
         throw new IndexOutOfBoundsException("expected: 0 <= start(" + var1 + ") <= srcIdx + length(" + var3 + ") <= srcLen(" + this.length() + ')');
      } else {
         char[] var4 = new char[var3];
         int var5 = 0;

         for(int var6 = var1 + this.arrayOffset(); var5 < var3; ++var6) {
            var4[var5] = b2c(this.value[var6]);
            ++var5;
         }

         return var4;
      }
   }

   public void copy(int var1, char[] var2, int var3, int var4) {
      if (var2 == null) {
         throw new NullPointerException("dst");
      } else if (MathUtil.isOutOfBounds(var1, var4, this.length())) {
         throw new IndexOutOfBoundsException("expected: 0 <= srcIdx(" + var1 + ") <= srcIdx + length(" + var4 + ") <= srcLen(" + this.length() + ')');
      } else {
         int var5 = var3 + var4;
         int var6 = var3;

         for(int var7 = var1 + this.arrayOffset(); var6 < var5; ++var7) {
            var2[var6] = b2c(this.value[var7]);
            ++var6;
         }

      }
   }

   public AsciiString subSequence(int var1) {
      return this.subSequence(var1, this.length());
   }

   public AsciiString subSequence(int var1, int var2) {
      return this.subSequence(var1, var2, true);
   }

   public AsciiString subSequence(int var1, int var2, boolean var3) {
      if (MathUtil.isOutOfBounds(var1, var2 - var1, this.length())) {
         throw new IndexOutOfBoundsException("expected: 0 <= start(" + var1 + ") <= end (" + var2 + ") <= length(" + this.length() + ')');
      } else if (var1 == 0 && var2 == this.length()) {
         return this;
      } else {
         return var2 == var1 ? EMPTY_STRING : new AsciiString(this.value, var1 + this.offset, var2 - var1, var3);
      }
   }

   public int indexOf(CharSequence var1) {
      return this.indexOf(var1, 0);
   }

   public int indexOf(CharSequence var1, int var2) {
      int var3 = var1.length();
      if (var2 < 0) {
         var2 = 0;
      }

      if (var3 <= 0) {
         return var2 < this.length ? var2 : this.length;
      } else if (var3 > this.length - var2) {
         return -1;
      } else {
         char var4 = var1.charAt(0);
         if (var4 > 255) {
            return -1;
         } else {
            byte var5 = c2b0(var4);
            int var6 = this.offset + this.length - var3;

            for(int var7 = var2 + this.offset; var7 <= var6; ++var7) {
               if (this.value[var7] == var5) {
                  int var8 = var7;
                  int var9 = 0;

                  do {
                     ++var9;
                     if (var9 >= var3) {
                        break;
                     }

                     ++var8;
                  } while(b2c(this.value[var8]) == var1.charAt(var9));

                  if (var9 == var3) {
                     return var7 - this.offset;
                  }
               }
            }

            return -1;
         }
      }
   }

   public int indexOf(char var1, int var2) {
      if (var1 > 255) {
         return -1;
      } else {
         if (var2 < 0) {
            var2 = 0;
         }

         byte var3 = c2b0(var1);
         int var4 = this.offset + var2 + this.length;

         for(int var5 = var2 + this.offset; var5 < var4; ++var5) {
            if (this.value[var5] == var3) {
               return var5 - this.offset;
            }
         }

         return -1;
      }
   }

   public int lastIndexOf(CharSequence var1) {
      return this.lastIndexOf(var1, this.length());
   }

   public int lastIndexOf(CharSequence var1, int var2) {
      int var3 = var1.length();
      if (var2 < 0) {
         var2 = 0;
      }

      if (var3 <= 0) {
         return var2 < this.length ? var2 : this.length;
      } else if (var3 > this.length - var2) {
         return -1;
      } else {
         char var4 = var1.charAt(0);
         if (var4 > 255) {
            return -1;
         } else {
            byte var5 = c2b0(var4);
            int var6 = this.offset + var2;

            for(int var7 = this.offset + this.length - var3; var7 >= var6; --var7) {
               if (this.value[var7] == var5) {
                  int var8 = var7;
                  int var9 = 0;

                  do {
                     ++var9;
                     if (var9 >= var3) {
                        break;
                     }

                     ++var8;
                  } while(b2c(this.value[var8]) == var1.charAt(var9));

                  if (var9 == var3) {
                     return var7 - this.offset;
                  }
               }
            }

            return -1;
         }
      }
   }

   public boolean regionMatches(int var1, CharSequence var2, int var3, int var4) {
      if (var2 == null) {
         throw new NullPointerException("string");
      } else if (var3 >= 0 && var2.length() - var3 >= var4) {
         int var5 = this.length();
         if (var1 >= 0 && var5 - var1 >= var4) {
            if (var4 <= 0) {
               return true;
            } else {
               int var6 = var3 + var4;
               int var7 = var3;

               for(int var8 = var1 + this.arrayOffset(); var7 < var6; ++var8) {
                  if (b2c(this.value[var8]) != var2.charAt(var7)) {
                     return false;
                  }

                  ++var7;
               }

               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean regionMatches(boolean var1, int var2, CharSequence var3, int var4, int var5) {
      if (!var1) {
         return this.regionMatches(var2, var3, var4, var5);
      } else if (var3 == null) {
         throw new NullPointerException("string");
      } else {
         int var6 = this.length();
         if (var2 >= 0 && var5 <= var6 - var2) {
            if (var4 >= 0 && var5 <= var3.length() - var4) {
               var2 += this.arrayOffset();
               int var7 = var2 + var5;

               do {
                  if (var2 >= var7) {
                     return true;
                  }
               } while(equalsIgnoreCase(b2c(this.value[var2++]), var3.charAt(var4++)));

               return false;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public AsciiString replace(char var1, char var2) {
      if (var1 > 255) {
         return this;
      } else {
         byte var3 = c2b0(var1);
         byte var4 = c2b(var2);
         int var5 = this.offset + this.length;

         for(int var6 = this.offset; var6 < var5; ++var6) {
            if (this.value[var6] == var3) {
               byte[] var7 = new byte[this.length()];
               System.arraycopy(this.value, this.offset, var7, 0, var6 - this.offset);
               var7[var6 - this.offset] = var4;
               ++var6;

               while(var6 < var5) {
                  byte var8 = this.value[var6];
                  var7[var6 - this.offset] = var8 != var3 ? var8 : var4;
                  ++var6;
               }

               return new AsciiString(var7, false);
            }
         }

         return this;
      }
   }

   public boolean startsWith(CharSequence var1) {
      return this.startsWith(var1, 0);
   }

   public boolean startsWith(CharSequence var1, int var2) {
      return this.regionMatches(var2, var1, 0, var1.length());
   }

   public AsciiString toLowerCase() {
      boolean var1 = true;
      int var4 = this.length() + this.arrayOffset();

      int var2;
      for(var2 = this.arrayOffset(); var2 < var4; ++var2) {
         byte var5 = this.value[var2];
         if (var5 >= 65 && var5 <= 90) {
            var1 = false;
            break;
         }
      }

      if (var1) {
         return this;
      } else {
         byte[] var6 = new byte[this.length()];
         var2 = 0;

         for(int var3 = this.arrayOffset(); var2 < var6.length; ++var3) {
            var6[var2] = toLowerCase(this.value[var3]);
            ++var2;
         }

         return new AsciiString(var6, false);
      }
   }

   public AsciiString toUpperCase() {
      boolean var1 = true;
      int var4 = this.length() + this.arrayOffset();

      int var2;
      for(var2 = this.arrayOffset(); var2 < var4; ++var2) {
         byte var5 = this.value[var2];
         if (var5 >= 97 && var5 <= 122) {
            var1 = false;
            break;
         }
      }

      if (var1) {
         return this;
      } else {
         byte[] var6 = new byte[this.length()];
         var2 = 0;

         for(int var3 = this.arrayOffset(); var2 < var6.length; ++var3) {
            var6[var2] = toUpperCase(this.value[var3]);
            ++var2;
         }

         return new AsciiString(var6, false);
      }
   }

   public static CharSequence trim(CharSequence var0) {
      if (var0.getClass() == AsciiString.class) {
         return ((AsciiString)var0).trim();
      } else if (var0 instanceof String) {
         return ((String)var0).trim();
      } else {
         int var1 = 0;
         int var2 = var0.length() - 1;

         int var3;
         for(var3 = var2; var1 <= var3 && var0.charAt(var1) <= ' '; ++var1) {
         }

         while(var3 >= var1 && var0.charAt(var3) <= ' ') {
            --var3;
         }

         return var1 == 0 && var3 == var2 ? var0 : var0.subSequence(var1, var3);
      }
   }

   public AsciiString trim() {
      int var1 = this.arrayOffset();
      int var2 = this.arrayOffset() + this.length() - 1;

      int var3;
      for(var3 = var2; var1 <= var3 && this.value[var1] <= 32; ++var1) {
      }

      while(var3 >= var1 && this.value[var3] <= 32) {
         --var3;
      }

      return var1 == 0 && var3 == var2 ? this : new AsciiString(this.value, var1, var3 - var1 + 1, false);
   }

   public boolean contentEquals(CharSequence var1) {
      if (var1 != null && var1.length() == this.length()) {
         if (var1.getClass() == AsciiString.class) {
            return this.equals(var1);
         } else {
            int var2 = this.arrayOffset();

            for(int var3 = 0; var3 < var1.length(); ++var3) {
               if (b2c(this.value[var2]) != var1.charAt(var3)) {
                  return false;
               }

               ++var2;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public boolean matches(String var1) {
      return Pattern.matches(var1, this);
   }

   public AsciiString[] split(String var1, int var2) {
      return toAsciiStringArray(Pattern.compile(var1).split(this, var2));
   }

   public AsciiString[] split(char var1) {
      ArrayList var2 = InternalThreadLocalMap.get().arrayList();
      int var3 = 0;
      int var4 = this.length();

      int var5;
      for(var5 = var3; var5 < var4; ++var5) {
         if (this.charAt(var5) == var1) {
            if (var3 == var5) {
               var2.add(EMPTY_STRING);
            } else {
               var2.add(new AsciiString(this.value, var3 + this.arrayOffset(), var5 - var3, false));
            }

            var3 = var5 + 1;
         }
      }

      if (var3 == 0) {
         var2.add(this);
      } else if (var3 != var4) {
         var2.add(new AsciiString(this.value, var3 + this.arrayOffset(), var4 - var3, false));
      } else {
         for(var5 = var2.size() - 1; var5 >= 0 && ((AsciiString)var2.get(var5)).isEmpty(); --var5) {
            var2.remove(var5);
         }
      }

      return (AsciiString[])var2.toArray(new AsciiString[var2.size()]);
   }

   public int hashCode() {
      int var1 = this.hash;
      if (var1 == 0) {
         var1 = PlatformDependent.hashCodeAscii(this.value, this.offset, this.length);
         this.hash = var1;
      }

      return var1;
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1.getClass() == AsciiString.class) {
         if (this == var1) {
            return true;
         } else {
            AsciiString var2 = (AsciiString)var1;
            return this.length() == var2.length() && this.hashCode() == var2.hashCode() && PlatformDependent.equals(this.array(), this.arrayOffset(), var2.array(), var2.arrayOffset(), this.length());
         }
      } else {
         return false;
      }
   }

   public String toString() {
      String var1 = this.string;
      if (var1 == null) {
         var1 = this.toString(0);
         this.string = var1;
      }

      return var1;
   }

   public String toString(int var1) {
      return this.toString(var1, this.length());
   }

   public String toString(int var1, int var2) {
      int var3 = var2 - var1;
      if (var3 == 0) {
         return "";
      } else if (MathUtil.isOutOfBounds(var1, var3, this.length())) {
         throw new IndexOutOfBoundsException("expected: 0 <= start(" + var1 + ") <= srcIdx + length(" + var3 + ") <= srcLen(" + this.length() + ')');
      } else {
         String var4 = new String(this.value, 0, var1 + this.offset, var3);
         return var4;
      }
   }

   public boolean parseBoolean() {
      return this.length >= 1 && this.value[this.offset] != 0;
   }

   public char parseChar() {
      return this.parseChar(0);
   }

   public char parseChar(int var1) {
      if (var1 + 1 >= this.length()) {
         throw new IndexOutOfBoundsException("2 bytes required to convert to character. index " + var1 + " would go out of bounds.");
      } else {
         int var2 = var1 + this.offset;
         return (char)(b2c(this.value[var2]) << 8 | b2c(this.value[var2 + 1]));
      }
   }

   public short parseShort() {
      return this.parseShort(0, this.length(), 10);
   }

   public short parseShort(int var1) {
      return this.parseShort(0, this.length(), var1);
   }

   public short parseShort(int var1, int var2) {
      return this.parseShort(var1, var2, 10);
   }

   public short parseShort(int var1, int var2, int var3) {
      int var4 = this.parseInt(var1, var2, var3);
      short var5 = (short)var4;
      if (var5 != var4) {
         throw new NumberFormatException(this.subSequence(var1, var2, false).toString());
      } else {
         return var5;
      }
   }

   public int parseInt() {
      return this.parseInt(0, this.length(), 10);
   }

   public int parseInt(int var1) {
      return this.parseInt(0, this.length(), var1);
   }

   public int parseInt(int var1, int var2) {
      return this.parseInt(var1, var2, 10);
   }

   public int parseInt(int var1, int var2, int var3) {
      if (var3 >= 2 && var3 <= 36) {
         if (var1 == var2) {
            throw new NumberFormatException();
         } else {
            int var4 = var1;
            boolean var5 = this.byteAt(var1) == 45;
            if (var5) {
               var4 = var1 + 1;
               if (var4 == var2) {
                  throw new NumberFormatException(this.subSequence(var1, var2, false).toString());
               }
            }

            return this.parseInt(var4, var2, var3, var5);
         }
      } else {
         throw new NumberFormatException();
      }
   }

   private int parseInt(int var1, int var2, int var3, boolean var4) {
      int var5 = -2147483648 / var3;
      int var6 = 0;

      int var9;
      for(int var7 = var1; var7 < var2; var6 = var9) {
         int var8 = Character.digit((char)(this.value[var7++ + this.offset] & 255), var3);
         if (var8 == -1) {
            throw new NumberFormatException(this.subSequence(var1, var2, false).toString());
         }

         if (var5 > var6) {
            throw new NumberFormatException(this.subSequence(var1, var2, false).toString());
         }

         var9 = var6 * var3 - var8;
         if (var9 > var6) {
            throw new NumberFormatException(this.subSequence(var1, var2, false).toString());
         }
      }

      if (!var4) {
         var6 = -var6;
         if (var6 < 0) {
            throw new NumberFormatException(this.subSequence(var1, var2, false).toString());
         }
      }

      return var6;
   }

   public long parseLong() {
      return this.parseLong(0, this.length(), 10);
   }

   public long parseLong(int var1) {
      return this.parseLong(0, this.length(), var1);
   }

   public long parseLong(int var1, int var2) {
      return this.parseLong(var1, var2, 10);
   }

   public long parseLong(int var1, int var2, int var3) {
      if (var3 >= 2 && var3 <= 36) {
         if (var1 == var2) {
            throw new NumberFormatException();
         } else {
            int var4 = var1;
            boolean var5 = this.byteAt(var1) == 45;
            if (var5) {
               var4 = var1 + 1;
               if (var4 == var2) {
                  throw new NumberFormatException(this.subSequence(var1, var2, false).toString());
               }
            }

            return this.parseLong(var4, var2, var3, var5);
         }
      } else {
         throw new NumberFormatException();
      }
   }

   private long parseLong(int var1, int var2, int var3, boolean var4) {
      long var5 = -9223372036854775808L / (long)var3;
      long var7 = 0L;

      long var11;
      for(int var9 = var1; var9 < var2; var7 = var11) {
         int var10 = Character.digit((char)(this.value[var9++ + this.offset] & 255), var3);
         if (var10 == -1) {
            throw new NumberFormatException(this.subSequence(var1, var2, false).toString());
         }

         if (var5 > var7) {
            throw new NumberFormatException(this.subSequence(var1, var2, false).toString());
         }

         var11 = var7 * (long)var3 - (long)var10;
         if (var11 > var7) {
            throw new NumberFormatException(this.subSequence(var1, var2, false).toString());
         }
      }

      if (!var4) {
         var7 = -var7;
         if (var7 < 0L) {
            throw new NumberFormatException(this.subSequence(var1, var2, false).toString());
         }
      }

      return var7;
   }

   public float parseFloat() {
      return this.parseFloat(0, this.length());
   }

   public float parseFloat(int var1, int var2) {
      return Float.parseFloat(this.toString(var1, var2));
   }

   public double parseDouble() {
      return this.parseDouble(0, this.length());
   }

   public double parseDouble(int var1, int var2) {
      return Double.parseDouble(this.toString(var1, var2));
   }

   public static AsciiString of(CharSequence var0) {
      return var0.getClass() == AsciiString.class ? (AsciiString)var0 : new AsciiString(var0);
   }

   public static AsciiString cached(String var0) {
      AsciiString var1 = new AsciiString(var0);
      var1.string = var0;
      return var1;
   }

   public static int hashCode(CharSequence var0) {
      if (var0 == null) {
         return 0;
      } else {
         return var0.getClass() == AsciiString.class ? var0.hashCode() : PlatformDependent.hashCodeAscii(var0);
      }
   }

   public static boolean contains(CharSequence var0, CharSequence var1) {
      return contains(var0, var1, AsciiString.DefaultCharEqualityComparator.INSTANCE);
   }

   public static boolean containsIgnoreCase(CharSequence var0, CharSequence var1) {
      return contains(var0, var1, AsciiString.AsciiCaseInsensitiveCharEqualityComparator.INSTANCE);
   }

   public static boolean contentEqualsIgnoreCase(CharSequence var0, CharSequence var1) {
      if (var0 != null && var1 != null) {
         if (var0.getClass() == AsciiString.class) {
            return ((AsciiString)var0).contentEqualsIgnoreCase(var1);
         } else if (var1.getClass() == AsciiString.class) {
            return ((AsciiString)var1).contentEqualsIgnoreCase(var0);
         } else if (var0.length() != var1.length()) {
            return false;
         } else {
            int var2 = 0;

            for(int var3 = 0; var2 < var0.length(); ++var3) {
               if (!equalsIgnoreCase(var0.charAt(var2), var1.charAt(var3))) {
                  return false;
               }

               ++var2;
            }

            return true;
         }
      } else {
         return var0 == var1;
      }
   }

   public static boolean containsContentEqualsIgnoreCase(Collection<CharSequence> var0, CharSequence var1) {
      Iterator var2 = var0.iterator();

      CharSequence var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (CharSequence)var2.next();
      } while(!contentEqualsIgnoreCase(var1, var3));

      return true;
   }

   public static boolean containsAllContentEqualsIgnoreCase(Collection<CharSequence> var0, Collection<CharSequence> var1) {
      Iterator var2 = var1.iterator();

      CharSequence var3;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         var3 = (CharSequence)var2.next();
      } while(containsContentEqualsIgnoreCase(var0, var3));

      return false;
   }

   public static boolean contentEquals(CharSequence var0, CharSequence var1) {
      if (var0 != null && var1 != null) {
         if (var0.getClass() == AsciiString.class) {
            return ((AsciiString)var0).contentEquals(var1);
         } else if (var1.getClass() == AsciiString.class) {
            return ((AsciiString)var1).contentEquals(var0);
         } else if (var0.length() != var1.length()) {
            return false;
         } else {
            for(int var2 = 0; var2 < var0.length(); ++var2) {
               if (var0.charAt(var2) != var1.charAt(var2)) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return var0 == var1;
      }
   }

   private static AsciiString[] toAsciiStringArray(String[] var0) {
      AsciiString[] var1 = new AsciiString[var0.length];

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1[var2] = new AsciiString(var0[var2]);
      }

      return var1;
   }

   private static boolean contains(CharSequence var0, CharSequence var1, AsciiString.CharEqualityComparator var2) {
      if (var0 != null && var1 != null && var0.length() >= var1.length()) {
         if (var1.length() == 0) {
            return true;
         } else {
            int var3 = 0;

            for(int var4 = 0; var4 < var0.length(); ++var4) {
               if (var2.equals(var1.charAt(var3), var0.charAt(var4))) {
                  ++var3;
                  if (var3 == var1.length()) {
                     return true;
                  }
               } else {
                  if (var0.length() - var4 < var1.length()) {
                     return false;
                  }

                  var3 = 0;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   private static boolean regionMatchesCharSequences(CharSequence var0, int var1, CharSequence var2, int var3, int var4, AsciiString.CharEqualityComparator var5) {
      if (var1 >= 0 && var4 <= var0.length() - var1) {
         if (var3 >= 0 && var4 <= var2.length() - var3) {
            int var6 = var1;
            int var7 = var1 + var4;
            int var8 = var3;

            char var9;
            char var10;
            do {
               if (var6 >= var7) {
                  return true;
               }

               var9 = var0.charAt(var6++);
               var10 = var2.charAt(var8++);
            } while(var5.equals(var9, var10));

            return false;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean regionMatches(CharSequence var0, boolean var1, int var2, CharSequence var3, int var4, int var5) {
      if (var0 != null && var3 != null) {
         if (var0 instanceof String && var3 instanceof String) {
            return ((String)var0).regionMatches(var1, var2, (String)var3, var4, var5);
         } else {
            return var0 instanceof AsciiString ? ((AsciiString)var0).regionMatches(var1, var2, var3, var4, var5) : regionMatchesCharSequences(var0, var2, var3, var4, var5, (AsciiString.CharEqualityComparator)(var1 ? AsciiString.GeneralCaseInsensitiveCharEqualityComparator.INSTANCE : AsciiString.DefaultCharEqualityComparator.INSTANCE));
         }
      } else {
         return false;
      }
   }

   public static boolean regionMatchesAscii(CharSequence var0, boolean var1, int var2, CharSequence var3, int var4, int var5) {
      if (var0 != null && var3 != null) {
         if (!var1 && var0 instanceof String && var3 instanceof String) {
            return ((String)var0).regionMatches(false, var2, (String)var3, var4, var5);
         } else {
            return var0 instanceof AsciiString ? ((AsciiString)var0).regionMatches(var1, var2, var3, var4, var5) : regionMatchesCharSequences(var0, var2, var3, var4, var5, (AsciiString.CharEqualityComparator)(var1 ? AsciiString.AsciiCaseInsensitiveCharEqualityComparator.INSTANCE : AsciiString.DefaultCharEqualityComparator.INSTANCE));
         }
      } else {
         return false;
      }
   }

   public static int indexOfIgnoreCase(CharSequence var0, CharSequence var1, int var2) {
      if (var0 != null && var1 != null) {
         if (var2 < 0) {
            var2 = 0;
         }

         int var3 = var1.length();
         int var4 = var0.length() - var3 + 1;
         if (var2 > var4) {
            return -1;
         } else if (var3 == 0) {
            return var2;
         } else {
            for(int var5 = var2; var5 < var4; ++var5) {
               if (regionMatches(var0, true, var5, var1, 0, var3)) {
                  return var5;
               }
            }

            return -1;
         }
      } else {
         return -1;
      }
   }

   public static int indexOfIgnoreCaseAscii(CharSequence var0, CharSequence var1, int var2) {
      if (var0 != null && var1 != null) {
         if (var2 < 0) {
            var2 = 0;
         }

         int var3 = var1.length();
         int var4 = var0.length() - var3 + 1;
         if (var2 > var4) {
            return -1;
         } else if (var3 == 0) {
            return var2;
         } else {
            for(int var5 = var2; var5 < var4; ++var5) {
               if (regionMatchesAscii(var0, true, var5, var1, 0, var3)) {
                  return var5;
               }
            }

            return -1;
         }
      } else {
         return -1;
      }
   }

   public static int indexOf(CharSequence var0, char var1, int var2) {
      if (var0 instanceof String) {
         return ((String)var0).indexOf(var1, var2);
      } else if (var0 instanceof AsciiString) {
         return ((AsciiString)var0).indexOf(var1, var2);
      } else if (var0 == null) {
         return -1;
      } else {
         int var3 = var0.length();

         for(int var4 = var2 < 0 ? 0 : var2; var4 < var3; ++var4) {
            if (var0.charAt(var4) == var1) {
               return var4;
            }
         }

         return -1;
      }
   }

   private static boolean equalsIgnoreCase(byte var0, byte var1) {
      return var0 == var1 || toLowerCase(var0) == toLowerCase(var1);
   }

   private static boolean equalsIgnoreCase(char var0, char var1) {
      return var0 == var1 || toLowerCase(var0) == toLowerCase(var1);
   }

   private static byte toLowerCase(byte var0) {
      return isUpperCase(var0) ? (byte)(var0 + 32) : var0;
   }

   private static char toLowerCase(char var0) {
      return isUpperCase(var0) ? (char)(var0 + 32) : var0;
   }

   private static byte toUpperCase(byte var0) {
      return isLowerCase(var0) ? (byte)(var0 - 32) : var0;
   }

   private static boolean isLowerCase(byte var0) {
      return var0 >= 97 && var0 <= 122;
   }

   public static boolean isUpperCase(byte var0) {
      return var0 >= 65 && var0 <= 90;
   }

   public static boolean isUpperCase(char var0) {
      return var0 >= 'A' && var0 <= 'Z';
   }

   public static byte c2b(char var0) {
      return (byte)(var0 > 255 ? 63 : var0);
   }

   private static byte c2b0(char var0) {
      return (byte)var0;
   }

   public static char b2c(byte var0) {
      return (char)(var0 & 255);
   }

   private static final class GeneralCaseInsensitiveCharEqualityComparator implements AsciiString.CharEqualityComparator {
      static final AsciiString.GeneralCaseInsensitiveCharEqualityComparator INSTANCE = new AsciiString.GeneralCaseInsensitiveCharEqualityComparator();

      private GeneralCaseInsensitiveCharEqualityComparator() {
         super();
      }

      public boolean equals(char var1, char var2) {
         return Character.toUpperCase(var1) == Character.toUpperCase(var2) || Character.toLowerCase(var1) == Character.toLowerCase(var2);
      }
   }

   private static final class AsciiCaseInsensitiveCharEqualityComparator implements AsciiString.CharEqualityComparator {
      static final AsciiString.AsciiCaseInsensitiveCharEqualityComparator INSTANCE = new AsciiString.AsciiCaseInsensitiveCharEqualityComparator();

      private AsciiCaseInsensitiveCharEqualityComparator() {
         super();
      }

      public boolean equals(char var1, char var2) {
         return AsciiString.equalsIgnoreCase(var1, var2);
      }
   }

   private static final class DefaultCharEqualityComparator implements AsciiString.CharEqualityComparator {
      static final AsciiString.DefaultCharEqualityComparator INSTANCE = new AsciiString.DefaultCharEqualityComparator();

      private DefaultCharEqualityComparator() {
         super();
      }

      public boolean equals(char var1, char var2) {
         return var1 == var2;
      }
   }

   private interface CharEqualityComparator {
      boolean equals(char var1, char var2);
   }
}
