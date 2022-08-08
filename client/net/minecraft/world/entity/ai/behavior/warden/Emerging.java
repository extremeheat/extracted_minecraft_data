package net.minecraft.world.entity.ai.behavior.warden;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.warden.Warden;

public class Emerging<E extends Warden> extends Behavior<E> {
   public Emerging(int var1) {
      super(ImmutableMap.of(MemoryModuleType.IS_EMERGING, MemoryStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED), var1);
   }

   protected boolean canStillUse(ServerLevel var1, E var2, long var3) {
      return true;
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      var2.setPose(Pose.EMERGING);
      var2.playSound(SoundEvents.WARDEN_EMERGE, 5.0F, 1.0F);
   }

   protected void stop(ServerLevel var1, E var2, long var3) {
      if (var2.hasPose(Pose.EMERGING)) {
         var2.setPose(Pose.STANDING);
      }

   }

   // $FF: synthetic method
   protected boolean canStillUse(ServerLevel var1, LivingEntity var2, long var3) {
      return this.canStillUse(var1, (Warden)var2, var3);
   }

   // $FF: synthetic method
   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      this.start(var1, (Warden)var2, var3);
   }
}
