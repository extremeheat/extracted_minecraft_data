package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockPressurePlateWeighted extends BlockBasePressurePlate {
   public static final PropertyInteger field_176579_a = PropertyInteger.func_177719_a("power", 0, 15);
   private final int field_150068_a;

   protected BlockPressurePlateWeighted(Material var1, int var2) {
      this(var1, var2, var1.func_151565_r());
   }

   protected BlockPressurePlateWeighted(Material var1, int var2, MapColor var3) {
      super(var1, var3);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176579_a, 0));
      this.field_150068_a = var2;
   }

   protected int func_180669_e(World var1, BlockPos var2) {
      int var3 = Math.min(var1.func_72872_a(Entity.class, this.func_180667_a(var2)).size(), this.field_150068_a);
      if (var3 > 0) {
         float var4 = (float)Math.min(this.field_150068_a, var3) / (float)this.field_150068_a;
         return MathHelper.func_76123_f(var4 * 15.0F);
      } else {
         return 0;
      }
   }

   protected int func_176576_e(IBlockState var1) {
      return (Integer)var1.func_177229_b(field_176579_a);
   }

   protected IBlockState func_176575_a(IBlockState var1, int var2) {
      return var1.func_177226_a(field_176579_a, var2);
   }

   public int func_149738_a(World var1) {
      return 10;
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176579_a, var1);
   }

   public int func_176201_c(IBlockState var1) {
      return (Integer)var1.func_177229_b(field_176579_a);
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176579_a});
   }
}
