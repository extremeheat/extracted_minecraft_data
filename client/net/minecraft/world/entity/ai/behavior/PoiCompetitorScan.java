package net.minecraft.world.entity.ai.behavior;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;

public class PoiCompetitorScan {
   public PoiCompetitorScan() {
      super();
   }

   public static BehaviorControl<Villager> create() {
      return BehaviorBuilder.create((var0) -> {
         return var0.group(var0.present(MemoryModuleType.JOB_SITE), var0.present(MemoryModuleType.NEAREST_LIVING_ENTITIES)).apply(var0, (var1, var2) -> {
            return (var3, var4, var5) -> {
               GlobalPos var7 = (GlobalPos)var0.get(var1);
               var3.getPoiManager().getType(var7.pos()).ifPresent((var4x) -> {
                  ((List)var0.get(var2)).stream().filter((var1) -> {
                     return var1 instanceof Villager && var1 != var4;
                  }).map((var0x) -> {
                     return (Villager)var0x;
                  }).filter(LivingEntity::isAlive).filter((var2x) -> {
                     return competesForSameJobsite(var7, var4x, var2x);
                  }).reduce(var4, PoiCompetitorScan::selectWinner);
               });
               return true;
            };
         });
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

   private static boolean competesForSameJobsite(GlobalPos var0, Holder<PoiType> var1, Villager var2) {
      Optional var3 = var2.getBrain().getMemory(MemoryModuleType.JOB_SITE);
      return var3.isPresent() && var0.equals(var3.get()) && hasMatchingProfession(var1, var2.getVillagerData().getProfession());
   }

   private static boolean hasMatchingProfession(Holder<PoiType> var0, VillagerProfession var1) {
      return var1.heldJobSite().test(var0);
   }
}
