package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public class ReedsFeature extends Feature<NoneFeatureConfiguration> {
   public ReedsFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, NoneFeatureConfiguration var5) {
      int var6 = 0;

      for(int var7 = 0; var7 < 20; ++var7) {
         BlockPos var8 = var4.offset(var3.nextInt(4) - var3.nextInt(4), 0, var3.nextInt(4) - var3.nextInt(4));
         if (var1.isEmptyBlock(var8)) {
            BlockPos var9 = var8.below();
            if (var1.getFluidState(var9.west()).is(FluidTags.WATER) || var1.getFluidState(var9.east()).is(FluidTags.WATER) || var1.getFluidState(var9.north()).is(FluidTags.WATER) || var1.getFluidState(var9.south()).is(FluidTags.WATER)) {
               int var10 = 2 + var3.nextInt(var3.nextInt(3) + 1);

               for(int var11 = 0; var11 < var10; ++var11) {
                  if (Blocks.SUGAR_CANE.defaultBlockState().canSurvive(var1, var8)) {
                     var1.setBlock(var8.above(var11), Blocks.SUGAR_CANE.defaultBlockState(), 2);
                     ++var6;
                  }
               }
            }
         }
      }

      return var6 > 0;
   }
}
