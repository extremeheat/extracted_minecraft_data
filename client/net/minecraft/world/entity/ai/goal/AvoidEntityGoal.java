package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class AvoidEntityGoal<T extends LivingEntity> extends Goal {
   protected final PathfinderMob mob;
   private final double walkSpeedModifier;
   private final double sprintSpeedModifier;
   @Nullable
   protected T toAvoid;
   protected final float maxDist;
   @Nullable
   protected Path path;
   protected final PathNavigation pathNav;
   protected final Class<T> avoidClass;
   protected final Predicate<LivingEntity> avoidPredicate;
   protected final Predicate<LivingEntity> predicateOnAvoidEntity;
   private final TargetingConditions avoidEntityTargeting;

   public AvoidEntityGoal(PathfinderMob var1, Class<T> var2, float var3, double var4, double var6) {
      this(var1, var2, var0 -> true, var3, var4, var6, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test);
   }

   public AvoidEntityGoal(PathfinderMob var1, Class<T> var2, Predicate<LivingEntity> var3, float var4, double var5, double var7, Predicate<LivingEntity> var9) {
      super();
      this.mob = var1;
      this.avoidClass = var2;
      this.avoidPredicate = var3;
      this.maxDist = var4;
      this.walkSpeedModifier = var5;
      this.sprintSpeedModifier = var7;
      this.predicateOnAvoidEntity = var9;
      this.pathNav = var1.getNavigation();
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      this.avoidEntityTargeting = TargetingConditions.forCombat().range((double)var4).selector((var2x, var3x) -> var9.test(var2x) && var3.test(var2x));
   }

   public AvoidEntityGoal(PathfinderMob var1, Class<T> var2, float var3, double var4, double var6, Predicate<LivingEntity> var8) {
      this(var1, var2, var0 -> true, var3, var4, var6, var8);
   }

   @Override
   public boolean canUse() {
      this.toAvoid = getServerLevel(this.mob)
         .getNearestEntity(
            this.mob
               .level()
               .getEntitiesOfClass(this.avoidClass, this.mob.getBoundingBox().inflate((double)this.maxDist, 3.0, (double)this.maxDist), var0 -> true),
            this.avoidEntityTargeting,
            this.mob,
            this.mob.getX(),
            this.mob.getY(),
            this.mob.getZ()
         );
      if (this.toAvoid == null) {
         return false;
      } else {
         Vec3 var1 = DefaultRandomPos.getPosAway(this.mob, 16, 7, this.toAvoid.position());
         if (var1 == null) {
            return false;
         } else if (this.toAvoid.distanceToSqr(var1.x, var1.y, var1.z) < this.toAvoid.distanceToSqr(this.mob)) {
            return false;
         } else {
            this.path = this.pathNav.createPath(var1.x, var1.y, var1.z, 0);
            return this.path != null;
         }
      }
   }

   @Override
   public boolean canContinueToUse() {
      return !this.pathNav.isDone();
   }

   @Override
   public void start() {
      this.pathNav.moveTo(this.path, this.walkSpeedModifier);
   }

   @Override
   public void stop() {
      this.toAvoid = null;
   }

   @Override
   public void tick() {
      if (this.mob.distanceToSqr(this.toAvoid) < 49.0) {
         this.mob.getNavigation().setSpeedModifier(this.sprintSpeedModifier);
      } else {
         this.mob.getNavigation().setSpeedModifier(this.walkSpeedModifier);
      }
   }
}
