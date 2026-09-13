package net.minecraft.inventory;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

class ContainerPlayer$1 extends Slot {
   ContainerPlayer$1(ContainerPlayer var1, IInventory var2, int var3, int var4, int var5, int var6) {
      super(var2, var3, var4, var5);
      this.field_75235_b = var1;
      this.field_75236_a = var6;
   }

   @Override
   public int func_75219_a() {
      return 1;
   }

   @Override
   public boolean func_75214_a(ItemStack var1) {
      if (var1 == null) {
         return false;
      } else if (var1.func_77973_b() instanceof ItemArmor) {
         return ((ItemArmor)var1.func_77973_b()).field_77881_a == this.field_75236_a;
      } else if (var1.func_77973_b() != Item.func_150898_a(Blocks.field_150423_aK) && var1.func_77973_b() != Items.field_151144_bL) {
         return false;
      } else {
         return this.field_75236_a == 0;
      }
   }

   @Override
   public IIcon func_75212_b() {
      return ItemArmor.func_94602_b(this.field_75236_a);
   }
}
