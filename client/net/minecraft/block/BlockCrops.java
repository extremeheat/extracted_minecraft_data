package net.minecraft.block;

import java.util.Random;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockCrops extends BlockBush implements IGrowable {
   private IIcon[] field_149867_a;

   protected BlockCrops() {
      super();
      this.func_149675_a(true);
      float var1 = 0.5F;
      this.func_149676_a(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, 0.25F, 0.5F + var1);
      this.func_149647_a(null);
      this.func_149711_c(0.0F);
      this.func_149672_a(field_149779_h);
      this.func_149649_H();
   }

   @Override
   protected boolean func_149854_a(Block var1) {
      return var1 == Blocks.field_150458_ak;
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      super.func_149674_a(var1, var2, var3, var4, var5);
      if (var1.func_72957_l(var2, var3 + 1, var4) >= 9) {
         int var6 = var1.func_72805_g(var2, var3, var4);
         if (var6 < 7) {
            float var7 = this.func_149864_n(var1, var2, var3, var4);
            if (var5.nextInt((int)(25.0F / var7) + 1) == 0) {
               var1.func_72921_c(var2, var3, var4, ++var6, 2);
            }
         }
      }
   }

   public void func_149863_m(World var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4) + MathHelper.func_76136_a(var1.field_73012_v, 2, 5);
      if (var5 > 7) {
         var5 = 7;
      }

      var1.func_72921_c(var2, var3, var4, var5, 2);
   }

   private float func_149864_n(World var1, int var2, int var3, int var4) {
      float var5 = 1.0F;
      Block var6 = var1.func_147439_a(var2, var3, var4 - 1);
      Block var7 = var1.func_147439_a(var2, var3, var4 + 1);
      Block var8 = var1.func_147439_a(var2 - 1, var3, var4);
      Block var9 = var1.func_147439_a(var2 + 1, var3, var4);
      Block var10 = var1.func_147439_a(var2 - 1, var3, var4 - 1);
      Block var11 = var1.func_147439_a(var2 + 1, var3, var4 - 1);
      Block var12 = var1.func_147439_a(var2 + 1, var3, var4 + 1);
      Block var13 = var1.func_147439_a(var2 - 1, var3, var4 + 1);
      boolean var14 = var8 == this || var9 == this;
      boolean var15 = var6 == this || var7 == this;
      boolean var16 = var10 == this || var11 == this || var12 == this || var13 == this;

      for(int var17 = var2 - 1; var17 <= var2 + 1; ++var17) {
         for(int var18 = var4 - 1; var18 <= var4 + 1; ++var18) {
            float var19 = 0.0F;
            if (var1.func_147439_a(var17, var3 - 1, var18) == Blocks.field_150458_ak) {
               var19 = 1.0F;
               if (var1.func_72805_g(var17, var3 - 1, var18) > 0) {
                  var19 = 3.0F;
               }
            }

            if (var17 != var2 || var18 != var4) {
               var19 /= 4.0F;
            }

            var5 += var19;
         }
      }

      if (var16 || var14 && var15) {
         var5 /= 2.0F;
      }

      return var5;
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      if (var2 < 0 || var2 > 7) {
         var2 = 7;
      }

      return this.field_149867_a[var2];
   }

   @Override
   public int func_149645_b() {
      return 6;
   }

   protected Item func_149866_i() {
      return Items.field_151014_N;
   }

   protected Item func_149865_P() {
      return Items.field_151015_O;
   }

   @Override
   public void func_149690_a(World var1, int var2, int var3, int var4, int var5, float var6, int var7) {
      super.func_149690_a(var1, var2, var3, var4, var5, var6, 0);
      if (!var1.field_72995_K) {
         if (var5 >= 7) {
            int var8 = 3 + var7;

            for(int var9 = 0; var9 < var8; ++var9) {
               if (var1.field_73012_v.nextInt(15) <= var5) {
                  this.func_149642_a(var1, var2, var3, var4, new ItemStack(this.func_149866_i(), 1, 0));
               }
            }
         }
      }
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return var1 == 7 ? this.func_149865_P() : this.func_149866_i();
   }

   @Override
   public int func_149745_a(Random var1) {
      return 1;
   }

   @Override
   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      return this.func_149866_i();
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149867_a = new IIcon[8];

      for(int var2 = 0; var2 < this.field_149867_a.length; ++var2) {
         this.field_149867_a[var2] = var1.func_94245_a(this.func_149641_N() + "_stage_" + var2);
      }
   }

   @Override
   public boolean func_149851_a(World var1, int var2, int var3, int var4, boolean var5) {
      return var1.func_72805_g(var2, var3, var4) != 7;
   }

   @Override
   public boolean func_149852_a(World var1, Random var2, int var3, int var4, int var5) {
      return true;
   }

   @Override
   public void func_149853_b(World var1, Random var2, int var3, int var4, int var5) {
      this.func_149863_m(var1, var3, var4, var5);
   }
}
