package net.minecraft.world.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.crafting.Ingredient;

public interface ArmorMaterial {
   int getDurabilityForType(ArmorItem.Type var1);

   int getDefenseForType(ArmorItem.Type var1);

   int getEnchantmentValue();

   SoundEvent getEquipSound();

   Ingredient getRepairIngredient();

   String getName();

   float getToughness();

   float getKnockbackResistance();
}
