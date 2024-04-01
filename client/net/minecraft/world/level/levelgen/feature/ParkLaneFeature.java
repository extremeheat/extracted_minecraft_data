package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class ParkLaneFeature extends Feature<NoneFeatureConfiguration> {
   public ParkLaneFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   @Override
   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> var1) {
      BlockPos var2 = var1.origin();
      WorldGenLevel var3 = var1.level();
      RandomSource var4 = var1.random();
      if (var3.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, var2).getY() <= var2.getY() + 2) {
         return false;
      } else if (!this.isValidPlacementLocation(var3, var2)) {
         return false;
      } else {
         Direction var5 = Direction.from2DDataValue(var4.nextInt(4));
         BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();
         var3.setBlock(var2.below(), Blocks.POISON_PATH.defaultBlockState(), 2);
         int var7 = var4.nextInt(6, 12);
         ArrayList var8 = new ArrayList();

         for(int var9 = 0; var9 < 2; ++var9) {
            var6.set(var2);
            float var10 = 1.0F;
            int var11 = 0;
            var5 = var5.getOpposite();

            while(var4.nextFloat() < var10) {
               if (++var11 > 2) {
                  break;
               }

               var6.move(var5);
               if (!this.isValidPlacementLocation(var3, var6)) {
                  break;
               }

               var10 *= 0.8F;
               Function var12 = var1x -> {
                  var8.add(var1x.immutable());
                  return Blocks.AIR.defaultBlockState();
               };
               Function var13 = var0 -> Blocks.POISON_PATH.defaultBlockState();
               this.runRowOfPotatoes(var3, var4, var6, var5.getClockWise(), var13, var12, var7);
               Direction var14 = var5.getCounterClockWise();
               this.runRowOfPotatoes(var3, var4, var6.relative(var14), var14, var13, var12, var7);
            }

            if (var4.nextInt(2) == 0) {
               var6.move(var5);
               if (this.isValidPlacementLocation(var3, var6)) {
                  Function var19 = var3x -> {
                     var8.add(var3x.immutable());
                     if (var4.nextInt(10) == 0) {
                        var3.setBlock(var3x.above(), Blocks.LANTERN.defaultBlockState(), 3);
                     }

                     return Blocks.POTATO_FENCE.defaultBlockState();
                  };
                  this.runRowOfPotatoes(var3, var4, var6, var5.getClockWise(), var0 -> Blocks.PEELGRASS_BLOCK.defaultBlockState(), var19, var7);
                  Direction var20 = var5.getCounterClockWise();
                  this.runRowOfPotatoes(var3, var4, var6.relative(var20), var20, var0 -> Blocks.PEELGRASS_BLOCK.defaultBlockState(), var19, var7);
               }
            }
         }

         for(BlockPos var18 : var8) {
            var3.getChunk(var18).markPosForPostprocessing(var18);
         }

         Direction var16 = var5.getCounterClockWise();
         this.runRowOfPotatoes(
            var3, var4, var2.relative(var16), var16, var0 -> Blocks.POISON_PATH.defaultBlockState(), var0 -> Blocks.AIR.defaultBlockState(), var7
         );
         var16 = var16.getOpposite();
         this.runRowOfPotatoes(
            var3, var4, var2.relative(var16), var16, var0 -> Blocks.POISON_PATH.defaultBlockState(), var0 -> Blocks.AIR.defaultBlockState(), var7
         );
         return false;
      }
   }

   private void runRowOfPotatoes(
      WorldGenLevel var1, RandomSource var2, BlockPos var3, Direction var4, Function<BlockPos, BlockState> var5, Function<BlockPos, BlockState> var6, int var7
   ) {
      BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos(var3);
      int var9 = var7 + var2.nextInt(3);

      for(int var10 = 0; var10 < var9; ++var10) {
         if (!this.isValidPlacementLocation(var1, var8)) {
            var8.move(Direction.UP);
            if (!this.isValidPlacementLocation(var1, var8)) {
               var8.move(Direction.DOWN, 2);
               if (!this.isValidPlacementLocation(var1, var8)) {
                  break;
               }
            }
         }

         var1.setBlock(var8.below(), (BlockState)var5.apply(var8.below()), 3);
         var1.setBlock(var8, (BlockState)var6.apply(var8), 3);
         var8.move(var4);
      }
   }

   private boolean isValidPlacementLocation(WorldGenLevel var1, BlockPos var2) {
      return (var1.isEmptyBlock(var2) || var1.getBlockState(var2).is(Blocks.POTATO_FENCE)) && var1.getBlockState(var2.below()).is(Blocks.PEELGRASS_BLOCK);
   }
}
