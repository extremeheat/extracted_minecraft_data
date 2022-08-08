package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;

public class FurnaceBlockEntity extends AbstractFurnaceBlockEntity {
   public FurnaceBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.FURNACE, var1, var2, RecipeType.SMELTING);
   }

   protected Component getDefaultName() {
      return Component.translatable("container.furnace");
   }

   protected AbstractContainerMenu createMenu(int var1, Inventory var2) {
      return new FurnaceMenu(var1, var2, this, this.dataAccess);
   }
}
