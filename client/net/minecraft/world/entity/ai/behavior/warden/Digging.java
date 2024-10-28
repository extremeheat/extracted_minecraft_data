package net.minecraft.world.entity.ai.behavior.warden;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.warden.Warden;

public class Digging<E extends Warden> extends Behavior<E> {
   public Digging(int var1) {
      super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), var1);
   }

   protected boolean canStillUse(ServerLevel var1, E var2, long var3) {
      return var2.getRemovalReason() == null;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, E var2) {
      return var2.onGround() || var2.isInWater() || var2.isInLava();
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      if (var2.onGround()) {
         var2.setPose(Pose.DIGGING);
         var2.playSound(SoundEvents.WARDEN_DIG, 5.0F, 1.0F);
      } else {
         var2.playSound(SoundEvents.WARDEN_AGITATED, 5.0F, 1.0F);
         this.stop(var1, var2, var3);
      }

   }

   protected void stop(ServerLevel var1, E var2, long var3) {
      if (var2.getRemovalReason() == null) {
         var2.remove(Entity.RemovalReason.DISCARDED);
      }

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
