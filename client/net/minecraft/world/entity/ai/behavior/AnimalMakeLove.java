package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.animal.Animal;

public class AnimalMakeLove extends Behavior<Animal> {
   private static final int BREED_RANGE = 3;
   private static final int MIN_DURATION = 60;
   private static final int MAX_DURATION = 110;
   private final EntityType<? extends Animal> partnerType;
   private final float speedModifier;
   private long spawnChildAtTime;

   public AnimalMakeLove(EntityType<? extends Animal> var1, float var2) {
      super(ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT, MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED), 110);
      this.partnerType = var1;
      this.speedModifier = var2;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Animal var2) {
      return var2.isInLove() && this.findValidBreedPartner(var2).isPresent();
   }

   protected void start(ServerLevel var1, Animal var2, long var3) {
      Animal var5 = (Animal)this.findValidBreedPartner(var2).get();
      var2.getBrain().setMemory(MemoryModuleType.BREED_TARGET, (Object)var5);
      var5.getBrain().setMemory(MemoryModuleType.BREED_TARGET, (Object)var2);
      BehaviorUtils.lockGazeAndWalkToEachOther(var2, var5, this.speedModifier);
      int var6 = 60 + var2.getRandom().nextInt(50);
      this.spawnChildAtTime = var3 + (long)var6;
   }

   protected boolean canStillUse(ServerLevel var1, Animal var2, long var3) {
      if (!this.hasBreedTargetOfRightType(var2)) {
         return false;
      } else {
         Animal var5 = this.getBreedTarget(var2);
         return var5.isAlive() && var2.canMate(var5) && BehaviorUtils.entityIsVisible(var2.getBrain(), var5) && var3 <= this.spawnChildAtTime;
      }
   }

   protected void tick(ServerLevel var1, Animal var2, long var3) {
      Animal var5 = this.getBreedTarget(var2);
      BehaviorUtils.lockGazeAndWalkToEachOther(var2, var5, this.speedModifier);
      if (var2.closerThan(var5, 3.0D)) {
         if (var3 >= this.spawnChildAtTime) {
            var2.spawnChildFromBreeding(var1, var5);
            var2.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
            var5.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
         }

      }
   }

   protected void stop(ServerLevel var1, Animal var2, long var3) {
      var2.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
      var2.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
      var2.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
      this.spawnChildAtTime = 0L;
   }

   private Animal getBreedTarget(Animal var1) {
      return (Animal)var1.getBrain().getMemory(MemoryModuleType.BREED_TARGET).get();
   }

   private boolean hasBreedTargetOfRightType(Animal var1) {
      Brain var2 = var1.getBrain();
      return var2.hasMemoryValue(MemoryModuleType.BREED_TARGET) && ((AgeableMob)var2.getMemory(MemoryModuleType.BREED_TARGET).get()).getType() == this.partnerType;
   }

   private Optional<? extends Animal> findValidBreedPartner(Animal var1) {
      Optional var10000 = ((NearestVisibleLivingEntities)var1.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get()).findClosest((var2) -> {
         boolean var10000;
         if (var2.getType() == this.partnerType && var2 instanceof Animal) {
            Animal var3 = (Animal)var2;
            if (var1.canMate(var3)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      });
      Objects.requireNonNull(Animal.class);
      return var10000.map(Animal.class::cast);
   }

   // $FF: synthetic method
   protected void stop(ServerLevel var1, LivingEntity var2, long var3) {
      this.stop(var1, (Animal)var2, var3);
   }

   // $FF: synthetic method
   protected void tick(ServerLevel var1, LivingEntity var2, long var3) {
      this.tick(var1, (Animal)var2, var3);
   }
}
