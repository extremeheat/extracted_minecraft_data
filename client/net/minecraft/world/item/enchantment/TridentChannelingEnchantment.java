package net.minecraft.world.item.enchantment;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;

public class TridentChannelingEnchantment extends Enchantment {
   public TridentChannelingEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, ItemTags.TRIDENT_ENCHANTABLE, var2);
   }

   @Override
   public int getMinCost(int var1) {
      return 25;
   }

   @Override
   public int getMaxCost(int var1) {
      return 50;
   }
}
