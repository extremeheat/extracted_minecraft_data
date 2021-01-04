package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class SwampTreeFeature extends AbstractTreeFeature<NoneFeatureConfiguration> {
   private static final BlockState TRUNK;
   private static final BlockState LEAF;

   public SwampTreeFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1) {
      super(var1, false);
   }

   public boolean doPlace(Set<BlockPos> var1, LevelSimulatedRW var2, Random var3, BlockPos var4, BoundingBox var5) {
      int var6 = var3.nextInt(4) + 5;
      var4 = var2.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR, var4);
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
               var9 = 3;
            }

            BlockPos.MutableBlockPos var10 = new BlockPos.MutableBlockPos();

            for(var11 = var4.getX() - var9; var11 <= var4.getX() + var9 && var7; ++var11) {
               for(var12 = var4.getZ() - var9; var12 <= var4.getZ() + var9 && var7; ++var12) {
                  if (var8 >= 0 && var8 < 256) {
                     var10.set(var11, var8, var12);
                     if (!isAirOrLeaves(var2, var10)) {
                        if (isBlockWater(var2, var10)) {
                           if (var8 > var4.getY()) {
                              var7 = false;
                           }
                        } else {
                           var7 = false;
                        }
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

            int var13;
            BlockPos var15;
            int var18;
            int var19;
            for(var8 = var4.getY() - 3 + var6; var8 <= var4.getY() + var6; ++var8) {
               var18 = var8 - (var4.getY() + var6);
               var19 = 2 - var18 / 2;

               for(var11 = var4.getX() - var19; var11 <= var4.getX() + var19; ++var11) {
                  var12 = var11 - var4.getX();

                  for(var13 = var4.getZ() - var19; var13 <= var4.getZ() + var19; ++var13) {
                     int var14 = var13 - var4.getZ();
                     if (Math.abs(var12) != var19 || Math.abs(var14) != var19 || var3.nextInt(2) != 0 && var18 != 0) {
                        var15 = new BlockPos(var11, var8, var13);
                        if (isAirOrLeaves(var2, var15) || isReplaceablePlant(var2, var15)) {
                           this.setBlock(var1, var2, var15, LEAF, var5);
                        }
                     }
                  }
               }
            }

            for(var8 = 0; var8 < var6; ++var8) {
               BlockPos var20 = var4.above(var8);
               if (isAirOrLeaves(var2, var20) || isBlockWater(var2, var20)) {
                  this.setBlock(var1, var2, var20, TRUNK, var5);
               }
            }

            for(var8 = var4.getY() - 3 + var6; var8 <= var4.getY() + var6; ++var8) {
               var18 = var8 - (var4.getY() + var6);
               var19 = 2 - var18 / 2;
               BlockPos.MutableBlockPos var21 = new BlockPos.MutableBlockPos();

               for(var12 = var4.getX() - var19; var12 <= var4.getX() + var19; ++var12) {
                  for(var13 = var4.getZ() - var19; var13 <= var4.getZ() + var19; ++var13) {
                     var21.set(var12, var8, var13);
                     if (isLeaves(var2, var21)) {
                        BlockPos var22 = var21.west();
                        var15 = var21.east();
                        BlockPos var16 = var21.north();
                        BlockPos var17 = var21.south();
                        if (var3.nextInt(4) == 0 && isAir(var2, var22)) {
                           this.addVine(var2, var22, VineBlock.EAST);
                        }

                        if (var3.nextInt(4) == 0 && isAir(var2, var15)) {
                           this.addVine(var2, var15, VineBlock.WEST);
                        }

                        if (var3.nextInt(4) == 0 && isAir(var2, var16)) {
                           this.addVine(var2, var16, VineBlock.SOUTH);
                        }

                        if (var3.nextInt(4) == 0 && isAir(var2, var17)) {
                           this.addVine(var2, var17, VineBlock.NORTH);
                        }
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

   private void addVine(LevelSimulatedRW var1, BlockPos var2, BooleanProperty var3) {
      BlockState var4 = (BlockState)Blocks.VINE.defaultBlockState().setValue(var3, true);
      this.setBlock(var1, var2, var4);
      int var5 = 4;

      for(var2 = var2.below(); isAir(var1, var2) && var5 > 0; --var5) {
         this.setBlock(var1, var2, var4);
         var2 = var2.below();
      }

   }

   static {
      TRUNK = Blocks.OAK_LOG.defaultBlockState();
      LEAF = Blocks.OAK_LEAVES.defaultBlockState();
   }
}
