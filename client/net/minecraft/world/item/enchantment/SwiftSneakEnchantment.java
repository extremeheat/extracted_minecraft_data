package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

public class SwiftSneakEnchantment extends Enchantment {
   public SwiftSneakEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, EnchantmentCategory.ARMOR_LEGS, var2);
   }

   public int getMinCost(int var1) {
      return var1 * 25;
   }

   public int getMaxCost(int var1) {
      return this.getMinCost(var1) + 50;
   }

   public boolean isTreasureOnly() {
      return true;
   }

   public boolean isTradeable() {
      return false;
   }

   public boolean isDiscoverable() {
      return false;
   }

   public int getMaxLevel() {
      return 3;
   }
}
