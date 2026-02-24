package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class LiquidBlock extends Block implements BucketPickup {
   private static final Codec<FlowingFluid> FLOWING_FLUID;
   public static final MapCodec<LiquidBlock> CODEC;
   public static final IntegerProperty LEVEL;
   protected final FlowingFluid fluid;
   private final List<FluidState> stateCache;
   public static final ImmutableList<Direction> POSSIBLE_FLOW_DIRECTIONS;
   private static final int BUBBLE_COLUMN_CHECK_DELAY = 20;

   public MapCodec<LiquidBlock> codec() {
      return CODEC;
   }

   protected LiquidBlock(final FlowingFluid fluid, final BlockBehaviour.Properties properties) {
      super(properties);
      this.fluid = fluid;
      this.stateCache = Lists.newArrayList();
      this.stateCache.add(fluid.getSource(false));

      for(int level = 1; level < 8; ++level) {
         this.stateCache.add(fluid.getFlowing(8 - level, false));
      }

      this.stateCache.add(fluid.getFlowing(8, true));
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LEVEL, 0));
   }

   protected VoxelShape getCollisionShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      if (context.alwaysCollideWithFluid()) {
         return Shapes.block();
      } else {
         return (Integer)state.getValue(LEVEL) != 0 ? Shapes.empty() : (VoxelShape)this.ifMobIsColliding(context).map(LivingEntity::getLiquidCollisionShape).filter((liquidStableShape) -> context.isAbove(liquidStableShape, pos, true) && context.canStandOnFluid(level.getFluidState(pos.above()), state.getFluidState())).orElse(Shapes.empty());
      }
   }

   private Optional<LivingEntity> ifMobIsColliding(final CollisionContext context) {
      if (context instanceof EntityCollisionContext entityCollisionContext) {
         Entity var4 = entityCollisionContext.getEntity();
         if (var4 instanceof LivingEntity mob) {
            return Optional.of(mob);
         }
      }

      return Optional.empty();
   }

   protected boolean isRandomlyTicking(final BlockState state) {
      return state.getFluidState().isRandomlyTicking();
   }

   protected void randomTick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      state.getFluidState().randomTick(level, pos, random);
   }

   protected boolean propagatesSkylightDown(final BlockState state) {
      return false;
   }

   protected boolean isPathfindable(final BlockState state, final PathComputationType type) {
      return !this.fluid.is(FluidTags.LAVA);
   }

   protected FluidState getFluidState(final BlockState state) {
      int level = (Integer)state.getValue(LEVEL);
      return (FluidState)this.stateCache.get(Math.min(level, 8));
   }

   protected boolean skipRendering(final BlockState state, final BlockState neighborState, final Direction direction) {
      return neighborState.getFluidState().getType().isSame(this.fluid);
   }

   protected RenderShape getRenderShape(final BlockState state) {
      return RenderShape.INVISIBLE;
   }

   protected List<ItemStack> getDrops(final BlockState state, final LootParams.Builder params) {
      return Collections.emptyList();
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return Shapes.empty();
   }

   protected void onPlace(final BlockState state, final Level level, final BlockPos pos, final BlockState oldState, final boolean movedByPiston) {
      if (this.shouldSpreadLiquid(level, pos, state)) {
         level.scheduleTick(pos, state.getFluidState().getType(), this.fluid.getTickDelay(level));
      }

      if (shouldBubbleColumnOccupy(state)) {
         BlockState stateBelow = level.getBlockState(pos.below());
         this.tryScheduleBubbleBlockColumn(level, pos, stateBelow);
      }

   }

   protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if (shouldBubbleColumnOccupy(state)) {
         BlockState stateBelow = level.getBlockState(pos.below());
         BubbleColumnBlock.updateColumn(Blocks.BUBBLE_COLUMN, level, pos, stateBelow);
      }

   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if (state.getFluidState().isSource() || neighbourState.getFluidState().isSource()) {
         ticks.scheduleTick(pos, state.getFluidState().getType(), this.fluid.getTickDelay(level));
      }

      if (directionToNeighbour == Direction.DOWN && shouldBubbleColumnOccupy(state)) {
         this.tryScheduleBubbleBlockColumn(ticks, pos, neighbourState);
      }

      return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
   }

   private static boolean shouldBubbleColumnOccupy(final BlockState state) {
      return state.getFluidState().is(FluidTags.BUBBLE_COLUMN_CAN_OCCUPY) && state.getFluidState().isSource() && state.getFluidState().isFull();
   }

   protected void neighborChanged(final BlockState state, final Level level, final BlockPos pos, final Block block, final @Nullable Orientation orientation, final boolean movedByPiston) {
      if (this.shouldSpreadLiquid(level, pos, state)) {
         level.scheduleTick(pos, state.getFluidState().getType(), this.fluid.getTickDelay(level));
      }

      if (shouldBubbleColumnOccupy(state)) {
         BlockState stateBelow = level.getBlockState(pos.below());
         this.tryScheduleBubbleBlockColumn(level, pos, stateBelow);
      }

   }

   private void tryScheduleBubbleBlockColumn(final ScheduledTickAccess ticks, final BlockPos pos, final BlockState stateBelow) {
      if (stateBelow.is(BlockTags.ENABLES_BUBBLE_COLUMN_DRAG_DOWN) || stateBelow.is(BlockTags.ENABLES_BUBBLE_COLUMN_PUSH_UP)) {
         ticks.scheduleTick(pos, (Block)this, 20);
      }

   }

   private boolean shouldSpreadLiquid(final Level level, final BlockPos pos, final BlockState state) {
      if (this.fluid.is(FluidTags.LAVA)) {
         boolean isOverSoulSoil = level.getBlockState(pos.below()).is(Blocks.SOUL_SOIL);
         UnmodifiableIterator var5 = POSSIBLE_FLOW_DIRECTIONS.iterator();

         while(var5.hasNext()) {
            Direction direction = (Direction)var5.next();
            BlockPos neighbourPos = pos.relative(direction.getOpposite());
            if (level.getFluidState(neighbourPos).is(FluidTags.WATER)) {
               Block convertToBlock = level.getFluidState(pos).isSource() ? Blocks.OBSIDIAN : Blocks.COBBLESTONE;
               level.setBlockAndUpdate(pos, convertToBlock.defaultBlockState());
               this.fizz(level, pos);
               return false;
            }

            if (isOverSoulSoil && level.getBlockState(neighbourPos).is(Blocks.BLUE_ICE)) {
               level.setBlockAndUpdate(pos, Blocks.BASALT.defaultBlockState());
               this.fizz(level, pos);
               return false;
            }
         }
      }

      return true;
   }

   private void fizz(final LevelAccessor level, final BlockPos pos) {
      level.levelEvent(1501, pos, 0);
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(LEVEL);
   }

   public ItemStack pickupBlock(final @Nullable LivingEntity user, final LevelAccessor level, final BlockPos pos, final BlockState state) {
      if ((Integer)state.getValue(LEVEL) == 0) {
         level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
         return new ItemStack(this.fluid.getBucket());
      } else {
         return ItemStack.EMPTY;
      }
   }

   public Optional<SoundEvent> getPickupSound() {
      return this.fluid.getPickupSound();
   }

   static {
      FLOWING_FLUID = BuiltInRegistries.FLUID.byNameCodec().comapFlatMap((fluid) -> {
         DataResult var10000;
         if (fluid instanceof FlowingFluid flowing) {
            var10000 = DataResult.success(flowing);
         } else {
            var10000 = DataResult.error(() -> "Not a flowing fluid: " + String.valueOf(fluid));
         }

         return var10000;
      }, (fluid) -> fluid);
      CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(FLOWING_FLUID.fieldOf("fluid").forGetter((b) -> b.fluid), propertiesCodec()).apply(i, LiquidBlock::new));
      LEVEL = BlockStateProperties.LEVEL;
      POSSIBLE_FLOW_DIRECTIONS = ImmutableList.of(Direction.DOWN, Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST);
   }
}
