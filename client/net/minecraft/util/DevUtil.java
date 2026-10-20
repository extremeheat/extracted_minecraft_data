package net.minecraft.util;

import com.mojang.logging.LogUtils;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Consumer;
import org.slf4j.Logger;

public class DevUtil {
   public static boolean IS_RUNNING_IN_IDE;
   private static Logger LOGGER = LogUtils.getLogger();
   private static Consumer<String> thePauser = (msg) -> {
   };

   public DevUtil() {
      super();
   }

   public static void logAndPauseIfInIde(final String message) {
      LOGGER.error("{}", message);
      if (IS_RUNNING_IN_IDE) {
         doPause(message);
      }

   }

   public static void logAndPauseIfInIde(final String message, final Throwable throwable) {
      LOGGER.error("{}", message, throwable);
      if (IS_RUNNING_IN_IDE) {
         doPause(message);
      }

   }

   public static <T extends Throwable> T pauseInIde(final T t) {
      if (IS_RUNNING_IN_IDE) {
         LOGGER.error("Trying to throw a fatal exception, pausing in IDE", t);
         doPause(t.getMessage());
      }

      return t;
   }

   public static void setPause(final Consumer<String> pauseFunction) {
      thePauser = pauseFunction;
   }

   private static void doPause(final String message) {
      Instant preLog = Instant.now();
      LOGGER.warn("Did you remember to set a breakpoint here?");
      boolean dontBotherWithPause = Duration.between(preLog, Instant.now()).toMillis() > 500L;
      if (!dontBotherWithPause) {
         thePauser.accept(message);
      }

   }
}
