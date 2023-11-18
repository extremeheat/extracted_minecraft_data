package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SleepInBed extends Behavior<LivingEntity> {
   public static final int COOLDOWN_AFTER_BEING_WOKEN = 100;
   private long nextOkStartTime;

   public SleepInBed() {
      super(ImmutableMap.of(MemoryModuleType.HOME, MemoryStatus.VALUE_PRESENT, MemoryModuleType.LAST_WOKEN, MemoryStatus.REGISTERED));
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel var1, LivingEntity var2) {
      if (var2.isPassenger()) {
         return false;
      } else {
         Brain var3 = var2.getBrain();
         GlobalPos var4 = var3.getMemory(MemoryModuleType.HOME).get();
         if (var1.dimension() != var4.dimension()) {
            return false;
         } else {
            Optional var5 = var3.getMemory(MemoryModuleType.LAST_WOKEN);
            if (var5.isPresent()) {
               long var6 = var1.getGameTime() - var5.get();
               if (var6 > 0L && var6 < 100L) {
                  return false;
               }
            }

            BlockState var8 = var1.getBlockState(var4.pos());
            return var4.pos().closerToCenterThan(var2.position(), 2.0) && var8.is(BlockTags.BEDS) && !var8.getValue(BedBlock.OCCUPIED);
         }
      }
   }

   @Override
   protected boolean canStillUse(ServerLevel var1, LivingEntity var2, long var3) {
      Optional var5 = var2.getBrain().getMemory(MemoryModuleType.HOME);
      if (var5.isEmpty()) {
         return false;
      } else {
         BlockPos var6 = ((GlobalPos)var5.get()).pos();
         return var2.getBrain().isActive(Activity.REST) && var2.getY() > (double)var6.getY() + 0.4 && var6.closerToCenterThan(var2.position(), 1.14);
      }
   }

   @Override
   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      if (var3 > this.nextOkStartTime) {
         Brain var5 = var2.getBrain();
         if (var5.hasMemoryValue(MemoryModuleType.DOORS_TO_CLOSE)) {
            Set var6 = var5.getMemory(MemoryModuleType.DOORS_TO_CLOSE).get();
            Optional var7;
            if (var5.hasMemoryValue(MemoryModuleType.NEAREST_LIVING_ENTITIES)) {
               var7 = var5.getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES);
            } else {
               var7 = Optional.empty();
            }

            InteractWithDoor.closeDoorsThatIHaveOpenedOrPassedThrough(var1, var2, null, null, var6, var7);
         }

         var2.startSleeping(var2.getBrain().getMemory(MemoryModuleType.HOME).get().pos());
      }
   }

   @Override
   protected boolean timedOut(long var1) {
      return false;
   }

   @Override
   protected void stop(ServerLevel var1, LivingEntity var2, long var3) {
      if (var2.isSleeping()) {
         var2.stopSleeping();
         this.nextOkStartTime = var3 + 40L;
      }
   }
}
