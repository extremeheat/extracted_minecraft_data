package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockDirtSnowy extends Block {
   public static final BooleanProperty field_196382_a;

   protected BlockDirtSnowy(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_196382_a, false));
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      if (var2 != EnumFacing.UP) {
         return super.func_196271_a(var1, var2, var3, var4, var5, var6);
      } else {
         Block var7 = var3.func_177230_c();
         return (IBlockState)var1.func_206870_a(field_196382_a, var7 == Blocks.field_196604_cC || var7 == Blocks.field_150433_aE);
      }
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      Block var2 = var1.func_195991_k().func_180495_p(var1.func_195995_a().func_177984_a()).func_177230_c();
      return (IBlockState)this.func_176223_P().func_206870_a(field_196382_a, var2 == Blocks.field_196604_cC || var2 == Blocks.field_150433_aE);
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_196382_a);
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return Blocks.field_150346_d;
   }

   static {
      field_196382_a = BlockStateProperties.field_208196_w;
   }
}
