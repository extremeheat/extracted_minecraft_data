package net.minecraft.client;

import java.util.concurrent.Callable;
import org.lwjgl.opengl.GL11;

class Minecraft$7 implements Callable {
   Minecraft$7(Minecraft var1) {
      super();
      this.field_79002_a = var1;
   }

   public String call() {
      return GL11.glGetString(7937) + " GL version " + GL11.glGetString(7938) + ", " + GL11.glGetString(7936);
   }
}
