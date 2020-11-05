package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

public class ArrowDamageEnchantment extends Enchantment {
   public ArrowDamageEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, EnchantmentCategory.BOW, var2);
   }

   public int getMinCost(int var1) {
      return 1 + (var1 - 1) * 10;
   }

   public int getMaxCost(int var1) {
      return this.getMinCost(var1) + 15;
   }

   public int getMaxLevel() {
      return 5;
   }
}
