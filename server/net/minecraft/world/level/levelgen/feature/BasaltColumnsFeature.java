package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.ColumnFeatureConfiguration;

public class BasaltColumnsFeature extends Feature<ColumnFeatureConfiguration> {
   private static final ImmutableList<Block> CANNOT_PLACE_ON;

   public BasaltColumnsFeature(Codec<ColumnFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, ColumnFeatureConfiguration var5) {
      int var6 = var2.getSeaLevel();
      if (!canPlaceAt(var1, var6, var4.mutable())) {
         return false;
      } else {
         int var7 = var5.height().sample(var3);
         boolean var8 = var3.nextFloat() < 0.9F;
         int var9 = Math.min(var7, var8 ? 5 : 8);
         int var10 = var8 ? 50 : 15;
         boolean var11 = false;
         Iterator var12 = BlockPos.randomBetweenClosed(var3, var10, var4.getX() - var9, var4.getY(), var4.getZ() - var9, var4.getX() + var9, var4.getY(), var4.getZ() + var9).iterator();

         while(var12.hasNext()) {
            BlockPos var13 = (BlockPos)var12.next();
            int var14 = var7 - var13.distManhattan(var4);
            if (var14 >= 0) {
               var11 |= this.placeColumn(var1, var6, var13, var14, var5.reach().sample(var3));
            }
         }

         return var11;
      }
   }

   private boolean placeColumn(LevelAccessor var1, int var2, BlockPos var3, int var4, int var5) {
      boolean var6 = false;
      Iterator var7 = BlockPos.betweenClosed(var3.getX() - var5, var3.getY(), var3.getZ() - var5, var3.getX() + var5, var3.getY(), var3.getZ() + var5).iterator();

      while(true) {
         int var9;
         BlockPos var10;
         do {
            if (!var7.hasNext()) {
               return var6;
            }

            BlockPos var8 = (BlockPos)var7.next();
            var9 = var8.distManhattan(var3);
            var10 = isAirOrLavaOcean(var1, var2, var8) ? findSurface(var1, var2, var8.mutable(), var9) : findAir(var1, var8.mutable(), var9);
         } while(var10 == null);

         int var11 = var4 - var9 / 2;

         for(BlockPos.MutableBlockPos var12 = var10.mutable(); var11 >= 0; --var11) {
            if (isAirOrLavaOcean(var1, var2, var12)) {
               this.setBlock(var1, var12, Blocks.BASALT.defaultBlockState());
               var12.move(Direction.UP);
               var6 = true;
            } else {
               if (!var1.getBlockState(var12).is(Blocks.BASALT)) {
                  break;
               }

               var12.move(Direction.UP);
            }
         }
      }
   }

   @Nullable
   private static BlockPos findSurface(LevelAccessor var0, int var1, BlockPos.MutableBlockPos var2, int var3) {
      while(var2.getY() > 1 && var3 > 0) {
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

   static {
      CANNOT_PLACE_ON = ImmutableList.of(Blocks.LAVA, Blocks.BEDROCK, Blocks.MAGMA_BLOCK, Blocks.SOUL_SAND, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICK_FENCE, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_WART, Blocks.CHEST, Blocks.SPAWNER);
   }
}
