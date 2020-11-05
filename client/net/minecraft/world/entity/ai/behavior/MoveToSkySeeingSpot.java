package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class MoveToSkySeeingSpot extends Behavior<LivingEntity> {
   private final float speedModifier;

   public MoveToSkySeeingSpot(float var1) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
      this.speedModifier = var1;
   }

   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      Optional var5 = Optional.ofNullable(this.getOutdoorPosition(var1, var2));
      if (var5.isPresent()) {
         var2.getBrain().setMemory(MemoryModuleType.WALK_TARGET, var5.map((var1x) -> {
            return new WalkTarget(var1x, this.speedModifier, 0);
         }));
      }

   }

   protected boolean checkExtraStartConditions(ServerLevel var1, LivingEntity var2) {
      return !var1.canSeeSky(var2.blockPosition());
   }

   @Nullable
   private Vec3 getOutdoorPosition(ServerLevel var1, LivingEntity var2) {
      Random var3 = var2.getRandom();
      BlockPos var4 = var2.blockPosition();

      for(int var5 = 0; var5 < 10; ++var5) {
         BlockPos var6 = var4.offset(var3.nextInt(20) - 10, var3.nextInt(6) - 3, var3.nextInt(20) - 10);
         if (hasNoBlocksAbove(var1, var2, var6)) {
            return Vec3.atBottomCenterOf(var6);
         }
      }

      return null;
   }

   public static boolean hasNoBlocksAbove(ServerLevel var0, LivingEntity var1, BlockPos var2) {
      return var0.canSeeSky(var2) && (double)var0.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, var2).getY() <= var1.getY();
   }
}
