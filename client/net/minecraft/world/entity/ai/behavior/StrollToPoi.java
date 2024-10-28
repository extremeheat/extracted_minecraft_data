package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import org.apache.commons.lang3.mutable.MutableLong;

public class StrollToPoi {
   public StrollToPoi() {
      super();
   }

   public static BehaviorControl<PathfinderMob> create(MemoryModuleType<GlobalPos> var0, float var1, int var2, int var3) {
      MutableLong var4 = new MutableLong(0L);
      return BehaviorBuilder.create((var5) -> {
         return var5.group(var5.registered(MemoryModuleType.WALK_TARGET), var5.present(var0)).apply(var5, (var5x, var6) -> {
            return (var7, var8, var9) -> {
               GlobalPos var11 = (GlobalPos)var5.get(var6);
               if (var7.dimension() == var11.dimension() && var11.pos().closerToCenterThan(var8.position(), (double)var3)) {
                  if (var9 <= var4.getValue()) {
                     return true;
                  } else {
                     var5x.set(new WalkTarget(var11.pos(), var1, var2));
                     var4.setValue(var9 + 80L);
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
