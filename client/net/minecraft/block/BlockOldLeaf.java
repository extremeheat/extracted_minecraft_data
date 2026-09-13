package net.minecraft.block;

import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockOldLeaf extends BlockLeaves {
   public static final String[][] field_150130_N = new String[][]{
      {"leaves_oak", "leaves_spruce", "leaves_birch", "leaves_jungle"},
      {"leaves_oak_opaque", "leaves_spruce_opaque", "leaves_birch_opaque", "leaves_jungle_opaque"}
   };
   public static final String[] field_150131_O = new String[]{"oak", "spruce", "birch", "jungle"};

   public BlockOldLeaf() {
      super();
   }

   @Override
   public int func_149741_i(int var1) {
      if ((var1 & 3) == 1) {
         return ColorizerFoliage.func_77466_a();
      } else {
         return (var1 & 3) == 2 ? ColorizerFoliage.func_77469_b() : super.func_149741_i(var1);
      }
   }

   @Override
   public int func_149720_d(IBlockAccess var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      if ((var5 & 3) == 1) {
         return ColorizerFoliage.func_77466_a();
      } else {
         return (var5 & 3) == 2 ? ColorizerFoliage.func_77469_b() : super.func_149720_d(var1, var2, var3, var4);
      }
   }

   @Override
   protected void func_150124_c(World var1, int var2, int var3, int var4, int var5, int var6) {
      if ((var5 & 3) == 0 && var1.field_73012_v.nextInt(var6) == 0) {
         this.func_149642_a(var1, var2, var3, var4, new ItemStack(Items.field_151034_e, 1, 0));
      }
   }

   @Override
   protected int func_150123_b(int var1) {
      int var2 = super.func_150123_b(var1);
      if ((var1 & 3) == 3) {
         var2 = 40;
      }

      return var2;
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      if ((var2 & 3) == 1) {
         return this.field_150129_M[this.field_150127_b][1];
      } else if ((var2 & 3) == 3) {
         return this.field_150129_M[this.field_150127_b][3];
      } else {
         return (var2 & 3) == 2 ? this.field_150129_M[this.field_150127_b][2] : this.field_150129_M[this.field_150127_b][0];
      }
   }

   @Override
   public void func_149666_a(Item var1, CreativeTabs var2, List var3) {
      var3.add(new ItemStack(var1, 1, 0));
      var3.add(new ItemStack(var1, 1, 1));
      var3.add(new ItemStack(var1, 1, 2));
      var3.add(new ItemStack(var1, 1, 3));
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      for(int var2 = 0; var2 < field_150130_N.length; ++var2) {
         this.field_150129_M[var2] = new IIcon[field_150130_N[var2].length];

         for(int var3 = 0; var3 < field_150130_N[var2].length; ++var3) {
            this.field_150129_M[var2][var3] = var1.func_94245_a(field_150130_N[var2][var3]);
         }
      }
   }

   @Override
   public String[] func_150125_e() {
      return field_150131_O;
   }
}
