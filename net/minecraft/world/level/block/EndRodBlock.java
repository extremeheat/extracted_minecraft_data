package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EndRodBlock extends DirectionalBlock {
   protected static final VoxelShape Y_AXIS_AABB = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D);
   protected static final VoxelShape Z_AXIS_AABB = Block.box(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 16.0D);
   protected static final VoxelShape X_AXIS_AABB = Block.box(0.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);

   protected EndRodBlock(Block.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.UP));
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      return (BlockState)var1.setValue(FACING, var2.mirror((Direction)var1.getValue(FACING)));
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      switch(((Direction)var1.getValue(FACING)).getAxis()) {
      case X:
      default:
         return X_AXIS_AABB;
      case Z:
         return Z_AXIS_AABB;
      case Y:
         return Y_AXIS_AABB;
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Direction var2 = var1.getClickedFace();
      BlockState var3 = var1.getLevel().getBlockState(var1.getClickedPos().relative(var2.getOpposite()));
      return var3.getBlock() == this && var3.getValue(FACING) == var2 ? (BlockState)this.defaultBlockState().setValue(FACING, var2.getOpposite()) : (BlockState)this.defaultBlockState().setValue(FACING, var2);
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      Direction var5 = (Direction)var1.getValue(FACING);
      double var6 = (double)var3.getX() + 0.55D - (double)(var4.nextFloat() * 0.1F);
      double var8 = (double)var3.getY() + 0.55D - (double)(var4.nextFloat() * 0.1F);
      double var10 = (double)var3.getZ() + 0.55D - (double)(var4.nextFloat() * 0.1F);
      double var12 = (double)(0.4F - (var4.nextFloat() + var4.nextFloat()) * 0.4F);
      if (var4.nextInt(5) == 0) {
         var2.addParticle(ParticleTypes.END_ROD, var6 + (double)var5.getStepX() * var12, var8 + (double)var5.getStepY() * var12, var10 + (double)var5.getStepZ() * var12, var4.nextGaussian() * 0.005D, var4.nextGaussian() * 0.005D, var4.nextGaussian() * 0.005D);
      }

   }

   protected void createBlockStateDefinition(StateDefinition.Builder var1) {
      var1.add(FACING);
   }

   public PushReaction getPistonPushReaction(BlockState var1) {
      return PushReaction.NORMAL;
   }
}
