package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class FarmlandBlock extends Block {
   public static final MapCodec<FarmlandBlock> CODEC = simpleCodec(FarmlandBlock::new);
   public static final IntegerProperty MOISTURE;
   private static final VoxelShape SHAPE;
   public static final int MAX_MOISTURE = 7;

   public MapCodec<FarmlandBlock> codec() {
      return CODEC;
   }

   protected FarmlandBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(MOISTURE, 0));
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if (directionToNeighbour == Direction.UP && !state.canSurvive(level, pos)) {
         ticks.scheduleTick(pos, (Block)this, 1);
      }

      return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
   }

   protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
      BlockState aboveState = level.getBlockState(pos.above());
      return !aboveState.isSolid() || aboveState.getBlock() instanceof FenceGateBlock || aboveState.getBlock() instanceof MovingPistonBlock;
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      return !this.defaultBlockState().canSurvive(context.getLevel(), context.getClickedPos()) ? Blocks.DIRT.defaultBlockState() : super.getStateForPlacement(context);
   }

   protected boolean useShapeForLightOcclusion(final BlockState state) {
      return true;
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPE;
   }

   protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if (!state.canSurvive(level, pos)) {
         turnToDirt((Entity)null, state, level, pos);
      }

   }

   protected void randomTick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      int moisture = (Integer)state.getValue(MOISTURE);
      if (!isNearWater(level, pos) && !level.isRainingAt(pos.above())) {
         if (moisture > 0) {
            level.setBlock(pos, (BlockState)state.setValue(MOISTURE, moisture - 1), 2);
         } else if (!shouldMaintainFarmland(level, pos)) {
            turnToDirt((Entity)null, state, level, pos);
         }
      } else if (moisture < 7) {
         level.setBlock(pos, (BlockState)state.setValue(MOISTURE, 7), 2);
      }

   }

   public void fallOn(final Level level, final BlockState state, final BlockPos pos, final Entity entity, final double fallDistance) {
      if (level instanceof ServerLevel serverLevel) {
         if ((double)level.getRandom().nextFloat() < fallDistance - 0.5 && entity instanceof LivingEntity && (entity instanceof Player || (Boolean)serverLevel.getGameRules().get(GameRules.MOB_GRIEFING)) && entity.getBbWidth() * entity.getBbWidth() * entity.getBbHeight() > 0.512F) {
            turnToDirt(entity, state, level, pos);
         }
      }

      super.fallOn(level, state, pos, entity, fallDistance);
   }

   public static void turnToDirt(final @Nullable Entity sourceEntity, final BlockState state, final Level level, final BlockPos pos) {
      BlockState newState = pushEntitiesUp(state, Blocks.DIRT.defaultBlockState(), level, pos);
      level.setBlockAndUpdate(pos, newState);
      level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(sourceEntity, newState));
   }

   private static boolean shouldMaintainFarmland(final BlockGetter level, final BlockPos pos) {
      return level.getBlockState(pos.above()).is(BlockTags.MAINTAINS_FARMLAND);
   }

   private static boolean isNearWater(final LevelReader level, final BlockPos pos) {
      for(BlockPos blockPos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
         if (level.getFluidState(blockPos).is(FluidTags.WATER)) {
            return true;
         }
      }

      return false;
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(MOISTURE);
   }

   protected boolean isPathfindable(final BlockState state, final PathComputationType type) {
      return false;
   }

   static {
      MOISTURE = BlockStateProperties.MOISTURE;
      SHAPE = Block.column(16.0, 0.0, 15.0);
   }
}
