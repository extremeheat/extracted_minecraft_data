package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public record BlockSource(ServerLevel level, BlockPos pos, BlockState state, DispenserBlockEntity blockEntity) implements DispenseSource {
   public BlockSource {
      super();
   }

   public Direction direction() {
      return (Direction)this.state.getValue(DispenserBlock.FACING);
   }

   public ItemStack insertItem(final ItemStack stack) {
      return this.blockEntity.insertItem(stack);
   }
}
