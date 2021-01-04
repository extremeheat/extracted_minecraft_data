package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public class CentralSpikedFeature extends Feature<NoneFeatureConfiguration> {
   protected final BlockState blockState;

   public CentralSpikedFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1, BlockState var2) {
      super(var1);
      this.blockState = var2;
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, NoneFeatureConfiguration var5) {
      int var6 = 0;

      for(int var7 = 0; var7 < 64; ++var7) {
         BlockPos var8 = var4.offset(var3.nextInt(8) - var3.nextInt(8), var3.nextInt(4) - var3.nextInt(4), var3.nextInt(8) - var3.nextInt(8));
         if (var1.isEmptyBlock(var8) && var1.getBlockState(var8.below()).getBlock() == Blocks.GRASS_BLOCK) {
            var1.setBlock(var8, this.blockState, 2);
            ++var6;
         }
      }

      return var6 > 0;
   }
}
