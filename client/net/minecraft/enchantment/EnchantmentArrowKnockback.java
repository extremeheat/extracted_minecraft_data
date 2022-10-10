package net.minecraft.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentArrowKnockback extends Enchantment {
   public EnchantmentArrowKnockback(Enchantment.Rarity var1, EntityEquipmentSlot... var2) {
      super(var1, EnumEnchantmentType.BOW, var2);
   }

   public int func_77321_a(int var1) {
      return 12 + (var1 - 1) * 20;
   }

   public int func_77317_b(int var1) {
      return this.func_77321_a(var1) + 25;
   }

   public int func_77325_b() {
      return 2;
   }
}
