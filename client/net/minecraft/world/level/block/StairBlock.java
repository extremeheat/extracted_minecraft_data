package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StairBlock extends Block implements SimpleWaterloggedBlock {
   public static final MapCodec<StairBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(BlockState.CODEC.fieldOf("base_state").forGetter((var0x) -> var0x.baseState), propertiesCodec()).apply(var0, StairBlock::new));
   public static final EnumProperty<Direction> FACING;
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
   protected final BlockState baseState;

   public MapCodec<? extends StairBlock> codec() {
      return CODEC;
   }

   private static VoxelShape[] makeShapes(VoxelShape var0, VoxelShape var1, VoxelShape var2, VoxelShape var3, VoxelShape var4) {
      return (VoxelShape[])IntStream.range(0, 16).mapToObj((var5) -> makeStairShape(var5, var0, var1, var2, var3, var4)).toArray((var0x) -> new VoxelShape[var0x]);
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

   protected boolean useShapeForLightOcclusion(BlockState var1) {
      return true;
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (var1.getValue(HALF) == Half.TOP ? TOP_SHAPES : BOTTOM_SHAPES)[SHAPE_BY_STATE[this.getShapeIndex(var1)]];
   }

   private int getShapeIndex(BlockState var1) {
      return ((StairsShape)var1.getValue(SHAPE)).ordinal() * 4 + ((Direction)var1.getValue(FACING)).get2DDataValue();
   }

   public float getExplosionResistance() {
      return this.base.getExplosionResistance();
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Direction var2 = var1.getClickedFace();
      BlockPos var3 = var1.getClickedPos();
      FluidState var4 = var1.getLevel().getFluidState(var3);
      BlockState var5 = (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, var1.getHorizontalDirection())).setValue(HALF, var2 != Direction.DOWN && (var2 == Direction.UP || !(var1.getClickLocation().y - (double)var3.getY() > 0.5)) ? Half.BOTTOM : Half.TOP)).setValue(WATERLOGGED, var4.getType() == Fluids.WATER);
      return (BlockState)var5.setValue(SHAPE, getStairsShape(var5, var1.getLevel(), var3));
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var3.scheduleTick(var4, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(var2));
      }

      return var5.getAxis().isHorizontal() ? (BlockState)var1.setValue(SHAPE, getStairsShape(var1, var2, var4)) : super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
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

   protected BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      Direction var3 = (Direction)var1.getValue(FACING);
      StairsShape var4 = (StairsShape)var1.getValue(SHAPE);
      switch (var2) {
         case LEFT_RIGHT:
            if (var3.getAxis() == Direction.Axis.Z) {
               switch (var4) {
                  case INNER_LEFT -> {
                     return (BlockState)var1.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
                  }
                  case INNER_RIGHT -> {
                     return (BlockState)var1.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
                  }
                  case OUTER_LEFT -> {
                     return (BlockState)var1.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
                  }
                  case OUTER_RIGHT -> {
                     return (BlockState)var1.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
                  }
                  default -> {
                     return var1.rotate(Rotation.CLOCKWISE_180);
                  }
               }
            }
            break;
         case FRONT_BACK:
            if (var3.getAxis() == Direction.Axis.X) {
               switch (var4) {
                  case INNER_LEFT -> {
                     return (BlockState)var1.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
                  }
                  case INNER_RIGHT -> {
                     return (BlockState)var1.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
                  }
                  case OUTER_LEFT -> {
                     return (BlockState)var1.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
                  }
                  case OUTER_RIGHT -> {
                     return (BlockState)var1.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
                  }
                  case STRAIGHT -> {
                     return var1.rotate(Rotation.CLOCKWISE_180);
                  }
               }
            }
      }

      return super.mirror(var1, var2);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, HALF, SHAPE, WATERLOGGED);
   }

   protected FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   static {
      FACING = HorizontalDirectionalBlock.FACING;
      HALF = BlockStateProperties.HALF;
      SHAPE = BlockStateProperties.STAIRS_SHAPE;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      TOP_AABB = SlabBlock.TOP_AABB;
      BOTTOM_AABB = SlabBlock.BOTTOM_AABB;
      OCTET_NNN = Block.box(0.0, 0.0, 0.0, 8.0, 8.0, 8.0);
      OCTET_NNP = Block.box(0.0, 0.0, 8.0, 8.0, 8.0, 16.0);
      OCTET_NPN = Block.box(0.0, 8.0, 0.0, 8.0, 16.0, 8.0);
      OCTET_NPP = Block.box(0.0, 8.0, 8.0, 8.0, 16.0, 16.0);
      OCTET_PNN = Block.box(8.0, 0.0, 0.0, 16.0, 8.0, 8.0);
      OCTET_PNP = Block.box(8.0, 0.0, 8.0, 16.0, 8.0, 16.0);
      OCTET_PPN = Block.box(8.0, 8.0, 0.0, 16.0, 16.0, 8.0);
      OCTET_PPP = Block.box(8.0, 8.0, 8.0, 16.0, 16.0, 16.0);
      TOP_SHAPES = makeShapes(TOP_AABB, OCTET_NNN, OCTET_PNN, OCTET_NNP, OCTET_PNP);
      BOTTOM_SHAPES = makeShapes(BOTTOM_AABB, OCTET_NPN, OCTET_PPN, OCTET_NPP, OCTET_PPP);
      SHAPE_BY_STATE = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};
   }
}
