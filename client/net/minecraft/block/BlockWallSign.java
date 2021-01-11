package net.minecraft.block;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockWallSign extends BlockSign {
   public static final PropertyDirection field_176412_a;

   public BlockWallSign() {
      super();
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176412_a, EnumFacing.NORTH));
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      EnumFacing var3 = (EnumFacing)var1.func_180495_p(var2).func_177229_b(field_176412_a);
      float var4 = 0.28125F;
      float var5 = 0.78125F;
      float var6 = 0.0F;
      float var7 = 1.0F;
      float var8 = 0.125F;
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      switch(var3) {
      case NORTH:
         this.func_149676_a(var6, var4, 1.0F - var8, var7, var5, 1.0F);
         break;
      case SOUTH:
         this.func_149676_a(var6, var4, 0.0F, var7, var5, var8);
         break;
      case WEST:
         this.func_149676_a(1.0F - var8, var4, var6, 1.0F, var5, var7);
         break;
      case EAST:
         this.func_149676_a(0.0F, var4, var6, var8, var5, var7);
      }

   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      EnumFacing var5 = (EnumFacing)var3.func_177229_b(field_176412_a);
      if (!var1.func_180495_p(var2.func_177972_a(var5.func_176734_d())).func_177230_c().func_149688_o().func_76220_a()) {
         this.func_176226_b(var1, var2, var3, 0);
         var1.func_175698_g(var2);
      }

      super.func_176204_a(var1, var2, var3, var4);
   }

   public IBlockState func_176203_a(int var1) {
      EnumFacing var2 = EnumFacing.func_82600_a(var1);
      if (var2.func_176740_k() == EnumFacing.Axis.Y) {
         var2 = EnumFacing.NORTH;
      }

      return this.func_176223_P().func_177226_a(field_176412_a, var2);
   }

   public int func_176201_c(IBlockState var1) {
      return ((EnumFacing)var1.func_177229_b(field_176412_a)).func_176745_a();
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176412_a});
   }

   static {
      field_176412_a = PropertyDirection.func_177712_a("facing", EnumFacing.Plane.HORIZONTAL);
   }
}
