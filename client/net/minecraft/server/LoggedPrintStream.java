package net.minecraft.server;

import com.mojang.logging.LogUtils;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class LoggedPrintStream extends PrintStream {
   private static final Logger LOGGER = LogUtils.getLogger();
   protected final String name;

   public LoggedPrintStream(String var1, OutputStream var2) {
      super(var2, false, StandardCharsets.UTF_8);
      this.name = var1;
   }

   public void println(@Nullable String var1) {
      this.logLine(var1);
   }

   public void println(@Nullable Object var1) {
      this.logLine(String.valueOf(var1));
   }

   protected void logLine(@Nullable String var1) {
      LOGGER.info("[{}]: {}", this.name, var1);
   }
}
