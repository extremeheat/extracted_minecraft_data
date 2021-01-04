package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.KelpBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.levelgen.Heightmap;

public class KelpFeature extends Feature<NoneFeatureConfiguration> {
   public KelpFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, NoneFeatureConfiguration var5) {
      int var6 = 0;
      int var7 = var1.getHeight(Heightmap.Types.OCEAN_FLOOR, var4.getX(), var4.getZ());
      BlockPos var8 = new BlockPos(var4.getX(), var7, var4.getZ());
      if (var1.getBlockState(var8).getBlock() == Blocks.WATER) {
         BlockState var9 = Blocks.KELP.defaultBlockState();
         BlockState var10 = Blocks.KELP_PLANT.defaultBlockState();
         int var11 = 1 + var3.nextInt(10);

         for(int var12 = 0; var12 <= var11; ++var12) {
            if (var1.getBlockState(var8).getBlock() == Blocks.WATER && var1.getBlockState(var8.above()).getBlock() == Blocks.WATER && var10.canSurvive(var1, var8)) {
               if (var12 == var11) {
                  var1.setBlock(var8, (BlockState)var9.setValue(KelpBlock.AGE, var3.nextInt(23)), 2);
                  ++var6;
               } else {
                  var1.setBlock(var8, var10, 2);
               }
            } else if (var12 > 0) {
               BlockPos var13 = var8.below();
               if (var9.canSurvive(var1, var13) && var1.getBlockState(var13.below()).getBlock() != Blocks.KELP) {
                  var1.setBlock(var13, (BlockState)var9.setValue(KelpBlock.AGE, var3.nextInt(23)), 2);
                  ++var6;
               }
               break;
            }

            var8 = var8.above();
         }
      }

      return var6 > 0;
   }
}
