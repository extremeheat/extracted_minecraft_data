package net.minecraft.world.level.block.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

public class BlastFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
   public BlastFurnaceBlockEntity() {
      super(BlockEntityType.BLAST_FURNACE, RecipeType.BLASTING);
   }

   protected Component getDefaultName() {
      return new TranslatableComponent("container.blast_furnace", new Object[0]);
   }

   protected int getBurnDuration(ItemStack var1) {
      return super.getBurnDuration(var1) / 2;
   }

   protected AbstractContainerMenu createMenu(int var1, Inventory var2) {
      return new BlastFurnaceMenu(var1, var2, this, this.dataAccess);
   }
}
