package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class DiggingEnchantment extends Enchantment {
   protected DiggingEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, EnchantmentCategory.DIGGER, var2);
   }

   public int getMinCost(int var1) {
      return 1 + 10 * (var1 - 1);
   }

   public int getMaxCost(int var1) {
      return super.getMinCost(var1) + 50;
   }

   public int getMaxLevel() {
      return 5;
   }

   public boolean canEnchant(ItemStack var1) {
      return var1.is(Items.SHEARS) ? true : super.canEnchant(var1);
   }
}
