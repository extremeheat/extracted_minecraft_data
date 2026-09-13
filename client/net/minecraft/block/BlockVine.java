package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockVine extends Block {
   public BlockVine() {
      super(Material.field_151582_l);
      this.func_149675_a(true);
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   @Override
   public void func_149683_g() {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public int func_149645_b() {
      return 20;
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
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      float var5 = 0.0625F;
      int var6 = var1.func_72805_g(var2, var3, var4);
      float var7 = 1.0F;
      float var8 = 1.0F;
      float var9 = 1.0F;
      float var10 = 0.0F;
      float var11 = 0.0F;
      float var12 = 0.0F;
      boolean var13 = var6 > 0;
      if ((var6 & 2) != 0) {
         var10 = Math.max(var10, 0.0625F);
         var7 = 0.0F;
         var8 = 0.0F;
         var11 = 1.0F;
         var9 = 0.0F;
         var12 = 1.0F;
         var13 = true;
      }

      if ((var6 & 8) != 0) {
         var7 = Math.min(var7, 0.9375F);
         var10 = 1.0F;
         var8 = 0.0F;
         var11 = 1.0F;
         var9 = 0.0F;
         var12 = 1.0F;
         var13 = true;
      }

      if ((var6 & 4) != 0) {
         var12 = Math.max(var12, 0.0625F);
         var9 = 0.0F;
         var7 = 0.0F;
         var10 = 1.0F;
         var8 = 0.0F;
         var11 = 1.0F;
         var13 = true;
      }

      if ((var6 & 1) != 0) {
         var9 = Math.min(var9, 0.9375F);
         var12 = 1.0F;
         var7 = 0.0F;
         var10 = 1.0F;
         var8 = 0.0F;
         var11 = 1.0F;
         var13 = true;
      }

      if (!var13 && this.func_150093_a(var1.func_147439_a(var2, var3 + 1, var4))) {
         var8 = Math.min(var8, 0.9375F);
         var11 = 1.0F;
         var7 = 0.0F;
         var10 = 1.0F;
         var9 = 0.0F;
         var12 = 1.0F;
      }

      this.func_149676_a(var7, var8, var9, var10, var11, var12);
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      return null;
   }

   @Override
   public boolean func_149707_d(World var1, int var2, int var3, int var4, int var5) {
      switch(var5) {
         case 1:
            return this.func_150093_a(var1.func_147439_a(var2, var3 + 1, var4));
         case 2:
            return this.func_150093_a(var1.func_147439_a(var2, var3, var4 + 1));
         case 3:
            return this.func_150093_a(var1.func_147439_a(var2, var3, var4 - 1));
         case 4:
            return this.func_150093_a(var1.func_147439_a(var2 + 1, var3, var4));
         case 5:
            return this.func_150093_a(var1.func_147439_a(var2 - 1, var3, var4));
         default:
            return false;
      }
   }

   private boolean func_150093_a(Block var1) {
      return var1.func_149686_d() && var1.field_149764_J.func_76230_c();
   }

   private boolean func_150094_e(World var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      int var6 = var5;
      if (var5 > 0) {
         for(int var7 = 0; var7 <= 3; ++var7) {
            int var8 = 1 << var7;
            if ((var5 & var8) != 0
               && !this.func_150093_a(var1.func_147439_a(var2 + Direction.field_71583_a[var7], var3, var4 + Direction.field_71581_b[var7]))
               && (var1.func_147439_a(var2, var3 + 1, var4) != this || (var1.func_72805_g(var2, var3 + 1, var4) & var8) == 0)) {
               var6 &= ~var8;
            }
         }
      }

      if (var6 == 0 && !this.func_150093_a(var1.func_147439_a(var2, var3 + 1, var4))) {
         return false;
      } else {
         if (var6 != var5) {
            var1.func_72921_c(var2, var3, var4, var6, 2);
         }

         return true;
      }
   }

   @Override
   public int func_149635_D() {
      return ColorizerFoliage.func_77468_c();
   }

   @Override
   public int func_149741_i(int var1) {
      return ColorizerFoliage.func_77468_c();
   }

   @Override
   public int func_149720_d(IBlockAccess var1, int var2, int var3, int var4) {
      return var1.func_72807_a(var2, var4).func_150571_c(var2, var3, var4);
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      if (!var1.field_72995_K && !this.func_150094_e(var1, var2, var3, var4)) {
         this.func_149697_b(var1, var2, var3, var4, var1.func_72805_g(var2, var3, var4), 0);
         var1.func_147468_f(var2, var3, var4);
      }
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      if (!var1.field_72995_K && var1.field_73012_v.nextInt(4) == 0) {
         byte var6 = 4;
         int var7 = 5;
         boolean var8 = false;

         label137:
         for(int var9 = var2 - var6; var9 <= var2 + var6; ++var9) {
            for(int var10 = var4 - var6; var10 <= var4 + var6; ++var10) {
               for(int var11 = var3 - 1; var11 <= var3 + 1; ++var11) {
                  if (var1.func_147439_a(var9, var11, var10) == this) {
                     if (--var7 <= 0) {
                        var8 = true;
                        break label137;
                     }
                  }
               }
            }
         }

         int var15 = var1.func_72805_g(var2, var3, var4);
         int var16 = var1.field_73012_v.nextInt(6);
         int var17 = Direction.field_71579_d[var16];
         if (var16 == 1 && var3 < 255 && var1.func_147437_c(var2, var3 + 1, var4)) {
            if (var8) {
               return;
            }

            int var19 = var1.field_73012_v.nextInt(16) & var15;
            if (var19 > 0) {
               for(int var22 = 0; var22 <= 3; ++var22) {
                  if (!this.func_150093_a(var1.func_147439_a(var2 + Direction.field_71583_a[var22], var3 + 1, var4 + Direction.field_71581_b[var22]))) {
                     var19 &= ~(1 << var22);
                  }
               }

               if (var19 > 0) {
                  var1.func_147465_d(var2, var3 + 1, var4, this, var19, 2);
               }
            }
         } else if (var16 >= 2 && var16 <= 5 && (var15 & 1 << var17) == 0) {
            if (var8) {
               return;
            }

            Block var18 = var1.func_147439_a(var2 + Direction.field_71583_a[var17], var3, var4 + Direction.field_71581_b[var17]);
            if (var18.field_149764_J == Material.field_151579_a) {
               int var21 = var17 + 1 & 3;
               int var23 = var17 + 3 & 3;
               if ((var15 & 1 << var21) != 0
                  && this.func_150093_a(
                     var1.func_147439_a(
                        var2 + Direction.field_71583_a[var17] + Direction.field_71583_a[var21],
                        var3,
                        var4 + Direction.field_71581_b[var17] + Direction.field_71581_b[var21]
                     )
                  )) {
                  var1.func_147465_d(var2 + Direction.field_71583_a[var17], var3, var4 + Direction.field_71581_b[var17], this, 1 << var21, 2);
               } else if ((var15 & 1 << var23) != 0
                  && this.func_150093_a(
                     var1.func_147439_a(
                        var2 + Direction.field_71583_a[var17] + Direction.field_71583_a[var23],
                        var3,
                        var4 + Direction.field_71581_b[var17] + Direction.field_71581_b[var23]
                     )
                  )) {
                  var1.func_147465_d(var2 + Direction.field_71583_a[var17], var3, var4 + Direction.field_71581_b[var17], this, 1 << var23, 2);
               } else if ((var15 & 1 << var21) != 0
                  && var1.func_147437_c(
                     var2 + Direction.field_71583_a[var17] + Direction.field_71583_a[var21],
                     var3,
                     var4 + Direction.field_71581_b[var17] + Direction.field_71581_b[var21]
                  )
                  && this.func_150093_a(var1.func_147439_a(var2 + Direction.field_71583_a[var21], var3, var4 + Direction.field_71581_b[var21]))) {
                  var1.func_147465_d(
                     var2 + Direction.field_71583_a[var17] + Direction.field_71583_a[var21],
                     var3,
                     var4 + Direction.field_71581_b[var17] + Direction.field_71581_b[var21],
                     this,
                     1 << (var17 + 2 & 3),
                     2
                  );
               } else if ((var15 & 1 << var23) != 0
                  && var1.func_147437_c(
                     var2 + Direction.field_71583_a[var17] + Direction.field_71583_a[var23],
                     var3,
                     var4 + Direction.field_71581_b[var17] + Direction.field_71581_b[var23]
                  )
                  && this.func_150093_a(var1.func_147439_a(var2 + Direction.field_71583_a[var23], var3, var4 + Direction.field_71581_b[var23]))) {
                  var1.func_147465_d(
                     var2 + Direction.field_71583_a[var17] + Direction.field_71583_a[var23],
                     var3,
                     var4 + Direction.field_71581_b[var17] + Direction.field_71581_b[var23],
                     this,
                     1 << (var17 + 2 & 3),
                     2
                  );
               } else if (this.func_150093_a(var1.func_147439_a(var2 + Direction.field_71583_a[var17], var3 + 1, var4 + Direction.field_71581_b[var17]))) {
                  var1.func_147465_d(var2 + Direction.field_71583_a[var17], var3, var4 + Direction.field_71581_b[var17], this, 0, 2);
               }
            } else if (var18.field_149764_J.func_76218_k() && var18.func_149686_d()) {
               var1.func_72921_c(var2, var3, var4, var15 | 1 << var17, 2);
            }
         } else if (var3 > 1) {
            Block var12 = var1.func_147439_a(var2, var3 - 1, var4);
            if (var12.field_149764_J == Material.field_151579_a) {
               int var13 = var1.field_73012_v.nextInt(16) & var15;
               if (var13 > 0) {
                  var1.func_147465_d(var2, var3 - 1, var4, this, var13, 2);
               }
            } else if (var12 == this) {
               int var20 = var1.field_73012_v.nextInt(16) & var15;
               int var14 = var1.func_72805_g(var2, var3 - 1, var4);
               if (var14 != (var14 | var20)) {
                  var1.func_72921_c(var2, var3 - 1, var4, var14 | var20, 2);
               }
            }
         }
      }
   }

   @Override
   public int func_149660_a(World var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, int var9) {
      byte var10 = 0;
      switch(var5) {
         case 2:
            var10 = 1;
            break;
         case 3:
            var10 = 4;
            break;
         case 4:
            var10 = 8;
            break;
         case 5:
            var10 = 2;
      }

      return var10 != 0 ? var10 : var9;
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
   public void func_149636_a(World var1, EntityPlayer var2, int var3, int var4, int var5, int var6) {
      if (!var1.field_72995_K && var2.func_71045_bC() != null && var2.func_71045_bC().func_77973_b() == Items.field_151097_aZ) {
         var2.func_71064_a(StatList.field_75934_C[Block.func_149682_b(this)], 1);
         this.func_149642_a(var1, var3, var4, var5, new ItemStack(Blocks.field_150395_bd, 1, 0));
      } else {
         super.func_149636_a(var1, var2, var3, var4, var5, var6);
      }
   }
}
