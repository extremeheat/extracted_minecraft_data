package net.minecraft.world.entity.livingblock.behavior;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class TrapPlayerBehavior implements LivingBlockBehavior {
   private static final int CHECK_INTERVAL = 20;
   private final double maxDistance;
   private final BlockState bars;
   private int lastTriggeredTick;

   private TrapPlayerBehavior(final double maxDistance, final BlockState bars) {
      super();
      this.maxDistance = maxDistance;
      this.bars = bars;
   }

   public static LivingBlockBehaviorType trapPlayer(final double maxDistance, final Block bars) {
      return LivingBlockBehaviorType.behaviorType((Supplier)(() -> new TrapPlayerBehavior(maxDistance, bars.defaultBlockState())));
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return entity.tickCount - this.lastTriggeredTick >= 20;
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      this.lastTriggeredTick = tickCount;
      Vec3 pos = entity.position();
      Player player = level.getNearestPlayer(pos.x(), pos.y(), pos.z(), this.maxDistance, (var0) -> true);
      if (player != null) {
         BlockPos trapPos = player.blockPosition();

         for(int x = -1; x < 2; ++x) {
            for(int z = -1; z < 2; ++z) {
               for(int y = 0; y < 3; ++y) {
                  if (x != 0 || z != 0 || y == 2) {
                     level.setBlockAndUpdate(trapPos.offset(x, y, z), this.bars);
                  }
               }
            }
         }

         entity.discard();
      }

      return false;
   }
}
