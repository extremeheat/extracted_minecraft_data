package net.minecraft.world.entity.livingblock.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class AnnoyingClickClackBehavior implements LivingBlockBehavior {
   private static final int CLICK_CLACK_INTERVAL = 10;
   public static final LivingBlockBehaviorType BEHAVIOR = LivingBlockBehaviorType.behaviorType(AnnoyingClickClackBehavior::new);
   private int lastTriggeredTick = 0;

   public AnnoyingClickClackBehavior() {
      super();
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return entity.tickCount - this.lastTriggeredTick >= 10;
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      this.lastTriggeredTick = tickCount;
      BlockState state = entity.getBlockState();
      BlockPos pos = entity.blockPosition();
      double randomValue = level.getRandom().nextDouble();
      boolean clack = (Boolean)state.getValue(BlockStateProperties.POWERED);
      if (clack && randomValue < 0.25) {
         Direction opposite = ((Direction)state.getValue(LeverBlock.FACING)).getOpposite();
         Direction oppositeConnect = LeverBlock.getConnectedDirection(state).getOpposite();
         double x = (double)pos.getX() + 0.5 + 0.1 * (double)opposite.getStepX() + 0.2 * (double)oppositeConnect.getStepX();
         double y = (double)pos.getY() + 0.5 + 0.1 * (double)opposite.getStepY() + 0.2 * (double)oppositeConnect.getStepY();
         double z = (double)pos.getZ() + 0.5 + 0.1 * (double)opposite.getStepZ() + 0.2 * (double)oppositeConnect.getStepZ();
         level.sendParticles(new DustParticleOptions(16711680, 0.5F), x, y, z, 1, 0.0, 0.0, 0.0, 1.0);
      }

      if (randomValue < 0.1) {
         BlockState stateAfter = (BlockState)state.cycle(BlockStateProperties.POWERED);
         entity.setBlockState(stateAfter);
         LeverBlock.playSound((Player)null, level, pos, stateAfter);
      }

      return false;
   }
}
