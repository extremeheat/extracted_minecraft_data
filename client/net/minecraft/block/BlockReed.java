package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockReed extends Block {
   protected BlockReed() {
      super(Material.field_151585_k);
      float var1 = 0.375F;
      this.func_149676_a(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, 1.0F, 0.5F + var1);
      this.func_149675_a(true);
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      if (var1.func_147439_a(var2, var3 - 1, var4) == Blocks.field_150436_aH || this.func_150170_e(var1, var2, var3, var4)) {
         if (var1.func_147437_c(var2, var3 + 1, var4)) {
            int var6 = 1;

            while(var1.func_147439_a(var2, var3 - var6, var4) == this) {
               ++var6;
            }

            if (var6 < 3) {
               int var7 = var1.func_72805_g(var2, var3, var4);
               if (var7 == 15) {
                  var1.func_147449_b(var2, var3 + 1, var4, this);
                  var1.func_72921_c(var2, var3, var4, 0, 4);
               } else {
                  var1.func_72921_c(var2, var3, var4, var7 + 1, 4);
               }
            }
         }
      }
   }

   @Override
   public boolean func_149742_c(World var1, int var2, int var3, int var4) {
      Block var5 = var1.func_147439_a(var2, var3 - 1, var4);
      if (var5 == this) {
         return true;
      } else if (var5 != Blocks.field_150349_c && var5 != Blocks.field_150346_d && var5 != Blocks.field_150354_m) {
         return false;
      } else if (var1.func_147439_a(var2 - 1, var3 - 1, var4).func_149688_o() == Material.field_151586_h) {
         return true;
      } else if (var1.func_147439_a(var2 + 1, var3 - 1, var4).func_149688_o() == Material.field_151586_h) {
         return true;
      } else if (var1.func_147439_a(var2, var3 - 1, var4 - 1).func_149688_o() == Material.field_151586_h) {
         return true;
      } else {
         return var1.func_147439_a(var2, var3 - 1, var4 + 1).func_149688_o() == Material.field_151586_h;
      }
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      this.func_150170_e(var1, var2, var3, var4);
   }

   protected final boolean func_150170_e(World var1, int var2, int var3, int var4) {
      if (!this.func_149718_j(var1, var2, var3, var4)) {
         this.func_149697_b(var1, var2, var3, var4, var1.func_72805_g(var2, var3, var4), 0);
         var1.func_147468_f(var2, var3, var4);
         return false;
      } else {
         return true;
      }
   }

   @Override
   public boolean func_149718_j(World var1, int var2, int var3, int var4) {
      return this.func_149742_c(var1, var2, var3, var4);
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      return null;
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return Items.field_151120_aE;
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }

   @Override
   public int func_149645_b() {
      return 1;
   }

   @Override
   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      return Items.field_151120_aE;
   }

   @Override
   public int func_149720_d(IBlockAccess var1, int var2, int var3, int var4) {
      return var1.func_72807_a(var2, var4).func_150558_b(var2, var3, var4);
   }
}
