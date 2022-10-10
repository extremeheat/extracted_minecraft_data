package net.minecraft.enchantment;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAbstractSkull;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.ItemTrident;

public enum EnumEnchantmentType {
   ALL {
      public boolean func_77557_a(Item var1) {
         EnumEnchantmentType[] var2 = EnumEnchantmentType.values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            EnumEnchantmentType var5 = var2[var4];
            if (var5 != EnumEnchantmentType.ALL && var5.func_77557_a(var1)) {
               return true;
            }
         }

         return false;
      }
   },
   ARMOR {
      public boolean func_77557_a(Item var1) {
         return var1 instanceof ItemArmor;
      }
   },
   ARMOR_FEET {
      public boolean func_77557_a(Item var1) {
         return var1 instanceof ItemArmor && ((ItemArmor)var1).func_185083_B_() == EntityEquipmentSlot.FEET;
      }
   },
   ARMOR_LEGS {
      public boolean func_77557_a(Item var1) {
         return var1 instanceof ItemArmor && ((ItemArmor)var1).func_185083_B_() == EntityEquipmentSlot.LEGS;
      }
   },
   ARMOR_CHEST {
      public boolean func_77557_a(Item var1) {
         return var1 instanceof ItemArmor && ((ItemArmor)var1).func_185083_B_() == EntityEquipmentSlot.CHEST;
      }
   },
   ARMOR_HEAD {
      public boolean func_77557_a(Item var1) {
         return var1 instanceof ItemArmor && ((ItemArmor)var1).func_185083_B_() == EntityEquipmentSlot.HEAD;
      }
   },
   WEAPON {
      public boolean func_77557_a(Item var1) {
         return var1 instanceof ItemSword;
      }
   },
   DIGGER {
      public boolean func_77557_a(Item var1) {
         return var1 instanceof ItemTool;
      }
   },
   FISHING_ROD {
      public boolean func_77557_a(Item var1) {
         return var1 instanceof ItemFishingRod;
      }
   },
   TRIDENT {
      public boolean func_77557_a(Item var1) {
         return var1 instanceof ItemTrident;
      }
   },
   BREAKABLE {
      public boolean func_77557_a(Item var1) {
         return var1.func_77645_m();
      }
   },
   BOW {
      public boolean func_77557_a(Item var1) {
         return var1 instanceof ItemBow;
      }
   },
   WEARABLE {
      public boolean func_77557_a(Item var1) {
         Block var2 = Block.func_149634_a(var1);
         return var1 instanceof ItemArmor || var1 instanceof ItemElytra || var2 instanceof BlockAbstractSkull || var2 instanceof BlockPumpkin;
      }
   };

   private EnumEnchantmentType() {
   }

   public abstract boolean func_77557_a(Item var1);

   // $FF: synthetic method
   EnumEnchantmentType(Object var3) {
      this();
   }
}
