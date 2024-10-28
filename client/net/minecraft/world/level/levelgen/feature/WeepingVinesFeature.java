package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class WeepingVinesFeature extends Feature<NoneFeatureConfiguration> {
   private static final Direction[] DIRECTIONS = Direction.values();

   public WeepingVinesFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      BlockPos var3 = var1.origin();
      RandomSource var4 = var1.random();
      if (!var2.isEmptyBlock(var3)) {
         return false;
      } else {
         BlockState var5 = var2.getBlockState(var3.above());
         if (!var5.is(Blocks.NETHERRACK) && !var5.is(Blocks.NETHER_WART_BLOCK)) {
            return false;
         } else {
            this.placeRoofNetherWart(var2, var4, var3);
            this.placeRoofWeepingVines(var2, var4, var3);
            return true;
         }
      }
   }

   private void placeRoofNetherWart(LevelAccessor var1, RandomSource var2, BlockPos var3) {
      var1.setBlock(var3, Blocks.NETHER_WART_BLOCK.defaultBlockState(), 2);
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos();
      BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();

      for(int var6 = 0; var6 < 200; ++var6) {
         var4.setWithOffset(var3, var2.nextInt(6) - var2.nextInt(6), var2.nextInt(2) - var2.nextInt(5), var2.nextInt(6) - var2.nextInt(6));
         if (var1.isEmptyBlock(var4)) {
            int var7 = 0;
            Direction[] var8 = DIRECTIONS;
            int var9 = var8.length;

            for(int var10 = 0; var10 < var9; ++var10) {
               Direction var11 = var8[var10];
               BlockState var12 = var1.getBlockState(var5.setWithOffset(var4, (Direction)var11));
               if (var12.is(Blocks.NETHERRACK) || var12.is(Blocks.NETHER_WART_BLOCK)) {
                  ++var7;
               }

               if (var7 > 1) {
                  break;
               }
            }

            if (var7 == 1) {
               var1.setBlock(var4, Blocks.NETHER_WART_BLOCK.defaultBlockState(), 2);
            }
         }
      }

   }

   private void placeRoofWeepingVines(LevelAccessor var1, RandomSource var2, BlockPos var3) {
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos();

      for(int var5 = 0; var5 < 100; ++var5) {
         var4.setWithOffset(var3, var2.nextInt(8) - var2.nextInt(8), var2.nextInt(2) - var2.nextInt(7), var2.nextInt(8) - var2.nextInt(8));
         if (var1.isEmptyBlock(var4)) {
            BlockState var6 = var1.getBlockState(var4.above());
            if (var6.is(Blocks.NETHERRACK) || var6.is(Blocks.NETHER_WART_BLOCK)) {
               int var7 = Mth.nextInt(var2, 1, 8);
               if (var2.nextInt(6) == 0) {
                  var7 *= 2;
               }

               if (var2.nextInt(5) == 0) {
                  var7 = 1;
               }

               boolean var8 = true;
               boolean var9 = true;
               placeWeepingVinesColumn(var1, var2, var4, var7, 17, 25);
            }
         }
      }

   }

   public static void placeWeepingVinesColumn(LevelAccessor var0, RandomSource var1, BlockPos.MutableBlockPos var2, int var3, int var4, int var5) {
      for(int var6 = 0; var6 <= var3; ++var6) {
         if (var0.isEmptyBlock(var2)) {
            if (var6 == var3 || !var0.isEmptyBlock(var2.below())) {
               var0.setBlock(var2, (BlockState)Blocks.WEEPING_VINES.defaultBlockState().setValue(GrowingPlantHeadBlock.AGE, Mth.nextInt(var1, var4, var5)), 2);
               break;
            }

            var0.setBlock(var2, Blocks.WEEPING_VINES_PLANT.defaultBlockState(), 2);
         }

         var2.move(Direction.DOWN);
      }

   }
}
