package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
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
      return (Boolean)var2.getBrain().getActiveNonCoreActivity().map((var0) -> {
         return var0 == Activity.IDLE || var0 == Activity.WORK || var0 == Activity.PLAY;
      }).orElse(true);
   }

   protected boolean canStillUse(ServerLevel var1, Villager var2, long var3) {
      return var2.getBrain().hasMemoryValue(MemoryModuleType.POTENTIAL_JOB_SITE);
   }

   protected void tick(ServerLevel var1, Villager var2, long var3) {
      BehaviorUtils.setWalkAndLookTargetMemories(var2, (BlockPos)((GlobalPos)var2.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get()).pos(), this.speedModifier, 1);
   }

   protected void stop(ServerLevel var1, Villager var2, long var3) {
      Optional var5 = var2.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
      var5.ifPresent((var1x) -> {
         BlockPos var2 = var1x.pos();
         ServerLevel var3 = var1.getServer().getLevel(var1x.dimension());
         if (var3 != null) {
            PoiManager var4 = var3.getPoiManager();
            if (var4.exists(var2, (var0) -> {
               return true;
            })) {
               var4.release(var2);
            }

            DebugPackets.sendPoiTicketCountPacket(var1, var2);
         }
      });
      var2.getBrain().eraseMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
   }

   // $FF: synthetic method
   protected boolean canStillUse(ServerLevel var1, LivingEntity var2, long var3) {
      return this.canStillUse(var1, (Villager)var2, var3);
   }

   // $FF: synthetic method
   protected void tick(ServerLevel var1, LivingEntity var2, long var3) {
      this.tick(var1, (Villager)var2, var3);
   }
}
