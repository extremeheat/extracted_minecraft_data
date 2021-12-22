package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RedstoneWallTorchBlock extends RedstoneTorchBlock {
   public static final DirectionProperty FACING;
   public static final BooleanProperty LIT;

   protected RedstoneWallTorchBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(LIT, true));
   }

   public String getDescriptionId() {
      return this.asItem().getDescriptionId();
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return WallTorchBlock.getShape(var1);
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return Blocks.WALL_TORCH.canSurvive(var1, var2, var3);
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return Blocks.WALL_TORCH.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = Blocks.WALL_TORCH.getStateForPlacement(var1);
      return var2 == null ? null : (BlockState)this.defaultBlockState().setValue(FACING, (Direction)var2.getValue(FACING));
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      if ((Boolean)var1.getValue(LIT)) {
         Direction var5 = ((Direction)var1.getValue(FACING)).getOpposite();
         double var6 = 0.27D;
         double var8 = (double)var3.getX() + 0.5D + (var4.nextDouble() - 0.5D) * 0.2D + 0.27D * (double)var5.getStepX();
         double var10 = (double)var3.getY() + 0.7D + (var4.nextDouble() - 0.5D) * 0.2D + 0.22D;
         double var12 = (double)var3.getZ() + 0.5D + (var4.nextDouble() - 0.5D) * 0.2D + 0.27D * (double)var5.getStepZ();
         var2.addParticle(this.flameParticle, var8, var10, var12, 0.0D, 0.0D, 0.0D);
      }
   }

   protected boolean hasNeighborSignal(Level var1, BlockPos var2, BlockState var3) {
      Direction var4 = ((Direction)var3.getValue(FACING)).getOpposite();
      return var1.hasSignal(var2.relative(var4), var4);
   }

   public int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return (Boolean)var1.getValue(LIT) && var1.getValue(FACING) != var4 ? 15 : 0;
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      return Blocks.WALL_TORCH.rotate(var1, var2);
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      return Blocks.WALL_TORCH.mirror(var1, var2);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, LIT);
   }

   static {
      FACING = HorizontalDirectionalBlock.FACING;
      LIT = RedstoneTorchBlock.LIT;
   }
}
