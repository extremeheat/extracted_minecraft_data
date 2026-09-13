package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.lwjgl.opengl.GL11;

public class GLAllocation {
   private static final Map field_74531_a = new HashMap();
   private static final List field_74530_b = new ArrayList();

   public static synchronized int func_74526_a(int var0) {
      int var1 = GL11.glGenLists(var0);
      field_74531_a.put(var1, var0);
      return var1;
   }

   public static synchronized void func_74523_b(int var0) {
      GL11.glDeleteLists(var0, field_74531_a.remove(var0));
   }

   public static synchronized void func_74525_a() {
      for(Entry var1 : field_74531_a.entrySet()) {
         GL11.glDeleteLists(var1.getKey(), var1.getValue());
      }

      field_74531_a.clear();
   }

   public static synchronized ByteBuffer func_74524_c(int var0) {
      return ByteBuffer.allocateDirect(var0).order(ByteOrder.nativeOrder());
   }

   public static IntBuffer func_74527_f(int var0) {
      return func_74524_c(var0 << 2).asIntBuffer();
   }

   public static FloatBuffer func_74529_h(int var0) {
      return func_74524_c(var0 << 2).asFloatBuffer();
   }
}
