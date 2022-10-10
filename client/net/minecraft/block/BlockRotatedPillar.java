package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;

public class BlockRotatedPillar extends Block {
   public static final EnumProperty<EnumFacing.Axis> field_176298_M;

   public BlockRotatedPillar(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)this.func_176223_P().func_206870_a(field_176298_M, EnumFacing.Axis.Y));
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      switch(var2) {
      case COUNTERCLOCKWISE_90:
      case CLOCKWISE_90:
         switch((EnumFacing.Axis)var1.func_177229_b(field_176298_M)) {
         case X:
            return (IBlockState)var1.func_206870_a(field_176298_M, EnumFacing.Axis.Z);
         case Z:
            return (IBlockState)var1.func_206870_a(field_176298_M, EnumFacing.Axis.X);
         default:
            return var1;
         }
      default:
         return var1;
      }
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176298_M);
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      return (IBlockState)this.func_176223_P().func_206870_a(field_176298_M, var1.func_196000_l().func_176740_k());
   }

   static {
      field_176298_M = BlockStateProperties.field_208148_A;
   }
}
