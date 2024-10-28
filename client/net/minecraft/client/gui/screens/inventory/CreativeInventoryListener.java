package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;

public class CreativeInventoryListener implements ContainerListener {
   private final Minecraft minecraft;

   public CreativeInventoryListener(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void slotChanged(AbstractContainerMenu var1, int var2, ItemStack var3) {
      this.minecraft.gameMode.handleCreativeModeItemAdd(var3, var2);
   }

   public void dataChanged(AbstractContainerMenu var1, int var2, int var3) {
   }
}
