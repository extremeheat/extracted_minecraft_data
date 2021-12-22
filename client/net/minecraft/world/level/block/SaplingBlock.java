package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SaplingBlock extends BushBlock implements BonemealableBlock {
   public static final IntegerProperty STAGE;
   protected static final float AABB_OFFSET = 6.0F;
   protected static final VoxelShape SHAPE;
   private final AbstractTreeGrower treeGrower;

   protected SaplingBlock(AbstractTreeGrower var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.treeGrower = var1;
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(STAGE, 0));
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   public void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      if (var2.getMaxLocalRawBrightness(var3.above()) >= 9 && var4.nextInt(7) == 0) {
         this.advanceTree(var2, var3, var1, var4);
      }

   }

   public void advanceTree(ServerLevel var1, BlockPos var2, BlockState var3, Random var4) {
      if ((Integer)var3.getValue(STAGE) == 0) {
         var1.setBlock(var2, (BlockState)var3.cycle(STAGE), 4);
      } else {
         this.treeGrower.growTree(var1, var1.getChunkSource().getGenerator(), var2, var3, var4);
      }

   }

   public boolean isValidBonemealTarget(BlockGetter var1, BlockPos var2, BlockState var3, boolean var4) {
      return true;
   }

   public boolean isBonemealSuccess(Level var1, Random var2, BlockPos var3, BlockState var4) {
      return (double)var1.random.nextFloat() < 0.45D;
   }

   public void performBonemeal(ServerLevel var1, Random var2, BlockPos var3, BlockState var4) {
      this.advanceTree(var1, var3, var4, var2);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(STAGE);
   }

   static {
      STAGE = BlockStateProperties.STAGE;
      SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);
   }
}
