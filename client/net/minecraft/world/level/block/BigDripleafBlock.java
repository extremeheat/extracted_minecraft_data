package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.Tilt;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class BigDripleafBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock, BonemealableBlock {
   public static final MapCodec<BigDripleafBlock> CODEC = simpleCodec(BigDripleafBlock::new);
   private static final BooleanProperty WATERLOGGED;
   private static final EnumProperty<Tilt> TILT;
   private static final int NO_TICK = -1;
   private static final Object2IntMap<Tilt> DELAY_UNTIL_NEXT_TILT_STATE;
   private static final int MAX_GEN_HEIGHT = 5;
   private static final int ENTITY_DETECTION_MIN_Y = 11;
   private static final int LOWEST_LEAF_TOP = 13;
   private static final Map<Tilt, VoxelShape> SHAPE_LEAF;
   private final Function<BlockState, VoxelShape> shapes;

   public MapCodec<BigDripleafBlock> codec() {
      return CODEC;
   }

   protected BigDripleafBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, false)).setValue(FACING, Direction.NORTH)).setValue(TILT, Tilt.NONE));
      this.shapes = this.makeShapes();
   }

   private Function<BlockState, VoxelShape> makeShapes() {
      Map<Direction, VoxelShape> stems = Shapes.rotateHorizontal(Block.column(6.0, 0.0, 13.0).move(0.0, 0.0, 0.25).optimize());
      return this.getShapeForEachState((state) -> Shapes.or((VoxelShape)SHAPE_LEAF.get(state.getValue(TILT)), (VoxelShape)stems.get(state.getValue(FACING))), new Property[]{WATERLOGGED});
   }

   public static void placeWithRandomHeight(final LevelAccessor level, final RandomSource random, final BlockPos stemBottomPos, final Direction facing) {
      int desiredHeight = Mth.nextInt(random, 2, 5);
      BlockPos.MutableBlockPos pos = stemBottomPos.mutable();
      int height = 0;

      while(height < desiredHeight && canPlaceAt(level, pos)) {
         ++height;
         pos.move(Direction.UP);
      }

      int leafY = stemBottomPos.getY() + height - 1;
      pos.setY(stemBottomPos.getY());

      while(pos.getY() < leafY) {
         BigDripleafStemBlock.place(level, pos, level.getFluidState(pos), facing);
         pos.move(Direction.UP);
      }

      place(level, pos, level.getFluidState(pos), facing);
   }

   private static boolean canReplace(final BlockState oldState) {
      return oldState.isAir() || oldState.is(Blocks.WATER) || oldState.is(Blocks.SMALL_DRIPLEAF);
   }

   protected static boolean canPlaceAt(final LevelReader level, final BlockPos pos) {
      return canGrowInto(level, pos);
   }

   protected static boolean canGrowInto(final LevelReader level, final BlockPos pos) {
      BlockState state = level.getBlockState(pos);
      return level.isInsideBuildHeight(pos) && canReplace(state);
   }

   protected static boolean place(final LevelAccessor level, final BlockPos pos, final FluidState fluidState, final Direction facing) {
      BlockState newState = (BlockState)((BlockState)Blocks.BIG_DRIPLEAF.defaultBlockState().setValue(WATERLOGGED, fluidState.isSourceOfType(Fluids.WATER))).setValue(FACING, facing);
      return level.setBlock(pos, newState, 3);
   }

   protected void onProjectileHit(final Level level, final BlockState state, final BlockHitResult blockHit, final Projectile projectile) {
      this.setTiltAndScheduleTick(state, level, blockHit.getBlockPos(), Tilt.FULL, SoundEvents.BIG_DRIPLEAF_TILT_DOWN);
   }

   protected FluidState getFluidState(final BlockState state) {
      return (Boolean)state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
      BlockPos belowPos = pos.below();
      BlockState belowState = level.getBlockState(belowPos);
      return belowState.is(this) || belowState.is(Blocks.BIG_DRIPLEAF_STEM) || belowState.is(BlockTags.SUPPORTS_BIG_DRIPLEAF);
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if (directionToNeighbour == Direction.DOWN && !state.canSurvive(level, pos)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         if ((Boolean)state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(level));
         }

         return directionToNeighbour == Direction.UP && neighbourState.is(this) ? Blocks.BIG_DRIPLEAF_STEM.withPropertiesOf(state) : super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
      }
   }

   public boolean isValidBonemealTarget(final LevelReader level, final BlockPos pos, final BlockState state) {
      return canGrowInto(level, pos.above());
   }

   public boolean isBonemealSuccess(final Level level, final RandomSource random, final BlockPos pos, final BlockState state) {
      return true;
   }

   public void performBonemeal(final ServerLevel level, final RandomSource random, final BlockPos pos, final BlockState state) {
      BlockPos abovePos = pos.above();
      if (canPlaceAt(level, abovePos)) {
         Direction facing = (Direction)state.getValue(FACING);
         BigDripleafStemBlock.place(level, pos, state.getFluidState(), facing);
         place(level, abovePos, level.getBlockState(abovePos).getFluidState(), facing);
      }

   }

   protected void entityInside(final BlockState state, final Level level, final BlockPos pos, final Entity entity, final InsideBlockEffectApplier effectApplier, final boolean isPrecise) {
      if (!level.isClientSide()) {
         if (state.getValue(TILT) == Tilt.NONE && canEntityTilt(pos, entity) && !level.hasNeighborSignal(pos)) {
            this.setTiltAndScheduleTick(state, level, pos, Tilt.UNSTABLE, (SoundEvent)null);
         }

      }
   }

   protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if (level.hasNeighborSignal(pos)) {
         resetTilt(state, level, pos);
      } else {
         Tilt tilt = (Tilt)state.getValue(TILT);
         if (tilt == Tilt.UNSTABLE) {
            this.setTiltAndScheduleTick(state, level, pos, Tilt.PARTIAL, SoundEvents.BIG_DRIPLEAF_TILT_DOWN);
         } else if (tilt == Tilt.PARTIAL) {
            this.setTiltAndScheduleTick(state, level, pos, Tilt.FULL, SoundEvents.BIG_DRIPLEAF_TILT_DOWN);
         } else if (tilt == Tilt.FULL) {
            resetTilt(state, level, pos);
         }

      }
   }

   protected void neighborChanged(final BlockState state, final Level level, final BlockPos pos, final Block block, final @Nullable Orientation orientation, final boolean movedByPiston) {
      if (level.hasNeighborSignal(pos)) {
         resetTilt(state, level, pos);
      }

   }

   private static void playTiltSound(final Level level, final BlockPos pos, final SoundEvent tiltSound) {
      float pitch = Mth.randomBetween(level.getRandom(), 0.8F, 1.2F);
      level.playSound((Entity)null, (BlockPos)pos, tiltSound, SoundSource.BLOCKS, 1.0F, pitch);
   }

   private static boolean canEntityTilt(final BlockPos pos, final Entity entity) {
      return entity.onGround() && entity.position().y > (double)((float)pos.getY() + 0.6875F);
   }

   private void setTiltAndScheduleTick(final BlockState state, final Level level, final BlockPos pos, final Tilt tilt, final @Nullable SoundEvent sound) {
      setTilt(state, level, pos, tilt);
      if (sound != null) {
         playTiltSound(level, pos, sound);
      }

      int tickDelay = DELAY_UNTIL_NEXT_TILT_STATE.getInt(tilt);
      if (tickDelay != -1) {
         level.scheduleTick(pos, this, tickDelay);
      }

   }

   private static void resetTilt(final BlockState state, final Level level, final BlockPos pos) {
      setTilt(state, level, pos, Tilt.NONE);
      if (state.getValue(TILT) != Tilt.NONE) {
         playTiltSound(level, pos, SoundEvents.BIG_DRIPLEAF_TILT_UP);
      }

   }

   private static void setTilt(final BlockState state, final Level level, final BlockPos pos, final Tilt tilt) {
      Tilt previousTilt = (Tilt)state.getValue(TILT);
      level.setBlock(pos, (BlockState)state.setValue(TILT, tilt), 2);
      if (tilt.causesVibration() && tilt != previousTilt) {
         level.gameEvent((Entity)null, GameEvent.BLOCK_CHANGE, pos);
      }

   }

   protected VoxelShape getCollisionShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return (VoxelShape)SHAPE_LEAF.get(state.getValue(TILT));
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return (VoxelShape)this.shapes.apply(state);
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      BlockState belowState = context.getLevel().getBlockState(context.getClickedPos().below());
      FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
      boolean belowIsDripleafPart = belowState.is(Blocks.BIG_DRIPLEAF) || belowState.is(Blocks.BIG_DRIPLEAF_STEM);
      return (BlockState)((BlockState)this.defaultBlockState().setValue(WATERLOGGED, fluidState.isSourceOfType(Fluids.WATER))).setValue(FACING, belowIsDripleafPart ? (Direction)belowState.getValue(FACING) : context.getHorizontalDirection().getOpposite());
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(WATERLOGGED, FACING, TILT);
   }

   static {
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      TILT = BlockStateProperties.TILT;
      DELAY_UNTIL_NEXT_TILT_STATE = (Object2IntMap)Util.make(new Object2IntArrayMap(), (map) -> {
         map.defaultReturnValue(-1);
         map.put(Tilt.UNSTABLE, 10);
         map.put(Tilt.PARTIAL, 10);
         map.put(Tilt.FULL, 100);
      });
      SHAPE_LEAF = Maps.newEnumMap(Map.of(Tilt.NONE, Block.column(16.0, 11.0, 15.0), Tilt.UNSTABLE, Block.column(16.0, 11.0, 15.0), Tilt.PARTIAL, Block.column(16.0, 11.0, 13.0), Tilt.FULL, Shapes.empty()));
   }
}
