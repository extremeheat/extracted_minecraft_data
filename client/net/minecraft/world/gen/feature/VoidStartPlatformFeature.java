package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class VoidStartPlatformFeature extends Feature<NoFeatureConfig> {
   public VoidStartPlatformFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoFeatureConfig var5) {
      BlockPos var6 = var1.func_175694_M();
      boolean var7 = true;
      double var8 = var6.func_177951_i(var4.func_177982_a(8, var6.func_177956_o(), 8));
      if (var8 > 1024.0D) {
         return true;
      } else {
         BlockPos var10 = new BlockPos(var6.func_177958_n() - 16, Math.max(var6.func_177956_o(), 4) - 1, var6.func_177952_p() - 16);
         BlockPos var11 = new BlockPos(var6.func_177958_n() + 16, Math.max(var6.func_177956_o(), 4) - 1, var6.func_177952_p() + 16);
         BlockPos.MutableBlockPos var12 = new BlockPos.MutableBlockPos(var10);

         for(int var13 = var4.func_177952_p(); var13 < var4.func_177952_p() + 16; ++var13) {
            for(int var14 = var4.func_177958_n(); var14 < var4.func_177958_n() + 16; ++var14) {
               if (var13 >= var10.func_177952_p() && var13 <= var11.func_177952_p() && var14 >= var10.func_177958_n() && var14 <= var11.func_177958_n()) {
                  var12.func_181079_c(var14, var12.func_177956_o(), var13);
                  if (var6.func_177958_n() == var14 && var6.func_177952_p() == var13) {
                     var1.func_180501_a(var12, Blocks.field_150347_e.func_176223_P(), 2);
                  } else {
                     var1.func_180501_a(var12, Blocks.field_150348_b.func_176223_P(), 2);
                  }
               }
            }
         }

         return true;
      }
   }
}
