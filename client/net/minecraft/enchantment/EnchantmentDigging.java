package net.minecraft.enchantment;

import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentDigging extends Enchantment {
   protected EnchantmentDigging(Enchantment.Rarity var1, EntityEquipmentSlot... var2) {
      super(var1, EnumEnchantmentType.DIGGER, var2);
   }

   public int func_77321_a(int var1) {
      return 1 + 10 * (var1 - 1);
   }

   public int func_77317_b(int var1) {
      return super.func_77321_a(var1) + 50;
   }

   public int func_77325_b() {
      return 5;
   }

   public boolean func_92089_a(ItemStack var1) {
      return var1.func_77973_b() == Items.field_151097_aZ ? true : super.func_92089_a(var1);
   }
}
