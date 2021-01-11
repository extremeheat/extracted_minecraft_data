package net.minecraft.block;

import java.util.Iterator;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLadder extends Block {
   public static final PropertyDirection field_176382_a;

   protected BlockLadder() {
      super(Material.field_151594_q);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176382_a, EnumFacing.NORTH));
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   public AxisAlignedBB func_180640_a(World var1, BlockPos var2, IBlockState var3) {
      this.func_180654_a(var1, var2);
      return super.func_180640_a(var1, var2, var3);
   }

   public AxisAlignedBB func_180646_a(World var1, BlockPos var2) {
      this.func_180654_a(var1, var2);
      return super.func_180646_a(var1, var2);
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      IBlockState var3 = var1.func_180495_p(var2);
      if (var3.func_177230_c() == this) {
         float var4 = 0.125F;
         switch((EnumFacing)var3.func_177229_b(field_176382_a)) {
         case NORTH:
            this.func_149676_a(0.0F, 0.0F, 1.0F - var4, 1.0F, 1.0F, 1.0F);
            break;
         case SOUTH:
            this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var4);
            break;
         case WEST:
            this.func_149676_a(1.0F - var4, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            break;
         case EAST:
         default:
            this.func_149676_a(0.0F, 0.0F, 0.0F, var4, 1.0F, 1.0F);
         }

      }
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_149686_d() {
      return false;
   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      if (var1.func_180495_p(var2.func_177976_e()).func_177230_c().func_149721_r()) {
         return true;
      } else if (var1.func_180495_p(var2.func_177974_f()).func_177230_c().func_149721_r()) {
         return true;
      } else if (var1.func_180495_p(var2.func_177978_c()).func_177230_c().func_149721_r()) {
         return true;
      } else {
         return var1.func_180495_p(var2.func_177968_d()).func_177230_c().func_149721_r();
      }
   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      if (var3.func_176740_k().func_176722_c() && this.func_176381_b(var1, var2, var3)) {
         return this.func_176223_P().func_177226_a(field_176382_a, var3);
      } else {
         Iterator var9 = EnumFacing.Plane.HORIZONTAL.iterator();

         EnumFacing var10;
         do {
            if (!var9.hasNext()) {
               return this.func_176223_P();
            }

            var10 = (EnumFacing)var9.next();
         } while(!this.func_176381_b(var1, var2, var10));

         return this.func_176223_P().func_177226_a(field_176382_a, var10);
      }
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      EnumFacing var5 = (EnumFacing)var3.func_177229_b(field_176382_a);
      if (!this.func_176381_b(var1, var2, var5)) {
         this.func_176226_b(var1, var2, var3, 0);
         var1.func_175698_g(var2);
      }

      super.func_176204_a(var1, var2, var3, var4);
   }

   protected boolean func_176381_b(World var1, BlockPos var2, EnumFacing var3) {
      return var1.func_180495_p(var2.func_177972_a(var3.func_176734_d())).func_177230_c().func_149721_r();
   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   public IBlockState func_176203_a(int var1) {
      EnumFacing var2 = EnumFacing.func_82600_a(var1);
      if (var2.func_176740_k() == EnumFacing.Axis.Y) {
         var2 = EnumFacing.NORTH;
      }

      return this.func_176223_P().func_177226_a(field_176382_a, var2);
   }

   public int func_176201_c(IBlockState var1) {
      return ((EnumFacing)var1.func_177229_b(field_176382_a)).func_176745_a();
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176382_a});
   }

   static {
      field_176382_a = PropertyDirection.func_177712_a("facing", EnumFacing.Plane.HORIZONTAL);
   }
}
