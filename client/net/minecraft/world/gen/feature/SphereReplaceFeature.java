package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class SphereReplaceFeature extends Feature<SphereReplaceConfig> {
   public SphereReplaceFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, SphereReplaceConfig var5) {
      if (!var1.func_204610_c(var4).func_206884_a(FluidTags.field_206959_a)) {
         return false;
      } else {
         int var6 = 0;
         int var7 = var3.nextInt(var5.field_202432_b - 2) + 2;

         for(int var8 = var4.func_177958_n() - var7; var8 <= var4.func_177958_n() + var7; ++var8) {
            for(int var9 = var4.func_177952_p() - var7; var9 <= var4.func_177952_p() + var7; ++var9) {
               int var10 = var8 - var4.func_177958_n();
               int var11 = var9 - var4.func_177952_p();
               if (var10 * var10 + var11 * var11 <= var7 * var7) {
                  for(int var12 = var4.func_177956_o() - var5.field_202433_c; var12 <= var4.func_177956_o() + var5.field_202433_c; ++var12) {
                     BlockPos var13 = new BlockPos(var8, var12, var9);
                     Block var14 = var1.func_180495_p(var13).func_177230_c();
                     if (var5.field_202434_d.contains(var14)) {
                        var1.func_180501_a(var13, var5.field_202431_a.func_176223_P(), 2);
                        ++var6;
                     }
                  }
               }
            }
         }

         return var6 > 0;
      }
   }
}
