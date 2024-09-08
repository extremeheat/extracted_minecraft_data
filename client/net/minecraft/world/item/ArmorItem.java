package net.minecraft.world.item;

import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

public class ArmorItem extends Item {
   public ArmorItem(ArmorMaterial var1, ArmorType var2, Item.Properties var3) {
      super(var1.humanoidProperties(var3, var2));
   }
}
