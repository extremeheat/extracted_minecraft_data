package net.minecraft.block;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockStandingSign extends BlockSign {
   public static final PropertyInteger field_176413_a = PropertyInteger.func_177719_a("rotation", 0, 15);

   public BlockStandingSign() {
      super();
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176413_a, 0));
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!var1.func_180495_p(var2.func_177977_b()).func_177230_c().func_149688_o().func_76220_a()) {
         this.func_176226_b(var1, var2, var3, 0);
         var1.func_175698_g(var2);
      }

      super.func_176204_a(var1, var2, var3, var4);
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176413_a, var1);
   }

   public int func_176201_c(IBlockState var1) {
      return (Integer)var1.func_177229_b(field_176413_a);
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176413_a});
   }
}
