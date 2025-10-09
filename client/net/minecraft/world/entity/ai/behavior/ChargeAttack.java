package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.Vec3;

public class ChargeAttack extends Behavior<Animal> {
   private final int timeBetweenAttacks;
   private final TargetingConditions chargeTargeting;
   private final float speed;
   private final float knockbackForce;
   private final double maxTargetDetectionDistance;
   private final double maxChargeDistance;
   private Vec3 chargeVelocityVector;
   private Vec3 startPosition;

   public ChargeAttack(int var1, TargetingConditions var2, float var3, float var4, double var5, double var7) {
      super(ImmutableMap.of(MemoryModuleType.CHARGE_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT));
      this.timeBetweenAttacks = var1;
      this.chargeTargeting = var2;
      this.speed = var3;
      this.knockbackForce = var4;
      this.maxChargeDistance = var5;
      this.maxTargetDetectionDistance = var7;
      this.chargeVelocityVector = Vec3.ZERO;
      this.startPosition = Vec3.ZERO;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Animal var2) {
      return var2.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET);
   }

   protected boolean canStillUse(ServerLevel var1, Animal var2, long var3) {
      Brain var5 = var2.getBrain();
      Optional var6 = var5.getMemory(MemoryModuleType.ATTACK_TARGET);
      if (var6.isEmpty()) {
         return false;
      } else {
         LivingEntity var7 = (LivingEntity)var6.get();
         if (var2 instanceof TamableAnimal) {
            TamableAnimal var8 = (TamableAnimal)var2;
            if (var8.isTame()) {
               return false;
            }
         }

         if (var2.position().subtract(this.startPosition).lengthSqr() >= this.maxChargeDistance * this.maxChargeDistance) {
            return false;
         } else if (var7.position().subtract(var2.position()).lengthSqr() >= this.maxTargetDetectionDistance * this.maxTargetDetectionDistance) {
            return false;
         } else if (!var2.hasLineOfSight(var7)) {
            return false;
         } else {
            return !var5.hasMemoryValue(MemoryModuleType.CHARGE_COOLDOWN_TICKS);
         }
      }
   }

   protected void start(ServerLevel var1, Animal var2, long var3) {
      Brain var5 = var2.getBrain();
      this.startPosition = var2.position();
      LivingEntity var6 = (LivingEntity)var5.getMemory(MemoryModuleType.ATTACK_TARGET).get();
      Vec3 var7 = var6.position().subtract(var2.position()).normalize();
      this.chargeVelocityVector = var7.scale((double)this.speed);
   }

   protected void tick(ServerLevel var1, Animal var2, long var3) {
      Brain var5 = var2.getBrain();
      LivingEntity var6 = (LivingEntity)var5.getMemory(MemoryModuleType.ATTACK_TARGET).orElseThrow();
      var2.lookAt(var6, 360.0F, 360.0F);
      var2.setDeltaMovement(this.chargeVelocityVector);
      ArrayList var7 = new ArrayList(1);
      var1.getEntities(EntityTypeTest.forClass(LivingEntity.class), var2.getBoundingBox(), (var3x) -> this.chargeTargeting.test(var1, var2, var3x), var7, 1);
      if (!var7.isEmpty()) {
         LivingEntity var8 = (LivingEntity)var7.get(0);
         if (var2.hasPassenger(var8)) {
            return;
         }

         this.dealDamageToTarget(var1, var2, var8);
         this.dealKnockBack(var2, var8);
         this.stop(var1, var2, var3);
      }

   }

   private void dealDamageToTarget(ServerLevel var1, Animal var2, LivingEntity var3) {
      DamageSource var4 = var1.damageSources().mobAttack(var2);
      float var5 = (float)var2.getAttributeValue(Attributes.ATTACK_DAMAGE);
      if (var3.hurtServer(var1, var4, var5)) {
         EnchantmentHelper.doPostAttackEffects(var1, var3, var4);
      }

   }

   private void dealKnockBack(Animal var1, LivingEntity var2) {
      int var3 = var1.hasEffect(MobEffects.SPEED) ? var1.getEffect(MobEffects.SPEED).getAmplifier() + 1 : 0;
      int var4 = var1.hasEffect(MobEffects.SLOWNESS) ? var1.getEffect(MobEffects.SLOWNESS).getAmplifier() + 1 : 0;
      float var5 = 0.25F * (float)(var3 - var4);
      float var6 = Mth.clamp(this.speed * (float)var1.getAttributeValue(Attributes.MOVEMENT_SPEED), 0.2F, 2.0F) + var5;
      var1.causeExtraKnockback(var2, var6 * this.knockbackForce, var1.getDeltaMovement());
   }

   protected void stop(ServerLevel var1, Animal var2, long var3) {
      var2.getBrain().setMemory(MemoryModuleType.CHARGE_COOLDOWN_TICKS, this.timeBetweenAttacks);
      var2.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
   }

   // $FF: synthetic method
   protected void stop(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.stop(var1, (Animal)var2, var3);
   }

   // $FF: synthetic method
   protected void start(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.start(var1, (Animal)var2, var3);
   }
}
