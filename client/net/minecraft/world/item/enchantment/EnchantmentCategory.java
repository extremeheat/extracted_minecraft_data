package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.level.block.Block;

public enum EnchantmentCategory {
   ARMOR {
      @Override
      public boolean canEnchant(Item var1) {
         return var1 instanceof ArmorItem;
      }
   },
   ARMOR_FEET {
      @Override
      public boolean canEnchant(Item var1) {
         if (var1 instanceof ArmorItem var2 && var2.getEquipmentSlot() == EquipmentSlot.FEET) {
            return true;
         }

         return false;
      }
   },
   ARMOR_LEGS {
      @Override
      public boolean canEnchant(Item var1) {
         if (var1 instanceof ArmorItem var2 && var2.getEquipmentSlot() == EquipmentSlot.LEGS) {
            return true;
         }

         return false;
      }
   },
   ARMOR_CHEST {
      @Override
      public boolean canEnchant(Item var1) {
         if (var1 instanceof ArmorItem var2 && var2.getEquipmentSlot() == EquipmentSlot.CHEST) {
            return true;
         }

         return false;
      }
   },
   ARMOR_HEAD {
      @Override
      public boolean canEnchant(Item var1) {
         if (var1 instanceof ArmorItem var2 && var2.getEquipmentSlot() == EquipmentSlot.HEAD) {
            return true;
         }

         return false;
      }
   },
   WEAPON {
      @Override
      public boolean canEnchant(Item var1) {
         return var1 instanceof SwordItem;
      }
   },
   DIGGER {
      @Override
      public boolean canEnchant(Item var1) {
         return var1 instanceof DiggerItem;
      }
   },
   FISHING_ROD {
      @Override
      public boolean canEnchant(Item var1) {
         return var1 instanceof FishingRodItem;
      }
   },
   TRIDENT {
      @Override
      public boolean canEnchant(Item var1) {
         return var1 instanceof TridentItem;
      }
   },
   BREAKABLE {
      @Override
      public boolean canEnchant(Item var1) {
         return var1.canBeDepleted();
      }
   },
   BOW {
      @Override
      public boolean canEnchant(Item var1) {
         return var1 instanceof BowItem;
      }
   },
   WEARABLE {
      @Override
      public boolean canEnchant(Item var1) {
         return var1 instanceof Equipable || Block.byItem(var1) instanceof Equipable;
      }
   },
   CROSSBOW {
      @Override
      public boolean canEnchant(Item var1) {
         return var1 instanceof CrossbowItem;
      }
   },
   VANISHABLE {
      @Override
      public boolean canEnchant(Item var1) {
         return var1 instanceof Vanishable || Block.byItem(var1) instanceof Vanishable || BREAKABLE.canEnchant(var1);
      }
   };

   EnchantmentCategory() {
   }

   public abstract boolean canEnchant(Item var1);
}
