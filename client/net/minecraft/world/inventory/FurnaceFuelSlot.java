package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class FurnaceFuelSlot extends Slot {
   private final AbstractFurnaceMenu menu;

   public FurnaceFuelSlot(AbstractFurnaceMenu var1, Container var2, int var3, int var4, int var5) {
      super(var2, var3, var4, var5);
      this.menu = var1;
   }

   public boolean mayPlace(ItemStack var1) {
      return this.menu.isFuel(var1) || isBucket(var1);
   }

   public int getMaxStackSize(ItemStack var1) {
      return isBucket(var1) ? 1 : super.getMaxStackSize(var1);
   }

   public static boolean isBucket(ItemStack var0) {
      return var0.is(Items.BUCKET);
   }
}
