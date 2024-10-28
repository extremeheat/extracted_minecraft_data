package net.minecraft.world.damagesource;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public class CombatRules {
   public static final float MAX_ARMOR = 20.0F;
   public static final float ARMOR_PROTECTION_DIVIDER = 25.0F;
   public static final float BASE_ARMOR_TOUGHNESS = 2.0F;
   public static final float MIN_ARMOR_RATIO = 0.2F;
   private static final int NUM_ARMOR_ITEMS = 4;

   public CombatRules() {
      super();
   }

   public static float getDamageAfterAbsorb(LivingEntity var0, float var1, DamageSource var2, float var3, float var4) {
      float var8;
      label12: {
         float var5 = 2.0F + var4 / 4.0F;
         float var6 = Mth.clamp(var3 - var1 / var5, var3 * 0.2F, 20.0F);
         float var7 = var6 / 25.0F;
         ItemStack var9 = var2.getWeaponItem();
         if (var9 != null) {
            Level var11 = var0.level();
            if (var11 instanceof ServerLevel) {
               ServerLevel var10 = (ServerLevel)var11;
               var8 = Mth.clamp(EnchantmentHelper.modifyArmorEffectiveness(var10, var9, var0, var2, var7), 0.0F, 1.0F);
               break label12;
            }
         }

         var8 = var7;
      }

      float var12 = 1.0F - var8;
      return var1 * var12;
   }

   public static float getDamageAfterMagicAbsorb(float var0, float var1) {
      float var2 = Mth.clamp(var1, 0.0F, 20.0F);
      return var0 * (1.0F - var2 / 25.0F);
   }
}
