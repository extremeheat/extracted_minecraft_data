package net.minecraft.world.item.enchantment;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;

public class SwiftSneakEnchantment extends Enchantment {
   public SwiftSneakEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, ItemTags.LEG_ARMOR_ENCHANTABLE, var2);
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

   @Override
   public boolean isTradeable() {
      return false;
   }

   @Override
   public boolean isDiscoverable() {
      return false;
   }

   @Override
   public int getMaxLevel() {
      return 3;
   }
}
