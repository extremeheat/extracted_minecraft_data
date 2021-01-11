package net.minecraft.util;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public final class EntitySelectors {
   public static final Predicate<Entity> field_94557_a = new Predicate<Entity>() {
      public boolean apply(Entity var1) {
         return var1.func_70089_S();
      }

      // $FF: synthetic method
      public boolean apply(Object var1) {
         return this.apply((Entity)var1);
      }
   };
   public static final Predicate<Entity> field_152785_b = new Predicate<Entity>() {
      public boolean apply(Entity var1) {
         return var1.func_70089_S() && var1.field_70153_n == null && var1.field_70154_o == null;
      }

      // $FF: synthetic method
      public boolean apply(Object var1) {
         return this.apply((Entity)var1);
      }
   };
   public static final Predicate<Entity> field_96566_b = new Predicate<Entity>() {
      public boolean apply(Entity var1) {
         return var1 instanceof IInventory && var1.func_70089_S();
      }

      // $FF: synthetic method
      public boolean apply(Object var1) {
         return this.apply((Entity)var1);
      }
   };
   public static final Predicate<Entity> field_180132_d = new Predicate<Entity>() {
      public boolean apply(Entity var1) {
         return !(var1 instanceof EntityPlayer) || !((EntityPlayer)var1).func_175149_v();
      }

      // $FF: synthetic method
      public boolean apply(Object var1) {
         return this.apply((Entity)var1);
      }
   };

   public static class ArmoredMob implements Predicate<Entity> {
      private final ItemStack field_96567_c;

      public ArmoredMob(ItemStack var1) {
         super();
         this.field_96567_c = var1;
      }

      public boolean apply(Entity var1) {
         if (!var1.func_70089_S()) {
            return false;
         } else if (!(var1 instanceof EntityLivingBase)) {
            return false;
         } else {
            EntityLivingBase var2 = (EntityLivingBase)var1;
            if (var2.func_71124_b(EntityLiving.func_82159_b(this.field_96567_c)) != null) {
               return false;
            } else if (var2 instanceof EntityLiving) {
               return ((EntityLiving)var2).func_98052_bS();
            } else if (var2 instanceof EntityArmorStand) {
               return true;
            } else {
               return var2 instanceof EntityPlayer;
            }
         }
      }

      // $FF: synthetic method
      public boolean apply(Object var1) {
         return this.apply((Entity)var1);
      }
   }
}
