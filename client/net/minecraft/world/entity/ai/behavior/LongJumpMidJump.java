package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;

public class LongJumpMidJump extends Behavior<Mob> {
   public static final int TIME_OUT_DURATION = 100;
   private final UniformInt timeBetweenLongJumps;
   private final SoundEvent landingSound;

   public LongJumpMidJump(UniformInt var1, SoundEvent var2) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryStatus.VALUE_PRESENT), 100);
      this.timeBetweenLongJumps = var1;
      this.landingSound = var2;
   }

   protected boolean canStillUse(ServerLevel var1, Mob var2, long var3) {
      return !var2.onGround();
   }

   protected void start(ServerLevel var1, Mob var2, long var3) {
      var2.setDiscardFriction(true);
      var2.setPose(Pose.LONG_JUMPING);
   }

   protected void stop(ServerLevel var1, Mob var2, long var3) {
      if (var2.onGround()) {
         var2.setDeltaMovement(var2.getDeltaMovement().multiply(0.10000000149011612, 1.0, 0.10000000149011612));
         var1.playSound((Player)null, var2, this.landingSound, SoundSource.NEUTRAL, 2.0F, 1.0F);
      }

      var2.setDiscardFriction(false);
      var2.setPose(Pose.STANDING);
      var2.getBrain().eraseMemory(MemoryModuleType.LONG_JUMP_MID_JUMP);
      var2.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, (Object)this.timeBetweenLongJumps.sample(var1.random));
   }

   // $FF: synthetic method
   protected void stop(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.stop(var1, (Mob)var2, var3);
   }

   // $FF: synthetic method
   protected void start(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.start(var1, (Mob)var2, var3);
   }
}
