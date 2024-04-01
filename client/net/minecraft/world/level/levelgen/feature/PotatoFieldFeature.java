package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class PotatoFieldFeature extends Feature<NoneFeatureConfiguration> {
   public PotatoFieldFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   @Override
   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> var1) {
      BlockPos var2 = var1.origin();
      WorldGenLevel var3 = var1.level();
      RandomSource var4 = var1.random();
      if (!this.isValidPlacementLocation(var3, var2)) {
         return false;
      } else {
         for(Direction var6 : Direction.Plane.HORIZONTAL) {
            if (!this.isValidPlacementLocation(var3, var2.relative(var6))) {
               return false;
            }
         }

         double var18 = var3.getLevel()
            .getChunkSource()
            .randomState()
            .router()
            .continents()
            .compute(new DensityFunction.SinglePointContext(var2.getX(), var2.getY(), var2.getZ()));
         Direction var7 = Direction.from2DDataValue((int)((var18 + 1.0) * 5.0));
         BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos();
         if (var4.nextInt(8) == 0) {
            var3.setBlock(var2.below(), Blocks.POTATO_FENCE.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(true)), 2);
            var3.setBlock(var2, Blocks.POTATO_FENCE.defaultBlockState(), 2);
            var3.setBlock(var2.above(), Blocks.LANTERN.defaultBlockState(), 2);
         } else {
            var3.setBlock(var2.below(), Blocks.WATER.defaultBlockState(), 2);
         }

         var3.setBlock(var2.below(2), Blocks.TERREDEPOMME.defaultBlockState(), 2);
         BoundingBox var9 = new BoundingBox(var2.below()).inflatedBy(4, 1, 4);

         for(int var10 = 0; var10 < 2; ++var10) {
            var8.set(var2);
            float var11 = 1.0F;
            int var12 = 0;
            var7 = var7.getOpposite();

            while(var4.nextFloat() < var11) {
               if (++var12 > 6) {
                  break;
               }

               var8.move(var7);
               if (!this.isValidPlacementLocation(var3, var8)) {
                  break;
               }

               var11 *= 0.8F;
               Function var13 = var1x -> Blocks.POTATOES.defaultBlockState().setValue(CropBlock.AGE, Integer.valueOf(var4.nextInt(7)));
               Function var14 = var1x -> {
                  BlockState var2xx = Blocks.POISON_FARMLAND.defaultBlockState();
                  return var9.isInside(var1x) ? var2xx.setValue(FarmBlock.MOISTURE, Integer.valueOf(7)) : var2xx;
               };
               this.runRowOfPotatoes(var3, var4, var8, var7.getClockWise(), var14, var13);
               Direction var15 = var7.getCounterClockWise();
               this.runRowOfPotatoes(var3, var4, var8.relative(var15), var15, var14, var13);
            }

            if (var4.nextInt(10) == 0) {
               var8.move(var7);
               if (!this.isValidPlacementLocation(var3, var8)) {
                  break;
               }

               ArrayList var21 = new ArrayList();
               Function var22 = var1x -> {
                  var21.add(var1x.immutable());
                  return Blocks.POTATO_FENCE.defaultBlockState();
               };
               this.runRowOfPotatoes(var3, var4, var8, var7.getClockWise(), var0 -> Blocks.PEELGRASS_BLOCK.defaultBlockState(), var22);
               Direction var23 = var7.getCounterClockWise();
               this.runRowOfPotatoes(var3, var4, var8.relative(var23), var23, var0 -> Blocks.PEELGRASS_BLOCK.defaultBlockState(), var22);

               for(BlockPos var17 : var21) {
                  var3.getChunk(var17).markPosForPostprocessing(var17);
               }
            }
         }

         Direction var19 = var7.getCounterClockWise();
         this.runRowOfPotatoes(var3, var4, var2.relative(var19), var19, var0 -> Blocks.POISON_PATH.defaultBlockState(), var0 -> Blocks.AIR.defaultBlockState());
         var19 = var19.getOpposite();
         this.runRowOfPotatoes(var3, var4, var2.relative(var19), var19, var0 -> Blocks.POISON_PATH.defaultBlockState(), var0 -> Blocks.AIR.defaultBlockState());
         return false;
      }
   }

   private void runRowOfPotatoes(
      WorldGenLevel var1, RandomSource var2, BlockPos var3, Direction var4, Function<BlockPos, BlockState> var5, Function<BlockPos, BlockState> var6
   ) {
      BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos(var3);
      int var8 = var2.nextInt(3, 15);

      for(int var9 = 0; var9 < var8; ++var9) {
         if (!this.isValidPlacementLocation(var1, var7)) {
            var7.move(Direction.UP);
            if (!this.isValidPlacementLocation(var1, var7)) {
               var7.move(Direction.DOWN, 2);
               if (!this.isValidPlacementLocation(var1, var7)) {
                  break;
               }
            }
         }

         var1.setBlock(var7.below(), (BlockState)var5.apply(var7.below()), 3);
         var1.setBlock(var7, (BlockState)var6.apply(var7), 3);
         var7.move(var4);
      }
   }

   private boolean isValidPlacementLocation(WorldGenLevel var1, BlockPos var2) {
      return var1.isEmptyBlock(var2) && var1.getBlockState(var2.below()).is(Blocks.PEELGRASS_BLOCK);
   }
}
