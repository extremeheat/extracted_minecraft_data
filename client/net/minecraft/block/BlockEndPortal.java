package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockEndPortal extends BlockContainer {
   public static boolean field_149948_a;

   protected BlockEndPortal(Material var1) {
      super(var1);
      this.func_149715_a(1.0F);
   }

   @Override
   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityEndPortal();
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      float var5 = 0.0625F;
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, var5, 1.0F);
   }

   @Override
   public boolean func_149646_a(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return var5 != 0 ? false : super.func_149646_a(var1, var2, var3, var4, var5);
   }

   @Override
   public void func_149743_a(World var1, int var2, int var3, int var4, AxisAlignedBB var5, List var6, Entity var7) {
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
   public int func_149745_a(Random var1) {
      return 0;
   }

   @Override
   public void func_149670_a(World var1, int var2, int var3, int var4, Entity var5) {
      if (var5.field_70154_o == null && var5.field_70153_n == null && !var1.field_72995_K) {
         var5.func_71027_c(1);
      }
   }

   @Override
   public void func_149734_b(World var1, int var2, int var3, int var4, Random var5) {
      double var6 = (double)((float)var2 + var5.nextFloat());
      double var8 = (double)((float)var3 + 0.8F);
      double var10 = (double)((float)var4 + var5.nextFloat());
      double var12 = 0.0;
      double var14 = 0.0;
      double var16 = 0.0;
      var1.func_72869_a("smoke", var6, var8, var10, var12, var14, var16);
   }

   @Override
   public int func_149645_b() {
      return -1;
   }

   @Override
   public void func_149726_b(World var1, int var2, int var3, int var4) {
      if (!field_149948_a) {
         if (var1.field_73011_w.field_76574_g != 0) {
            var1.func_147468_f(var2, var3, var4);
         }
      }
   }

   @Override
   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      return Item.func_150899_d(0);
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149761_L = var1.func_94245_a("portal");
   }

   @Override
   public MapColor func_149728_f(int var1) {
      return MapColor.field_151654_J;
   }
}
