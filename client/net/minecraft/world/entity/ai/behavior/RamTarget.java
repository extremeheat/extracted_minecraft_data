package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class RamTarget<E extends PathfinderMob> extends Behavior<E> {
   public static final int TIME_OUT_DURATION = 200;
   public static final float RAM_SPEED_FORCE_FACTOR = 1.65F;
   private final Function<E, UniformInt> getTimeBetweenRams;
   private final TargetingConditions ramTargeting;
   private final float speed;
   private final ToDoubleFunction<E> getKnockbackForce;
   private Vec3 ramDirection;
   private final Function<E, SoundEvent> getImpactSound;

   public RamTarget(Function<E, UniformInt> var1, TargetingConditions var2, float var3, ToDoubleFunction<E> var4, Function<E, SoundEvent> var5) {
      super(ImmutableMap.of(MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT, MemoryModuleType.RAM_TARGET, MemoryStatus.VALUE_PRESENT), 200);
      this.getTimeBetweenRams = var1;
      this.ramTargeting = var2;
      this.speed = var3;
      this.getKnockbackForce = var4;
      this.getImpactSound = var5;
      this.ramDirection = Vec3.ZERO;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, PathfinderMob var2) {
      return var2.getBrain().hasMemoryValue(MemoryModuleType.RAM_TARGET);
   }

   protected boolean canStillUse(ServerLevel var1, PathfinderMob var2, long var3) {
      return var2.getBrain().hasMemoryValue(MemoryModuleType.RAM_TARGET);
   }

   protected void start(ServerLevel var1, PathfinderMob var2, long var3) {
      BlockPos var5 = var2.blockPosition();
      Brain var6 = var2.getBrain();
      Vec3 var7 = (Vec3)var6.getMemory(MemoryModuleType.RAM_TARGET).get();
      this.ramDirection = (new Vec3((double)var5.getX() - var7.method_2(), 0.0D, (double)var5.getZ() - var7.method_4())).normalize();
      var6.setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(var7, this.speed, 0)));
   }

   protected void tick(ServerLevel var1, E var2, long var3) {
      List var5 = var1.getNearbyEntities(LivingEntity.class, this.ramTargeting, var2, var2.getBoundingBox());
      Brain var6 = var2.getBrain();
      if (!var5.isEmpty()) {
         LivingEntity var7 = (LivingEntity)var5.get(0);
         var7.hurt(DamageSource.mobAttack(var2).setNoAggro(), (float)var2.getAttributeValue(Attributes.ATTACK_DAMAGE));
         int var8 = var2.hasEffect(MobEffects.MOVEMENT_SPEED) ? var2.getEffect(MobEffects.MOVEMENT_SPEED).getAmplifier() + 1 : 0;
         int var9 = var2.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) ? var2.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() + 1 : 0;
         float var10 = 0.25F * (float)(var8 - var9);
         float var11 = Mth.clamp(var2.getSpeed() * 1.65F, 0.2F, 3.0F) + var10;
         float var12 = var7.isDamageSourceBlocked(DamageSource.mobAttack(var2)) ? 0.5F : 1.0F;
         var7.knockback((double)(var12 * var11) * this.getKnockbackForce.applyAsDouble(var2), this.ramDirection.method_2(), this.ramDirection.method_4());
         this.finishRam(var1, var2);
         var1.playSound((Player)null, var2, (SoundEvent)this.getImpactSound.apply(var2), SoundSource.HOSTILE, 1.0F, 1.0F);
      } else {
         Optional var13 = var6.getMemory(MemoryModuleType.WALK_TARGET);
         Optional var14 = var6.getMemory(MemoryModuleType.RAM_TARGET);
         boolean var15 = !var13.isPresent() || !var14.isPresent() || ((WalkTarget)var13.get()).getTarget().currentPosition().distanceTo((Vec3)var14.get()) < 0.25D;
         if (var15) {
            this.finishRam(var1, var2);
         }
      }

   }

   protected void finishRam(ServerLevel var1, E var2) {
      var1.broadcastEntityEvent(var2, (byte)59);
      var2.getBrain().setMemory(MemoryModuleType.RAM_COOLDOWN_TICKS, (Object)((UniformInt)this.getTimeBetweenRams.apply(var2)).sample(var1.random));
      var2.getBrain().eraseMemory(MemoryModuleType.RAM_TARGET);
   }

   // $FF: synthetic method
   protected boolean canStillUse(ServerLevel var1, LivingEntity var2, long var3) {
      return this.canStillUse(var1, (PathfinderMob)var2, var3);
   }

   // $FF: synthetic method
   protected void tick(ServerLevel var1, LivingEntity var2, long var3) {
      this.tick(var1, (PathfinderMob)var2, var3);
   }

   // $FF: synthetic method
   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      this.start(var1, (PathfinderMob)var2, var3);
   }
}
