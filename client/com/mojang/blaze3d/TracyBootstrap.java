package com.mojang.blaze3d;

import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogListeners;
import org.slf4j.event.Level;

public class TracyBootstrap {
   private static boolean setup;

   public TracyBootstrap() {
      super();
   }

   public static void setup() {
      if (!setup) {
         TracyClient.load();
         if (TracyClient.isAvailable()) {
            LogListeners.addListener("Tracy", (message, level) -> TracyClient.message(message, messageColor(level)));
            setup = true;
         }
      }
   }

   private static int messageColor(final Level level) {
      int var10000;
      switch (level) {
         case DEBUG -> var10000 = 11184810;
         case WARN -> var10000 = 16777130;
         case ERROR -> var10000 = 16755370;
         default -> var10000 = 16777215;
      }

      return var10000;
   }
}
