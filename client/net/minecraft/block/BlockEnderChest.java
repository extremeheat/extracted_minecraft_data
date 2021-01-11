package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockEnderChest extends BlockContainer {
   public static final PropertyDirection field_176437_a;

   protected BlockEnderChest() {
      super(Material.field_151576_e);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176437_a, EnumFacing.NORTH));
      this.func_149647_a(CreativeTabs.field_78031_c);
      this.func_149676_a(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_149686_d() {
      return false;
   }

   public int func_149645_b() {
      return 2;
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Item.func_150898_a(Blocks.field_150343_Z);
   }

   public int func_149745_a(Random var1) {
      return 8;
   }

   protected boolean func_149700_E() {
      return true;
   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.func_176223_P().func_177226_a(field_176437_a, var8.func_174811_aO().func_176734_d());
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      var1.func_180501_a(var2, var3.func_177226_a(field_176437_a, var4.func_174811_aO().func_176734_d()), 2);
   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      InventoryEnderChest var9 = var4.func_71005_bN();
      TileEntity var10 = var1.func_175625_s(var2);
      if (var9 != null && var10 instanceof TileEntityEnderChest) {
         if (var1.func_180495_p(var2.func_177984_a()).func_177230_c().func_149721_r()) {
            return true;
         } else if (var1.field_72995_K) {
            return true;
         } else {
            var9.func_146031_a((TileEntityEnderChest)var10);
            var4.func_71007_a(var9);
            var4.func_71029_a(StatList.field_181738_V);
            return true;
         }
      } else {
         return true;
      }
   }

   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityEnderChest();
   }

   public void func_180655_c(World var1, BlockPos var2, IBlockState var3, Random var4) {
      for(int var5 = 0; var5 < 3; ++var5) {
         int var6 = var4.nextInt(2) * 2 - 1;
         int var7 = var4.nextInt(2) * 2 - 1;
         double var8 = (double)var2.func_177958_n() + 0.5D + 0.25D * (double)var6;
         double var10 = (double)((float)var2.func_177956_o() + var4.nextFloat());
         double var12 = (double)var2.func_177952_p() + 0.5D + 0.25D * (double)var7;
         double var14 = (double)(var4.nextFloat() * (float)var6);
         double var16 = ((double)var4.nextFloat() - 0.5D) * 0.125D;
         double var18 = (double)(var4.nextFloat() * (float)var7);
         var1.func_175688_a(EnumParticleTypes.PORTAL, var8, var10, var12, var14, var16, var18);
      }

   }

   public IBlockState func_176203_a(int var1) {
      EnumFacing var2 = EnumFacing.func_82600_a(var1);
      if (var2.func_176740_k() == EnumFacing.Axis.Y) {
         var2 = EnumFacing.NORTH;
      }

      return this.func_176223_P().func_177226_a(field_176437_a, var2);
   }

   public int func_176201_c(IBlockState var1) {
      return ((EnumFacing)var1.func_177229_b(field_176437_a)).func_176745_a();
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176437_a});
   }

   static {
      field_176437_a = PropertyDirection.func_177712_a("facing", EnumFacing.Plane.HORIZONTAL);
   }
}
