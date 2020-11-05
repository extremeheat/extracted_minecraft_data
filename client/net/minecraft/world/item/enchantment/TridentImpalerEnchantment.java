package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;

public class TridentImpalerEnchantment extends Enchantment {
   public TridentImpalerEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, EnchantmentCategory.TRIDENT, var2);
   }

   public int getMinCost(int var1) {
      return 1 + (var1 - 1) * 8;
   }

   public int getMaxCost(int var1) {
      return this.getMinCost(var1) + 20;
   }

   public int getMaxLevel() {
      return 5;
   }

   public float getDamageBonus(int var1, MobType var2) {
      return var2 == MobType.WATER ? (float)var1 * 2.5F : 0.0F;
   }
}
