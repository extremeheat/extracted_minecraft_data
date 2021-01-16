package org.apache.commons.io.input;

import java.io.IOException;
import java.io.Reader;

public class BoundedReader extends Reader {
   private static final int INVALID = -1;
   private final Reader target;
   private int charsRead = 0;
   private int markedAt = -1;
   private int readAheadLimit;
   private final int maxCharsFromTargetReader;

   public BoundedReader(Reader var1, int var2) throws IOException {
      super();
      this.target = var1;
      this.maxCharsFromTargetReader = var2;
   }

   public void close() throws IOException {
      this.target.close();
   }

   public void reset() throws IOException {
      this.charsRead = this.markedAt;
      this.target.reset();
   }

   public void mark(int var1) throws IOException {
      this.readAheadLimit = var1 - this.charsRead;
      this.markedAt = this.charsRead;
      this.target.mark(var1);
   }

   public int read() throws IOException {
      if (this.charsRead >= this.maxCharsFromTargetReader) {
         return -1;
      } else if (this.markedAt >= 0 && this.charsRead - this.markedAt >= this.readAheadLimit) {
         return -1;
      } else {
         ++this.charsRead;
         return this.target.read();
      }
   }

   public int read(char[] var1, int var2, int var3) throws IOException {
      for(int var5 = 0; var5 < var3; ++var5) {
         int var4 = this.read();
         if (var4 == -1) {
            return var5;
         }

         var1[var2 + var5] = (char)var4;
      }

      return var3;
   }
}
