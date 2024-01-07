package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

@Deprecated
public class LakeFeature extends Feature<LakeFeature.Configuration> {
   private static final BlockState AIR = Blocks.CAVE_AIR.defaultBlockState();

   public LakeFeature(Codec<LakeFeature.Configuration> var1) {
      super(var1);
   }

   @Override
   public boolean place(FeaturePlaceContext<LakeFeature.Configuration> var1) {
      BlockPos var2 = var1.origin();
      WorldGenLevel var3 = var1.level();
      RandomSource var4 = var1.random();
      LakeFeature.Configuration var5 = (LakeFeature.Configuration)var1.config();
      if (var2.getY() <= var3.getMinBuildHeight() + 4) {
         return false;
      } else {
         var2 = var2.below(4);
         boolean[] var6 = new boolean[2048];
         int var7 = var4.nextInt(4) + 4;

         for(int var8 = 0; var8 < var7; ++var8) {
            double var9 = var4.nextDouble() * 6.0 + 3.0;
            double var11 = var4.nextDouble() * 4.0 + 2.0;
            double var13 = var4.nextDouble() * 6.0 + 3.0;
            double var15 = var4.nextDouble() * (16.0 - var9 - 2.0) + 1.0 + var9 / 2.0;
            double var17 = var4.nextDouble() * (8.0 - var11 - 4.0) + 2.0 + var11 / 2.0;
            double var19 = var4.nextDouble() * (16.0 - var13 - 2.0) + 1.0 + var13 / 2.0;

            for(int var21 = 1; var21 < 15; ++var21) {
               for(int var22 = 1; var22 < 15; ++var22) {
                  for(int var23 = 1; var23 < 7; ++var23) {
                     double var24 = ((double)var21 - var15) / (var9 / 2.0);
                     double var26 = ((double)var23 - var17) / (var11 / 2.0);
                     double var28 = ((double)var22 - var19) / (var13 / 2.0);
                     double var30 = var24 * var24 + var26 * var26 + var28 * var28;
                     if (var30 < 1.0) {
                        var6[(var21 * 16 + var22) * 8 + var23] = true;
                     }
                  }
               }
            }
         }

         BlockState var33 = var5.fluid().getState(var4, var2);

         for(int var34 = 0; var34 < 16; ++var34) {
            for(int var10 = 0; var10 < 16; ++var10) {
               for(int var40 = 0; var40 < 8; ++var40) {
                  boolean var12 = !var6[(var34 * 16 + var10) * 8 + var40]
                     && (
                        var34 < 15 && var6[((var34 + 1) * 16 + var10) * 8 + var40]
                           || var34 > 0 && var6[((var34 - 1) * 16 + var10) * 8 + var40]
                           || var10 < 15 && var6[(var34 * 16 + var10 + 1) * 8 + var40]
                           || var10 > 0 && var6[(var34 * 16 + (var10 - 1)) * 8 + var40]
                           || var40 < 7 && var6[(var34 * 16 + var10) * 8 + var40 + 1]
                           || var40 > 0 && var6[(var34 * 16 + var10) * 8 + (var40 - 1)]
                     );
                  if (var12) {
                     BlockState var47 = var3.getBlockState(var2.offset(var34, var40, var10));
                     if (var40 >= 4 && var47.liquid()) {
                        return false;
                     }

                     if (var40 < 4 && !var47.isSolid() && var3.getBlockState(var2.offset(var34, var40, var10)) != var33) {
                        return false;
                     }
                  }
               }
            }
         }

         for(int var35 = 0; var35 < 16; ++var35) {
            for(int var37 = 0; var37 < 16; ++var37) {
               for(int var41 = 0; var41 < 8; ++var41) {
                  if (var6[(var35 * 16 + var37) * 8 + var41]) {
                     BlockPos var44 = var2.offset(var35, var41, var37);
                     if (this.canReplaceBlock(var3.getBlockState(var44))) {
                        boolean var48 = var41 >= 4;
                        var3.setBlock(var44, var48 ? AIR : var33, 2);
                        if (var48) {
                           var3.scheduleTick(var44, AIR.getBlock(), 0);
                           this.markAboveForPostProcessing(var3, var44);
                        }
                     }
                  }
               }
            }
         }

         BlockState var36 = var5.barrier().getState(var4, var2);
         if (!var36.isAir()) {
            for(int var38 = 0; var38 < 16; ++var38) {
               for(int var42 = 0; var42 < 16; ++var42) {
                  for(int var45 = 0; var45 < 8; ++var45) {
                     boolean var49 = !var6[(var38 * 16 + var42) * 8 + var45]
                        && (
                           var38 < 15 && var6[((var38 + 1) * 16 + var42) * 8 + var45]
                              || var38 > 0 && var6[((var38 - 1) * 16 + var42) * 8 + var45]
                              || var42 < 15 && var6[(var38 * 16 + var42 + 1) * 8 + var45]
                              || var42 > 0 && var6[(var38 * 16 + (var42 - 1)) * 8 + var45]
                              || var45 < 7 && var6[(var38 * 16 + var42) * 8 + var45 + 1]
                              || var45 > 0 && var6[(var38 * 16 + var42) * 8 + (var45 - 1)]
                        );
                     if (var49 && (var45 < 4 || var4.nextInt(2) != 0)) {
                        BlockState var14 = var3.getBlockState(var2.offset(var38, var45, var42));
                        if (var14.isSolid() && !var14.is(BlockTags.LAVA_POOL_STONE_CANNOT_REPLACE)) {
                           BlockPos var51 = var2.offset(var38, var45, var42);
                           var3.setBlock(var51, var36, 2);
                           this.markAboveForPostProcessing(var3, var51);
                        }
                     }
                  }
               }
            }
         }

         if (var33.getFluidState().is(FluidTags.WATER)) {
            for(int var39 = 0; var39 < 16; ++var39) {
               for(int var43 = 0; var43 < 16; ++var43) {
                  boolean var46 = true;
                  BlockPos var50 = var2.offset(var39, 4, var43);
                  if (var3.getBiome(var50).value().shouldFreeze(var3, var50, false) && this.canReplaceBlock(var3.getBlockState(var50))) {
                     var3.setBlock(var50, Blocks.ICE.defaultBlockState(), 2);
                  }
               }
            }
         }

         return true;
      }
   }

   private boolean canReplaceBlock(BlockState var1) {
      return !var1.is(BlockTags.FEATURES_CANNOT_REPLACE);
   }

   public static record Configuration(BlockStateProvider b, BlockStateProvider c) implements FeatureConfiguration {
      private final BlockStateProvider fluid;
      private final BlockStateProvider barrier;
      public static final Codec<LakeFeature.Configuration> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  BlockStateProvider.CODEC.fieldOf("fluid").forGetter(LakeFeature.Configuration::fluid),
                  BlockStateProvider.CODEC.fieldOf("barrier").forGetter(LakeFeature.Configuration::barrier)
               )
               .apply(var0, LakeFeature.Configuration::new)
      );

      public Configuration(BlockStateProvider var1, BlockStateProvider var2) {
         super();
         this.fluid = var1;
         this.barrier = var2;
      }
   }
}
