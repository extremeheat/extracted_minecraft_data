package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

public class SetWalkTargetAwayFrom {
   public SetWalkTargetAwayFrom() {
      super();
   }

   public static BehaviorControl<PathfinderMob> pos(MemoryModuleType<BlockPos> var0, float var1, int var2, boolean var3) {
      return create(var0, var1, var2, var3, Vec3::atBottomCenterOf);
   }

   public static OneShot<PathfinderMob> entity(MemoryModuleType<? extends Entity> var0, float var1, int var2, boolean var3) {
      return create(var0, var1, var2, var3, Entity::position);
   }

   private static <T> OneShot<PathfinderMob> create(MemoryModuleType<T> var0, float var1, int var2, boolean var3, Function<T, Vec3> var4) {
      return BehaviorBuilder.create(
         var5 -> var5.group(var5.registered(MemoryModuleType.WALK_TARGET), var5.present(var0)).apply(var5, (var5x, var6) -> (var7, var8, var9) -> {
                  Optional var11 = var5.tryGet(var5x);
                  if (var11.isPresent() && !var3) {
                     return false;
                  } else {
                     Vec3 var12 = var8.position();
                     Vec3 var13 = (Vec3)var4.apply(var5.get(var6));
                     if (!var12.closerThan(var13, (double)var2)) {
                        return false;
                     } else {
                        if (var11.isPresent() && ((WalkTarget)var11.get()).getSpeedModifier() == var1) {
                           Vec3 var14 = ((WalkTarget)var11.get()).getTarget().currentPosition().subtract(var12);
                           Vec3 var15 = var13.subtract(var12);
                           if (var14.dot(var15) < 0.0) {
                              return false;
                           }
                        }

                        for (int var16 = 0; var16 < 10; var16++) {
                           Vec3 var17 = LandRandomPos.getPosAway(var8, 16, 7, var13);
                           if (var17 != null) {
                              var5x.set(new WalkTarget(var17, var1, 0));
                              break;
                           }
                        }

                        return true;
                     }
                  }
               })
      );
   }
}
