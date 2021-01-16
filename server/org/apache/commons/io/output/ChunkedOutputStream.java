package org.apache.commons.io.output;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ChunkedOutputStream extends FilterOutputStream {
   private static final int DEFAULT_CHUNK_SIZE = 4096;
   private final int chunkSize;

   public ChunkedOutputStream(OutputStream var1, int var2) {
      super(var1);
      if (var2 <= 0) {
         throw new IllegalArgumentException();
      } else {
         this.chunkSize = var2;
      }
   }

   public ChunkedOutputStream(OutputStream var1) {
      this(var1, 4096);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      int var4 = var3;

      int var6;
      for(int var5 = var2; var4 > 0; var5 += var6) {
         var6 = Math.min(var4, this.chunkSize);
         this.out.write(var1, var5, var6);
         var4 -= var6;
      }

   }
}
