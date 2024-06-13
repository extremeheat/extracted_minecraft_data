package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.Vec3;

public class SetWalkTargetFromBlockMemory {
   public SetWalkTargetFromBlockMemory() {
      super();
   }

   public static OneShot<Villager> create(MemoryModuleType<GlobalPos> var0, float var1, int var2, int var3, int var4) {
      return BehaviorBuilder.create(
         var5 -> var5.group(var5.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE), var5.absent(MemoryModuleType.WALK_TARGET), var5.present(var0))
               .apply(var5, (var6, var7, var8) -> (var9, var10, var11) -> {
                     GlobalPos var13 = var5.get(var8);
                     Optional var14 = var5.tryGet(var6);
                     if (var13.dimension() == var9.dimension() && (!var14.isPresent() || var9.getGameTime() - (Long)var14.get() <= (long)var4)) {
                        if (var13.pos().distManhattan(var10.blockPosition()) > var3) {
                           Vec3 var15 = null;
                           int var16 = 0;
                           short var17 = 1000;

                           while (var15 == null || BlockPos.containing(var15).distManhattan(var10.blockPosition()) > var3) {
                              var15 = DefaultRandomPos.getPosTowards(var10, 15, 7, Vec3.atBottomCenterOf(var13.pos()), 1.5707963705062866);
                              if (++var16 == 1000) {
                                 var10.releasePoi(var0);
                                 var8.erase();
                                 var6.set(var11);
                                 return true;
                              }
                           }

                           var7.set(new WalkTarget(var15, var1, var2));
                        } else if (var13.pos().distManhattan(var10.blockPosition()) > var2) {
                           var7.set(new WalkTarget(var13.pos(), var1, var2));
                        }
                     } else {
                        var10.releasePoi(var0);
                        var8.erase();
                        var6.set(var11);
                     }

                     return true;
                  })
      );
   }
}
