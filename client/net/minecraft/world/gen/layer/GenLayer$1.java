package net.minecraft.world.gen.layer;

import java.util.concurrent.Callable;
import net.minecraft.world.biome.BiomeGenBase;

final class GenLayer$1 implements Callable {
   GenLayer$1(int var1) {
      super();
      this.field_151684_a = var1;
   }

   public String call() {
      return String.valueOf(BiomeGenBase.func_150568_d(this.field_151684_a));
   }
}
