package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PumpkinBlock;

public enum EnchantmentCategory {
   ALL {
      public boolean canEnchant(Item var1) {
         EnchantmentCategory[] var2 = EnchantmentCategory.values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            EnchantmentCategory var5 = var2[var4];
            if (var5 != EnchantmentCategory.ALL && var5.canEnchant(var1)) {
               return true;
            }
         }

         return false;
      }
   },
   ARMOR {
      public boolean canEnchant(Item var1) {
         return var1 instanceof ArmorItem;
      }
   },
   ARMOR_FEET {
      public boolean canEnchant(Item var1) {
         return var1 instanceof ArmorItem && ((ArmorItem)var1).getSlot() == EquipmentSlot.FEET;
      }
   },
   ARMOR_LEGS {
      public boolean canEnchant(Item var1) {
         return var1 instanceof ArmorItem && ((ArmorItem)var1).getSlot() == EquipmentSlot.LEGS;
      }
   },
   ARMOR_CHEST {
      public boolean canEnchant(Item var1) {
         return var1 instanceof ArmorItem && ((ArmorItem)var1).getSlot() == EquipmentSlot.CHEST;
      }
   },
   ARMOR_HEAD {
      public boolean canEnchant(Item var1) {
         return var1 instanceof ArmorItem && ((ArmorItem)var1).getSlot() == EquipmentSlot.HEAD;
      }
   },
   WEAPON {
      public boolean canEnchant(Item var1) {
         return var1 instanceof SwordItem;
      }
   },
   DIGGER {
      public boolean canEnchant(Item var1) {
         return var1 instanceof DiggerItem;
      }
   },
   FISHING_ROD {
      public boolean canEnchant(Item var1) {
         return var1 instanceof FishingRodItem;
      }
   },
   TRIDENT {
      public boolean canEnchant(Item var1) {
         return var1 instanceof TridentItem;
      }
   },
   BREAKABLE {
      public boolean canEnchant(Item var1) {
         return var1.canBeDepleted();
      }
   },
   BOW {
      public boolean canEnchant(Item var1) {
         return var1 instanceof BowItem;
      }
   },
   WEARABLE {
      public boolean canEnchant(Item var1) {
         Block var2 = Block.byItem(var1);
         return var1 instanceof ArmorItem || var1 instanceof ElytraItem || var2 instanceof AbstractSkullBlock || var2 instanceof PumpkinBlock;
      }
   },
   CROSSBOW {
      public boolean canEnchant(Item var1) {
         return var1 instanceof CrossbowItem;
      }
   };

   private EnchantmentCategory() {
   }

   public abstract boolean canEnchant(Item var1);

   // $FF: synthetic method
   EnchantmentCategory(Object var3) {
      this();
   }
}
