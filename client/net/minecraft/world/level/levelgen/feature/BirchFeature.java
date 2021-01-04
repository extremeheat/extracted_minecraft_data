package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class BirchFeature extends AbstractTreeFeature<NoneFeatureConfiguration> {
   private static final BlockState LOG;
   private static final BlockState LEAF;
   private final boolean superBirch;

   public BirchFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1, boolean var2, boolean var3) {
      super(var1, var2);
      this.superBirch = var3;
   }

   public boolean doPlace(Set<BlockPos> var1, LevelSimulatedRW var2, Random var3, BlockPos var4, BoundingBox var5) {
      int var6 = var3.nextInt(3) + 5;
      if (this.superBirch) {
         var6 += var3.nextInt(7);
      }

      boolean var7 = true;
      if (var4.getY() >= 1 && var4.getY() + var6 + 1 <= 256) {
         int var8;
         int var11;
         int var12;
         for(var8 = var4.getY(); var8 <= var4.getY() + 1 + var6; ++var8) {
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
         } else if (isGrassOrDirtOrFarmland(var2, var4.below()) && var4.getY() < 256 - var6 - 1) {
            this.setDirtAt(var2, var4.below());

            for(var8 = var4.getY() - 3 + var6; var8 <= var4.getY() + var6; ++var8) {
               int var16 = var8 - (var4.getY() + var6);
               int var17 = 1 - var16 / 2;

               for(var11 = var4.getX() - var17; var11 <= var4.getX() + var17; ++var11) {
                  var12 = var11 - var4.getX();

                  for(int var13 = var4.getZ() - var17; var13 <= var4.getZ() + var17; ++var13) {
                     int var14 = var13 - var4.getZ();
                     if (Math.abs(var12) != var17 || Math.abs(var14) != var17 || var3.nextInt(2) != 0 && var16 != 0) {
                        BlockPos var15 = new BlockPos(var11, var8, var13);
                        if (isAirOrLeaves(var2, var15)) {
                           this.setBlock(var1, var2, var15, LEAF, var5);
                        }
                     }
                  }
               }
            }

            for(var8 = 0; var8 < var6; ++var8) {
               if (isAirOrLeaves(var2, var4.above(var8))) {
                  this.setBlock(var1, var2, var4.above(var8), LOG, var5);
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

   static {
      LOG = Blocks.BIRCH_LOG.defaultBlockState();
      LEAF = Blocks.BIRCH_LEAVES.defaultBlockState();
   }
}
