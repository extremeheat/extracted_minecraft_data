package com.google.common.io;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Ascii;
import com.google.common.base.CharMatcher;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.math.IntMath;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.RoundingMode;
import java.util.Arrays;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
public abstract class BaseEncoding {
   private static final BaseEncoding BASE64 = new BaseEncoding.Base64Encoding("base64()", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", '=');
   private static final BaseEncoding BASE64_URL = new BaseEncoding.Base64Encoding("base64Url()", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_", '=');
   private static final BaseEncoding BASE32 = new BaseEncoding.StandardBaseEncoding("base32()", "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567", '=');
   private static final BaseEncoding BASE32_HEX = new BaseEncoding.StandardBaseEncoding("base32Hex()", "0123456789ABCDEFGHIJKLMNOPQRSTUV", '=');
   private static final BaseEncoding BASE16 = new BaseEncoding.Base16Encoding("base16()", "0123456789ABCDEF");

   BaseEncoding() {
      super();
   }

   public String encode(byte[] var1) {
      return this.encode(var1, 0, var1.length);
   }

   public final String encode(byte[] var1, int var2, int var3) {
      Preconditions.checkPositionIndexes(var2, var2 + var3, var1.length);
      StringBuilder var4 = new StringBuilder(this.maxEncodedSize(var3));

      try {
         this.encodeTo(var4, var1, var2, var3);
      } catch (IOException var6) {
         throw new AssertionError(var6);
      }

      return var4.toString();
   }

   @GwtIncompatible
   public abstract OutputStream encodingStream(Writer var1);

   @GwtIncompatible
   public final ByteSink encodingSink(final CharSink var1) {
      Preconditions.checkNotNull(var1);
      return new ByteSink() {
         public OutputStream openStream() throws IOException {
            return BaseEncoding.this.encodingStream(var1.openStream());
         }
      };
   }

   private static byte[] extract(byte[] var0, int var1) {
      if (var1 == var0.length) {
         return var0;
      } else {
         byte[] var2 = new byte[var1];
         System.arraycopy(var0, 0, var2, 0, var1);
         return var2;
      }
   }

   public abstract boolean canDecode(CharSequence var1);

   public final byte[] decode(CharSequence var1) {
      try {
         return this.decodeChecked(var1);
      } catch (BaseEncoding.DecodingException var3) {
         throw new IllegalArgumentException(var3);
      }
   }

   final byte[] decodeChecked(CharSequence var1) throws BaseEncoding.DecodingException {
      String var4 = this.padding().trimTrailingFrom(var1);
      byte[] var2 = new byte[this.maxDecodedSize(var4.length())];
      int var3 = this.decodeTo(var2, var4);
      return extract(var2, var3);
   }

   @GwtIncompatible
   public abstract InputStream decodingStream(Reader var1);

   @GwtIncompatible
   public final ByteSource decodingSource(final CharSource var1) {
      Preconditions.checkNotNull(var1);
      return new ByteSource() {
         public InputStream openStream() throws IOException {
            return BaseEncoding.this.decodingStream(var1.openStream());
         }
      };
   }

   abstract int maxEncodedSize(int var1);

   abstract void encodeTo(Appendable var1, byte[] var2, int var3, int var4) throws IOException;

   abstract int maxDecodedSize(int var1);

   abstract int decodeTo(byte[] var1, CharSequence var2) throws BaseEncoding.DecodingException;

   abstract CharMatcher padding();

   public abstract BaseEncoding omitPadding();

   public abstract BaseEncoding withPadChar(char var1);

   public abstract BaseEncoding withSeparator(String var1, int var2);

   public abstract BaseEncoding upperCase();

   public abstract BaseEncoding lowerCase();

   public static BaseEncoding base64() {
      return BASE64;
   }

   public static BaseEncoding base64Url() {
      return BASE64_URL;
   }

   public static BaseEncoding base32() {
      return BASE32;
   }

   public static BaseEncoding base32Hex() {
      return BASE32_HEX;
   }

   public static BaseEncoding base16() {
      return BASE16;
   }

   @GwtIncompatible
   static Reader ignoringReader(final Reader var0, final CharMatcher var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new Reader() {
         public int read() throws IOException {
            int var1x;
            do {
               var1x = var0.read();
            } while(var1x != -1 && var1.matches((char)var1x));

            return var1x;
         }

         public int read(char[] var1x, int var2, int var3) throws IOException {
            throw new UnsupportedOperationException();
         }

         public void close() throws IOException {
            var0.close();
         }
      };
   }

   static Appendable separatingAppendable(final Appendable var0, final String var1, final int var2) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Preconditions.checkArgument(var2 > 0);
      return new Appendable() {
         int charsUntilSeparator = var2;

         public Appendable append(char var1x) throws IOException {
            if (this.charsUntilSeparator == 0) {
               var0.append(var1);
               this.charsUntilSeparator = var2;
            }

            var0.append(var1x);
            --this.charsUntilSeparator;
            return this;
         }

         public Appendable append(CharSequence var1x, int var2x, int var3) throws IOException {
            throw new UnsupportedOperationException();
         }

         public Appendable append(CharSequence var1x) throws IOException {
            throw new UnsupportedOperationException();
         }
      };
   }

   @GwtIncompatible
   static Writer separatingWriter(final Writer var0, String var1, int var2) {
      final Appendable var3 = separatingAppendable(var0, var1, var2);
      return new Writer() {
         public void write(int var1) throws IOException {
            var3.append((char)var1);
         }

         public void write(char[] var1, int var2, int var3x) throws IOException {
            throw new UnsupportedOperationException();
         }

         public void flush() throws IOException {
            var0.flush();
         }

         public void close() throws IOException {
            var0.close();
         }
      };
   }

   static final class SeparatedBaseEncoding extends BaseEncoding {
      private final BaseEncoding delegate;
      private final String separator;
      private final int afterEveryChars;
      private final CharMatcher separatorChars;

      SeparatedBaseEncoding(BaseEncoding var1, String var2, int var3) {
         super();
         this.delegate = (BaseEncoding)Preconditions.checkNotNull(var1);
         this.separator = (String)Preconditions.checkNotNull(var2);
         this.afterEveryChars = var3;
         Preconditions.checkArgument(var3 > 0, "Cannot add a separator after every %s chars", var3);
         this.separatorChars = CharMatcher.anyOf(var2).precomputed();
      }

      CharMatcher padding() {
         return this.delegate.padding();
      }

      int maxEncodedSize(int var1) {
         int var2 = this.delegate.maxEncodedSize(var1);
         return var2 + this.separator.length() * IntMath.divide(Math.max(0, var2 - 1), this.afterEveryChars, RoundingMode.FLOOR);
      }

      @GwtIncompatible
      public OutputStream encodingStream(Writer var1) {
         return this.delegate.encodingStream(separatingWriter(var1, this.separator, this.afterEveryChars));
      }

      void encodeTo(Appendable var1, byte[] var2, int var3, int var4) throws IOException {
         this.delegate.encodeTo(separatingAppendable(var1, this.separator, this.afterEveryChars), var2, var3, var4);
      }

      int maxDecodedSize(int var1) {
         return this.delegate.maxDecodedSize(var1);
      }

      public boolean canDecode(CharSequence var1) {
         return this.delegate.canDecode(this.separatorChars.removeFrom(var1));
      }

      int decodeTo(byte[] var1, CharSequence var2) throws BaseEncoding.DecodingException {
         return this.delegate.decodeTo(var1, this.separatorChars.removeFrom(var2));
      }

      @GwtIncompatible
      public InputStream decodingStream(Reader var1) {
         return this.delegate.decodingStream(ignoringReader(var1, this.separatorChars));
      }

      public BaseEncoding omitPadding() {
         return this.delegate.omitPadding().withSeparator(this.separator, this.afterEveryChars);
      }

      public BaseEncoding withPadChar(char var1) {
         return this.delegate.withPadChar(var1).withSeparator(this.separator, this.afterEveryChars);
      }

      public BaseEncoding withSeparator(String var1, int var2) {
         throw new UnsupportedOperationException("Already have a separator");
      }

      public BaseEncoding upperCase() {
         return this.delegate.upperCase().withSeparator(this.separator, this.afterEveryChars);
      }

      public BaseEncoding lowerCase() {
         return this.delegate.lowerCase().withSeparator(this.separator, this.afterEveryChars);
      }

      public String toString() {
         return this.delegate + ".withSeparator(\"" + this.separator + "\", " + this.afterEveryChars + ")";
      }
   }

   static final class Base64Encoding extends BaseEncoding.StandardBaseEncoding {
      Base64Encoding(String var1, String var2, @Nullable Character var3) {
         this(new BaseEncoding.Alphabet(var1, var2.toCharArray()), var3);
      }

      private Base64Encoding(BaseEncoding.Alphabet var1, @Nullable Character var2) {
         super(var1, var2);
         Preconditions.checkArgument(var1.chars.length == 64);
      }

      void encodeTo(Appendable var1, byte[] var2, int var3, int var4) throws IOException {
         Preconditions.checkNotNull(var1);
         Preconditions.checkPositionIndexes(var3, var3 + var4, var2.length);
         int var5 = var3;

         for(int var6 = var4; var6 >= 3; var6 -= 3) {
            int var7 = (var2[var5++] & 255) << 16 | (var2[var5++] & 255) << 8 | var2[var5++] & 255;
            var1.append(this.alphabet.encode(var7 >>> 18));
            var1.append(this.alphabet.encode(var7 >>> 12 & 63));
            var1.append(this.alphabet.encode(var7 >>> 6 & 63));
            var1.append(this.alphabet.encode(var7 & 63));
         }

         if (var5 < var3 + var4) {
            this.encodeChunkTo(var1, var2, var5, var3 + var4 - var5);
         }

      }

      int decodeTo(byte[] var1, CharSequence var2) throws BaseEncoding.DecodingException {
         Preconditions.checkNotNull(var1);
         String var6 = this.padding().trimTrailingFrom(var2);
         if (!this.alphabet.isValidPaddingStartPosition(var6.length())) {
            throw new BaseEncoding.DecodingException("Invalid input length " + var6.length());
         } else {
            int var3 = 0;
            int var4 = 0;

            while(var4 < var6.length()) {
               int var5 = this.alphabet.decode(var6.charAt(var4++)) << 18;
               var5 |= this.alphabet.decode(var6.charAt(var4++)) << 12;
               var1[var3++] = (byte)(var5 >>> 16);
               if (var4 < var6.length()) {
                  var5 |= this.alphabet.decode(var6.charAt(var4++)) << 6;
                  var1[var3++] = (byte)(var5 >>> 8 & 255);
                  if (var4 < var6.length()) {
                     var5 |= this.alphabet.decode(var6.charAt(var4++));
                     var1[var3++] = (byte)(var5 & 255);
                  }
               }
            }

            return var3;
         }
      }

      BaseEncoding newInstance(BaseEncoding.Alphabet var1, @Nullable Character var2) {
         return new BaseEncoding.Base64Encoding(var1, var2);
      }
   }

   static final class Base16Encoding extends BaseEncoding.StandardBaseEncoding {
      final char[] encoding;

      Base16Encoding(String var1, String var2) {
         this(new BaseEncoding.Alphabet(var1, var2.toCharArray()));
      }

      private Base16Encoding(BaseEncoding.Alphabet var1) {
         super(var1, (Character)null);
         this.encoding = new char[512];
         Preconditions.checkArgument(var1.chars.length == 16);

         for(int var2 = 0; var2 < 256; ++var2) {
            this.encoding[var2] = var1.encode(var2 >>> 4);
            this.encoding[var2 | 256] = var1.encode(var2 & 15);
         }

      }

      void encodeTo(Appendable var1, byte[] var2, int var3, int var4) throws IOException {
         Preconditions.checkNotNull(var1);
         Preconditions.checkPositionIndexes(var3, var3 + var4, var2.length);

         for(int var5 = 0; var5 < var4; ++var5) {
            int var6 = var2[var3 + var5] & 255;
            var1.append(this.encoding[var6]);
            var1.append(this.encoding[var6 | 256]);
         }

      }

      int decodeTo(byte[] var1, CharSequence var2) throws BaseEncoding.DecodingException {
         Preconditions.checkNotNull(var1);
         if (var2.length() % 2 == 1) {
            throw new BaseEncoding.DecodingException("Invalid input length " + var2.length());
         } else {
            int var3 = 0;

            for(int var4 = 0; var4 < var2.length(); var4 += 2) {
               int var5 = this.alphabet.decode(var2.charAt(var4)) << 4 | this.alphabet.decode(var2.charAt(var4 + 1));
               var1[var3++] = (byte)var5;
            }

            return var3;
         }
      }

      BaseEncoding newInstance(BaseEncoding.Alphabet var1, @Nullable Character var2) {
         return new BaseEncoding.Base16Encoding(var1);
      }
   }

   static class StandardBaseEncoding extends BaseEncoding {
      final BaseEncoding.Alphabet alphabet;
      @Nullable
      final Character paddingChar;
      private transient BaseEncoding upperCase;
      private transient BaseEncoding lowerCase;

      StandardBaseEncoding(String var1, String var2, @Nullable Character var3) {
         this(new BaseEncoding.Alphabet(var1, var2.toCharArray()), var3);
      }

      StandardBaseEncoding(BaseEncoding.Alphabet var1, @Nullable Character var2) {
         super();
         this.alphabet = (BaseEncoding.Alphabet)Preconditions.checkNotNull(var1);
         Preconditions.checkArgument(var2 == null || !var1.matches(var2), "Padding character %s was already in alphabet", (Object)var2);
         this.paddingChar = var2;
      }

      CharMatcher padding() {
         return this.paddingChar == null ? CharMatcher.none() : CharMatcher.is(this.paddingChar);
      }

      int maxEncodedSize(int var1) {
         return this.alphabet.charsPerChunk * IntMath.divide(var1, this.alphabet.bytesPerChunk, RoundingMode.CEILING);
      }

      @GwtIncompatible
      public OutputStream encodingStream(final Writer var1) {
         Preconditions.checkNotNull(var1);
         return new OutputStream() {
            int bitBuffer = 0;
            int bitBufferLength = 0;
            int writtenChars = 0;

            public void write(int var1x) throws IOException {
               this.bitBuffer <<= 8;
               this.bitBuffer |= var1x & 255;

               for(this.bitBufferLength += 8; this.bitBufferLength >= StandardBaseEncoding.this.alphabet.bitsPerChar; this.bitBufferLength -= StandardBaseEncoding.this.alphabet.bitsPerChar) {
                  int var2 = this.bitBuffer >> this.bitBufferLength - StandardBaseEncoding.this.alphabet.bitsPerChar & StandardBaseEncoding.this.alphabet.mask;
                  var1.write(StandardBaseEncoding.this.alphabet.encode(var2));
                  ++this.writtenChars;
               }

            }

            public void flush() throws IOException {
               var1.flush();
            }

            public void close() throws IOException {
               if (this.bitBufferLength > 0) {
                  int var1x = this.bitBuffer << StandardBaseEncoding.this.alphabet.bitsPerChar - this.bitBufferLength & StandardBaseEncoding.this.alphabet.mask;
                  var1.write(StandardBaseEncoding.this.alphabet.encode(var1x));
                  ++this.writtenChars;
                  if (StandardBaseEncoding.this.paddingChar != null) {
                     while(this.writtenChars % StandardBaseEncoding.this.alphabet.charsPerChunk != 0) {
                        var1.write(StandardBaseEncoding.this.paddingChar);
                        ++this.writtenChars;
                     }
                  }
               }

               var1.close();
            }
         };
      }

      void encodeTo(Appendable var1, byte[] var2, int var3, int var4) throws IOException {
         Preconditions.checkNotNull(var1);
         Preconditions.checkPositionIndexes(var3, var3 + var4, var2.length);

         for(int var5 = 0; var5 < var4; var5 += this.alphabet.bytesPerChunk) {
            this.encodeChunkTo(var1, var2, var3 + var5, Math.min(this.alphabet.bytesPerChunk, var4 - var5));
         }

      }

      void encodeChunkTo(Appendable var1, byte[] var2, int var3, int var4) throws IOException {
         Preconditions.checkNotNull(var1);
         Preconditions.checkPositionIndexes(var3, var3 + var4, var2.length);
         Preconditions.checkArgument(var4 <= this.alphabet.bytesPerChunk);
         long var5 = 0L;

         int var7;
         for(var7 = 0; var7 < var4; ++var7) {
            var5 |= (long)(var2[var3 + var7] & 255);
            var5 <<= 8;
         }

         var7 = (var4 + 1) * 8 - this.alphabet.bitsPerChar;

         int var8;
         for(var8 = 0; var8 < var4 * 8; var8 += this.alphabet.bitsPerChar) {
            int var9 = (int)(var5 >>> var7 - var8) & this.alphabet.mask;
            var1.append(this.alphabet.encode(var9));
         }

         if (this.paddingChar != null) {
            while(var8 < this.alphabet.bytesPerChunk * 8) {
               var1.append(this.paddingChar);
               var8 += this.alphabet.bitsPerChar;
            }
         }

      }

      int maxDecodedSize(int var1) {
         return (int)(((long)this.alphabet.bitsPerChar * (long)var1 + 7L) / 8L);
      }

      public boolean canDecode(CharSequence var1) {
         String var3 = this.padding().trimTrailingFrom(var1);
         if (!this.alphabet.isValidPaddingStartPosition(var3.length())) {
            return false;
         } else {
            for(int var2 = 0; var2 < var3.length(); ++var2) {
               if (!this.alphabet.canDecode(var3.charAt(var2))) {
                  return false;
               }
            }

            return true;
         }
      }

      int decodeTo(byte[] var1, CharSequence var2) throws BaseEncoding.DecodingException {
         Preconditions.checkNotNull(var1);
         String var10 = this.padding().trimTrailingFrom(var2);
         if (!this.alphabet.isValidPaddingStartPosition(var10.length())) {
            throw new BaseEncoding.DecodingException("Invalid input length " + var10.length());
         } else {
            int var3 = 0;

            for(int var4 = 0; var4 < var10.length(); var4 += this.alphabet.charsPerChunk) {
               long var5 = 0L;
               int var7 = 0;

               int var8;
               for(var8 = 0; var8 < this.alphabet.charsPerChunk; ++var8) {
                  var5 <<= this.alphabet.bitsPerChar;
                  if (var4 + var8 < var10.length()) {
                     var5 |= (long)this.alphabet.decode(var10.charAt(var4 + var7++));
                  }
               }

               var8 = this.alphabet.bytesPerChunk * 8 - var7 * this.alphabet.bitsPerChar;

               for(int var9 = (this.alphabet.bytesPerChunk - 1) * 8; var9 >= var8; var9 -= 8) {
                  var1[var3++] = (byte)((int)(var5 >>> var9 & 255L));
               }
            }

            return var3;
         }
      }

      @GwtIncompatible
      public InputStream decodingStream(final Reader var1) {
         Preconditions.checkNotNull(var1);
         return new InputStream() {
            int bitBuffer = 0;
            int bitBufferLength = 0;
            int readChars = 0;
            boolean hitPadding = false;
            final CharMatcher paddingMatcher = StandardBaseEncoding.this.padding();

            public int read() throws IOException {
               while(true) {
                  int var1x = var1.read();
                  if (var1x == -1) {
                     if (!this.hitPadding && !StandardBaseEncoding.this.alphabet.isValidPaddingStartPosition(this.readChars)) {
                        throw new BaseEncoding.DecodingException("Invalid input length " + this.readChars);
                     }

                     return -1;
                  }

                  ++this.readChars;
                  char var2 = (char)var1x;
                  if (!this.paddingMatcher.matches(var2)) {
                     if (this.hitPadding) {
                        throw new BaseEncoding.DecodingException("Expected padding character but found '" + var2 + "' at index " + this.readChars);
                     }

                     this.bitBuffer <<= StandardBaseEncoding.this.alphabet.bitsPerChar;
                     this.bitBuffer |= StandardBaseEncoding.this.alphabet.decode(var2);
                     this.bitBufferLength += StandardBaseEncoding.this.alphabet.bitsPerChar;
                     if (this.bitBufferLength >= 8) {
                        this.bitBufferLength -= 8;
                        return this.bitBuffer >> this.bitBufferLength & 255;
                     }
                  } else {
                     if (!this.hitPadding && (this.readChars == 1 || !StandardBaseEncoding.this.alphabet.isValidPaddingStartPosition(this.readChars - 1))) {
                        throw new BaseEncoding.DecodingException("Padding cannot start at index " + this.readChars);
                     }

                     this.hitPadding = true;
                  }
               }
            }

            public void close() throws IOException {
               var1.close();
            }
         };
      }

      public BaseEncoding omitPadding() {
         return (BaseEncoding)(this.paddingChar == null ? this : this.newInstance(this.alphabet, (Character)null));
      }

      public BaseEncoding withPadChar(char var1) {
         return (BaseEncoding)(8 % this.alphabet.bitsPerChar != 0 && (this.paddingChar == null || this.paddingChar != var1) ? this.newInstance(this.alphabet, var1) : this);
      }

      public BaseEncoding withSeparator(String var1, int var2) {
         Preconditions.checkArgument(this.padding().or(this.alphabet).matchesNoneOf(var1), "Separator (%s) cannot contain alphabet or padding characters", (Object)var1);
         return new BaseEncoding.SeparatedBaseEncoding(this, var1, var2);
      }

      public BaseEncoding upperCase() {
         BaseEncoding var1 = this.upperCase;
         if (var1 == null) {
            BaseEncoding.Alphabet var2 = this.alphabet.upperCase();
            var1 = this.upperCase = (BaseEncoding)(var2 == this.alphabet ? this : this.newInstance(var2, this.paddingChar));
         }

         return var1;
      }

      public BaseEncoding lowerCase() {
         BaseEncoding var1 = this.lowerCase;
         if (var1 == null) {
            BaseEncoding.Alphabet var2 = this.alphabet.lowerCase();
            var1 = this.lowerCase = (BaseEncoding)(var2 == this.alphabet ? this : this.newInstance(var2, this.paddingChar));
         }

         return var1;
      }

      BaseEncoding newInstance(BaseEncoding.Alphabet var1, @Nullable Character var2) {
         return new BaseEncoding.StandardBaseEncoding(var1, var2);
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder("BaseEncoding.");
         var1.append(this.alphabet.toString());
         if (8 % this.alphabet.bitsPerChar != 0) {
            if (this.paddingChar == null) {
               var1.append(".omitPadding()");
            } else {
               var1.append(".withPadChar('").append(this.paddingChar).append("')");
            }
         }

         return var1.toString();
      }

      public boolean equals(@Nullable Object var1) {
         if (!(var1 instanceof BaseEncoding.StandardBaseEncoding)) {
            return false;
         } else {
            BaseEncoding.StandardBaseEncoding var2 = (BaseEncoding.StandardBaseEncoding)var1;
            return this.alphabet.equals(var2.alphabet) && Objects.equal(this.paddingChar, var2.paddingChar);
         }
      }

      public int hashCode() {
         return this.alphabet.hashCode() ^ Objects.hashCode(this.paddingChar);
      }
   }

   private static final class Alphabet extends CharMatcher {
      private final String name;
      private final char[] chars;
      final int mask;
      final int bitsPerChar;
      final int charsPerChunk;
      final int bytesPerChunk;
      private final byte[] decodabet;
      private final boolean[] validPadding;

      Alphabet(String var1, char[] var2) {
         super();
         this.name = (String)Preconditions.checkNotNull(var1);
         this.chars = (char[])Preconditions.checkNotNull(var2);

         try {
            this.bitsPerChar = IntMath.log2(var2.length, RoundingMode.UNNECESSARY);
         } catch (ArithmeticException var8) {
            throw new IllegalArgumentException("Illegal alphabet length " + var2.length, var8);
         }

         int var3 = Math.min(8, Integer.lowestOneBit(this.bitsPerChar));

         try {
            this.charsPerChunk = 8 / var3;
            this.bytesPerChunk = this.bitsPerChar / var3;
         } catch (ArithmeticException var7) {
            throw new IllegalArgumentException("Illegal alphabet " + new String(var2), var7);
         }

         this.mask = var2.length - 1;
         byte[] var4 = new byte[128];
         Arrays.fill(var4, (byte)-1);

         for(int var5 = 0; var5 < var2.length; ++var5) {
            char var6 = var2[var5];
            Preconditions.checkArgument(CharMatcher.ascii().matches(var6), "Non-ASCII character: %s", var6);
            Preconditions.checkArgument(var4[var6] == -1, "Duplicate character: %s", var6);
            var4[var6] = (byte)var5;
         }

         this.decodabet = var4;
         boolean[] var9 = new boolean[this.charsPerChunk];

         for(int var10 = 0; var10 < this.bytesPerChunk; ++var10) {
            var9[IntMath.divide(var10 * 8, this.bitsPerChar, RoundingMode.CEILING)] = true;
         }

         this.validPadding = var9;
      }

      char encode(int var1) {
         return this.chars[var1];
      }

      boolean isValidPaddingStartPosition(int var1) {
         return this.validPadding[var1 % this.charsPerChunk];
      }

      boolean canDecode(char var1) {
         return var1 <= 127 && this.decodabet[var1] != -1;
      }

      int decode(char var1) throws BaseEncoding.DecodingException {
         if (var1 <= 127 && this.decodabet[var1] != -1) {
            return this.decodabet[var1];
         } else {
            throw new BaseEncoding.DecodingException("Unrecognized character: " + (CharMatcher.invisible().matches(var1) ? "0x" + Integer.toHexString(var1) : var1));
         }
      }

      private boolean hasLowerCase() {
         char[] var1 = this.chars;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            char var4 = var1[var3];
            if (Ascii.isLowerCase(var4)) {
               return true;
            }
         }

         return false;
      }

      private boolean hasUpperCase() {
         char[] var1 = this.chars;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            char var4 = var1[var3];
            if (Ascii.isUpperCase(var4)) {
               return true;
            }
         }

         return false;
      }

      BaseEncoding.Alphabet upperCase() {
         if (!this.hasLowerCase()) {
            return this;
         } else {
            Preconditions.checkState(!this.hasUpperCase(), "Cannot call upperCase() on a mixed-case alphabet");
            char[] var1 = new char[this.chars.length];

            for(int var2 = 0; var2 < this.chars.length; ++var2) {
               var1[var2] = Ascii.toUpperCase(this.chars[var2]);
            }

            return new BaseEncoding.Alphabet(this.name + ".upperCase()", var1);
         }
      }

      BaseEncoding.Alphabet lowerCase() {
         if (!this.hasUpperCase()) {
            return this;
         } else {
            Preconditions.checkState(!this.hasLowerCase(), "Cannot call lowerCase() on a mixed-case alphabet");
            char[] var1 = new char[this.chars.length];

            for(int var2 = 0; var2 < this.chars.length; ++var2) {
               var1[var2] = Ascii.toLowerCase(this.chars[var2]);
            }

            return new BaseEncoding.Alphabet(this.name + ".lowerCase()", var1);
         }
      }

      public boolean matches(char var1) {
         return CharMatcher.ascii().matches(var1) && this.decodabet[var1] != -1;
      }

      public String toString() {
         return this.name;
      }

      public boolean equals(@Nullable Object var1) {
         if (var1 instanceof BaseEncoding.Alphabet) {
            BaseEncoding.Alphabet var2 = (BaseEncoding.Alphabet)var1;
            return Arrays.equals(this.chars, var2.chars);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Arrays.hashCode(this.chars);
      }
   }

   public static final class DecodingException extends IOException {
      DecodingException(String var1) {
         super(var1);
      }

      DecodingException(Throwable var1) {
         super(var1);
      }
   }
}
