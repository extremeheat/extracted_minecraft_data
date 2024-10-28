package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;

public class RamTarget extends Behavior<Goat> {
   public static final int TIME_OUT_DURATION = 200;
   public static final float RAM_SPEED_FORCE_FACTOR = 1.65F;
   private final Function<Goat, UniformInt> getTimeBetweenRams;
   private final TargetingConditions ramTargeting;
   private final float speed;
   private final ToDoubleFunction<Goat> getKnockbackForce;
   private Vec3 ramDirection;
   private final Function<Goat, SoundEvent> getImpactSound;
   private final Function<Goat, SoundEvent> getHornBreakSound;

   public RamTarget(Function<Goat, UniformInt> var1, TargetingConditions var2, float var3, ToDoubleFunction<Goat> var4, Function<Goat, SoundEvent> var5, Function<Goat, SoundEvent> var6) {
      super(ImmutableMap.of(MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT, MemoryModuleType.RAM_TARGET, MemoryStatus.VALUE_PRESENT), 200);
      this.getTimeBetweenRams = var1;
      this.ramTargeting = var2;
      this.speed = var3;
      this.getKnockbackForce = var4;
      this.getImpactSound = var5;
      this.getHornBreakSound = var6;
      this.ramDirection = Vec3.ZERO;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Goat var2) {
      return var2.getBrain().hasMemoryValue(MemoryModuleType.RAM_TARGET);
   }

   protected boolean canStillUse(ServerLevel var1, Goat var2, long var3) {
      return var2.getBrain().hasMemoryValue(MemoryModuleType.RAM_TARGET);
   }

   protected void start(ServerLevel var1, Goat var2, long var3) {
      BlockPos var5 = var2.blockPosition();
      Brain var6 = var2.getBrain();
      Vec3 var7 = (Vec3)var6.getMemory(MemoryModuleType.RAM_TARGET).get();
      this.ramDirection = (new Vec3((double)var5.getX() - var7.x(), 0.0, (double)var5.getZ() - var7.z())).normalize();
      var6.setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(var7, this.speed, 0)));
   }

   protected void tick(ServerLevel var1, Goat var2, long var3) {
      List var5 = var1.getNearbyEntities(LivingEntity.class, this.ramTargeting, var2, var2.getBoundingBox());
      Brain var6 = var2.getBrain();
      if (!var5.isEmpty()) {
         LivingEntity var7 = (LivingEntity)var5.get(0);
         DamageSource var8 = var1.damageSources().noAggroMobAttack(var2);
         if (var7.hurt(var8, (float)var2.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
            EnchantmentHelper.doPostAttackEffects(var1, var7, var8);
         }

         int var9 = var2.hasEffect(MobEffects.MOVEMENT_SPEED) ? var2.getEffect(MobEffects.MOVEMENT_SPEED).getAmplifier() + 1 : 0;
         int var10 = var2.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) ? var2.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() + 1 : 0;
         float var11 = 0.25F * (float)(var9 - var10);
         float var12 = Mth.clamp(var2.getSpeed() * 1.65F, 0.2F, 3.0F) + var11;
         float var13 = var7.isDamageSourceBlocked(var1.damageSources().mobAttack(var2)) ? 0.5F : 1.0F;
         var7.knockback((double)(var13 * var12) * this.getKnockbackForce.applyAsDouble(var2), this.ramDirection.x(), this.ramDirection.z());
         this.finishRam(var1, var2);
         var1.playSound((Player)null, var2, (SoundEvent)this.getImpactSound.apply(var2), SoundSource.NEUTRAL, 1.0F, 1.0F);
      } else if (this.hasRammedHornBreakingBlock(var1, var2)) {
         var1.playSound((Player)null, var2, (SoundEvent)this.getImpactSound.apply(var2), SoundSource.NEUTRAL, 1.0F, 1.0F);
         boolean var14 = var2.dropHorn();
         if (var14) {
            var1.playSound((Player)null, var2, (SoundEvent)this.getHornBreakSound.apply(var2), SoundSource.NEUTRAL, 1.0F, 1.0F);
         }

         this.finishRam(var1, var2);
      } else {
         Optional var15 = var6.getMemory(MemoryModuleType.WALK_TARGET);
         Optional var16 = var6.getMemory(MemoryModuleType.RAM_TARGET);
         boolean var17 = var15.isEmpty() || var16.isEmpty() || ((WalkTarget)var15.get()).getTarget().currentPosition().closerThan((Position)var16.get(), 0.25);
         if (var17) {
            this.finishRam(var1, var2);
         }
      }

   }

   private boolean hasRammedHornBreakingBlock(ServerLevel var1, Goat var2) {
      Vec3 var3 = var2.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize();
      BlockPos var4 = BlockPos.containing(var2.position().add(var3));
      return var1.getBlockState(var4).is(BlockTags.SNAPS_GOAT_HORN) || var1.getBlockState(var4.above()).is(BlockTags.SNAPS_GOAT_HORN);
   }

   protected void finishRam(ServerLevel var1, Goat var2) {
      var1.broadcastEntityEvent(var2, (byte)59);
      var2.getBrain().setMemory(MemoryModuleType.RAM_COOLDOWN_TICKS, (Object)((UniformInt)this.getTimeBetweenRams.apply(var2)).sample(var1.random));
      var2.getBrain().eraseMemory(MemoryModuleType.RAM_TARGET);
   }

   // $FF: synthetic method
   protected void start(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.start(var1, (Goat)var2, var3);
   }
}
