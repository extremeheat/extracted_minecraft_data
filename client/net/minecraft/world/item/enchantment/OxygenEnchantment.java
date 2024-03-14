package net.minecraft.world.item.enchantment;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;

public class OxygenEnchantment extends Enchantment {
   public OxygenEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, ItemTags.HEAD_ARMOR_ENCHANTABLE, var2);
   }

   @Override
   public int getMinCost(int var1) {
      return 10 * var1;
   }

   @Override
   public int getMaxCost(int var1) {
      return this.getMinCost(var1) + 30;
   }

   @Override
   public int getMaxLevel() {
      return 3;
   }
}
