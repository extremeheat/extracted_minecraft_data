package net.minecraft.world.entity;

import net.minecraft.world.item.ItemStack;

public class Crackiness {
   public static final Crackiness GOLEM = new Crackiness(0.75F, 0.5F, 0.25F);
   public static final Crackiness WOLF_ARMOR = new Crackiness(0.95F, 0.69F, 0.32F);
   private final float fractionLow;
   private final float fractionMedium;
   private final float fractionHigh;

   private Crackiness(float var1, float var2, float var3) {
      super();
      this.fractionLow = var1;
      this.fractionMedium = var2;
      this.fractionHigh = var3;
   }

   public Level byFraction(float var1) {
      if (var1 < this.fractionHigh) {
         return Crackiness.Level.HIGH;
      } else if (var1 < this.fractionMedium) {
         return Crackiness.Level.MEDIUM;
      } else {
         return var1 < this.fractionLow ? Crackiness.Level.LOW : Crackiness.Level.NONE;
      }
   }

   public Level byDamage(ItemStack var1) {
      return !var1.isDamageableItem() ? Crackiness.Level.NONE : this.byDamage(var1.getDamageValue(), var1.getMaxDamage());
   }

   public Level byDamage(int var1, int var2) {
      return this.byFraction((float)(var2 - var1) / (float)var2);
   }

   public static enum Level {
      NONE,
      LOW,
      MEDIUM,
      HIGH;

      private Level() {
      }

      // $FF: synthetic method
      private static Level[] $values() {
         return new Level[]{NONE, LOW, MEDIUM, HIGH};
      }
   }
}
