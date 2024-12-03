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
            LogListeners.addListener("Tracy", (var0, var1) -> TracyClient.message(var0, messageColor(var1)));
            setup = true;
         }
      }
   }

   private static int messageColor(Level var0) {
      int var10000;
      switch (var0) {
         case DEBUG -> var10000 = 11184810;
         case WARN -> var10000 = 16777130;
         case ERROR -> var10000 = 16755370;
         default -> var10000 = 16777215;
      }

      return var10000;
   }
}
