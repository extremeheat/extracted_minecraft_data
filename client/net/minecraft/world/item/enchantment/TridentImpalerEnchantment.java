package net.minecraft.world.item.enchantment;

import javax.annotation.Nullable;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;

public class TridentImpalerEnchantment extends Enchantment {
   public TridentImpalerEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, ItemTags.TRIDENT_ENCHANTABLE, var2);
   }

   @Override
   public int getMinCost(int var1) {
      return 1 + (var1 - 1) * 8;
   }

   @Override
   public int getMaxCost(int var1) {
      return this.getMinCost(var1) + 20;
   }

   @Override
   public int getMaxLevel() {
      return 5;
   }

   @Override
   public float getDamageBonus(int var1, @Nullable EntityType<?> var2) {
      return var2 != null && var2.is(EntityTypeTags.AQUATIC) ? (float)var1 * 2.5F : 0.0F;
   }
}
