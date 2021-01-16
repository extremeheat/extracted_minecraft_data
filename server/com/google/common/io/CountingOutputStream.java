package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Beta
@GwtIncompatible
public final class CountingOutputStream extends FilterOutputStream {
   private long count;

   public CountingOutputStream(OutputStream var1) {
      super((OutputStream)Preconditions.checkNotNull(var1));
   }

   public long getCount() {
      return this.count;
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.out.write(var1, var2, var3);
      this.count += (long)var3;
   }

   public void write(int var1) throws IOException {
      this.out.write(var1);
      ++this.count;
   }

   public void close() throws IOException {
      this.out.close();
   }
}
