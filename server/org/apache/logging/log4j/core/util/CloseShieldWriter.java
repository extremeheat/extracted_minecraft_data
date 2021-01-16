package org.apache.logging.log4j.core.util;

import java.io.IOException;
import java.io.Writer;

public class CloseShieldWriter extends Writer {
   private final Writer delegate;

   public CloseShieldWriter(Writer var1) {
      super();
      this.delegate = var1;
   }

   public void close() throws IOException {
   }

   public void flush() throws IOException {
      this.delegate.flush();
   }

   public void write(char[] var1, int var2, int var3) throws IOException {
      this.delegate.write(var1, var2, var3);
   }
}
