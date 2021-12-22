package net.minecraft.world.level.block;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public interface SimpleWaterloggedBlock extends BucketPickup, LiquidBlockContainer {
   default boolean canPlaceLiquid(BlockGetter var1, BlockPos var2, BlockState var3, Fluid var4) {
      return !(Boolean)var3.getValue(BlockStateProperties.WATERLOGGED) && var4 == Fluids.WATER;
   }

   default boolean placeLiquid(LevelAccessor var1, BlockPos var2, BlockState var3, FluidState var4) {
      if (!(Boolean)var3.getValue(BlockStateProperties.WATERLOGGED) && var4.getType() == Fluids.WATER) {
         if (!var1.isClientSide()) {
            var1.setBlock(var2, (BlockState)var3.setValue(BlockStateProperties.WATERLOGGED, true), 3);
            var1.scheduleTick(var2, var4.getType(), var4.getType().getTickDelay(var1));
         }

         return true;
      } else {
         return false;
      }
   }

   default ItemStack pickupBlock(LevelAccessor var1, BlockPos var2, BlockState var3) {
      if ((Boolean)var3.getValue(BlockStateProperties.WATERLOGGED)) {
         var1.setBlock(var2, (BlockState)var3.setValue(BlockStateProperties.WATERLOGGED, false), 3);
         if (!var3.canSurvive(var1, var2)) {
            var1.destroyBlock(var2, true);
         }

         return new ItemStack(Items.WATER_BUCKET);
      } else {
         return ItemStack.EMPTY;
      }
   }

   default Optional<SoundEvent> getPickupSound() {
      return Fluids.WATER.getPickupSound();
   }
}
