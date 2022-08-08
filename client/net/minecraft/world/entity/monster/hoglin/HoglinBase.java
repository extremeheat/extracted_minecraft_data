package net.minecraft.world.entity.monster.hoglin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

public interface HoglinBase {
   int ATTACK_ANIMATION_DURATION = 10;

   int getAttackAnimationRemainingTicks();

   static boolean hurtAndThrowTarget(LivingEntity var0, LivingEntity var1) {
      float var3 = (float)var0.getAttributeValue(Attributes.ATTACK_DAMAGE);
      float var2;
      if (!var0.isBaby() && (int)var3 > 0) {
         var2 = var3 / 2.0F + (float)var0.level.random.nextInt((int)var3);
      } else {
         var2 = var3;
      }

      boolean var4 = var1.hurt(DamageSource.mobAttack(var0), var2);
      if (var4) {
         var0.doEnchantDamageEffects(var0, var1);
         if (!var0.isBaby()) {
            throwTarget(var0, var1);
         }
      }

      return var4;
   }

   static void throwTarget(LivingEntity var0, LivingEntity var1) {
      double var2 = var0.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
      double var4 = var1.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
      double var6 = var2 - var4;
      if (!(var6 <= 0.0)) {
         double var8 = var1.getX() - var0.getX();
         double var10 = var1.getZ() - var0.getZ();
         float var12 = (float)(var0.level.random.nextInt(21) - 10);
         double var13 = var6 * (double)(var0.level.random.nextFloat() * 0.5F + 0.2F);
         Vec3 var15 = (new Vec3(var8, 0.0, var10)).normalize().scale(var13).yRot(var12);
         double var16 = var6 * (double)var0.level.random.nextFloat() * 0.5;
         var1.push(var15.x, var16, var15.z);
         var1.hurtMarked = true;
      }
   }
}
