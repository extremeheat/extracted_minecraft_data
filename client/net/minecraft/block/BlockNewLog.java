package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BlockNewLog extends BlockLog {
   public static final PropertyEnum<BlockPlanks.EnumType> field_176300_b = PropertyEnum.func_177708_a("variant", BlockPlanks.EnumType.class, new Predicate<BlockPlanks.EnumType>() {
      public boolean apply(BlockPlanks.EnumType var1) {
         return var1.func_176839_a() >= 4;
      }

      // $FF: synthetic method
      public boolean apply(Object var1) {
         return this.apply((BlockPlanks.EnumType)var1);
      }
   });

   public BlockNewLog() {
      super();
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176300_b, BlockPlanks.EnumType.ACACIA).func_177226_a(field_176299_a, BlockLog.EnumAxis.Y));
   }

   public MapColor func_180659_g(IBlockState var1) {
      BlockPlanks.EnumType var2 = (BlockPlanks.EnumType)var1.func_177229_b(field_176300_b);
      switch((BlockLog.EnumAxis)var1.func_177229_b(field_176299_a)) {
      case X:
      case Z:
      case NONE:
      default:
         switch(var2) {
         case ACACIA:
         default:
            return MapColor.field_151665_m;
         case DARK_OAK:
            return BlockPlanks.EnumType.DARK_OAK.func_181070_c();
         }
      case Y:
         return var2.func_181070_c();
      }
   }

   public void func_149666_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      var3.add(new ItemStack(var1, 1, BlockPlanks.EnumType.ACACIA.func_176839_a() - 4));
      var3.add(new ItemStack(var1, 1, BlockPlanks.EnumType.DARK_OAK.func_176839_a() - 4));
   }

   public IBlockState func_176203_a(int var1) {
      IBlockState var2 = this.func_176223_P().func_177226_a(field_176300_b, BlockPlanks.EnumType.func_176837_a((var1 & 3) + 4));
      switch(var1 & 12) {
      case 0:
         var2 = var2.func_177226_a(field_176299_a, BlockLog.EnumAxis.Y);
         break;
      case 4:
         var2 = var2.func_177226_a(field_176299_a, BlockLog.EnumAxis.X);
         break;
      case 8:
         var2 = var2.func_177226_a(field_176299_a, BlockLog.EnumAxis.Z);
         break;
      default:
         var2 = var2.func_177226_a(field_176299_a, BlockLog.EnumAxis.NONE);
      }

      return var2;
   }

   public int func_176201_c(IBlockState var1) {
      byte var2 = 0;
      int var3 = var2 | ((BlockPlanks.EnumType)var1.func_177229_b(field_176300_b)).func_176839_a() - 4;
      switch((BlockLog.EnumAxis)var1.func_177229_b(field_176299_a)) {
      case X:
         var3 |= 4;
         break;
      case Z:
         var3 |= 8;
         break;
      case NONE:
         var3 |= 12;
      }

      return var3;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176300_b, field_176299_a});
   }

   protected ItemStack func_180643_i(IBlockState var1) {
      return new ItemStack(Item.func_150898_a(this), 1, ((BlockPlanks.EnumType)var1.func_177229_b(field_176300_b)).func_176839_a() - 4);
   }

   public int func_180651_a(IBlockState var1) {
      return ((BlockPlanks.EnumType)var1.func_177229_b(field_176300_b)).func_176839_a() - 4;
   }
}
