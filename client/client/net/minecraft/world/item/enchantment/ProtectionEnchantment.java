package net.minecraft.world.item.enchantment;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class ProtectionEnchantment extends Enchantment {
   public final ProtectionEnchantment.Type type;

   public ProtectionEnchantment(Enchantment.EnchantmentDefinition var1, ProtectionEnchantment.Type var2) {
      super(var1);
      this.type = var2;
   }

   @Override
   public int getDamageProtection(int var1, DamageSource var2) {
      if (var2.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return 0;
      } else if (this.type == ProtectionEnchantment.Type.ALL) {
         return var1;
      } else if (this.type == ProtectionEnchantment.Type.FIRE && var2.is(DamageTypeTags.IS_FIRE)) {
         return var1 * 2;
      } else if (this.type == ProtectionEnchantment.Type.FALL && var2.is(DamageTypeTags.IS_FALL)) {
         return var1 * 3;
      } else if (this.type == ProtectionEnchantment.Type.EXPLOSION && var2.is(DamageTypeTags.IS_EXPLOSION)) {
         return var1 * 2;
      } else {
         return this.type == ProtectionEnchantment.Type.PROJECTILE && var2.is(DamageTypeTags.IS_PROJECTILE) ? var1 * 2 : 0;
      }
   }

   @Override
   public boolean checkCompatibility(Enchantment var1) {
      if (var1 instanceof ProtectionEnchantment var2) {
         return this.type == var2.type ? false : this.type == ProtectionEnchantment.Type.FALL || var2.type == ProtectionEnchantment.Type.FALL;
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
         var1 *= Mth.clamp(1.0 - (double)var3 * 0.15, 0.0, 1.0);
      }

      return var1;
   }

   public static enum Type {
      ALL,
      FIRE,
      FALL,
      EXPLOSION,
      PROJECTILE;

      private Type() {
      }
   }
}
