package net.minecraft.block;

import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockNewLog extends BlockLog {
   public static final String[] field_150169_M = new String[]{"acacia", "big_oak"};

   public BlockNewLog() {
      super();
   }

   @Override
   public void func_149666_a(Item var1, CreativeTabs var2, List var3) {
      var3.add(new ItemStack(var1, 1, 0));
      var3.add(new ItemStack(var1, 1, 1));
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_150167_a = new IIcon[field_150169_M.length];
      this.field_150166_b = new IIcon[field_150169_M.length];

      for(int var2 = 0; var2 < this.field_150167_a.length; ++var2) {
         this.field_150167_a[var2] = var1.func_94245_a(this.func_149641_N() + "_" + field_150169_M[var2]);
         this.field_150166_b[var2] = var1.func_94245_a(this.func_149641_N() + "_" + field_150169_M[var2] + "_top");
      }
   }
}
