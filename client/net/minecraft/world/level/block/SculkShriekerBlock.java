package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.SculkShriekerBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class SculkShriekerBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final BooleanProperty SHRIEKING;
   public static final BooleanProperty WATERLOGGED;
   public static final BooleanProperty CAN_SUMMON;
   private static final VoxelShape SHAPE_COLLISION;
   public static final double TOP_Y;

   public SculkShriekerBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(SHRIEKING, false)).setValue(WATERLOGGED, false)).setValue(CAN_SUMMON, false));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(SHRIEKING);
      builder.add(WATERLOGGED);
      builder.add(CAN_SUMMON);
   }

   public void stepOn(final Level level, final BlockPos pos, final BlockState onState, final Entity entity) {
      if (level instanceof ServerLevel serverLevel) {
         ServerPlayer player = SculkShriekerBlockEntity.tryGetPlayer(entity);
         if (player != null) {
            serverLevel.getBlockEntity(pos, BlockEntityTypes.SCULK_SHRIEKER).ifPresent((shrieker) -> shrieker.tryShriek(serverLevel, player));
         }
      }

      super.stepOn(level, pos, onState, entity);
   }

   protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if ((Boolean)state.getValue(SHRIEKING)) {
         level.setBlockAndUpdate(pos, (BlockState)state.setValue(SHRIEKING, false));
         level.getBlockEntity(pos, BlockEntityTypes.SCULK_SHRIEKER).ifPresent((shrieker) -> shrieker.tryRespond(level));
      }

   }

   protected VoxelShape getCollisionShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPE_COLLISION;
   }

   protected VoxelShape getOcclusionShape(final BlockState state) {
      return SHAPE_COLLISION;
   }

   protected boolean useShapeForLightOcclusion(final BlockState state) {
      return true;
   }

   public @Nullable BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      return new SculkShriekerBlockEntity(worldPosition, blockState);
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         ticks.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
   }

   public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).is(Fluids.WATER));
   }

   protected FluidState getFluidState(final BlockState state) {
      return (Boolean)state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   protected void spawnAfterBreak(final BlockState state, final ServerLevel level, final BlockPos pos, final ItemStack tool, final boolean dropExperience) {
      super.spawnAfterBreak(state, level, pos, tool, dropExperience);
      if (dropExperience) {
         this.tryDropExperience(level, pos, tool, ConstantInt.of(5));
      }

   }

   public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(final Level level, final BlockState blockState, final BlockEntityType<T> type) {
      return !level.isClientSide() ? BaseEntityBlock.createTickerHelper(type, BlockEntityTypes.SCULK_SHRIEKER, (innerLevel, pos, state, entity) -> VibrationSystem.Ticker.tick(innerLevel, entity.getVibrationData(), entity.getVibrationUser())) : null;
   }

   static {
      SHRIEKING = BlockStateProperties.SHRIEKING;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      CAN_SUMMON = BlockStateProperties.CAN_SUMMON;
      SHAPE_COLLISION = Block.column(16.0, 0.0, 8.0);
      TOP_Y = SHAPE_COLLISION.max(Direction.Axis.Y);
   }
}
