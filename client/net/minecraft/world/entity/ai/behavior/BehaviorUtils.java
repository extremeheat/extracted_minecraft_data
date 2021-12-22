package net.minecraft.world.entity.ai.behavior;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;

public class BehaviorUtils {
   private BehaviorUtils() {
      super();
   }

   public static void lockGazeAndWalkToEachOther(LivingEntity var0, LivingEntity var1, float var2) {
      lookAtEachOther(var0, var1);
      setWalkAndLookTargetMemoriesToEachOther(var0, var1, var2);
   }

   public static boolean entityIsVisible(Brain<?> var0, LivingEntity var1) {
      Optional var2 = var0.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
      return var2.isPresent() && ((NearestVisibleLivingEntities)var2.get()).contains(var1);
   }

   public static boolean targetIsValid(Brain<?> var0, MemoryModuleType<? extends LivingEntity> var1, EntityType<?> var2) {
      return targetIsValid(var0, var1, (var1x) -> {
         return var1x.getType() == var2;
      });
   }

   private static boolean targetIsValid(Brain<?> var0, MemoryModuleType<? extends LivingEntity> var1, Predicate<LivingEntity> var2) {
      return var0.getMemory(var1).filter(var2).filter(LivingEntity::isAlive).filter((var1x) -> {
         return entityIsVisible(var0, var1x);
      }).isPresent();
   }

   private static void lookAtEachOther(LivingEntity var0, LivingEntity var1) {
      lookAtEntity(var0, var1);
      lookAtEntity(var1, var0);
   }

   public static void lookAtEntity(LivingEntity var0, LivingEntity var1) {
      var0.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new EntityTracker(var1, true)));
   }

   private static void setWalkAndLookTargetMemoriesToEachOther(LivingEntity var0, LivingEntity var1, float var2) {
      boolean var3 = true;
      setWalkAndLookTargetMemories(var0, (Entity)var1, var2, 2);
      setWalkAndLookTargetMemories(var1, (Entity)var0, var2, 2);
   }

   public static void setWalkAndLookTargetMemories(LivingEntity var0, Entity var1, float var2, int var3) {
      WalkTarget var4 = new WalkTarget(new EntityTracker(var1, false), var2, var3);
      var0.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new EntityTracker(var1, true)));
      var0.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)var4);
   }

   public static void setWalkAndLookTargetMemories(LivingEntity var0, BlockPos var1, float var2, int var3) {
      WalkTarget var4 = new WalkTarget(new BlockPosTracker(var1), var2, var3);
      var0.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new BlockPosTracker(var1)));
      var0.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)var4);
   }

   public static void throwItem(LivingEntity var0, ItemStack var1, Vec3 var2) {
      double var3 = var0.getEyeY() - 0.30000001192092896D;
      ItemEntity var5 = new ItemEntity(var0.level, var0.getX(), var3, var0.getZ(), var1);
      float var6 = 0.3F;
      Vec3 var7 = var2.subtract(var0.position());
      var7 = var7.normalize().scale(0.30000001192092896D);
      var5.setDeltaMovement(var7);
      var5.setDefaultPickUpDelay();
      var0.level.addFreshEntity(var5);
   }

   public static SectionPos findSectionClosestToVillage(ServerLevel var0, SectionPos var1, int var2) {
      int var3 = var0.sectionsToVillage(var1);
      Stream var10000 = SectionPos.cube(var1, var2).filter((var2x) -> {
         return var0.sectionsToVillage(var2x) < var3;
      });
      Objects.requireNonNull(var0);
      return (SectionPos)var10000.min(Comparator.comparingInt(var0::sectionsToVillage)).orElse(var1);
   }

   public static boolean isWithinAttackRange(Mob var0, LivingEntity var1, int var2) {
      Item var3 = var0.getMainHandItem().getItem();
      if (var3 instanceof ProjectileWeaponItem) {
         ProjectileWeaponItem var4 = (ProjectileWeaponItem)var3;
         if (var0.canFireProjectileWeapon((ProjectileWeaponItem)var3)) {
            int var5 = var4.getDefaultProjectileRange() - var2;
            return var0.closerThan(var1, (double)var5);
         }
      }

      return isWithinMeleeAttackRange(var0, var1);
   }

   public static boolean isWithinMeleeAttackRange(Mob var0, LivingEntity var1) {
      double var2 = var0.distanceToSqr(var1.getX(), var1.getY(), var1.getZ());
      return var2 <= var0.getMeleeAttackRangeSqr(var1);
   }

   public static boolean isOtherTargetMuchFurtherAwayThanCurrentAttackTarget(LivingEntity var0, LivingEntity var1, double var2) {
      Optional var4 = var0.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
      if (var4.isEmpty()) {
         return false;
      } else {
         double var5 = var0.distanceToSqr(((LivingEntity)var4.get()).position());
         double var7 = var0.distanceToSqr(var1.position());
         return var7 > var5 + var2 * var2;
      }
   }

   public static boolean canSee(LivingEntity var0, LivingEntity var1) {
      Brain var2 = var0.getBrain();
      return !var2.hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES) ? false : ((NearestVisibleLivingEntities)var2.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get()).contains(var1);
   }

   public static LivingEntity getNearestTarget(LivingEntity var0, Optional<LivingEntity> var1, LivingEntity var2) {
      return var1.isEmpty() ? var2 : getTargetNearestMe(var0, (LivingEntity)var1.get(), var2);
   }

   public static LivingEntity getTargetNearestMe(LivingEntity var0, LivingEntity var1, LivingEntity var2) {
      Vec3 var3 = var1.position();
      Vec3 var4 = var2.position();
      return var0.distanceToSqr(var3) < var0.distanceToSqr(var4) ? var1 : var2;
   }

   public static Optional<LivingEntity> getLivingEntityFromUUIDMemory(LivingEntity var0, MemoryModuleType<UUID> var1) {
      Optional var2 = var0.getBrain().getMemory(var1);
      return var2.map((var1x) -> {
         return ((ServerLevel)var0.level).getEntity(var1x);
      }).map((var0x) -> {
         LivingEntity var10000;
         if (var0x instanceof LivingEntity) {
            LivingEntity var1 = (LivingEntity)var0x;
            var10000 = var1;
         } else {
            var10000 = null;
         }

         return var10000;
      });
   }

   public static Stream<Villager> getNearbyVillagersWithCondition(Villager var0, Predicate<Villager> var1) {
      return (Stream)var0.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).map((var2) -> {
         return var2.stream().filter((var1x) -> {
            return var1x instanceof Villager && var1x != var0;
         }).map((var0x) -> {
            return (Villager)var0x;
         }).filter(LivingEntity::isAlive).filter(var1);
      }).orElseGet(Stream::empty);
   }

   @Nullable
   public static Vec3 getRandomSwimmablePos(PathfinderMob var0, int var1, int var2) {
      Vec3 var3 = DefaultRandomPos.getPos(var0, var1, var2);

      for(int var4 = 0; var3 != null && !var0.level.getBlockState(new BlockPos(var3)).isPathfindable(var0.level, new BlockPos(var3), PathComputationType.WATER) && var4++ < 10; var3 = DefaultRandomPos.getPos(var0, var1, var2)) {
      }

      return var3;
   }
}
