package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.phys.Vec3;

public class RandomLookAround extends Behavior<Mob> {
   private final IntProvider interval;
   private final float maxYaw;
   private final float minPitch;
   private final float pitchRange;

   public RandomLookAround(IntProvider var1, float var2, float var3, float var4) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.GAZE_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT));
      if (var3 > var4) {
         throw new IllegalArgumentException("Minimum pitch is larger than maximum pitch! " + var3 + " > " + var4);
      } else {
         this.interval = var1;
         this.maxYaw = var2;
         this.minPitch = var3;
         this.pitchRange = var4 - var3;
      }
   }

   protected void start(ServerLevel var1, Mob var2, long var3) {
      RandomSource var5 = var2.getRandom();
      float var6 = Mth.clamp(var5.nextFloat() * this.pitchRange + this.minPitch, -90.0F, 90.0F);
      float var7 = Mth.wrapDegrees(var2.getYRot() + 2.0F * var5.nextFloat() * this.maxYaw - this.maxYaw);
      Vec3 var8 = Vec3.directionFromRotation(var6, var7);
      var2.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new BlockPosTracker(var2.getEyePosition().add(var8))));
      var2.getBrain().setMemory(MemoryModuleType.GAZE_COOLDOWN_TICKS, (Object)this.interval.sample(var5));
   }

   // $FF: synthetic method
   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      this.start(var1, (Mob)var2, var3);
   }
}
