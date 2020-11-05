package net.minecraft.world.level.block;

import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StairBlock extends Block implements SimpleWaterloggedBlock {
   public static final DirectionProperty FACING;
   public static final EnumProperty<Half> HALF;
   public static final EnumProperty<StairsShape> SHAPE;
   public static final BooleanProperty WATERLOGGED;
   protected static final VoxelShape TOP_AABB;
   protected static final VoxelShape BOTTOM_AABB;
   protected static final VoxelShape OCTET_NNN;
   protected static final VoxelShape OCTET_NNP;
   protected static final VoxelShape OCTET_NPN;
   protected static final VoxelShape OCTET_NPP;
   protected static final VoxelShape OCTET_PNN;
   protected static final VoxelShape OCTET_PNP;
   protected static final VoxelShape OCTET_PPN;
   protected static final VoxelShape OCTET_PPP;
   protected static final VoxelShape[] TOP_SHAPES;
   protected static final VoxelShape[] BOTTOM_SHAPES;
   private static final int[] SHAPE_BY_STATE;
   private final Block base;
   private final BlockState baseState;

   private static VoxelShape[] makeShapes(VoxelShape var0, VoxelShape var1, VoxelShape var2, VoxelShape var3, VoxelShape var4) {
      return (VoxelShape[])IntStream.range(0, 16).mapToObj((var5) -> {
         return makeStairShape(var5, var0, var1, var2, var3, var4);
      }).toArray((var0x) -> {
         return new VoxelShape[var0x];
      });
   }

   private static VoxelShape makeStairShape(int var0, VoxelShape var1, VoxelShape var2, VoxelShape var3, VoxelShape var4, VoxelShape var5) {
      VoxelShape var6 = var1;
      if ((var0 & 1) != 0) {
         var6 = Shapes.or(var1, var2);
      }

      if ((var0 & 2) != 0) {
         var6 = Shapes.or(var6, var3);
      }

      if ((var0 & 4) != 0) {
         var6 = Shapes.or(var6, var4);
      }

      if ((var0 & 8) != 0) {
         var6 = Shapes.or(var6, var5);
      }

      return var6;
   }

   protected StairBlock(BlockState var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(HALF, Half.BOTTOM)).setValue(SHAPE, StairsShape.STRAIGHT)).setValue(WATERLOGGED, false));
      this.base = var1.getBlock();
      this.baseState = var1;
   }

   public boolean useShapeForLightOcclusion(BlockState var1) {
      return true;
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (var1.getValue(HALF) == Half.TOP ? TOP_SHAPES : BOTTOM_SHAPES)[SHAPE_BY_STATE[this.getShapeIndex(var1)]];
   }

   private int getShapeIndex(BlockState var1) {
      return ((StairsShape)var1.getValue(SHAPE)).ordinal() * 4 + ((Direction)var1.getValue(FACING)).get2DDataValue();
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      this.base.animateTick(var1, var2, var3, var4);
   }

   public void attack(BlockState var1, Level var2, BlockPos var3, Player var4) {
      this.baseState.attack(var2, var3, var4);
   }

   public void destroy(LevelAccessor var1, BlockPos var2, BlockState var3) {
      this.base.destroy(var1, var2, var3);
   }

   public float getExplosionResistance() {
      return this.base.getExplosionResistance();
   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var1.is(var1.getBlock())) {
         this.baseState.neighborChanged(var2, var3, Blocks.AIR, var3, false);
         this.base.onPlace(this.baseState, var2, var3, var4, false);
      }
   }

   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var1.is(var4.getBlock())) {
         this.baseState.onRemove(var2, var3, var4, var5);
      }
   }

   public void stepOn(Level var1, BlockPos var2, Entity var3) {
      this.base.stepOn(var1, var2, var3);
   }

   public boolean isRandomlyTicking(BlockState var1) {
      return this.base.isRandomlyTicking(var1);
   }

   public void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      this.base.randomTick(var1, var2, var3, var4);
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      this.base.tick(var1, var2, var3, var4);
   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      return this.baseState.use(var2, var4, var5, var6);
   }

   public void wasExploded(Level var1, BlockPos var2, Explosion var3) {
      this.base.wasExploded(var1, var2, var3);
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Direction var2 = var1.getClickedFace();
      BlockPos var3 = var1.getClickedPos();
      FluidState var4 = var1.getLevel().getFluidState(var3);
      BlockState var5 = (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, var1.getHorizontalDirection())).setValue(HALF, var2 != Direction.DOWN && (var2 == Direction.UP || var1.getClickLocation().y - (double)var3.getY() <= 0.5D) ? Half.BOTTOM : Half.TOP)).setValue(WATERLOGGED, var4.getType() == Fluids.WATER);
      return (BlockState)var5.setValue(SHAPE, getStairsShape(var5, var1.getLevel(), var3));
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var4.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      return var2.getAxis().isHorizontal() ? (BlockState)var1.setValue(SHAPE, getStairsShape(var1, var4, var5)) : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   private static StairsShape getStairsShape(BlockState var0, BlockGetter var1, BlockPos var2) {
      Direction var3 = (Direction)var0.getValue(FACING);
      BlockState var4 = var1.getBlockState(var2.relative(var3));
      if (isStairs(var4) && var0.getValue(HALF) == var4.getValue(HALF)) {
         Direction var5 = (Direction)var4.getValue(FACING);
         if (var5.getAxis() != ((Direction)var0.getValue(FACING)).getAxis() && canTakeShape(var0, var1, var2, var5.getOpposite())) {
            if (var5 == var3.getCounterClockWise()) {
               return StairsShape.OUTER_LEFT;
            }

            return StairsShape.OUTER_RIGHT;
         }
      }

      BlockState var7 = var1.getBlockState(var2.relative(var3.getOpposite()));
      if (isStairs(var7) && var0.getValue(HALF) == var7.getValue(HALF)) {
         Direction var6 = (Direction)var7.getValue(FACING);
         if (var6.getAxis() != ((Direction)var0.getValue(FACING)).getAxis() && canTakeShape(var0, var1, var2, var6)) {
            if (var6 == var3.getCounterClockWise()) {
               return StairsShape.INNER_LEFT;
            }

            return StairsShape.INNER_RIGHT;
         }
      }

      return StairsShape.STRAIGHT;
   }

   private static boolean canTakeShape(BlockState var0, BlockGetter var1, BlockPos var2, Direction var3) {
      BlockState var4 = var1.getBlockState(var2.relative(var3));
      return !isStairs(var4) || var4.getValue(FACING) != var0.getValue(FACING) || var4.getValue(HALF) != var0.getValue(HALF);
   }

   public static boolean isStairs(BlockState var0) {
      return var0.getBlock() instanceof StairBlock;
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      Direction var3 = (Direction)var1.getValue(FACING);
      StairsShape var4 = (StairsShape)var1.getValue(SHAPE);
      switch(var2) {
      case LEFT_RIGHT:
         if (var3.getAxis() == Direction.Axis.Z) {
            switch(var4) {
            case INNER_LEFT:
               return (BlockState)var1.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
            case INNER_RIGHT:
               return (BlockState)var1.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
            case OUTER_LEFT:
               return (BlockState)var1.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
            case OUTER_RIGHT:
               return (BlockState)var1.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
            default:
               return var1.rotate(Rotation.CLOCKWISE_180);
            }
         }
         break;
      case FRONT_BACK:
         if (var3.getAxis() == Direction.Axis.X) {
            switch(var4) {
            case INNER_LEFT:
               return (BlockState)var1.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
            case INNER_RIGHT:
               return (BlockState)var1.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
            case OUTER_LEFT:
               return (BlockState)var1.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
            case OUTER_RIGHT:
               return (BlockState)var1.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
            case STRAIGHT:
               return var1.rotate(Rotation.CLOCKWISE_180);
            }
         }
      }

      return super.mirror(var1, var2);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, HALF, SHAPE, WATERLOGGED);
   }

   public FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   static {
      FACING = HorizontalDirectionalBlock.FACING;
      HALF = BlockStateProperties.HALF;
      SHAPE = BlockStateProperties.STAIRS_SHAPE;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      TOP_AABB = SlabBlock.TOP_AABB;
      BOTTOM_AABB = SlabBlock.BOTTOM_AABB;
      OCTET_NNN = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 8.0D, 8.0D);
      OCTET_NNP = Block.box(0.0D, 0.0D, 8.0D, 8.0D, 8.0D, 16.0D);
      OCTET_NPN = Block.box(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 8.0D);
      OCTET_NPP = Block.box(0.0D, 8.0D, 8.0D, 8.0D, 16.0D, 16.0D);
      OCTET_PNN = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D);
      OCTET_PNP = Block.box(8.0D, 0.0D, 8.0D, 16.0D, 8.0D, 16.0D);
      OCTET_PPN = Block.box(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
      OCTET_PPP = Block.box(8.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D);
      TOP_SHAPES = makeShapes(TOP_AABB, OCTET_NNN, OCTET_PNN, OCTET_NNP, OCTET_PNP);
      BOTTOM_SHAPES = makeShapes(BOTTOM_AABB, OCTET_NPN, OCTET_PPN, OCTET_NPP, OCTET_PPP);
      SHAPE_BY_STATE = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};
   }
}
