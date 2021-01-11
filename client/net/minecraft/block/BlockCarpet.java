package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCarpet extends Block {
   public static final PropertyEnum<EnumDyeColor> field_176330_a = PropertyEnum.func_177709_a("color", EnumDyeColor.class);

   protected BlockCarpet() {
      super(Material.field_151593_r);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176330_a, EnumDyeColor.WHITE));
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
      this.func_149675_a(true);
      this.func_149647_a(CreativeTabs.field_78031_c);
      this.func_150089_b(0);
   }

   public MapColor func_180659_g(IBlockState var1) {
      return ((EnumDyeColor)var1.func_177229_b(field_176330_a)).func_176768_e();
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_149686_d() {
      return false;
   }

   public void func_149683_g() {
      this.func_150089_b(0);
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      this.func_150089_b(0);
   }

   protected void func_150089_b(int var1) {
      byte var2 = 0;
      float var3 = (float)(1 * (1 + var2)) / 16.0F;
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, var3, 1.0F);
   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      return super.func_176196_c(var1, var2) && this.func_176329_d(var1, var2);
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      this.func_176328_e(var1, var2, var3);
   }

   private boolean func_176328_e(World var1, BlockPos var2, IBlockState var3) {
      if (!this.func_176329_d(var1, var2)) {
         this.func_176226_b(var1, var2, var3, 0);
         var1.func_175698_g(var2);
         return false;
      } else {
         return true;
      }
   }

   private boolean func_176329_d(World var1, BlockPos var2) {
      return !var1.func_175623_d(var2.func_177977_b());
   }

   public boolean func_176225_a(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return var3 == EnumFacing.UP ? true : super.func_176225_a(var1, var2, var3);
   }

   public int func_180651_a(IBlockState var1) {
      return ((EnumDyeColor)var1.func_177229_b(field_176330_a)).func_176765_a();
   }

   public void func_149666_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      for(int var4 = 0; var4 < 16; ++var4) {
         var3.add(new ItemStack(var1, 1, var4));
      }

   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176330_a, EnumDyeColor.func_176764_b(var1));
   }

   public int func_176201_c(IBlockState var1) {
      return ((EnumDyeColor)var1.func_177229_b(field_176330_a)).func_176765_a();
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176330_a});
   }
}
