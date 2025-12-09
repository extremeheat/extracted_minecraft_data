package net.minecraft.world.entity.ai.behavior;

import java.util.Map;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import org.jspecify.annotations.Nullable;

public class SpearApproach extends Behavior<PathfinderMob> {
   double speedModifierWhenRepositioning;
   float approachDistanceSq;

   public SpearApproach(double var1, float var3) {
      super(Map.of(MemoryModuleType.SPEAR_STATUS, MemoryStatus.VALUE_ABSENT));
      this.speedModifierWhenRepositioning = var1;
      this.approachDistanceSq = var3 * var3;
   }

   private boolean ableToAttack(PathfinderMob var1) {
      return this.getTarget(var1) != null && var1.getMainHandItem().has(DataComponents.KINETIC_WEAPON);
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, PathfinderMob var2) {
      return this.ableToAttack(var2) && !var2.isUsingItem();
   }

   protected void start(ServerLevel var1, PathfinderMob var2, long var3) {
      var2.setAggressive(true);
      var2.getBrain().setMemory(MemoryModuleType.SPEAR_STATUS, SpearAttack.SpearStatus.APPROACH);
      super.start(var1, var2, var3);
   }

   private @Nullable LivingEntity getTarget(PathfinderMob var1) {
      return (LivingEntity)var1.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse((Object)null);
   }

   protected boolean canStillUse(ServerLevel var1, PathfinderMob var2, long var3) {
      return this.ableToAttack(var2) && this.farEnough(var2);
   }

   private boolean farEnough(PathfinderMob var1) {
      LivingEntity var2 = this.getTarget(var1);
      double var3 = var1.distanceToSqr(var2.getX(), var2.getY(), var2.getZ());
      return var3 > (double)this.approachDistanceSq;
   }

   protected void tick(ServerLevel var1, PathfinderMob var2, long var3) {
      LivingEntity var5 = this.getTarget(var2);
      Entity var6 = var2.getRootVehicle();
      float var7 = 1.0F;
      if (var6 instanceof Mob var8) {
         var7 = var8.chargeSpeedModifier();
      }

      var2.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(var5, true));
      var2.getNavigation().moveTo((Entity)var5, (double)var7 * this.speedModifierWhenRepositioning);
   }

   protected void stop(ServerLevel var1, PathfinderMob var2, long var3) {
      var2.getNavigation().stop();
      var2.getBrain().setMemory(MemoryModuleType.SPEAR_STATUS, SpearAttack.SpearStatus.CHARGING);
   }

   protected boolean timedOut(long var1) {
      return false;
   }

   // $FF: synthetic method
   protected boolean canStillUse(final ServerLevel var1, final LivingEntity var2, final long var3) {
      return this.canStillUse(var1, (PathfinderMob)var2, var3);
   }

   // $FF: synthetic method
   protected void stop(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.stop(var1, (PathfinderMob)var2, var3);
   }

   // $FF: synthetic method
   protected void start(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.start(var1, (PathfinderMob)var2, var3);
   }
}
