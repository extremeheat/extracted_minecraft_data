package net.minecraft.world.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public interface ContainerSynchronizer {
   void sendInitialData(AbstractContainerMenu var1, NonNullList<ItemStack> var2, ItemStack var3, int[] var4);

   void sendSlotChange(AbstractContainerMenu var1, int var2, ItemStack var3);

   void sendCarriedChange(AbstractContainerMenu var1, ItemStack var2);

   void sendDataChange(AbstractContainerMenu var1, int var2, int var3);
}
