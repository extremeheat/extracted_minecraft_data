package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPane extends Block {
   public static final PropertyBool field_176241_b = PropertyBool.func_177716_a("north");
   public static final PropertyBool field_176242_M = PropertyBool.func_177716_a("east");
   public static final PropertyBool field_176243_N = PropertyBool.func_177716_a("south");
   public static final PropertyBool field_176244_O = PropertyBool.func_177716_a("west");
   private final boolean field_150099_b;

   protected BlockPane(Material var1, boolean var2) {
      super(var1);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176241_b, false).func_177226_a(field_176242_M, false).func_177226_a(field_176243_N, false).func_177226_a(field_176244_O, false));
      this.field_150099_b = var2;
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   public IBlockState func_176221_a(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      return var1.func_177226_a(field_176241_b, this.func_150098_a(var2.func_180495_p(var3.func_177978_c()).func_177230_c())).func_177226_a(field_176243_N, this.func_150098_a(var2.func_180495_p(var3.func_177968_d()).func_177230_c())).func_177226_a(field_176244_O, this.func_150098_a(var2.func_180495_p(var3.func_177976_e()).func_177230_c())).func_177226_a(field_176242_M, this.func_150098_a(var2.func_180495_p(var3.func_177974_f()).func_177230_c()));
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return !this.field_150099_b ? null : super.func_180660_a(var1, var2, var3);
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_149686_d() {
      return false;
   }

   public boolean func_176225_a(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return var1.func_180495_p(var2).func_177230_c() == this ? false : super.func_176225_a(var1, var2, var3);
   }

   public void func_180638_a(World var1, BlockPos var2, IBlockState var3, AxisAlignedBB var4, List<AxisAlignedBB> var5, Entity var6) {
      boolean var7 = this.func_150098_a(var1.func_180495_p(var2.func_177978_c()).func_177230_c());
      boolean var8 = this.func_150098_a(var1.func_180495_p(var2.func_177968_d()).func_177230_c());
      boolean var9 = this.func_150098_a(var1.func_180495_p(var2.func_177976_e()).func_177230_c());
      boolean var10 = this.func_150098_a(var1.func_180495_p(var2.func_177974_f()).func_177230_c());
      if ((!var9 || !var10) && (var9 || var10 || var7 || var8)) {
         if (var9) {
            this.func_149676_a(0.0F, 0.0F, 0.4375F, 0.5F, 1.0F, 0.5625F);
            super.func_180638_a(var1, var2, var3, var4, var5, var6);
         } else if (var10) {
            this.func_149676_a(0.5F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
            super.func_180638_a(var1, var2, var3, var4, var5, var6);
         }
      } else {
         this.func_149676_a(0.0F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
         super.func_180638_a(var1, var2, var3, var4, var5, var6);
      }

      if ((!var7 || !var8) && (var9 || var10 || var7 || var8)) {
         if (var7) {
            this.func_149676_a(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 0.5F);
            super.func_180638_a(var1, var2, var3, var4, var5, var6);
         } else if (var8) {
            this.func_149676_a(0.4375F, 0.0F, 0.5F, 0.5625F, 1.0F, 1.0F);
            super.func_180638_a(var1, var2, var3, var4, var5, var6);
         }
      } else {
         this.func_149676_a(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 1.0F);
         super.func_180638_a(var1, var2, var3, var4, var5, var6);
      }

   }

   public void func_149683_g() {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      float var3 = 0.4375F;
      float var4 = 0.5625F;
      float var5 = 0.4375F;
      float var6 = 0.5625F;
      boolean var7 = this.func_150098_a(var1.func_180495_p(var2.func_177978_c()).func_177230_c());
      boolean var8 = this.func_150098_a(var1.func_180495_p(var2.func_177968_d()).func_177230_c());
      boolean var9 = this.func_150098_a(var1.func_180495_p(var2.func_177976_e()).func_177230_c());
      boolean var10 = this.func_150098_a(var1.func_180495_p(var2.func_177974_f()).func_177230_c());
      if ((!var9 || !var10) && (var9 || var10 || var7 || var8)) {
         if (var9) {
            var3 = 0.0F;
         } else if (var10) {
            var4 = 1.0F;
         }
      } else {
         var3 = 0.0F;
         var4 = 1.0F;
      }

      if ((!var7 || !var8) && (var9 || var10 || var7 || var8)) {
         if (var7) {
            var5 = 0.0F;
         } else if (var8) {
            var6 = 1.0F;
         }
      } else {
         var5 = 0.0F;
         var6 = 1.0F;
      }

      this.func_149676_a(var3, 0.0F, var5, var4, 1.0F, var6);
   }

   public final boolean func_150098_a(Block var1) {
      return var1.func_149730_j() || var1 == this || var1 == Blocks.field_150359_w || var1 == Blocks.field_150399_cn || var1 == Blocks.field_150397_co || var1 instanceof BlockPane;
   }

   protected boolean func_149700_E() {
      return true;
   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.CUTOUT_MIPPED;
   }

   public int func_176201_c(IBlockState var1) {
      return 0;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176241_b, field_176242_M, field_176244_O, field_176243_N});
   }
}
