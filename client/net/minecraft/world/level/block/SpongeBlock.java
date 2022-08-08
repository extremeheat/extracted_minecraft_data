package net.minecraft.world.level.block;

import com.google.common.collect.Lists;
import java.util.LinkedList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;

public class SpongeBlock extends Block {
   public static final int MAX_DEPTH = 6;
   public static final int MAX_COUNT = 64;

   protected SpongeBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var4.is(var1.getBlock())) {
         this.tryAbsorbWater(var2, var3);
      }
   }

   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      this.tryAbsorbWater(var2, var3);
      super.neighborChanged(var1, var2, var3, var4, var5, var6);
   }

   protected void tryAbsorbWater(Level var1, BlockPos var2) {
      if (this.removeWaterBreadthFirstSearch(var1, var2)) {
         var1.setBlock(var2, Blocks.WET_SPONGE.defaultBlockState(), 2);
         var1.levelEvent(2001, var2, Block.getId(Blocks.WATER.defaultBlockState()));
      }

   }

   private boolean removeWaterBreadthFirstSearch(Level var1, BlockPos var2) {
      LinkedList var3 = Lists.newLinkedList();
      var3.add(new Tuple(var2, 0));
      int var4 = 0;

      while(!var3.isEmpty()) {
         Tuple var5 = (Tuple)var3.poll();
         BlockPos var6 = (BlockPos)var5.getA();
         int var7 = (Integer)var5.getB();
         Direction[] var8 = Direction.values();
         int var9 = var8.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            Direction var11 = var8[var10];
            BlockPos var12 = var6.relative(var11);
            BlockState var13 = var1.getBlockState(var12);
            FluidState var14 = var1.getFluidState(var12);
            Material var15 = var13.getMaterial();
            if (var14.is(FluidTags.WATER)) {
               if (var13.getBlock() instanceof BucketPickup && !((BucketPickup)var13.getBlock()).pickupBlock(var1, var12, var13).isEmpty()) {
                  ++var4;
                  if (var7 < 6) {
                     var3.add(new Tuple(var12, var7 + 1));
                  }
               } else if (var13.getBlock() instanceof LiquidBlock) {
                  var1.setBlock(var12, Blocks.AIR.defaultBlockState(), 3);
                  ++var4;
                  if (var7 < 6) {
                     var3.add(new Tuple(var12, var7 + 1));
                  }
               } else if (var15 == Material.WATER_PLANT || var15 == Material.REPLACEABLE_WATER_PLANT) {
                  BlockEntity var16 = var13.hasBlockEntity() ? var1.getBlockEntity(var12) : null;
                  dropResources(var13, var1, var12, var16);
                  var1.setBlock(var12, Blocks.AIR.defaultBlockState(), 3);
                  ++var4;
                  if (var7 < 6) {
                     var3.add(new Tuple(var12, var7 + 1));
                  }
               }
            }
         }

         if (var4 > 64) {
            break;
         }
      }

      return var4 > 0;
   }
}
