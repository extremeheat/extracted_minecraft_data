package net.minecraft.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentKnockback extends Enchantment {
   protected EnchantmentKnockback(Enchantment.Rarity var1, EntityEquipmentSlot... var2) {
      super(var1, EnumEnchantmentType.WEAPON, var2);
   }

   public int func_77321_a(int var1) {
      return 5 + 20 * (var1 - 1);
   }

   public int func_77317_b(int var1) {
      return super.func_77321_a(var1) + 50;
   }

   public int func_77325_b() {
      return 2;
   }
}
