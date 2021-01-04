package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class TreeFeature extends AbstractTreeFeature<NoneFeatureConfiguration> {
   private static final BlockState DEFAULT_TRUNK;
   private static final BlockState DEFAULT_LEAF;
   protected final int baseHeight;
   private final boolean addJungleFeatures;
   private final BlockState trunk;
   private final BlockState leaf;

   public TreeFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1, boolean var2) {
      this(var1, var2, 4, DEFAULT_TRUNK, DEFAULT_LEAF, false);
   }

   public TreeFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1, boolean var2, int var3, BlockState var4, BlockState var5, boolean var6) {
      super(var1, var2);
      this.baseHeight = var3;
      this.trunk = var4;
      this.leaf = var5;
      this.addJungleFeatures = var6;
   }

   public boolean doPlace(Set<BlockPos> var1, LevelSimulatedRW var2, Random var3, BlockPos var4, BoundingBox var5) {
      int var6 = this.getTreeHeight(var3);
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
         } else if (isGrassOrDirtOrFarmland(var2, var4.below()) && var4.getY() < 256 - var6 - 1) {
            this.setDirtAt(var2, var4.below());
            boolean var20 = true;
            boolean var21 = false;

            int var14;
            int var15;
            BlockPos var17;
            int var22;
            for(var22 = var4.getY() - 3 + var6; var22 <= var4.getY() + var6; ++var22) {
               var11 = var22 - (var4.getY() + var6);
               var12 = 1 - var11 / 2;

               for(int var13 = var4.getX() - var12; var13 <= var4.getX() + var12; ++var13) {
                  var14 = var13 - var4.getX();

                  for(var15 = var4.getZ() - var12; var15 <= var4.getZ() + var12; ++var15) {
                     int var16 = var15 - var4.getZ();
                     if (Math.abs(var14) != var12 || Math.abs(var16) != var12 || var3.nextInt(2) != 0 && var11 != 0) {
                        var17 = new BlockPos(var13, var22, var15);
                        if (isAirOrLeaves(var2, var17) || isReplaceablePlant(var2, var17)) {
                           this.setBlock(var1, var2, var17, this.leaf, var5);
                        }
                     }
                  }
               }
            }

            for(var22 = 0; var22 < var6; ++var22) {
               if (isAirOrLeaves(var2, var4.above(var22)) || isReplaceablePlant(var2, var4.above(var22))) {
                  this.setBlock(var1, var2, var4.above(var22), this.trunk, var5);
                  if (this.addJungleFeatures && var22 > 0) {
                     if (var3.nextInt(3) > 0 && isAir(var2, var4.offset(-1, var22, 0))) {
                        this.addVine(var2, var4.offset(-1, var22, 0), VineBlock.EAST);
                     }

                     if (var3.nextInt(3) > 0 && isAir(var2, var4.offset(1, var22, 0))) {
                        this.addVine(var2, var4.offset(1, var22, 0), VineBlock.WEST);
                     }

                     if (var3.nextInt(3) > 0 && isAir(var2, var4.offset(0, var22, -1))) {
                        this.addVine(var2, var4.offset(0, var22, -1), VineBlock.SOUTH);
                     }

                     if (var3.nextInt(3) > 0 && isAir(var2, var4.offset(0, var22, 1))) {
                        this.addVine(var2, var4.offset(0, var22, 1), VineBlock.NORTH);
                     }
                  }
               }
            }

            if (this.addJungleFeatures) {
               for(var22 = var4.getY() - 3 + var6; var22 <= var4.getY() + var6; ++var22) {
                  var11 = var22 - (var4.getY() + var6);
                  var12 = 2 - var11 / 2;
                  BlockPos.MutableBlockPos var25 = new BlockPos.MutableBlockPos();

                  for(var14 = var4.getX() - var12; var14 <= var4.getX() + var12; ++var14) {
                     for(var15 = var4.getZ() - var12; var15 <= var4.getZ() + var12; ++var15) {
                        var25.set(var14, var22, var15);
                        if (isLeaves(var2, var25)) {
                           BlockPos var27 = var25.west();
                           var17 = var25.east();
                           BlockPos var18 = var25.north();
                           BlockPos var19 = var25.south();
                           if (var3.nextInt(4) == 0 && isAir(var2, var27)) {
                              this.addHangingVine(var2, var27, VineBlock.EAST);
                           }

                           if (var3.nextInt(4) == 0 && isAir(var2, var17)) {
                              this.addHangingVine(var2, var17, VineBlock.WEST);
                           }

                           if (var3.nextInt(4) == 0 && isAir(var2, var18)) {
                              this.addHangingVine(var2, var18, VineBlock.SOUTH);
                           }

                           if (var3.nextInt(4) == 0 && isAir(var2, var19)) {
                              this.addHangingVine(var2, var19, VineBlock.NORTH);
                           }
                        }
                     }
                  }
               }

               if (var3.nextInt(5) == 0 && var6 > 5) {
                  for(var22 = 0; var22 < 2; ++var22) {
                     Iterator var23 = Direction.Plane.HORIZONTAL.iterator();

                     while(var23.hasNext()) {
                        Direction var24 = (Direction)var23.next();
                        if (var3.nextInt(4 - var22) == 0) {
                           Direction var26 = var24.getOpposite();
                           this.placeCocoa(var2, var3.nextInt(3), var4.offset(var26.getStepX(), var6 - 5 + var22, var26.getStepZ()), var24);
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

   protected int getTreeHeight(Random var1) {
      return this.baseHeight + var1.nextInt(3);
   }

   private void placeCocoa(LevelWriter var1, int var2, BlockPos var3, Direction var4) {
      this.setBlock(var1, var3, (BlockState)((BlockState)Blocks.COCOA.defaultBlockState().setValue(CocoaBlock.AGE, var2)).setValue(CocoaBlock.FACING, var4));
   }

   private void addVine(LevelWriter var1, BlockPos var2, BooleanProperty var3) {
      this.setBlock(var1, var2, (BlockState)Blocks.VINE.defaultBlockState().setValue(var3, true));
   }

   private void addHangingVine(LevelSimulatedRW var1, BlockPos var2, BooleanProperty var3) {
      this.addVine(var1, var2, var3);
      int var4 = 4;

      for(var2 = var2.below(); isAir(var1, var2) && var4 > 0; --var4) {
         this.addVine(var1, var2, var3);
         var2 = var2.below();
      }

   }

   static {
      DEFAULT_TRUNK = Blocks.OAK_LOG.defaultBlockState();
      DEFAULT_LEAF = Blocks.OAK_LEAVES.defaultBlockState();
   }
}
