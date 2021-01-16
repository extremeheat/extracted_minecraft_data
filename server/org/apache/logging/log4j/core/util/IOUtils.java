package org.apache.logging.log4j.core.util;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class IOUtils {
   private static final int DEFAULT_BUFFER_SIZE = 4096;
   public static final int EOF = -1;

   public IOUtils() {
      super();
   }

   public static int copy(Reader var0, Writer var1) throws IOException {
      long var2 = copyLarge(var0, var1);
      return var2 > 2147483647L ? -1 : (int)var2;
   }

   public static long copyLarge(Reader var0, Writer var1) throws IOException {
      return copyLarge(var0, var1, new char[4096]);
   }

   public static long copyLarge(Reader var0, Writer var1, char[] var2) throws IOException {
      long var3;
      int var5;
      for(var3 = 0L; -1 != (var5 = var0.read(var2)); var3 += (long)var5) {
         var1.write(var2, 0, var5);
      }

      return var3;
   }

   public static String toString(Reader var0) throws IOException {
      StringBuilderWriter var1 = new StringBuilderWriter();
      copy(var0, var1);
      return var1.toString();
   }
}
