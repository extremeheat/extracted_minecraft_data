package net.minecraft.block;

import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockNewLeaf extends BlockLeaves {
   public static final String[][] field_150132_N = new String[][]{{"leaves_acacia", "leaves_big_oak"}, {"leaves_acacia_opaque", "leaves_big_oak_opaque"}};
   public static final String[] field_150133_O = new String[]{"acacia", "big_oak"};

   public BlockNewLeaf() {
      super();
   }

   @Override
   protected void func_150124_c(World var1, int var2, int var3, int var4, int var5, int var6) {
      if ((var5 & 3) == 1 && var1.field_73012_v.nextInt(var6) == 0) {
         this.func_149642_a(var1, var2, var3, var4, new ItemStack(Items.field_151034_e, 1, 0));
      }
   }

   @Override
   public int func_149692_a(int var1) {
      return super.func_149692_a(var1) + 4;
   }

   @Override
   public int func_149643_k(World var1, int var2, int var3, int var4) {
      return var1.func_72805_g(var2, var3, var4) & 3;
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      return (var2 & 3) == 1 ? this.field_150129_M[this.field_150127_b][1] : this.field_150129_M[this.field_150127_b][0];
   }

   @Override
   public void func_149666_a(Item var1, CreativeTabs var2, List var3) {
      var3.add(new ItemStack(var1, 1, 0));
      var3.add(new ItemStack(var1, 1, 1));
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      for(int var2 = 0; var2 < field_150132_N.length; ++var2) {
         this.field_150129_M[var2] = new IIcon[field_150132_N[var2].length];

         for(int var3 = 0; var3 < field_150132_N[var2].length; ++var3) {
            this.field_150129_M[var2][var3] = var1.func_94245_a(field_150132_N[var2][var3]);
         }
      }
   }

   @Override
   public String[] func_150125_e() {
      return field_150133_O;
   }
}
