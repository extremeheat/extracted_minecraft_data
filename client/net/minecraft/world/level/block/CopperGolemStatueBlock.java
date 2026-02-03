package net.minecraft.world.level.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.IntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CopperGolemStatueBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class CopperGolemStatueBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<CopperGolemStatueBlock> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state").forGetter(CopperGolemStatueBlock::getWeatheringState), propertiesCodec()).apply(i, CopperGolemStatueBlock::new));
   public static final EnumProperty<Direction> FACING;
   public static final EnumProperty<Pose> POSE;
   public static final BooleanProperty WATERLOGGED;
   private static final VoxelShape SHAPE;
   private final WeatheringCopper.WeatherState weatheringState;

   public MapCodec<? extends CopperGolemStatueBlock> codec() {
      return CODEC;
   }

   public CopperGolemStatueBlock(final WeatheringCopper.WeatherState weatherState, final BlockBehaviour.Properties properties) {
      super(properties);
      this.weatheringState = weatherState;
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH)).setValue(POSE, CopperGolemStatueBlock.Pose.STANDING)).setValue(WATERLOGGED, false));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(FACING, POSE, WATERLOGGED);
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      FluidState replacedFluidState = context.getLevel().getFluidState(context.getClickedPos());
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())).setValue(WATERLOGGED, replacedFluidState.is(Fluids.WATER));
   }

   protected BlockState rotate(final BlockState state, final Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   protected BlockState mirror(final BlockState state, final Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPE;
   }

   public WeatheringCopper.WeatherState getWeatheringState() {
      return this.weatheringState;
   }

   protected InteractionResult useItemOn(final ItemStack itemStack, final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final BlockHitResult hitResult) {
      if (itemStack.is(ItemTags.AXES)) {
         return InteractionResult.PASS;
      } else {
         this.updatePose(level, state, pos, player);
         return InteractionResult.SUCCESS;
      }
   }

   void updatePose(final Level level, final BlockState state, final BlockPos pos, final Player player) {
      level.playSound((Entity)null, pos, SoundEvents.COPPER_GOLEM_BECOME_STATUE, SoundSource.BLOCKS);
      level.setBlock(pos, (BlockState)state.setValue(POSE, ((Pose)state.getValue(POSE)).getNextPose()), 3);
      level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
   }

   protected boolean isPathfindable(final BlockState state, final PathComputationType type) {
      return type == PathComputationType.WATER && state.getFluidState().is(FluidTags.WATER);
   }

   public @Nullable BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      return new CopperGolemStatueBlockEntity(worldPosition, blockState);
   }

   public boolean shouldChangedStateKeepBlockEntity(final BlockState oldState) {
      return oldState.is(BlockTags.COPPER_GOLEM_STATUES);
   }

   protected boolean hasAnalogOutputSignal(final BlockState state) {
      return true;
   }

   protected int getAnalogOutputSignal(final BlockState state, final Level level, final BlockPos pos, final Direction direction) {
      return ((Pose)state.getValue(POSE)).ordinal() + 1;
   }

   protected ItemStack getCloneItemStack(final LevelReader level, final BlockPos pos, final BlockState state, final boolean includeData) {
      BlockEntity var6 = level.getBlockEntity(pos);
      if (var6 instanceof CopperGolemStatueBlockEntity entity) {
         return entity.getItem(this.asItem().getDefaultInstance(), (Pose)state.getValue(POSE));
      } else {
         return super.getCloneItemStack(level, pos, state, includeData);
      }
   }

   protected void affectNeighborsAfterRemoval(final BlockState state, final ServerLevel level, final BlockPos pos, final boolean movedByPiston) {
      level.updateNeighbourForOutputSignal(pos, state.getBlock());
   }

   protected FluidState getFluidState(final BlockState state) {
      return (Boolean)state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         ticks.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
   }

   static {
      FACING = BlockStateProperties.HORIZONTAL_FACING;
      POSE = BlockStateProperties.COPPER_GOLEM_POSE;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      SHAPE = Block.column(10.0, 0.0, 14.0);
   }

   public static enum Pose implements StringRepresentable {
      STANDING("standing"),
      SITTING("sitting"),
      RUNNING("running"),
      STAR("star");

      public static final IntFunction<Pose> BY_ID = ByIdMap.<Pose>continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final Codec<Pose> CODEC = StringRepresentable.<Pose>fromEnum(Pose::values);
      private final String name;

      private Pose(final String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      public Pose getNextPose() {
         return (Pose)BY_ID.apply(this.ordinal() + 1);
      }

      // $FF: synthetic method
      private static Pose[] $values() {
         return new Pose[]{STANDING, SITTING, RUNNING, STAR};
      }
   }
}
