package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.apache.commons.lang3.mutable.MutableInt;

public class SetHiddenState {
   private static final int HIDE_TIMEOUT = 300;

   public SetHiddenState() {
      super();
   }

   public static BehaviorControl<LivingEntity> create(int var0, int var1) {
      int var2 = var0 * 20;
      MutableInt var3 = new MutableInt(0);
      return BehaviorBuilder.create((var3x) -> {
         return var3x.group(var3x.present(MemoryModuleType.HIDING_PLACE), var3x.present(MemoryModuleType.HEARD_BELL_TIME)).apply(var3x, (var4, var5) -> {
            return (var6, var7, var8) -> {
               long var10 = (Long)var3x.get(var5);
               boolean var12 = var10 + 300L <= var8;
               if (var3.getValue() <= var2 && !var12) {
                  BlockPos var13 = ((GlobalPos)var3x.get(var4)).pos();
                  if (var13.closerThan(var7.blockPosition(), (double)var1)) {
                     var3.increment();
                  }

                  return true;
               } else {
                  var5.erase();
                  var4.erase();
                  var7.getBrain().updateActivityFromSchedule(var6.getDayTime(), var6.getGameTime());
                  var3.setValue(0);
                  return true;
               }
            };
         });
      });
   }
}
