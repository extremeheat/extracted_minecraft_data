package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockHopper extends BlockContainer {
   public static final PropertyDirection field_176430_a = PropertyDirection.func_177712_a("facing", new Predicate<EnumFacing>() {
      public boolean apply(EnumFacing var1) {
         return var1 != EnumFacing.UP;
      }

      // $FF: synthetic method
      public boolean apply(Object var1) {
         return this.apply((EnumFacing)var1);
      }
   });
   public static final PropertyBool field_176429_b = PropertyBool.func_177716_a("enabled");

   public BlockHopper() {
      super(Material.field_151573_f, MapColor.field_151665_m);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176430_a, EnumFacing.DOWN).func_177226_a(field_176429_b, true));
      this.func_149647_a(CreativeTabs.field_78028_d);
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   public void func_180638_a(World var1, BlockPos var2, IBlockState var3, AxisAlignedBB var4, List<AxisAlignedBB> var5, Entity var6) {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
      super.func_180638_a(var1, var2, var3, var4, var5, var6);
      float var7 = 0.125F;
      this.func_149676_a(0.0F, 0.0F, 0.0F, var7, 1.0F, 1.0F);
      super.func_180638_a(var1, var2, var3, var4, var5, var6);
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var7);
      super.func_180638_a(var1, var2, var3, var4, var5, var6);
      this.func_149676_a(1.0F - var7, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      super.func_180638_a(var1, var2, var3, var4, var5, var6);
      this.func_149676_a(0.0F, 0.0F, 1.0F - var7, 1.0F, 1.0F, 1.0F);
      super.func_180638_a(var1, var2, var3, var4, var5, var6);
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      EnumFacing var9 = var3.func_176734_d();
      if (var9 == EnumFacing.UP) {
         var9 = EnumFacing.DOWN;
      }

      return this.func_176223_P().func_177226_a(field_176430_a, var9).func_177226_a(field_176429_b, true);
   }

   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityHopper();
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      super.func_180633_a(var1, var2, var3, var4, var5);
      if (var5.func_82837_s()) {
         TileEntity var6 = var1.func_175625_s(var2);
         if (var6 instanceof TileEntityHopper) {
            ((TileEntityHopper)var6).func_145886_a(var5.func_82833_r());
         }
      }

   }

   public void func_176213_c(World var1, BlockPos var2, IBlockState var3) {
      this.func_176427_e(var1, var2, var3);
   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.field_72995_K) {
         return true;
      } else {
         TileEntity var9 = var1.func_175625_s(var2);
         if (var9 instanceof TileEntityHopper) {
            var4.func_71007_a((TileEntityHopper)var9);
            var4.func_71029_a(StatList.field_181732_P);
         }

         return true;
      }
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      this.func_176427_e(var1, var2, var3);
   }

   private void func_176427_e(World var1, BlockPos var2, IBlockState var3) {
      boolean var4 = !var1.func_175640_z(var2);
      if (var4 != (Boolean)var3.func_177229_b(field_176429_b)) {
         var1.func_180501_a(var2, var3.func_177226_a(field_176429_b, var4), 4);
      }

   }

   public void func_180663_b(World var1, BlockPos var2, IBlockState var3) {
      TileEntity var4 = var1.func_175625_s(var2);
      if (var4 instanceof TileEntityHopper) {
         InventoryHelper.func_180175_a(var1, var2, (TileEntityHopper)var4);
         var1.func_175666_e(var2, this);
      }

      super.func_180663_b(var1, var2, var3);
   }

   public int func_149645_b() {
      return 3;
   }

   public boolean func_149686_d() {
      return false;
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_176225_a(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return true;
   }

   public static EnumFacing func_176428_b(int var0) {
      return EnumFacing.func_82600_a(var0 & 7);
   }

   public static boolean func_149917_c(int var0) {
      return (var0 & 8) != 8;
   }

   public boolean func_149740_M() {
      return true;
   }

   public int func_180641_l(World var1, BlockPos var2) {
      return Container.func_178144_a(var1.func_175625_s(var2));
   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.CUTOUT_MIPPED;
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176430_a, func_176428_b(var1)).func_177226_a(field_176429_b, func_149917_c(var1));
   }

   public int func_176201_c(IBlockState var1) {
      byte var2 = 0;
      int var3 = var2 | ((EnumFacing)var1.func_177229_b(field_176430_a)).func_176745_a();
      if (!(Boolean)var1.func_177229_b(field_176429_b)) {
         var3 |= 8;
      }

      return var3;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176430_a, field_176429_b});
   }
}
