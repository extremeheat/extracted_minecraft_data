package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockEndPortalFrame extends Block {
   public static final PropertyDirection field_176508_a;
   public static final PropertyBool field_176507_b;

   public BlockEndPortalFrame() {
      super(Material.field_151576_e, MapColor.field_151651_C);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176508_a, EnumFacing.NORTH).func_177226_a(field_176507_b, false));
   }

   public boolean func_149662_c() {
      return false;
   }

   public void func_149683_g() {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.8125F, 1.0F);
   }

   public void func_180638_a(World var1, BlockPos var2, IBlockState var3, AxisAlignedBB var4, List<AxisAlignedBB> var5, Entity var6) {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.8125F, 1.0F);
      super.func_180638_a(var1, var2, var3, var4, var5, var6);
      if ((Boolean)var1.func_180495_p(var2).func_177229_b(field_176507_b)) {
         this.func_149676_a(0.3125F, 0.8125F, 0.3125F, 0.6875F, 1.0F, 0.6875F);
         super.func_180638_a(var1, var2, var3, var4, var5, var6);
      }

      this.func_149683_g();
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return null;
   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.func_176223_P().func_177226_a(field_176508_a, var8.func_174811_aO().func_176734_d()).func_177226_a(field_176507_b, false);
   }

   public boolean func_149740_M() {
      return true;
   }

   public int func_180641_l(World var1, BlockPos var2) {
      return (Boolean)var1.func_180495_p(var2).func_177229_b(field_176507_b) ? 15 : 0;
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176507_b, (var1 & 4) != 0).func_177226_a(field_176508_a, EnumFacing.func_176731_b(var1 & 3));
   }

   public int func_176201_c(IBlockState var1) {
      byte var2 = 0;
      int var3 = var2 | ((EnumFacing)var1.func_177229_b(field_176508_a)).func_176736_b();
      if ((Boolean)var1.func_177229_b(field_176507_b)) {
         var3 |= 4;
      }

      return var3;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176508_a, field_176507_b});
   }

   static {
      field_176508_a = PropertyDirection.func_177712_a("facing", EnumFacing.Plane.HORIZONTAL);
      field_176507_b = PropertyBool.func_177716_a("eye");
   }
}
