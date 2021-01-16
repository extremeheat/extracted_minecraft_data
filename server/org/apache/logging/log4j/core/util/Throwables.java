package org.apache.logging.log4j.core.util;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public final class Throwables {
   private Throwables() {
      super();
   }

   public static Throwable getRootCause(Throwable var0) {
      Throwable var1;
      Throwable var2;
      for(var2 = var0; (var1 = var2.getCause()) != null; var2 = var1) {
      }

      return var2;
   }

   public static List<String> toStringList(Throwable var0) {
      StringWriter var1 = new StringWriter();
      PrintWriter var2 = new PrintWriter(var1);

      try {
         var0.printStackTrace(var2);
      } catch (RuntimeException var10) {
      }

      var2.flush();
      ArrayList var3 = new ArrayList();
      LineNumberReader var4 = new LineNumberReader(new StringReader(var1.toString()));

      try {
         for(String var5 = var4.readLine(); var5 != null; var5 = var4.readLine()) {
            var3.add(var5);
         }
      } catch (IOException var11) {
         if (var11 instanceof InterruptedIOException) {
            Thread.currentThread().interrupt();
         }

         var3.add(var11.toString());
      } finally {
         Closer.closeSilently(var4);
      }

      return var3;
   }

   public static void rethrow(Throwable var0) {
      rethrow0(var0);
   }

   private static <T extends Throwable> void rethrow0(Throwable var0) throws T {
      throw var0;
   }
}
