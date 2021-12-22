package net.minecraft.world.item.enchantment;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class ProtectionEnchantment extends Enchantment {
   public final ProtectionEnchantment.Type type;

   public ProtectionEnchantment(Enchantment.Rarity var1, ProtectionEnchantment.Type var2, EquipmentSlot... var3) {
      super(var1, var2 == ProtectionEnchantment.Type.FALL ? EnchantmentCategory.ARMOR_FEET : EnchantmentCategory.ARMOR, var3);
      this.type = var2;
   }

   public int getMinCost(int var1) {
      return this.type.getMinCost() + (var1 - 1) * this.type.getLevelCost();
   }

   public int getMaxCost(int var1) {
      return this.getMinCost(var1) + this.type.getLevelCost();
   }

   public int getMaxLevel() {
      return 4;
   }

   public int getDamageProtection(int var1, DamageSource var2) {
      if (var2.isBypassInvul()) {
         return 0;
      } else if (this.type == ProtectionEnchantment.Type.ALL) {
         return var1;
      } else if (this.type == ProtectionEnchantment.Type.FIRE && var2.isFire()) {
         return var1 * 2;
      } else if (this.type == ProtectionEnchantment.Type.FALL && var2.isFall()) {
         return var1 * 3;
      } else if (this.type == ProtectionEnchantment.Type.EXPLOSION && var2.isExplosion()) {
         return var1 * 2;
      } else {
         return this.type == ProtectionEnchantment.Type.PROJECTILE && var2.isProjectile() ? var1 * 2 : 0;
      }
   }

   public boolean checkCompatibility(Enchantment var1) {
      if (var1 instanceof ProtectionEnchantment) {
         ProtectionEnchantment var2 = (ProtectionEnchantment)var1;
         if (this.type == var2.type) {
            return false;
         } else {
            return this.type == ProtectionEnchantment.Type.FALL || var2.type == ProtectionEnchantment.Type.FALL;
         }
      } else {
         return super.checkCompatibility(var1);
      }
   }

   public static int getFireAfterDampener(LivingEntity var0, int var1) {
      int var2 = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_PROTECTION, var0);
      if (var2 > 0) {
         var1 -= Mth.floor((float)var1 * (float)var2 * 0.15F);
      }

      return var1;
   }

   public static double getExplosionKnockbackAfterDampener(LivingEntity var0, double var1) {
      int var3 = EnchantmentHelper.getEnchantmentLevel(Enchantments.BLAST_PROTECTION, var0);
      if (var3 > 0) {
         var1 -= (double)Mth.floor(var1 * (double)((float)var3 * 0.15F));
      }

      return var1;
   }

   public static enum Type {
      ALL(1, 11),
      FIRE(10, 8),
      FALL(5, 6),
      EXPLOSION(5, 8),
      PROJECTILE(3, 6);

      private final int minCost;
      private final int levelCost;

      private Type(int var3, int var4) {
         this.minCost = var3;
         this.levelCost = var4;
      }

      public int getMinCost() {
         return this.minCost;
      }

      public int getLevelCost() {
         return this.levelCost;
      }

      // $FF: synthetic method
      private static ProtectionEnchantment.Type[] $values() {
         return new ProtectionEnchantment.Type[]{ALL, FIRE, FALL, EXPLOSION, PROJECTILE};
      }
   }
}
