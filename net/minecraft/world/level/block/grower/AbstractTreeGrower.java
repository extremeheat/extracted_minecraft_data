package net.minecraft.world.level.block.grower;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SmallTreeConfiguration;

public abstract class AbstractTreeGrower {
   @Nullable
   protected abstract ConfiguredFeature getConfiguredFeature(Random var1);

   public boolean growTree(LevelAccessor var1, ChunkGenerator var2, BlockPos var3, BlockState var4, Random var5) {
      ConfiguredFeature var6 = this.getConfiguredFeature(var5);
      if (var6 == null) {
         return false;
      } else {
         var1.setBlock(var3, Blocks.AIR.defaultBlockState(), 4);
         ((SmallTreeConfiguration)var6.config).setFromSapling();
         if (var6.place(var1, var2, var5, var3)) {
            return true;
         } else {
            var1.setBlock(var3, var4, 4);
            return false;
         }
      }
   }
}
