package net.minecraft.util;

public class Util {
   public static Util$EnumOS func_110647_a() {
      String var0 = System.getProperty("os.name").toLowerCase();
      if (var0.contains("win")) {
         return Util$EnumOS.WINDOWS;
      } else if (var0.contains("mac")) {
         return Util$EnumOS.OSX;
      } else if (var0.contains("solaris")) {
         return Util$EnumOS.SOLARIS;
      } else if (var0.contains("sunos")) {
         return Util$EnumOS.SOLARIS;
      } else if (var0.contains("linux")) {
         return Util$EnumOS.LINUX;
      } else {
         return var0.contains("unix") ? Util$EnumOS.LINUX : Util$EnumOS.UNKNOWN;
      }
   }
}
