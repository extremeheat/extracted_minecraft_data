package net.minecraft.enchantment;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public enum EnumEnchantmentType {
   ALL,
   ARMOR,
   ARMOR_FEET,
   ARMOR_LEGS,
   ARMOR_TORSO,
   ARMOR_HEAD,
   WEAPON,
   DIGGER,
   FISHING_ROD,
   BREAKABLE,
   BOW;

   private EnumEnchantmentType() {
   }

   public boolean func_77557_a(Item var1) {
      if (this == ALL) {
         return true;
      } else if (this == BREAKABLE && var1.func_77645_m()) {
         return true;
      } else if (var1 instanceof ItemArmor) {
         if (this == ARMOR) {
            return true;
         } else {
            ItemArmor var2 = (ItemArmor)var1;
            if (var2.field_77881_a == 0) {
               return this == ARMOR_HEAD;
            } else if (var2.field_77881_a == 2) {
               return this == ARMOR_LEGS;
            } else if (var2.field_77881_a == 1) {
               return this == ARMOR_TORSO;
            } else if (var2.field_77881_a == 3) {
               return this == ARMOR_FEET;
            } else {
               return false;
            }
         }
      } else if (var1 instanceof ItemSword) {
         return this == WEAPON;
      } else if (var1 instanceof ItemTool) {
         return this == DIGGER;
      } else if (var1 instanceof ItemBow) {
         return this == BOW;
      } else if (var1 instanceof ItemFishingRod) {
         return this == FISHING_ROD;
      } else {
         return false;
      }
   }
}
