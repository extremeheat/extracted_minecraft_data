package net.minecraft.block;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockRail extends BlockRailBase {
   public static final PropertyEnum<BlockRailBase.EnumRailDirection> field_176565_b = PropertyEnum.func_177709_a("shape", BlockRailBase.EnumRailDirection.class);

   protected BlockRail() {
      super(false);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176565_b, BlockRailBase.EnumRailDirection.NORTH_SOUTH));
   }

   protected void func_176561_b(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (var4.func_149744_f() && (new BlockRailBase.Rail(var1, var2, var3)).func_150650_a() == 3) {
         this.func_176564_a(var1, var2, var3, false);
      }

   }

   public IProperty<BlockRailBase.EnumRailDirection> func_176560_l() {
      return field_176565_b;
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176565_b, BlockRailBase.EnumRailDirection.func_177016_a(var1));
   }

   public int func_176201_c(IBlockState var1) {
      return ((BlockRailBase.EnumRailDirection)var1.func_177229_b(field_176565_b)).func_177015_a();
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176565_b});
   }
}
