package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
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

   @Override
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

      for (int var30 = 0; var30 < var9; var30++) {
         int var31 = var2.outerWallDistance.sample(var3);
         int var32 = var2.outerWallDistance.sample(var3);
         int var33 = var2.outerWallDistance.sample(var3);
         BlockPos var34 = var4.offset(var31, var32, var33);
         BlockState var35 = var5.getBlockState(var34);
         if (var35.isAir() || var35.is(BlockTags.GEODE_INVALID_BLOCKS)) {
            if (++var29 > var2.invalidBlocksThreshold) {
               return false;
            }
         }

         var8.add(Pair.of(var34, var2.pointOffset.sample(var3)));
      }

      if (var28) {
         int var46 = var3.nextInt(4);
         int var48 = var9 * 2 + 1;
         if (var46 == 0) {
            var12.add(var4.offset(var48, 7, 0));
            var12.add(var4.offset(var48, 5, 0));
            var12.add(var4.offset(var48, 1, 0));
         } else if (var46 == 1) {
            var12.add(var4.offset(0, 7, var48));
            var12.add(var4.offset(0, 5, var48));
            var12.add(var4.offset(0, 1, var48));
         } else if (var46 == 2) {
            var12.add(var4.offset(var48, 7, var48));
            var12.add(var4.offset(var48, 5, var48));
            var12.add(var4.offset(var48, 1, var48));
         } else {
            var12.add(var4.offset(0, 7, 0));
            var12.add(var4.offset(0, 5, 0));
            var12.add(var4.offset(0, 1, 0));
         }
      }

      ArrayList var47 = Lists.newArrayList();
      Predicate var49 = isReplaceable(var2.geodeBlockSettings.cannotReplace);

      for (BlockPos var52 : BlockPos.betweenClosed(var4.offset(var6, var6, var6), var4.offset(var7, var7, var7))) {
         double var54 = var11.getValue((double)var52.getX(), (double)var52.getY(), (double)var52.getZ()) * var2.noiseMultiplier;
         double var36 = 0.0;
         double var38 = 0.0;

         for (Pair var41 : var8) {
            var36 += Mth.invSqrt(var52.distSqr((Vec3i)var41.getFirst()) + (double)((Integer)var41.getSecond()).intValue()) + var54;
         }

         for (BlockPos var63 : var12) {
            var38 += Mth.invSqrt(var52.distSqr(var63) + (double)var17.crackPointOffset) + var54;
         }

         if (!(var36 < var24)) {
            if (var28 && var38 >= var26 && var36 < var18) {
               this.safeSetBlock(var5, var52, Blocks.AIR.defaultBlockState(), var49);

               for (Direction var43 : DIRECTIONS) {
                  BlockPos var44 = var52.relative(var43);
                  FluidState var45 = var5.getFluidState(var44);
                  if (!var45.isEmpty()) {
                     var5.scheduleTick(var44, var45.getType(), 0);
                  }
               }
            } else if (var36 >= var18) {
               this.safeSetBlock(var5, var52, var16.fillingProvider.getState(var3, var52), var49);
            } else if (var36 >= var20) {
               boolean var60 = (double)var3.nextFloat() < var2.useAlternateLayer0Chance;
               if (var60) {
                  this.safeSetBlock(var5, var52, var16.alternateInnerLayerProvider.getState(var3, var52), var49);
               } else {
                  this.safeSetBlock(var5, var52, var16.innerLayerProvider.getState(var3, var52), var49);
               }

               if ((!var2.placementsRequireLayer0Alternate || var60) && (double)var3.nextFloat() < var2.usePotentialPlacementsChance) {
                  var47.add(var52.immutable());
               }
            } else if (var36 >= var22) {
               this.safeSetBlock(var5, var52, var16.middleLayerProvider.getState(var3, var52), var49);
            } else if (var36 >= var24) {
               this.safeSetBlock(var5, var52, var16.outerLayerProvider.getState(var3, var52), var49);
            }
         }
      }

      List var51 = var16.innerPlacements;

      for (BlockPos var55 : var47) {
         BlockState var56 = Util.getRandom(var51, var3);

         for (Direction var39 : DIRECTIONS) {
            if (var56.hasProperty(BlockStateProperties.FACING)) {
               var56 = var56.setValue(BlockStateProperties.FACING, var39);
            }

            BlockPos var62 = var55.relative(var39);
            BlockState var65 = var5.getBlockState(var62);
            if (var56.hasProperty(BlockStateProperties.WATERLOGGED)) {
               var56 = var56.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(var65.getFluidState().isSource()));
            }

            if (BuddingAmethystBlock.canClusterGrowAtState(var65)) {
               this.safeSetBlock(var5, var62, var56, var49);
               break;
            }
         }
      }

      return true;
   }
}
