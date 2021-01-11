package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemLead;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFence extends Block {
   public static final PropertyBool field_176526_a = PropertyBool.func_177716_a("north");
   public static final PropertyBool field_176525_b = PropertyBool.func_177716_a("east");
   public static final PropertyBool field_176527_M = PropertyBool.func_177716_a("south");
   public static final PropertyBool field_176528_N = PropertyBool.func_177716_a("west");

   public BlockFence(Material var1) {
      this(var1, var1.func_151565_r());
   }

   public BlockFence(Material var1, MapColor var2) {
      super(var1, var2);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176526_a, false).func_177226_a(field_176525_b, false).func_177226_a(field_176527_M, false).func_177226_a(field_176528_N, false));
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   public void func_180638_a(World var1, BlockPos var2, IBlockState var3, AxisAlignedBB var4, List<AxisAlignedBB> var5, Entity var6) {
      boolean var7 = this.func_176524_e(var1, var2.func_177978_c());
      boolean var8 = this.func_176524_e(var1, var2.func_177968_d());
      boolean var9 = this.func_176524_e(var1, var2.func_177976_e());
      boolean var10 = this.func_176524_e(var1, var2.func_177974_f());
      float var11 = 0.375F;
      float var12 = 0.625F;
      float var13 = 0.375F;
      float var14 = 0.625F;
      if (var7) {
         var13 = 0.0F;
      }

      if (var8) {
         var14 = 1.0F;
      }

      if (var7 || var8) {
         this.func_149676_a(var11, 0.0F, var13, var12, 1.5F, var14);
         super.func_180638_a(var1, var2, var3, var4, var5, var6);
      }

      var13 = 0.375F;
      var14 = 0.625F;
      if (var9) {
         var11 = 0.0F;
      }

      if (var10) {
         var12 = 1.0F;
      }

      if (var9 || var10 || !var7 && !var8) {
         this.func_149676_a(var11, 0.0F, var13, var12, 1.5F, var14);
         super.func_180638_a(var1, var2, var3, var4, var5, var6);
      }

      if (var7) {
         var13 = 0.0F;
      }

      if (var8) {
         var14 = 1.0F;
      }

      this.func_149676_a(var11, 0.0F, var13, var12, 1.0F, var14);
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      boolean var3 = this.func_176524_e(var1, var2.func_177978_c());
      boolean var4 = this.func_176524_e(var1, var2.func_177968_d());
      boolean var5 = this.func_176524_e(var1, var2.func_177976_e());
      boolean var6 = this.func_176524_e(var1, var2.func_177974_f());
      float var7 = 0.375F;
      float var8 = 0.625F;
      float var9 = 0.375F;
      float var10 = 0.625F;
      if (var3) {
         var9 = 0.0F;
      }

      if (var4) {
         var10 = 1.0F;
      }

      if (var5) {
         var7 = 0.0F;
      }

      if (var6) {
         var8 = 1.0F;
      }

      this.func_149676_a(var7, 0.0F, var9, var8, 1.0F, var10);
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_149686_d() {
      return false;
   }

   public boolean func_176205_b(IBlockAccess var1, BlockPos var2) {
      return false;
   }

   public boolean func_176524_e(IBlockAccess var1, BlockPos var2) {
      Block var3 = var1.func_180495_p(var2).func_177230_c();
      if (var3 == Blocks.field_180401_cv) {
         return false;
      } else if ((!(var3 instanceof BlockFence) || var3.field_149764_J != this.field_149764_J) && !(var3 instanceof BlockFenceGate)) {
         if (var3.field_149764_J.func_76218_k() && var3.func_149686_d()) {
            return var3.field_149764_J != Material.field_151572_C;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public boolean func_176225_a(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return true;
   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      return var1.field_72995_K ? true : ItemLead.func_180618_a(var4, var1, var2);
   }

   public int func_176201_c(IBlockState var1) {
      return 0;
   }

   public IBlockState func_176221_a(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      return var1.func_177226_a(field_176526_a, this.func_176524_e(var2, var3.func_177978_c())).func_177226_a(field_176525_b, this.func_176524_e(var2, var3.func_177974_f())).func_177226_a(field_176527_M, this.func_176524_e(var2, var3.func_177968_d())).func_177226_a(field_176528_N, this.func_176524_e(var2, var3.func_177976_e()));
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176526_a, field_176525_b, field_176528_N, field_176527_M});
   }
}
