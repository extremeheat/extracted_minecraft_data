package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FenceBlock extends CrossCollisionBlock {
   public static final MapCodec<FenceBlock> CODEC = simpleCodec(FenceBlock::new);
   private final VoxelShape[] occlusionByIndex;

   @Override
   public MapCodec<FenceBlock> codec() {
      return CODEC;
   }

   public FenceBlock(BlockBehaviour.Properties var1) {
      super(2.0F, 2.0F, 16.0F, 16.0F, 24.0F, var1);
      this.registerDefaultState(
         this.stateDefinition
            .any()
            .setValue(NORTH, Boolean.valueOf(false))
            .setValue(EAST, Boolean.valueOf(false))
            .setValue(SOUTH, Boolean.valueOf(false))
            .setValue(WEST, Boolean.valueOf(false))
            .setValue(WATERLOGGED, Boolean.valueOf(false))
      );
      this.occlusionByIndex = this.makeShapes(2.0F, 1.0F, 16.0F, 6.0F, 15.0F);
   }

   @Override
   protected VoxelShape getOcclusionShape(BlockState var1) {
      return this.occlusionByIndex[this.getAABBIndex(var1)];
   }

   @Override
   protected VoxelShape getVisualShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return this.getShape(var1, var2, var3, var4);
   }

   @Override
   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   public boolean connectsTo(BlockState var1, boolean var2, Direction var3) {
      Block var4 = var1.getBlock();
      boolean var5 = this.isSameFence(var1);
      boolean var6 = var4 instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(var1, var3);
      return !isExceptionForConnection(var1) && var2 || var5 || var6;
   }

   private boolean isSameFence(BlockState var1) {
      return var1.is(BlockTags.FENCES) && var1.is(BlockTags.WOODEN_FENCES) == this.defaultBlockState().is(BlockTags.WOODEN_FENCES);
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      return (InteractionResult)(!var2.isClientSide() ? LeadItem.bindPlayerMobs(var4, var2, var3) : InteractionResult.PASS);
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
      BlockState var9 = var2.getBlockState(var5);
      BlockState var10 = var2.getBlockState(var6);
      BlockState var11 = var2.getBlockState(var7);
      BlockState var12 = var2.getBlockState(var8);
      return super.getStateForPlacement(var1)
         .setValue(NORTH, Boolean.valueOf(this.connectsTo(var9, var9.isFaceSturdy(var2, var5, Direction.SOUTH), Direction.SOUTH)))
         .setValue(EAST, Boolean.valueOf(this.connectsTo(var10, var10.isFaceSturdy(var2, var6, Direction.WEST), Direction.WEST)))
         .setValue(SOUTH, Boolean.valueOf(this.connectsTo(var11, var11.isFaceSturdy(var2, var7, Direction.NORTH), Direction.NORTH)))
         .setValue(WEST, Boolean.valueOf(this.connectsTo(var12, var12.isFaceSturdy(var2, var8, Direction.EAST), Direction.EAST)))
         .setValue(WATERLOGGED, Boolean.valueOf(var4.getType() == Fluids.WATER));
   }

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var1.getValue(WATERLOGGED)) {
         var4.scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      return var2.getAxis().getPlane() == Direction.Plane.HORIZONTAL
         ? var1.setValue(
            PROPERTY_BY_DIRECTION.get(var2), Boolean.valueOf(this.connectsTo(var3, var3.isFaceSturdy(var4, var6, var2.getOpposite()), var2.getOpposite()))
         )
         : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
   }
}
