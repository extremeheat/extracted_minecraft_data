package net.minecraft.client;

import java.util.concurrent.Callable;
import net.minecraft.client.renderer.OpenGlHelper;

class Minecraft$8 implements Callable {
   Minecraft$8(Minecraft var1) {
      super();
      this.field_74500_a = var1;
   }

   public String call() {
      return OpenGlHelper.func_153172_c();
   }
}
