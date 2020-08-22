package net.minecraft.world.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public interface ContainerListener {
   void refreshContainer(AbstractContainerMenu var1, NonNullList var2);

   void slotChanged(AbstractContainerMenu var1, int var2, ItemStack var3);

   void setContainerData(AbstractContainerMenu var1, int var2, int var3);
}
