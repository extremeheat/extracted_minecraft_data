package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

public class MendingEnchantment extends Enchantment {
   public MendingEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, EnchantmentCategory.BREAKABLE, var2);
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

   public int getMaxLevel() {
      return 1;
   }
}
