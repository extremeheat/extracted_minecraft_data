package net.minecraft.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentArrowFire extends Enchantment {
   public EnchantmentArrowFire(Enchantment.Rarity var1, EntityEquipmentSlot... var2) {
      super(var1, EnumEnchantmentType.BOW, var2);
   }

   public int func_77321_a(int var1) {
      return 20;
   }

   public int func_77317_b(int var1) {
      return 50;
   }

   public int func_77325_b() {
      return 1;
   }
}
