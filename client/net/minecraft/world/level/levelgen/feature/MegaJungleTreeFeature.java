package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class MegaJungleTreeFeature extends MegaTreeFeature<NoneFeatureConfiguration> {
   public MegaJungleTreeFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1, boolean var2, int var3, int var4, BlockState var5, BlockState var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public boolean doPlace(Set<BlockPos> var1, LevelSimulatedRW var2, Random var3, BlockPos var4, BoundingBox var5) {
      int var6 = this.calcTreeHeigth(var3);
      if (!this.prepareTree(var2, var4, var6)) {
         return false;
      } else {
         this.createCrown(var2, var4.above(var6), 2, var5, var1);

         for(int var7 = var4.getY() + var6 - 2 - var3.nextInt(4); var7 > var4.getY() + var6 / 2; var7 -= 2 + var3.nextInt(4)) {
            float var8 = var3.nextFloat() * 6.2831855F;
            int var9 = var4.getX() + (int)(0.5F + Mth.cos(var8) * 4.0F);
            int var10 = var4.getZ() + (int)(0.5F + Mth.sin(var8) * 4.0F);

            int var11;
            for(var11 = 0; var11 < 5; ++var11) {
               var9 = var4.getX() + (int)(1.5F + Mth.cos(var8) * (float)var11);
               var10 = var4.getZ() + (int)(1.5F + Mth.sin(var8) * (float)var11);
               this.setBlock(var1, var2, new BlockPos(var9, var7 - 3 + var11 / 2, var10), this.trunk, var5);
            }

            var11 = 1 + var3.nextInt(2);
            int var12 = var7;

            for(int var13 = var7 - var11; var13 <= var12; ++var13) {
               int var14 = var13 - var12;
               this.placeSingleTrunkLeaves(var2, new BlockPos(var9, var13, var10), 1 - var14, var5, var1);
            }
         }

         for(int var15 = 0; var15 < var6; ++var15) {
            BlockPos var16 = var4.above(var15);
            if (isFree(var2, var16)) {
               this.setBlock(var1, var2, var16, this.trunk, var5);
               if (var15 > 0) {
                  this.placeVine(var2, var3, var16.west(), VineBlock.EAST);
                  this.placeVine(var2, var3, var16.north(), VineBlock.SOUTH);
               }
            }

            if (var15 < var6 - 1) {
               BlockPos var17 = var16.east();
               if (isFree(var2, var17)) {
                  this.setBlock(var1, var2, var17, this.trunk, var5);
                  if (var15 > 0) {
                     this.placeVine(var2, var3, var17.east(), VineBlock.WEST);
                     this.placeVine(var2, var3, var17.north(), VineBlock.SOUTH);
                  }
               }

               BlockPos var18 = var16.south().east();
               if (isFree(var2, var18)) {
                  this.setBlock(var1, var2, var18, this.trunk, var5);
                  if (var15 > 0) {
                     this.placeVine(var2, var3, var18.east(), VineBlock.WEST);
                     this.placeVine(var2, var3, var18.south(), VineBlock.NORTH);
                  }
               }

               BlockPos var19 = var16.south();
               if (isFree(var2, var19)) {
                  this.setBlock(var1, var2, var19, this.trunk, var5);
                  if (var15 > 0) {
                     this.placeVine(var2, var3, var19.west(), VineBlock.EAST);
                     this.placeVine(var2, var3, var19.south(), VineBlock.NORTH);
                  }
               }
            }
         }

         return true;
      }
   }

   private void placeVine(LevelSimulatedRW var1, Random var2, BlockPos var3, BooleanProperty var4) {
      if (var2.nextInt(3) > 0 && isAir(var1, var3)) {
         this.setBlock(var1, var3, (BlockState)Blocks.VINE.defaultBlockState().setValue(var4, true));
      }

   }

   private void createCrown(LevelSimulatedRW var1, BlockPos var2, int var3, BoundingBox var4, Set<BlockPos> var5) {
      boolean var6 = true;

      for(int var7 = -2; var7 <= 0; ++var7) {
         this.placeDoubleTrunkLeaves(var1, var2.above(var7), var3 + 1 - var7, var4, var5);
      }

   }
}
