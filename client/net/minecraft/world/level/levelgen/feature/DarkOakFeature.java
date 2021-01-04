package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class DarkOakFeature extends AbstractTreeFeature<NoneFeatureConfiguration> {
   private static final BlockState LOG;
   private static final BlockState LEAVES;

   public DarkOakFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1, boolean var2) {
      super(var1, var2);
   }

   public boolean doPlace(Set<BlockPos> var1, LevelSimulatedRW var2, Random var3, BlockPos var4, BoundingBox var5) {
      int var6 = var3.nextInt(3) + var3.nextInt(2) + 6;
      int var7 = var4.getX();
      int var8 = var4.getY();
      int var9 = var4.getZ();
      if (var8 >= 1 && var8 + var6 + 1 < 256) {
         BlockPos var10 = var4.below();
         if (!isGrassOrDirt(var2, var10)) {
            return false;
         } else if (!this.canPlaceTreeOfHeight(var2, var4, var6)) {
            return false;
         } else {
            this.setDirtAt(var2, var10);
            this.setDirtAt(var2, var10.east());
            this.setDirtAt(var2, var10.south());
            this.setDirtAt(var2, var10.south().east());
            Direction var11 = Direction.Plane.HORIZONTAL.getRandomDirection(var3);
            int var12 = var6 - var3.nextInt(4);
            int var13 = 2 - var3.nextInt(3);
            int var14 = var7;
            int var15 = var9;
            int var16 = var8 + var6 - 1;

            int var17;
            int var18;
            for(var17 = 0; var17 < var6; ++var17) {
               if (var17 >= var12 && var13 > 0) {
                  var14 += var11.getStepX();
                  var15 += var11.getStepZ();
                  --var13;
               }

               var18 = var8 + var17;
               BlockPos var19 = new BlockPos(var14, var18, var15);
               if (isAirOrLeaves(var2, var19)) {
                  this.placeLogAt(var1, var2, var19, var5);
                  this.placeLogAt(var1, var2, var19.east(), var5);
                  this.placeLogAt(var1, var2, var19.south(), var5);
                  this.placeLogAt(var1, var2, var19.east().south(), var5);
               }
            }

            for(var17 = -2; var17 <= 0; ++var17) {
               for(var18 = -2; var18 <= 0; ++var18) {
                  byte var22 = -1;
                  this.placeLeafAt(var2, var14 + var17, var16 + var22, var15 + var18, var5, var1);
                  this.placeLeafAt(var2, 1 + var14 - var17, var16 + var22, var15 + var18, var5, var1);
                  this.placeLeafAt(var2, var14 + var17, var16 + var22, 1 + var15 - var18, var5, var1);
                  this.placeLeafAt(var2, 1 + var14 - var17, var16 + var22, 1 + var15 - var18, var5, var1);
                  if ((var17 > -2 || var18 > -1) && (var17 != -1 || var18 != -2)) {
                     byte var23 = 1;
                     this.placeLeafAt(var2, var14 + var17, var16 + var23, var15 + var18, var5, var1);
                     this.placeLeafAt(var2, 1 + var14 - var17, var16 + var23, var15 + var18, var5, var1);
                     this.placeLeafAt(var2, var14 + var17, var16 + var23, 1 + var15 - var18, var5, var1);
                     this.placeLeafAt(var2, 1 + var14 - var17, var16 + var23, 1 + var15 - var18, var5, var1);
                  }
               }
            }

            if (var3.nextBoolean()) {
               this.placeLeafAt(var2, var14, var16 + 2, var15, var5, var1);
               this.placeLeafAt(var2, var14 + 1, var16 + 2, var15, var5, var1);
               this.placeLeafAt(var2, var14 + 1, var16 + 2, var15 + 1, var5, var1);
               this.placeLeafAt(var2, var14, var16 + 2, var15 + 1, var5, var1);
            }

            for(var17 = -3; var17 <= 4; ++var17) {
               for(var18 = -3; var18 <= 4; ++var18) {
                  if ((var17 != -3 || var18 != -3) && (var17 != -3 || var18 != 4) && (var17 != 4 || var18 != -3) && (var17 != 4 || var18 != 4) && (Math.abs(var17) < 3 || Math.abs(var18) < 3)) {
                     this.placeLeafAt(var2, var14 + var17, var16, var15 + var18, var5, var1);
                  }
               }
            }

            for(var17 = -1; var17 <= 2; ++var17) {
               for(var18 = -1; var18 <= 2; ++var18) {
                  if ((var17 < 0 || var17 > 1 || var18 < 0 || var18 > 1) && var3.nextInt(3) <= 0) {
                     int var24 = var3.nextInt(3) + 2;

                     int var20;
                     for(var20 = 0; var20 < var24; ++var20) {
                        this.placeLogAt(var1, var2, new BlockPos(var7 + var17, var16 - var20 - 1, var9 + var18), var5);
                     }

                     int var21;
                     for(var20 = -1; var20 <= 1; ++var20) {
                        for(var21 = -1; var21 <= 1; ++var21) {
                           this.placeLeafAt(var2, var14 + var17 + var20, var16, var15 + var18 + var21, var5, var1);
                        }
                     }

                     for(var20 = -2; var20 <= 2; ++var20) {
                        for(var21 = -2; var21 <= 2; ++var21) {
                           if (Math.abs(var20) != 2 || Math.abs(var21) != 2) {
                              this.placeLeafAt(var2, var14 + var17 + var20, var16 - 1, var15 + var18 + var21, var5, var1);
                           }
                        }
                     }
                  }
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private boolean canPlaceTreeOfHeight(LevelSimulatedReader var1, BlockPos var2, int var3) {
      int var4 = var2.getX();
      int var5 = var2.getY();
      int var6 = var2.getZ();
      BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos();

      for(int var8 = 0; var8 <= var3 + 1; ++var8) {
         byte var9 = 1;
         if (var8 == 0) {
            var9 = 0;
         }

         if (var8 >= var3 - 1) {
            var9 = 2;
         }

         for(int var10 = -var9; var10 <= var9; ++var10) {
            for(int var11 = -var9; var11 <= var9; ++var11) {
               if (!isFree(var1, var7.set(var4 + var10, var5 + var8, var6 + var11))) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private void placeLogAt(Set<BlockPos> var1, LevelSimulatedRW var2, BlockPos var3, BoundingBox var4) {
      if (isFree(var2, var3)) {
         this.setBlock(var1, var2, var3, LOG, var4);
      }

   }

   private void placeLeafAt(LevelSimulatedRW var1, int var2, int var3, int var4, BoundingBox var5, Set<BlockPos> var6) {
      BlockPos var7 = new BlockPos(var2, var3, var4);
      if (isAir(var1, var7)) {
         this.setBlock(var6, var1, var7, LEAVES, var5);
      }

   }

   static {
      LOG = Blocks.DARK_OAK_LOG.defaultBlockState();
      LEAVES = Blocks.DARK_OAK_LEAVES.defaultBlockState();
   }
}
