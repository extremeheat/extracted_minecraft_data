package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public abstract class CoralFeature extends Feature<NoneFeatureConfiguration> {
   public CoralFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> var1) {
      Random var2 = var1.random();
      WorldGenLevel var3 = var1.level();
      BlockPos var4 = var1.origin();
      BlockState var5 = ((Block)BlockTags.CORAL_BLOCKS.getRandomElement(var2)).defaultBlockState();
      return this.placeFeature(var3, var2, var4, var5);
   }

   protected abstract boolean placeFeature(LevelAccessor var1, Random var2, BlockPos var3, BlockState var4);

   protected boolean placeCoralBlock(LevelAccessor var1, Random var2, BlockPos var3, BlockState var4) {
      BlockPos var5 = var3.above();
      BlockState var6 = var1.getBlockState(var3);
      if ((var6.is(Blocks.WATER) || var6.is(BlockTags.CORALS)) && var1.getBlockState(var5).is(Blocks.WATER)) {
         var1.setBlock(var3, var4, 3);
         if (var2.nextFloat() < 0.25F) {
            var1.setBlock(var5, ((Block)BlockTags.CORALS.getRandomElement(var2)).defaultBlockState(), 2);
         } else if (var2.nextFloat() < 0.05F) {
            var1.setBlock(var5, (BlockState)Blocks.SEA_PICKLE.defaultBlockState().setValue(SeaPickleBlock.PICKLES, var2.nextInt(4) + 1), 2);
         }

         Iterator var7 = Direction.Plane.HORIZONTAL.iterator();

         while(var7.hasNext()) {
            Direction var8 = (Direction)var7.next();
            if (var2.nextFloat() < 0.2F) {
               BlockPos var9 = var3.relative(var8);
               if (var1.getBlockState(var9).is(Blocks.WATER)) {
                  BlockState var10 = ((Block)BlockTags.WALL_CORALS.getRandomElement(var2)).defaultBlockState();
                  if (var10.hasProperty(BaseCoralWallFanBlock.FACING)) {
                     var10 = (BlockState)var10.setValue(BaseCoralWallFanBlock.FACING, var8);
                  }

                  var1.setBlock(var9, var10, 2);
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
