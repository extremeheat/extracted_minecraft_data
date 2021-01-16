package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

public class ArrowKnockbackEnchantment extends Enchantment {
   public ArrowKnockbackEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, EnchantmentCategory.BOW, var2);
   }

   public int getMinCost(int var1) {
      return 12 + (var1 - 1) * 20;
   }

   public int getMaxCost(int var1) {
      return this.getMinCost(var1) + 25;
   }

   public int getMaxLevel() {
      return 2;
   }
}
