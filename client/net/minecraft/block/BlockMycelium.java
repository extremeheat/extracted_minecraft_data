package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMycelium extends Block {
   private IIcon field_150200_a;
   private IIcon field_150199_b;

   protected BlockMycelium() {
      super(Material.field_151577_b);
      this.func_149675_a(true);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      if (var1 == 1) {
         return this.field_150200_a;
      } else {
         return var1 == 0 ? Blocks.field_150346_d.func_149733_h(var1) : this.field_149761_L;
      }
   }

   @Override
   public IIcon func_149673_e(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      if (var5 == 1) {
         return this.field_150200_a;
      } else if (var5 == 0) {
         return Blocks.field_150346_d.func_149733_h(var5);
      } else {
         Material var6 = var1.func_147439_a(var2, var3 + 1, var4).func_149688_o();
         return var6 != Material.field_151597_y && var6 != Material.field_151596_z ? this.field_149761_L : this.field_150199_b;
      }
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149761_L = var1.func_94245_a(this.func_149641_N() + "_side");
      this.field_150200_a = var1.func_94245_a(this.func_149641_N() + "_top");
      this.field_150199_b = var1.func_94245_a("grass_side_snowed");
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      if (!var1.field_72995_K) {
         if (var1.func_72957_l(var2, var3 + 1, var4) < 4 && var1.func_147439_a(var2, var3 + 1, var4).func_149717_k() > 2) {
            var1.func_147449_b(var2, var3, var4, Blocks.field_150346_d);
         } else if (var1.func_72957_l(var2, var3 + 1, var4) >= 9) {
            for(int var6 = 0; var6 < 4; ++var6) {
               int var7 = var2 + var5.nextInt(3) - 1;
               int var8 = var3 + var5.nextInt(5) - 3;
               int var9 = var4 + var5.nextInt(3) - 1;
               Block var10 = var1.func_147439_a(var7, var8 + 1, var9);
               if (var1.func_147439_a(var7, var8, var9) == Blocks.field_150346_d
                  && var1.func_72805_g(var7, var8, var9) == 0
                  && var1.func_72957_l(var7, var8 + 1, var9) >= 4
                  && var10.func_149717_k() <= 2) {
                  var1.func_147449_b(var7, var8, var9, this);
               }
            }
         }
      }
   }

   @Override
   public void func_149734_b(World var1, int var2, int var3, int var4, Random var5) {
      super.func_149734_b(var1, var2, var3, var4, var5);
      if (var5.nextInt(10) == 0) {
         var1.func_72869_a(
            "townaura", (double)((float)var2 + var5.nextFloat()), (double)((float)var3 + 1.1F), (double)((float)var4 + var5.nextFloat()), 0.0, 0.0, 0.0
         );
      }
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return Blocks.field_150346_d.func_149650_a(0, var2, var3);
   }
}
