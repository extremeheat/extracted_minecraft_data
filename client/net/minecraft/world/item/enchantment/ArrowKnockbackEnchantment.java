package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

public class ArrowKnockbackEnchantment extends Enchantment {
   public ArrowKnockbackEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, EnchantmentCategory.BOW, var2);
   }

   @Override
   public int getMinCost(int var1) {
      return 12 + (var1 - 1) * 20;
   }

   @Override
   public int getMaxCost(int var1) {
      return this.getMinCost(var1) + 25;
   }

   @Override
   public int getMaxLevel() {
      return 2;
   }
}
