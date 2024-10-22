package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Tilt;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BigDripleafBlock extends HorizontalDirectionalBlock implements BonemealableBlock, SimpleWaterloggedBlock {
   public static final MapCodec<BigDripleafBlock> CODEC = simpleCodec(BigDripleafBlock::new);
   private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   private static final EnumProperty<Tilt> TILT = BlockStateProperties.TILT;
   private static final int NO_TICK = -1;
   private static final Object2IntMap<Tilt> DELAY_UNTIL_NEXT_TILT_STATE = Util.make(new Object2IntArrayMap(), var0 -> {
      var0.defaultReturnValue(-1);
      var0.put(Tilt.UNSTABLE, 10);
      var0.put(Tilt.PARTIAL, 10);
      var0.put(Tilt.FULL, 100);
   });
   private static final int MAX_GEN_HEIGHT = 5;
   private static final int STEM_WIDTH = 6;
   private static final int ENTITY_DETECTION_MIN_Y = 11;
   private static final int LOWEST_LEAF_TOP = 13;
   private static final Map<Tilt, VoxelShape> LEAF_SHAPES = ImmutableMap.of(
      Tilt.NONE,
      Block.box(0.0, 11.0, 0.0, 16.0, 15.0, 16.0),
      Tilt.UNSTABLE,
      Block.box(0.0, 11.0, 0.0, 16.0, 15.0, 16.0),
      Tilt.PARTIAL,
      Block.box(0.0, 11.0, 0.0, 16.0, 13.0, 16.0),
      Tilt.FULL,
      Shapes.empty()
   );
   private static final VoxelShape STEM_SLICER = Block.box(0.0, 13.0, 0.0, 16.0, 16.0, 16.0);
   private static final Map<Direction, VoxelShape> STEM_SHAPES = ImmutableMap.of(
      Direction.NORTH,
      Shapes.joinUnoptimized(BigDripleafStemBlock.NORTH_SHAPE, STEM_SLICER, BooleanOp.ONLY_FIRST),
      Direction.SOUTH,
      Shapes.joinUnoptimized(BigDripleafStemBlock.SOUTH_SHAPE, STEM_SLICER, BooleanOp.ONLY_FIRST),
      Direction.EAST,
      Shapes.joinUnoptimized(BigDripleafStemBlock.EAST_SHAPE, STEM_SLICER, BooleanOp.ONLY_FIRST),
      Direction.WEST,
      Shapes.joinUnoptimized(BigDripleafStemBlock.WEST_SHAPE, STEM_SLICER, BooleanOp.ONLY_FIRST)
   );
   private final Map<BlockState, VoxelShape> shapesCache;

   @Override
   public MapCodec<BigDripleafBlock> codec() {
      return CODEC;
   }

   protected BigDripleafBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(
         this.stateDefinition.any().setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(FACING, Direction.NORTH).setValue(TILT, Tilt.NONE)
      );
      this.shapesCache = this.getShapeForEachState(BigDripleafBlock::calculateShape);
   }

   private static VoxelShape calculateShape(BlockState var0) {
      return Shapes.or(LEAF_SHAPES.get(var0.getValue(TILT)), STEM_SHAPES.get(var0.getValue(FACING)));
   }

   public static void placeWithRandomHeight(LevelAccessor var0, RandomSource var1, BlockPos var2, Direction var3) {
      int var4 = Mth.nextInt(var1, 2, 5);
      BlockPos.MutableBlockPos var5 = var2.mutable();
      int var6 = 0;

      while (var6 < var4 && canPlaceAt(var0, var5, var0.getBlockState(var5))) {
         var6++;
         var5.move(Direction.UP);
      }

      int var7 = var2.getY() + var6 - 1;
      var5.setY(var2.getY());

      while (var5.getY() < var7) {
         BigDripleafStemBlock.place(var0, var5, var0.getFluidState(var5), var3);
         var5.move(Direction.UP);
      }

      place(var0, var5, var0.getFluidState(var5), var3);
   }

   private static boolean canReplace(BlockState var0) {
      return var0.isAir() || var0.is(Blocks.WATER) || var0.is(Blocks.SMALL_DRIPLEAF);
   }

   protected static boolean canPlaceAt(LevelHeightAccessor var0, BlockPos var1, BlockState var2) {
      return !var0.isOutsideBuildHeight(var1) && canReplace(var2);
   }

   protected static boolean place(LevelAccessor var0, BlockPos var1, FluidState var2, Direction var3) {
      BlockState var4 = Blocks.BIG_DRIPLEAF
         .defaultBlockState()
         .setValue(WATERLOGGED, Boolean.valueOf(var2.isSourceOfType(Fluids.WATER)))
         .setValue(FACING, var3);
      return var0.setBlock(var1, var4, 3);
   }

   @Override
   protected void onProjectileHit(Level var1, BlockState var2, BlockHitResult var3, Projectile var4) {
      this.setTiltAndScheduleTick(var2, var1, var3.getBlockPos(), Tilt.FULL, SoundEvents.BIG_DRIPLEAF_TILT_DOWN);
   }

   @Override
   protected FluidState getFluidState(BlockState var1) {
      return var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   @Override
   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockPos var4 = var3.below();
      BlockState var5 = var2.getBlockState(var4);
      return var5.is(this) || var5.is(Blocks.BIG_DRIPLEAF_STEM) || var5.is(BlockTags.BIG_DRIPLEAF_PLACEABLE);
   }

   @Override
   protected BlockState updateShape(
      BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8
   ) {
      if (var5 == Direction.DOWN && !var1.canSurvive(var2, var4)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         if (var1.getValue(WATERLOGGED)) {
            var3.scheduleTick(var4, Fluids.WATER, Fluids.WATER.getTickDelay(var2));
         }

         return var5 == Direction.UP && var7.is(this)
            ? Blocks.BIG_DRIPLEAF_STEM.withPropertiesOf(var1)
            : super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
      }
   }

   @Override
   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      BlockState var4 = var1.getBlockState(var2.above());
      return canReplace(var4);
   }

   @Override
   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   @Override
   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      BlockPos var5 = var3.above();
      BlockState var6 = var1.getBlockState(var5);
      if (canPlaceAt(var1, var5, var6)) {
         Direction var7 = var4.getValue(FACING);
         BigDripleafStemBlock.place(var1, var3, var4.getFluidState(), var7);
         place(var1, var5, var6.getFluidState(), var7);
      }
   }

   @Override
   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (!var2.isClientSide) {
         if (var1.getValue(TILT) == Tilt.NONE && canEntityTilt(var3, var4) && !var2.hasNeighborSignal(var3)) {
            this.setTiltAndScheduleTick(var1, var2, var3, Tilt.UNSTABLE, null);
         }
      }
   }

   @Override
   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (var2.hasNeighborSignal(var3)) {
         resetTilt(var1, var2, var3);
      } else {
         Tilt var5 = var1.getValue(TILT);
         if (var5 == Tilt.UNSTABLE) {
            this.setTiltAndScheduleTick(var1, var2, var3, Tilt.PARTIAL, SoundEvents.BIG_DRIPLEAF_TILT_DOWN);
         } else if (var5 == Tilt.PARTIAL) {
            this.setTiltAndScheduleTick(var1, var2, var3, Tilt.FULL, SoundEvents.BIG_DRIPLEAF_TILT_DOWN);
         } else if (var5 == Tilt.FULL) {
            resetTilt(var1, var2, var3);
         }
      }
   }

   @Override
   protected void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, @Nullable Orientation var5, boolean var6) {
      if (var2.hasNeighborSignal(var3)) {
         resetTilt(var1, var2, var3);
      }
   }

   private static void playTiltSound(Level var0, BlockPos var1, SoundEvent var2) {
      float var3 = Mth.randomBetween(var0.random, 0.8F, 1.2F);
      var0.playSound(null, var1, var2, SoundSource.BLOCKS, 1.0F, var3);
   }

   private static boolean canEntityTilt(BlockPos var0, Entity var1) {
      return var1.onGround() && var1.position().y > (double)((float)var0.getY() + 0.6875F);
   }

   private void setTiltAndScheduleTick(BlockState var1, Level var2, BlockPos var3, Tilt var4, @Nullable SoundEvent var5) {
      setTilt(var1, var2, var3, var4);
      if (var5 != null) {
         playTiltSound(var2, var3, var5);
      }

      int var6 = DELAY_UNTIL_NEXT_TILT_STATE.getInt(var4);
      if (var6 != -1) {
         var2.scheduleTick(var3, this, var6);
      }
   }

   private static void resetTilt(BlockState var0, Level var1, BlockPos var2) {
      setTilt(var0, var1, var2, Tilt.NONE);
      if (var0.getValue(TILT) != Tilt.NONE) {
         playTiltSound(var1, var2, SoundEvents.BIG_DRIPLEAF_TILT_UP);
      }
   }

   private static void setTilt(BlockState var0, Level var1, BlockPos var2, Tilt var3) {
      Tilt var4 = var0.getValue(TILT);
      var1.setBlock(var2, var0.setValue(TILT, var3), 2);
      if (var3.causesVibration() && var3 != var4) {
         var1.gameEvent(null, GameEvent.BLOCK_CHANGE, var2);
      }
   }

   @Override
   protected VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return LEAF_SHAPES.get(var1.getValue(TILT));
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return this.shapesCache.get(var1);
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = var1.getLevel().getBlockState(var1.getClickedPos().below());
      FluidState var3 = var1.getLevel().getFluidState(var1.getClickedPos());
      boolean var4 = var2.is(Blocks.BIG_DRIPLEAF) || var2.is(Blocks.BIG_DRIPLEAF_STEM);
      return this.defaultBlockState()
         .setValue(WATERLOGGED, Boolean.valueOf(var3.isSourceOfType(Fluids.WATER)))
         .setValue(FACING, var4 ? var2.getValue(FACING) : var1.getHorizontalDirection().getOpposite());
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(WATERLOGGED, FACING, TILT);
   }
}
