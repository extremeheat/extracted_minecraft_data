package net.minecraft.client.gui.font.providers;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.freetype.FT_Vector;
import org.lwjgl.util.freetype.FreeType;

public class FreeTypeUtil {
   private static long library = 0L;

   public FreeTypeUtil() {
      super();
   }

   public static long getLibrary() {
      if (library == 0L) {
         MemoryStack var0 = MemoryStack.stackPush();

         try {
            PointerBuffer var1 = var0.mallocPointer(1);
            checkError(FreeType.FT_Init_FreeType(var1), "Initializing FreeType library");
            library = var1.get();
         } catch (Throwable var4) {
            if (var0 != null) {
               try {
                  var0.close();
               } catch (Throwable var3) {
                  var4.addSuppressed(var3);
               }
            }

            throw var4;
         }

         if (var0 != null) {
            var0.close();
         }
      }

      return library;
   }

   public static void checkError(int var0, String var1) {
      if (var0 != 0) {
         String var10002 = describeError(var0);
         throw new IllegalStateException("FreeType error: " + var10002 + " (" + var1 + ")");
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
      if (library != 0L) {
         FreeType.FT_Done_Library(library);
         library = 0L;
      }

   }
}
