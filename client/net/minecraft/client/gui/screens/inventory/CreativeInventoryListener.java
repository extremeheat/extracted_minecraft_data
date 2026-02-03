package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;

public class CreativeInventoryListener implements ContainerListener {
   private final Minecraft minecraft;

   public CreativeInventoryListener(final Minecraft minecraft) {
      super();
      this.minecraft = minecraft;
   }

   public void slotChanged(final AbstractContainerMenu container, final int slotIndex, final ItemStack itemStack) {
      this.minecraft.gameMode.handleCreativeModeItemAdd(itemStack, slotIndex);
   }

   public void dataChanged(final AbstractContainerMenu container, final int id, final int value) {
   }
}
