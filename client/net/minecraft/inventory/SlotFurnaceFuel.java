package net.minecraft.inventory;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class SlotFurnaceFuel extends Slot {
   public SlotFurnaceFuel(IInventory var1, int var2, int var3, int var4) {
      super(var1, var2, var3, var4);
   }

   public boolean func_75214_a(ItemStack var1) {
      return TileEntityFurnace.func_145954_b(var1) || func_178173_c_(var1);
   }

   public int func_178170_b(ItemStack var1) {
      return func_178173_c_(var1) ? 1 : super.func_178170_b(var1);
   }

   public static boolean func_178173_c_(ItemStack var0) {
      return var0.func_77973_b() == Items.field_151133_ar;
   }
}
