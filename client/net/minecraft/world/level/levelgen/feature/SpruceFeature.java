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

public class SpruceFeature extends AbstractTreeFeature<NoneFeatureConfiguration> {
   private static final BlockState TRUNK;
   private static final BlockState LEAF;

   public SpruceFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1, boolean var2) {
      super(var1, var2);
   }

   public boolean doPlace(Set<BlockPos> var1, LevelSimulatedRW var2, Random var3, BlockPos var4, BoundingBox var5) {
      int var6 = var3.nextInt(4) + 6;
      int var7 = 1 + var3.nextInt(2);
      int var8 = var6 - var7;
      int var9 = 2 + var3.nextInt(2);
      boolean var10 = true;
      if (var4.getY() >= 1 && var4.getY() + var6 + 1 <= 256) {
         int var11;
         int var12;
         int var14;
         int var15;
         for(var11 = var4.getY(); var11 <= var4.getY() + 1 + var6 && var10; ++var11) {
            if (var11 - var4.getY() < var7) {
               var12 = 0;
            } else {
               var12 = var9;
            }

            BlockPos.MutableBlockPos var13 = new BlockPos.MutableBlockPos();

            for(var14 = var4.getX() - var12; var14 <= var4.getX() + var12 && var10; ++var14) {
               for(var15 = var4.getZ() - var12; var15 <= var4.getZ() + var12 && var10; ++var15) {
                  if (var11 >= 0 && var11 < 256) {
                     var13.set(var14, var11, var15);
                     if (!isAirOrLeaves(var2, var13)) {
                        var10 = false;
                     }
                  } else {
                     var10 = false;
                  }
               }
            }
         }

         if (!var10) {
            return false;
         } else if (isGrassOrDirtOrFarmland(var2, var4.below()) && var4.getY() < 256 - var6 - 1) {
            this.setDirtAt(var2, var4.below());
            var11 = var3.nextInt(2);
            var12 = 1;
            byte var21 = 0;

            for(var14 = 0; var14 <= var8; ++var14) {
               var15 = var4.getY() + var6 - var14;

               for(int var16 = var4.getX() - var11; var16 <= var4.getX() + var11; ++var16) {
                  int var17 = var16 - var4.getX();

                  for(int var18 = var4.getZ() - var11; var18 <= var4.getZ() + var11; ++var18) {
                     int var19 = var18 - var4.getZ();
                     if (Math.abs(var17) != var11 || Math.abs(var19) != var11 || var11 <= 0) {
                        BlockPos var20 = new BlockPos(var16, var15, var18);
                        if (isAirOrLeaves(var2, var20) || isReplaceablePlant(var2, var20)) {
                           this.setBlock(var1, var2, var20, LEAF, var5);
                        }
                     }
                  }
               }

               if (var11 >= var12) {
                  var11 = var21;
                  var21 = 1;
                  ++var12;
                  if (var12 > var9) {
                     var12 = var9;
                  }
               } else {
                  ++var11;
               }
            }

            var14 = var3.nextInt(3);

            for(var15 = 0; var15 < var6 - var14; ++var15) {
               if (isAirOrLeaves(var2, var4.above(var15))) {
                  this.setBlock(var1, var2, var4.above(var15), TRUNK, var5);
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
      TRUNK = Blocks.SPRUCE_LOG.defaultBlockState();
      LEAF = Blocks.SPRUCE_LEAVES.defaultBlockState();
   }
}
