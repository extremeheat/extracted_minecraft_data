package net.minecraft.world.entity.ai.behavior.warden;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.warden.WardenAi;

public class Roar extends Behavior<Warden> {
   private static final int TICKS_BEFORE_PLAYING_ROAR_SOUND = 25;
   private static final int ROAR_ANGER_INCREASE = 20;

   public Roar() {
      super(ImmutableMap.of(MemoryModuleType.ROAR_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.ROAR_SOUND_COOLDOWN, MemoryStatus.REGISTERED, MemoryModuleType.ROAR_SOUND_DELAY, MemoryStatus.REGISTERED), WardenAi.ROAR_DURATION);
   }

   protected void start(ServerLevel var1, Warden var2, long var3) {
      Brain var5 = var2.getBrain();
      var5.setMemoryWithExpiry(MemoryModuleType.ROAR_SOUND_DELAY, Unit.INSTANCE, 25L);
      var5.eraseMemory(MemoryModuleType.WALK_TARGET);
      LivingEntity var6 = (LivingEntity)var2.getBrain().getMemory(MemoryModuleType.ROAR_TARGET).get();
      BehaviorUtils.lookAtEntity(var2, var6);
      var2.setPose(Pose.ROARING);
      var2.increaseAngerAt(var6, 20, false);
   }

   protected boolean canStillUse(ServerLevel var1, Warden var2, long var3) {
      return true;
   }

   protected void tick(ServerLevel var1, Warden var2, long var3) {
      if (!var2.getBrain().hasMemoryValue(MemoryModuleType.ROAR_SOUND_DELAY) && !var2.getBrain().hasMemoryValue(MemoryModuleType.ROAR_SOUND_COOLDOWN)) {
         var2.getBrain().setMemoryWithExpiry(MemoryModuleType.ROAR_SOUND_COOLDOWN, Unit.INSTANCE, (long)(WardenAi.ROAR_DURATION - 25));
         var2.playSound(SoundEvents.WARDEN_ROAR, 3.0F, 1.0F);
      }
   }

   protected void stop(ServerLevel var1, Warden var2, long var3) {
      if (var2.hasPose(Pose.ROARING)) {
         var2.setPose(Pose.STANDING);
      }

      Optional var10000 = var2.getBrain().getMemory(MemoryModuleType.ROAR_TARGET);
      Objects.requireNonNull(var2);
      var10000.ifPresent(var2::setAttackTarget);
      var2.getBrain().eraseMemory(MemoryModuleType.ROAR_TARGET);
   }

   // $FF: synthetic method
   protected boolean canStillUse(final ServerLevel var1, final LivingEntity var2, final long var3) {
      return this.canStillUse(var1, (Warden)var2, var3);
   }

   // $FF: synthetic method
   protected void stop(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.stop(var1, (Warden)var2, var3);
   }

   // $FF: synthetic method
   protected void start(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.start(var1, (Warden)var2, var3);
   }
}
