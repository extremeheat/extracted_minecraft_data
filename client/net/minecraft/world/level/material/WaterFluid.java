package net.minecraft.world.level.material;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.InsideBlockEffectType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;

public class WaterFluid extends FlowingFluid {
   public static final double FLOW_SCALE = 0.014;

   public WaterFluid(final Fluid.Properties properties) {
      super(properties);
   }

   public void animateTick(final Level level, final BlockPos pos, final FluidState fluidState, final RandomSource random) {
      if (!fluidState.isSource() && !fluidState.isFalling()) {
         if (random.nextInt(64) == 0) {
            level.playLocalSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.WATER_AMBIENT, SoundSource.AMBIENT, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
         }
      } else if (random.nextInt(10) == 0) {
         level.addParticle(ParticleTypes.UNDERWATER, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + random.nextDouble(), (double)pos.getZ() + random.nextDouble(), 0.0, 0.0, 0.0);
      }

   }

   protected boolean canConvertToSource(final ServerLevel level) {
      return (Boolean)level.getGameRules().get(GameRules.WATER_SOURCE_CONVERSION);
   }

   protected void beforeDestroyingBlock(final LevelAccessor level, final BlockPos pos, final BlockState state) {
   }

   protected void entityInside(final Level level, final BlockPos pos, final Entity entity, final InsideBlockEffectApplier effectApplier) {
      effectApplier.apply(InsideBlockEffectType.EXTINGUISH);
   }

   public double getFlowScale(final Level level) {
      return 0.014;
   }

   public int getSlopeFindDistance(final LevelReader level) {
      return 4;
   }

   public BlockState createLegacyBlock(final FluidState fluidState) {
      return (BlockState)Blocks.WATER.defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(fluidState));
   }

   public int getDropOff(final LevelReader level) {
      return 1;
   }

   public int getTickDelay(final LevelReader level) {
      return 5;
   }

   public boolean canBeReplacedWith(final FluidState state, final BlockGetter level, final BlockPos pos, final FluidState other, final Direction direction) {
      return direction == Direction.DOWN && !other.is(FluidTags.WATER);
   }
}
