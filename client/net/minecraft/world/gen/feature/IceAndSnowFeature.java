package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockDirtSnowy;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class IceAndSnowFeature extends Feature<NoFeatureConfig> {
   public IceAndSnowFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoFeatureConfig var5) {
      BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();
      BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos();

      for(int var8 = 0; var8 < 16; ++var8) {
         for(int var9 = 0; var9 < 16; ++var9) {
            int var10 = var4.func_177958_n() + var8;
            int var11 = var4.func_177952_p() + var9;
            int var12 = var1.func_201676_a(Heightmap.Type.MOTION_BLOCKING, var10, var11);
            var6.func_181079_c(var10, var12, var11);
            var7.func_189533_g(var6).func_189534_c(EnumFacing.DOWN, 1);
            Biome var13 = var1.func_180494_b(var6);
            if (var13.func_201854_a(var1, var7, false)) {
               var1.func_180501_a(var7, Blocks.field_150432_aD.func_176223_P(), 2);
            }

            if (var13.func_201850_b(var1, var6)) {
               var1.func_180501_a(var6, Blocks.field_150433_aE.func_176223_P(), 2);
               IBlockState var14 = var1.func_180495_p(var7);
               if (var14.func_196959_b(BlockDirtSnowy.field_196382_a)) {
                  var1.func_180501_a(var7, (IBlockState)var14.func_206870_a(BlockDirtSnowy.field_196382_a, true), 2);
               }
            }
         }
      }

      return true;
   }
}
