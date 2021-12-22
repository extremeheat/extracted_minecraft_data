package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.material.Material;

/** @deprecated */
@Deprecated
public class LakeFeature extends Feature<LakeFeature.Configuration> {
   private static final BlockState AIR;

   public LakeFeature(Codec<LakeFeature.Configuration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<LakeFeature.Configuration> var1) {
      BlockPos var2 = var1.origin();
      WorldGenLevel var3 = var1.level();
      Random var4 = var1.random();
      LakeFeature.Configuration var5 = (LakeFeature.Configuration)var1.config();
      if (var2.getY() <= var3.getMinBuildHeight() + 4) {
         return false;
      } else {
         var2 = var2.below(4);
         if (!var3.startsForFeature(SectionPos.method_71(var2), StructureFeature.VILLAGE).isEmpty()) {
            return false;
         } else {
            boolean[] var6 = new boolean[2048];
            int var7 = var4.nextInt(4) + 4;

            for(int var8 = 0; var8 < var7; ++var8) {
               double var9 = var4.nextDouble() * 6.0D + 3.0D;
               double var11 = var4.nextDouble() * 4.0D + 2.0D;
               double var13 = var4.nextDouble() * 6.0D + 3.0D;
               double var15 = var4.nextDouble() * (16.0D - var9 - 2.0D) + 1.0D + var9 / 2.0D;
               double var17 = var4.nextDouble() * (8.0D - var11 - 4.0D) + 2.0D + var11 / 2.0D;
               double var19 = var4.nextDouble() * (16.0D - var13 - 2.0D) + 1.0D + var13 / 2.0D;

               for(int var21 = 1; var21 < 15; ++var21) {
                  for(int var22 = 1; var22 < 15; ++var22) {
                     for(int var23 = 1; var23 < 7; ++var23) {
                        double var24 = ((double)var21 - var15) / (var9 / 2.0D);
                        double var26 = ((double)var23 - var17) / (var11 / 2.0D);
                        double var28 = ((double)var22 - var19) / (var13 / 2.0D);
                        double var30 = var24 * var24 + var26 * var26 + var28 * var28;
                        if (var30 < 1.0D) {
                           var6[(var21 * 16 + var22) * 8 + var23] = true;
                        }
                     }
                  }
               }
            }

            BlockState var32 = var5.fluid().getState(var4, var2);

            int var10;
            boolean var12;
            int var33;
            int var35;
            for(var33 = 0; var33 < 16; ++var33) {
               for(var10 = 0; var10 < 16; ++var10) {
                  for(var35 = 0; var35 < 8; ++var35) {
                     var12 = !var6[(var33 * 16 + var10) * 8 + var35] && (var33 < 15 && var6[((var33 + 1) * 16 + var10) * 8 + var35] || var33 > 0 && var6[((var33 - 1) * 16 + var10) * 8 + var35] || var10 < 15 && var6[(var33 * 16 + var10 + 1) * 8 + var35] || var10 > 0 && var6[(var33 * 16 + (var10 - 1)) * 8 + var35] || var35 < 7 && var6[(var33 * 16 + var10) * 8 + var35 + 1] || var35 > 0 && var6[(var33 * 16 + var10) * 8 + (var35 - 1)]);
                     if (var12) {
                        Material var38 = var3.getBlockState(var2.offset(var33, var35, var10)).getMaterial();
                        if (var35 >= 4 && var38.isLiquid()) {
                           return false;
                        }

                        if (var35 < 4 && !var38.isSolid() && var3.getBlockState(var2.offset(var33, var35, var10)) != var32) {
                           return false;
                        }
                     }
                  }
               }
            }

            boolean var39;
            for(var33 = 0; var33 < 16; ++var33) {
               for(var10 = 0; var10 < 16; ++var10) {
                  for(var35 = 0; var35 < 8; ++var35) {
                     if (var6[(var33 * 16 + var10) * 8 + var35]) {
                        BlockPos var36 = var2.offset(var33, var35, var10);
                        if (this.canReplaceBlock(var3.getBlockState(var36))) {
                           var39 = var35 >= 4;
                           var3.setBlock(var36, var39 ? AIR : var32, 2);
                           if (var39) {
                              var3.scheduleTick(var36, AIR.getBlock(), 0);
                              this.markAboveForPostProcessing(var3, var36);
                           }
                        }
                     }
                  }
               }
            }

            BlockState var34 = var5.barrier().getState(var4, var2);
            if (!var34.isAir()) {
               for(var10 = 0; var10 < 16; ++var10) {
                  for(var35 = 0; var35 < 16; ++var35) {
                     for(int var37 = 0; var37 < 8; ++var37) {
                        var39 = !var6[(var10 * 16 + var35) * 8 + var37] && (var10 < 15 && var6[((var10 + 1) * 16 + var35) * 8 + var37] || var10 > 0 && var6[((var10 - 1) * 16 + var35) * 8 + var37] || var35 < 15 && var6[(var10 * 16 + var35 + 1) * 8 + var37] || var35 > 0 && var6[(var10 * 16 + (var35 - 1)) * 8 + var37] || var37 < 7 && var6[(var10 * 16 + var35) * 8 + var37 + 1] || var37 > 0 && var6[(var10 * 16 + var35) * 8 + (var37 - 1)]);
                        if (var39 && (var37 < 4 || var4.nextInt(2) != 0)) {
                           BlockState var14 = var3.getBlockState(var2.offset(var10, var37, var35));
                           if (var14.getMaterial().isSolid() && !var14.is(BlockTags.LAVA_POOL_STONE_CANNOT_REPLACE)) {
                              BlockPos var41 = var2.offset(var10, var37, var35);
                              var3.setBlock(var41, var34, 2);
                              this.markAboveForPostProcessing(var3, var41);
                           }
                        }
                     }
                  }
               }
            }

            if (var32.getFluidState().method_56(FluidTags.WATER)) {
               for(var10 = 0; var10 < 16; ++var10) {
                  for(var35 = 0; var35 < 16; ++var35) {
                     var12 = true;
                     BlockPos var40 = var2.offset(var10, 4, var35);
                     if (var3.getBiome(var40).shouldFreeze(var3, var40, false) && this.canReplaceBlock(var3.getBlockState(var40))) {
                        var3.setBlock(var40, Blocks.ICE.defaultBlockState(), 2);
                     }
                  }
               }
            }

            return true;
         }
      }
   }

   private boolean canReplaceBlock(BlockState var1) {
      return !var1.is(BlockTags.FEATURES_CANNOT_REPLACE);
   }

   static {
      AIR = Blocks.CAVE_AIR.defaultBlockState();
   }

   public static record Configuration(BlockStateProvider b, BlockStateProvider c) implements FeatureConfiguration {
      private final BlockStateProvider fluid;
      private final BlockStateProvider barrier;
      public static final Codec<LakeFeature.Configuration> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(BlockStateProvider.CODEC.fieldOf("fluid").forGetter(LakeFeature.Configuration::fluid), BlockStateProvider.CODEC.fieldOf("barrier").forGetter(LakeFeature.Configuration::barrier)).apply(var0, LakeFeature.Configuration::new);
      });

      public Configuration(BlockStateProvider var1, BlockStateProvider var2) {
         super();
         this.fluid = var1;
         this.barrier = var2;
      }

      public BlockStateProvider fluid() {
         return this.fluid;
      }

      public BlockStateProvider barrier() {
         return this.barrier;
      }
   }
}
