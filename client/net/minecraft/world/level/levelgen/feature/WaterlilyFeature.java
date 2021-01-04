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

public class WaterlilyFeature extends Feature<NoneFeatureConfiguration> {
   public WaterlilyFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, NoneFeatureConfiguration var5) {
      BlockPos var7;
      for(BlockPos var6 = var4; var6.getY() > 0; var6 = var7) {
         var7 = var6.below();
         if (!var1.isEmptyBlock(var7)) {
            break;
         }
      }

      for(int var10 = 0; var10 < 10; ++var10) {
         BlockPos var8 = var4.offset(var3.nextInt(8) - var3.nextInt(8), var3.nextInt(4) - var3.nextInt(4), var3.nextInt(8) - var3.nextInt(8));
         BlockState var9 = Blocks.LILY_PAD.defaultBlockState();
         if (var1.isEmptyBlock(var8) && var9.canSurvive(var1, var8)) {
            var1.setBlock(var8, var9, 2);
         }
      }

      return true;
   }
}
