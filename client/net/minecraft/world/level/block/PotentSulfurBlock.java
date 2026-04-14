package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.PotentSulfurEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jspecify.annotations.Nullable;

public class PotentSulfurBlock extends BaseEntityBlock {
   public static final int ALLOWED_WATER_BLOCKS_ABOVE = 4;
   public static final MapCodec<PotentSulfurBlock> CODEC = simpleCodec(PotentSulfurBlock::new);

   public MapCodec<PotentSulfurBlock> codec() {
      return CODEC;
   }

   public PotentSulfurBlock(final BlockBehaviour.Properties properties) {
      super(properties);
   }

   public @Nullable BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      return new PotentSulfurEntity(worldPosition, blockState);
   }

   public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
      if (level.getFluidState(pos.above()).isSourceOfType(Fluids.WATER)) {
         spawnBubbleParticlesAt(level, random, (double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ());
         spawnBubbleParticlesAt(level, random, (double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ());
         if (random.nextInt(10) == 0) {
            level.playLocalSound((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundEvents.NOXIOUS_GAS, SoundSource.AMBIENT, 1.0F, 1.0F, false);
         }

      }
   }

   private static void spawnBubbleParticlesAt(final Level level, final RandomSource random, final double x, final double y, final double z) {
      level.addAlwaysVisibleParticle(ParticleTypes.SULFUR_BUBBLES, x + (double)random.nextFloat(), y + (double)random.nextFloat(), z + (double)random.nextFloat(), 0.0, 0.0, 0.0);
   }

   public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(final Level level, final BlockState blockState, final BlockEntityType<T> type) {
      return createTickerHelper(type, BlockEntityTypes.POTENT_SULFUR, level.isClientSide() ? PotentSulfurEntity::clientTick : PotentSulfurEntity::serverTick);
   }
}
