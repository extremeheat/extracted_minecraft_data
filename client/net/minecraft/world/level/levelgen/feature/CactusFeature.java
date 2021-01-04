package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public class CactusFeature extends Feature<NoneFeatureConfiguration> {
   public CactusFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, NoneFeatureConfiguration var5) {
      for(int var6 = 0; var6 < 10; ++var6) {
         BlockPos var7 = var4.offset(var3.nextInt(8) - var3.nextInt(8), var3.nextInt(4) - var3.nextInt(4), var3.nextInt(8) - var3.nextInt(8));
         if (var1.isEmptyBlock(var7)) {
            int var8 = 1 + var3.nextInt(var3.nextInt(3) + 1);

            for(int var9 = 0; var9 < var8; ++var9) {
               if (Blocks.CACTUS.defaultBlockState().canSurvive(var1, var7)) {
                  var1.setBlock(var7.above(var9), Blocks.CACTUS.defaultBlockState(), 2);
               }
            }
         }
      }

      return true;
   }
}
