package net.minecraft.util;

import java.io.OutputStream;
import java.io.PrintStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggingPrintStream extends PrintStream {
   private static final Logger field_179884_a = LogManager.getLogger();
   private final String field_179883_b;

   public LoggingPrintStream(String var1, OutputStream var2) {
      super(var2);
      this.field_179883_b = var1;
   }

   public void println(String var1) {
      this.func_179882_a(var1);
   }

   public void println(Object var1) {
      this.func_179882_a(String.valueOf(var1));
   }

   private void func_179882_a(String var1) {
      StackTraceElement[] var2 = Thread.currentThread().getStackTrace();
      StackTraceElement var3 = var2[Math.min(3, var2.length)];
      field_179884_a.info("[{}]@.({}:{}): {}", new Object[]{this.field_179883_b, var3.getFileName(), var3.getLineNumber(), var1});
   }
}
