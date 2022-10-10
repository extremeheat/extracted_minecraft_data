package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLAllocation {
   public static synchronized int func_74526_a(int var0) {
      int var1 = GlStateManager.func_187442_t(var0);
      if (var1 == 0) {
         int var2 = GlStateManager.func_187434_L();
         String var3 = "No error code reported";
         if (var2 != 0) {
            var3 = OpenGlHelper.func_195917_n(var2);
         }

         throw new IllegalStateException("glGenLists returned an ID of 0 for a count of " + var0 + ", GL error (" + var2 + "): " + var3);
      } else {
         return var1;
      }
   }

   public static synchronized void func_178874_a(int var0, int var1) {
      GlStateManager.func_187449_e(var0, var1);
   }

   public static synchronized void func_74523_b(int var0) {
      func_178874_a(var0, 1);
   }

   public static synchronized ByteBuffer func_74524_c(int var0) {
      return ByteBuffer.allocateDirect(var0).order(ByteOrder.nativeOrder());
   }

   public static FloatBuffer func_74529_h(int var0) {
      return func_74524_c(var0 << 2).asFloatBuffer();
   }
}
