package net.minecraft.world.level.material;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public abstract class WaterFluid extends FlowingFluid {
   public WaterFluid() {
      super();
   }

   public Fluid getFlowing() {
      return Fluids.FLOWING_WATER;
   }

   public Fluid getSource() {
      return Fluids.WATER;
   }

   public Item getBucket() {
      return Items.WATER_BUCKET;
   }

   protected boolean canConvertToSource() {
      return true;
   }

   protected void beforeDestroyingBlock(LevelAccessor var1, BlockPos var2, BlockState var3) {
      BlockEntity var4 = var3.getBlock().isEntityBlock() ? var1.getBlockEntity(var2) : null;
      Block.dropResources(var3, var1, var2, var4);
   }

   public int getSlopeFindDistance(LevelReader var1) {
      return 4;
   }

   public BlockState createLegacyBlock(FluidState var1) {
      return (BlockState)Blocks.WATER.defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(var1));
   }

   public boolean isSame(Fluid var1) {
      return var1 == Fluids.WATER || var1 == Fluids.FLOWING_WATER;
   }

   public int getDropOff(LevelReader var1) {
      return 1;
   }

   public int getTickDelay(LevelReader var1) {
      return 5;
   }

   public boolean canBeReplacedWith(FluidState var1, BlockGetter var2, BlockPos var3, Fluid var4, Direction var5) {
      return var5 == Direction.DOWN && !var4.is(FluidTags.WATER);
   }

   protected float getExplosionResistance() {
      return 100.0F;
   }

   public static class Flowing extends WaterFluid {
      public Flowing() {
         super();
      }

      protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> var1) {
         super.createFluidStateDefinition(var1);
         var1.add(LEVEL);
      }

      public int getAmount(FluidState var1) {
         return (Integer)var1.getValue(LEVEL);
      }

      public boolean isSource(FluidState var1) {
         return false;
      }
   }

   public static class Source extends WaterFluid {
      public Source() {
         super();
      }

      public int getAmount(FluidState var1) {
         return 8;
      }

      public boolean isSource(FluidState var1) {
         return true;
      }
   }
}
