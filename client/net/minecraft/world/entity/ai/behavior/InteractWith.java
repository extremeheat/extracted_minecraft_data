package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class InteractWith<E extends LivingEntity, T extends LivingEntity> extends Behavior<E> {
   private final int maxDist;
   private final float speedModifier;
   private final EntityType<? extends T> type;
   private final int interactionRangeSqr;
   private final Predicate<T> targetFilter;
   private final Predicate<E> selfFilter;
   private final MemoryModuleType<T> memory;

   public InteractWith(EntityType<? extends T> var1, int var2, Predicate<E> var3, Predicate<T> var4, MemoryModuleType<T> var5, float var6, int var7) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
      this.type = var1;
      this.speedModifier = var6;
      this.interactionRangeSqr = var2 * var2;
      this.maxDist = var7;
      this.targetFilter = var4;
      this.selfFilter = var3;
      this.memory = var5;
   }

   // $FF: renamed from: of (net.minecraft.world.entity.EntityType, int, net.minecraft.world.entity.ai.memory.MemoryModuleType, float, int) net.minecraft.world.entity.ai.behavior.InteractWith
   public static <T extends LivingEntity> InteractWith<LivingEntity, T> method_15(EntityType<? extends T> var0, int var1, MemoryModuleType<T> var2, float var3, int var4) {
      return new InteractWith(var0, var1, (var0x) -> {
         return true;
      }, (var0x) -> {
         return true;
      }, var2, var3, var4);
   }

   // $FF: renamed from: of (net.minecraft.world.entity.EntityType, int, java.util.function.Predicate, net.minecraft.world.entity.ai.memory.MemoryModuleType, float, int) net.minecraft.world.entity.ai.behavior.InteractWith
   public static <T extends LivingEntity> InteractWith<LivingEntity, T> method_16(EntityType<? extends T> var0, int var1, Predicate<T> var2, MemoryModuleType<T> var3, float var4, int var5) {
      return new InteractWith(var0, var1, (var0x) -> {
         return true;
      }, var2, var3, var4, var5);
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, E var2) {
      return this.selfFilter.test(var2) && this.seesAtLeastOneValidTarget(var2);
   }

   private boolean seesAtLeastOneValidTarget(E var1) {
      NearestVisibleLivingEntities var2 = (NearestVisibleLivingEntities)var1.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get();
      return var2.contains(this::isTargetValid);
   }

   private boolean isTargetValid(LivingEntity var1) {
      return this.type.equals(var1.getType()) && this.targetFilter.test(var1);
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      Brain var5 = var2.getBrain();
      Optional var6 = var5.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
      if (!var6.isEmpty()) {
         NearestVisibleLivingEntities var7 = (NearestVisibleLivingEntities)var6.get();
         var7.findClosest((var2x) -> {
            return this.canInteract(var2, var2x);
         }).ifPresent((var2x) -> {
            var5.setMemory(this.memory, (Object)var2x);
            var5.setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new EntityTracker(var2x, true)));
            var5.setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(new EntityTracker(var2x, false), this.speedModifier, this.maxDist)));
         });
      }
   }

   private boolean canInteract(E var1, LivingEntity var2) {
      return this.type.equals(var2.getType()) && var2.distanceToSqr(var1) <= (double)this.interactionRangeSqr && this.targetFilter.test(var2);
   }
}
