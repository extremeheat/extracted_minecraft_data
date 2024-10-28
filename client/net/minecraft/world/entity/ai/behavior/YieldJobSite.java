package net.minecraft.world.entity.ai.behavior;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.pathfinder.Path;

public class YieldJobSite {
   public YieldJobSite() {
      super();
   }

   public static BehaviorControl<Villager> create(float var0) {
      return BehaviorBuilder.create((var1) -> {
         return var1.group(var1.present(MemoryModuleType.POTENTIAL_JOB_SITE), var1.absent(MemoryModuleType.JOB_SITE), var1.present(MemoryModuleType.NEAREST_LIVING_ENTITIES), var1.registered(MemoryModuleType.WALK_TARGET), var1.registered(MemoryModuleType.LOOK_TARGET)).apply(var1, (var2, var3, var4, var5, var6) -> {
            return (var6x, var7, var8) -> {
               if (var7.isBaby()) {
                  return false;
               } else if (var7.getVillagerData().getProfession() != VillagerProfession.NONE) {
                  return false;
               } else {
                  BlockPos var10 = ((GlobalPos)var1.get(var2)).pos();
                  Optional var11 = var6x.getPoiManager().getType(var10);
                  if (var11.isEmpty()) {
                     return true;
                  } else {
                     ((List)var1.get(var4)).stream().filter((var1x) -> {
                        return var1x instanceof Villager && var1x != var7;
                     }).map((var0x) -> {
                        return (Villager)var0x;
                     }).filter(LivingEntity::isAlive).filter((var2x) -> {
                        return nearbyWantsJobsite((Holder)var11.get(), var2x, var10);
                     }).findFirst().ifPresent((var6xx) -> {
                        var5.erase();
                        var6.erase();
                        var2.erase();
                        if (var6xx.getBrain().getMemory(MemoryModuleType.JOB_SITE).isEmpty()) {
                           BehaviorUtils.setWalkAndLookTargetMemories(var6xx, (BlockPos)var10, var0, 1);
                           var6xx.getBrain().setMemory(MemoryModuleType.POTENTIAL_JOB_SITE, (Object)GlobalPos.of(var6x.dimension(), var10));
                           DebugPackets.sendPoiTicketCountPacket(var6x, var10);
                        }

                     });
                     return true;
                  }
               }
            };
         });
      });
   }

   private static boolean nearbyWantsJobsite(Holder<PoiType> var0, Villager var1, BlockPos var2) {
      boolean var3 = var1.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).isPresent();
      if (var3) {
         return false;
      } else {
         Optional var4 = var1.getBrain().getMemory(MemoryModuleType.JOB_SITE);
         VillagerProfession var5 = var1.getVillagerData().getProfession();
         if (var5.heldJobSite().test(var0)) {
            return var4.isEmpty() ? canReachPos(var1, var2, (PoiType)var0.value()) : ((GlobalPos)var4.get()).pos().equals(var2);
         } else {
            return false;
         }
      }
   }

   private static boolean canReachPos(PathfinderMob var0, BlockPos var1, PoiType var2) {
      Path var3 = var0.getNavigation().createPath(var1, var2.validRange());
      return var3 != null && var3.canReach();
   }
}
