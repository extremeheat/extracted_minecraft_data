package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class MegaPineTreeFeature extends MegaTreeFeature<NoneFeatureConfiguration> {
   private static final BlockState TRUNK;
   private static final BlockState LEAF;
   private static final BlockState PODZOL;
   private final boolean isSpruce;

   public MegaPineTreeFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1, boolean var2, boolean var3) {
      super(var1, var2, 13, 15, TRUNK, LEAF);
      this.isSpruce = var3;
   }

   public boolean doPlace(Set<BlockPos> var1, LevelSimulatedRW var2, Random var3, BlockPos var4, BoundingBox var5) {
      int var6 = this.calcTreeHeigth(var3);
      if (!this.prepareTree(var2, var4, var6)) {
         return false;
      } else {
         this.createCrown(var2, var4.getX(), var4.getZ(), var4.getY() + var6, 0, var3, var5, var1);

         for(int var7 = 0; var7 < var6; ++var7) {
            if (isAirOrLeaves(var2, var4.above(var7))) {
               this.setBlock(var1, var2, var4.above(var7), this.trunk, var5);
            }

            if (var7 < var6 - 1) {
               if (isAirOrLeaves(var2, var4.offset(1, var7, 0))) {
                  this.setBlock(var1, var2, var4.offset(1, var7, 0), this.trunk, var5);
               }

               if (isAirOrLeaves(var2, var4.offset(1, var7, 1))) {
                  this.setBlock(var1, var2, var4.offset(1, var7, 1), this.trunk, var5);
               }

               if (isAirOrLeaves(var2, var4.offset(0, var7, 1))) {
                  this.setBlock(var1, var2, var4.offset(0, var7, 1), this.trunk, var5);
               }
            }
         }

         this.postPlaceTree(var2, var3, var4);
         return true;
      }
   }

   private void createCrown(LevelSimulatedRW var1, int var2, int var3, int var4, int var5, Random var6, BoundingBox var7, Set<BlockPos> var8) {
      int var9 = var6.nextInt(5) + (this.isSpruce ? this.baseHeight : 3);
      int var10 = 0;

      for(int var11 = var4 - var9; var11 <= var4; ++var11) {
         int var12 = var4 - var11;
         int var13 = var5 + Mth.floor((float)var12 / (float)var9 * 3.5F);
         this.placeDoubleTrunkLeaves(var1, new BlockPos(var2, var11, var3), var13 + (var12 > 0 && var13 == var10 && (var11 & 1) == 0 ? 1 : 0), var7, var8);
         var10 = var13;
      }

   }

   public void postPlaceTree(LevelSimulatedRW var1, Random var2, BlockPos var3) {
      this.placePodzolCircle(var1, var3.west().north());
      this.placePodzolCircle(var1, var3.east(2).north());
      this.placePodzolCircle(var1, var3.west().south(2));
      this.placePodzolCircle(var1, var3.east(2).south(2));

      for(int var4 = 0; var4 < 5; ++var4) {
         int var5 = var2.nextInt(64);
         int var6 = var5 % 8;
         int var7 = var5 / 8;
         if (var6 == 0 || var6 == 7 || var7 == 0 || var7 == 7) {
            this.placePodzolCircle(var1, var3.offset(-3 + var6, 0, -3 + var7));
         }
      }

   }

   private void placePodzolCircle(LevelSimulatedRW var1, BlockPos var2) {
      for(int var3 = -2; var3 <= 2; ++var3) {
         for(int var4 = -2; var4 <= 2; ++var4) {
            if (Math.abs(var3) != 2 || Math.abs(var4) != 2) {
               this.placePodzolAt(var1, var2.offset(var3, 0, var4));
            }
         }
      }

   }

   private void placePodzolAt(LevelSimulatedRW var1, BlockPos var2) {
      for(int var3 = 2; var3 >= -3; --var3) {
         BlockPos var4 = var2.above(var3);
         if (isGrassOrDirt(var1, var4)) {
            this.setBlock(var1, var4, PODZOL);
            break;
         }

         if (!isAir(var1, var4) && var3 < 0) {
            break;
         }
      }

   }

   static {
      TRUNK = Blocks.SPRUCE_LOG.defaultBlockState();
      LEAF = Blocks.SPRUCE_LEAVES.defaultBlockState();
      PODZOL = Blocks.PODZOL.defaultBlockState();
   }
}
