package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class DriedGhastBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
   public static final int MAX_HYDRATION_LEVEL = 3;
   public static final IntegerProperty HYDRATION_LEVEL;
   public static final BooleanProperty WATERLOGGED;
   public static final int HYDRATION_TICK_DELAY = 5000;
   private static final VoxelShape SHAPE;

   public DriedGhastBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(HYDRATION_LEVEL, 0)).setValue(WATERLOGGED, false));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(FACING, HYDRATION_LEVEL, WATERLOGGED);
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         ticks.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
   }

   public VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPE;
   }

   public int getHydrationLevel(final BlockState state) {
      return (Integer)state.getValue(HYDRATION_LEVEL);
   }

   private boolean isReadyToSpawn(final BlockState state) {
      return this.getHydrationLevel(state) == 3;
   }

   protected void tick(final BlockState state, final ServerLevel level, final BlockPos position, final RandomSource random) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         this.tickWaterlogged(state, level, position, random);
      } else {
         int hydrationLevel = this.getHydrationLevel(state);
         if (hydrationLevel > 0) {
            level.setBlock(position, (BlockState)state.setValue(HYDRATION_LEVEL, hydrationLevel - 1), 2);
            level.gameEvent(GameEvent.BLOCK_CHANGE, position, GameEvent.Context.of(state));
         }

      }
   }

   private void tickWaterlogged(final BlockState state, final ServerLevel level, final BlockPos position, final RandomSource random) {
      if (!this.isReadyToSpawn(state)) {
         level.playSound((Entity)null, position, SoundEvents.DRIED_GHAST_TRANSITION, SoundSource.BLOCKS, 1.0F, 1.0F);
         level.setBlock(position, (BlockState)state.setValue(HYDRATION_LEVEL, this.getHydrationLevel(state) + 1), 2);
         level.gameEvent(GameEvent.BLOCK_CHANGE, position, GameEvent.Context.of(state));
      } else {
         this.spawnGhastling(level, position, state);
      }

   }

   private void spawnGhastling(final ServerLevel level, final BlockPos position, final BlockState state) {
      level.removeBlock(position, false);
      HappyGhast ghastling = (HappyGhast)EntityTypes.HAPPY_GHAST.create(level, (EntitySpawnReason)EntitySpawnReason.BREEDING);
      if (ghastling != null) {
         Vec3 spawnAt = Vec3.atBottomCenterOf(position);
         ghastling.setBaby(true);
         float blockRotation = Direction.getYRot((Direction)state.getValue(FACING));
         ghastling.setYHeadRot(blockRotation);
         ghastling.snapTo(spawnAt.x(), spawnAt.y(), spawnAt.z(), blockRotation, 0.0F);
         level.addFreshEntity(ghastling);
         level.playSound((Entity)null, ghastling, SoundEvents.GHASTLING_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F);
      }

   }

   public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
      double x = (double)pos.getX() + 0.5;
      double y = (double)pos.getY() + 0.5;
      double z = (double)pos.getZ() + 0.5;
      if (!(Boolean)state.getValue(WATERLOGGED)) {
         if (random.nextInt(40) == 0 && level.getBlockState(pos.below()).is(BlockTags.TRIGGERS_AMBIENT_DRIED_GHAST_BLOCK_SOUNDS)) {
            level.playLocalSound(x, y, z, SoundEvents.DRIED_GHAST_AMBIENT, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         }

         if (random.nextInt(6) == 0) {
            level.addParticle(ParticleTypes.WHITE_SMOKE, x, y, z, 0.0, 0.02, 0.0);
         }
      } else {
         if (random.nextInt(40) == 0) {
            level.playLocalSound(x, y, z, SoundEvents.DRIED_GHAST_AMBIENT_WATER, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         }

         if (random.nextInt(6) == 0) {
            level.addParticle(ParticleTypes.HAPPY_VILLAGER, x + (double)((random.nextFloat() * 2.0F - 1.0F) / 3.0F), y + 0.4, z + (double)((random.nextFloat() * 2.0F - 1.0F) / 3.0F), 0.0, (double)random.nextFloat(), 0.0);
         }
      }

   }

   protected void randomTick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if (((Boolean)state.getValue(WATERLOGGED) || (Integer)state.getValue(HYDRATION_LEVEL) > 0) && !level.getBlockTicks().hasScheduledTick(pos, this)) {
         level.scheduleTick(pos, this, 5000);
      }

   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      FluidState replacedFluidState = context.getLevel().getFluidState(context.getClickedPos());
      boolean isWaterSource = replacedFluidState.is(Fluids.WATER);
      return (BlockState)((BlockState)super.getStateForPlacement(context).setValue(WATERLOGGED, isWaterSource)).setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   protected FluidState getFluidState(final BlockState state) {
      return (Boolean)state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public boolean placeLiquid(final LevelAccessor level, final BlockPos pos, final BlockState state, final FluidState fluidState) {
      if (!(Boolean)state.getValue(BlockStateProperties.WATERLOGGED) && fluidState.is(Fluids.WATER)) {
         if (!level.isClientSide()) {
            level.setBlockAndUpdate(pos, (BlockState)state.setValue(BlockStateProperties.WATERLOGGED, true));
            level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
            level.playSound((Entity)null, pos, SoundEvents.DRIED_GHAST_PLACE_IN_WATER, SoundSource.BLOCKS, 1.0F, 1.0F);
         }

         return true;
      } else {
         return false;
      }
   }

   public void setPlacedBy(final Level level, final BlockPos pos, final BlockState state, final @Nullable LivingEntity by, final ItemStack itemStack) {
      super.setPlacedBy(level, pos, state, by, itemStack);
      level.playSound((Entity)null, (BlockPos)pos, (Boolean)state.getValue(WATERLOGGED) ? SoundEvents.DRIED_GHAST_PLACE_IN_WATER : SoundEvents.DRIED_GHAST_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
   }

   public boolean isPathfindable(final BlockState state, final PathComputationType type) {
      return false;
   }

   static {
      HYDRATION_LEVEL = BlockStateProperties.DRIED_GHAST_HYDRATION_LEVELS;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      SHAPE = Block.column(10.0, 10.0, 0.0, 10.0);
   }
}
