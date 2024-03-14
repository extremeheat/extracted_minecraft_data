package net.minecraft.world.item.enchantment;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;

public class MultiShotEnchantment extends Enchantment {
   public MultiShotEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, ItemTags.CROSSBOW_ENCHANTABLE, var2);
   }

   @Override
   public int getMinCost(int var1) {
      return 20;
   }

   @Override
   public int getMaxCost(int var1) {
      return 50;
   }

   @Override
   public boolean checkCompatibility(Enchantment var1) {
      return super.checkCompatibility(var1) && var1 != Enchantments.PIERCING;
   }
}
