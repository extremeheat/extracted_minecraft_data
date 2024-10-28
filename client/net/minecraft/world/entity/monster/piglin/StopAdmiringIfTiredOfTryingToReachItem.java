package net.minecraft.world.entity.monster.piglin;

import java.util.Optional;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class StopAdmiringIfTiredOfTryingToReachItem {
   public StopAdmiringIfTiredOfTryingToReachItem() {
      super();
   }

   public static BehaviorControl<LivingEntity> create(int var0, int var1) {
      return BehaviorBuilder.create((var2) -> {
         return var2.group(var2.present(MemoryModuleType.ADMIRING_ITEM), var2.present(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM), var2.registered(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM), var2.registered(MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM)).apply(var2, (var3, var4, var5, var6) -> {
            return (var6x, var7, var8) -> {
               if (!var7.getOffhandItem().isEmpty()) {
                  return false;
               } else {
                  Optional var10 = var2.tryGet(var5);
                  if (var10.isEmpty()) {
                     var5.set(0);
                  } else {
                     int var11 = (Integer)var10.get();
                     if (var11 > var0) {
                        var3.erase();
                        var5.erase();
                        var6.setWithExpiry(true, (long)var1);
                     } else {
                        var5.set(var11 + 1);
                     }
                  }

                  return true;
               }
            };
         });
      });
   }
}
