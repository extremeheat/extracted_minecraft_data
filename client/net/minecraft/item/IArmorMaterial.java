package net.minecraft.item;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;

public interface IArmorMaterial {
   int func_200896_a(EntityEquipmentSlot var1);

   int func_200902_b(EntityEquipmentSlot var1);

   int func_200900_a();

   SoundEvent func_200899_b();

   Ingredient func_200898_c();

   String func_200897_d();

   float func_200901_e();
}
