package net.minecraft.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class CreativeCrafting implements IContainerListener {
   private final Minecraft field_146109_a;

   public CreativeCrafting(Minecraft var1) {
      super();
      this.field_146109_a = var1;
   }

   public void func_71110_a(Container var1, NonNullList<ItemStack> var2) {
   }

   public void func_71111_a(Container var1, int var2, ItemStack var3) {
      this.field_146109_a.field_71442_b.func_78761_a(var3, var2);
   }

   public void func_71112_a(Container var1, int var2, int var3) {
   }

   public void func_175173_a(Container var1, IInventory var2) {
   }
}
