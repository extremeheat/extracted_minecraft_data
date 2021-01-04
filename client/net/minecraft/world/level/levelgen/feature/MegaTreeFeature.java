package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public abstract class MegaTreeFeature<T extends FeatureConfiguration> extends AbstractTreeFeature<T> {
   protected final int baseHeight;
   protected final BlockState trunk;
   protected final BlockState leaf;
   protected final int heightInterval;

   public MegaTreeFeature(Function<Dynamic<?>, ? extends T> var1, boolean var2, int var3, int var4, BlockState var5, BlockState var6) {
      super(var1, var2);
      this.baseHeight = var3;
      this.heightInterval = var4;
      this.trunk = var5;
      this.leaf = var6;
   }

   protected int calcTreeHeigth(Random var1) {
      int var2 = var1.nextInt(3) + this.baseHeight;
      if (this.heightInterval > 1) {
         var2 += var1.nextInt(this.heightInterval);
      }

      return var2;
   }

   private boolean checkIsFree(LevelSimulatedReader var1, BlockPos var2, int var3) {
      boolean var4 = true;
      if (var2.getY() >= 1 && var2.getY() + var3 + 1 <= 256) {
         for(int var5 = 0; var5 <= 1 + var3; ++var5) {
            byte var6 = 2;
            if (var5 == 0) {
               var6 = 1;
            } else if (var5 >= 1 + var3 - 2) {
               var6 = 2;
            }

            for(int var7 = -var6; var7 <= var6 && var4; ++var7) {
               for(int var8 = -var6; var8 <= var6 && var4; ++var8) {
                  if (var2.getY() + var5 < 0 || var2.getY() + var5 >= 256 || !isFree(var1, var2.offset(var7, var5, var8))) {
                     var4 = false;
                  }
               }
            }
         }

         return var4;
      } else {
         return false;
      }
   }

   private boolean makeDirtFloor(LevelSimulatedRW var1, BlockPos var2) {
      BlockPos var3 = var2.below();
      if (isGrassOrDirt(var1, var3) && var2.getY() >= 2) {
         this.setDirtAt(var1, var3);
         this.setDirtAt(var1, var3.east());
         this.setDirtAt(var1, var3.south());
         this.setDirtAt(var1, var3.south().east());
         return true;
      } else {
         return false;
      }
   }

   protected boolean prepareTree(LevelSimulatedRW var1, BlockPos var2, int var3) {
      return this.checkIsFree(var1, var2, var3) && this.makeDirtFloor(var1, var2);
   }

   protected void placeDoubleTrunkLeaves(LevelSimulatedRW var1, BlockPos var2, int var3, BoundingBox var4, Set<BlockPos> var5) {
      int var6 = var3 * var3;

      for(int var7 = -var3; var7 <= var3 + 1; ++var7) {
         for(int var8 = -var3; var8 <= var3 + 1; ++var8) {
            int var9 = Math.min(Math.abs(var7), Math.abs(var7 - 1));
            int var10 = Math.min(Math.abs(var8), Math.abs(var8 - 1));
            if (var9 + var10 < 7 && var9 * var9 + var10 * var10 <= var6) {
               BlockPos var11 = var2.offset(var7, 0, var8);
               if (isAirOrLeaves(var1, var11)) {
                  this.setBlock(var5, var1, var11, this.leaf, var4);
               }
            }
         }
      }

   }

   protected void placeSingleTrunkLeaves(LevelSimulatedRW var1, BlockPos var2, int var3, BoundingBox var4, Set<BlockPos> var5) {
      int var6 = var3 * var3;

      for(int var7 = -var3; var7 <= var3; ++var7) {
         for(int var8 = -var3; var8 <= var3; ++var8) {
            if (var7 * var7 + var8 * var8 <= var6) {
               BlockPos var9 = var2.offset(var7, 0, var8);
               if (isAirOrLeaves(var1, var9)) {
                  this.setBlock(var5, var1, var9, this.leaf, var4);
               }
            }
         }
      }

   }
}
