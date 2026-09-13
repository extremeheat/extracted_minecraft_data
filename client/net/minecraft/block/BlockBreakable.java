package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.Facing;
import net.minecraft.world.IBlockAccess;

public class BlockBreakable extends Block {
   private boolean field_149996_a;
   private String field_149995_b;

   protected BlockBreakable(String var1, Material var2, boolean var3) {
      super(var2);
      this.field_149996_a = var3;
      this.field_149995_b = var1;
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public boolean func_149646_a(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      Block var6 = var1.func_147439_a(var2, var3, var4);
      if (this == Blocks.field_150359_w || this == Blocks.field_150399_cn) {
         if (var1.func_72805_g(var2, var3, var4)
            != var1.func_72805_g(var2 - Facing.field_71586_b[var5], var3 - Facing.field_71587_c[var5], var4 - Facing.field_71585_d[var5])) {
            return true;
         }

         if (var6 == this) {
            return false;
         }
      }

      return !this.field_149996_a && var6 == this ? false : super.func_149646_a(var1, var2, var3, var4, var5);
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149761_L = var1.func_94245_a(this.field_149995_b);
   }
}
