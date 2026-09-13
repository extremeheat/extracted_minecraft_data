package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockBrewingStand extends BlockContainer {
   private Random field_149961_a = new Random();
   private IIcon field_149960_b;

   public BlockBrewingStand() {
      super(Material.field_151573_f);
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public int func_149645_b() {
      return 25;
   }

   @Override
   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityBrewingStand();
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }

   @Override
   public void func_149743_a(World var1, int var2, int var3, int var4, AxisAlignedBB var5, List var6, Entity var7) {
      this.func_149676_a(0.4375F, 0.0F, 0.4375F, 0.5625F, 0.875F, 0.5625F);
      super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
      this.func_149683_g();
      super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
   }

   @Override
   public void func_149683_g() {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      if (var1.field_72995_K) {
         return true;
      } else {
         TileEntityBrewingStand var10 = (TileEntityBrewingStand)var1.func_147438_o(var2, var3, var4);
         if (var10 != null) {
            var5.func_146098_a(var10);
         }

         return true;
      }
   }

   @Override
   public void func_149689_a(World var1, int var2, int var3, int var4, EntityLivingBase var5, ItemStack var6) {
      if (var6.func_82837_s()) {
         ((TileEntityBrewingStand)var1.func_147438_o(var2, var3, var4)).func_145937_a(var6.func_82833_r());
      }
   }

   @Override
   public void func_149734_b(World var1, int var2, int var3, int var4, Random var5) {
      double var6 = (double)((float)var2 + 0.4F + var5.nextFloat() * 0.2F);
      double var8 = (double)((float)var3 + 0.7F + var5.nextFloat() * 0.3F);
      double var10 = (double)((float)var4 + 0.4F + var5.nextFloat() * 0.2F);
      var1.func_72869_a("smoke", var6, var8, var10, 0.0, 0.0, 0.0);
   }

   @Override
   public void func_149749_a(World var1, int var2, int var3, int var4, Block var5, int var6) {
      TileEntity var7 = var1.func_147438_o(var2, var3, var4);
      if (var7 instanceof TileEntityBrewingStand) {
         TileEntityBrewingStand var8 = (TileEntityBrewingStand)var7;

         for(int var9 = 0; var9 < var8.func_70302_i_(); ++var9) {
            ItemStack var10 = var8.func_70301_a(var9);
            if (var10 != null) {
               float var11 = this.field_149961_a.nextFloat() * 0.8F + 0.1F;
               float var12 = this.field_149961_a.nextFloat() * 0.8F + 0.1F;
               float var13 = this.field_149961_a.nextFloat() * 0.8F + 0.1F;

               while(var10.field_77994_a > 0) {
                  int var14 = this.field_149961_a.nextInt(21) + 10;
                  if (var14 > var10.field_77994_a) {
                     var14 = var10.field_77994_a;
                  }

                  var10.field_77994_a -= var14;
                  EntityItem var15 = new EntityItem(
                     var1,
                     (double)((float)var2 + var11),
                     (double)((float)var3 + var12),
                     (double)((float)var4 + var13),
                     new ItemStack(var10.func_77973_b(), var14, var10.func_77960_j())
                  );
                  float var16 = 0.05F;
                  var15.field_70159_w = (double)((float)this.field_149961_a.nextGaussian() * var16);
                  var15.field_70181_x = (double)((float)this.field_149961_a.nextGaussian() * var16 + 0.2F);
                  var15.field_70179_y = (double)((float)this.field_149961_a.nextGaussian() * var16);
                  var1.func_72838_d(var15);
               }
            }
         }
      }

      super.func_149749_a(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return Items.field_151067_bt;
   }

   @Override
   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      return Items.field_151067_bt;
   }

   @Override
   public boolean func_149740_M() {
      return true;
   }

   @Override
   public int func_149736_g(World var1, int var2, int var3, int var4, int var5) {
      return Container.func_94526_b((IInventory)var1.func_147438_o(var2, var3, var4));
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      super.func_149651_a(var1);
      this.field_149960_b = var1.func_94245_a(this.func_149641_N() + "_base");
   }

   public IIcon func_149959_e() {
      return this.field_149960_b;
   }
}
