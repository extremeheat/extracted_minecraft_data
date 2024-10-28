package net.minecraft.world.entity.ai.behavior.warden;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.warden.WardenAi;

public class Sniffing<E extends Warden> extends Behavior<E> {
   private static final double ANGER_FROM_SNIFFING_MAX_DISTANCE_XZ = 6.0;
   private static final double ANGER_FROM_SNIFFING_MAX_DISTANCE_Y = 20.0;

   public Sniffing(int var1) {
      super(ImmutableMap.of(MemoryModuleType.IS_SNIFFING, MemoryStatus.VALUE_PRESENT, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.NEAREST_ATTACKABLE, MemoryStatus.REGISTERED, MemoryModuleType.DISTURBANCE_LOCATION, MemoryStatus.REGISTERED, MemoryModuleType.SNIFF_COOLDOWN, MemoryStatus.REGISTERED), var1);
   }

   protected boolean canStillUse(ServerLevel var1, E var2, long var3) {
      return true;
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      var2.playSound(SoundEvents.WARDEN_SNIFF, 5.0F, 1.0F);
   }

   protected void stop(ServerLevel var1, E var2, long var3) {
      if (var2.hasPose(Pose.SNIFFING)) {
         var2.setPose(Pose.STANDING);
      }

      var2.getBrain().eraseMemory(MemoryModuleType.IS_SNIFFING);
      Optional var10000 = var2.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE);
      Objects.requireNonNull(var2);
      var10000.filter(var2::canTargetEntity).ifPresent((var1x) -> {
         if (var2.closerThan(var1x, 6.0, 20.0)) {
            var2.increaseAngerAt(var1x);
         }

         if (!var2.getBrain().hasMemoryValue(MemoryModuleType.DISTURBANCE_LOCATION)) {
            WardenAi.setDisturbanceLocation(var2, var1x.blockPosition());
         }

      });
   }

   // $FF: synthetic method
   protected void stop(ServerLevel var1, LivingEntity var2, long var3) {
      this.stop(var1, (Warden)var2, var3);
   }

   // $FF: synthetic method
   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      this.start(var1, (Warden)var2, var3);
   }
}
