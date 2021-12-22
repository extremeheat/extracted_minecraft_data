package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.material.Material;

public class HugeFungusFeature extends Feature<HugeFungusConfiguration> {
   private static final float HUGE_PROBABILITY = 0.06F;

   public HugeFungusFeature(Codec<HugeFungusConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<HugeFungusConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      BlockPos var3 = var1.origin();
      Random var4 = var1.random();
      ChunkGenerator var5 = var1.chunkGenerator();
      HugeFungusConfiguration var6 = (HugeFungusConfiguration)var1.config();
      Block var7 = var6.validBaseState.getBlock();
      BlockPos var8 = null;
      BlockState var9 = var2.getBlockState(var3.below());
      if (var9.is(var7)) {
         var8 = var3;
      }

      if (var8 == null) {
         return false;
      } else {
         int var10 = Mth.nextInt(var4, 4, 13);
         if (var4.nextInt(12) == 0) {
            var10 *= 2;
         }

         if (!var6.planted) {
            int var11 = var5.getGenDepth();
            if (var8.getY() + var10 + 1 >= var11) {
               return false;
            }
         }

         boolean var12 = !var6.planted && var4.nextFloat() < 0.06F;
         var2.setBlock(var3, Blocks.AIR.defaultBlockState(), 4);
         this.placeStem(var2, var4, var6, var8, var10, var12);
         this.placeHat(var2, var4, var6, var8, var10, var12);
         return true;
      }
   }

   private static boolean isReplaceable(LevelAccessor var0, BlockPos var1, boolean var2) {
      return var0.isStateAtPosition(var1, (var1x) -> {
         Material var2x = var1x.getMaterial();
         return var1x.getMaterial().isReplaceable() || var2 && var2x == Material.PLANT;
      });
   }

   private void placeStem(LevelAccessor var1, Random var2, HugeFungusConfiguration var3, BlockPos var4, int var5, boolean var6) {
      BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos();
      BlockState var8 = var3.stemState;
      int var9 = var6 ? 1 : 0;

      for(int var10 = -var9; var10 <= var9; ++var10) {
         for(int var11 = -var9; var11 <= var9; ++var11) {
            boolean var12 = var6 && Mth.abs(var10) == var9 && Mth.abs(var11) == var9;

            for(int var13 = 0; var13 < var5; ++var13) {
               var7.setWithOffset(var4, var10, var13, var11);
               if (isReplaceable(var1, var7, true)) {
                  if (var3.planted) {
                     if (!var1.getBlockState(var7.below()).isAir()) {
                        var1.destroyBlock(var7, true);
                     }

                     var1.setBlock(var7, var8, 3);
                  } else if (var12) {
                     if (var2.nextFloat() < 0.1F) {
                        this.setBlock(var1, var7, var8);
                     }
                  } else {
                     this.setBlock(var1, var7, var8);
                  }
               }
            }
         }
      }

   }

   private void placeHat(LevelAccessor var1, Random var2, HugeFungusConfiguration var3, BlockPos var4, int var5, boolean var6) {
      BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos();
      boolean var8 = var3.hatState.is(Blocks.NETHER_WART_BLOCK);
      int var9 = Math.min(var2.nextInt(1 + var5 / 3) + 5, var5);
      int var10 = var5 - var9;

      for(int var11 = var10; var11 <= var5; ++var11) {
         int var12 = var11 < var5 - var2.nextInt(3) ? 2 : 1;
         if (var9 > 8 && var11 < var10 + 4) {
            var12 = 3;
         }

         if (var6) {
            ++var12;
         }

         for(int var13 = -var12; var13 <= var12; ++var13) {
            for(int var14 = -var12; var14 <= var12; ++var14) {
               boolean var15 = var13 == -var12 || var13 == var12;
               boolean var16 = var14 == -var12 || var14 == var12;
               boolean var17 = !var15 && !var16 && var11 != var5;
               boolean var18 = var15 && var16;
               boolean var19 = var11 < var10 + 3;
               var7.setWithOffset(var4, var13, var11, var14);
               if (isReplaceable(var1, var7, false)) {
                  if (var3.planted && !var1.getBlockState(var7.below()).isAir()) {
                     var1.destroyBlock(var7, true);
                  }

                  if (var19) {
                     if (!var17) {
                        this.placeHatDropBlock(var1, var2, var7, var3.hatState, var8);
                     }
                  } else if (var17) {
                     this.placeHatBlock(var1, var2, var3, var7, 0.1F, 0.2F, var8 ? 0.1F : 0.0F);
                  } else if (var18) {
                     this.placeHatBlock(var1, var2, var3, var7, 0.01F, 0.7F, var8 ? 0.083F : 0.0F);
                  } else {
                     this.placeHatBlock(var1, var2, var3, var7, 5.0E-4F, 0.98F, var8 ? 0.07F : 0.0F);
                  }
               }
            }
         }
      }

   }

   private void placeHatBlock(LevelAccessor var1, Random var2, HugeFungusConfiguration var3, BlockPos.MutableBlockPos var4, float var5, float var6, float var7) {
      if (var2.nextFloat() < var5) {
         this.setBlock(var1, var4, var3.decorState);
      } else if (var2.nextFloat() < var6) {
         this.setBlock(var1, var4, var3.hatState);
         if (var2.nextFloat() < var7) {
            tryPlaceWeepingVines(var4, var1, var2);
         }
      }

   }

   private void placeHatDropBlock(LevelAccessor var1, Random var2, BlockPos var3, BlockState var4, boolean var5) {
      if (var1.getBlockState(var3.below()).is(var4.getBlock())) {
         this.setBlock(var1, var3, var4);
      } else if ((double)var2.nextFloat() < 0.15D) {
         this.setBlock(var1, var3, var4);
         if (var5 && var2.nextInt(11) == 0) {
            tryPlaceWeepingVines(var3, var1, var2);
         }
      }

   }

   private static void tryPlaceWeepingVines(BlockPos var0, LevelAccessor var1, Random var2) {
      BlockPos.MutableBlockPos var3 = var0.mutable().move(Direction.DOWN);
      if (var1.isEmptyBlock(var3)) {
         int var4 = Mth.nextInt(var2, 1, 5);
         if (var2.nextInt(7) == 0) {
            var4 *= 2;
         }

         boolean var5 = true;
         boolean var6 = true;
         WeepingVinesFeature.placeWeepingVinesColumn(var1, var2, var3, var4, 23, 25);
      }
   }
}
