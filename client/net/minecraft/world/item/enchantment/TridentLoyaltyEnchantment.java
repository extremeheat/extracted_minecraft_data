package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

public class TridentLoyaltyEnchantment extends Enchantment {
   public TridentLoyaltyEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, EnchantmentCategory.TRIDENT, var2);
   }

   public int getMinCost(int var1) {
      return 5 + var1 * 7;
   }

   public int getMaxCost(int var1) {
      return 50;
   }

   public int getMaxLevel() {
      return 3;
   }
}
