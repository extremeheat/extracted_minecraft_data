package net.minecraft.world.entity.ai.behavior;

import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class SetWalkTargetFromLookTarget {
   public SetWalkTargetFromLookTarget() {
      super();
   }

   public static OneShot<LivingEntity> create(float var0, int var1) {
      return create((var0x) -> {
         return true;
      }, (var1x) -> {
         return var0;
      }, var1);
   }

   public static OneShot<LivingEntity> create(Predicate<LivingEntity> var0, Function<LivingEntity, Float> var1, int var2) {
      return BehaviorBuilder.create((var3) -> {
         return var3.group(var3.absent(MemoryModuleType.WALK_TARGET), var3.present(MemoryModuleType.LOOK_TARGET)).apply(var3, (var4, var5) -> {
            return (var6, var7, var8) -> {
               if (!var0.test(var7)) {
                  return false;
               } else {
                  var4.set(new WalkTarget((PositionTracker)var3.get(var5), (Float)var1.apply(var7), var2));
                  return true;
               }
            };
         });
      });
   }
}
