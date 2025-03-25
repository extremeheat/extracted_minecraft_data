package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PiglinWallSkullBlock extends WallSkullBlock {
   public static final MapCodec<PiglinWallSkullBlock> CODEC = simpleCodec(PiglinWallSkullBlock::new);
   private static final Map<Direction, VoxelShape> SHAPES = Shapes.rotateHorizontal(Block.boxZ(10.0, 8.0, 8.0, 16.0));

   public MapCodec<PiglinWallSkullBlock> codec() {
      return CODEC;
   }

   public PiglinWallSkullBlock(BlockBehaviour.Properties var1) {
      super(SkullBlock.Types.PIGLIN, var1);
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (VoxelShape)SHAPES.get(var1.getValue(FACING));
   }
}
