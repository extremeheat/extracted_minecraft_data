package net.minecraft.world.item.enchantment;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;

public class LootBonusEnchantment extends Enchantment {
   protected LootBonusEnchantment(Enchantment.Rarity var1, TagKey<Item> var2, EquipmentSlot... var3) {
      super(var1, var2, var3);
   }

   @Override
   public int getMinCost(int var1) {
      return 15 + (var1 - 1) * 9;
   }

   @Override
   public int getMaxCost(int var1) {
      return super.getMinCost(var1) + 50;
   }

   @Override
   public int getMaxLevel() {
      return 3;
   }

   @Override
   public boolean checkCompatibility(Enchantment var1) {
      return super.checkCompatibility(var1) && var1 != Enchantments.SILK_TOUCH;
   }
}
