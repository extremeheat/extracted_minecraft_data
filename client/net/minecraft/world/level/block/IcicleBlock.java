package net.minecraft.world.level.block;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import org.jspecify.annotations.Nullable;

public class IcicleBlock extends SpeleothemBlock {
   private static final int MAX_GROWING_LENGTH = 4;
   private static final int BREAKS_IF_ABOVE_LIGHT_LEVEL = 4;
   private static final BooleanProperty ATTACHED;
   private static final List<Direction> ONLY_GROW_STALACTITES;
   private static final float DRIP_PROBABILITY_PER_ANIMATE_TICK = 0.02F;

   public IcicleBlock(final BlockState blockToGrowOn, final BlockBehaviour.Properties properties) {
      super(blockToGrowOn, properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(ATTACHED, false));
   }

   public void fallOn(final Level level, final BlockState state, final BlockPos pos, final Entity entity, final double fallDistance) {
      super.fallOnDamage(level, state, pos, entity, fallDistance);
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(ATTACHED);
   }

   public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
      BlockState state = super.getStateForPlacement(context);
      if (state == null) {
         return null;
      } else {
         boolean attached = isStalactiteStartPos(state, context.getLevel(), context.getClickedPos()) || isStalagmiteStartPos(state, context.getLevel(), context.getClickedPos());
         return (BlockState)state.setValue(ATTACHED, attached);
      }
   }

   protected int getStalactiteLandingSound() {
      return 1055;
   }

   protected int getMaxGrowthLength() {
      return 4;
   }

   protected boolean canGrow(final LevelReader level, final BlockPos pos) {
      if (!super.canGrow(level, pos)) {
         return false;
      } else {
         return this.wouldMelt(level, pos) ? false : isStalactite(level.getBlockState(pos));
      }
   }

   protected boolean shouldMergeTips() {
      return false;
   }

   protected List<Direction> getNaturalGrowthDirections() {
      return ONLY_GROW_STALACTITES;
   }

   public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
      if (isFreeHangingStalactite(state)) {
         float randomValue = random.nextFloat();
         if (!(randomValue > 0.02F)) {
            this.spawnDripParticle(level, pos, state, pos.above(), (Fluid)null);
         }
      }
   }

   protected @Nullable ParticleOptions getDripParticle(final Level level, final @Nullable Fluid fluidAbove, final BlockPos posAbove) {
      return new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.SNOW.defaultBlockState());
   }

   protected void randomTick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if (!this.isStartPos(state, level, pos) && this.wouldMelt(level, pos)) {
         if (!isStalagmite(state) && (!isTip(state, true) || level.getBlockState(pos.below()).isAir())) {
            spawnFallingStalactite(state, level, pos);
         } else {
            level.destroyBlock(pos, true);
         }

      } else {
         super.randomTick(state, level, pos, random);
      }
   }

   private boolean isStartPos(final BlockState state, final LevelReader level, final BlockPos pos) {
      return isStalactiteStartPos(state, level, pos) || isStalagmiteStartPos(state, level, pos);
   }

   private boolean wouldMelt(final LevelReader level, final BlockPos pos) {
      return (Boolean)level.environmentAttributes().getValue(EnvironmentAttributes.WATER_EVAPORATES, pos) || this.isTooBright(level, pos) || this.isAboveMeltingBlock(level, pos);
   }

   private boolean isAboveMeltingBlock(final LevelReader level, final BlockPos pos) {
      return level.getBlockState(pos.below()).is(BlockTags.MELTS_ICICLE_ABOVE) || level.getBlockState(pos.below(2)).is(BlockTags.MELTS_ICICLE_ABOVE);
   }

   private boolean isTooBright(final LevelReader level, final BlockPos pos) {
      return level.getBrightness(LightLayer.BLOCK, pos) > 4;
   }

   static {
      ATTACHED = BlockStateProperties.ATTACHED;
      ONLY_GROW_STALACTITES = List.of(Direction.DOWN);
   }
}
