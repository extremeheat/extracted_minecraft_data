package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

public class ArrowInfiniteEnchantment extends Enchantment {
   public ArrowInfiniteEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, EnchantmentCategory.BOW, var2);
   }

   public int getMinCost(int var1) {
      return 20;
   }

   public int getMaxCost(int var1) {
      return 50;
   }

   public int getMaxLevel() {
      return 1;
   }

   public boolean checkCompatibility(Enchantment var1) {
      return var1 instanceof MendingEnchantment ? false : super.checkCompatibility(var1);
   }
}
