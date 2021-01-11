package net.minecraft.block;

import java.util.Iterator;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

public abstract class BlockLog extends BlockRotatedPillar {
   public static final PropertyEnum<BlockLog.EnumAxis> field_176299_a = PropertyEnum.func_177709_a("axis", BlockLog.EnumAxis.class);

   public BlockLog() {
      super(Material.field_151575_d);
      this.func_149647_a(CreativeTabs.field_78030_b);
      this.func_149711_c(2.0F);
      this.func_149672_a(field_149766_f);
   }

   public void func_180663_b(World var1, BlockPos var2, IBlockState var3) {
      byte var4 = 4;
      int var5 = var4 + 1;
      if (var1.func_175707_a(var2.func_177982_a(-var5, -var5, -var5), var2.func_177982_a(var5, var5, var5))) {
         Iterator var6 = BlockPos.func_177980_a(var2.func_177982_a(-var4, -var4, -var4), var2.func_177982_a(var4, var4, var4)).iterator();

         while(var6.hasNext()) {
            BlockPos var7 = (BlockPos)var6.next();
            IBlockState var8 = var1.func_180495_p(var7);
            if (var8.func_177230_c().func_149688_o() == Material.field_151584_j && !(Boolean)var8.func_177229_b(BlockLeaves.field_176236_b)) {
               var1.func_180501_a(var7, var8.func_177226_a(BlockLeaves.field_176236_b, true), 4);
            }
         }

      }
   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return super.func_180642_a(var1, var2, var3, var4, var5, var6, var7, var8).func_177226_a(field_176299_a, BlockLog.EnumAxis.func_176870_a(var3.func_176740_k()));
   }

   public static enum EnumAxis implements IStringSerializable {
      X("x"),
      Y("y"),
      Z("z"),
      NONE("none");

      private final String field_176874_e;

      private EnumAxis(String var3) {
         this.field_176874_e = var3;
      }

      public String toString() {
         return this.field_176874_e;
      }

      public static BlockLog.EnumAxis func_176870_a(EnumFacing.Axis var0) {
         switch(var0) {
         case X:
            return X;
         case Y:
            return Y;
         case Z:
            return Z;
         default:
            return NONE;
         }
      }

      public String func_176610_l() {
         return this.field_176874_e;
      }
   }
}
