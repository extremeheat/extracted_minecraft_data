package net.minecraft.world.entity.ai.behavior;

import java.util.function.Function;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerProfession;

public class ResetProfession {
   public ResetProfession() {
      super();
   }

   public static BehaviorControl<Villager> create() {
      return BehaviorBuilder.create((Function)((var0) -> var0.group(var0.absent(MemoryModuleType.JOB_SITE)).apply(var0, (var0x) -> (var0, var1, var2) -> {
               VillagerData var4 = var1.getVillagerData();
               boolean var5 = !var4.profession().is(VillagerProfession.NONE) && !var4.profession().is(VillagerProfession.NITWIT);
               if (var5 && var1.getVillagerXp() == 0 && var4.level() <= 1) {
                  var1.setVillagerData(var1.getVillagerData().withProfession(var0.registryAccess(), VillagerProfession.NONE));
                  var1.refreshBrain(var0);
                  return true;
               } else {
                  return false;
               }
            })));
   }
}
