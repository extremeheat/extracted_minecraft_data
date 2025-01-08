package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BaseCoralPlantBlock extends BaseCoralPlantTypeBlock {
   public static final MapCodec<BaseCoralPlantBlock> CODEC = simpleCodec(BaseCoralPlantBlock::new);
   private static final VoxelShape SHAPE = Block.column(12.0, 0.0, 15.0);

   public MapCodec<BaseCoralPlantBlock> codec() {
      return CODEC;
   }

   protected BaseCoralPlantBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }
}
