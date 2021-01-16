package io.netty.util.internal;

final class NativeLibraryUtil {
   public static void loadLibrary(String var0, boolean var1) {
      if (var1) {
         System.load(var0);
      } else {
         System.loadLibrary(var0);
      }

   }

   private NativeLibraryUtil() {
      super();
   }
}
