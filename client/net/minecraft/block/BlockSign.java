package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSign extends BlockContainer {
   private Class field_149968_a;
   private boolean field_149967_b;

   protected BlockSign(Class var1, boolean var2) {
      super(Material.field_151575_d);
      this.field_149967_b = var2;
      this.field_149968_a = var1;
      float var3 = 0.25F;
      float var4 = 1.0F;
      this.func_149676_a(0.5F - var3, 0.0F, 0.5F - var3, 0.5F + var3, var4, 0.5F + var3);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      return Blocks.field_150344_f.func_149733_h(var1);
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      return null;
   }

   @Override
   public AxisAlignedBB func_149633_g(World var1, int var2, int var3, int var4) {
      this.func_149719_a(var1, var2, var3, var4);
      return super.func_149633_g(var1, var2, var3, var4);
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      if (!this.field_149967_b) {
         int var5 = var1.func_72805_g(var2, var3, var4);
         float var6 = 0.28125F;
         float var7 = 0.78125F;
         float var8 = 0.0F;
         float var9 = 1.0F;
         float var10 = 0.125F;
         this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
         if (var5 == 2) {
            this.func_149676_a(var8, var6, 1.0F - var10, var9, var7, 1.0F);
         }

         if (var5 == 3) {
            this.func_149676_a(var8, var6, 0.0F, var9, var7, var10);
         }

         if (var5 == 4) {
            this.func_149676_a(1.0F - var10, var6, var8, 1.0F, var7, var9);
         }

         if (var5 == 5) {
            this.func_149676_a(0.0F, var6, var8, var10, var7, var9);
         }
      }
   }

   @Override
   public int func_149645_b() {
      return -1;
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }

   @Override
   public boolean func_149655_b(IBlockAccess var1, int var2, int var3, int var4) {
      return true;
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public TileEntity func_149915_a(World var1, int var2) {
      try {
         return (TileEntity)this.field_149968_a.newInstance();
      } catch (Exception var4) {
         throw new RuntimeException(var4);
      }
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return Items.field_151155_ap;
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      boolean var6 = false;
      if (this.field_149967_b) {
         if (!var1.func_147439_a(var2, var3 - 1, var4).func_149688_o().func_76220_a()) {
            var6 = true;
         }
      } else {
         int var7 = var1.func_72805_g(var2, var3, var4);
         var6 = true;
         if (var7 == 2 && var1.func_147439_a(var2, var3, var4 + 1).func_149688_o().func_76220_a()) {
            var6 = false;
         }

         if (var7 == 3 && var1.func_147439_a(var2, var3, var4 - 1).func_149688_o().func_76220_a()) {
            var6 = false;
         }

         if (var7 == 4 && var1.func_147439_a(var2 + 1, var3, var4).func_149688_o().func_76220_a()) {
            var6 = false;
         }

         if (var7 == 5 && var1.func_147439_a(var2 - 1, var3, var4).func_149688_o().func_76220_a()) {
            var6 = false;
         }
      }

      if (var6) {
         this.func_149697_b(var1, var2, var3, var4, var1.func_72805_g(var2, var3, var4), 0);
         var1.func_147468_f(var2, var3, var4);
      }

      super.func_149695_a(var1, var2, var3, var4, var5);
   }

   @Override
   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      return Items.field_151155_ap;
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
   }
}
