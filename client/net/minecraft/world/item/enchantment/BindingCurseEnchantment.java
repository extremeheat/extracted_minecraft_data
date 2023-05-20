package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BindingCurseEnchantment extends Enchantment {
   public BindingCurseEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, EnchantmentCategory.WEARABLE, var2);
   }

   @Override
   public int getMinCost(int var1) {
      return 25;
   }

   @Override
   public int getMaxCost(int var1) {
      return 50;
   }

   @Override
   public boolean isTreasureOnly() {
      return true;
   }

   @Override
   public boolean isCurse() {
      return true;
   }

   @Override
   public boolean canEnchant(ItemStack var1) {
      return !var1.is(Items.SHIELD) && super.canEnchant(var1);
   }
}
