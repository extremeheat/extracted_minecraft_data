package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class SetEntityLookTarget extends Behavior<LivingEntity> {
   private final Predicate<LivingEntity> predicate;
   private final float maxDistSqr;
   private Optional<LivingEntity> nearestEntityMatchingTest = Optional.empty();

   public SetEntityLookTarget(TagKey<EntityType<?>> var1, float var2) {
      this(var1x -> var1x.getType().is(var1), var2);
   }

   public SetEntityLookTarget(MobCategory var1, float var2) {
      this(var1x -> var1.equals(var1x.getType().getCategory()), var2);
   }

   public SetEntityLookTarget(EntityType<?> var1, float var2) {
      this(var1x -> var1.equals(var1x.getType()), var2);
   }

   public SetEntityLookTarget(float var1) {
      this(var0 -> true, var1);
   }

   public SetEntityLookTarget(Predicate<LivingEntity> var1, float var2) {
      super(
         ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT)
      );
      this.predicate = var1;
      this.maxDistSqr = var2 * var2;
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel var1, LivingEntity var2) {
      NearestVisibleLivingEntities var3 = var2.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get();
      this.nearestEntityMatchingTest = var3.findClosest(this.predicate.and(var2x -> var2x.distanceToSqr(var2) <= (double)this.maxDistSqr));
      return this.nearestEntityMatchingTest.isPresent();
   }

   @Override
   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      var2.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(this.nearestEntityMatchingTest.get(), true));
      this.nearestEntityMatchingTest = Optional.empty();
   }
}
