package org.apache.logging.log4j.util;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Objects;

final class LowLevelLogUtil {
   private static PrintWriter writer;

   public static void logException(Throwable var0) {
      var0.printStackTrace(writer);
   }

   public static void logException(String var0, Throwable var1) {
      if (var0 != null) {
         writer.println(var0);
      }

      logException(var1);
   }

   public static void setOutputStream(OutputStream var0) {
      writer = new PrintWriter((OutputStream)Objects.requireNonNull(var0), true);
   }

   public static void setWriter(Writer var0) {
      writer = new PrintWriter((Writer)Objects.requireNonNull(var0), true);
   }

   private LowLevelLogUtil() {
      super();
   }

   static {
      writer = new PrintWriter(System.err, true);
   }
}
