package net.minecraft.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentArrowDamage extends Enchantment {
   public EnchantmentArrowDamage(Enchantment.Rarity var1, EntityEquipmentSlot... var2) {
      super(var1, EnumEnchantmentType.BOW, var2);
   }

   public int func_77321_a(int var1) {
      return 1 + (var1 - 1) * 10;
   }

   public int func_77317_b(int var1) {
      return this.func_77321_a(var1) + 15;
   }

   public int func_77325_b() {
      return 5;
   }
}
