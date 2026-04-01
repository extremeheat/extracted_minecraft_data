package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class AvoidEntityGoal<T extends LivingEntity> extends Goal {
   protected final PathfinderMob mob;
   private final double walkSpeedModifier;
   private final double sprintSpeedModifier;
   protected @Nullable T toAvoid;
   protected final float maxDist;
   protected @Nullable Path path;
   protected final PathNavigation pathNav;
   protected final Class<T> avoidClass;
   protected final Predicate<? super LivingEntity> avoidPredicate;
   protected final Predicate<? super LivingEntity> predicateOnAvoidEntity;
   private final TargetingConditions avoidEntityTargeting;

   public AvoidEntityGoal(final PathfinderMob mob, final Class<T> avoidClass, final float maxDist, final double walkSpeedModifier, final double sprintSpeedModifier) {
      this(mob, avoidClass, (t) -> true, maxDist, walkSpeedModifier, sprintSpeedModifier, EntitySelector.NO_CREATIVE_OR_SPECTATOR);
   }

   public AvoidEntityGoal(final PathfinderMob mob, final Class<T> avoidClass, final Predicate<Entity> avoidPredicate, final float maxDist, final double walkSpeedModifier, final double sprintSpeedModifier, final Predicate<? super Entity> predicateOnAvoidEntity) {
      super();
      this.mob = mob;
      this.avoidClass = avoidClass;
      this.avoidPredicate = avoidPredicate;
      this.maxDist = maxDist;
      this.walkSpeedModifier = walkSpeedModifier;
      this.sprintSpeedModifier = sprintSpeedModifier;
      this.predicateOnAvoidEntity = predicateOnAvoidEntity;
      this.pathNav = mob.getNavigation();
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      this.avoidEntityTargeting = TargetingConditions.forCombat().range((double)maxDist).selector((target, level) -> predicateOnAvoidEntity.test(target) && avoidPredicate.test(target));
   }

   public AvoidEntityGoal(final PathfinderMob mob, final Class<T> avoidClass, final float maxDist, final double walkSpeedModifier, final double sprintSpeedModifier, final Predicate<? super Entity> predicateOnAvoidEntity) {
      this(mob, avoidClass, (t) -> true, maxDist, walkSpeedModifier, sprintSpeedModifier, predicateOnAvoidEntity);
   }

   public boolean canUse() {
      this.toAvoid = (T)(getServerLevel(this.mob).getNearestEntity(this.mob.level().getEntitiesOfClass(this.avoidClass, this.mob.getBoundingBox().inflate((double)this.maxDist, 3.0, (double)this.maxDist), (entity) -> true), this.avoidEntityTargeting, this.mob, this.mob.getX(), this.mob.getY(), this.mob.getZ()));
      if (this.toAvoid == null) {
         return false;
      } else {
         Vec3 pos = DefaultRandomPos.getPosAway(this.mob, 16, 7, this.toAvoid.position());
         if (pos == null) {
            return false;
         } else if (this.toAvoid.distanceToSqr(pos.x, pos.y, pos.z) < this.toAvoid.distanceToSqr(this.mob)) {
            return false;
         } else {
            this.path = this.pathNav.createPath(pos.x, pos.y, pos.z, 0);
            return this.path != null;
         }
      }
   }

   public boolean canContinueToUse() {
      return !this.pathNav.isDone();
   }

   public void start() {
      this.pathNav.moveTo(this.path, this.walkSpeedModifier);
   }

   public void stop() {
      this.toAvoid = null;
   }

   public void tick() {
      if (this.mob.distanceToSqr(this.toAvoid) < 49.0) {
         this.mob.getNavigation().setSpeedModifier(this.sprintSpeedModifier);
      } else {
         this.mob.getNavigation().setSpeedModifier(this.walkSpeedModifier);
      }

   }
}
