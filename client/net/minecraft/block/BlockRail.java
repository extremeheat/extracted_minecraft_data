package net.minecraft.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockRail extends BlockRailBase {
   private IIcon field_150056_b;

   protected BlockRail() {
      super(false);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      return var2 >= 6 ? this.field_150056_b : this.field_149761_L;
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      super.func_149651_a(var1);
      this.field_150056_b = var1.func_94245_a(this.func_149641_N() + "_turned");
   }

   @Override
   protected void func_150048_a(World var1, int var2, int var3, int var4, int var5, int var6, Block var7) {
      if (var7.func_149744_f() && new BlockRailBase$Rail(this, var1, var2, var3, var4).func_150650_a() == 3) {
         this.func_150052_a(var1, var2, var3, var4, false);
      }
   }
}
