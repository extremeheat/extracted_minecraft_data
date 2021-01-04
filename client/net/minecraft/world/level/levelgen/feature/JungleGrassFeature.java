package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public class JungleGrassFeature extends Feature<NoneFeatureConfiguration> {
   public JungleGrassFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public BlockState getState(Random var1) {
      return var1.nextInt(4) == 0 ? Blocks.FERN.defaultBlockState() : Blocks.GRASS.defaultBlockState();
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, NoneFeatureConfiguration var5) {
      BlockState var6 = this.getState(var3);

      for(BlockState var7 = var1.getBlockState(var4); (var7.isAir() || var7.is(BlockTags.LEAVES)) && var4.getY() > 0; var7 = var1.getBlockState(var4)) {
         var4 = var4.below();
      }

      int var8 = 0;

      for(int var9 = 0; var9 < 128; ++var9) {
         BlockPos var10 = var4.offset(var3.nextInt(8) - var3.nextInt(8), var3.nextInt(4) - var3.nextInt(4), var3.nextInt(8) - var3.nextInt(8));
         if (var1.isEmptyBlock(var10) && var1.getBlockState(var10.below()).getBlock() != Blocks.PODZOL && var6.canSurvive(var1, var10)) {
            var1.setBlock(var10, var6, 2);
            ++var8;
         }
      }

      return var8 > 0;
   }
}
