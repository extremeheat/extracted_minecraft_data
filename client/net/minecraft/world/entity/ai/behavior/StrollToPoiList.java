package net.minecraft.world.entity.ai.behavior;

import java.util.List;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import org.apache.commons.lang3.mutable.MutableLong;

public class StrollToPoiList {
   public StrollToPoiList() {
      super();
   }

   public static BehaviorControl<Villager> create(MemoryModuleType<List<GlobalPos>> var0, float var1, int var2, int var3, MemoryModuleType<GlobalPos> var4) {
      MutableLong var5 = new MutableLong(0L);
      return BehaviorBuilder.create((var6) -> {
         return var6.group(var6.registered(MemoryModuleType.WALK_TARGET), var6.present(var0), var6.present(var4)).apply(var6, (var5x, var6x, var7) -> {
            return (var8, var9, var10) -> {
               List var12 = (List)var6.get(var6x);
               GlobalPos var13 = (GlobalPos)var6.get(var7);
               if (var12.isEmpty()) {
                  return false;
               } else {
                  GlobalPos var14 = (GlobalPos)var12.get(var8.getRandom().nextInt(var12.size()));
                  if (var14 != null && var8.dimension() == var14.dimension() && var13.pos().closerToCenterThan(var9.position(), (double)var3)) {
                     if (var10 > var5.getValue()) {
                        var5x.set(new WalkTarget(var14.pos(), var1, var2));
                        var5.setValue(var10 + 100L);
                     }

                     return true;
                  } else {
                     return false;
                  }
               }
            };
         });
      });
   }
}
