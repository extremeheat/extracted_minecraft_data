package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class GeodeFeature extends Feature<GeodeConfiguration> {
   private static final Direction[] DIRECTIONS = Direction.values();

   public GeodeFeature(Codec<GeodeConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, GeodeConfiguration var5) {
      int var6 = var5.minGenOffset;
      int var7 = var5.maxGenOffset;
      if (var1.getFluidState(var4.offset(0, var7 / 3, 0)).isSource()) {
         return false;
      } else {
         LinkedList var8 = Lists.newLinkedList();
         int var9 = var5.minDistributionPoints + var3.nextInt(var5.maxDistributionPoints - var5.minDistributionPoints);
         WorldgenRandom var10 = new WorldgenRandom(var1.getSeed());
         NormalNoise var11 = NormalNoise.create(var10, -4, (double[])(1.0D));
         LinkedList var12 = Lists.newLinkedList();
         double var13 = (double)var9 / (double)var5.maxOuterWallDistance;
         GeodeLayerSettings var15 = var5.geodeLayerSettings;
         GeodeBlockSettings var16 = var5.geodeBlockSettings;
         GeodeCrackSettings var17 = var5.geodeCrackSettings;
         double var18 = 1.0D / Math.sqrt(var15.filling);
         double var20 = 1.0D / Math.sqrt(var15.innerLayer + var13);
         double var22 = 1.0D / Math.sqrt(var15.middleLayer + var13);
         double var24 = 1.0D / Math.sqrt(var15.outerLayer + var13);
         double var26 = 1.0D / Math.sqrt(var17.baseCrackSize + var3.nextDouble() / 2.0D + (var9 > 3 ? var13 : 0.0D));
         boolean var28 = (double)var3.nextFloat() < var17.generateCrackChance;

         int var29;
         int var30;
         for(var29 = 0; var29 < var9; ++var29) {
            var30 = var5.minOuterWallDistance + var3.nextInt(var5.maxOuterWallDistance - var5.minOuterWallDistance);
            int var31 = var5.minOuterWallDistance + var3.nextInt(var5.maxOuterWallDistance - var5.minOuterWallDistance);
            int var32 = var5.minOuterWallDistance + var3.nextInt(var5.maxOuterWallDistance - var5.minOuterWallDistance);
            var8.add(Pair.of(var4.offset(var30, var31, var32), var5.minPointOffset + var3.nextInt(var5.maxPointOffset - var5.minPointOffset)));
         }

         if (var28) {
            var29 = var3.nextInt(4);
            var30 = var9 * 2 + 1;
            if (var29 == 0) {
               var12.add(var4.offset(var30, 7, 0));
               var12.add(var4.offset(var30, 5, 0));
               var12.add(var4.offset(var30, 1, 0));
            } else if (var29 == 1) {
               var12.add(var4.offset(0, 7, var30));
               var12.add(var4.offset(0, 5, var30));
               var12.add(var4.offset(0, 1, var30));
            } else if (var29 == 2) {
               var12.add(var4.offset(var30, 7, var30));
               var12.add(var4.offset(var30, 5, var30));
               var12.add(var4.offset(var30, 1, var30));
            } else {
               var12.add(var4.offset(0, 7, 0));
               var12.add(var4.offset(0, 5, 0));
               var12.add(var4.offset(0, 1, 0));
            }
         }

         ArrayList var40 = Lists.newArrayList();
         Iterator var41 = BlockPos.betweenClosed(var4.offset(var6, var6, var6), var4.offset(var7, var7, var7)).iterator();

         while(true) {
            while(true) {
               double var34;
               double var36;
               BlockPos var43;
               do {
                  if (!var41.hasNext()) {
                     List var42 = var16.innerPlacements;
                     Iterator var44 = var40.iterator();

                     while(true) {
                        while(var44.hasNext()) {
                           BlockPos var46 = (BlockPos)var44.next();
                           BlockState var33 = (BlockState)var42.get(var3.nextInt(var42.size()));
                           Direction[] var47 = DIRECTIONS;
                           int var35 = var47.length;

                           for(int var48 = 0; var48 < var35; ++var48) {
                              Direction var37 = var47[var48];
                              if (var33.hasProperty(BlockStateProperties.FACING)) {
                                 var33 = (BlockState)var33.setValue(BlockStateProperties.FACING, var37);
                              }

                              BlockPos var50 = var46.relative(var37);
                              BlockState var52 = var1.getBlockState(var50);
                              if (var33.hasProperty(BlockStateProperties.WATERLOGGED)) {
                                 var33 = (BlockState)var33.setValue(BlockStateProperties.WATERLOGGED, var52.getFluidState().isSource());
                              }

                              if (BuddingAmethystBlock.canClusterGrowAtState(var52)) {
                                 var1.setBlock(var50, var33, 2);
                                 break;
                              }
                           }
                        }

                        return true;
                     }
                  }

                  var43 = (BlockPos)var41.next();
                  double var45 = var11.getValue((double)var43.getX(), (double)var43.getY(), (double)var43.getZ()) * var5.noiseMultiplier;
                  var34 = 0.0D;
                  var36 = 0.0D;

                  Iterator var38;
                  Pair var39;
                  for(var38 = var8.iterator(); var38.hasNext(); var34 += Mth.fastInvSqrt(var43.distSqr((Vec3i)var39.getFirst()) + (double)(Integer)var39.getSecond()) + var45) {
                     var39 = (Pair)var38.next();
                  }

                  BlockPos var51;
                  for(var38 = var12.iterator(); var38.hasNext(); var36 += Mth.fastInvSqrt(var43.distSqr(var51) + (double)var17.crackPointOffset) + var45) {
                     var51 = (BlockPos)var38.next();
                  }
               } while(var34 < var24);

               if (var28 && var36 >= var26 && var34 < var18) {
                  if (var1.getFluidState(var43).isEmpty()) {
                     var1.setBlock(var43, Blocks.AIR.defaultBlockState(), 2);
                  }
               } else if (var34 >= var18) {
                  var1.setBlock(var43, var16.fillingProvider.getState(var3, var43), 2);
               } else if (var34 >= var20) {
                  boolean var49 = (double)var3.nextFloat() < var5.useAlternateLayer0Chance;
                  if (var49) {
                     var1.setBlock(var43, var16.alternateInnerLayerProvider.getState(var3, var43), 2);
                  } else {
                     var1.setBlock(var43, var16.innerLayerProvider.getState(var3, var43), 2);
                  }

                  if ((!var5.placementsRequireLayer0Alternate || var49) && (double)var3.nextFloat() < var5.usePotentialPlacementsChance) {
                     var40.add(var43.immutable());
                  }
               } else if (var34 >= var22) {
                  var1.setBlock(var43, var16.middleLayerProvider.getState(var3, var43), 2);
               } else if (var34 >= var24) {
                  var1.setBlock(var43, var16.outerLayerProvider.getState(var3, var43), 2);
               }
            }
         }
      }
   }
}
