package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockHopper extends BlockContainer {
   private final Random field_149922_a = new Random();
   private IIcon field_149921_b;
   private IIcon field_149923_M;
   private IIcon field_149924_N;

   public BlockHopper() {
      super(Material.field_151573_f);
      this.func_149647_a(CreativeTabs.field_78028_d);
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public void func_149743_a(World var1, int var2, int var3, int var4, AxisAlignedBB var5, List var6, Entity var7) {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
      super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
      float var8 = 0.125F;
      this.func_149676_a(0.0F, 0.0F, 0.0F, var8, 1.0F, 1.0F);
      super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var8);
      super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
      this.func_149676_a(1.0F - var8, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
      this.func_149676_a(0.0F, 0.0F, 1.0F - var8, 1.0F, 1.0F, 1.0F);
      super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public int func_149660_a(World var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, int var9) {
      int var10 = Facing.field_71588_a[var5];
      if (var10 == 1) {
         var10 = 0;
      }

      return var10;
   }

   @Override
   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityHopper();
   }

   @Override
   public void func_149689_a(World var1, int var2, int var3, int var4, EntityLivingBase var5, ItemStack var6) {
      super.func_149689_a(var1, var2, var3, var4, var5, var6);
      if (var6.func_82837_s()) {
         TileEntityHopper var7 = func_149920_e(var1, var2, var3, var4);
         var7.func_145886_a(var6.func_82833_r());
      }
   }

   @Override
   public void func_149726_b(World var1, int var2, int var3, int var4) {
      super.func_149726_b(var1, var2, var3, var4);
      this.func_149919_e(var1, var2, var3, var4);
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      if (var1.field_72995_K) {
         return true;
      } else {
         TileEntityHopper var10 = func_149920_e(var1, var2, var3, var4);
         if (var10 != null) {
            var5.func_146093_a(var10);
         }

         return true;
      }
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      this.func_149919_e(var1, var2, var3, var4);
   }

   private void func_149919_e(World var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      int var6 = func_149918_b(var5);
      boolean var7 = !var1.func_72864_z(var2, var3, var4);
      boolean var8 = func_149917_c(var5);
      if (var7 != var8) {
         var1.func_72921_c(var2, var3, var4, var6 | (var7 ? 0 : 8), 4);
      }
   }

   @Override
   public void func_149749_a(World var1, int var2, int var3, int var4, Block var5, int var6) {
      TileEntityHopper var7 = (TileEntityHopper)var1.func_147438_o(var2, var3, var4);
      if (var7 != null) {
         for(int var8 = 0; var8 < var7.func_70302_i_(); ++var8) {
            ItemStack var9 = var7.func_70301_a(var8);
            if (var9 != null) {
               float var10 = this.field_149922_a.nextFloat() * 0.8F + 0.1F;
               float var11 = this.field_149922_a.nextFloat() * 0.8F + 0.1F;
               float var12 = this.field_149922_a.nextFloat() * 0.8F + 0.1F;

               while(var9.field_77994_a > 0) {
                  int var13 = this.field_149922_a.nextInt(21) + 10;
                  if (var13 > var9.field_77994_a) {
                     var13 = var9.field_77994_a;
                  }

                  var9.field_77994_a -= var13;
                  EntityItem var14 = new EntityItem(
                     var1,
                     (double)((float)var2 + var10),
                     (double)((float)var3 + var11),
                     (double)((float)var4 + var12),
                     new ItemStack(var9.func_77973_b(), var13, var9.func_77960_j())
                  );
                  if (var9.func_77942_o()) {
                     var14.func_92059_d().func_77982_d((NBTTagCompound)var9.func_77978_p().func_74737_b());
                  }

                  float var15 = 0.05F;
                  var14.field_70159_w = (double)((float)this.field_149922_a.nextGaussian() * var15);
                  var14.field_70181_x = (double)((float)this.field_149922_a.nextGaussian() * var15 + 0.2F);
                  var14.field_70179_y = (double)((float)this.field_149922_a.nextGaussian() * var15);
                  var1.func_72838_d(var14);
               }
            }
         }

         var1.func_147453_f(var2, var3, var4, var5);
      }

      super.func_149749_a(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public int func_149645_b() {
      return 38;
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
   public boolean func_149646_a(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return true;
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      return var1 == 1 ? this.field_149923_M : this.field_149921_b;
   }

   public static int func_149918_b(int var0) {
      return var0 & 7;
   }

   public static boolean func_149917_c(int var0) {
      return (var0 & 8) != 8;
   }

   @Override
   public boolean func_149740_M() {
      return true;
   }

   @Override
   public int func_149736_g(World var1, int var2, int var3, int var4, int var5) {
      return Container.func_94526_b(func_149920_e(var1, var2, var3, var4));
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149921_b = var1.func_94245_a("hopper_outside");
      this.field_149923_M = var1.func_94245_a("hopper_top");
      this.field_149924_N = var1.func_94245_a("hopper_inside");
   }

   public static IIcon func_149916_e(String var0) {
      if (var0.equals("hopper_outside")) {
         return Blocks.field_150438_bZ.field_149921_b;
      } else {
         return var0.equals("hopper_inside") ? Blocks.field_150438_bZ.field_149924_N : null;
      }
   }

   @Override
   public String func_149702_O() {
      return "hopper";
   }

   public static TileEntityHopper func_149920_e(IBlockAccess var0, int var1, int var2, int var3) {
      return (TileEntityHopper)var0.func_147438_o(var1, var2, var3);
   }
}
