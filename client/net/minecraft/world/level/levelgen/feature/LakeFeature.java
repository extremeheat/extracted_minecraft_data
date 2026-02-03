package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

/** @deprecated */
@Deprecated
public class LakeFeature extends Feature<Configuration> {
   private static final BlockState AIR;

   public LakeFeature(final Codec<Configuration> codec) {
      super(codec);
   }

   public boolean place(final FeaturePlaceContext<Configuration> context) {
      BlockPos origin = context.origin();
      WorldGenLevel level = context.level();
      RandomSource random = context.random();
      Configuration config = context.config();
      if (origin.getY() <= level.getMinY() + 4) {
         return false;
      } else {
         origin = origin.below(4);
         boolean[] grid = new boolean[2048];
         int spots = random.nextInt(4) + 4;

         for(int i = 0; i < spots; ++i) {
            double xr = random.nextDouble() * 6.0 + 3.0;
            double yr = random.nextDouble() * 4.0 + 2.0;
            double zr = random.nextDouble() * 6.0 + 3.0;
            double xp = random.nextDouble() * (16.0 - xr - 2.0) + 1.0 + xr / 2.0;
            double yp = random.nextDouble() * (8.0 - yr - 4.0) + 2.0 + yr / 2.0;
            double zp = random.nextDouble() * (16.0 - zr - 2.0) + 1.0 + zr / 2.0;

            for(int xx = 1; xx < 15; ++xx) {
               for(int zz = 1; zz < 15; ++zz) {
                  for(int yy = 1; yy < 7; ++yy) {
                     double xd = ((double)xx - xp) / (xr / 2.0);
                     double yd = ((double)yy - yp) / (yr / 2.0);
                     double zd = ((double)zz - zp) / (zr / 2.0);
                     double d = xd * xd + yd * yd + zd * zd;
                     if (d < 1.0) {
                        grid[(xx * 16 + zz) * 8 + yy] = true;
                     }
                  }
               }
            }
         }

         BlockState fluid = config.fluid().getState(random, origin);

         for(int xx = 0; xx < 16; ++xx) {
            for(int zz = 0; zz < 16; ++zz) {
               for(int yy = 0; yy < 8; ++yy) {
                  boolean check = !grid[(xx * 16 + zz) * 8 + yy] && (xx < 15 && grid[((xx + 1) * 16 + zz) * 8 + yy] || xx > 0 && grid[((xx - 1) * 16 + zz) * 8 + yy] || zz < 15 && grid[(xx * 16 + zz + 1) * 8 + yy] || zz > 0 && grid[(xx * 16 + (zz - 1)) * 8 + yy] || yy < 7 && grid[(xx * 16 + zz) * 8 + yy + 1] || yy > 0 && grid[(xx * 16 + zz) * 8 + (yy - 1)]);
                  if (check) {
                     BlockState blockState = level.getBlockState(origin.offset(xx, yy, zz));
                     if (yy >= 4 && blockState.liquid()) {
                        return false;
                     }

                     if (yy < 4 && !blockState.isSolid() && level.getBlockState(origin.offset(xx, yy, zz)) != fluid) {
                        return false;
                     }
                  }
               }
            }
         }

         for(int xx = 0; xx < 16; ++xx) {
            for(int zz = 0; zz < 16; ++zz) {
               for(int yy = 0; yy < 8; ++yy) {
                  if (grid[(xx * 16 + zz) * 8 + yy]) {
                     BlockPos placePos = origin.offset(xx, yy, zz);
                     if (this.canReplaceBlock(level.getBlockState(placePos))) {
                        boolean placeAir = yy >= 4;
                        level.setBlock(placePos, placeAir ? AIR : fluid, 2);
                        if (placeAir) {
                           level.scheduleTick(placePos, AIR.getBlock(), 0);
                           this.markAboveForPostProcessing(level, placePos);
                        }
                     }
                  }
               }
            }
         }

         BlockState barrier = config.barrier().getState(random, origin);
         if (!barrier.isAir()) {
            for(int xx = 0; xx < 16; ++xx) {
               for(int zz = 0; zz < 16; ++zz) {
                  for(int yy = 0; yy < 8; ++yy) {
                     boolean check = !grid[(xx * 16 + zz) * 8 + yy] && (xx < 15 && grid[((xx + 1) * 16 + zz) * 8 + yy] || xx > 0 && grid[((xx - 1) * 16 + zz) * 8 + yy] || zz < 15 && grid[(xx * 16 + zz + 1) * 8 + yy] || zz > 0 && grid[(xx * 16 + (zz - 1)) * 8 + yy] || yy < 7 && grid[(xx * 16 + zz) * 8 + yy + 1] || yy > 0 && grid[(xx * 16 + zz) * 8 + (yy - 1)]);
                     if (check && (yy < 4 || random.nextInt(2) != 0)) {
                        BlockState blockState = level.getBlockState(origin.offset(xx, yy, zz));
                        if (blockState.isSolid() && !blockState.is(BlockTags.LAVA_POOL_STONE_CANNOT_REPLACE)) {
                           BlockPos barrierPos = origin.offset(xx, yy, zz);
                           level.setBlock(barrierPos, barrier, 2);
                           this.markAboveForPostProcessing(level, barrierPos);
                        }
                     }
                  }
               }
            }
         }

         if (fluid.getFluidState().is(FluidTags.WATER)) {
            for(int xx = 0; xx < 16; ++xx) {
               for(int zz = 0; zz < 16; ++zz) {
                  int yy = 4;
                  BlockPos offset = origin.offset(xx, 4, zz);
                  if (((Biome)level.getBiome(offset).value()).shouldFreeze(level, offset, false) && this.canReplaceBlock(level.getBlockState(offset))) {
                     level.setBlock(offset, Blocks.ICE.defaultBlockState(), 2);
                  }
               }
            }
         }

         return true;
      }
   }

   private boolean canReplaceBlock(final BlockState state) {
      return !state.is(BlockTags.FEATURES_CANNOT_REPLACE);
   }

   static {
      AIR = Blocks.CAVE_AIR.defaultBlockState();
   }

   public static record Configuration(BlockStateProvider fluid, BlockStateProvider barrier) implements FeatureConfiguration {
      public static final Codec<Configuration> CODEC = RecordCodecBuilder.create((i) -> i.group(BlockStateProvider.CODEC.fieldOf("fluid").forGetter(Configuration::fluid), BlockStateProvider.CODEC.fieldOf("barrier").forGetter(Configuration::barrier)).apply(i, Configuration::new));

      public Configuration {
         super();
      }
   }
}
