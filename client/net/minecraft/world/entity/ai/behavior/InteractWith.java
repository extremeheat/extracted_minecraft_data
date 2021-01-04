package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class InteractWith<E extends LivingEntity, T extends LivingEntity> extends Behavior<E> {
   private final int maxDist;
   private final float speed;
   private final EntityType<? extends T> type;
   private final int interactionRangeSqr;
   private final Predicate<T> targetFilter;
   private final Predicate<E> selfFilter;
   private final MemoryModuleType<T> memory;

   public InteractWith(EntityType<? extends T> var1, int var2, Predicate<E> var3, Predicate<T> var4, MemoryModuleType<T> var5, float var6, int var7) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, var5, MemoryStatus.VALUE_ABSENT, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
      this.type = var1;
      this.speed = var6;
      this.interactionRangeSqr = var2 * var2;
      this.maxDist = var7;
      this.targetFilter = var4;
      this.selfFilter = var3;
      this.memory = var5;
   }

   public static <T extends LivingEntity> InteractWith<LivingEntity, T> of(EntityType<? extends T> var0, int var1, MemoryModuleType<T> var2, float var3, int var4) {
      return new InteractWith(var0, var1, (var0x) -> {
         return true;
      }, (var0x) -> {
         return true;
      }, var2, var3, var4);
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, E var2) {
      return this.selfFilter.test(var2) && ((List)var2.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).get()).stream().anyMatch((var1x) -> {
         return this.type.equals(var1x.getType()) && this.targetFilter.test(var1x);
      });
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      Brain var5 = var2.getBrain();
      var5.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).ifPresent((var3x) -> {
         var3x.stream().filter((var1) -> {
            return this.type.equals(var1.getType());
         }).map((var0) -> {
            return var0;
         }).filter((var2x) -> {
            return var2x.distanceToSqr(var2) <= (double)this.interactionRangeSqr;
         }).filter(this.targetFilter).findFirst().ifPresent((var2x) -> {
            var5.setMemory(this.memory, (Object)var2x);
            var5.setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new EntityPosWrapper(var2x)));
            var5.setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(new EntityPosWrapper(var2x), this.speed, this.maxDist)));
         });
      });
   }
}
