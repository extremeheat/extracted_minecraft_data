package it.unimi.dsi.fastutil.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class FastMultiByteArrayInputStream extends MeasurableInputStream implements RepositionableStream {
   public static final int SLICE_BITS = 10;
   public static final int SLICE_SIZE = 1024;
   public static final int SLICE_MASK = 1023;
   public byte[][] array;
   public byte[] current;
   public long length;
   private long position;

   public FastMultiByteArrayInputStream(MeasurableInputStream var1) throws IOException {
      this(var1, var1.length());
   }

   public FastMultiByteArrayInputStream(InputStream var1, long var2) throws IOException {
      super();
      this.length = var2;
      this.array = new byte[(int)((var2 + 1024L - 1L) / 1024L) + 1][];

      for(int var4 = 0; var4 < this.array.length - 1; ++var4) {
         this.array[var4] = new byte[var2 >= 1024L ? 1024 : (int)var2];
         if (BinIO.loadBytes(var1, this.array[var4]) != this.array[var4].length) {
            throw new EOFException();
         }

         var2 -= (long)this.array[var4].length;
      }

      this.current = this.array[0];
   }

   public FastMultiByteArrayInputStream(FastMultiByteArrayInputStream var1) {
      super();
      this.array = var1.array;
      this.length = var1.length;
      this.current = this.array[0];
   }

   public FastMultiByteArrayInputStream(byte[] var1) {
      super();
      if (var1.length == 0) {
         this.array = new byte[1][];
      } else {
         this.array = new byte[2][];
         this.array[0] = var1;
         this.length = (long)var1.length;
         this.current = var1;
      }

   }

   public int available() {
      return (int)Math.min(2147483647L, this.length - this.position);
   }

   public long skip(long var1) {
      if (var1 > this.length - this.position) {
         var1 = this.length - this.position;
      }

      this.position += var1;
      this.updateCurrent();
      return var1;
   }

   public int read() {
      if (this.length == this.position) {
         return -1;
      } else {
         int var1 = (int)(this.position++ & 1023L);
         if (var1 == 0) {
            this.updateCurrent();
         }

         return this.current[var1] & 255;
      }
   }

   public int read(byte[] var1, int var2, int var3) {
      long var4 = this.length - this.position;
      if (var4 == 0L) {
         return var3 == 0 ? 0 : -1;
      } else {
         int var6 = (int)Math.min((long)var3, var4);
         int var7 = var6;

         while(true) {
            int var8 = (int)(this.position & 1023L);
            if (var8 == 0) {
               this.updateCurrent();
            }

            int var9 = Math.min(var6, this.current.length - var8);
            System.arraycopy(this.current, var8, var1, var2, var9);
            var6 -= var9;
            this.position += (long)var9;
            if (var6 == 0) {
               return var7;
            }

            var2 += var9;
         }
      }
   }

   private void updateCurrent() {
      this.current = this.array[(int)(this.position >>> 10)];
   }

   public long position() {
      return this.position;
   }

   public void position(long var1) {
      this.position = Math.min(var1, this.length);
      this.updateCurrent();
   }

   public long length() throws IOException {
      return this.length;
   }

   public void close() {
   }

   public boolean markSupported() {
      return false;
   }

   public void mark(int var1) {
      throw new UnsupportedOperationException();
   }

   public void reset() {
      throw new UnsupportedOperationException();
   }
}
