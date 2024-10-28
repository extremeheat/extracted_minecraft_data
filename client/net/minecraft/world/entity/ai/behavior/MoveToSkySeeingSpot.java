package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class MoveToSkySeeingSpot {
   public MoveToSkySeeingSpot() {
      super();
   }

   public static OneShot<LivingEntity> create(float var0) {
      return BehaviorBuilder.create((var1) -> {
         return var1.group(var1.absent(MemoryModuleType.WALK_TARGET)).apply(var1, (var1x) -> {
            return (var2, var3, var4) -> {
               if (var2.canSeeSky(var3.blockPosition())) {
                  return false;
               } else {
                  Optional var6 = Optional.ofNullable(getOutdoorPosition(var2, var3));
                  var6.ifPresent((var2x) -> {
                     var1x.set(new WalkTarget(var2x, var0, 0));
                  });
                  return true;
               }
            };
         });
      });
   }

   @Nullable
   private static Vec3 getOutdoorPosition(ServerLevel var0, LivingEntity var1) {
      RandomSource var2 = var1.getRandom();
      BlockPos var3 = var1.blockPosition();

      for(int var4 = 0; var4 < 10; ++var4) {
         BlockPos var5 = var3.offset(var2.nextInt(20) - 10, var2.nextInt(6) - 3, var2.nextInt(20) - 10);
         if (hasNoBlocksAbove(var0, var1, var5)) {
            return Vec3.atBottomCenterOf(var5);
         }
      }

      return null;
   }

   public static boolean hasNoBlocksAbove(ServerLevel var0, LivingEntity var1, BlockPos var2) {
      return var0.canSeeSky(var2) && (double)var0.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, var2).getY() <= var1.getY();
   }
}
