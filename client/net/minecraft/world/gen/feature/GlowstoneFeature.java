package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class GlowstoneFeature extends Feature<NoFeatureConfig> {
   public GlowstoneFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoFeatureConfig var5) {
      if (!var1.func_175623_d(var4)) {
         return false;
      } else if (var1.func_180495_p(var4.func_177984_a()).func_177230_c() != Blocks.field_150424_aL) {
         return false;
      } else {
         var1.func_180501_a(var4, Blocks.field_150426_aN.func_176223_P(), 2);

         for(int var6 = 0; var6 < 1500; ++var6) {
            BlockPos var7 = var4.func_177982_a(var3.nextInt(8) - var3.nextInt(8), -var3.nextInt(12), var3.nextInt(8) - var3.nextInt(8));
            if (var1.func_180495_p(var7).func_196958_f()) {
               int var8 = 0;
               EnumFacing[] var9 = EnumFacing.values();
               int var10 = var9.length;

               for(int var11 = 0; var11 < var10; ++var11) {
                  EnumFacing var12 = var9[var11];
                  if (var1.func_180495_p(var7.func_177972_a(var12)).func_177230_c() == Blocks.field_150426_aN) {
                     ++var8;
                  }

                  if (var8 > 1) {
                     break;
                  }
               }

               if (var8 == 1) {
                  var1.func_180501_a(var7, Blocks.field_150426_aN.func_176223_P(), 2);
               }
            }
         }

         return true;
      }
   }
}
