package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

public class SoulSpeedEnchantment extends Enchantment {
   public SoulSpeedEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, EnchantmentCategory.ARMOR_FEET, var2);
   }

   @Override
   public int getMinCost(int var1) {
      return var1 * 10;
   }

   @Override
   public int getMaxCost(int var1) {
      return this.getMinCost(var1) + 15;
   }

   @Override
   public boolean isTreasureOnly() {
      return true;
   }

   @Override
   public boolean isTradeable() {
      return false;
   }

   @Override
   public boolean isDiscoverable() {
      return false;
   }

   @Override
   public int getMaxLevel() {
      return 3;
   }
}
