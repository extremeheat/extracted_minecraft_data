package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class BlockFlower extends BlockBush {
   protected static final VoxelShape field_196398_a = Block.func_208617_a(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);

   public BlockFlower(Block.Properties var1) {
      super(var1);
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      Vec3d var4 = var1.func_191059_e(var2, var3);
      return field_196398_a.func_197751_a(var4.field_72450_a, var4.field_72448_b, var4.field_72449_c);
   }

   public Block.EnumOffsetType func_176218_Q() {
      return Block.EnumOffsetType.XZ;
   }
}
