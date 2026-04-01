package net.minecraft.world.entity.ai.behavior;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class BehaviorUtils {
   private BehaviorUtils() {
      super();
   }

   public static void lockGazeAndWalkToEachOther(final LivingEntity entity1, final LivingEntity entity2, final float speedModifier, final int closeEnoughDistance) {
      lookAtEachOther(entity1, entity2);
      setWalkAndLookTargetMemoriesToEachOther(entity1, entity2, speedModifier, closeEnoughDistance);
   }

   public static boolean entityIsVisible(final Brain<?> brain, final LivingEntity targetEntity) {
      Optional<NearestVisibleLivingEntities> visibleEntities = brain.<NearestVisibleLivingEntities>getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
      return visibleEntities.isPresent() && ((NearestVisibleLivingEntities)visibleEntities.get()).contains(targetEntity);
   }

   public static boolean targetIsValid(final Brain<?> brain, final MemoryModuleType<? extends LivingEntity> memory, final EntityType<?> targetType) {
      return targetIsValid(brain, memory, (Predicate)((entity) -> entity.is(targetType)));
   }

   private static boolean targetIsValid(final Brain<?> brain, final MemoryModuleType<? extends LivingEntity> memory, final Predicate<LivingEntity> targetPredicate) {
      return brain.getMemory(memory).filter(targetPredicate).filter(LivingEntity::isAlive).filter((entity) -> entityIsVisible(brain, entity)).isPresent();
   }

   private static void lookAtEachOther(final LivingEntity entity1, final LivingEntity entity2) {
      lookAtEntity(entity1, entity2);
      lookAtEntity(entity2, entity1);
   }

   public static void lookAtEntity(final LivingEntity looker, final Entity targetEntity) {
      looker.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(targetEntity, true));
   }

   private static void setWalkAndLookTargetMemoriesToEachOther(final LivingEntity entity1, final LivingEntity entity2, final float speedModifier, final int closeEnoughDistance) {
      setWalkAndLookTargetMemories(entity1, (Entity)entity2, speedModifier, closeEnoughDistance);
      setWalkAndLookTargetMemories(entity2, (Entity)entity1, speedModifier, closeEnoughDistance);
   }

   public static void setWalkAndLookTargetMemories(final LivingEntity walker, final Entity targetEntity, final float speedModifier, final int closeEnoughDistance) {
      setWalkAndLookTargetMemories(walker, (PositionTracker)(new EntityTracker(targetEntity, true)), speedModifier, closeEnoughDistance);
   }

   public static void setWalkAndLookTargetMemories(final LivingEntity walker, final BlockPos targetPos, final float speedModifier, final int closeEnoughDistance) {
      setWalkAndLookTargetMemories(walker, (PositionTracker)(new BlockPosTracker(targetPos)), speedModifier, closeEnoughDistance);
   }

   public static void setWalkAndLookTargetMemories(final LivingEntity walker, final PositionTracker target, final float speedModifier, final int closeEnoughDistance) {
      WalkTarget walkTarget = new WalkTarget(target, speedModifier, closeEnoughDistance);
      walker.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, target);
      walker.getBrain().setMemory(MemoryModuleType.WALK_TARGET, walkTarget);
   }

   public static void throwItem(final LivingEntity thrower, final ItemStack item, final Vec3 targetPos) {
      Vec3 throwVelocity = new Vec3(0.30000001192092896, 0.30000001192092896, 0.30000001192092896);
      throwItem(thrower, item, targetPos, throwVelocity, 0.3F);
   }

   public static void throwItem(final LivingEntity thrower, final ItemStack item, final Vec3 targetPos, final Vec3 throwVelocity, final float handYDistanceFromEye) {
      double yHandPos = thrower.getEyeY() - (double)handYDistanceFromEye;
      LivingBlock itemEntity = LivingBlock.createAt(thrower.level(), BlockPos.containing(thrower.getX(), yHandPos, thrower.getZ()), item);
      Vec3 throwVector = targetPos.subtract(thrower.position());
      throwVector = throwVector.normalize().multiply(throwVelocity.x, throwVelocity.y, throwVelocity.z);
      itemEntity.setDeltaMovement(throwVector);
      thrower.level().addFreshEntity(itemEntity);
   }

   public static SectionPos findSectionClosestToVillage(final ServerLevel level, final SectionPos center, final int radius) {
      int distToVillage = level.sectionsToVillage(center);
      Stream var10000 = SectionPos.cube(center, radius).filter((s) -> level.sectionsToVillage(s) < distToVillage);
      Objects.requireNonNull(level);
      return (SectionPos)var10000.min(Comparator.comparingInt(level::sectionsToVillage)).orElse(center);
   }

   public static boolean isWithinAttackRange(final Mob body, final Entity target, final int projectileAttackRangeMargin) {
      Item var4 = body.getMainHandItem().getItem();
      if (var4 instanceof ProjectileWeaponItem weapon) {
         if (body.canUseNonMeleeWeapon(body.getMainHandItem())) {
            int maxAllowedDistance = weapon.getDefaultProjectileRange() - projectileAttackRangeMargin;
            return body.closerThan(target, (double)maxAllowedDistance);
         }
      }

      return body.isWithinMeleeAttackRange(target);
   }

   public static boolean isOtherTargetMuchFurtherAwayThanCurrentAttackTarget(final LivingEntity body, final Entity otherTarget, final double howMuchFurtherAway) {
      Optional<Entity> currentTarget = body.getBrain().<Entity>getMemory(MemoryModuleType.ATTACK_TARGET);
      if (currentTarget.isEmpty()) {
         return false;
      } else {
         double distSqrToCurrentTarget = body.distanceToSqr(((Entity)currentTarget.get()).position());
         double distSqrToOtherTarget = body.distanceToSqr(otherTarget.position());
         return distSqrToOtherTarget > distSqrToCurrentTarget + howMuchFurtherAway * howMuchFurtherAway;
      }
   }

   public static boolean canSee(final LivingEntity body, final Entity target) {
      Brain<?> brain = body.getBrain();
      return !brain.hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES) ? false : ((NearestVisibleLivingEntities)brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get()).contains(target);
   }

   public static Entity getNearestTarget(final LivingEntity body, final Optional<Entity> target1, final Entity target2) {
      return target1.isEmpty() ? target2 : getTargetNearestMe(body, (Entity)target1.get(), target2);
   }

   public static Entity getTargetNearestMe(final LivingEntity body, final Entity target1, final Entity target2) {
      Vec3 pos1 = target1.position();
      Vec3 pos2 = target2.position();
      return body.distanceToSqr(pos1) < body.distanceToSqr(pos2) ? target1 : target2;
   }

   public static Optional<Entity> getLivingEntityFromUUIDMemory(final LivingEntity body, final MemoryModuleType<UUID> memoryType) {
      Optional<UUID> uuidMemory = body.getBrain().<UUID>getMemory(memoryType);
      return uuidMemory.map((uuid) -> body.level().getEntity(uuid));
   }

   public static @Nullable Vec3 getRandomSwimmablePos(final PathfinderMob body, final int maxHorizontalDistance, final int maxVerticalDistance) {
      Vec3 targetPos = DefaultRandomPos.getPos(body, maxHorizontalDistance, maxVerticalDistance);

      for(int count = 0; targetPos != null && !body.level().getBlockState(BlockPos.containing(targetPos)).isPathfindable(PathComputationType.WATER) && count++ < 10; targetPos = DefaultRandomPos.getPos(body, maxHorizontalDistance, maxVerticalDistance)) {
      }

      return targetPos;
   }

   public static boolean isBreeding(final LivingEntity body) {
      return body.getBrain().hasMemoryValue(MemoryModuleType.BREED_TARGET);
   }
}
