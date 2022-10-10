package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class IcePathFeature extends Feature<FeatureRadiusConfig> {
   private final Block field_150555_a;

   public IcePathFeature() {
      super();
      this.field_150555_a = Blocks.field_150403_cj;
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, FeatureRadiusConfig var5) {
      while(var1.func_175623_d(var4) && var4.func_177956_o() > 2) {
         var4 = var4.func_177977_b();
      }

      if (var1.func_180495_p(var4).func_177230_c() != Blocks.field_196604_cC) {
         return false;
      } else {
         int var6 = var3.nextInt(var5.field_202436_a) + 2;
         boolean var7 = true;

         for(int var8 = var4.func_177958_n() - var6; var8 <= var4.func_177958_n() + var6; ++var8) {
            for(int var9 = var4.func_177952_p() - var6; var9 <= var4.func_177952_p() + var6; ++var9) {
               int var10 = var8 - var4.func_177958_n();
               int var11 = var9 - var4.func_177952_p();
               if (var10 * var10 + var11 * var11 <= var6 * var6) {
                  for(int var12 = var4.func_177956_o() - 1; var12 <= var4.func_177956_o() + 1; ++var12) {
                     BlockPos var13 = new BlockPos(var8, var12, var9);
                     Block var14 = var1.func_180495_p(var13).func_177230_c();
                     if (Block.func_196245_f(var14) || var14 == Blocks.field_196604_cC || var14 == Blocks.field_150432_aD) {
                        var1.func_180501_a(var13, this.field_150555_a.func_176223_P(), 2);
                     }
                  }
               }
            }
         }

         return true;
      }
   }
}
