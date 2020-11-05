package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FenceBlock extends CrossCollisionBlock {
   private final VoxelShape[] occlusionByIndex;

   public FenceBlock(BlockBehaviour.Properties var1) {
      super(2.0F, 2.0F, 16.0F, 16.0F, 24.0F, var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, false)).setValue(EAST, false)).setValue(SOUTH, false)).setValue(WEST, false)).setValue(WATERLOGGED, false));
      this.occlusionByIndex = this.makeShapes(2.0F, 1.0F, 16.0F, 6.0F, 15.0F);
   }

   public VoxelShape getOcclusionShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return this.occlusionByIndex[this.getAABBIndex(var1)];
   }

   public VoxelShape getVisualShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return this.getShape(var1, var2, var3, var4);
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
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

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if (var2.isClientSide) {
         ItemStack var7 = var4.getItemInHand(var5);
         return var7.is(Items.LEAD) ? InteractionResult.SUCCESS : InteractionResult.PASS;
      } else {
         return LeadItem.bindPlayerMobs(var4, var2, var3);
      }
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
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)super.getStateForPlacement(var1).setValue(NORTH, this.connectsTo(var9, var9.isFaceSturdy(var2, var5, Direction.SOUTH), Direction.SOUTH))).setValue(EAST, this.connectsTo(var10, var10.isFaceSturdy(var2, var6, Direction.WEST), Direction.WEST))).setValue(SOUTH, this.connectsTo(var11, var11.isFaceSturdy(var2, var7, Direction.NORTH), Direction.NORTH))).setValue(WEST, this.connectsTo(var12, var12.isFaceSturdy(var2, var8, Direction.EAST), Direction.EAST))).setValue(WATERLOGGED, var4.getType() == Fluids.WATER);
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var4.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      return var2.getAxis().getPlane() == Direction.Plane.HORIZONTAL ? (BlockState)var1.setValue((Property)PROPERTY_BY_DIRECTION.get(var2), this.connectsTo(var3, var3.isFaceSturdy(var4, var6, var2.getOpposite()), var2.getOpposite())) : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
   }
}
