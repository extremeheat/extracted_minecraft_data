package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class BlockContainer extends Block implements ITileEntityProvider {
   protected BlockContainer(Material var1) {
      super(var1);
      this.field_149758_A = true;
   }

   @Override
   public void func_149726_b(World var1, int var2, int var3, int var4) {
      super.func_149726_b(var1, var2, var3, var4);
   }

   @Override
   public void func_149749_a(World var1, int var2, int var3, int var4, Block var5, int var6) {
      super.func_149749_a(var1, var2, var3, var4, var5, var6);
      var1.func_147475_p(var2, var3, var4);
   }

   @Override
   public boolean func_149696_a(World var1, int var2, int var3, int var4, int var5, int var6) {
      super.func_149696_a(var1, var2, var3, var4, var5, var6);
      TileEntity var7 = var1.func_147438_o(var2, var3, var4);
      return var7 != null ? var7.func_145842_c(var5, var6) : false;
   }
}
