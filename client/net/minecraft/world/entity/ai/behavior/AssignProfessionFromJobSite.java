package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;

public class AssignProfessionFromJobSite {
   public AssignProfessionFromJobSite() {
      super();
   }

   public static BehaviorControl<Villager> create() {
      return BehaviorBuilder.create((var0) -> {
         return var0.group(var0.present(MemoryModuleType.POTENTIAL_JOB_SITE), var0.registered(MemoryModuleType.JOB_SITE)).apply(var0, (var1, var2) -> {
            return (var3, var4, var5) -> {
               GlobalPos var7 = (GlobalPos)var0.get(var1);
               if (!var7.pos().closerToCenterThan(var4.position(), 2.0) && !var4.assignProfessionWhenSpawned()) {
                  return false;
               } else {
                  var1.erase();
                  var2.set(var7);
                  var3.broadcastEntityEvent(var4, (byte)14);
                  if (var4.getVillagerData().getProfession() != VillagerProfession.NONE) {
                     return true;
                  } else {
                     MinecraftServer var8 = var3.getServer();
                     Optional.ofNullable(var8.getLevel(var7.dimension())).flatMap((var1x) -> {
                        return var1x.getPoiManager().getType(var7.pos());
                     }).flatMap((var0x) -> {
                        return BuiltInRegistries.VILLAGER_PROFESSION.stream().filter((var1) -> {
                           return var1.heldJobSite().test(var0x);
                        }).findFirst();
                     }).ifPresent((var2x) -> {
                        var4.setVillagerData(var4.getVillagerData().setProfession(var2x));
                        var4.refreshBrain(var3);
                     });
                     return true;
                  }
               }
            };
         });
      });
   }
}
