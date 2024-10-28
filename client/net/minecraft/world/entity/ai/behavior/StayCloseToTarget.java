package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class StayCloseToTarget {
   public StayCloseToTarget() {
      super();
   }

   public static BehaviorControl<LivingEntity> create(Function<LivingEntity, Optional<PositionTracker>> var0, Predicate<LivingEntity> var1, int var2, int var3, float var4) {
      return BehaviorBuilder.create((var5) -> {
         return var5.group(var5.registered(MemoryModuleType.LOOK_TARGET), var5.registered(MemoryModuleType.WALK_TARGET)).apply(var5, (var5x, var6) -> {
            return (var7, var8, var9) -> {
               Optional var11 = (Optional)var0.apply(var8);
               if (!var11.isEmpty() && var1.test(var8)) {
                  PositionTracker var12 = (PositionTracker)var11.get();
                  if (var8.position().closerThan(var12.currentPosition(), (double)var3)) {
                     return false;
                  } else {
                     PositionTracker var13 = (PositionTracker)var11.get();
                     var5x.set(var13);
                     var6.set(new WalkTarget(var13, var4, var2));
                     return true;
                  }
               } else {
                  return false;
               }
            };
         });
      });
   }
}
