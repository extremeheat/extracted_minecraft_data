package org.apache.logging.log4j.core.util;

import java.io.IOException;
import java.io.OutputStream;

public class CloseShieldOutputStream extends OutputStream {
   private final OutputStream delegate;

   public CloseShieldOutputStream(OutputStream var1) {
      super();
      this.delegate = var1;
   }

   public void close() {
   }

   public void flush() throws IOException {
      this.delegate.flush();
   }

   public void write(byte[] var1) throws IOException {
      this.delegate.write(var1);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.delegate.write(var1, var2, var3);
   }

   public void write(int var1) throws IOException {
      this.delegate.write(var1);
   }
}
