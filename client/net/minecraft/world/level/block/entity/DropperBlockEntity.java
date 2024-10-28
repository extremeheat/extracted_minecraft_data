package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class DropperBlockEntity extends DispenserBlockEntity {
   public DropperBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.DROPPER, var1, var2);
   }

   protected Component getDefaultName() {
      return Component.translatable("container.dropper");
   }
}
