package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Map;
import java.util.Random;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Tilt;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BigDripleafBlock extends HorizontalDirectionalBlock implements BonemealableBlock, SimpleWaterloggedBlock {
   private static final BooleanProperty WATERLOGGED;
   private static final EnumProperty<Tilt> TILT;
   private static final int NO_TICK = -1;
   private static final Object2IntMap<Tilt> DELAY_UNTIL_NEXT_TILT_STATE;
   private static final int MAX_GEN_HEIGHT = 5;
   private static final int STEM_WIDTH = 6;
   private static final int ENTITY_DETECTION_MIN_Y = 11;
   private static final int LOWEST_LEAF_TOP = 13;
   private static final Map<Tilt, VoxelShape> LEAF_SHAPES;
   private static final VoxelShape STEM_SLICER;
   private static final Map<Direction, VoxelShape> STEM_SHAPES;
   private final Map<BlockState, VoxelShape> shapesCache;

   protected BigDripleafBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, false)).setValue(FACING, Direction.NORTH)).setValue(TILT, Tilt.NONE));
      this.shapesCache = this.getShapeForEachState(BigDripleafBlock::calculateShape);
   }

   private static VoxelShape calculateShape(BlockState var0) {
      return Shapes.method_31((VoxelShape)LEAF_SHAPES.get(var0.getValue(TILT)), (VoxelShape)STEM_SHAPES.get(var0.getValue(FACING)));
   }

   public static void placeWithRandomHeight(LevelAccessor var0, Random var1, BlockPos var2, Direction var3) {
      int var4 = Mth.nextInt(var1, 2, 5);
      BlockPos.MutableBlockPos var5 = var2.mutable();
      int var6 = 0;

      while(var6 < var4 && canPlaceAt(var0, var5, var0.getBlockState(var5))) {
         ++var6;
         var5.move(Direction.field_526);
      }

      int var7 = var2.getY() + var6 - 1;
      var5.setY(var2.getY());

      while(var5.getY() < var7) {
         BigDripleafStemBlock.place(var0, var5, var0.getFluidState(var5), var3);
         var5.move(Direction.field_526);
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
      BlockState var4 = (BlockState)((BlockState)Blocks.BIG_DRIPLEAF.defaultBlockState().setValue(WATERLOGGED, var2.isSourceOfType(Fluids.WATER))).setValue(FACING, var3);
      return var0.setBlock(var1, var4, 3);
   }

   public void onProjectileHit(Level var1, BlockState var2, BlockHitResult var3, Projectile var4) {
      this.setTiltAndScheduleTick(var2, var1, var3.getBlockPos(), Tilt.FULL, SoundEvents.BIG_DRIPLEAF_TILT_DOWN);
   }

   public FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockPos var4 = var3.below();
      BlockState var5 = var2.getBlockState(var4);
      return var5.is(this) || var5.is(Blocks.BIG_DRIPLEAF_STEM) || var5.is(BlockTags.BIG_DRIPLEAF_PLACEABLE);
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var2 == Direction.DOWN && !var1.canSurvive(var4, var5)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         if ((Boolean)var1.getValue(WATERLOGGED)) {
            var4.scheduleTick(var5, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(var4));
         }

         return var2 == Direction.field_526 && var3.is(this) ? Blocks.BIG_DRIPLEAF_STEM.withPropertiesOf(var1) : super.updateShape(var1, var2, var3, var4, var5, var6);
      }
   }

   public boolean isValidBonemealTarget(BlockGetter var1, BlockPos var2, BlockState var3, boolean var4) {
      BlockState var5 = var1.getBlockState(var2.above());
      return canReplace(var5);
   }

   public boolean isBonemealSuccess(Level var1, Random var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, Random var2, BlockPos var3, BlockState var4) {
      BlockPos var5 = var3.above();
      BlockState var6 = var1.getBlockState(var5);
      if (canPlaceAt(var1, var5, var6)) {
         Direction var7 = (Direction)var4.getValue(FACING);
         BigDripleafStemBlock.place(var1, var3, var4.getFluidState(), var7);
         place(var1, var5, var6.getFluidState(), var7);
      }

   }

   public void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (!var2.isClientSide) {
         if (var1.getValue(TILT) == Tilt.NONE && canEntityTilt(var3, var4) && !var2.hasNeighborSignal(var3)) {
            this.setTiltAndScheduleTick(var1, var2, var3, Tilt.UNSTABLE, (SoundEvent)null);
         }

      }
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      if (var2.hasNeighborSignal(var3)) {
         resetTilt(var1, var2, var3);
      } else {
         Tilt var5 = (Tilt)var1.getValue(TILT);
         if (var5 == Tilt.UNSTABLE) {
            this.setTiltAndScheduleTick(var1, var2, var3, Tilt.PARTIAL, SoundEvents.BIG_DRIPLEAF_TILT_DOWN);
         } else if (var5 == Tilt.PARTIAL) {
            this.setTiltAndScheduleTick(var1, var2, var3, Tilt.FULL, SoundEvents.BIG_DRIPLEAF_TILT_DOWN);
         } else if (var5 == Tilt.FULL) {
            resetTilt(var1, var2, var3);
         }

      }
   }

   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      if (var2.hasNeighborSignal(var3)) {
         resetTilt(var1, var2, var3);
      }

   }

   private static void playTiltSound(Level var0, BlockPos var1, SoundEvent var2) {
      float var3 = Mth.randomBetween(var0.random, 0.8F, 1.2F);
      var0.playSound((Player)null, (BlockPos)var1, var2, SoundSource.BLOCKS, 1.0F, var3);
   }

   private static boolean canEntityTilt(BlockPos var0, Entity var1) {
      return var1.isOnGround() && var1.position().field_415 > (double)((float)var0.getY() + 0.6875F);
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
      var1.setBlock(var2, (BlockState)var0.setValue(TILT, var3), 2);
      if (var3.causesVibration()) {
         var1.gameEvent(GameEvent.BLOCK_CHANGE, var2);
      }

   }

   public VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (VoxelShape)LEAF_SHAPES.get(var1.getValue(TILT));
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (VoxelShape)this.shapesCache.get(var1);
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = var1.getLevel().getBlockState(var1.getClickedPos().below());
      FluidState var3 = var1.getLevel().getFluidState(var1.getClickedPos());
      boolean var4 = var2.is(Blocks.BIG_DRIPLEAF) || var2.is(Blocks.BIG_DRIPLEAF_STEM);
      return (BlockState)((BlockState)this.defaultBlockState().setValue(WATERLOGGED, var3.isSourceOfType(Fluids.WATER))).setValue(FACING, var4 ? (Direction)var2.getValue(FACING) : var1.getHorizontalDirection().getOpposite());
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(WATERLOGGED, FACING, TILT);
   }

   static {
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      TILT = BlockStateProperties.TILT;
      DELAY_UNTIL_NEXT_TILT_STATE = (Object2IntMap)Util.make(new Object2IntArrayMap(), (var0) -> {
         var0.defaultReturnValue(-1);
         var0.put(Tilt.UNSTABLE, 10);
         var0.put(Tilt.PARTIAL, 10);
         var0.put(Tilt.FULL, 100);
      });
      LEAF_SHAPES = ImmutableMap.of(Tilt.NONE, Block.box(0.0D, 11.0D, 0.0D, 16.0D, 15.0D, 16.0D), Tilt.UNSTABLE, Block.box(0.0D, 11.0D, 0.0D, 16.0D, 15.0D, 16.0D), Tilt.PARTIAL, Block.box(0.0D, 11.0D, 0.0D, 16.0D, 13.0D, 16.0D), Tilt.FULL, Shapes.empty());
      STEM_SLICER = Block.box(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      STEM_SHAPES = ImmutableMap.of(Direction.NORTH, Shapes.joinUnoptimized(BigDripleafStemBlock.NORTH_SHAPE, STEM_SLICER, BooleanOp.ONLY_FIRST), Direction.SOUTH, Shapes.joinUnoptimized(BigDripleafStemBlock.SOUTH_SHAPE, STEM_SLICER, BooleanOp.ONLY_FIRST), Direction.EAST, Shapes.joinUnoptimized(BigDripleafStemBlock.EAST_SHAPE, STEM_SLICER, BooleanOp.ONLY_FIRST), Direction.WEST, Shapes.joinUnoptimized(BigDripleafStemBlock.WEST_SHAPE, STEM_SLICER, BooleanOp.ONLY_FIRST));
   }
}
