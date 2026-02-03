package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class IceSpikeFeature extends Feature<NoneFeatureConfiguration> {
   public IceSpikeFeature(final Codec<NoneFeatureConfiguration> codec) {
      super(codec);
   }

   public boolean place(final FeaturePlaceContext<NoneFeatureConfiguration> context) {
      BlockPos origin = context.origin();
      RandomSource random = context.random();

      WorldGenLevel level;
      for(level = context.level(); level.isEmptyBlock(origin) && origin.getY() > level.getMinY() + 2; origin = origin.below()) {
      }

      if (!level.getBlockState(origin).is(Blocks.SNOW_BLOCK)) {
         return false;
      } else {
         origin = origin.above(random.nextInt(4));
         int height = random.nextInt(4) + 7;
         int width = height / 4 + random.nextInt(2);
         if (width > 1 && random.nextInt(60) == 0) {
            origin = origin.above(10 + random.nextInt(30));
         }

         for(int yOff = 0; yOff < height; ++yOff) {
            float scale = (1.0F - (float)yOff / (float)height) * (float)width;
            int newWidth = Mth.ceil(scale);

            for(int xo = -newWidth; xo <= newWidth; ++xo) {
               float dx = (float)Mth.abs(xo) - 0.25F;

               for(int zo = -newWidth; zo <= newWidth; ++zo) {
                  float dz = (float)Mth.abs(zo) - 0.25F;
                  if ((xo == 0 && zo == 0 || !(dx * dx + dz * dz > scale * scale)) && (xo != -newWidth && xo != newWidth && zo != -newWidth && zo != newWidth || !(random.nextFloat() > 0.75F))) {
                     BlockState state = level.getBlockState(origin.offset(xo, yOff, zo));
                     if (state.isAir() || isDirt(state) || state.is(Blocks.SNOW_BLOCK) || state.is(Blocks.ICE)) {
                        this.setBlock(level, origin.offset(xo, yOff, zo), Blocks.PACKED_ICE.defaultBlockState());
                     }

                     if (yOff != 0 && newWidth > 1) {
                        state = level.getBlockState(origin.offset(xo, -yOff, zo));
                        if (state.isAir() || isDirt(state) || state.is(Blocks.SNOW_BLOCK) || state.is(Blocks.ICE)) {
                           this.setBlock(level, origin.offset(xo, -yOff, zo), Blocks.PACKED_ICE.defaultBlockState());
                        }
                     }
                  }
               }
            }
         }

         int pillarWidth = width - 1;
         if (pillarWidth < 0) {
            pillarWidth = 0;
         } else if (pillarWidth > 1) {
            pillarWidth = 1;
         }

         for(int xo = -pillarWidth; xo <= pillarWidth; ++xo) {
            for(int zo = -pillarWidth; zo <= pillarWidth; ++zo) {
               BlockPos iceBlock = origin.offset(xo, -1, zo);
               int runLength = 50;
               if (Math.abs(xo) == 1 && Math.abs(zo) == 1) {
                  runLength = random.nextInt(5);
               }

               while(iceBlock.getY() > 50) {
                  BlockState state = level.getBlockState(iceBlock);
                  if (!state.isAir() && !isDirt(state) && !state.is(Blocks.SNOW_BLOCK) && !state.is(Blocks.ICE) && !state.is(Blocks.PACKED_ICE)) {
                     break;
                  }

                  this.setBlock(level, iceBlock, Blocks.PACKED_ICE.defaultBlockState());
                  iceBlock = iceBlock.below();
                  --runLength;
                  if (runLength <= 0) {
                     iceBlock = iceBlock.below(random.nextInt(5) + 1);
                     runLength = random.nextInt(5);
                  }
               }
            }
         }

         return true;
      }
   }
}
