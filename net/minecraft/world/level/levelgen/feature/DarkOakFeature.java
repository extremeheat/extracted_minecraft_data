package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.MegaTreeConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class DarkOakFeature extends AbstractTreeFeature {
   public DarkOakFeature(Function var1) {
      super(var1);
   }

   public boolean doPlace(LevelSimulatedRW var1, Random var2, BlockPos var3, Set var4, Set var5, BoundingBox var6, MegaTreeConfiguration var7) {
      int var8 = var2.nextInt(3) + var2.nextInt(2) + var7.baseHeight;
      int var9 = var3.getX();
      int var10 = var3.getY();
      int var11 = var3.getZ();
      if (var10 >= 1 && var10 + var8 + 1 < 256) {
         BlockPos var12 = var3.below();
         if (!isGrassOrDirt(var1, var12)) {
            return false;
         } else if (!this.canPlaceTreeOfHeight(var1, var3, var8)) {
            return false;
         } else {
            this.setDirtAt(var1, var12);
            this.setDirtAt(var1, var12.east());
            this.setDirtAt(var1, var12.south());
            this.setDirtAt(var1, var12.south().east());
            Direction var13 = Direction.Plane.HORIZONTAL.getRandomDirection(var2);
            int var14 = var8 - var2.nextInt(4);
            int var15 = 2 - var2.nextInt(3);
            int var16 = var9;
            int var17 = var11;
            int var18 = var10 + var8 - 1;

            int var19;
            int var20;
            for(var19 = 0; var19 < var8; ++var19) {
               if (var19 >= var14 && var15 > 0) {
                  var16 += var13.getStepX();
                  var17 += var13.getStepZ();
                  --var15;
               }

               var20 = var10 + var19;
               BlockPos var21 = new BlockPos(var16, var20, var17);
               if (isAirOrLeaves(var1, var21)) {
                  this.placeLog(var1, var2, var21, var4, var6, var7);
                  this.placeLog(var1, var2, var21.east(), var4, var6, var7);
                  this.placeLog(var1, var2, var21.south(), var4, var6, var7);
                  this.placeLog(var1, var2, var21.east().south(), var4, var6, var7);
               }
            }

            for(var19 = -2; var19 <= 0; ++var19) {
               for(var20 = -2; var20 <= 0; ++var20) {
                  byte var24 = -1;
                  this.placeLeaf(var1, var2, new BlockPos(var16 + var19, var18 + var24, var17 + var20), var5, var6, var7);
                  this.placeLeaf(var1, var2, new BlockPos(1 + var16 - var19, var18 + var24, var17 + var20), var5, var6, var7);
                  this.placeLeaf(var1, var2, new BlockPos(var16 + var19, var18 + var24, 1 + var17 - var20), var5, var6, var7);
                  this.placeLeaf(var1, var2, new BlockPos(1 + var16 - var19, var18 + var24, 1 + var17 - var20), var5, var6, var7);
                  if ((var19 > -2 || var20 > -1) && (var19 != -1 || var20 != -2)) {
                     byte var25 = 1;
                     this.placeLeaf(var1, var2, new BlockPos(var16 + var19, var18 + var25, var17 + var20), var5, var6, var7);
                     this.placeLeaf(var1, var2, new BlockPos(1 + var16 - var19, var18 + var25, var17 + var20), var5, var6, var7);
                     this.placeLeaf(var1, var2, new BlockPos(var16 + var19, var18 + var25, 1 + var17 - var20), var5, var6, var7);
                     this.placeLeaf(var1, var2, new BlockPos(1 + var16 - var19, var18 + var25, 1 + var17 - var20), var5, var6, var7);
                  }
               }
            }

            if (var2.nextBoolean()) {
               this.placeLeaf(var1, var2, new BlockPos(var16, var18 + 2, var17), var5, var6, var7);
               this.placeLeaf(var1, var2, new BlockPos(var16 + 1, var18 + 2, var17), var5, var6, var7);
               this.placeLeaf(var1, var2, new BlockPos(var16 + 1, var18 + 2, var17 + 1), var5, var6, var7);
               this.placeLeaf(var1, var2, new BlockPos(var16, var18 + 2, var17 + 1), var5, var6, var7);
            }

            for(var19 = -3; var19 <= 4; ++var19) {
               for(var20 = -3; var20 <= 4; ++var20) {
                  if ((var19 != -3 || var20 != -3) && (var19 != -3 || var20 != 4) && (var19 != 4 || var20 != -3) && (var19 != 4 || var20 != 4) && (Math.abs(var19) < 3 || Math.abs(var20) < 3)) {
                     this.placeLeaf(var1, var2, new BlockPos(var16 + var19, var18, var17 + var20), var5, var6, var7);
                  }
               }
            }

            for(var19 = -1; var19 <= 2; ++var19) {
               for(var20 = -1; var20 <= 2; ++var20) {
                  if ((var19 < 0 || var19 > 1 || var20 < 0 || var20 > 1) && var2.nextInt(3) <= 0) {
                     int var26 = var2.nextInt(3) + 2;

                     int var22;
                     for(var22 = 0; var22 < var26; ++var22) {
                        this.placeLog(var1, var2, new BlockPos(var9 + var19, var18 - var22 - 1, var11 + var20), var4, var6, var7);
                     }

                     int var23;
                     for(var22 = -1; var22 <= 1; ++var22) {
                        for(var23 = -1; var23 <= 1; ++var23) {
                           this.placeLeaf(var1, var2, new BlockPos(var16 + var19 + var22, var18, var17 + var20 + var23), var5, var6, var7);
                        }
                     }

                     for(var22 = -2; var22 <= 2; ++var22) {
                        for(var23 = -2; var23 <= 2; ++var23) {
                           if (Math.abs(var22) != 2 || Math.abs(var23) != 2) {
                              this.placeLeaf(var1, var2, new BlockPos(var16 + var19 + var22, var18 - 1, var17 + var20 + var23), var5, var6, var7);
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
}
