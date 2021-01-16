package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.DeltaFeatureConfiguration;

public class DeltaFeature extends Feature<DeltaFeatureConfiguration> {
   private static final ImmutableList<Block> CANNOT_REPLACE;
   private static final Direction[] DIRECTIONS;

   public DeltaFeature(Codec<DeltaFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, DeltaFeatureConfiguration var5) {
      boolean var6 = false;
      boolean var7 = var3.nextDouble() < 0.9D;
      int var8 = var7 ? var5.rimSize().sample(var3) : 0;
      int var9 = var7 ? var5.rimSize().sample(var3) : 0;
      boolean var10 = var7 && var8 != 0 && var9 != 0;
      int var11 = var5.size().sample(var3);
      int var12 = var5.size().sample(var3);
      int var13 = Math.max(var11, var12);
      Iterator var14 = BlockPos.withinManhattan(var4, var11, 0, var12).iterator();

      while(var14.hasNext()) {
         BlockPos var15 = (BlockPos)var14.next();
         if (var15.distManhattan(var4) > var13) {
            break;
         }

         if (isClear(var1, var15, var5)) {
            if (var10) {
               var6 = true;
               this.setBlock(var1, var15, var5.rim());
            }

            BlockPos var16 = var15.offset(var8, 0, var9);
            if (isClear(var1, var16, var5)) {
               var6 = true;
               this.setBlock(var1, var16, var5.contents());
            }
         }
      }

      return var6;
   }

   private static boolean isClear(LevelAccessor var0, BlockPos var1, DeltaFeatureConfiguration var2) {
      BlockState var3 = var0.getBlockState(var1);
      if (var3.is(var2.contents().getBlock())) {
         return false;
      } else if (CANNOT_REPLACE.contains(var3.getBlock())) {
         return false;
      } else {
         Direction[] var4 = DIRECTIONS;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Direction var7 = var4[var6];
            boolean var8 = var0.getBlockState(var1.relative(var7)).isAir();
            if (var8 && var7 != Direction.UP || !var8 && var7 == Direction.UP) {
               return false;
            }
         }

         return true;
      }
   }

   static {
      CANNOT_REPLACE = ImmutableList.of(Blocks.BEDROCK, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICK_FENCE, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_WART, Blocks.CHEST, Blocks.SPAWNER);
      DIRECTIONS = Direction.values();
   }
}
