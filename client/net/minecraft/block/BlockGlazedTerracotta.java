package net.minecraft.block;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;

public class BlockGlazedTerracotta extends BlockHorizontal {
   public BlockGlazedTerracotta(Block.Properties var1) {
      super(var1);
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_185512_D);
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      return (IBlockState)this.func_176223_P().func_206870_a(field_185512_D, var1.func_195992_f().func_176734_d());
   }

   public EnumPushReaction func_149656_h(IBlockState var1) {
      return EnumPushReaction.PUSH_ONLY;
   }
}
