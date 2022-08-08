package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;

public class BlastFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
   public BlastFurnaceBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.BLAST_FURNACE, var1, var2, RecipeType.BLASTING);
   }

   protected Component getDefaultName() {
      return Component.translatable("container.blast_furnace");
   }

   protected int getBurnDuration(ItemStack var1) {
      return super.getBurnDuration(var1) / 2;
   }

   protected AbstractContainerMenu createMenu(int var1, Inventory var2) {
      return new BlastFurnaceMenu(var1, var2, this, this.dataAccess);
   }
}
