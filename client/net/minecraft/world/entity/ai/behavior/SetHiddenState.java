package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class SetHiddenState extends Behavior<LivingEntity> {
   private static final int HIDE_TIMEOUT = 300;
   private final int closeEnoughDist;
   private final int stayHiddenTicks;
   private int ticksHidden;

   public SetHiddenState(int var1, int var2) {
      super(ImmutableMap.of(MemoryModuleType.HIDING_PLACE, MemoryStatus.VALUE_PRESENT, MemoryModuleType.HEARD_BELL_TIME, MemoryStatus.VALUE_PRESENT));
      this.stayHiddenTicks = var1 * 20;
      this.ticksHidden = 0;
      this.closeEnoughDist = var2;
   }

   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      Brain var5 = var2.getBrain();
      Optional var6 = var5.getMemory(MemoryModuleType.HEARD_BELL_TIME);
      boolean var7 = (Long)var6.get() + 300L <= var3;
      if (this.ticksHidden <= this.stayHiddenTicks && !var7) {
         BlockPos var8 = ((GlobalPos)var5.getMemory(MemoryModuleType.HIDING_PLACE).get()).pos();
         if (var8.closerThan(var2.blockPosition(), (double)this.closeEnoughDist)) {
            ++this.ticksHidden;
         }

      } else {
         var5.eraseMemory(MemoryModuleType.HEARD_BELL_TIME);
         var5.eraseMemory(MemoryModuleType.HIDING_PLACE);
         var5.updateActivityFromSchedule(var1.getDayTime(), var1.getGameTime());
         this.ticksHidden = 0;
      }
   }
}
