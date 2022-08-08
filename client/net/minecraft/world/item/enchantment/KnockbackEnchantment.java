package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

public class KnockbackEnchantment extends Enchantment {
   protected KnockbackEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, EnchantmentCategory.WEAPON, var2);
   }

   public int getMinCost(int var1) {
      return 5 + 20 * (var1 - 1);
   }

   public int getMaxCost(int var1) {
      return super.getMinCost(var1) + 50;
   }

   public int getMaxLevel() {
      return 2;
   }
}
