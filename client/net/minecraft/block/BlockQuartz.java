package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

public class BlockQuartz extends Block {
   public static final PropertyEnum<BlockQuartz.EnumType> field_176335_a = PropertyEnum.func_177709_a("variant", BlockQuartz.EnumType.class);

   public BlockQuartz() {
      super(Material.field_151576_e);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176335_a, BlockQuartz.EnumType.DEFAULT));
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      if (var7 == BlockQuartz.EnumType.LINES_Y.func_176796_a()) {
         switch(var3.func_176740_k()) {
         case Z:
            return this.func_176223_P().func_177226_a(field_176335_a, BlockQuartz.EnumType.LINES_Z);
         case X:
            return this.func_176223_P().func_177226_a(field_176335_a, BlockQuartz.EnumType.LINES_X);
         case Y:
         default:
            return this.func_176223_P().func_177226_a(field_176335_a, BlockQuartz.EnumType.LINES_Y);
         }
      } else {
         return var7 == BlockQuartz.EnumType.CHISELED.func_176796_a() ? this.func_176223_P().func_177226_a(field_176335_a, BlockQuartz.EnumType.CHISELED) : this.func_176223_P().func_177226_a(field_176335_a, BlockQuartz.EnumType.DEFAULT);
      }
   }

   public int func_180651_a(IBlockState var1) {
      BlockQuartz.EnumType var2 = (BlockQuartz.EnumType)var1.func_177229_b(field_176335_a);
      return var2 != BlockQuartz.EnumType.LINES_X && var2 != BlockQuartz.EnumType.LINES_Z ? var2.func_176796_a() : BlockQuartz.EnumType.LINES_Y.func_176796_a();
   }

   protected ItemStack func_180643_i(IBlockState var1) {
      BlockQuartz.EnumType var2 = (BlockQuartz.EnumType)var1.func_177229_b(field_176335_a);
      return var2 != BlockQuartz.EnumType.LINES_X && var2 != BlockQuartz.EnumType.LINES_Z ? super.func_180643_i(var1) : new ItemStack(Item.func_150898_a(this), 1, BlockQuartz.EnumType.LINES_Y.func_176796_a());
   }

   public void func_149666_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      var3.add(new ItemStack(var1, 1, BlockQuartz.EnumType.DEFAULT.func_176796_a()));
      var3.add(new ItemStack(var1, 1, BlockQuartz.EnumType.CHISELED.func_176796_a()));
      var3.add(new ItemStack(var1, 1, BlockQuartz.EnumType.LINES_Y.func_176796_a()));
   }

   public MapColor func_180659_g(IBlockState var1) {
      return MapColor.field_151677_p;
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176335_a, BlockQuartz.EnumType.func_176794_a(var1));
   }

   public int func_176201_c(IBlockState var1) {
      return ((BlockQuartz.EnumType)var1.func_177229_b(field_176335_a)).func_176796_a();
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176335_a});
   }

   public static enum EnumType implements IStringSerializable {
      DEFAULT(0, "default", "default"),
      CHISELED(1, "chiseled", "chiseled"),
      LINES_Y(2, "lines_y", "lines"),
      LINES_X(3, "lines_x", "lines"),
      LINES_Z(4, "lines_z", "lines");

      private static final BlockQuartz.EnumType[] field_176797_f = new BlockQuartz.EnumType[values().length];
      private final int field_176798_g;
      private final String field_176805_h;
      private final String field_176806_i;

      private EnumType(int var3, String var4, String var5) {
         this.field_176798_g = var3;
         this.field_176805_h = var4;
         this.field_176806_i = var5;
      }

      public int func_176796_a() {
         return this.field_176798_g;
      }

      public String toString() {
         return this.field_176806_i;
      }

      public static BlockQuartz.EnumType func_176794_a(int var0) {
         if (var0 < 0 || var0 >= field_176797_f.length) {
            var0 = 0;
         }

         return field_176797_f[var0];
      }

      public String func_176610_l() {
         return this.field_176805_h;
      }

      static {
         BlockQuartz.EnumType[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            BlockQuartz.EnumType var3 = var0[var2];
            field_176797_f[var3.func_176796_a()] = var3;
         }

      }
   }
}
