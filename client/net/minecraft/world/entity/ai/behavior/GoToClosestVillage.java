package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.Vec3;

public class GoToClosestVillage {
   public GoToClosestVillage() {
      super();
   }

   public static BehaviorControl<Villager> create(float var0, int var1) {
      return BehaviorBuilder.create((var2) -> {
         return var2.group(var2.absent(MemoryModuleType.WALK_TARGET)).apply(var2, (var2x) -> {
            return (var3, var4, var5) -> {
               if (var3.isVillage(var4.blockPosition())) {
                  return false;
               } else {
                  PoiManager var7 = var3.getPoiManager();
                  int var8 = var7.sectionsToVillage(SectionPos.of(var4.blockPosition()));
                  Vec3 var9 = null;

                  for(int var10 = 0; var10 < 5; ++var10) {
                     Vec3 var11 = LandRandomPos.getPos(var4, 15, 7, (var1x) -> {
                        return (double)(-var7.sectionsToVillage(SectionPos.of(var1x)));
                     });
                     if (var11 != null) {
                        int var12 = var7.sectionsToVillage(SectionPos.of(BlockPos.containing(var11)));
                        if (var12 < var8) {
                           var9 = var11;
                           break;
                        }

                        if (var12 == var8) {
                           var9 = var11;
                        }
                     }
                  }

                  if (var9 != null) {
                     var2x.set(new WalkTarget(var9, var0, var1));
                  }

                  return true;
               }
            };
         });
      });
   }
}
