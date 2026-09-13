package net.minecraft.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneTorch extends BlockTorch {
   private boolean field_150113_a;
   private static Map field_150112_b = new HashMap();

   private boolean func_150111_a(World var1, int var2, int var3, int var4, boolean var5) {
      if (!field_150112_b.containsKey(var1)) {
         field_150112_b.put(var1, new ArrayList());
      }

      List var6 = (List)field_150112_b.get(var1);
      if (var5) {
         var6.add(new BlockRedstoneTorch$Toggle(var2, var3, var4, var1.func_82737_E()));
      }

      int var7 = 0;

      for(int var8 = 0; var8 < var6.size(); ++var8) {
         BlockRedstoneTorch$Toggle var9 = (BlockRedstoneTorch$Toggle)var6.get(var8);
         if (var9.field_150847_a == var2 && var9.field_150845_b == var3 && var9.field_150846_c == var4) {
            if (++var7 >= 8) {
               return true;
            }
         }
      }

      return false;
   }

   protected BlockRedstoneTorch(boolean var1) {
      super();
      this.field_150113_a = var1;
      this.func_149675_a(true);
      this.func_149647_a(null);
   }

   @Override
   public int func_149738_a(World var1) {
      return 2;
   }

   @Override
   public void func_149726_b(World var1, int var2, int var3, int var4) {
      if (var1.func_72805_g(var2, var3, var4) == 0) {
         super.func_149726_b(var1, var2, var3, var4);
      }

      if (this.field_150113_a) {
         var1.func_147459_d(var2, var3 - 1, var4, this);
         var1.func_147459_d(var2, var3 + 1, var4, this);
         var1.func_147459_d(var2 - 1, var3, var4, this);
         var1.func_147459_d(var2 + 1, var3, var4, this);
         var1.func_147459_d(var2, var3, var4 - 1, this);
         var1.func_147459_d(var2, var3, var4 + 1, this);
      }
   }

   @Override
   public void func_149749_a(World var1, int var2, int var3, int var4, Block var5, int var6) {
      if (this.field_150113_a) {
         var1.func_147459_d(var2, var3 - 1, var4, this);
         var1.func_147459_d(var2, var3 + 1, var4, this);
         var1.func_147459_d(var2 - 1, var3, var4, this);
         var1.func_147459_d(var2 + 1, var3, var4, this);
         var1.func_147459_d(var2, var3, var4 - 1, this);
         var1.func_147459_d(var2, var3, var4 + 1, this);
      }
   }

   @Override
   public int func_149709_b(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      if (!this.field_150113_a) {
         return 0;
      } else {
         int var6 = var1.func_72805_g(var2, var3, var4);
         if (var6 == 5 && var5 == 1) {
            return 0;
         } else if (var6 == 3 && var5 == 3) {
            return 0;
         } else if (var6 == 4 && var5 == 2) {
            return 0;
         } else if (var6 == 1 && var5 == 5) {
            return 0;
         } else {
            return var6 == 2 && var5 == 4 ? 0 : 15;
         }
      }
   }

   private boolean func_150110_m(World var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      if (var5 == 5 && var1.func_94574_k(var2, var3 - 1, var4, 0)) {
         return true;
      } else if (var5 == 3 && var1.func_94574_k(var2, var3, var4 - 1, 2)) {
         return true;
      } else if (var5 == 4 && var1.func_94574_k(var2, var3, var4 + 1, 3)) {
         return true;
      } else if (var5 == 1 && var1.func_94574_k(var2 - 1, var3, var4, 4)) {
         return true;
      } else {
         return var5 == 2 && var1.func_94574_k(var2 + 1, var3, var4, 5);
      }
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      boolean var6 = this.func_150110_m(var1, var2, var3, var4);
      List var7 = (List)field_150112_b.get(var1);

      while(var7 != null && !var7.isEmpty() && var1.func_82737_E() - ((BlockRedstoneTorch$Toggle)var7.get(0)).field_150844_d > 60L) {
         var7.remove(0);
      }

      if (this.field_150113_a) {
         if (var6) {
            var1.func_147465_d(var2, var3, var4, Blocks.field_150437_az, var1.func_72805_g(var2, var3, var4), 3);
            if (this.func_150111_a(var1, var2, var3, var4, true)) {
               var1.func_72908_a(
                  (double)((float)var2 + 0.5F),
                  (double)((float)var3 + 0.5F),
                  (double)((float)var4 + 0.5F),
                  "random.fizz",
                  0.5F,
                  2.6F + (var1.field_73012_v.nextFloat() - var1.field_73012_v.nextFloat()) * 0.8F
               );

               for(int var8 = 0; var8 < 5; ++var8) {
                  double var9 = (double)var2 + var5.nextDouble() * 0.6 + 0.2;
                  double var11 = (double)var3 + var5.nextDouble() * 0.6 + 0.2;
                  double var13 = (double)var4 + var5.nextDouble() * 0.6 + 0.2;
                  var1.func_72869_a("smoke", var9, var11, var13, 0.0, 0.0, 0.0);
               }
            }
         }
      } else if (!var6 && !this.func_150111_a(var1, var2, var3, var4, false)) {
         var1.func_147465_d(var2, var3, var4, Blocks.field_150429_aA, var1.func_72805_g(var2, var3, var4), 3);
      }
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      if (!this.func_150108_b(var1, var2, var3, var4, var5)) {
         boolean var6 = this.func_150110_m(var1, var2, var3, var4);
         if (this.field_150113_a && var6 || !this.field_150113_a && !var6) {
            var1.func_147464_a(var2, var3, var4, this, this.func_149738_a(var1));
         }
      }
   }

   @Override
   public int func_149748_c(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return var5 == 0 ? this.func_149709_b(var1, var2, var3, var4, var5) : 0;
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return Item.func_150898_a(Blocks.field_150429_aA);
   }

   @Override
   public boolean func_149744_f() {
      return true;
   }

   @Override
   public void func_149734_b(World var1, int var2, int var3, int var4, Random var5) {
      if (this.field_150113_a) {
         int var6 = var1.func_72805_g(var2, var3, var4);
         double var7 = (double)((float)var2 + 0.5F) + (double)(var5.nextFloat() - 0.5F) * 0.2;
         double var9 = (double)((float)var3 + 0.7F) + (double)(var5.nextFloat() - 0.5F) * 0.2;
         double var11 = (double)((float)var4 + 0.5F) + (double)(var5.nextFloat() - 0.5F) * 0.2;
         double var13 = 0.2199999988079071;
         double var15 = 0.27000001072883606;
         if (var6 == 1) {
            var1.func_72869_a("reddust", var7 - var15, var9 + var13, var11, 0.0, 0.0, 0.0);
         } else if (var6 == 2) {
            var1.func_72869_a("reddust", var7 + var15, var9 + var13, var11, 0.0, 0.0, 0.0);
         } else if (var6 == 3) {
            var1.func_72869_a("reddust", var7, var9 + var13, var11 - var15, 0.0, 0.0, 0.0);
         } else if (var6 == 4) {
            var1.func_72869_a("reddust", var7, var9 + var13, var11 + var15, 0.0, 0.0, 0.0);
         } else {
            var1.func_72869_a("reddust", var7, var9, var11, 0.0, 0.0, 0.0);
         }
      }
   }

   @Override
   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      return Item.func_150898_a(Blocks.field_150429_aA);
   }

   @Override
   public boolean func_149667_c(Block var1) {
      return var1 == Blocks.field_150437_az || var1 == Blocks.field_150429_aA;
   }
}
