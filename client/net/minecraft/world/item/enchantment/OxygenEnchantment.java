package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

public class OxygenEnchantment extends Enchantment {
   public OxygenEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, EnchantmentCategory.ARMOR_HEAD, var2);
   }

   public int getMinCost(int var1) {
      return 10 * var1;
   }

   public int getMaxCost(int var1) {
      return this.getMinCost(var1) + 30;
   }

   public int getMaxLevel() {
      return 3;
   }
}
