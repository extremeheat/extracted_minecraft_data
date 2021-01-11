package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class BlockBrewingStand extends BlockContainer {
   public static final PropertyBool[] field_176451_a = new PropertyBool[]{PropertyBool.func_177716_a("has_bottle_0"), PropertyBool.func_177716_a("has_bottle_1"), PropertyBool.func_177716_a("has_bottle_2")};

   public BlockBrewingStand() {
      super(Material.field_151573_f);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176451_a[0], false).func_177226_a(field_176451_a[1], false).func_177226_a(field_176451_a[2], false));
   }

   public String func_149732_F() {
      return StatCollector.func_74838_a("item.brewingStand.name");
   }

   public boolean func_149662_c() {
      return false;
   }

   public int func_149645_b() {
      return 3;
   }

   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityBrewingStand();
   }

   public boolean func_149686_d() {
      return false;
   }

   public void func_180638_a(World var1, BlockPos var2, IBlockState var3, AxisAlignedBB var4, List<AxisAlignedBB> var5, Entity var6) {
      this.func_149676_a(0.4375F, 0.0F, 0.4375F, 0.5625F, 0.875F, 0.5625F);
      super.func_180638_a(var1, var2, var3, var4, var5, var6);
      this.func_149683_g();
      super.func_180638_a(var1, var2, var3, var4, var5, var6);
   }

   public void func_149683_g() {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.field_72995_K) {
         return true;
      } else {
         TileEntity var9 = var1.func_175625_s(var2);
         if (var9 instanceof TileEntityBrewingStand) {
            var4.func_71007_a((TileEntityBrewingStand)var9);
            var4.func_71029_a(StatList.field_181729_M);
         }

         return true;
      }
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      if (var5.func_82837_s()) {
         TileEntity var6 = var1.func_175625_s(var2);
         if (var6 instanceof TileEntityBrewingStand) {
            ((TileEntityBrewingStand)var6).func_145937_a(var5.func_82833_r());
         }
      }

   }

   public void func_180655_c(World var1, BlockPos var2, IBlockState var3, Random var4) {
      double var5 = (double)((float)var2.func_177958_n() + 0.4F + var4.nextFloat() * 0.2F);
      double var7 = (double)((float)var2.func_177956_o() + 0.7F + var4.nextFloat() * 0.3F);
      double var9 = (double)((float)var2.func_177952_p() + 0.4F + var4.nextFloat() * 0.2F);
      var1.func_175688_a(EnumParticleTypes.SMOKE_NORMAL, var5, var7, var9, 0.0D, 0.0D, 0.0D);
   }

   public void func_180663_b(World var1, BlockPos var2, IBlockState var3) {
      TileEntity var4 = var1.func_175625_s(var2);
      if (var4 instanceof TileEntityBrewingStand) {
         InventoryHelper.func_180175_a(var1, var2, (TileEntityBrewingStand)var4);
      }

      super.func_180663_b(var1, var2, var3);
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Items.field_151067_bt;
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return Items.field_151067_bt;
   }

   public boolean func_149740_M() {
      return true;
   }

   public int func_180641_l(World var1, BlockPos var2) {
      return Container.func_178144_a(var1.func_175625_s(var2));
   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   public IBlockState func_176203_a(int var1) {
      IBlockState var2 = this.func_176223_P();

      for(int var3 = 0; var3 < 3; ++var3) {
         var2 = var2.func_177226_a(field_176451_a[var3], (var1 & 1 << var3) > 0);
      }

      return var2;
   }

   public int func_176201_c(IBlockState var1) {
      int var2 = 0;

      for(int var3 = 0; var3 < 3; ++var3) {
         if ((Boolean)var1.func_177229_b(field_176451_a[var3])) {
            var2 |= 1 << var3;
         }
      }

      return var2;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176451_a[0], field_176451_a[1], field_176451_a[2]});
   }
}
