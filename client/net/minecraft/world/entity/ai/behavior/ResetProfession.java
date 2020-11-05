package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;

public class ResetProfession extends Behavior<Villager> {
   public ResetProfession() {
      super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_ABSENT));
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Villager var2) {
      VillagerData var3 = var2.getVillagerData();
      return var3.getProfession() != VillagerProfession.NONE && var3.getProfession() != VillagerProfession.NITWIT && var2.getVillagerXp() == 0 && var3.getLevel() <= 1;
   }

   protected void start(ServerLevel var1, Villager var2, long var3) {
      var2.setVillagerData(var2.getVillagerData().setProfession(VillagerProfession.NONE));
      var2.refreshBrain(var1);
   }
}
