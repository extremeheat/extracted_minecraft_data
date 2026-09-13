package net.minecraft.block;

import java.util.Random;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockNetherWart extends BlockBush {
   private IIcon[] field_149883_a;

   protected BlockNetherWart() {
      super();
      this.func_149675_a(true);
      float var1 = 0.5F;
      this.func_149676_a(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, 0.25F, 0.5F + var1);
      this.func_149647_a(null);
   }

   @Override
   protected boolean func_149854_a(Block var1) {
      return var1 == Blocks.field_150425_aM;
   }

   @Override
   public boolean func_149718_j(World var1, int var2, int var3, int var4) {
      return this.func_149854_a(var1.func_147439_a(var2, var3 - 1, var4));
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      int var6 = var1.func_72805_g(var2, var3, var4);
      if (var6 < 3 && var5.nextInt(10) == 0) {
         var1.func_72921_c(var2, var3, var4, ++var6, 2);
      }

      super.func_149674_a(var1, var2, var3, var4, var5);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      if (var2 >= 3) {
         return this.field_149883_a[2];
      } else {
         return var2 > 0 ? this.field_149883_a[1] : this.field_149883_a[0];
      }
   }

   @Override
   public int func_149645_b() {
      return 6;
   }

   @Override
   public void func_149690_a(World var1, int var2, int var3, int var4, int var5, float var6, int var7) {
      if (!var1.field_72995_K) {
         int var8 = 1;
         if (var5 >= 3) {
            var8 = 2 + var1.field_73012_v.nextInt(3);
            if (var7 > 0) {
               var8 += var1.field_73012_v.nextInt(var7 + 1);
            }
         }

         for(int var9 = 0; var9 < var8; ++var9) {
            this.func_149642_a(var1, var2, var3, var4, new ItemStack(Items.field_151075_bm));
         }
      }
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return null;
   }

   @Override
   public int func_149745_a(Random var1) {
      return 0;
   }

   @Override
   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      return Items.field_151075_bm;
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149883_a = new IIcon[3];

      for(int var2 = 0; var2 < this.field_149883_a.length; ++var2) {
         this.field_149883_a[var2] = var1.func_94245_a(this.func_149641_N() + "_stage_" + var2);
      }
   }
}
