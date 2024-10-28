package net.minecraft.client.gui.font.providers;

import com.mojang.logging.LogUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.freetype.FT_Vector;
import org.lwjgl.util.freetype.FreeType;
import org.slf4j.Logger;

public class FreeTypeUtil {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Object LIBRARY_LOCK = new Object();
   private static long library = 0L;

   public FreeTypeUtil() {
      super();
   }

   public static long getLibrary() {
      synchronized(LIBRARY_LOCK) {
         if (library == 0L) {
            MemoryStack var1 = MemoryStack.stackPush();

            try {
               PointerBuffer var2 = var1.mallocPointer(1);
               assertError(FreeType.FT_Init_FreeType(var2), "Initializing FreeType library");
               library = var2.get();
            } catch (Throwable var6) {
               if (var1 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }
               }

               throw var6;
            }

            if (var1 != null) {
               var1.close();
            }
         }

         return library;
      }
   }

   public static void assertError(int var0, String var1) {
      if (var0 != 0) {
         String var10002 = describeError(var0);
         throw new IllegalStateException("FreeType error: " + var10002 + " (" + var1 + ")");
      }
   }

   public static boolean checkError(int var0, String var1) {
      if (var0 != 0) {
         LOGGER.error("FreeType error: {} ({})", describeError(var0), var1);
         return true;
      } else {
         return false;
      }
   }

   private static String describeError(int var0) {
      String var1 = FreeType.FT_Error_String(var0);
      return var1 != null ? var1 : "Unrecognized error: 0x" + Integer.toHexString(var0);
   }

   public static FT_Vector setVector(FT_Vector var0, float var1, float var2) {
      long var3 = (long)Math.round(var1 * 64.0F);
      long var5 = (long)Math.round(var2 * 64.0F);
      return var0.set(var3, var5);
   }

   public static float x(FT_Vector var0) {
      return (float)var0.x() / 64.0F;
   }

   public static void destroy() {
      synchronized(LIBRARY_LOCK) {
         if (library != 0L) {
            FreeType.FT_Done_Library(library);
            library = 0L;
         }

      }
   }
}
