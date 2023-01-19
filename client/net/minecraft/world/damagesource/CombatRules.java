package net.minecraft.world.damagesource;

import net.minecraft.util.Mth;

public class CombatRules {
   public static final float MAX_ARMOR = 20.0F;
   public static final float ARMOR_PROTECTION_DIVIDER = 25.0F;
   public static final float BASE_ARMOR_TOUGHNESS = 2.0F;
   public static final float MIN_ARMOR_RATIO = 0.2F;
   private static final int NUM_ARMOR_ITEMS = 4;

   public CombatRules() {
      super();
   }

   public static float getDamageAfterAbsorb(float var0, float var1, float var2) {
      float var3 = 2.0F + var2 / 4.0F;
      float var4 = Mth.clamp(var1 - var0 / var3, var1 * 0.2F, 20.0F);
      return var0 * (1.0F - var4 / 25.0F);
   }

   public static float getDamageAfterMagicAbsorb(float var0, float var1) {
      float var2 = Mth.clamp(var1, 0.0F, 20.0F);
      return var0 * (1.0F - var2 / 25.0F);
   }
}
