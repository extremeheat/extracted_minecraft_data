package net.minecraft.server;

import java.io.OutputStream;
import net.minecraft.util.LoggingPrintStream;

public class DebugLoggingPrintStream extends LoggingPrintStream {
   public DebugLoggingPrintStream(String var1, OutputStream var2) {
      super(var1, var2);
   }

   protected void func_179882_a(String var1) {
      StackTraceElement[] var2 = Thread.currentThread().getStackTrace();
      StackTraceElement var3 = var2[Math.min(3, var2.length)];
      field_179884_a.info("[{}]@.({}:{}): {}", this.field_179883_b, var3.getFileName(), var3.getLineNumber(), var1);
   }
}
