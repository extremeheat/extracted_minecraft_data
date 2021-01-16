package com.google.common.hash;

import com.google.common.base.Preconditions;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

abstract class AbstractNonStreamingHashFunction implements HashFunction {
   AbstractNonStreamingHashFunction() {
      super();
   }

   public Hasher newHasher() {
      return new AbstractNonStreamingHashFunction.BufferingHasher(32);
   }

   public Hasher newHasher(int var1) {
      Preconditions.checkArgument(var1 >= 0);
      return new AbstractNonStreamingHashFunction.BufferingHasher(var1);
   }

   public <T> HashCode hashObject(T var1, Funnel<? super T> var2) {
      return this.newHasher().putObject(var1, var2).hash();
   }

   public HashCode hashUnencodedChars(CharSequence var1) {
      int var2 = var1.length();
      Hasher var3 = this.newHasher(var2 * 2);

      for(int var4 = 0; var4 < var2; ++var4) {
         var3.putChar(var1.charAt(var4));
      }

      return var3.hash();
   }

   public HashCode hashString(CharSequence var1, Charset var2) {
      return this.hashBytes(var1.toString().getBytes(var2));
   }

   public HashCode hashInt(int var1) {
      return this.newHasher(4).putInt(var1).hash();
   }

   public HashCode hashLong(long var1) {
      return this.newHasher(8).putLong(var1).hash();
   }

   public HashCode hashBytes(byte[] var1) {
      return this.hashBytes(var1, 0, var1.length);
   }

   private static final class ExposedByteArrayOutputStream extends ByteArrayOutputStream {
      ExposedByteArrayOutputStream(int var1) {
         super(var1);
      }

      byte[] byteArray() {
         return this.buf;
      }

      int length() {
         return this.count;
      }
   }

   private final class BufferingHasher extends AbstractHasher {
      final AbstractNonStreamingHashFunction.ExposedByteArrayOutputStream stream;
      static final int BOTTOM_BYTE = 255;

      BufferingHasher(int var2) {
         super();
         this.stream = new AbstractNonStreamingHashFunction.ExposedByteArrayOutputStream(var2);
      }

      public Hasher putByte(byte var1) {
         this.stream.write(var1);
         return this;
      }

      public Hasher putBytes(byte[] var1) {
         try {
            this.stream.write(var1);
            return this;
         } catch (IOException var3) {
            throw new RuntimeException(var3);
         }
      }

      public Hasher putBytes(byte[] var1, int var2, int var3) {
         this.stream.write(var1, var2, var3);
         return this;
      }

      public Hasher putShort(short var1) {
         this.stream.write(var1 & 255);
         this.stream.write(var1 >>> 8 & 255);
         return this;
      }

      public Hasher putInt(int var1) {
         this.stream.write(var1 & 255);
         this.stream.write(var1 >>> 8 & 255);
         this.stream.write(var1 >>> 16 & 255);
         this.stream.write(var1 >>> 24 & 255);
         return this;
      }

      public Hasher putLong(long var1) {
         for(int var3 = 0; var3 < 64; var3 += 8) {
            this.stream.write((byte)((int)(var1 >>> var3 & 255L)));
         }

         return this;
      }

      public Hasher putChar(char var1) {
         this.stream.write(var1 & 255);
         this.stream.write(var1 >>> 8 & 255);
         return this;
      }

      public <T> Hasher putObject(T var1, Funnel<? super T> var2) {
         var2.funnel(var1, this);
         return this;
      }

      public HashCode hash() {
         return AbstractNonStreamingHashFunction.this.hashBytes(this.stream.byteArray(), 0, this.stream.length());
      }
   }
}
