package org.apache.commons.io.output;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

public class ChunkedWriter extends FilterWriter {
   private static final int DEFAULT_CHUNK_SIZE = 4096;
   private final int chunkSize;

   public ChunkedWriter(Writer var1, int var2) {
      super(var1);
      if (var2 <= 0) {
         throw new IllegalArgumentException();
      } else {
         this.chunkSize = var2;
      }
   }

   public ChunkedWriter(Writer var1) {
      this(var1, 4096);
   }

   public void write(char[] var1, int var2, int var3) throws IOException {
      int var4 = var3;

      int var6;
      for(int var5 = var2; var4 > 0; var5 += var6) {
         var6 = Math.min(var4, this.chunkSize);
         this.out.write(var1, var5, var6);
         var4 -= var6;
      }

   }
}
