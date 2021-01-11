package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public class BlockSandStone extends Block {
   public static final PropertyEnum<BlockSandStone.EnumType> field_176297_a = PropertyEnum.func_177709_a("type", BlockSandStone.EnumType.class);

   public BlockSandStone() {
      super(Material.field_151576_e);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176297_a, BlockSandStone.EnumType.DEFAULT));
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public int func_180651_a(IBlockState var1) {
      return ((BlockSandStone.EnumType)var1.func_177229_b(field_176297_a)).func_176675_a();
   }

   public void func_149666_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      BlockSandStone.EnumType[] var4 = BlockSandStone.EnumType.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         BlockSandStone.EnumType var7 = var4[var6];
         var3.add(new ItemStack(var1, 1, var7.func_176675_a()));
      }

   }

   public MapColor func_180659_g(IBlockState var1) {
      return MapColor.field_151658_d;
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176297_a, BlockSandStone.EnumType.func_176673_a(var1));
   }

   public int func_176201_c(IBlockState var1) {
      return ((BlockSandStone.EnumType)var1.func_177229_b(field_176297_a)).func_176675_a();
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176297_a});
   }

   public static enum EnumType implements IStringSerializable {
      DEFAULT(0, "sandstone", "default"),
      CHISELED(1, "chiseled_sandstone", "chiseled"),
      SMOOTH(2, "smooth_sandstone", "smooth");

      private static final BlockSandStone.EnumType[] field_176679_d = new BlockSandStone.EnumType[values().length];
      private final int field_176680_e;
      private final String field_176677_f;
      private final String field_176678_g;

      private EnumType(int var3, String var4, String var5) {
         this.field_176680_e = var3;
         this.field_176677_f = var4;
         this.field_176678_g = var5;
      }

      public int func_176675_a() {
         return this.field_176680_e;
      }

      public String toString() {
         return this.field_176677_f;
      }

      public static BlockSandStone.EnumType func_176673_a(int var0) {
         if (var0 < 0 || var0 >= field_176679_d.length) {
            var0 = 0;
         }

         return field_176679_d[var0];
      }

      public String func_176610_l() {
         return this.field_176677_f;
      }

      public String func_176676_c() {
         return this.field_176678_g;
      }

      static {
         BlockSandStone.EnumType[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            BlockSandStone.EnumType var3 = var0[var2];
            field_176679_d[var3.func_176675_a()] = var3;
         }

      }
   }
}
