package net.minecraft.world.level.block.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SmokerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

public class SmokerBlockEntity extends AbstractFurnaceBlockEntity {
   public SmokerBlockEntity() {
      super(BlockEntityType.SMOKER, RecipeType.SMOKING);
   }

   protected Component getDefaultName() {
      return new TranslatableComponent("container.smoker", new Object[0]);
   }

   protected int getBurnDuration(ItemStack var1) {
      return super.getBurnDuration(var1) / 2;
   }

   protected AbstractContainerMenu createMenu(int var1, Inventory var2) {
      return new SmokerMenu(var1, var2, this, this.dataAccess);
   }
}
