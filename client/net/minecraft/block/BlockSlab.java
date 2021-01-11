package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockSlab extends Block {
   public static final PropertyEnum<BlockSlab.EnumBlockHalf> field_176554_a = PropertyEnum.func_177709_a("half", BlockSlab.EnumBlockHalf.class);

   public BlockSlab(Material var1) {
      super(var1);
      if (this.func_176552_j()) {
         this.field_149787_q = true;
      } else {
         this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
      }

      this.func_149713_g(255);
   }

   protected boolean func_149700_E() {
      return false;
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      if (this.func_176552_j()) {
         this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      } else {
         IBlockState var3 = var1.func_180495_p(var2);
         if (var3.func_177230_c() == this) {
            if (var3.func_177229_b(field_176554_a) == BlockSlab.EnumBlockHalf.TOP) {
               this.func_149676_a(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
            } else {
               this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
            }
         }

      }
   }

   public void func_149683_g() {
      if (this.func_176552_j()) {
         this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      } else {
         this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
      }

   }

   public void func_180638_a(World var1, BlockPos var2, IBlockState var3, AxisAlignedBB var4, List<AxisAlignedBB> var5, Entity var6) {
      this.func_180654_a(var1, var2);
      super.func_180638_a(var1, var2, var3, var4, var5, var6);
   }

   public boolean func_149662_c() {
      return this.func_176552_j();
   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      IBlockState var9 = super.func_180642_a(var1, var2, var3, var4, var5, var6, var7, var8).func_177226_a(field_176554_a, BlockSlab.EnumBlockHalf.BOTTOM);
      if (this.func_176552_j()) {
         return var9;
      } else {
         return var3 != EnumFacing.DOWN && (var3 == EnumFacing.UP || (double)var5 <= 0.5D) ? var9 : var9.func_177226_a(field_176554_a, BlockSlab.EnumBlockHalf.TOP);
      }
   }

   public int func_149745_a(Random var1) {
      return this.func_176552_j() ? 2 : 1;
   }

   public boolean func_149686_d() {
      return this.func_176552_j();
   }

   public boolean func_176225_a(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      if (this.func_176552_j()) {
         return super.func_176225_a(var1, var2, var3);
      } else if (var3 != EnumFacing.UP && var3 != EnumFacing.DOWN && !super.func_176225_a(var1, var2, var3)) {
         return false;
      } else {
         BlockPos var4 = var2.func_177972_a(var3.func_176734_d());
         IBlockState var5 = var1.func_180495_p(var2);
         IBlockState var6 = var1.func_180495_p(var4);
         boolean var7 = func_150003_a(var5.func_177230_c()) && var5.func_177229_b(field_176554_a) == BlockSlab.EnumBlockHalf.TOP;
         boolean var8 = func_150003_a(var6.func_177230_c()) && var6.func_177229_b(field_176554_a) == BlockSlab.EnumBlockHalf.TOP;
         if (var8) {
            if (var3 == EnumFacing.DOWN) {
               return true;
            } else if (var3 == EnumFacing.UP && super.func_176225_a(var1, var2, var3)) {
               return true;
            } else {
               return !func_150003_a(var5.func_177230_c()) || !var7;
            }
         } else if (var3 == EnumFacing.UP) {
            return true;
         } else if (var3 == EnumFacing.DOWN && super.func_176225_a(var1, var2, var3)) {
            return true;
         } else {
            return !func_150003_a(var5.func_177230_c()) || var7;
         }
      }
   }

   protected static boolean func_150003_a(Block var0) {
      return var0 == Blocks.field_150333_U || var0 == Blocks.field_150376_bx || var0 == Blocks.field_180389_cP;
   }

   public abstract String func_150002_b(int var1);

   public int func_176222_j(World var1, BlockPos var2) {
      return super.func_176222_j(var1, var2) & 7;
   }

   public abstract boolean func_176552_j();

   public abstract IProperty<?> func_176551_l();

   public abstract Object func_176553_a(ItemStack var1);

   public static enum EnumBlockHalf implements IStringSerializable {
      TOP("top"),
      BOTTOM("bottom");

      private final String field_176988_c;

      private EnumBlockHalf(String var3) {
         this.field_176988_c = var3;
      }

      public String toString() {
         return this.field_176988_c;
      }

      public String func_176610_l() {
         return this.field_176988_c;
      }
   }
}
