package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

public abstract class BlockHorizontal extends Block {
   public static final DirectionProperty field_185512_D;

   protected BlockHorizontal(Block.Properties var1) {
      super(var1);
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      return (IBlockState)var1.func_206870_a(field_185512_D, var2.func_185831_a((EnumFacing)var1.func_177229_b(field_185512_D)));
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      return var1.func_185907_a(var2.func_185800_a((EnumFacing)var1.func_177229_b(field_185512_D)));
   }

   static {
      field_185512_D = BlockStateProperties.field_208157_J;
   }
}
