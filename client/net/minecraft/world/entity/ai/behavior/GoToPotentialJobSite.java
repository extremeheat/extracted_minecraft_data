package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.schedule.Activity;

public class GoToPotentialJobSite extends Behavior<Villager> {
   private static final int TICKS_UNTIL_TIMEOUT = 1200;
   final float speedModifier;

   public GoToPotentialJobSite(float var1) {
      super(ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, MemoryStatus.VALUE_PRESENT), 1200);
      this.speedModifier = var1;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Villager var2) {
      return var2.getBrain().getActiveNonCoreActivity().map(var0 -> var0 == Activity.IDLE || var0 == Activity.WORK || var0 == Activity.PLAY).orElse(true);
   }

   protected boolean canStillUse(ServerLevel var1, Villager var2, long var3) {
      return var2.getBrain().hasMemoryValue(MemoryModuleType.POTENTIAL_JOB_SITE);
   }

   protected void tick(ServerLevel var1, Villager var2, long var3) {
      BehaviorUtils.setWalkAndLookTargetMemories(
         var2, ((GlobalPos)var2.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get()).pos(), this.speedModifier, 1
      );
   }

   protected void stop(ServerLevel var1, Villager var2, long var3) {
      Optional var5 = var2.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
      var5.ifPresent(var1x -> {
         BlockPos var2xx = var1x.pos();
         ServerLevel var3xx = var1.getServer().getLevel(var1x.dimension());
         if (var3xx != null) {
            PoiManager var4 = var3xx.getPoiManager();
            if (var4.exists(var2xx, var0x -> true)) {
               var4.release(var2xx);
            }

            DebugPackets.sendPoiTicketCountPacket(var1, var2xx);
         }
      });
      var2.getBrain().eraseMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
   }
}
