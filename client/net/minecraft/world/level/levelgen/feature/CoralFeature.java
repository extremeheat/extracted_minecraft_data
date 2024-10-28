package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
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
      RandomSource var2 = var1.random();
      WorldGenLevel var3 = var1.level();
      BlockPos var4 = var1.origin();
      Optional var5 = BuiltInRegistries.BLOCK.getRandomElementOf(BlockTags.CORAL_BLOCKS, var2).map(Holder::value);
      return var5.isEmpty() ? false : this.placeFeature(var3, var2, var4, ((Block)var5.get()).defaultBlockState());
   }

   protected abstract boolean placeFeature(LevelAccessor var1, RandomSource var2, BlockPos var3, BlockState var4);

   protected boolean placeCoralBlock(LevelAccessor var1, RandomSource var2, BlockPos var3, BlockState var4) {
      BlockPos var5 = var3.above();
      BlockState var6 = var1.getBlockState(var3);
      if ((var6.is(Blocks.WATER) || var6.is(BlockTags.CORALS)) && var1.getBlockState(var5).is(Blocks.WATER)) {
         var1.setBlock(var3, var4, 3);
         if (var2.nextFloat() < 0.25F) {
            BuiltInRegistries.BLOCK.getRandomElementOf(BlockTags.CORALS, var2).map(Holder::value).ifPresent((var2x) -> {
               var1.setBlock(var5, var2x.defaultBlockState(), 2);
            });
         } else if (var2.nextFloat() < 0.05F) {
            var1.setBlock(var5, (BlockState)Blocks.SEA_PICKLE.defaultBlockState().setValue(SeaPickleBlock.PICKLES, var2.nextInt(4) + 1), 2);
         }

         Iterator var7 = Direction.Plane.HORIZONTAL.iterator();

         while(var7.hasNext()) {
            Direction var8 = (Direction)var7.next();
            if (var2.nextFloat() < 0.2F) {
               BlockPos var9 = var3.relative(var8);
               if (var1.getBlockState(var9).is(Blocks.WATER)) {
                  BuiltInRegistries.BLOCK.getRandomElementOf(BlockTags.WALL_CORALS, var2).map(Holder::value).ifPresent((var3x) -> {
                     BlockState var4 = var3x.defaultBlockState();
                     if (var4.hasProperty(BaseCoralWallFanBlock.FACING)) {
                        var4 = (BlockState)var4.setValue(BaseCoralWallFanBlock.FACING, var8);
                     }

                     var1.setBlock(var9, var4, 2);
                  });
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
