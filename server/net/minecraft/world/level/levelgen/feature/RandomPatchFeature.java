package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;

public class RandomPatchFeature extends Feature<RandomPatchConfiguration> {
   public RandomPatchFeature(Codec<RandomPatchConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, RandomPatchConfiguration var5) {
      BlockState var6 = var5.stateProvider.getState(var3, var4);
      BlockPos var7;
      if (var5.project) {
         var7 = var1.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, var4);
      } else {
         var7 = var4;
      }

      int var8 = 0;
      BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();

      for(int var10 = 0; var10 < var5.tries; ++var10) {
         var9.setWithOffset(var7, var3.nextInt(var5.xspread + 1) - var3.nextInt(var5.xspread + 1), var3.nextInt(var5.yspread + 1) - var3.nextInt(var5.yspread + 1), var3.nextInt(var5.zspread + 1) - var3.nextInt(var5.zspread + 1));
         BlockPos var11 = var9.below();
         BlockState var12 = var1.getBlockState(var11);
         if ((var1.isEmptyBlock(var9) || var5.canReplace && var1.getBlockState(var9).getMaterial().isReplaceable()) && var6.canSurvive(var1, var9) && (var5.whitelist.isEmpty() || var5.whitelist.contains(var12.getBlock())) && !var5.blacklist.contains(var12) && (!var5.needWater || var1.getFluidState(var11.west()).is(FluidTags.WATER) || var1.getFluidState(var11.east()).is(FluidTags.WATER) || var1.getFluidState(var11.north()).is(FluidTags.WATER) || var1.getFluidState(var11.south()).is(FluidTags.WATER))) {
            var5.blockPlacer.place(var1, var9, var6, var3);
            ++var8;
         }
      }

      return var8 > 0;
   }
}
