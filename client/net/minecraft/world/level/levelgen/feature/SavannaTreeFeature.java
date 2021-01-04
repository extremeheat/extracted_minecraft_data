package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class SavannaTreeFeature extends AbstractTreeFeature<NoneFeatureConfiguration> {
   private static final BlockState TRUNK;
   private static final BlockState LEAF;

   public SavannaTreeFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1, boolean var2) {
      super(var1, var2);
   }

   public boolean doPlace(Set<BlockPos> var1, LevelSimulatedRW var2, Random var3, BlockPos var4, BoundingBox var5) {
      int var6 = var3.nextInt(3) + var3.nextInt(3) + 5;
      boolean var7 = true;
      if (var4.getY() >= 1 && var4.getY() + var6 + 1 <= 256) {
         int var11;
         int var12;
         for(int var8 = var4.getY(); var8 <= var4.getY() + 1 + var6; ++var8) {
            byte var9 = 1;
            if (var8 == var4.getY()) {
               var9 = 0;
            }

            if (var8 >= var4.getY() + 1 + var6 - 2) {
               var9 = 2;
            }

            BlockPos.MutableBlockPos var10 = new BlockPos.MutableBlockPos();

            for(var11 = var4.getX() - var9; var11 <= var4.getX() + var9 && var7; ++var11) {
               for(var12 = var4.getZ() - var9; var12 <= var4.getZ() + var9 && var7; ++var12) {
                  if (var8 >= 0 && var8 < 256) {
                     if (!isFree(var2, var10.set(var11, var8, var12))) {
                        var7 = false;
                     }
                  } else {
                     var7 = false;
                  }
               }
            }
         }

         if (!var7) {
            return false;
         } else if (isGrassOrDirt(var2, var4.below()) && var4.getY() < 256 - var6 - 1) {
            this.setDirtAt(var2, var4.below());
            Direction var20 = Direction.Plane.HORIZONTAL.getRandomDirection(var3);
            int var21 = var6 - var3.nextInt(4) - 1;
            int var22 = 3 - var3.nextInt(3);
            var11 = var4.getX();
            var12 = var4.getZ();
            int var13 = 0;

            int var15;
            for(int var14 = 0; var14 < var6; ++var14) {
               var15 = var4.getY() + var14;
               if (var14 >= var21 && var22 > 0) {
                  var11 += var20.getStepX();
                  var12 += var20.getStepZ();
                  --var22;
               }

               BlockPos var16 = new BlockPos(var11, var15, var12);
               if (isAirOrLeaves(var2, var16)) {
                  this.placeLogAt(var1, var2, var16, var5);
                  var13 = var15;
               }
            }

            BlockPos var23 = new BlockPos(var11, var13, var12);

            int var25;
            for(var15 = -3; var15 <= 3; ++var15) {
               for(var25 = -3; var25 <= 3; ++var25) {
                  if (Math.abs(var15) != 3 || Math.abs(var25) != 3) {
                     this.placeLeafAt(var1, var2, var23.offset(var15, 0, var25), var5);
                  }
               }
            }

            var23 = var23.above();

            for(var15 = -1; var15 <= 1; ++var15) {
               for(var25 = -1; var25 <= 1; ++var25) {
                  this.placeLeafAt(var1, var2, var23.offset(var15, 0, var25), var5);
               }
            }

            this.placeLeafAt(var1, var2, var23.east(2), var5);
            this.placeLeafAt(var1, var2, var23.west(2), var5);
            this.placeLeafAt(var1, var2, var23.south(2), var5);
            this.placeLeafAt(var1, var2, var23.north(2), var5);
            var11 = var4.getX();
            var12 = var4.getZ();
            Direction var24 = Direction.Plane.HORIZONTAL.getRandomDirection(var3);
            if (var24 != var20) {
               var15 = var21 - var3.nextInt(2) - 1;
               var25 = 1 + var3.nextInt(3);
               var13 = 0;

               int var18;
               for(int var17 = var15; var17 < var6 && var25 > 0; --var25) {
                  if (var17 >= 1) {
                     var18 = var4.getY() + var17;
                     var11 += var24.getStepX();
                     var12 += var24.getStepZ();
                     BlockPos var19 = new BlockPos(var11, var18, var12);
                     if (isAirOrLeaves(var2, var19)) {
                        this.placeLogAt(var1, var2, var19, var5);
                        var13 = var18;
                     }
                  }

                  ++var17;
               }

               if (var13 > 0) {
                  BlockPos var26 = new BlockPos(var11, var13, var12);

                  int var27;
                  for(var18 = -2; var18 <= 2; ++var18) {
                     for(var27 = -2; var27 <= 2; ++var27) {
                        if (Math.abs(var18) != 2 || Math.abs(var27) != 2) {
                           this.placeLeafAt(var1, var2, var26.offset(var18, 0, var27), var5);
                        }
                     }
                  }

                  var26 = var26.above();

                  for(var18 = -1; var18 <= 1; ++var18) {
                     for(var27 = -1; var27 <= 1; ++var27) {
                        this.placeLeafAt(var1, var2, var26.offset(var18, 0, var27), var5);
                     }
                  }
               }
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private void placeLogAt(Set<BlockPos> var1, LevelWriter var2, BlockPos var3, BoundingBox var4) {
      this.setBlock(var1, var2, var3, TRUNK, var4);
   }

   private void placeLeafAt(Set<BlockPos> var1, LevelSimulatedRW var2, BlockPos var3, BoundingBox var4) {
      if (isAirOrLeaves(var2, var3)) {
         this.setBlock(var1, var2, var3, LEAF, var4);
      }

   }

   static {
      TRUNK = Blocks.ACACIA_LOG.defaultBlockState();
      LEAF = Blocks.ACACIA_LEAVES.defaultBlockState();
   }
}
