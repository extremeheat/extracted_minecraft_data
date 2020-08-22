package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;

public class AssignProfessionFromJobSite extends Behavior {
   public AssignProfessionFromJobSite() {
      super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT));
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Villager var2) {
      return var2.getVillagerData().getProfession() == VillagerProfession.NONE;
   }

   protected void start(ServerLevel var1, Villager var2, long var3) {
      GlobalPos var5 = (GlobalPos)var2.getBrain().getMemory(MemoryModuleType.JOB_SITE).get();
      MinecraftServer var6 = var1.getServer();
      var6.getLevel(var5.dimension()).getPoiManager().getType(var5.pos()).ifPresent((var2x) -> {
         Registry.VILLAGER_PROFESSION.stream().filter((var1x) -> {
            return var1x.getJobPoiType() == var2x;
         }).findFirst().ifPresent((var2xx) -> {
            var2.setVillagerData(var2.getVillagerData().setProfession(var2xx));
            var2.refreshBrain(var1);
         });
      });
   }
}
