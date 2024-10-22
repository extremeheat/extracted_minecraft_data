package net.minecraft.world.entity.monster.hoglin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;

public interface HoglinBase {
   int ATTACK_ANIMATION_DURATION = 10;
   float PROBABILITY_OF_SPAWNING_AS_BABY = 0.2F;

   int getAttackAnimationRemainingTicks();

   static boolean hurtAndThrowTarget(ServerLevel var0, LivingEntity var1, LivingEntity var2) {
      float var4 = (float)var1.getAttributeValue(Attributes.ATTACK_DAMAGE);
      float var3;
      if (!var1.isBaby() && (int)var4 > 0) {
         var3 = var4 / 2.0F + (float)var0.random.nextInt((int)var4);
      } else {
         var3 = var4;
      }

      DamageSource var5 = var1.damageSources().mobAttack(var1);
      boolean var6 = var2.hurtServer(var0, var5, var3);
      if (var6) {
         EnchantmentHelper.doPostAttackEffects(var0, var2, var5);
         if (!var1.isBaby()) {
            throwTarget(var1, var2);
         }
      }

      return var6;
   }

   static void throwTarget(LivingEntity var0, LivingEntity var1) {
      double var2 = var0.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
      double var4 = var1.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
      double var6 = var2 - var4;
      if (!(var6 <= 0.0)) {
         double var8 = var1.getX() - var0.getX();
         double var10 = var1.getZ() - var0.getZ();
         float var12 = (float)(var0.level().random.nextInt(21) - 10);
         double var13 = var6 * (double)(var0.level().random.nextFloat() * 0.5F + 0.2F);
         Vec3 var15 = new Vec3(var8, 0.0, var10).normalize().scale(var13).yRot(var12);
         double var16 = var6 * (double)var0.level().random.nextFloat() * 0.5;
         var1.push(var15.x, var16, var15.z);
         var1.hurtMarked = true;
      }
   }
}
