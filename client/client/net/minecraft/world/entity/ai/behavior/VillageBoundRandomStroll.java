package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

public class VillageBoundRandomStroll {
   private static final int MAX_XZ_DIST = 10;
   private static final int MAX_Y_DIST = 7;

   public VillageBoundRandomStroll() {
      super();
   }

   public static OneShot<PathfinderMob> create(float var0) {
      return create(var0, 10, 7);
   }

   public static OneShot<PathfinderMob> create(float var0, int var1, int var2) {
      return BehaviorBuilder.create(var3 -> var3.group(var3.absent(MemoryModuleType.WALK_TARGET)).apply(var3, var3x -> (var4, var5, var6) -> {
               BlockPos var8 = var5.blockPosition();
               Vec3 var9;
               if (var4.isVillage(var8)) {
                  var9 = LandRandomPos.getPos(var5, var1, var2);
               } else {
                  SectionPos var10 = SectionPos.of(var8);
                  SectionPos var11 = BehaviorUtils.findSectionClosestToVillage(var4, var10, 2);
                  if (var11 != var10) {
                     var9 = DefaultRandomPos.getPosTowards(var5, var1, var2, Vec3.atBottomCenterOf(var11.center()), 1.5707963705062866);
                  } else {
                     var9 = LandRandomPos.getPos(var5, var1, var2);
                  }
               }

               var3x.setOrErase(Optional.ofNullable(var9).map(var1xxxx -> new WalkTarget(var1xxxx, var0, 0)));
               return true;
            }));
   }
}
