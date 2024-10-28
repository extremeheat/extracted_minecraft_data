package net.minecraft.world.entity.monster.piglin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.item.ItemEntity;

public class StartAdmiringItemIfSeen {
   public StartAdmiringItemIfSeen() {
      super();
   }

   public static BehaviorControl<LivingEntity> create(int var0) {
      return BehaviorBuilder.create((var1) -> {
         return var1.group(var1.present(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM), var1.absent(MemoryModuleType.ADMIRING_ITEM), var1.absent(MemoryModuleType.ADMIRING_DISABLED), var1.absent(MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM)).apply(var1, (var2, var3, var4, var5) -> {
            return (var4x, var5x, var6) -> {
               ItemEntity var8 = (ItemEntity)var1.get(var2);
               if (!PiglinAi.isLovedItem(var8.getItem())) {
                  return false;
               } else {
                  var3.setWithExpiry(true, (long)var0);
                  return true;
               }
            };
         });
      });
   }
}
