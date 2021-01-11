package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class BlockContainer extends Block implements ITileEntityProvider {
   protected BlockContainer(Material var1) {
      this(var1, var1.func_151565_r());
   }

   protected BlockContainer(Material var1, MapColor var2) {
      super(var1, var2);
      this.field_149758_A = true;
   }

   protected boolean func_181086_a(World var1, BlockPos var2, EnumFacing var3) {
      return var1.func_180495_p(var2.func_177972_a(var3)).func_177230_c().func_149688_o() == Material.field_151570_A;
   }

   protected boolean func_181087_e(World var1, BlockPos var2) {
      return this.func_181086_a(var1, var2, EnumFacing.NORTH) || this.func_181086_a(var1, var2, EnumFacing.SOUTH) || this.func_181086_a(var1, var2, EnumFacing.WEST) || this.func_181086_a(var1, var2, EnumFacing.EAST);
   }

   public int func_149645_b() {
      return -1;
   }

   public void func_180663_b(World var1, BlockPos var2, IBlockState var3) {
      super.func_180663_b(var1, var2, var3);
      var1.func_175713_t(var2);
   }

   public boolean func_180648_a(World var1, BlockPos var2, IBlockState var3, int var4, int var5) {
      super.func_180648_a(var1, var2, var3, var4, var5);
      TileEntity var6 = var1.func_175625_s(var2);
      return var6 == null ? false : var6.func_145842_c(var4, var5);
   }
}
