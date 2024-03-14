package net.minecraft.world.item.enchantment;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;

public class MendingEnchantment extends Enchantment {
   public MendingEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, ItemTags.DURABILITY_ENCHANTABLE, var2);
   }

   @Override
   public int getMinCost(int var1) {
      return var1 * 25;
   }

   @Override
   public int getMaxCost(int var1) {
      return this.getMinCost(var1) + 50;
   }

   @Override
   public boolean isTreasureOnly() {
      return true;
   }
}
