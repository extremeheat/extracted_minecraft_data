package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class ReedFeature extends Feature<NoFeatureConfig> {
   public ReedFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoFeatureConfig var5) {
      int var6 = 0;

      for(int var7 = 0; var7 < 20; ++var7) {
         BlockPos var8 = var4.func_177982_a(var3.nextInt(4) - var3.nextInt(4), 0, var3.nextInt(4) - var3.nextInt(4));
         if (var1.func_175623_d(var8)) {
            BlockPos var9 = var8.func_177977_b();
            if (var1.func_204610_c(var9.func_177976_e()).func_206884_a(FluidTags.field_206959_a) || var1.func_204610_c(var9.func_177974_f()).func_206884_a(FluidTags.field_206959_a) || var1.func_204610_c(var9.func_177978_c()).func_206884_a(FluidTags.field_206959_a) || var1.func_204610_c(var9.func_177968_d()).func_206884_a(FluidTags.field_206959_a)) {
               int var10 = 2 + var3.nextInt(var3.nextInt(3) + 1);

               for(int var11 = 0; var11 < var10; ++var11) {
                  if (Blocks.field_196608_cF.func_176223_P().func_196955_c(var1, var8)) {
                     var1.func_180501_a(var8.func_177981_b(var11), Blocks.field_196608_cF.func_176223_P(), 2);
                     ++var6;
                  }
               }
            }
         }
      }

      return var6 > 0;
   }
}
