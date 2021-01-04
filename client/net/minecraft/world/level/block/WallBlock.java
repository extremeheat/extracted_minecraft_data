package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WallBlock extends CrossCollisionBlock {
   public static final BooleanProperty UP;
   private final VoxelShape[] shapeWithPostByIndex;
   private final VoxelShape[] collisionShapeWithPostByIndex;

   public WallBlock(Block.Properties var1) {
      super(0.0F, 3.0F, 0.0F, 14.0F, 24.0F, var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(UP, true)).setValue(NORTH, false)).setValue(EAST, false)).setValue(SOUTH, false)).setValue(WEST, false)).setValue(WATERLOGGED, false));
      this.shapeWithPostByIndex = this.makeShapes(4.0F, 3.0F, 16.0F, 0.0F, 14.0F);
      this.collisionShapeWithPostByIndex = this.makeShapes(4.0F, 3.0F, 24.0F, 0.0F, 24.0F);
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (Boolean)var1.getValue(UP) ? this.shapeWithPostByIndex[this.getAABBIndex(var1)] : super.getShape(var1, var2, var3, var4);
   }

   public VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (Boolean)var1.getValue(UP) ? this.collisionShapeWithPostByIndex[this.getAABBIndex(var1)] : super.getCollisionShape(var1, var2, var3, var4);
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   private boolean connectsTo(BlockState var1, boolean var2, Direction var3) {
      Block var4 = var1.getBlock();
      boolean var5 = var4.is(BlockTags.WALLS) || var4 instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(var1, var3);
      return !isExceptionForConnection(var4) && var2 || var5;
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      FluidState var4 = var1.getLevel().getFluidState(var1.getClickedPos());
      BlockPos var5 = var3.north();
      BlockPos var6 = var3.east();
      BlockPos var7 = var3.south();
      BlockPos var8 = var3.west();
      BlockState var9 = var2.getBlockState(var5);
      BlockState var10 = var2.getBlockState(var6);
      BlockState var11 = var2.getBlockState(var7);
      BlockState var12 = var2.getBlockState(var8);
      boolean var13 = this.connectsTo(var9, var9.isFaceSturdy(var2, var5, Direction.SOUTH), Direction.SOUTH);
      boolean var14 = this.connectsTo(var10, var10.isFaceSturdy(var2, var6, Direction.WEST), Direction.WEST);
      boolean var15 = this.connectsTo(var11, var11.isFaceSturdy(var2, var7, Direction.NORTH), Direction.NORTH);
      boolean var16 = this.connectsTo(var12, var12.isFaceSturdy(var2, var8, Direction.EAST), Direction.EAST);
      boolean var17 = (!var13 || var14 || !var15 || var16) && (var13 || !var14 || var15 || !var16);
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(UP, var17 || !var2.isEmptyBlock(var3.above()))).setValue(NORTH, var13)).setValue(EAST, var14)).setValue(SOUTH, var15)).setValue(WEST, var16)).setValue(WATERLOGGED, var4.getType() == Fluids.WATER);
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var4.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      if (var2 == Direction.DOWN) {
         return super.updateShape(var1, var2, var3, var4, var5, var6);
      } else {
         Direction var7 = var2.getOpposite();
         boolean var8 = var2 == Direction.NORTH ? this.connectsTo(var3, var3.isFaceSturdy(var4, var6, var7), var7) : (Boolean)var1.getValue(NORTH);
         boolean var9 = var2 == Direction.EAST ? this.connectsTo(var3, var3.isFaceSturdy(var4, var6, var7), var7) : (Boolean)var1.getValue(EAST);
         boolean var10 = var2 == Direction.SOUTH ? this.connectsTo(var3, var3.isFaceSturdy(var4, var6, var7), var7) : (Boolean)var1.getValue(SOUTH);
         boolean var11 = var2 == Direction.WEST ? this.connectsTo(var3, var3.isFaceSturdy(var4, var6, var7), var7) : (Boolean)var1.getValue(WEST);
         boolean var12 = (!var8 || var9 || !var10 || var11) && (var8 || !var9 || var10 || !var11);
         return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(UP, var12 || !var4.isEmptyBlock(var5.above()))).setValue(NORTH, var8)).setValue(EAST, var9)).setValue(SOUTH, var10)).setValue(WEST, var11);
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(UP, NORTH, EAST, WEST, SOUTH, WATERLOGGED);
   }

   static {
      UP = BlockStateProperties.UP;
   }
}
