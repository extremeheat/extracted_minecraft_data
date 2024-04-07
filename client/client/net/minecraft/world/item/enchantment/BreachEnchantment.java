package net.minecraft.world.item.enchantment;

import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;

public class BreachEnchantment extends Enchantment {
   public BreachEnchantment() {
      super(
         Enchantment.definition(
            ItemTags.MACE_ENCHANTABLE,
            2,
            4,
            Enchantment.dynamicCost(15, 9),
            Enchantment.dynamicCost(65, 9),
            4,
            FeatureFlagSet.of(FeatureFlags.UPDATE_1_21),
            EquipmentSlot.MAINHAND
         )
      );
   }

   public static float calculateArmorBreach(float var0, float var1) {
      return Mth.clamp(var1 - 0.15F * var0, 0.0F, 1.0F);
   }
}
