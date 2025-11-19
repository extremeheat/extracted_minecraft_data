package net.minecraft.world.entity.ai.behavior;

import java.util.Map;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.item.component.KineticWeapon;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class SpearAttack extends Behavior<PathfinderMob> {
   public static final int MIN_REPOSITION_DISTANCE = 6;
   public static final int MAX_REPOSITION_DISTANCE = 7;
   double speedModifierWhenCharging;
   double speedModifierWhenRepositioning;
   float approachDistanceSq;
   float targetInRangeRadiusSq;

   public SpearAttack(double var1, double var3, float var5, float var6) {
      super(Map.of(MemoryModuleType.SPEAR_STATUS, MemoryStatus.VALUE_PRESENT));
      this.speedModifierWhenCharging = var1;
      this.speedModifierWhenRepositioning = var3;
      this.approachDistanceSq = var5 * var5;
      this.targetInRangeRadiusSq = var6 * var6;
   }

   private @Nullable LivingEntity getTarget(PathfinderMob var1) {
      return (LivingEntity)var1.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse((Object)null);
   }

   private boolean ableToAttack(PathfinderMob var1) {
      return this.getTarget(var1) != null && var1.getMainHandItem().has(DataComponents.KINETIC_WEAPON);
   }

   private int getKineticWeaponUseDuration(PathfinderMob var1) {
      return (Integer)Optional.ofNullable((KineticWeapon)var1.getMainHandItem().get(DataComponents.KINETIC_WEAPON)).map(KineticWeapon::computeDamageUseDuration).orElse(0);
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, PathfinderMob var2) {
      return var2.getBrain().getMemory(MemoryModuleType.SPEAR_STATUS).orElse(SpearAttack.SpearStatus.APPROACH) == SpearAttack.SpearStatus.CHARGING && this.ableToAttack(var2) && !var2.isUsingItem();
   }

   protected void start(ServerLevel var1, PathfinderMob var2, long var3) {
      var2.setAggressive(true);
      var2.getBrain().setMemory(MemoryModuleType.SPEAR_ENGAGE_TIME, this.getKineticWeaponUseDuration(var2));
      var2.getBrain().eraseMemory(MemoryModuleType.SPEAR_CHARGE_POSITION);
      var2.startUsingItem(InteractionHand.MAIN_HAND);
      super.start(var1, var2, var3);
   }

   protected boolean canStillUse(ServerLevel var1, PathfinderMob var2, long var3) {
      return (Integer)var2.getBrain().getMemory(MemoryModuleType.SPEAR_ENGAGE_TIME).orElse(0) > 0 && this.ableToAttack(var2);
   }

   protected void tick(ServerLevel var1, PathfinderMob var2, long var3) {
      LivingEntity var5 = this.getTarget(var2);
      double var6 = var2.distanceToSqr(var5.getX(), var5.getY(), var5.getZ());
      Entity var8 = var2.getRootVehicle();
      float var9 = 1.0F;
      if (var8 instanceof Mob var10) {
         var9 = var10.chargeSpeedModifier();
      }

      int var15 = var2.isPassenger() ? 2 : 0;
      var2.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(var5, true));
      var2.getBrain().setMemory(MemoryModuleType.SPEAR_ENGAGE_TIME, (Integer)var2.getBrain().getMemory(MemoryModuleType.SPEAR_ENGAGE_TIME).orElse(0) - 1);
      Vec3 var11 = (Vec3)var2.getBrain().getMemory(MemoryModuleType.SPEAR_CHARGE_POSITION).orElse((Object)null);
      if (var11 != null) {
         var2.getNavigation().moveTo(var11.x, var11.y, var11.z, (double)var9 * this.speedModifierWhenRepositioning);
         if (var2.getNavigation().isDone()) {
            var2.getBrain().eraseMemory(MemoryModuleType.SPEAR_CHARGE_POSITION);
         }
      } else {
         var2.getNavigation().moveTo((Entity)var5, (double)var9 * this.speedModifierWhenCharging);
         if (var6 < (double)this.targetInRangeRadiusSq || var2.getNavigation().isDone()) {
            double var12 = Math.sqrt(var6);
            Vec3 var14 = LandRandomPos.getPosAway(var2, (double)(6 + var15) - var12, (double)(7 + var15) - var12, 7, var5.position());
            var2.getBrain().setMemory(MemoryModuleType.SPEAR_CHARGE_POSITION, var14);
         }
      }

   }

   protected void stop(ServerLevel var1, PathfinderMob var2, long var3) {
      var2.getNavigation().stop();
      var2.stopUsingItem();
      var2.getBrain().eraseMemory(MemoryModuleType.SPEAR_CHARGE_POSITION);
      var2.getBrain().eraseMemory(MemoryModuleType.SPEAR_ENGAGE_TIME);
      var2.getBrain().setMemory(MemoryModuleType.SPEAR_STATUS, SpearAttack.SpearStatus.RETREAT);
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

   public static enum SpearStatus {
      APPROACH,
      CHARGING,
      RETREAT;

      private SpearStatus() {
      }

      // $FF: synthetic method
      private static SpearStatus[] $values() {
         return new SpearStatus[]{APPROACH, CHARGING, RETREAT};
      }
   }
}
