package net.minecraft.server;

import java.io.OutputStream;

public class DebugLoggedPrintStream extends LoggedPrintStream {
   public DebugLoggedPrintStream(String var1, OutputStream var2) {
      super(var1, var2);
   }

   protected void logLine(String var1) {
      StackTraceElement[] var2 = Thread.currentThread().getStackTrace();
      StackTraceElement var3 = var2[Math.min(3, var2.length)];
      LOGGER.info("[{}]@.({}:{}): {}", this.name, var3.getFileName(), var3.getLineNumber(), var1);
   }
}
