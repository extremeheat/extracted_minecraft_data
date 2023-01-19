package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SkullBlock extends AbstractSkullBlock {
   public static final int MAX = 15;
   private static final int ROTATIONS = 16;
   public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
   protected static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);

   protected SkullBlock(SkullBlock.Type var1, BlockBehaviour.Properties var2) {
      super(var1, var2);
      this.registerDefaultState(this.stateDefinition.any().setValue(ROTATION, Integer.valueOf(0)));
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   @Override
   public VoxelShape getOcclusionShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return Shapes.empty();
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return this.defaultBlockState().setValue(ROTATION, Integer.valueOf(Mth.floor((double)(var1.getRotation() * 16.0F / 360.0F) + 0.5) & 15));
   }

   @Override
   public BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(ROTATION, Integer.valueOf(var2.rotate(var1.getValue(ROTATION), 16)));
   }

   @Override
   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.setValue(ROTATION, Integer.valueOf(var2.mirror(var1.getValue(ROTATION), 16)));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(ROTATION);
   }

   public interface Type {
   }

   public static enum Types implements SkullBlock.Type {
      SKELETON,
      WITHER_SKELETON,
      PLAYER,
      ZOMBIE,
      CREEPER,
      DRAGON;

      private Types() {
      }
   }
}
