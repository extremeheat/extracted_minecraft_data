package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class BlockFalling extends Block {
   public static boolean field_149832_M;

   public BlockFalling() {
      super(Material.field_151595_p);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public BlockFalling(Material var1) {
      super(var1);
   }

   @Override
   public void func_149726_b(World var1, int var2, int var3, int var4) {
      var1.func_147464_a(var2, var3, var4, this, this.func_149738_a(var1));
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      var1.func_147464_a(var2, var3, var4, this, this.func_149738_a(var1));
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      if (!var1.field_72995_K) {
         this.func_149830_m(var1, var2, var3, var4);
      }
   }

   private void func_149830_m(World var1, int var2, int var3, int var4) {
      if (func_149831_e(var1, var2, var3 - 1, var4) && var3 >= 0) {
         byte var8 = 32;
         if (field_149832_M || !var1.func_72904_c(var2 - var8, var3 - var8, var4 - var8, var2 + var8, var3 + var8, var4 + var8)) {
            var1.func_147468_f(var2, var3, var4);

            while(func_149831_e(var1, var2, var3 - 1, var4) && var3 > 0) {
               --var3;
            }

            if (var3 > 0) {
               var1.func_147449_b(var2, var3, var4, this);
            }
         } else if (!var1.field_72995_K) {
            EntityFallingBlock var9 = new EntityFallingBlock(
               var1, (double)((float)var2 + 0.5F), (double)((float)var3 + 0.5F), (double)((float)var4 + 0.5F), this, var1.func_72805_g(var2, var3, var4)
            );
            this.func_149829_a(var9);
            var1.func_72838_d(var9);
         }
      }
   }

   protected void func_149829_a(EntityFallingBlock var1) {
   }

   @Override
   public int func_149738_a(World var1) {
      return 2;
   }

   public static boolean func_149831_e(World var0, int var1, int var2, int var3) {
      Block var4 = var0.func_147439_a(var1, var2, var3);
      if (var4.field_149764_J == Material.field_151579_a) {
         return true;
      } else if (var4 == Blocks.field_150480_ab) {
         return true;
      } else {
         Material var5 = var4.field_149764_J;
         if (var5 == Material.field_151586_h) {
            return true;
         } else {
            return var5 == Material.field_151587_i;
         }
      }
   }

   public void func_149828_a(World var1, int var2, int var3, int var4, int var5) {
   }
}
