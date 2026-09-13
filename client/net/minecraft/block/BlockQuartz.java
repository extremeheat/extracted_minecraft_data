package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockQuartz extends Block {
   public static final String[] field_150191_a = new String[]{"default", "chiseled", "lines"};
   private static final String[] field_150189_b = new String[]{"side", "chiseled", "lines", null, null};
   private IIcon[] field_150192_M;
   private IIcon field_150193_N;
   private IIcon field_150194_O;
   private IIcon field_150190_P;
   private IIcon field_150188_Q;

   public BlockQuartz() {
      super(Material.field_151576_e);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      if (var2 != 2 && var2 != 3 && var2 != 4) {
         if (var1 != 1 && (var1 != 0 || var2 != 1)) {
            if (var1 == 0) {
               return this.field_150188_Q;
            } else {
               if (var2 < 0 || var2 >= this.field_150192_M.length) {
                  var2 = 0;
               }

               return this.field_150192_M[var2];
            }
         } else {
            return var2 == 1 ? this.field_150193_N : this.field_150190_P;
         }
      } else if (var2 != 2 || var1 != 1 && var1 != 0) {
         if (var2 != 3 || var1 != 5 && var1 != 4) {
            return var2 != 4 || var1 != 2 && var1 != 3 ? this.field_150192_M[var2] : this.field_150194_O;
         } else {
            return this.field_150194_O;
         }
      } else {
         return this.field_150194_O;
      }
   }

   @Override
   public int func_149660_a(World var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, int var9) {
      if (var9 == 2) {
         switch(var5) {
            case 0:
            case 1:
               var9 = 2;
               break;
            case 2:
            case 3:
               var9 = 4;
               break;
            case 4:
            case 5:
               var9 = 3;
         }
      }

      return var9;
   }

   @Override
   public int func_149692_a(int var1) {
      return var1 != 3 && var1 != 4 ? var1 : 2;
   }

   @Override
   protected ItemStack func_149644_j(int var1) {
      return var1 != 3 && var1 != 4 ? super.func_149644_j(var1) : new ItemStack(Item.func_150898_a(this), 1, 2);
   }

   @Override
   public int func_149645_b() {
      return 39;
   }

   @Override
   public void func_149666_a(Item var1, CreativeTabs var2, List var3) {
      var3.add(new ItemStack(var1, 1, 0));
      var3.add(new ItemStack(var1, 1, 1));
      var3.add(new ItemStack(var1, 1, 2));
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_150192_M = new IIcon[field_150189_b.length];

      for(int var2 = 0; var2 < this.field_150192_M.length; ++var2) {
         if (field_150189_b[var2] == null) {
            this.field_150192_M[var2] = this.field_150192_M[var2 - 1];
         } else {
            this.field_150192_M[var2] = var1.func_94245_a(this.func_149641_N() + "_" + field_150189_b[var2]);
         }
      }

      this.field_150190_P = var1.func_94245_a(this.func_149641_N() + "_" + "top");
      this.field_150193_N = var1.func_94245_a(this.func_149641_N() + "_" + "chiseled_top");
      this.field_150194_O = var1.func_94245_a(this.func_149641_N() + "_" + "lines_top");
      this.field_150188_Q = var1.func_94245_a(this.func_149641_N() + "_" + "bottom");
   }

   @Override
   public MapColor func_149728_f(int var1) {
      return MapColor.field_151677_p;
   }
}
