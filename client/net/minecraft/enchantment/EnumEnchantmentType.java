package net.minecraft.enchantment;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public enum EnumEnchantmentType {
   all,
   armor,
   armor_feet,
   armor_legs,
   armor_torso,
   armor_head,
   weapon,
   digger,
   fishing_rod,
   breakable,
   bow;

   private EnumEnchantmentType() {
   }

   public boolean func_77557_a(Item var1) {
      if (this == all) {
         return true;
      } else if (this == breakable && var1.func_77645_m()) {
         return true;
      } else if (var1 instanceof ItemArmor) {
         if (this == armor) {
            return true;
         } else {
            ItemArmor var2 = (ItemArmor)var1;
            if (var2.field_77881_a == 0) {
               return this == armor_head;
            } else if (var2.field_77881_a == 2) {
               return this == armor_legs;
            } else if (var2.field_77881_a == 1) {
               return this == armor_torso;
            } else if (var2.field_77881_a == 3) {
               return this == armor_feet;
            } else {
               return false;
            }
         }
      } else if (var1 instanceof ItemSword) {
         return this == weapon;
      } else if (var1 instanceof ItemTool) {
         return this == digger;
      } else if (var1 instanceof ItemBow) {
         return this == bow;
      } else if (var1 instanceof ItemFishingRod) {
         return this == fishing_rod;
      } else {
         return false;
      }
   }
}
