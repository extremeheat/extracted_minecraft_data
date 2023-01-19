package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class StayCloseToTarget {
   public StayCloseToTarget() {
      super();
   }

   public static BehaviorControl<LivingEntity> create(Function<LivingEntity, Optional<PositionTracker>> var0, int var1, int var2, float var3) {
      return BehaviorBuilder.create(
         var4 -> var4.group(var4.registered(MemoryModuleType.LOOK_TARGET), var4.absent(MemoryModuleType.WALK_TARGET))
               .apply(var4, (var4x, var5) -> (var6, var7, var8) -> {
                     Optional var10 = (Optional)var0.apply(var7);
                     if (var10.isEmpty()) {
                        return false;
                     } else {
                        PositionTracker var11 = (PositionTracker)var10.get();
                        if (var7.position().closerThan(var11.currentPosition(), (double)var2)) {
                           return false;
                        } else {
                           PositionTracker var12 = (PositionTracker)var10.get();
                           var4x.set(var12);
                           var5.set(new WalkTarget(var12, var3, var1));
                           return true;
                        }
                     }
                  })
      );
   }
}
