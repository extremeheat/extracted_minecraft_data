package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockFurnace extends BlockContainer {
   public static final PropertyDirection field_176447_a;
   private final boolean field_149932_b;
   private static boolean field_149934_M;

   protected BlockFurnace(boolean var1) {
      super(Material.field_151576_e);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176447_a, EnumFacing.NORTH));
      this.field_149932_b = var1;
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Item.func_150898_a(Blocks.field_150460_al);
   }

   public void func_176213_c(World var1, BlockPos var2, IBlockState var3) {
      this.func_176445_e(var1, var2, var3);
   }

   private void func_176445_e(World var1, BlockPos var2, IBlockState var3) {
      if (!var1.field_72995_K) {
         Block var4 = var1.func_180495_p(var2.func_177978_c()).func_177230_c();
         Block var5 = var1.func_180495_p(var2.func_177968_d()).func_177230_c();
         Block var6 = var1.func_180495_p(var2.func_177976_e()).func_177230_c();
         Block var7 = var1.func_180495_p(var2.func_177974_f()).func_177230_c();
         EnumFacing var8 = (EnumFacing)var3.func_177229_b(field_176447_a);
         if (var8 == EnumFacing.NORTH && var4.func_149730_j() && !var5.func_149730_j()) {
            var8 = EnumFacing.SOUTH;
         } else if (var8 == EnumFacing.SOUTH && var5.func_149730_j() && !var4.func_149730_j()) {
            var8 = EnumFacing.NORTH;
         } else if (var8 == EnumFacing.WEST && var6.func_149730_j() && !var7.func_149730_j()) {
            var8 = EnumFacing.EAST;
         } else if (var8 == EnumFacing.EAST && var7.func_149730_j() && !var6.func_149730_j()) {
            var8 = EnumFacing.WEST;
         }

         var1.func_180501_a(var2, var3.func_177226_a(field_176447_a, var8), 2);
      }
   }

   public void func_180655_c(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (this.field_149932_b) {
         EnumFacing var5 = (EnumFacing)var3.func_177229_b(field_176447_a);
         double var6 = (double)var2.func_177958_n() + 0.5D;
         double var8 = (double)var2.func_177956_o() + var4.nextDouble() * 6.0D / 16.0D;
         double var10 = (double)var2.func_177952_p() + 0.5D;
         double var12 = 0.52D;
         double var14 = var4.nextDouble() * 0.6D - 0.3D;
         switch(var5) {
         case WEST:
            var1.func_175688_a(EnumParticleTypes.SMOKE_NORMAL, var6 - var12, var8, var10 + var14, 0.0D, 0.0D, 0.0D);
            var1.func_175688_a(EnumParticleTypes.FLAME, var6 - var12, var8, var10 + var14, 0.0D, 0.0D, 0.0D);
            break;
         case EAST:
            var1.func_175688_a(EnumParticleTypes.SMOKE_NORMAL, var6 + var12, var8, var10 + var14, 0.0D, 0.0D, 0.0D);
            var1.func_175688_a(EnumParticleTypes.FLAME, var6 + var12, var8, var10 + var14, 0.0D, 0.0D, 0.0D);
            break;
         case NORTH:
            var1.func_175688_a(EnumParticleTypes.SMOKE_NORMAL, var6 + var14, var8, var10 - var12, 0.0D, 0.0D, 0.0D);
            var1.func_175688_a(EnumParticleTypes.FLAME, var6 + var14, var8, var10 - var12, 0.0D, 0.0D, 0.0D);
            break;
         case SOUTH:
            var1.func_175688_a(EnumParticleTypes.SMOKE_NORMAL, var6 + var14, var8, var10 + var12, 0.0D, 0.0D, 0.0D);
            var1.func_175688_a(EnumParticleTypes.FLAME, var6 + var14, var8, var10 + var12, 0.0D, 0.0D, 0.0D);
         }

      }
   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.field_72995_K) {
         return true;
      } else {
         TileEntity var9 = var1.func_175625_s(var2);
         if (var9 instanceof TileEntityFurnace) {
            var4.func_71007_a((TileEntityFurnace)var9);
            var4.func_71029_a(StatList.field_181741_Y);
         }

         return true;
      }
   }

   public static void func_176446_a(boolean var0, World var1, BlockPos var2) {
      IBlockState var3 = var1.func_180495_p(var2);
      TileEntity var4 = var1.func_175625_s(var2);
      field_149934_M = true;
      if (var0) {
         var1.func_180501_a(var2, Blocks.field_150470_am.func_176223_P().func_177226_a(field_176447_a, var3.func_177229_b(field_176447_a)), 3);
         var1.func_180501_a(var2, Blocks.field_150470_am.func_176223_P().func_177226_a(field_176447_a, var3.func_177229_b(field_176447_a)), 3);
      } else {
         var1.func_180501_a(var2, Blocks.field_150460_al.func_176223_P().func_177226_a(field_176447_a, var3.func_177229_b(field_176447_a)), 3);
         var1.func_180501_a(var2, Blocks.field_150460_al.func_176223_P().func_177226_a(field_176447_a, var3.func_177229_b(field_176447_a)), 3);
      }

      field_149934_M = false;
      if (var4 != null) {
         var4.func_145829_t();
         var1.func_175690_a(var2, var4);
      }

   }

   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityFurnace();
   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.func_176223_P().func_177226_a(field_176447_a, var8.func_174811_aO().func_176734_d());
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      var1.func_180501_a(var2, var3.func_177226_a(field_176447_a, var4.func_174811_aO().func_176734_d()), 2);
      if (var5.func_82837_s()) {
         TileEntity var6 = var1.func_175625_s(var2);
         if (var6 instanceof TileEntityFurnace) {
            ((TileEntityFurnace)var6).func_145951_a(var5.func_82833_r());
         }
      }

   }

   public void func_180663_b(World var1, BlockPos var2, IBlockState var3) {
      if (!field_149934_M) {
         TileEntity var4 = var1.func_175625_s(var2);
         if (var4 instanceof TileEntityFurnace) {
            InventoryHelper.func_180175_a(var1, var2, (TileEntityFurnace)var4);
            var1.func_175666_e(var2, this);
         }
      }

      super.func_180663_b(var1, var2, var3);
   }

   public boolean func_149740_M() {
      return true;
   }

   public int func_180641_l(World var1, BlockPos var2) {
      return Container.func_178144_a(var1.func_175625_s(var2));
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return Item.func_150898_a(Blocks.field_150460_al);
   }

   public int func_149645_b() {
      return 3;
   }

   public IBlockState func_176217_b(IBlockState var1) {
      return this.func_176223_P().func_177226_a(field_176447_a, EnumFacing.SOUTH);
   }

   public IBlockState func_176203_a(int var1) {
      EnumFacing var2 = EnumFacing.func_82600_a(var1);
      if (var2.func_176740_k() == EnumFacing.Axis.Y) {
         var2 = EnumFacing.NORTH;
      }

      return this.func_176223_P().func_177226_a(field_176447_a, var2);
   }

   public int func_176201_c(IBlockState var1) {
      return ((EnumFacing)var1.func_177229_b(field_176447_a)).func_176745_a();
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176447_a});
   }

   static {
      field_176447_a = PropertyDirection.func_177712_a("facing", EnumFacing.Plane.HORIZONTAL);
   }
}
