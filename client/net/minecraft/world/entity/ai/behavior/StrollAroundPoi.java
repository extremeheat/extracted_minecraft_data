package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import org.apache.commons.lang3.mutable.MutableLong;

public class StrollAroundPoi {
   private static final int MIN_TIME_BETWEEN_STROLLS = 180;
   private static final int STROLL_MAX_XZ_DIST = 8;
   private static final int STROLL_MAX_Y_DIST = 6;

   public StrollAroundPoi() {
      super();
   }

   public static OneShot<PathfinderMob> create(MemoryModuleType<GlobalPos> var0, float var1, int var2) {
      MutableLong var3 = new MutableLong(0L);
      return BehaviorBuilder.create((var4) -> {
         return var4.group(var4.registered(MemoryModuleType.WALK_TARGET), var4.present(var0)).apply(var4, (var4x, var5) -> {
            return (var6, var7, var8) -> {
               GlobalPos var10 = (GlobalPos)var4.get(var5);
               if (var6.dimension() == var10.dimension() && var10.pos().closerToCenterThan(var7.position(), (double)var2)) {
                  if (var8 <= var3.getValue()) {
                     return true;
                  } else {
                     Optional var11 = Optional.ofNullable(LandRandomPos.getPos(var7, 8, 6));
                     var4x.setOrErase(var11.map((var1x) -> {
                        return new WalkTarget(var1x, var1, 1);
                     }));
                     var3.setValue(var8 + 180L);
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
