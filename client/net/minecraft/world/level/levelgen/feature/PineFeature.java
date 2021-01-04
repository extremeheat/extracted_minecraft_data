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

public class PineFeature extends AbstractTreeFeature<NoneFeatureConfiguration> {
   private static final BlockState TRUNK;
   private static final BlockState LEAF;

   public PineFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1) {
      super(var1, false);
   }

   public boolean doPlace(Set<BlockPos> var1, LevelSimulatedRW var2, Random var3, BlockPos var4, BoundingBox var5) {
      int var6 = var3.nextInt(5) + 7;
      int var7 = var6 - var3.nextInt(2) - 3;
      int var8 = var6 - var7;
      int var9 = 1 + var3.nextInt(var8 + 1);
      if (var4.getY() >= 1 && var4.getY() + var6 + 1 <= 256) {
         boolean var10 = true;

         int var11;
         int var14;
         int var15;
         int var18;
         for(var11 = var4.getY(); var11 <= var4.getY() + 1 + var6 && var10; ++var11) {
            boolean var12 = true;
            if (var11 - var4.getY() < var7) {
               var18 = 0;
            } else {
               var18 = var9;
            }

            BlockPos.MutableBlockPos var13 = new BlockPos.MutableBlockPos();

            for(var14 = var4.getX() - var18; var14 <= var4.getX() + var18 && var10; ++var14) {
               for(var15 = var4.getZ() - var18; var15 <= var4.getZ() + var18 && var10; ++var15) {
                  if (var11 >= 0 && var11 < 256) {
                     if (!isFree(var2, var13.set(var14, var11, var15))) {
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
         } else if (isGrassOrDirt(var2, var4.below()) && var4.getY() < 256 - var6 - 1) {
            this.setDirtAt(var2, var4.below());
            var11 = 0;

            for(var18 = var4.getY() + var6; var18 >= var4.getY() + var7; --var18) {
               for(int var19 = var4.getX() - var11; var19 <= var4.getX() + var11; ++var19) {
                  var14 = var19 - var4.getX();

                  for(var15 = var4.getZ() - var11; var15 <= var4.getZ() + var11; ++var15) {
                     int var16 = var15 - var4.getZ();
                     if (Math.abs(var14) != var11 || Math.abs(var16) != var11 || var11 <= 0) {
                        BlockPos var17 = new BlockPos(var19, var18, var15);
                        if (isAirOrLeaves(var2, var17)) {
                           this.setBlock(var1, var2, var17, LEAF, var5);
                        }
                     }
                  }
               }

               if (var11 >= 1 && var18 == var4.getY() + var7 + 1) {
                  --var11;
               } else if (var11 < var9) {
                  ++var11;
               }
            }

            for(var18 = 0; var18 < var6 - 1; ++var18) {
               if (isAirOrLeaves(var2, var4.above(var18))) {
                  this.setBlock(var1, var2, var4.above(var18), TRUNK, var5);
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
