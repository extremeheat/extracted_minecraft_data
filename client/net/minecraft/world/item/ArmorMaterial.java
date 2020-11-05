package net.minecraft.world.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.crafting.Ingredient;

public interface ArmorMaterial {
   int getDurabilityForSlot(EquipmentSlot var1);

   int getDefenseForSlot(EquipmentSlot var1);

   int getEnchantmentValue();

   SoundEvent getEquipSound();

   Ingredient getRepairIngredient();

   String getName();

   float getToughness();

   float getKnockbackResistance();
}
