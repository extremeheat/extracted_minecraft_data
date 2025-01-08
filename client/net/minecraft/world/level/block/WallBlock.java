package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WallBlock extends Block implements SimpleWaterloggedBlock {
   public static final MapCodec<WallBlock> CODEC = simpleCodec(WallBlock::new);
   public static final BooleanProperty UP;
   public static final EnumProperty<WallSide> EAST;
   public static final EnumProperty<WallSide> NORTH;
   public static final EnumProperty<WallSide> SOUTH;
   public static final EnumProperty<WallSide> WEST;
   public static final Map<Direction, EnumProperty<WallSide>> PROPERTY_BY_DIRECTION;
   public static final BooleanProperty WATERLOGGED;
   private final Function<BlockState, VoxelShape> shapes;
   private final Function<BlockState, VoxelShape> collisionShapes;
   private static final VoxelShape TEST_SHAPE_POST;
   private static final Map<Direction, VoxelShape> TEST_SHAPES_WALL;

   public MapCodec<WallBlock> codec() {
      return CODEC;
   }

   public WallBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(UP, true)).setValue(NORTH, WallSide.NONE)).setValue(EAST, WallSide.NONE)).setValue(SOUTH, WallSide.NONE)).setValue(WEST, WallSide.NONE)).setValue(WATERLOGGED, false));
      this.shapes = this.makeShapes(16.0F, 14.0F);
      this.collisionShapes = this.makeShapes(24.0F, 24.0F);
   }

   private Function<BlockState, VoxelShape> makeShapes(float var1, float var2) {
      VoxelShape var3 = Block.column(8.0, 0.0, (double)var1);
      boolean var4 = true;
      Map var5 = Shapes.rotateHorizontal(Block.boxZ(6.0, 0.0, (double)var2, 0.0, 11.0));
      Map var6 = Shapes.rotateHorizontal(Block.boxZ(6.0, 0.0, (double)var1, 0.0, 11.0));
      return this.getShapeForEachState((var3x) -> {
         VoxelShape var4 = (Boolean)var3x.getValue(UP) ? var3 : Shapes.empty();

         for(Map.Entry var6x : PROPERTY_BY_DIRECTION.entrySet()) {
            VoxelShape var10001;
            switch ((WallSide)var3x.getValue((Property)var6x.getValue())) {
               case NONE -> var10001 = Shapes.empty();
               case LOW -> var10001 = (VoxelShape)var5.get(var6x.getKey());
               case TALL -> var10001 = (VoxelShape)var6.get(var6x.getKey());
               default -> throw new MatchException((String)null, (Throwable)null);
            }

            var4 = Shapes.or(var4, var10001);
         }

         return var4;
      }, new Property[]{WATERLOGGED});
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (VoxelShape)this.shapes.apply(var1);
   }

   protected VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (VoxelShape)this.collisionShapes.apply(var1);
   }

   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   private boolean connectsTo(BlockState var1, boolean var2, Direction var3) {
      Block var4 = var1.getBlock();
      boolean var5 = var4 instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(var1, var3);
      return var1.is(BlockTags.WALLS) || !isExceptionForConnection(var1) && var2 || var4 instanceof IronBarsBlock || var5;
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      FluidState var4 = var1.getLevel().getFluidState(var1.getClickedPos());
      BlockPos var5 = var3.north();
      BlockPos var6 = var3.east();
      BlockPos var7 = var3.south();
      BlockPos var8 = var3.west();
      BlockPos var9 = var3.above();
      BlockState var10 = var2.getBlockState(var5);
      BlockState var11 = var2.getBlockState(var6);
      BlockState var12 = var2.getBlockState(var7);
      BlockState var13 = var2.getBlockState(var8);
      BlockState var14 = var2.getBlockState(var9);
      boolean var15 = this.connectsTo(var10, var10.isFaceSturdy(var2, var5, Direction.SOUTH), Direction.SOUTH);
      boolean var16 = this.connectsTo(var11, var11.isFaceSturdy(var2, var6, Direction.WEST), Direction.WEST);
      boolean var17 = this.connectsTo(var12, var12.isFaceSturdy(var2, var7, Direction.NORTH), Direction.NORTH);
      boolean var18 = this.connectsTo(var13, var13.isFaceSturdy(var2, var8, Direction.EAST), Direction.EAST);
      BlockState var19 = (BlockState)this.defaultBlockState().setValue(WATERLOGGED, var4.getType() == Fluids.WATER);
      return this.updateShape(var2, var19, var9, var14, var15, var16, var17, var18);
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var3.scheduleTick(var4, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(var2));
      }

      if (var5 == Direction.DOWN) {
         return super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
      } else {
         return var5 == Direction.UP ? this.topUpdate(var2, var1, var6, var7) : this.sideUpdate(var2, var4, var1, var6, var7, var5);
      }
   }

   private static boolean isConnected(BlockState var0, Property<WallSide> var1) {
      return var0.getValue(var1) != WallSide.NONE;
   }

   private static boolean isCovered(VoxelShape var0, VoxelShape var1) {
      return !Shapes.joinIsNotEmpty(var1, var0, BooleanOp.ONLY_FIRST);
   }

   private BlockState topUpdate(LevelReader var1, BlockState var2, BlockPos var3, BlockState var4) {
      boolean var5 = isConnected(var2, NORTH);
      boolean var6 = isConnected(var2, EAST);
      boolean var7 = isConnected(var2, SOUTH);
      boolean var8 = isConnected(var2, WEST);
      return this.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   private BlockState sideUpdate(LevelReader var1, BlockPos var2, BlockState var3, BlockPos var4, BlockState var5, Direction var6) {
      Direction var7 = var6.getOpposite();
      boolean var8 = var6 == Direction.NORTH ? this.connectsTo(var5, var5.isFaceSturdy(var1, var4, var7), var7) : isConnected(var3, NORTH);
      boolean var9 = var6 == Direction.EAST ? this.connectsTo(var5, var5.isFaceSturdy(var1, var4, var7), var7) : isConnected(var3, EAST);
      boolean var10 = var6 == Direction.SOUTH ? this.connectsTo(var5, var5.isFaceSturdy(var1, var4, var7), var7) : isConnected(var3, SOUTH);
      boolean var11 = var6 == Direction.WEST ? this.connectsTo(var5, var5.isFaceSturdy(var1, var4, var7), var7) : isConnected(var3, WEST);
      BlockPos var12 = var2.above();
      BlockState var13 = var1.getBlockState(var12);
      return this.updateShape(var1, var3, var12, var13, var8, var9, var10, var11);
   }

   private BlockState updateShape(LevelReader var1, BlockState var2, BlockPos var3, BlockState var4, boolean var5, boolean var6, boolean var7, boolean var8) {
      VoxelShape var9 = var4.getCollisionShape(var1, var3).getFaceShape(Direction.DOWN);
      BlockState var10 = this.updateSides(var2, var5, var6, var7, var8, var9);
      return (BlockState)var10.setValue(UP, this.shouldRaisePost(var10, var4, var9));
   }

   private boolean shouldRaisePost(BlockState var1, BlockState var2, VoxelShape var3) {
      boolean var4 = var2.getBlock() instanceof WallBlock && (Boolean)var2.getValue(UP);
      if (var4) {
         return true;
      } else {
         WallSide var5 = (WallSide)var1.getValue(NORTH);
         WallSide var6 = (WallSide)var1.getValue(SOUTH);
         WallSide var7 = (WallSide)var1.getValue(EAST);
         WallSide var8 = (WallSide)var1.getValue(WEST);
         boolean var9 = var6 == WallSide.NONE;
         boolean var10 = var8 == WallSide.NONE;
         boolean var11 = var7 == WallSide.NONE;
         boolean var12 = var5 == WallSide.NONE;
         boolean var13 = var12 && var9 && var10 && var11 || var12 != var9 || var10 != var11;
         if (var13) {
            return true;
         } else {
            boolean var14 = var5 == WallSide.TALL && var6 == WallSide.TALL || var7 == WallSide.TALL && var8 == WallSide.TALL;
            if (var14) {
               return false;
            } else {
               return var2.is(BlockTags.WALL_POST_OVERRIDE) || isCovered(var3, TEST_SHAPE_POST);
            }
         }
      }
   }

   private BlockState updateSides(BlockState var1, boolean var2, boolean var3, boolean var4, boolean var5, VoxelShape var6) {
      return (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, this.makeWallState(var2, var6, (VoxelShape)TEST_SHAPES_WALL.get(Direction.NORTH)))).setValue(EAST, this.makeWallState(var3, var6, (VoxelShape)TEST_SHAPES_WALL.get(Direction.EAST)))).setValue(SOUTH, this.makeWallState(var4, var6, (VoxelShape)TEST_SHAPES_WALL.get(Direction.SOUTH)))).setValue(WEST, this.makeWallState(var5, var6, (VoxelShape)TEST_SHAPES_WALL.get(Direction.WEST)));
   }

   private WallSide makeWallState(boolean var1, VoxelShape var2, VoxelShape var3) {
      if (var1) {
         return isCovered(var2, var3) ? WallSide.TALL : WallSide.LOW;
      } else {
         return WallSide.NONE;
      }
   }

   protected FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   protected boolean propagatesSkylightDown(BlockState var1) {
      return !(Boolean)var1.getValue(WATERLOGGED);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(UP, NORTH, EAST, WEST, SOUTH, WATERLOGGED);
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      switch (var2) {
         case CLOCKWISE_180 -> {
            return (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, (WallSide)var1.getValue(SOUTH))).setValue(EAST, (WallSide)var1.getValue(WEST))).setValue(SOUTH, (WallSide)var1.getValue(NORTH))).setValue(WEST, (WallSide)var1.getValue(EAST));
         }
         case COUNTERCLOCKWISE_90 -> {
            return (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, (WallSide)var1.getValue(EAST))).setValue(EAST, (WallSide)var1.getValue(SOUTH))).setValue(SOUTH, (WallSide)var1.getValue(WEST))).setValue(WEST, (WallSide)var1.getValue(NORTH));
         }
         case CLOCKWISE_90 -> {
            return (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, (WallSide)var1.getValue(WEST))).setValue(EAST, (WallSide)var1.getValue(NORTH))).setValue(SOUTH, (WallSide)var1.getValue(EAST))).setValue(WEST, (WallSide)var1.getValue(SOUTH));
         }
         default -> {
            return var1;
         }
      }
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      switch (var2) {
         case LEFT_RIGHT -> {
            return (BlockState)((BlockState)var1.setValue(NORTH, (WallSide)var1.getValue(SOUTH))).setValue(SOUTH, (WallSide)var1.getValue(NORTH));
         }
         case FRONT_BACK -> {
            return (BlockState)((BlockState)var1.setValue(EAST, (WallSide)var1.getValue(WEST))).setValue(WEST, (WallSide)var1.getValue(EAST));
         }
         default -> {
            return super.mirror(var1, var2);
         }
      }
   }

   static {
      UP = BlockStateProperties.UP;
      EAST = BlockStateProperties.EAST_WALL;
      NORTH = BlockStateProperties.NORTH_WALL;
      SOUTH = BlockStateProperties.SOUTH_WALL;
      WEST = BlockStateProperties.WEST_WALL;
      PROPERTY_BY_DIRECTION = ImmutableMap.copyOf(Maps.newEnumMap(Map.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST)));
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      TEST_SHAPE_POST = Block.column(2.0, 0.0, 16.0);
      TEST_SHAPES_WALL = Shapes.rotateHorizontal(Block.boxZ(2.0, 16.0, 0.0, 9.0));
   }
}
