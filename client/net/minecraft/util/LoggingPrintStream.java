package net.minecraft.util;

import java.io.OutputStream;
import java.io.PrintStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggingPrintStream extends PrintStream {
   protected static final Logger field_179884_a = LogManager.getLogger();
   protected final String field_179883_b;

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

   protected void func_179882_a(String var1) {
      field_179884_a.info("[{}]: {}", this.field_179883_b, var1);
   }
}
