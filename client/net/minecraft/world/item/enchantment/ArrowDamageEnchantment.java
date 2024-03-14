package net.minecraft.world.item.enchantment;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;

public class ArrowDamageEnchantment extends Enchantment {
   public ArrowDamageEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, ItemTags.BOW_ENCHANTABLE, var2);
   }

   @Override
   public int getMinCost(int var1) {
      return 1 + (var1 - 1) * 10;
   }

   @Override
   public int getMaxCost(int var1) {
      return this.getMinCost(var1) + 15;
   }

   @Override
   public int getMaxLevel() {
      return 5;
   }
}
