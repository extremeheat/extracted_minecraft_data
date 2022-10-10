package net.minecraft.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentFireAspect extends Enchantment {
   protected EnchantmentFireAspect(Enchantment.Rarity var1, EntityEquipmentSlot... var2) {
      super(var1, EnumEnchantmentType.WEAPON, var2);
   }

   public int func_77321_a(int var1) {
      return 10 + 20 * (var1 - 1);
   }

   public int func_77317_b(int var1) {
      return super.func_77321_a(var1) + 50;
   }

   public int func_77325_b() {
      return 2;
   }
}
