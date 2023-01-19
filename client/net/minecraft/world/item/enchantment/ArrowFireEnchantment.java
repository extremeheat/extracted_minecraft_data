package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

public class ArrowFireEnchantment extends Enchantment {
   public ArrowFireEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, EnchantmentCategory.BOW, var2);
   }

   @Override
   public int getMinCost(int var1) {
      return 20;
   }

   @Override
   public int getMaxCost(int var1) {
      return 50;
   }

   @Override
   public int getMaxLevel() {
      return 1;
   }
}
