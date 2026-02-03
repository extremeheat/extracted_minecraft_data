package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class BubbleColumnBlock extends Block implements BucketPickup {
   public static final MapCodec<BubbleColumnBlock> CODEC = simpleCodec(BubbleColumnBlock::new);
   public static final BooleanProperty DRAG_DOWN;
   private static final int CHECK_PERIOD = 5;

   public MapCodec<BubbleColumnBlock> codec() {
      return CODEC;
   }

   public BubbleColumnBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(DRAG_DOWN, true));
   }

   protected void entityInside(final BlockState state, final Level level, final BlockPos pos, final Entity entity, final InsideBlockEffectApplier effectApplier, final boolean isPrecise) {
      if (isPrecise) {
         BlockState stateAbove = level.getBlockState(pos.above());
         boolean nothingAbove = stateAbove.getCollisionShape(level, pos).isEmpty() && stateAbove.getFluidState().isEmpty();
         if (nothingAbove) {
            entity.onAboveBubbleColumn((Boolean)state.getValue(DRAG_DOWN), pos);
         } else {
            entity.onInsideBubbleColumn((Boolean)state.getValue(DRAG_DOWN));
         }
      }

   }

   protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      updateColumn(this, level, pos, state, level.getBlockState(pos.below()));
   }

   protected FluidState getFluidState(final BlockState state) {
      return Fluids.WATER.getSource(false);
   }

   public static void updateColumn(final Block bubbleColumn, final LevelAccessor level, final BlockPos occupyAt, final BlockState belowState) {
      updateColumn(bubbleColumn, level, occupyAt, level.getBlockState(occupyAt), belowState);
   }

   public static void updateColumn(final Block bubbleColumn, final LevelAccessor level, final BlockPos occupyAt, final BlockState occupyState, final BlockState belowState) {
      if (canOccupy(bubbleColumn, occupyState)) {
         BlockState columnState = getColumnState(bubbleColumn, belowState, occupyState);
         level.setBlock(occupyAt, columnState, 2);
         BlockPos.MutableBlockPos pos = occupyAt.mutable().move(Direction.UP);

         while(canOccupy(bubbleColumn, level.getBlockState(pos))) {
            if (!level.setBlock(pos, columnState, 2)) {
               return;
            }

            pos.move(Direction.UP);
         }

      }
   }

   private static boolean canOccupy(final Block bubbleColumn, final BlockState occupyState) {
      return occupyState.is(bubbleColumn) || occupyState.getFluidState().is(FluidTags.BUBBLE_COLUMN_CAN_OCCUPY) && occupyState.getFluidState().isSource() && occupyState.getFluidState().getAmount() >= 8;
   }

   private static BlockState getColumnState(final Block bubbleColumn, final BlockState belowState, final BlockState occupyState) {
      if (belowState.is(bubbleColumn)) {
         return belowState;
      } else if (belowState.is(BlockTags.ENABLES_BUBBLE_COLUMN_PUSH_UP)) {
         return (BlockState)bubbleColumn.defaultBlockState().setValue(DRAG_DOWN, false);
      } else if (belowState.is(BlockTags.ENABLES_BUBBLE_COLUMN_DRAG_DOWN)) {
         return (BlockState)bubbleColumn.defaultBlockState().setValue(DRAG_DOWN, true);
      } else {
         return occupyState.is(bubbleColumn) ? Blocks.WATER.defaultBlockState() : occupyState;
      }
   }

   public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
      double x = (double)pos.getX();
      double y = (double)pos.getY();
      double z = (double)pos.getZ();
      if ((Boolean)state.getValue(DRAG_DOWN)) {
         level.addAlwaysVisibleParticle(ParticleTypes.CURRENT_DOWN, x + 0.5, y + 0.8, z, 0.0, 0.0, 0.0);
         if (random.nextInt(200) == 0) {
            level.playLocalSound(x, y, z, SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
         }
      } else {
         level.addAlwaysVisibleParticle(ParticleTypes.BUBBLE_COLUMN_UP, x + 0.5, y, z + 0.5, 0.0, 0.04, 0.0);
         level.addAlwaysVisibleParticle(ParticleTypes.BUBBLE_COLUMN_UP, x + (double)random.nextFloat(), y + (double)random.nextFloat(), z + (double)random.nextFloat(), 0.0, 0.04, 0.0);
         if (random.nextInt(200) == 0) {
            level.playLocalSound(x, y, z, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
         }
      }

   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      ticks.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(level));
      if (!state.canSurvive(level, pos) || directionToNeighbour == Direction.DOWN || directionToNeighbour == Direction.UP && !neighbourState.is(this) && canOccupy(this, neighbourState)) {
         ticks.scheduleTick(pos, (Block)this, 5);
      }

      return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
   }

   protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
      BlockState belowState = level.getBlockState(pos.below());
      return belowState.is(this) || belowState.is(BlockTags.ENABLES_BUBBLE_COLUMN_PUSH_UP) || belowState.is(BlockTags.ENABLES_BUBBLE_COLUMN_DRAG_DOWN);
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return Shapes.empty();
   }

   protected RenderShape getRenderShape(final BlockState state) {
      return RenderShape.INVISIBLE;
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(DRAG_DOWN);
   }

   public ItemStack pickupBlock(final @Nullable LivingEntity user, final LevelAccessor level, final BlockPos pos, final BlockState state) {
      level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
      return new ItemStack(Items.WATER_BUCKET);
   }

   public Optional<SoundEvent> getPickupSound() {
      return Fluids.WATER.getPickupSound();
   }

   static {
      DRAG_DOWN = BlockStateProperties.DRAG;
   }
}
