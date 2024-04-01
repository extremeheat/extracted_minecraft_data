package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.ColumnFeatureConfiguration;

public class BasaltColumnsFeature extends Feature<ColumnFeatureConfiguration> {
   private static final ImmutableList<Block> CANNOT_PLACE_ON = ImmutableList.of(
      Blocks.LAVA,
      Blocks.WATER,
      Blocks.BEDROCK,
      Blocks.MAGMA_BLOCK,
      Blocks.SLIME_BLOCK,
      Blocks.SOUL_SAND,
      Blocks.NETHER_BRICKS,
      Blocks.NETHER_BRICK_FENCE,
      Blocks.NETHER_BRICK_STAIRS,
      Blocks.NETHER_WART,
      Blocks.CHEST,
      Blocks.SPAWNER,
      new Block[0]
   );
   private static final int CLUSTERED_REACH = 5;
   private static final int CLUSTERED_SIZE = 50;
   private static final int UNCLUSTERED_REACH = 8;
   private static final int UNCLUSTERED_SIZE = 15;

   public BasaltColumnsFeature(Codec<ColumnFeatureConfiguration> var1) {
      super(var1);
   }

   @Override
   public boolean place(FeaturePlaceContext<ColumnFeatureConfiguration> var1) {
      int var2 = var1.chunkGenerator().getSeaLevel();
      BlockPos var3 = var1.origin();
      WorldGenLevel var4 = var1.level();
      RandomSource var5 = var1.random();
      ColumnFeatureConfiguration var6 = (ColumnFeatureConfiguration)var1.config();
      if (!canPlaceAt(var4, var2, var3.mutable())) {
         return false;
      } else {
         int var7 = var6.height().sample(var5);
         boolean var8 = var5.nextFloat() < 0.9F;
         int var9 = Math.min(var7, var8 ? 5 : 8);
         int var10 = var8 ? 50 : 15;
         boolean var11 = false;

         for(BlockPos var13 : BlockPos.randomBetweenClosed(
            var5, var10, var3.getX() - var9, var3.getY(), var3.getZ() - var9, var3.getX() + var9, var3.getY(), var3.getZ() + var9
         )) {
            int var14 = var7 - var13.distManhattan(var3);
            if (var14 >= 0) {
               var11 |= this.placeColumn(var4, var2, var13, var14, var6.reach().sample(var5), var6.state());
            }
         }

         return var11;
      }
   }

   private boolean placeColumn(LevelAccessor var1, int var2, BlockPos var3, int var4, int var5, BlockState var6) {
      boolean var7 = false;

      for(BlockPos var9 : BlockPos.betweenClosed(var3.getX() - var5, var3.getY(), var3.getZ() - var5, var3.getX() + var5, var3.getY(), var3.getZ() + var5)) {
         int var10 = var9.distManhattan(var3);
         BlockPos var11 = isAirOrLavaOcean(var1, var2, var9) ? findSurface(var1, var2, var9.mutable(), var10) : findAir(var1, var9.mutable(), var10);
         if (var11 != null) {
            int var12 = var4 - var10 / 2;

            for(BlockPos.MutableBlockPos var13 = var11.mutable(); var12 >= 0; --var12) {
               if (isAirOrLavaOcean(var1, var2, var13)) {
                  this.setBlock(var1, var13, var6);
                  var13.move(Direction.UP);
                  var7 = true;
               } else {
                  if (!var1.getBlockState(var13).is(Blocks.BASALT) && !var1.getBlockState(var13).is(Blocks.ANCIENT_DEBRIS)) {
                     break;
                  }

                  var13.move(Direction.UP);
               }
            }
         }
      }

      return var7;
   }

   @Nullable
   private static BlockPos findSurface(LevelAccessor var0, int var1, BlockPos.MutableBlockPos var2, int var3) {
      while(var2.getY() > var0.getMinBuildHeight() + 1 && var3 > 0) {
         --var3;
         if (canPlaceAt(var0, var1, var2)) {
            return var2;
         }

         var2.move(Direction.DOWN);
      }

      return null;
   }

   private static boolean canPlaceAt(LevelAccessor var0, int var1, BlockPos.MutableBlockPos var2) {
      if (!isAirOrLavaOcean(var0, var1, var2)) {
         return false;
      } else {
         BlockState var3 = var0.getBlockState(var2.move(Direction.DOWN));
         var2.move(Direction.UP);
         return !var3.isAir() && !CANNOT_PLACE_ON.contains(var3.getBlock());
      }
   }

   @Nullable
   private static BlockPos findAir(LevelAccessor var0, BlockPos.MutableBlockPos var1, int var2) {
      while(var1.getY() < var0.getMaxBuildHeight() && var2 > 0) {
         --var2;
         BlockState var3 = var0.getBlockState(var1);
         if (CANNOT_PLACE_ON.contains(var3.getBlock())) {
            return null;
         }

         if (var3.isAir()) {
            return var1;
         }

         var1.move(Direction.UP);
      }

      return null;
   }

   private static boolean isAirOrLavaOcean(LevelAccessor var0, int var1, BlockPos var2) {
      BlockState var3 = var0.getBlockState(var2);
      return var3.isAir() || var3.is(Blocks.LAVA) && var2.getY() <= var1;
   }
}
