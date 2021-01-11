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
import net.minecraft.util.StatCollector;

public class BlockPrismarine extends Block {
   public static final PropertyEnum<BlockPrismarine.EnumType> field_176332_a = PropertyEnum.func_177709_a("variant", BlockPrismarine.EnumType.class);
   public static final int field_176331_b;
   public static final int field_176333_M;
   public static final int field_176334_N;

   public BlockPrismarine() {
      super(Material.field_151576_e);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176332_a, BlockPrismarine.EnumType.ROUGH));
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public String func_149732_F() {
      return StatCollector.func_74838_a(this.func_149739_a() + "." + BlockPrismarine.EnumType.ROUGH.func_176809_c() + ".name");
   }

   public MapColor func_180659_g(IBlockState var1) {
      return var1.func_177229_b(field_176332_a) == BlockPrismarine.EnumType.ROUGH ? MapColor.field_151679_y : MapColor.field_151648_G;
   }

   public int func_180651_a(IBlockState var1) {
      return ((BlockPrismarine.EnumType)var1.func_177229_b(field_176332_a)).func_176807_a();
   }

   public int func_176201_c(IBlockState var1) {
      return ((BlockPrismarine.EnumType)var1.func_177229_b(field_176332_a)).func_176807_a();
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176332_a});
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176332_a, BlockPrismarine.EnumType.func_176810_a(var1));
   }

   public void func_149666_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      var3.add(new ItemStack(var1, 1, field_176331_b));
      var3.add(new ItemStack(var1, 1, field_176333_M));
      var3.add(new ItemStack(var1, 1, field_176334_N));
   }

   static {
      field_176331_b = BlockPrismarine.EnumType.ROUGH.func_176807_a();
      field_176333_M = BlockPrismarine.EnumType.BRICKS.func_176807_a();
      field_176334_N = BlockPrismarine.EnumType.DARK.func_176807_a();
   }

   public static enum EnumType implements IStringSerializable {
      ROUGH(0, "prismarine", "rough"),
      BRICKS(1, "prismarine_bricks", "bricks"),
      DARK(2, "dark_prismarine", "dark");

      private static final BlockPrismarine.EnumType[] field_176813_d = new BlockPrismarine.EnumType[values().length];
      private final int field_176814_e;
      private final String field_176811_f;
      private final String field_176812_g;

      private EnumType(int var3, String var4, String var5) {
         this.field_176814_e = var3;
         this.field_176811_f = var4;
         this.field_176812_g = var5;
      }

      public int func_176807_a() {
         return this.field_176814_e;
      }

      public String toString() {
         return this.field_176811_f;
      }

      public static BlockPrismarine.EnumType func_176810_a(int var0) {
         if (var0 < 0 || var0 >= field_176813_d.length) {
            var0 = 0;
         }

         return field_176813_d[var0];
      }

      public String func_176610_l() {
         return this.field_176811_f;
      }

      public String func_176809_c() {
         return this.field_176812_g;
      }

      static {
         BlockPrismarine.EnumType[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            BlockPrismarine.EnumType var3 = var0[var2];
            field_176813_d[var3.func_176807_a()] = var3;
         }

      }
   }
}
