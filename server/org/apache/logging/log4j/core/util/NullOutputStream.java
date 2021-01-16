package org.apache.logging.log4j.core.util;

import java.io.IOException;
import java.io.OutputStream;

public class NullOutputStream extends OutputStream {
   private static final NullOutputStream INSTANCE = new NullOutputStream();
   /** @deprecated */
   @Deprecated
   public static final NullOutputStream NULL_OUTPUT_STREAM;

   public static NullOutputStream getInstance() {
      return INSTANCE;
   }

   private NullOutputStream() {
      super();
   }

   public void write(byte[] var1, int var2, int var3) {
   }

   public void write(int var1) {
   }

   public void write(byte[] var1) throws IOException {
   }

   static {
      NULL_OUTPUT_STREAM = INSTANCE;
   }
}
