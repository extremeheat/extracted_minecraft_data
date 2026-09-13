package net.minecraft.client;

import java.util.concurrent.Callable;
import org.lwjgl.Sys;

class Minecraft$6 implements Callable {
   Minecraft$6(Minecraft var1) {
      super();
      this.field_74503_a = var1;
   }

   public String call() {
      return Sys.getVersion();
   }
}
