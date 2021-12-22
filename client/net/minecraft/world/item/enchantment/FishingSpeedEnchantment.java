package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

public class FishingSpeedEnchantment extends Enchantment {
   protected FishingSpeedEnchantment(Enchantment.Rarity var1, EnchantmentCategory var2, EquipmentSlot... var3) {
      super(var1, var2, var3);
   }

   public int getMinCost(int var1) {
      return 15 + (var1 - 1) * 9;
   }

   public int getMaxCost(int var1) {
      return super.getMinCost(var1) + 50;
   }

   public int getMaxLevel() {
      return 3;
   }
}
