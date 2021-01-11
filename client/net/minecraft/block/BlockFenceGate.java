package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFenceGate extends BlockDirectional {
   public static final PropertyBool field_176466_a = PropertyBool.func_177716_a("open");
   public static final PropertyBool field_176465_b = PropertyBool.func_177716_a("powered");
   public static final PropertyBool field_176467_M = PropertyBool.func_177716_a("in_wall");

   public BlockFenceGate(BlockPlanks.EnumType var1) {
      super(Material.field_151575_d, var1.func_181070_c());
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176466_a, false).func_177226_a(field_176465_b, false).func_177226_a(field_176467_M, false));
      this.func_149647_a(CreativeTabs.field_78028_d);
   }

   public IBlockState func_176221_a(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      EnumFacing.Axis var4 = ((EnumFacing)var1.func_177229_b(field_176387_N)).func_176740_k();
      if (var4 == EnumFacing.Axis.Z && (var2.func_180495_p(var3.func_177976_e()).func_177230_c() == Blocks.field_150463_bK || var2.func_180495_p(var3.func_177974_f()).func_177230_c() == Blocks.field_150463_bK) || var4 == EnumFacing.Axis.X && (var2.func_180495_p(var3.func_177978_c()).func_177230_c() == Blocks.field_150463_bK || var2.func_180495_p(var3.func_177968_d()).func_177230_c() == Blocks.field_150463_bK)) {
         var1 = var1.func_177226_a(field_176467_M, true);
      }

      return var1;
   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      return var1.func_180495_p(var2.func_177977_b()).func_177230_c().func_149688_o().func_76220_a() ? super.func_176196_c(var1, var2) : false;
   }

   public AxisAlignedBB func_180640_a(World var1, BlockPos var2, IBlockState var3) {
      if ((Boolean)var3.func_177229_b(field_176466_a)) {
         return null;
      } else {
         EnumFacing.Axis var4 = ((EnumFacing)var3.func_177229_b(field_176387_N)).func_176740_k();
         return var4 == EnumFacing.Axis.Z ? new AxisAlignedBB((double)var2.func_177958_n(), (double)var2.func_177956_o(), (double)((float)var2.func_177952_p() + 0.375F), (double)(var2.func_177958_n() + 1), (double)((float)var2.func_177956_o() + 1.5F), (double)((float)var2.func_177952_p() + 0.625F)) : new AxisAlignedBB((double)((float)var2.func_177958_n() + 0.375F), (double)var2.func_177956_o(), (double)var2.func_177952_p(), (double)((float)var2.func_177958_n() + 0.625F), (double)((float)var2.func_177956_o() + 1.5F), (double)(var2.func_177952_p() + 1));
      }
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      EnumFacing.Axis var3 = ((EnumFacing)var1.func_180495_p(var2).func_177229_b(field_176387_N)).func_176740_k();
      if (var3 == EnumFacing.Axis.Z) {
         this.func_149676_a(0.0F, 0.0F, 0.375F, 1.0F, 1.0F, 0.625F);
      } else {
         this.func_149676_a(0.375F, 0.0F, 0.0F, 0.625F, 1.0F, 1.0F);
      }

   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_149686_d() {
      return false;
   }

   public boolean func_176205_b(IBlockAccess var1, BlockPos var2) {
      return (Boolean)var1.func_180495_p(var2).func_177229_b(field_176466_a);
   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.func_176223_P().func_177226_a(field_176387_N, var8.func_174811_aO()).func_177226_a(field_176466_a, false).func_177226_a(field_176465_b, false).func_177226_a(field_176467_M, false);
   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if ((Boolean)var3.func_177229_b(field_176466_a)) {
         var3 = var3.func_177226_a(field_176466_a, false);
         var1.func_180501_a(var2, var3, 2);
      } else {
         EnumFacing var9 = EnumFacing.func_176733_a((double)var4.field_70177_z);
         if (var3.func_177229_b(field_176387_N) == var9.func_176734_d()) {
            var3 = var3.func_177226_a(field_176387_N, var9);
         }

         var3 = var3.func_177226_a(field_176466_a, true);
         var1.func_180501_a(var2, var3, 2);
      }

      var1.func_180498_a(var4, (Boolean)var3.func_177229_b(field_176466_a) ? 1003 : 1006, var2, 0);
      return true;
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!var1.field_72995_K) {
         boolean var5 = var1.func_175640_z(var2);
         if (var5 || var4.func_149744_f()) {
            if (var5 && !(Boolean)var3.func_177229_b(field_176466_a) && !(Boolean)var3.func_177229_b(field_176465_b)) {
               var1.func_180501_a(var2, var3.func_177226_a(field_176466_a, true).func_177226_a(field_176465_b, true), 2);
               var1.func_180498_a((EntityPlayer)null, 1003, var2, 0);
            } else if (!var5 && (Boolean)var3.func_177229_b(field_176466_a) && (Boolean)var3.func_177229_b(field_176465_b)) {
               var1.func_180501_a(var2, var3.func_177226_a(field_176466_a, false).func_177226_a(field_176465_b, false), 2);
               var1.func_180498_a((EntityPlayer)null, 1006, var2, 0);
            } else if (var5 != (Boolean)var3.func_177229_b(field_176465_b)) {
               var1.func_180501_a(var2, var3.func_177226_a(field_176465_b, var5), 2);
            }
         }

      }
   }

   public boolean func_176225_a(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return true;
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176387_N, EnumFacing.func_176731_b(var1)).func_177226_a(field_176466_a, (var1 & 4) != 0).func_177226_a(field_176465_b, (var1 & 8) != 0);
   }

   public int func_176201_c(IBlockState var1) {
      byte var2 = 0;
      int var3 = var2 | ((EnumFacing)var1.func_177229_b(field_176387_N)).func_176736_b();
      if ((Boolean)var1.func_177229_b(field_176465_b)) {
         var3 |= 8;
      }

      if ((Boolean)var1.func_177229_b(field_176466_a)) {
         var3 |= 4;
      }

      return var3;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176387_N, field_176466_a, field_176465_b, field_176467_M});
   }
}
