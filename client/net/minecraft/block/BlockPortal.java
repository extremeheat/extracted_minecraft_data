package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPortal extends BlockBreakable {
   public static final int[][] field_150001_a = new int[][]{new int[0], {3, 1}, {2, 0}};

   public BlockPortal() {
      super("portal", Material.field_151567_E, false);
      this.func_149675_a(true);
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      super.func_149674_a(var1, var2, var3, var4, var5);
      if (var1.field_73011_w.func_76569_d() && var1.func_82736_K().func_82766_b("doMobSpawning") && var5.nextInt(2000) < var1.field_73013_u.func_151525_a()) {
         int var6 = var3;

         while(!World.func_147466_a(var1, var2, var6, var4) && var6 > 0) {
            --var6;
         }

         if (var6 > 0 && !var1.func_147439_a(var2, var6 + 1, var4).func_149721_r()) {
            Entity var7 = ItemMonsterPlacer.func_77840_a(var1, 57, (double)var2 + 0.5, (double)var6 + 1.1, (double)var4 + 0.5);
            if (var7 != null) {
               var7.field_71088_bW = var7.func_82147_ab();
            }
         }
      }
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      return null;
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      int var5 = func_149999_b(var1.func_72805_g(var2, var3, var4));
      if (var5 == 0) {
         if (var1.func_147439_a(var2 - 1, var3, var4) != this && var1.func_147439_a(var2 + 1, var3, var4) != this) {
            var5 = 2;
         } else {
            var5 = 1;
         }

         if (var1 instanceof World && !((World)var1).field_72995_K) {
            ((World)var1).func_72921_c(var2, var3, var4, var5, 2);
         }
      }

      float var6 = 0.125F;
      float var7 = 0.125F;
      if (var5 == 1) {
         var6 = 0.5F;
      }

      if (var5 == 2) {
         var7 = 0.5F;
      }

      this.func_149676_a(0.5F - var6, 0.0F, 0.5F - var7, 0.5F + var6, 1.0F, 0.5F + var7);
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }

   public boolean func_150000_e(World var1, int var2, int var3, int var4) {
      BlockPortal$Size var5 = new BlockPortal$Size(var1, var2, var3, var4, 1);
      BlockPortal$Size var6 = new BlockPortal$Size(var1, var2, var3, var4, 2);
      if (var5.func_150860_b() && BlockPortal$Size.access$000(var5) == 0) {
         var5.func_150859_c();
         return true;
      } else if (var6.func_150860_b() && BlockPortal$Size.access$000(var6) == 0) {
         var6.func_150859_c();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      int var6 = func_149999_b(var1.func_72805_g(var2, var3, var4));
      BlockPortal$Size var7 = new BlockPortal$Size(var1, var2, var3, var4, 1);
      BlockPortal$Size var8 = new BlockPortal$Size(var1, var2, var3, var4, 2);
      if (var6 != 1 || var7.func_150860_b() && BlockPortal$Size.access$000(var7) >= BlockPortal$Size.access$100(var7) * BlockPortal$Size.access$200(var7)) {
         if (var6 != 2 || var8.func_150860_b() && BlockPortal$Size.access$000(var8) >= BlockPortal$Size.access$100(var8) * BlockPortal$Size.access$200(var8)) {
            if (var6 == 0 && !var7.func_150860_b() && !var8.func_150860_b()) {
               var1.func_147449_b(var2, var3, var4, Blocks.field_150350_a);
            }
         } else {
            var1.func_147449_b(var2, var3, var4, Blocks.field_150350_a);
         }
      } else {
         var1.func_147449_b(var2, var3, var4, Blocks.field_150350_a);
      }
   }

   @Override
   public boolean func_149646_a(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      int var6 = 0;
      if (var1.func_147439_a(var2, var3, var4) == this) {
         var6 = func_149999_b(var1.func_72805_g(var2, var3, var4));
         if (var6 == 0) {
            return false;
         }

         if (var6 == 2 && var5 != 5 && var5 != 4) {
            return false;
         }

         if (var6 == 1 && var5 != 3 && var5 != 2) {
            return false;
         }
      }

      boolean var7 = var1.func_147439_a(var2 - 1, var3, var4) == this && var1.func_147439_a(var2 - 2, var3, var4) != this;
      boolean var8 = var1.func_147439_a(var2 + 1, var3, var4) == this && var1.func_147439_a(var2 + 2, var3, var4) != this;
      boolean var9 = var1.func_147439_a(var2, var3, var4 - 1) == this && var1.func_147439_a(var2, var3, var4 - 2) != this;
      boolean var10 = var1.func_147439_a(var2, var3, var4 + 1) == this && var1.func_147439_a(var2, var3, var4 + 2) != this;
      boolean var11 = var7 || var8 || var6 == 1;
      boolean var12 = var9 || var10 || var6 == 2;
      if (var11 && var5 == 4) {
         return true;
      } else if (var11 && var5 == 5) {
         return true;
      } else if (var12 && var5 == 2) {
         return true;
      } else {
         return var12 && var5 == 3;
      }
   }

   @Override
   public int func_149745_a(Random var1) {
      return 0;
   }

   @Override
   public int func_149701_w() {
      return 1;
   }

   @Override
   public void func_149670_a(World var1, int var2, int var3, int var4, Entity var5) {
      if (var5.field_70154_o == null && var5.field_70153_n == null) {
         var5.func_70063_aa();
      }
   }

   @Override
   public void func_149734_b(World var1, int var2, int var3, int var4, Random var5) {
      if (var5.nextInt(100) == 0) {
         var1.func_72980_b((double)var2 + 0.5, (double)var3 + 0.5, (double)var4 + 0.5, "portal.portal", 0.5F, var5.nextFloat() * 0.4F + 0.8F, false);
      }

      for(int var6 = 0; var6 < 4; ++var6) {
         double var7 = (double)((float)var2 + var5.nextFloat());
         double var9 = (double)((float)var3 + var5.nextFloat());
         double var11 = (double)((float)var4 + var5.nextFloat());
         double var13 = 0.0;
         double var15 = 0.0;
         double var17 = 0.0;
         int var19 = var5.nextInt(2) * 2 - 1;
         var13 = ((double)var5.nextFloat() - 0.5) * 0.5;
         var15 = ((double)var5.nextFloat() - 0.5) * 0.5;
         var17 = ((double)var5.nextFloat() - 0.5) * 0.5;
         if (var1.func_147439_a(var2 - 1, var3, var4) != this && var1.func_147439_a(var2 + 1, var3, var4) != this) {
            var7 = (double)var2 + 0.5 + 0.25 * (double)var19;
            var13 = (double)(var5.nextFloat() * 2.0F * (float)var19);
         } else {
            var11 = (double)var4 + 0.5 + 0.25 * (double)var19;
            var17 = (double)(var5.nextFloat() * 2.0F * (float)var19);
         }

         var1.func_72869_a("portal", var7, var9, var11, var13, var15, var17);
      }
   }

   @Override
   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      return Item.func_150899_d(0);
   }

   public static int func_149999_b(int var0) {
      return var0 & 3;
   }
}
