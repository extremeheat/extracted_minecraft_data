package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCocoa extends BlockDirectional implements IGrowable {
   private IIcon[] field_149989_a;

   public BlockCocoa() {
      super(Material.field_151585_k);
      this.func_149675_a(true);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      return this.field_149989_a[2];
   }

   public IIcon func_149988_b(int var1) {
      if (var1 < 0 || var1 >= this.field_149989_a.length) {
         var1 = this.field_149989_a.length - 1;
      }

      return this.field_149989_a[var1];
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      if (!this.func_149718_j(var1, var2, var3, var4)) {
         this.func_149697_b(var1, var2, var3, var4, var1.func_72805_g(var2, var3, var4), 0);
         var1.func_147465_d(var2, var3, var4, func_149729_e(0), 0, 2);
      } else if (var1.field_73012_v.nextInt(5) == 0) {
         int var6 = var1.func_72805_g(var2, var3, var4);
         int var7 = func_149987_c(var6);
         if (var7 < 2) {
            var1.func_72921_c(var2, var3, var4, ++var7 << 2 | func_149895_l(var6), 2);
         }
      }
   }

   @Override
   public boolean func_149718_j(World var1, int var2, int var3, int var4) {
      int var5 = func_149895_l(var1.func_72805_g(var2, var3, var4));
      var2 += Direction.field_71583_a[var5];
      var4 += Direction.field_71581_b[var5];
      Block var6 = var1.func_147439_a(var2, var3, var4);
      return var6 == Blocks.field_150364_r && BlockLog.func_150165_c(var1.func_72805_g(var2, var3, var4)) == 3;
   }

   @Override
   public int func_149645_b() {
      return 28;
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      this.func_149719_a(var1, var2, var3, var4);
      return super.func_149668_a(var1, var2, var3, var4);
   }

   @Override
   public AxisAlignedBB func_149633_g(World var1, int var2, int var3, int var4) {
      this.func_149719_a(var1, var2, var3, var4);
      return super.func_149633_g(var1, var2, var3, var4);
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      int var6 = func_149895_l(var5);
      int var7 = func_149987_c(var5);
      int var8 = 4 + var7 * 2;
      int var9 = 5 + var7 * 2;
      float var10 = (float)var8 / 2.0F;
      switch(var6) {
         case 0:
            this.func_149676_a((8.0F - var10) / 16.0F, (12.0F - (float)var9) / 16.0F, (15.0F - (float)var8) / 16.0F, (8.0F + var10) / 16.0F, 0.75F, 0.9375F);
            break;
         case 1:
            this.func_149676_a(0.0625F, (12.0F - (float)var9) / 16.0F, (8.0F - var10) / 16.0F, (1.0F + (float)var8) / 16.0F, 0.75F, (8.0F + var10) / 16.0F);
            break;
         case 2:
            this.func_149676_a((8.0F - var10) / 16.0F, (12.0F - (float)var9) / 16.0F, 0.0625F, (8.0F + var10) / 16.0F, 0.75F, (1.0F + (float)var8) / 16.0F);
            break;
         case 3:
            this.func_149676_a((15.0F - (float)var8) / 16.0F, (12.0F - (float)var9) / 16.0F, (8.0F - var10) / 16.0F, 0.9375F, 0.75F, (8.0F + var10) / 16.0F);
      }
   }

   @Override
   public void func_149689_a(World var1, int var2, int var3, int var4, EntityLivingBase var5, ItemStack var6) {
      int var7 = ((MathHelper.func_76128_c((double)(var5.field_70177_z * 4.0F / 360.0F) + 0.5) & 3) + 0) % 4;
      var1.func_72921_c(var2, var3, var4, var7, 2);
   }

   @Override
   public int func_149660_a(World var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, int var9) {
      if (var5 == 1 || var5 == 0) {
         var5 = 2;
      }

      return Direction.field_71580_e[Direction.field_71579_d[var5]];
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      if (!this.func_149718_j(var1, var2, var3, var4)) {
         this.func_149697_b(var1, var2, var3, var4, var1.func_72805_g(var2, var3, var4), 0);
         var1.func_147465_d(var2, var3, var4, func_149729_e(0), 0, 2);
      }
   }

   public static int func_149987_c(int var0) {
      return (var0 & 12) >> 2;
   }

   @Override
   public void func_149690_a(World var1, int var2, int var3, int var4, int var5, float var6, int var7) {
      int var8 = func_149987_c(var5);
      byte var9 = 1;
      if (var8 >= 2) {
         var9 = 3;
      }

      for(int var10 = 0; var10 < var9; ++var10) {
         this.func_149642_a(var1, var2, var3, var4, new ItemStack(Items.field_151100_aR, 1, 3));
      }
   }

   @Override
   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      return Items.field_151100_aR;
   }

   @Override
   public int func_149643_k(World var1, int var2, int var3, int var4) {
      return 3;
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149989_a = new IIcon[3];

      for(int var2 = 0; var2 < this.field_149989_a.length; ++var2) {
         this.field_149989_a[var2] = var1.func_94245_a(this.func_149641_N() + "_stage_" + var2);
      }
   }

   @Override
   public boolean func_149851_a(World var1, int var2, int var3, int var4, boolean var5) {
      int var6 = var1.func_72805_g(var2, var3, var4);
      int var7 = func_149987_c(var6);
      return var7 < 2;
   }

   @Override
   public boolean func_149852_a(World var1, Random var2, int var3, int var4, int var5) {
      return true;
   }

   @Override
   public void func_149853_b(World var1, Random var2, int var3, int var4, int var5) {
      int var6 = var1.func_72805_g(var3, var4, var5);
      int var7 = BlockDirectional.func_149895_l(var6);
      int var8 = func_149987_c(var6);
      var1.func_72921_c(var3, var4, var5, ++var8 << 2 | var7, 2);
   }
}
