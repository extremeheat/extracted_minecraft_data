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

public class MelonFeature extends Feature<NoneFeatureConfiguration> {
   public MelonFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, NoneFeatureConfiguration var5) {
      for(int var6 = 0; var6 < 64; ++var6) {
         BlockPos var7 = var4.offset(var3.nextInt(8) - var3.nextInt(8), var3.nextInt(4) - var3.nextInt(4), var3.nextInt(8) - var3.nextInt(8));
         BlockState var8 = Blocks.MELON.defaultBlockState();
         if (var1.getBlockState(var7).getMaterial().isReplaceable() && var1.getBlockState(var7.below()).getBlock() == Blocks.GRASS_BLOCK) {
            var1.setBlock(var7, var8, 2);
         }
      }

      return true;
   }
}
