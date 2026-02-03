package net.minecraft.world.entity.ai.behavior;

import java.util.function.Function;
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

   public static BehaviorControl<LivingEntity> create(final int seconds, final int closeEnoughDist) {
      int stayHiddenTicks = seconds * 20;
      MutableInt ticksHidden = new MutableInt(0);
      return BehaviorBuilder.create((Function)((i) -> i.group(i.present(MemoryModuleType.HIDING_PLACE), i.present(MemoryModuleType.HEARD_BELL_TIME)).apply(i, (hidingPlace, heardBellTime) -> (level, body, timestamp) -> {
               long timeTriggered = (Long)i.get(heardBellTime);
               boolean timedOutTryingToHide = timeTriggered + 300L <= timestamp;
               if (ticksHidden.intValue() <= stayHiddenTicks && !timedOutTryingToHide) {
                  BlockPos hidePos = ((GlobalPos)i.get(hidingPlace)).pos();
                  if (hidePos.closerThan(body.blockPosition(), (double)closeEnoughDist)) {
                     ticksHidden.increment();
                  }

                  return true;
               } else {
                  heardBellTime.erase();
                  hidingPlace.erase();
                  body.getBrain().updateActivityFromSchedule(level.environmentAttributes(), level.getGameTime(), body.position());
                  ticksHidden.setValue(0);
                  return true;
               }
            })));
   }
}
