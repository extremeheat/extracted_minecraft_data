package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WallBlock extends Block implements SimpleWaterloggedBlock {
   public static final MapCodec<WallBlock> CODEC = simpleCodec(WallBlock::new);
   public static final BooleanProperty UP = BlockStateProperties.UP;
   public static final EnumProperty<WallSide> EAST_WALL = BlockStateProperties.EAST_WALL;
   public static final EnumProperty<WallSide> NORTH_WALL = BlockStateProperties.NORTH_WALL;
   public static final EnumProperty<WallSide> SOUTH_WALL = BlockStateProperties.SOUTH_WALL;
   public static final EnumProperty<WallSide> WEST_WALL = BlockStateProperties.WEST_WALL;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   private final Map<BlockState, VoxelShape> shapeByIndex;
   private final Map<BlockState, VoxelShape> collisionShapeByIndex;
   private static final int WALL_WIDTH = 3;
   private static final int WALL_HEIGHT = 14;
   private static final int POST_WIDTH = 4;
   private static final int POST_COVER_WIDTH = 1;
   private static final int WALL_COVER_START = 7;
   private static final int WALL_COVER_END = 9;
   private static final VoxelShape POST_TEST = Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0);
   private static final VoxelShape NORTH_TEST = Block.box(7.0, 0.0, 0.0, 9.0, 16.0, 9.0);
   private static final VoxelShape SOUTH_TEST = Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 16.0);
   private static final VoxelShape WEST_TEST = Block.box(0.0, 0.0, 7.0, 9.0, 16.0, 9.0);
   private static final VoxelShape EAST_TEST = Block.box(7.0, 0.0, 7.0, 16.0, 16.0, 9.0);

   @Override
   public MapCodec<WallBlock> codec() {
      return CODEC;
   }

   public WallBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(
         this.stateDefinition
            .any()
            .setValue(UP, Boolean.valueOf(true))
            .setValue(NORTH_WALL, WallSide.NONE)
            .setValue(EAST_WALL, WallSide.NONE)
            .setValue(SOUTH_WALL, WallSide.NONE)
            .setValue(WEST_WALL, WallSide.NONE)
            .setValue(WATERLOGGED, Boolean.valueOf(false))
      );
      this.shapeByIndex = this.makeShapes(4.0F, 3.0F, 16.0F, 0.0F, 14.0F, 16.0F);
      this.collisionShapeByIndex = this.makeShapes(4.0F, 3.0F, 24.0F, 0.0F, 24.0F, 24.0F);
   }

   private static VoxelShape applyWallShape(VoxelShape var0, WallSide var1, VoxelShape var2, VoxelShape var3) {
      if (var1 == WallSide.TALL) {
         return Shapes.or(var0, var3);
      } else {
         return var1 == WallSide.LOW ? Shapes.or(var0, var2) : var0;
      }
   }

   private Map<BlockState, VoxelShape> makeShapes(float var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = 8.0F - var1;
      float var8 = 8.0F + var1;
      float var9 = 8.0F - var2;
      float var10 = 8.0F + var2;
      VoxelShape var11 = Block.box((double)var7, 0.0, (double)var7, (double)var8, (double)var3, (double)var8);
      VoxelShape var12 = Block.box((double)var9, (double)var4, 0.0, (double)var10, (double)var5, (double)var10);
      VoxelShape var13 = Block.box((double)var9, (double)var4, (double)var9, (double)var10, (double)var5, 16.0);
      VoxelShape var14 = Block.box(0.0, (double)var4, (double)var9, (double)var10, (double)var5, (double)var10);
      VoxelShape var15 = Block.box((double)var9, (double)var4, (double)var9, 16.0, (double)var5, (double)var10);
      VoxelShape var16 = Block.box((double)var9, (double)var4, 0.0, (double)var10, (double)var6, (double)var10);
      VoxelShape var17 = Block.box((double)var9, (double)var4, (double)var9, (double)var10, (double)var6, 16.0);
      VoxelShape var18 = Block.box(0.0, (double)var4, (double)var9, (double)var10, (double)var6, (double)var10);
      VoxelShape var19 = Block.box((double)var9, (double)var4, (double)var9, 16.0, (double)var6, (double)var10);
      Builder var20 = ImmutableMap.builder();

      for (Boolean var22 : UP.getPossibleValues()) {
         for (WallSide var24 : EAST_WALL.getPossibleValues()) {
            for (WallSide var26 : NORTH_WALL.getPossibleValues()) {
               for (WallSide var28 : WEST_WALL.getPossibleValues()) {
                  for (WallSide var30 : SOUTH_WALL.getPossibleValues()) {
                     VoxelShape var31 = Shapes.empty();
                     var31 = applyWallShape(var31, var24, var15, var19);
                     var31 = applyWallShape(var31, var28, var14, var18);
                     var31 = applyWallShape(var31, var26, var12, var16);
                     var31 = applyWallShape(var31, var30, var13, var17);
                     if (var22) {
                        var31 = Shapes.or(var31, var11);
                     }

                     BlockState var32 = this.defaultBlockState()
                        .setValue(UP, var22)
                        .setValue(EAST_WALL, var24)
                        .setValue(WEST_WALL, var28)
                        .setValue(NORTH_WALL, var26)
                        .setValue(SOUTH_WALL, var30);
                     var20.put(var32.setValue(WATERLOGGED, Boolean.valueOf(false)), var31);
                     var20.put(var32.setValue(WATERLOGGED, Boolean.valueOf(true)), var31);
                  }
               }
            }
         }
      }

      return var20.build();
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return this.shapeByIndex.get(var1);
   }

   @Override
   protected VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return this.collisionShapeByIndex.get(var1);
   }

   @Override
   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   private boolean connectsTo(BlockState var1, boolean var2, Direction var3) {
      Block var4 = var1.getBlock();
      boolean var5 = var4 instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(var1, var3);
      return var1.is(BlockTags.WALLS) || !isExceptionForConnection(var1) && var2 || var4 instanceof IronBarsBlock || var5;
   }

   @Override
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
      BlockState var19 = this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(var4.getType() == Fluids.WATER));
      return this.updateShape(var2, var19, var9, var14, var15, var16, var17, var18);
   }

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var1.getValue(WATERLOGGED)) {
         var4.scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      if (var2 == Direction.DOWN) {
         return super.updateShape(var1, var2, var3, var4, var5, var6);
      } else {
         return var2 == Direction.UP ? this.topUpdate(var4, var1, var6, var3) : this.sideUpdate(var4, var5, var1, var6, var3, var2);
      }
   }

   private static boolean isConnected(BlockState var0, Property<WallSide> var1) {
      return var0.getValue(var1) != WallSide.NONE;
   }

   private static boolean isCovered(VoxelShape var0, VoxelShape var1) {
      return !Shapes.joinIsNotEmpty(var1, var0, BooleanOp.ONLY_FIRST);
   }

   private BlockState topUpdate(LevelReader var1, BlockState var2, BlockPos var3, BlockState var4) {
      boolean var5 = isConnected(var2, NORTH_WALL);
      boolean var6 = isConnected(var2, EAST_WALL);
      boolean var7 = isConnected(var2, SOUTH_WALL);
      boolean var8 = isConnected(var2, WEST_WALL);
      return this.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   private BlockState sideUpdate(LevelReader var1, BlockPos var2, BlockState var3, BlockPos var4, BlockState var5, Direction var6) {
      Direction var7 = var6.getOpposite();
      boolean var8 = var6 == Direction.NORTH ? this.connectsTo(var5, var5.isFaceSturdy(var1, var4, var7), var7) : isConnected(var3, NORTH_WALL);
      boolean var9 = var6 == Direction.EAST ? this.connectsTo(var5, var5.isFaceSturdy(var1, var4, var7), var7) : isConnected(var3, EAST_WALL);
      boolean var10 = var6 == Direction.SOUTH ? this.connectsTo(var5, var5.isFaceSturdy(var1, var4, var7), var7) : isConnected(var3, SOUTH_WALL);
      boolean var11 = var6 == Direction.WEST ? this.connectsTo(var5, var5.isFaceSturdy(var1, var4, var7), var7) : isConnected(var3, WEST_WALL);
      BlockPos var12 = var2.above();
      BlockState var13 = var1.getBlockState(var12);
      return this.updateShape(var1, var3, var12, var13, var8, var9, var10, var11);
   }

   private BlockState updateShape(LevelReader var1, BlockState var2, BlockPos var3, BlockState var4, boolean var5, boolean var6, boolean var7, boolean var8) {
      VoxelShape var9 = var4.getCollisionShape(var1, var3).getFaceShape(Direction.DOWN);
      BlockState var10 = this.updateSides(var2, var5, var6, var7, var8, var9);
      return var10.setValue(UP, Boolean.valueOf(this.shouldRaisePost(var10, var4, var9)));
   }

   private boolean shouldRaisePost(BlockState var1, BlockState var2, VoxelShape var3) {
      boolean var4 = var2.getBlock() instanceof WallBlock && var2.getValue(UP);
      if (var4) {
         return true;
      } else {
         WallSide var5 = var1.getValue(NORTH_WALL);
         WallSide var6 = var1.getValue(SOUTH_WALL);
         WallSide var7 = var1.getValue(EAST_WALL);
         WallSide var8 = var1.getValue(WEST_WALL);
         boolean var9 = var6 == WallSide.NONE;
         boolean var10 = var8 == WallSide.NONE;
         boolean var11 = var7 == WallSide.NONE;
         boolean var12 = var5 == WallSide.NONE;
         boolean var13 = var12 && var9 && var10 && var11 || var12 != var9 || var10 != var11;
         if (var13) {
            return true;
         } else {
            boolean var14 = var5 == WallSide.TALL && var6 == WallSide.TALL || var7 == WallSide.TALL && var8 == WallSide.TALL;
            return var14 ? false : var2.is(BlockTags.WALL_POST_OVERRIDE) || isCovered(var3, POST_TEST);
         }
      }
   }

   private BlockState updateSides(BlockState var1, boolean var2, boolean var3, boolean var4, boolean var5, VoxelShape var6) {
      return var1.setValue(NORTH_WALL, this.makeWallState(var2, var6, NORTH_TEST))
         .setValue(EAST_WALL, this.makeWallState(var3, var6, EAST_TEST))
         .setValue(SOUTH_WALL, this.makeWallState(var4, var6, SOUTH_TEST))
         .setValue(WEST_WALL, this.makeWallState(var5, var6, WEST_TEST));
   }

   private WallSide makeWallState(boolean var1, VoxelShape var2, VoxelShape var3) {
      if (var1) {
         return isCovered(var2, var3) ? WallSide.TALL : WallSide.LOW;
      } else {
         return WallSide.NONE;
      }
   }

   @Override
   protected FluidState getFluidState(BlockState var1) {
      return var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   @Override
   protected boolean propagatesSkylightDown(BlockState var1, BlockGetter var2, BlockPos var3) {
      return !var1.getValue(WATERLOGGED);
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(UP, NORTH_WALL, EAST_WALL, WEST_WALL, SOUTH_WALL, WATERLOGGED);
   }

   @Override
   protected BlockState rotate(BlockState var1, Rotation var2) {
      switch (var2) {
         case CLOCKWISE_180:
            return var1.setValue(NORTH_WALL, var1.getValue(SOUTH_WALL))
               .setValue(EAST_WALL, var1.getValue(WEST_WALL))
               .setValue(SOUTH_WALL, var1.getValue(NORTH_WALL))
               .setValue(WEST_WALL, var1.getValue(EAST_WALL));
         case COUNTERCLOCKWISE_90:
            return var1.setValue(NORTH_WALL, var1.getValue(EAST_WALL))
               .setValue(EAST_WALL, var1.getValue(SOUTH_WALL))
               .setValue(SOUTH_WALL, var1.getValue(WEST_WALL))
               .setValue(WEST_WALL, var1.getValue(NORTH_WALL));
         case CLOCKWISE_90:
            return var1.setValue(NORTH_WALL, var1.getValue(WEST_WALL))
               .setValue(EAST_WALL, var1.getValue(NORTH_WALL))
               .setValue(SOUTH_WALL, var1.getValue(EAST_WALL))
               .setValue(WEST_WALL, var1.getValue(SOUTH_WALL));
         default:
            return var1;
      }
   }

   @Override
   protected BlockState mirror(BlockState var1, Mirror var2) {
      switch (var2) {
         case LEFT_RIGHT:
            return var1.setValue(NORTH_WALL, var1.getValue(SOUTH_WALL)).setValue(SOUTH_WALL, var1.getValue(NORTH_WALL));
         case FRONT_BACK:
            return var1.setValue(EAST_WALL, var1.getValue(WEST_WALL)).setValue(WEST_WALL, var1.getValue(EAST_WALL));
         default:
            return super.mirror(var1, var2);
      }
   }
}
