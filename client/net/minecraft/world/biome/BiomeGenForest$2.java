package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.world.World;

class BiomeGenForest$2 extends BiomeGenMutated {
   BiomeGenForest$2(BiomeGenForest var1, int var2, BiomeGenBase var3) {
      super(var2, var3);
      this.field_150613_aC = var1;
   }

   @Override
   public void func_76728_a(World var1, Random var2, int var3, int var4) {
      this.field_150611_aD.func_76728_a(var1, var2, var3, var4);
   }
}
