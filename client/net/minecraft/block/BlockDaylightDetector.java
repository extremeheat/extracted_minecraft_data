package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDaylightDetector;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDaylightDetector extends BlockContainer {
   public static final PropertyInteger field_176436_a = PropertyInteger.func_177719_a("power", 0, 15);
   private final boolean field_176435_b;

   public BlockDaylightDetector(boolean var1) {
      super(Material.field_151575_d);
      this.field_176435_b = var1;
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176436_a, 0));
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
      this.func_149647_a(CreativeTabs.field_78028_d);
      this.func_149711_c(0.2F);
      this.func_149672_a(field_149766_f);
      this.func_149663_c("daylightDetector");
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
   }

   public int func_180656_a(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return (Integer)var3.func_177229_b(field_176436_a);
   }

   public void func_180677_d(World var1, BlockPos var2) {
      if (!var1.field_73011_w.func_177495_o()) {
         IBlockState var3 = var1.func_180495_p(var2);
         int var4 = var1.func_175642_b(EnumSkyBlock.SKY, var2) - var1.func_175657_ab();
         float var5 = var1.func_72929_e(1.0F);
         float var6 = var5 < 3.1415927F ? 0.0F : 6.2831855F;
         var5 += (var6 - var5) * 0.2F;
         var4 = Math.round((float)var4 * MathHelper.func_76134_b(var5));
         var4 = MathHelper.func_76125_a(var4, 0, 15);
         if (this.field_176435_b) {
            var4 = 15 - var4;
         }

         if ((Integer)var3.func_177229_b(field_176436_a) != var4) {
            var1.func_180501_a(var2, var3.func_177226_a(field_176436_a, var4), 3);
         }

      }
   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var4.func_175142_cm()) {
         if (var1.field_72995_K) {
            return true;
         } else {
            if (this.field_176435_b) {
               var1.func_180501_a(var2, Blocks.field_150453_bW.func_176223_P().func_177226_a(field_176436_a, var3.func_177229_b(field_176436_a)), 4);
               Blocks.field_150453_bW.func_180677_d(var1, var2);
            } else {
               var1.func_180501_a(var2, Blocks.field_180402_cm.func_176223_P().func_177226_a(field_176436_a, var3.func_177229_b(field_176436_a)), 4);
               Blocks.field_180402_cm.func_180677_d(var1, var2);
            }

            return true;
         }
      } else {
         return super.func_180639_a(var1, var2, var3, var4, var5, var6, var7, var8);
      }
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Item.func_150898_a(Blocks.field_150453_bW);
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return Item.func_150898_a(Blocks.field_150453_bW);
   }

   public boolean func_149686_d() {
      return false;
   }

   public boolean func_149662_c() {
      return false;
   }

   public int func_149645_b() {
      return 3;
   }

   public boolean func_149744_f() {
      return true;
   }

   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityDaylightDetector();
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176436_a, var1);
   }

   public int func_176201_c(IBlockState var1) {
      return (Integer)var1.func_177229_b(field_176436_a);
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176436_a});
   }

   public void func_149666_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      if (!this.field_176435_b) {
         super.func_149666_a(var1, var2, var3);
      }

   }
}
