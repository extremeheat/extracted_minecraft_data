package net.minecraft.world.level.block;

import java.util.Iterator;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ScaffoldingBlock extends Block implements SimpleWaterloggedBlock {
   private static final int TICK_DELAY = 1;
   private static final VoxelShape STABLE_SHAPE;
   private static final VoxelShape UNSTABLE_SHAPE;
   private static final VoxelShape UNSTABLE_SHAPE_BOTTOM = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
   private static final VoxelShape BELOW_BLOCK = Shapes.block().move(0.0, -1.0, 0.0);
   public static final int STABILITY_MAX_DISTANCE = 7;
   public static final IntegerProperty DISTANCE;
   public static final BooleanProperty WATERLOGGED;
   public static final BooleanProperty BOTTOM;

   protected ScaffoldingBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(DISTANCE, 7)).setValue(WATERLOGGED, false)).setValue(BOTTOM, false));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(DISTANCE, WATERLOGGED, BOTTOM);
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      if (!var4.isHoldingItem(var1.getBlock().asItem())) {
         return (Boolean)var1.getValue(BOTTOM) ? UNSTABLE_SHAPE : STABLE_SHAPE;
      } else {
         return Shapes.block();
      }
   }

   public VoxelShape getInteractionShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return Shapes.block();
   }

   public boolean canBeReplaced(BlockState var1, BlockPlaceContext var2) {
      return var2.getItemInHand().is(this.asItem());
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockPos var2 = var1.getClickedPos();
      Level var3 = var1.getLevel();
      int var4 = getDistance(var3, var2);
      return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(WATERLOGGED, var3.getFluidState(var2).getType() == Fluids.WATER)).setValue(DISTANCE, var4)).setValue(BOTTOM, this.isBottom(var3, var2, var4));
   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var2.isClientSide) {
         var2.scheduleTick(var3, this, 1);
      }

   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var4.scheduleTick(var5, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      if (!var4.isClientSide()) {
         var4.scheduleTick(var5, (Block)this, 1);
      }

      return var1;
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      int var5 = getDistance(var2, var3);
      BlockState var6 = (BlockState)((BlockState)var1.setValue(DISTANCE, var5)).setValue(BOTTOM, this.isBottom(var2, var3, var5));
      if ((Integer)var6.getValue(DISTANCE) == 7) {
         if ((Integer)var1.getValue(DISTANCE) == 7) {
            FallingBlockEntity.fall(var2, var3, var6);
         } else {
            var2.destroyBlock(var3, true);
         }
      } else if (var1 != var6) {
         var2.setBlock(var3, var6, 3);
      }

   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return getDistance(var2, var3) < 7;
   }

   public VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      if (var4.isAbove(Shapes.block(), var3, true) && !var4.isDescending()) {
         return STABLE_SHAPE;
      } else {
         return (Integer)var1.getValue(DISTANCE) != 0 && (Boolean)var1.getValue(BOTTOM) && var4.isAbove(BELOW_BLOCK, var3, true) ? UNSTABLE_SHAPE_BOTTOM : Shapes.empty();
      }
   }

   public FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   private boolean isBottom(BlockGetter var1, BlockPos var2, int var3) {
      return var3 > 0 && !var1.getBlockState(var2.below()).is(this);
   }

   public static int getDistance(BlockGetter var0, BlockPos var1) {
      BlockPos.MutableBlockPos var2 = var1.mutable().move(Direction.DOWN);
      BlockState var3 = var0.getBlockState(var2);
      int var4 = 7;
      if (var3.is(Blocks.SCAFFOLDING)) {
         var4 = (Integer)var3.getValue(DISTANCE);
      } else if (var3.isFaceSturdy(var0, var2, Direction.UP)) {
         return 0;
      }

      Iterator var5 = Direction.Plane.HORIZONTAL.iterator();

      while(var5.hasNext()) {
         Direction var6 = (Direction)var5.next();
         BlockState var7 = var0.getBlockState(var2.setWithOffset(var1, (Direction)var6));
         if (var7.is(Blocks.SCAFFOLDING)) {
            var4 = Math.min(var4, (Integer)var7.getValue(DISTANCE) + 1);
            if (var4 == 1) {
               break;
            }
         }
      }

      return var4;
   }

   static {
      DISTANCE = BlockStateProperties.STABILITY_DISTANCE;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      BOTTOM = BlockStateProperties.BOTTOM;
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
      UNSTABLE_SHAPE = Shapes.or(UNSTABLE_SHAPE_BOTTOM, STABLE_SHAPE, var6, var5, var8, var7);
   }
}
