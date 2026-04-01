package net.minecraft.world.entity.ai.memory;

import com.google.common.collect.Iterables;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.sensing.Sensor;

public class NearestVisibleLivingEntities {
   private static final NearestVisibleLivingEntities EMPTY = new NearestVisibleLivingEntities();
   private final List<Entity> nearbyEntities;
   private final Predicate<Entity> lineOfSightTest;

   private NearestVisibleLivingEntities() {
      super();
      this.nearbyEntities = List.of();
      this.lineOfSightTest = (ignored) -> false;
   }

   public NearestVisibleLivingEntities(final ServerLevel level, final Entity body, final List<Entity> livingEntities) {
      super();
      this.nearbyEntities = livingEntities;
      Object2BooleanOpenHashMap<Entity> cache = new Object2BooleanOpenHashMap(livingEntities.size());
      Predicate<Entity> targetTest = (targetEntity) -> Sensor.isEntityTargetable(level, body, targetEntity);
      this.lineOfSightTest = (otherEntity) -> cache.computeIfAbsent(otherEntity, targetTest);
   }

   public static NearestVisibleLivingEntities empty() {
      return EMPTY;
   }

   @VisibleForDebug
   public List<Entity> nearbyEntities() {
      return this.nearbyEntities;
   }

   public Optional<Entity> findClosest(final Predicate<Entity> filter) {
      for(Entity nearbyEntity : this.nearbyEntities) {
         if (filter.test(nearbyEntity) && this.lineOfSightTest.test(nearbyEntity)) {
            return Optional.of(nearbyEntity);
         }
      }

      return Optional.empty();
   }

   public Optional<Entity> findClosestMatchingLivingEntityPredicate(final Predicate<LivingEntity> filter) {
      for(Entity nearbyEntity : this.nearbyEntities) {
         if (nearbyEntity instanceof LivingEntity nearbyLivingEntity) {
            if (filter.test(nearbyLivingEntity) && this.lineOfSightTest.test(nearbyLivingEntity)) {
               return Optional.of(nearbyLivingEntity);
            }
         }
      }

      return Optional.empty();
   }

   public Iterable<Entity> findAll(final Predicate<Entity> filter) {
      return Iterables.filter(this.nearbyEntities, (entity) -> filter.test(entity) && this.lineOfSightTest.test(entity));
   }

   public Iterable<Entity> findAllEntitiesMatchingLivingEntityPredicate(final Predicate<LivingEntity> filter) {
      return Iterables.filter(this.nearbyEntities, (entity) -> {
         if (!(entity instanceof LivingEntity livingEntity)) {
            return false;
         } else {
            return filter.test(livingEntity) && this.lineOfSightTest.test(livingEntity);
         }
      });
   }

   public Stream<Entity> find(final Predicate<Entity> filter) {
      return this.nearbyEntities.stream().filter((entity) -> filter.test(entity) && this.lineOfSightTest.test(entity));
   }

   public boolean contains(final Entity targetEntity) {
      return this.nearbyEntities.contains(targetEntity) && this.lineOfSightTest.test(targetEntity);
   }

   public boolean contains(final Predicate<Entity> filter) {
      for(Entity nearbyEntity : this.nearbyEntities) {
         if (filter.test(nearbyEntity) && this.lineOfSightTest.test(nearbyEntity)) {
            return true;
         }
      }

      return false;
   }
}
