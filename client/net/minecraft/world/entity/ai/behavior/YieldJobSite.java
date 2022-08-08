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
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.pathfinder.Path;

public class YieldJobSite extends Behavior<Villager> {
   private final float speedModifier;

   public YieldJobSite(float var1) {
      super(ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, MemoryStatus.VALUE_PRESENT, MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
      this.speedModifier = var1;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Villager var2) {
      if (var2.isBaby()) {
         return false;
      } else {
         return var2.getVillagerData().getProfession() == VillagerProfession.NONE;
      }
   }

   protected void start(ServerLevel var1, Villager var2, long var3) {
      BlockPos var5 = ((GlobalPos)var2.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get()).pos();
      Optional var6 = var1.getPoiManager().getType(var5);
      if (var6.isPresent()) {
         BehaviorUtils.getNearbyVillagersWithCondition(var2, (var3x) -> {
            return this.nearbyWantsJobsite((Holder)var6.get(), var3x, var5);
         }).findFirst().ifPresent((var4) -> {
            this.yieldJobSite(var1, var2, var4, var5, var4.getBrain().getMemory(MemoryModuleType.JOB_SITE).isPresent());
         });
      }
   }

   private boolean nearbyWantsJobsite(Holder<PoiType> var1, Villager var2, BlockPos var3) {
      boolean var4 = var2.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).isPresent();
      if (var4) {
         return false;
      } else {
         Optional var5 = var2.getBrain().getMemory(MemoryModuleType.JOB_SITE);
         VillagerProfession var6 = var2.getVillagerData().getProfession();
         if (var6.heldJobSite().test(var1)) {
            return !var5.isPresent() ? this.canReachPos(var2, var3, (PoiType)var1.value()) : ((GlobalPos)var5.get()).pos().equals(var3);
         } else {
            return false;
         }
      }
   }

   private void yieldJobSite(ServerLevel var1, Villager var2, Villager var3, BlockPos var4, boolean var5) {
      this.eraseMemories(var2);
      if (!var5) {
         BehaviorUtils.setWalkAndLookTargetMemories(var3, (BlockPos)var4, this.speedModifier, 1);
         var3.getBrain().setMemory(MemoryModuleType.POTENTIAL_JOB_SITE, (Object)GlobalPos.of(var1.dimension(), var4));
         DebugPackets.sendPoiTicketCountPacket(var1, var4);
      }

   }

   private boolean canReachPos(Villager var1, BlockPos var2, PoiType var3) {
      Path var4 = var1.getNavigation().createPath(var2, var3.validRange());
      return var4 != null && var4.canReach();
   }

   private void eraseMemories(Villager var1) {
      var1.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
      var1.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
      var1.getBrain().eraseMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
   }
}
