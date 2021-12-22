package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;

public class PoiCompetitorScan extends Behavior<Villager> {
   final VillagerProfession profession;

   public PoiCompetitorScan(VillagerProfession var1) {
      super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
      this.profession = var1;
   }

   protected void start(ServerLevel var1, Villager var2, long var3) {
      GlobalPos var5 = (GlobalPos)var2.getBrain().getMemory(MemoryModuleType.JOB_SITE).get();
      var1.getPoiManager().getType(var5.pos()).ifPresent((var3x) -> {
         BehaviorUtils.getNearbyVillagersWithCondition(var2, (var3) -> {
            return this.competesForSameJobsite(var5, var3x, var3);
         }).reduce(var2, PoiCompetitorScan::selectWinner);
      });
   }

   private static Villager selectWinner(Villager var0, Villager var1) {
      Villager var2;
      Villager var3;
      if (var0.getVillagerXp() > var1.getVillagerXp()) {
         var2 = var0;
         var3 = var1;
      } else {
         var2 = var1;
         var3 = var0;
      }

      var3.getBrain().eraseMemory(MemoryModuleType.JOB_SITE);
      return var2;
   }

   private boolean competesForSameJobsite(GlobalPos var1, PoiType var2, Villager var3) {
      return this.hasJobSite(var3) && var1.equals(var3.getBrain().getMemory(MemoryModuleType.JOB_SITE).get()) && this.hasMatchingProfession(var2, var3.getVillagerData().getProfession());
   }

   private boolean hasMatchingProfession(PoiType var1, VillagerProfession var2) {
      return var2.getJobPoiType().getPredicate().test(var1);
   }

   private boolean hasJobSite(Villager var1) {
      return var1.getBrain().getMemory(MemoryModuleType.JOB_SITE).isPresent();
   }
}
