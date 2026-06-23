package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.function.ToIntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CandleBlock extends AbstractCandleBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<CandleBlock> CODEC = simpleCodec(CandleBlock::new);
   public static final int MIN_CANDLES = 1;
   public static final int MAX_CANDLES = 4;
   public static final IntegerProperty CANDLES;
   public static final BooleanProperty LIT;
   public static final BooleanProperty WATERLOGGED;
   public static final ToIntFunction<BlockState> LIGHT_EMISSION;
   private static final Int2ObjectMap<List<Vec3>> PARTICLE_OFFSETS;
   private static final VoxelShape[] SHAPES;

   public MapCodec<CandleBlock> codec() {
      return CODEC;
   }

   public CandleBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(CANDLES, 1)).setValue(LIT, false)).setValue(WATERLOGGED, false));
   }

   protected InteractionResult useItemOn(final ItemStack itemStack, final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final BlockHitResult hitResult) {
      if (itemStack.isEmpty() && player.getAbilities().mayBuild && (Boolean)state.getValue(LIT)) {
         extinguish(player, state, level, pos);
         return InteractionResult.SUCCESS;
      } else {
         return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
      }
   }

   protected boolean canBeReplaced(final BlockState state, final BlockPlaceContext context) {
      return !context.isSecondaryUseActive() && context.getItemInHand().getItem() == this.asItem() && (Integer)state.getValue(CANDLES) < 4 ? true : super.canBeReplaced(state, context);
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      BlockState state = context.getLevel().getBlockState(context.getClickedPos());
      if (state.is(this)) {
         return (BlockState)state.cycle(CANDLES);
      } else {
         FluidState replacedFluidState = context.getLevel().getFluidState(context.getClickedPos());
         boolean isWaterSource = replacedFluidState.is(Fluids.WATER);
         return (BlockState)super.getStateForPlacement(context).setValue(WATERLOGGED, isWaterSource);
      }
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

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPES[(Integer)state.getValue(CANDLES) - 1];
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(CANDLES, LIT, WATERLOGGED);
   }

   public boolean placeLiquid(final LevelAccessor level, final BlockPos pos, final BlockState state, final FluidState fluidState) {
      if (!(Boolean)state.getValue(WATERLOGGED) && fluidState.is(Fluids.WATER)) {
         BlockState newState = (BlockState)state.setValue(WATERLOGGED, true);
         if ((Boolean)state.getValue(LIT)) {
            extinguish((Player)null, newState, level, pos);
         } else {
            level.setBlockAndUpdate(pos, newState);
         }

         level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
         return true;
      } else {
         return false;
      }
   }

   public static boolean canLight(final BlockState state) {
      return state.is(BlockTags.CANDLES, (s) -> s.hasProperty(LIT) && s.hasProperty(WATERLOGGED)) && !(Boolean)state.getValue(LIT) && !(Boolean)state.getValue(WATERLOGGED);
   }

   protected Iterable<Vec3> getParticleOffsets(final BlockState state) {
      return (Iterable)PARTICLE_OFFSETS.get((Integer)state.getValue(CANDLES));
   }

   protected boolean canBeLit(final BlockState state) {
      return !(Boolean)state.getValue(WATERLOGGED) && super.canBeLit(state);
   }

   protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
      return Block.canSupportCenter(level, pos.below(), Direction.UP);
   }

   static {
      CANDLES = BlockStateProperties.CANDLES;
      LIT = AbstractCandleBlock.LIT;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      LIGHT_EMISSION = (state) -> (Boolean)state.getValue(LIT) ? 3 * (Integer)state.getValue(CANDLES) : 0;
      PARTICLE_OFFSETS = (Int2ObjectMap)Util.make(new Int2ObjectOpenHashMap(4), (map) -> {
         float s = 0.0625F;
         map.put(1, List.of((new Vec3(8.0, 8.0, 8.0)).scale(0.0625)));
         map.put(2, List.of((new Vec3(6.0, 7.0, 8.0)).scale(0.0625), (new Vec3(10.0, 8.0, 7.0)).scale(0.0625)));
         map.put(3, List.of((new Vec3(8.0, 5.0, 10.0)).scale(0.0625), (new Vec3(6.0, 7.0, 8.0)).scale(0.0625), (new Vec3(9.0, 8.0, 7.0)).scale(0.0625)));
         map.put(4, List.of((new Vec3(7.0, 5.0, 9.0)).scale(0.0625), (new Vec3(10.0, 7.0, 9.0)).scale(0.0625), (new Vec3(6.0, 7.0, 6.0)).scale(0.0625), (new Vec3(9.0, 8.0, 6.0)).scale(0.0625)));
      });
      SHAPES = new VoxelShape[]{Block.column(2.0, 0.0, 6.0), Block.box(5.0, 0.0, 6.0, 11.0, 6.0, 9.0), Block.box(5.0, 0.0, 6.0, 10.0, 6.0, 11.0), Block.box(5.0, 0.0, 5.0, 11.0, 6.0, 10.0)};
   }
}
