package net.minecraft.world.item.enchantment;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;

public class KnockbackEnchantment extends Enchantment {
   protected KnockbackEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, ItemTags.SWORD_ENCHANTABLE, var2);
   }

   @Override
   public int getMinCost(int var1) {
      return 5 + 20 * (var1 - 1);
   }

   @Override
   public int getMaxCost(int var1) {
      return super.getMinCost(var1) + 50;
   }

   @Override
   public int getMaxLevel() {
      return 2;
   }
}
