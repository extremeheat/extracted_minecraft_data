package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class GoToTargetLocation {
   public GoToTargetLocation() {
      super();
   }

   private static BlockPos getNearbyPos(Mob var0, BlockPos var1) {
      RandomSource var2 = var0.level().random;
      return var1.offset(getRandomOffset(var2), 0, getRandomOffset(var2));
   }

   private static int getRandomOffset(RandomSource var0) {
      return var0.nextInt(3) - 1;
   }

   public static <E extends Mob> OneShot<E> create(MemoryModuleType<BlockPos> var0, int var1, float var2) {
      return BehaviorBuilder.create((var3) -> {
         return var3.group(var3.present(var0), var3.absent(MemoryModuleType.ATTACK_TARGET), var3.absent(MemoryModuleType.WALK_TARGET), var3.registered(MemoryModuleType.LOOK_TARGET)).apply(var3, (var3x, var4, var5, var6) -> {
            return (var4x, var5x, var6x) -> {
               BlockPos var8 = (BlockPos)var3.get(var3x);
               boolean var9 = var8.closerThan(var5x.blockPosition(), (double)var1);
               if (!var9) {
                  BehaviorUtils.setWalkAndLookTargetMemories(var5x, (BlockPos)getNearbyPos(var5x, var8), var2, var1);
               }

               return true;
            };
         });
      });
   }
}
