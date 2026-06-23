package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;

public class LightningRodBlock extends RodBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<LightningRodBlock> CODEC = simpleCodec(LightningRodBlock::new);
   public static final BooleanProperty WATERLOGGED;
   public static final BooleanProperty POWERED;
   private static final int ACTIVATION_TICKS = 8;
   public static final int RANGE = 128;
   private static final int SPARK_CYCLE = 200;

   public MapCodec<? extends LightningRodBlock> codec() {
      return CODEC;
   }

   public LightningRodBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.UP)).setValue(WATERLOGGED, false)).setValue(POWERED, false));
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      FluidState replacedFluidState = context.getLevel().getFluidState(context.getClickedPos());
      boolean isWaterSource = replacedFluidState.is(Fluids.WATER);
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, context.getClickedFace())).setValue(WATERLOGGED, isWaterSource);
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         ticks.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
   }

   protected FluidState getFluidState(final BlockState state) {
      return (Boolean)state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   protected int ownSignal(final BlockState state, final BlockGetter level, final BlockPos pos) {
      return (Boolean)state.getValue(POWERED) ? 15 : 0;
   }

   protected int getDirectSignal(final BlockState state, final BlockGetter level, final BlockPos pos, final Direction direction) {
      return (Boolean)state.getValue(POWERED) && state.getValue(FACING) == direction ? 15 : 0;
   }

   public void onLightningStrike(final BlockState state, final Level level, final BlockPos pos) {
      level.setBlockAndUpdate(pos, (BlockState)state.setValue(POWERED, true));
      this.updateNeighbours(state, level, pos);
      level.scheduleTick(pos, this, 8);
      level.levelEvent(3002, pos, ((Direction)state.getValue(FACING)).getAxis().ordinal());
   }

   private void updateNeighbours(final BlockState state, final Level level, final BlockPos pos) {
      Direction front = ((Direction)state.getValue(FACING)).getOpposite();
      level.updateNeighborsAt(pos.relative(front), this, ExperimentalRedstoneUtils.initialOrientation(level, front, (Direction)null));
   }

   protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      level.setBlockAndUpdate(pos, (BlockState)state.setValue(POWERED, false));
      this.updateNeighbours(state, level, pos);
   }

   public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
      if (level.isThundering() && (long)level.getRandom().nextInt(200) <= level.getGameTime() % 200L && pos.getY() == level.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ()) - 1) {
         ParticleUtils.spawnParticlesAlongAxis(((Direction)state.getValue(FACING)).getAxis(), level, pos, 0.125, ParticleTypes.ELECTRIC_SPARK, UniformInt.of(1, 2));
      }
   }

   protected void affectNeighborsAfterRemoval(final BlockState state, final ServerLevel level, final BlockPos pos, final boolean movedByPiston) {
      if ((Boolean)state.getValue(POWERED)) {
         this.updateNeighbours(state, level, pos);
      }

   }

   protected void onPlace(final BlockState state, final Level level, final BlockPos pos, final BlockState oldState, final boolean movedByPiston) {
      if (!state.is(oldState.getBlock())) {
         if ((Boolean)state.getValue(POWERED) && !level.getBlockTicks().hasScheduledTick(pos, this)) {
            level.scheduleTick(pos, this, 8);
         }

      }
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(FACING, POWERED, WATERLOGGED);
   }

   protected boolean isSignalSource(final BlockState state) {
      return true;
   }

   static {
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      POWERED = BlockStateProperties.POWERED;
   }
}
