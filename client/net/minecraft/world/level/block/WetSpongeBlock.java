package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WetSpongeBlock extends Block {
   protected WetSpongeBlock(final BlockBehaviour.Properties properties) {
      super(properties);
   }

   protected void onPlace(final BlockState state, final Level level, final BlockPos pos, final BlockState oldState, final boolean movedByPiston) {
      if ((Boolean)level.environmentAttributes().getValue(EnvironmentAttributes.WATER_EVAPORATES, pos)) {
         level.setBlockAndUpdate(pos, Blocks.SPONGE.defaultBlockState());
         level.levelEvent(2009, pos, 0);
         level.playSound((Entity)null, (BlockPos)pos, SoundEvents.WET_SPONGE_DRIES, SoundSource.BLOCKS, 1.0F, (1.0F + level.getRandom().nextFloat() * 0.2F) * 0.7F);
      }

   }

   public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
      Direction direction = Direction.getRandom(random);
      if (direction != Direction.UP) {
         BlockPos relativePos = pos.relative(direction);
         BlockState blockState = level.getBlockState(relativePos);
         if (!state.canOcclude() || !blockState.isFaceSturdy(level, relativePos, direction.getOpposite())) {
            double xx = (double)pos.getX();
            double yy = (double)pos.getY();
            double zz = (double)pos.getZ();
            if (direction == Direction.DOWN) {
               yy -= 0.05;
               xx += random.nextDouble();
               zz += random.nextDouble();
            } else {
               yy += random.nextDouble() * 0.8;
               if (direction.getAxis() == Direction.Axis.X) {
                  zz += random.nextDouble();
                  if (direction == Direction.EAST) {
                     ++xx;
                  } else {
                     xx += 0.05;
                  }
               } else {
                  xx += random.nextDouble();
                  if (direction == Direction.SOUTH) {
                     ++zz;
                  } else {
                     zz += 0.05;
                  }
               }
            }

            level.addParticle(ParticleTypes.DRIPPING_WATER, xx, yy, zz, 0.0, 0.0, 0.0);
         }
      }
   }
}
