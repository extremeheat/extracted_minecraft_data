package net.minecraft.world.level.levelgen.placement.nether;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

public class CountMultiLayerDecorator extends FeatureDecorator<CountConfiguration> {
   public CountMultiLayerDecorator(Codec<CountConfiguration> var1) {
      super(var1);
   }

   public Stream<BlockPos> getPositions(DecorationContext var1, Random var2, CountConfiguration var3, BlockPos var4) {
      ArrayList var5 = Lists.newArrayList();
      int var7 = 0;

      boolean var6;
      do {
         var6 = false;

         for(int var8 = 0; var8 < var3.count().sample(var2); ++var8) {
            int var9 = var2.nextInt(16) + var4.getX();
            int var10 = var2.nextInt(16) + var4.getZ();
            int var11 = var1.getHeight(Heightmap.Types.MOTION_BLOCKING, var9, var10);
            int var12 = findOnGroundYPosition(var1, var9, var11, var10, var7);
            if (var12 != 2147483647) {
               var5.add(new BlockPos(var9, var12, var10));
               var6 = true;
            }
         }

         ++var7;
      } while(var6);

      return var5.stream();
   }

   private static int findOnGroundYPosition(DecorationContext var0, int var1, int var2, int var3, int var4) {
      BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos(var1, var2, var3);
      int var6 = 0;
      BlockState var7 = var0.getBlockState(var5);

      for(int var8 = var2; var8 >= var0.getMaxBuildHeight() + 1; --var8) {
         var5.setY(var8 - 1);
         BlockState var9 = var0.getBlockState(var5);
         if (!isEmpty(var9) && isEmpty(var7) && !var9.is(Blocks.BEDROCK)) {
            if (var6 == var4) {
               return var5.getY() + 1;
            }

            ++var6;
         }

         var7 = var9;
      }

      return 2147483647;
   }

   private static boolean isEmpty(BlockState var0) {
      return var0.isAir() || var0.is(Blocks.WATER) || var0.is(Blocks.LAVA);
   }
}
