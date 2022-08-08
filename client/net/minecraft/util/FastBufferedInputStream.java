package net.minecraft.util;

import java.io.IOException;
import java.io.InputStream;

public class FastBufferedInputStream extends InputStream {
   private static final int DEFAULT_BUFFER_SIZE = 8192;
   private final InputStream in;
   private final byte[] buffer;
   private int limit;
   private int position;

   public FastBufferedInputStream(InputStream var1) {
      this(var1, 8192);
   }

   public FastBufferedInputStream(InputStream var1, int var2) {
      super();
      this.in = var1;
      this.buffer = new byte[var2];
   }

   public int read() throws IOException {
      if (this.position >= this.limit) {
         this.fill();
         if (this.position >= this.limit) {
            return -1;
         }
      }

      return Byte.toUnsignedInt(this.buffer[this.position++]);
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      int var4 = this.bytesInBuffer();
      if (var4 <= 0) {
         if (var3 >= this.buffer.length) {
            return this.in.read(var1, var2, var3);
         }

         this.fill();
         var4 = this.bytesInBuffer();
         if (var4 <= 0) {
            return -1;
         }
      }

      if (var3 > var4) {
         var3 = var4;
      }

      System.arraycopy(this.buffer, this.position, var1, var2, var3);
      this.position += var3;
      return var3;
   }

   public long skip(long var1) throws IOException {
      if (var1 <= 0L) {
         return 0L;
      } else {
         long var3 = (long)this.bytesInBuffer();
         if (var3 <= 0L) {
            return this.in.skip(var1);
         } else {
            if (var1 > var3) {
               var1 = var3;
            }

            this.position = (int)((long)this.position + var1);
            return var1;
         }
      }
   }

   public int available() throws IOException {
      return this.bytesInBuffer() + this.in.available();
   }

   public void close() throws IOException {
      this.in.close();
   }

   private int bytesInBuffer() {
      return this.limit - this.position;
   }

   private void fill() throws IOException {
      this.limit = 0;
      this.position = 0;
      int var1 = this.in.read(this.buffer, 0, this.buffer.length);
      if (var1 > 0) {
         this.limit = var1;
      }

   }
}
