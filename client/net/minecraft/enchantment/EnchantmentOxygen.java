package net.minecraft.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentOxygen extends Enchantment {
   public EnchantmentOxygen(Enchantment.Rarity var1, EntityEquipmentSlot... var2) {
      super(var1, EnumEnchantmentType.ARMOR_HEAD, var2);
   }

   public int func_77321_a(int var1) {
      return 10 * var1;
   }

   public int func_77317_b(int var1) {
      return this.func_77321_a(var1) + 30;
   }

   public int func_77325_b() {
      return 3;
   }
}
