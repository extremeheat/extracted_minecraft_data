package net.minecraft.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import org.apache.logging.log4j.Logger;

public class Util {
   public static Util.EnumOS func_110647_a() {
      String var0 = System.getProperty("os.name").toLowerCase();
      if (var0.contains("win")) {
         return Util.EnumOS.WINDOWS;
      } else if (var0.contains("mac")) {
         return Util.EnumOS.OSX;
      } else if (var0.contains("solaris")) {
         return Util.EnumOS.SOLARIS;
      } else if (var0.contains("sunos")) {
         return Util.EnumOS.SOLARIS;
      } else if (var0.contains("linux")) {
         return Util.EnumOS.LINUX;
      } else {
         return var0.contains("unix") ? Util.EnumOS.LINUX : Util.EnumOS.UNKNOWN;
      }
   }

   public static <V> V func_181617_a(FutureTask<V> var0, Logger var1) {
      try {
         var0.run();
         return var0.get();
      } catch (ExecutionException var3) {
         var1.fatal("Error executing task", var3);
      } catch (InterruptedException var4) {
         var1.fatal("Error executing task", var4);
      }

      return null;
   }

   public static enum EnumOS {
      LINUX,
      SOLARIS,
      WINDOWS,
      OSX,
      UNKNOWN;

      private EnumOS() {
      }
   }
}
