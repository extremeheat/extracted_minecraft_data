package net.minecraft.world.item.enchantment;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;

public class TridentLoyaltyEnchantment extends Enchantment {
   public TridentLoyaltyEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, ItemTags.TRIDENT_ENCHANTABLE, var2);
   }

   @Override
   public int getMinCost(int var1) {
      return 5 + var1 * 7;
   }

   @Override
   public int getMaxCost(int var1) {
      return 50;
   }

   @Override
   public int getMaxLevel() {
      return 3;
   }
}
