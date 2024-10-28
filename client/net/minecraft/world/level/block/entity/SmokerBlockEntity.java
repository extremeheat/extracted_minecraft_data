package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SmokerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;

public class SmokerBlockEntity extends AbstractFurnaceBlockEntity {
   public SmokerBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.SMOKER, var1, var2, RecipeType.SMOKING);
   }

   protected Component getDefaultName() {
      return Component.translatable("container.smoker");
   }

   protected int getBurnDuration(ItemStack var1) {
      return super.getBurnDuration(var1) / 2;
   }

   protected AbstractContainerMenu createMenu(int var1, Inventory var2) {
      return new SmokerMenu(var1, var2, this, this.dataAccess);
   }
}
