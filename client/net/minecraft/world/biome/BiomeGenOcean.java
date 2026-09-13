package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class BiomeGenOcean extends BiomeGenBase {
   public BiomeGenOcean(int var1) {
      super(var1);
      this.field_76762_K.clear();
   }

   @Override
   public BiomeGenBase$TempCategory func_150561_m() {
      return BiomeGenBase$TempCategory.OCEAN;
   }

   @Override
   public void func_150573_a(World var1, Random var2, Block[] var3, byte[] var4, int var5, int var6, double var7) {
      super.func_150573_a(var1, var2, var3, var4, var5, var6, var7);
   }
}
