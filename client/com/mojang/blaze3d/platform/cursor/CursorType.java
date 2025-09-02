package com.mojang.blaze3d.platform.cursor;

import com.mojang.blaze3d.platform.Window;
import org.lwjgl.glfw.GLFW;

public class CursorType {
   public static final CursorType DEFAULT = new CursorType("default", 0L);
   private final String name;
   private final long handle;

   private CursorType(String var1, long var2) {
      super();
      this.name = var1;
      this.handle = var2;
   }

   public void select(Window var1) {
      GLFW.glfwSetCursor(var1.handle(), this.handle);
   }

   public String toString() {
      return this.name;
   }

   public static CursorType createStandardCursor(int var0, String var1, CursorType var2) {
      long var3 = GLFW.glfwCreateStandardCursor(var0);
      return var3 == 0L ? var2 : new CursorType(var1, var3);
   }
}
