package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.material.FluidState;

public class GeodeFeature extends Feature<GeodeConfiguration> {
   private static final Direction[] DIRECTIONS = Direction.values();

   public GeodeFeature(Codec<GeodeConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<GeodeConfiguration> var1) {
      GeodeConfiguration var2 = (GeodeConfiguration)var1.config();
      RandomSource var3 = var1.random();
      BlockPos var4 = var1.origin();
      WorldGenLevel var5 = var1.level();
      int var6 = var2.minGenOffset;
      int var7 = var2.maxGenOffset;
      LinkedList var8 = Lists.newLinkedList();
      int var9 = var2.distributionPoints.sample(var3);
      WorldgenRandom var10 = new WorldgenRandom(new LegacyRandomSource(var5.getSeed()));
      NormalNoise var11 = NormalNoise.create(var10, -4, 1.0);
      LinkedList var12 = Lists.newLinkedList();
      double var13 = (double)var9 / (double)var2.outerWallDistance.getMaxValue();
      GeodeLayerSettings var15 = var2.geodeLayerSettings;
      GeodeBlockSettings var16 = var2.geodeBlockSettings;
      GeodeCrackSettings var17 = var2.geodeCrackSettings;
      double var18 = 1.0 / Math.sqrt(var15.filling);
      double var20 = 1.0 / Math.sqrt(var15.innerLayer + var13);
      double var22 = 1.0 / Math.sqrt(var15.middleLayer + var13);
      double var24 = 1.0 / Math.sqrt(var15.outerLayer + var13);
      double var26 = 1.0 / Math.sqrt(var17.baseCrackSize + var3.nextDouble() / 2.0 + (var9 > 3 ? var13 : 0.0));
      boolean var28 = (double)var3.nextFloat() < var17.generateCrackChance;
      int var29 = 0;

      int var30;
      int var31;
      BlockPos var34;
      BlockState var35;
      for(var30 = 0; var30 < var9; ++var30) {
         var31 = var2.outerWallDistance.sample(var3);
         int var32 = var2.outerWallDistance.sample(var3);
         int var33 = var2.outerWallDistance.sample(var3);
         var34 = var4.offset(var31, var32, var33);
         var35 = var5.getBlockState(var34);
         if (var35.isAir() || var35.is(BlockTags.GEODE_INVALID_BLOCKS)) {
            ++var29;
            if (var29 > var2.invalidBlocksThreshold) {
               return false;
            }
         }

         var8.add(Pair.of(var34, var2.pointOffset.sample(var3)));
      }

      if (var28) {
         var30 = var3.nextInt(4);
         var31 = var9 * 2 + 1;
         if (var30 == 0) {
            var12.add(var4.offset(var31, 7, 0));
            var12.add(var4.offset(var31, 5, 0));
            var12.add(var4.offset(var31, 1, 0));
         } else if (var30 == 1) {
            var12.add(var4.offset(0, 7, var31));
            var12.add(var4.offset(0, 5, var31));
            var12.add(var4.offset(0, 1, var31));
         } else if (var30 == 2) {
            var12.add(var4.offset(var31, 7, var31));
            var12.add(var4.offset(var31, 5, var31));
            var12.add(var4.offset(var31, 1, var31));
         } else {
            var12.add(var4.offset(0, 7, 0));
            var12.add(var4.offset(0, 5, 0));
            var12.add(var4.offset(0, 1, 0));
         }
      }

      ArrayList var46 = Lists.newArrayList();
      Predicate var47 = isReplaceable(var2.geodeBlockSettings.cannotReplace);
      Iterator var48 = BlockPos.betweenClosed(var4.offset(var6, var6, var6), var4.offset(var7, var7, var7)).iterator();

      while(true) {
         while(true) {
            double var36;
            double var38;
            BlockPos var50;
            do {
               if (!var48.hasNext()) {
                  List var49 = var16.innerPlacements;
                  Iterator var51 = var46.iterator();

                  while(true) {
                     while(var51.hasNext()) {
                        var34 = (BlockPos)var51.next();
                        var35 = (BlockState)Util.getRandom(var49, var3);
                        Direction[] var53 = DIRECTIONS;
                        int var37 = var53.length;

                        for(int var54 = 0; var54 < var37; ++var54) {
                           Direction var39 = var53[var54];
                           if (var35.hasProperty(BlockStateProperties.FACING)) {
                              var35 = (BlockState)var35.setValue(BlockStateProperties.FACING, var39);
                           }

                           BlockPos var58 = var34.relative(var39);
                           BlockState var60 = var5.getBlockState(var58);
                           if (var35.hasProperty(BlockStateProperties.WATERLOGGED)) {
                              var35 = (BlockState)var35.setValue(BlockStateProperties.WATERLOGGED, var60.getFluidState().isSource());
                           }

                           if (BuddingAmethystBlock.canClusterGrowAtState(var60)) {
                              this.safeSetBlock(var5, var58, var35, var47);
                              break;
                           }
                        }
                     }

                     return true;
                  }
               }

               var50 = (BlockPos)var48.next();
               double var52 = var11.getValue((double)var50.getX(), (double)var50.getY(), (double)var50.getZ()) * var2.noiseMultiplier;
               var36 = 0.0;
               var38 = 0.0;

               Iterator var40;
               Pair var41;
               for(var40 = var8.iterator(); var40.hasNext(); var36 += Mth.invSqrt(var50.distSqr((Vec3i)var41.getFirst()) + (double)(Integer)var41.getSecond()) + var52) {
                  var41 = (Pair)var40.next();
               }

               BlockPos var57;
               for(var40 = var12.iterator(); var40.hasNext(); var38 += Mth.invSqrt(var50.distSqr(var57) + (double)var17.crackPointOffset) + var52) {
                  var57 = (BlockPos)var40.next();
               }
            } while(var36 < var24);

            if (var28 && var38 >= var26 && var36 < var18) {
               this.safeSetBlock(var5, var50, Blocks.AIR.defaultBlockState(), var47);
               Direction[] var56 = DIRECTIONS;
               int var59 = var56.length;

               for(int var42 = 0; var42 < var59; ++var42) {
                  Direction var43 = var56[var42];
                  BlockPos var44 = var50.relative(var43);
                  FluidState var45 = var5.getFluidState(var44);
                  if (!var45.isEmpty()) {
                     var5.scheduleTick(var44, var45.getType(), 0);
                  }
               }
            } else if (var36 >= var18) {
               this.safeSetBlock(var5, var50, var16.fillingProvider.getState(var3, var50), var47);
            } else if (var36 >= var20) {
               boolean var55 = (double)var3.nextFloat() < var2.useAlternateLayer0Chance;
               if (var55) {
                  this.safeSetBlock(var5, var50, var16.alternateInnerLayerProvider.getState(var3, var50), var47);
               } else {
                  this.safeSetBlock(var5, var50, var16.innerLayerProvider.getState(var3, var50), var47);
               }

               if ((!var2.placementsRequireLayer0Alternate || var55) && (double)var3.nextFloat() < var2.usePotentialPlacementsChance) {
                  var46.add(var50.immutable());
               }
            } else if (var36 >= var22) {
               this.safeSetBlock(var5, var50, var16.middleLayerProvider.getState(var3, var50), var47);
            } else if (var36 >= var24) {
               this.safeSetBlock(var5, var50, var16.outerLayerProvider.getState(var3, var50), var47);
            }
         }
      }
   }
}
