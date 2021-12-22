package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;

public class AssignProfessionFromJobSite extends Behavior<Villager> {
   public AssignProfessionFromJobSite() {
      super(ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, MemoryStatus.VALUE_PRESENT));
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Villager var2) {
      BlockPos var3 = ((GlobalPos)var2.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get()).pos();
      return var3.closerThan(var2.position(), 2.0D) || var2.assignProfessionWhenSpawned();
   }

   protected void start(ServerLevel var1, Villager var2, long var3) {
      GlobalPos var5 = (GlobalPos)var2.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get();
      var2.getBrain().eraseMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
      var2.getBrain().setMemory(MemoryModuleType.JOB_SITE, (Object)var5);
      var1.broadcastEntityEvent(var2, (byte)14);
      if (var2.getVillagerData().getProfession() == VillagerProfession.NONE) {
         MinecraftServer var6 = var1.getServer();
         Optional.ofNullable(var6.getLevel(var5.dimension())).flatMap((var1x) -> {
            return var1x.getPoiManager().getType(var5.pos());
         }).flatMap((var0) -> {
            return Registry.VILLAGER_PROFESSION.stream().filter((var1) -> {
               return var1.getJobPoiType() == var0;
            }).findFirst();
         }).ifPresent((var2x) -> {
            var2.setVillagerData(var2.getVillagerData().setProfession(var2x));
            var2.refreshBrain(var1);
         });
      }
   }
}
