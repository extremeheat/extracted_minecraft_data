package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class InteractWith {
   public InteractWith() {
      super();
   }

   public static <T extends LivingEntity> BehaviorControl<LivingEntity> of(EntityType<? extends T> var0, int var1, MemoryModuleType<T> var2, float var3, int var4) {
      return of(var0, var1, (var0x) -> {
         return true;
      }, (var0x) -> {
         return true;
      }, var2, var3, var4);
   }

   public static <E extends LivingEntity, T extends LivingEntity> BehaviorControl<E> of(EntityType<? extends T> var0, int var1, Predicate<E> var2, Predicate<T> var3, MemoryModuleType<T> var4, float var5, int var6) {
      int var7 = var1 * var1;
      Predicate var8 = (var2x) -> {
         return var0.equals(var2x.getType()) && var3.test(var2x);
      };
      return BehaviorBuilder.create((var6x) -> {
         return var6x.group(var6x.registered(var4), var6x.registered(MemoryModuleType.LOOK_TARGET), var6x.absent(MemoryModuleType.WALK_TARGET), var6x.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)).apply(var6x, (var6xx, var7x, var8x, var9) -> {
            return (var10, var11, var12) -> {
               NearestVisibleLivingEntities var14 = (NearestVisibleLivingEntities)var6x.get(var9);
               if (var2.test(var11) && var14.contains(var8)) {
                  Optional var15 = var14.findClosest((var3) -> {
                     return var3.distanceToSqr(var11) <= (double)var7 && var8.test(var3);
                  });
                  var15.ifPresent((var5x) -> {
                     var6xx.set(var5x);
                     var7x.set(new EntityTracker(var5x, true));
                     var8x.set(new WalkTarget(new EntityTracker(var5x, false), var5, var6));
                  });
                  return true;
               } else {
                  return false;
               }
            };
         });
      });
   }
}
