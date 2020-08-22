package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.SerializableLong;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;

public class WorkAtPoi extends Behavior {
   private long lastCheck;

   public WorkAtPoi() {
      super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED));
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Villager var2) {
      if (var1.getGameTime() - this.lastCheck < 300L) {
         return false;
      } else if (var1.random.nextInt(2) != 0) {
         return false;
      } else {
         this.lastCheck = var1.getGameTime();
         GlobalPos var3 = (GlobalPos)var2.getBrain().getMemory(MemoryModuleType.JOB_SITE).get();
         return Objects.equals(var3.dimension(), var1.getDimension().getType()) && var3.pos().closerThan(var2.position(), 1.73D);
      }
   }

   protected void start(ServerLevel var1, Villager var2, long var3) {
      Brain var5 = var2.getBrain();
      var5.setMemory(MemoryModuleType.LAST_WORKED_AT_POI, (Object)SerializableLong.of(var3));
      var5.getMemory(MemoryModuleType.JOB_SITE).ifPresent((var1x) -> {
         var5.setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new BlockPosWrapper(var1x.pos())));
      });
      var2.playWorkSound();
      if (var2.shouldRestock()) {
         var2.restock();
      }

   }
}
