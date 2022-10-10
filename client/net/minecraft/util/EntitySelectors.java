package net.minecraft.util;

import com.google.common.base.Predicates;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;

public final class EntitySelectors {
   public static final Predicate<Entity> field_94557_a = Entity::func_70089_S;
   public static final Predicate<EntityLivingBase> field_212545_b = EntityLivingBase::func_70089_S;
   public static final Predicate<Entity> field_152785_b = (var0) -> {
      return var0.func_70089_S() && !var0.func_184207_aI() && !var0.func_184218_aH();
   };
   public static final Predicate<Entity> field_96566_b = (var0) -> {
      return var0 instanceof IInventory && var0.func_70089_S();
   };
   public static final Predicate<Entity> field_188444_d = (var0) -> {
      return !(var0 instanceof EntityPlayer) || !((EntityPlayer)var0).func_175149_v() && !((EntityPlayer)var0).func_184812_l_();
   };
   public static final Predicate<Entity> field_180132_d = (var0) -> {
      return !(var0 instanceof EntityPlayer) || !((EntityPlayer)var0).func_175149_v();
   };

   public static Predicate<Entity> func_188443_a(double var0, double var2, double var4, double var6) {
      double var8 = var6 * var6;
      return (var8x) -> {
         return var8x != null && var8x.func_70092_e(var0, var2, var4) <= var8;
      };
   }

   public static Predicate<Entity> func_200823_a(Entity var0) {
      Team var1 = var0.func_96124_cp();
      Team.CollisionRule var2 = var1 == null ? Team.CollisionRule.ALWAYS : var1.func_186681_k();
      return (Predicate)(var2 == Team.CollisionRule.NEVER ? Predicates.alwaysFalse() : field_180132_d.and((var3) -> {
         if (!var3.func_70104_M()) {
            return false;
         } else if (var0.field_70170_p.field_72995_K && (!(var3 instanceof EntityPlayer) || !((EntityPlayer)var3).func_175144_cb())) {
            return false;
         } else {
            Team var4 = var3.func_96124_cp();
            Team.CollisionRule var5 = var4 == null ? Team.CollisionRule.ALWAYS : var4.func_186681_k();
            if (var5 == Team.CollisionRule.NEVER) {
               return false;
            } else {
               boolean var6 = var1 != null && var1.func_142054_a(var4);
               if ((var2 == Team.CollisionRule.PUSH_OWN_TEAM || var5 == Team.CollisionRule.PUSH_OWN_TEAM) && var6) {
                  return false;
               } else {
                  return var2 != Team.CollisionRule.PUSH_OTHER_TEAMS && var5 != Team.CollisionRule.PUSH_OTHER_TEAMS || var6;
               }
            }
         }
      }));
   }

   public static Predicate<Entity> func_200820_b(Entity var0) {
      return (var1) -> {
         while(true) {
            if (var1.func_184218_aH()) {
               var1 = var1.func_184187_bx();
               if (var1 != var0) {
                  continue;
               }

               return false;
            }

            return true;
         }
      };
   }

   public static class ArmoredMob implements Predicate<Entity> {
      private final ItemStack field_96567_c;

      public ArmoredMob(ItemStack var1) {
         super();
         this.field_96567_c = var1;
      }

      public boolean test(@Nullable Entity var1) {
         if (!var1.func_70089_S()) {
            return false;
         } else if (!(var1 instanceof EntityLivingBase)) {
            return false;
         } else {
            EntityLivingBase var2 = (EntityLivingBase)var1;
            EntityEquipmentSlot var3 = EntityLiving.func_184640_d(this.field_96567_c);
            if (!var2.func_184582_a(var3).func_190926_b()) {
               return false;
            } else if (var2 instanceof EntityLiving) {
               return ((EntityLiving)var2).func_98052_bS();
            } else if (var2 instanceof EntityArmorStand) {
               return !((EntityArmorStand)var2).func_184796_b(var3);
            } else {
               return var2 instanceof EntityPlayer;
            }
         }
      }

      // $FF: synthetic method
      public boolean test(@Nullable Object var1) {
         return this.test((Entity)var1);
      }
   }
}
