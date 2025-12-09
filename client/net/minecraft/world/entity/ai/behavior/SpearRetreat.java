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
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class SpearRetreat extends Behavior<PathfinderMob> {
   public static final int MIN_COOLDOWN_DISTANCE = 9;
   public static final int MAX_COOLDOWN_DISTANCE = 11;
   public static final int MAX_FLEEING_TIME = 100;
   double speedModifierWhenRepositioning;

   public SpearRetreat(double var1) {
      super(Map.of(MemoryModuleType.SPEAR_STATUS, MemoryStatus.VALUE_PRESENT), 100);
      this.speedModifierWhenRepositioning = var1;
   }

   private @Nullable LivingEntity getTarget(PathfinderMob var1) {
      return (LivingEntity)var1.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse((Object)null);
   }

   private boolean ableToAttack(PathfinderMob var1) {
      return this.getTarget(var1) != null && var1.getMainHandItem().has(DataComponents.KINETIC_WEAPON);
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, PathfinderMob var2) {
      if (this.ableToAttack(var2) && !var2.isUsingItem()) {
         if (var2.getBrain().getMemory(MemoryModuleType.SPEAR_STATUS).orElse(SpearAttack.SpearStatus.APPROACH) != SpearAttack.SpearStatus.RETREAT) {
            return false;
         } else {
            LivingEntity var3 = this.getTarget(var2);
            double var4 = var2.distanceToSqr(var3.getX(), var3.getY(), var3.getZ());
            int var6 = var2.isPassenger() ? 2 : 0;
            double var7 = Math.sqrt(var4);
            Vec3 var9 = LandRandomPos.getPosAway(var2, Math.max(0.0, (double)(9 + var6) - var7), Math.max(1.0, (double)(11 + var6) - var7), 7, var3.position());
            if (var9 == null) {
               return false;
            } else {
               var2.getBrain().setMemory(MemoryModuleType.SPEAR_FLEEING_POSITION, var9);
               return true;
            }
         }
      } else {
         return false;
      }
   }

   protected void start(ServerLevel var1, PathfinderMob var2, long var3) {
      var2.setAggressive(true);
      var2.getBrain().setMemory(MemoryModuleType.SPEAR_FLEEING_TIME, 0);
      super.start(var1, var2, var3);
   }

   protected boolean canStillUse(ServerLevel var1, PathfinderMob var2, long var3) {
      return (Integer)var2.getBrain().getMemory(MemoryModuleType.SPEAR_FLEEING_TIME).orElse(100) < 100 && var2.getBrain().getMemory(MemoryModuleType.SPEAR_FLEEING_POSITION).isPresent() && !var2.getNavigation().isDone() && this.ableToAttack(var2);
   }

   protected void tick(ServerLevel var1, PathfinderMob var2, long var3) {
      LivingEntity var5 = this.getTarget(var2);
      Entity var6 = var2.getRootVehicle();
      float var10000;
      if (var6 instanceof Mob var8) {
         var10000 = var8.chargeSpeedModifier();
      } else {
         var10000 = 1.0F;
      }

      float var7 = var10000;
      var2.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(var5, true));
      var2.getBrain().setMemory(MemoryModuleType.SPEAR_FLEEING_TIME, (Integer)var2.getBrain().getMemory(MemoryModuleType.SPEAR_FLEEING_TIME).orElse(0) + 1);
      var2.getBrain().getMemory(MemoryModuleType.SPEAR_FLEEING_POSITION).ifPresent((var3x) -> var2.getNavigation().moveTo(var3x.x, var3x.y, var3x.z, (double)var7 * this.speedModifierWhenRepositioning));
   }

   protected void stop(ServerLevel var1, PathfinderMob var2, long var3) {
      var2.getNavigation().stop();
      var2.setAggressive(false);
      var2.stopUsingItem();
      var2.getBrain().eraseMemory(MemoryModuleType.SPEAR_FLEEING_TIME);
      var2.getBrain().eraseMemory(MemoryModuleType.SPEAR_FLEEING_POSITION);
      var2.getBrain().eraseMemory(MemoryModuleType.SPEAR_STATUS);
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
