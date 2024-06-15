package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ScaffoldingBlock extends Block implements SimpleWaterloggedBlock {
   public static final MapCodec<ScaffoldingBlock> CODEC = simpleCodec(ScaffoldingBlock::new);
   private static final int TICK_DELAY = 1;
   private static final VoxelShape STABLE_SHAPE;
   private static final VoxelShape UNSTABLE_SHAPE;
   private static final VoxelShape UNSTABLE_SHAPE_BOTTOM = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
   private static final VoxelShape BELOW_BLOCK = Shapes.block().move(0.0, -1.0, 0.0);
   public static final int STABILITY_MAX_DISTANCE = 7;
   public static final IntegerProperty DISTANCE = BlockStateProperties.STABILITY_DISTANCE;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final BooleanProperty BOTTOM = BlockStateProperties.BOTTOM;

   @Override
   public MapCodec<ScaffoldingBlock> codec() {
      return CODEC;
   }

   protected ScaffoldingBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(
         this.stateDefinition
            .any()
            .setValue(DISTANCE, Integer.valueOf(7))
            .setValue(WATERLOGGED, Boolean.valueOf(false))
            .setValue(BOTTOM, Boolean.valueOf(false))
      );
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(DISTANCE, WATERLOGGED, BOTTOM);
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      if (!var4.isHoldingItem(var1.getBlock().asItem())) {
         return var1.getValue(BOTTOM) ? UNSTABLE_SHAPE : STABLE_SHAPE;
      } else {
         return Shapes.block();
      }
   }

   @Override
   protected VoxelShape getInteractionShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return Shapes.block();
   }

   @Override
   protected boolean canBeReplaced(BlockState var1, BlockPlaceContext var2) {
      return var2.getItemInHand().is(this.asItem());
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockPos var2 = var1.getClickedPos();
      Level var3 = var1.getLevel();
      int var4 = getDistance(var3, var2);
      return this.defaultBlockState()
         .setValue(WATERLOGGED, Boolean.valueOf(var3.getFluidState(var2).getType() == Fluids.WATER))
         .setValue(DISTANCE, Integer.valueOf(var4))
         .setValue(BOTTOM, Boolean.valueOf(this.isBottom(var3, var2, var4)));
   }

   @Override
   protected void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var2.isClientSide) {
         var2.scheduleTick(var3, this, 1);
      }
   }

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var1.getValue(WATERLOGGED)) {
         var4.scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      if (!var4.isClientSide()) {
         var4.scheduleTick(var5, this, 1);
      }

      return var1;
   }

   @Override
   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      int var5 = getDistance(var2, var3);
      BlockState var6 = var1.setValue(DISTANCE, Integer.valueOf(var5)).setValue(BOTTOM, Boolean.valueOf(this.isBottom(var2, var3, var5)));
      if (var6.getValue(DISTANCE) == 7) {
         if (var1.getValue(DISTANCE) == 7) {
            FallingBlockEntity.fall(var2, var3, var6);
         } else {
            var2.destroyBlock(var3, true);
         }
      } else if (var1 != var6) {
         var2.setBlock(var3, var6, 3);
      }
   }

   @Override
   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return getDistance(var2, var3) < 7;
   }

   @Override
   protected VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      if (var4.isAbove(Shapes.block(), var3, true) && !var4.isDescending()) {
         return STABLE_SHAPE;
      } else {
         return var1.getValue(DISTANCE) != 0 && var1.getValue(BOTTOM) && var4.isAbove(BELOW_BLOCK, var3, true) ? UNSTABLE_SHAPE_BOTTOM : Shapes.empty();
      }
   }

   @Override
   protected FluidState getFluidState(BlockState var1) {
      return var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   private boolean isBottom(BlockGetter var1, BlockPos var2, int var3) {
      return var3 > 0 && !var1.getBlockState(var2.below()).is(this);
   }

   public static int getDistance(BlockGetter var0, BlockPos var1) {
      BlockPos.MutableBlockPos var2 = var1.mutable().move(Direction.DOWN);
      BlockState var3 = var0.getBlockState(var2);
      int var4 = 7;
      if (var3.is(Blocks.SCAFFOLDING)) {
         var4 = var3.getValue(DISTANCE);
      } else if (var3.isFaceSturdy(var0, var2, Direction.UP)) {
         return 0;
      }

      for (Direction var6 : Direction.Plane.HORIZONTAL) {
         BlockState var7 = var0.getBlockState(var2.setWithOffset(var1, var6));
         if (var7.is(Blocks.SCAFFOLDING)) {
            var4 = Math.min(var4, var7.getValue(DISTANCE) + 1);
            if (var4 == 1) {
               break;
            }
         }
      }

      return var4;
   }

   static {
      VoxelShape var0 = Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);
      VoxelShape var1 = Block.box(0.0, 0.0, 0.0, 2.0, 16.0, 2.0);
      VoxelShape var2 = Block.box(14.0, 0.0, 0.0, 16.0, 16.0, 2.0);
      VoxelShape var3 = Block.box(0.0, 0.0, 14.0, 2.0, 16.0, 16.0);
      VoxelShape var4 = Block.box(14.0, 0.0, 14.0, 16.0, 16.0, 16.0);
      STABLE_SHAPE = Shapes.or(var0, var1, var2, var3, var4);
      VoxelShape var5 = Block.box(0.0, 0.0, 0.0, 2.0, 2.0, 16.0);
      VoxelShape var6 = Block.box(14.0, 0.0, 0.0, 16.0, 2.0, 16.0);
      VoxelShape var7 = Block.box(0.0, 0.0, 14.0, 16.0, 2.0, 16.0);
      VoxelShape var8 = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 2.0);
      UNSTABLE_SHAPE = Shapes.or(ScaffoldingBlock.UNSTABLE_SHAPE_BOTTOM, STABLE_SHAPE, var6, var5, var8, var7);
   }
}
