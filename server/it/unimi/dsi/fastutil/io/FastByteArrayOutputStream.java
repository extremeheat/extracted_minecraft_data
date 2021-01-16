package it.unimi.dsi.fastutil.io;

import it.unimi.dsi.fastutil.bytes.ByteArrays;
import java.io.IOException;

public class FastByteArrayOutputStream extends MeasurableOutputStream implements RepositionableStream {
   public static final int DEFAULT_INITIAL_CAPACITY = 16;
   public byte[] array;
   public int length;
   private int position;

   public FastByteArrayOutputStream() {
      this(16);
   }

   public FastByteArrayOutputStream(int var1) {
      super();
      this.array = new byte[var1];
   }

   public FastByteArrayOutputStream(byte[] var1) {
      super();
      this.array = var1;
   }

   public void reset() {
      this.length = 0;
      this.position = 0;
   }

   public void trim() {
      this.array = ByteArrays.trim(this.array, this.length);
   }

   public void write(int var1) {
      if (this.position >= this.array.length) {
         this.array = ByteArrays.grow(this.array, this.position + 1, this.length);
      }

      this.array[this.position++] = (byte)var1;
      if (this.length < this.position) {
         this.length = this.position;
      }

   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      ByteArrays.ensureOffsetLength(var1, var2, var3);
      if (this.position + var3 > this.array.length) {
         this.array = ByteArrays.grow(this.array, this.position + var3, this.position);
      }

      System.arraycopy(var1, var2, this.array, this.position, var3);
      if (this.position + var3 > this.length) {
         this.length = this.position += var3;
      }

   }

   public void position(long var1) {
      if (this.position > 2147483647) {
         throw new IllegalArgumentException("Position too large: " + var1);
      } else {
         this.position = (int)var1;
      }
   }

   public long position() {
      return (long)this.position;
   }

   public long length() throws IOException {
      return (long)this.length;
   }
}
