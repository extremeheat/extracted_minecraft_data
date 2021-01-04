package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SleepInBed extends Behavior<LivingEntity> {
   private long nextOkStartTime;

   public SleepInBed() {
      super(ImmutableMap.of(MemoryModuleType.HOME, MemoryStatus.VALUE_PRESENT));
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, LivingEntity var2) {
      if (var2.isPassenger()) {
         return false;
      } else {
         GlobalPos var3 = (GlobalPos)var2.getBrain().getMemory(MemoryModuleType.HOME).get();
         if (!Objects.equals(var1.getDimension().getType(), var3.dimension())) {
            return false;
         } else {
            BlockState var4 = var1.getBlockState(var3.pos());
            return var3.pos().closerThan(var2.position(), 2.0D) && var4.getBlock().is(BlockTags.BEDS) && !(Boolean)var4.getValue(BedBlock.OCCUPIED);
         }
      }
   }

   protected boolean canStillUse(ServerLevel var1, LivingEntity var2, long var3) {
      Optional var5 = var2.getBrain().getMemory(MemoryModuleType.HOME);
      if (!var5.isPresent()) {
         return false;
      } else {
         BlockPos var6 = ((GlobalPos)var5.get()).pos();
         return var2.getBrain().isActive(Activity.REST) && var2.y > (double)var6.getY() + 0.4D && var6.closerThan(var2.position(), 1.14D);
      }
   }

   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      if (var3 > this.nextOkStartTime) {
         var2.getBrain().getMemory(MemoryModuleType.OPENED_DOORS).ifPresent((var2x) -> {
            InteractWithDoor.closeAllOpenedDoors(var1, ImmutableList.of(), 0, var2, var2.getBrain());
         });
         var2.startSleeping(((GlobalPos)var2.getBrain().getMemory(MemoryModuleType.HOME).get()).pos());
      }

   }

   protected boolean timedOut(long var1) {
      return false;
   }

   protected void stop(ServerLevel var1, LivingEntity var2, long var3) {
      if (var2.isSleeping()) {
         var2.stopSleeping();
         this.nextOkStartTime = var3 + 40L;
      }

   }
}
