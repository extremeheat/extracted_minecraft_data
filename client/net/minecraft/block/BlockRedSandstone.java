package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public class BlockRedSandstone extends Block {
   public static final PropertyEnum<BlockRedSandstone.EnumType> field_176336_a = PropertyEnum.func_177709_a("type", BlockRedSandstone.EnumType.class);

   public BlockRedSandstone() {
      super(Material.field_151576_e, BlockSand.EnumType.RED_SAND.func_176687_c());
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176336_a, BlockRedSandstone.EnumType.DEFAULT));
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public int func_180651_a(IBlockState var1) {
      return ((BlockRedSandstone.EnumType)var1.func_177229_b(field_176336_a)).func_176827_a();
   }

   public void func_149666_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      BlockRedSandstone.EnumType[] var4 = BlockRedSandstone.EnumType.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         BlockRedSandstone.EnumType var7 = var4[var6];
         var3.add(new ItemStack(var1, 1, var7.func_176827_a()));
      }

   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176336_a, BlockRedSandstone.EnumType.func_176825_a(var1));
   }

   public int func_176201_c(IBlockState var1) {
      return ((BlockRedSandstone.EnumType)var1.func_177229_b(field_176336_a)).func_176827_a();
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176336_a});
   }

   public static enum EnumType implements IStringSerializable {
      DEFAULT(0, "red_sandstone", "default"),
      CHISELED(1, "chiseled_red_sandstone", "chiseled"),
      SMOOTH(2, "smooth_red_sandstone", "smooth");

      private static final BlockRedSandstone.EnumType[] field_176831_d = new BlockRedSandstone.EnumType[values().length];
      private final int field_176832_e;
      private final String field_176829_f;
      private final String field_176830_g;

      private EnumType(int var3, String var4, String var5) {
         this.field_176832_e = var3;
         this.field_176829_f = var4;
         this.field_176830_g = var5;
      }

      public int func_176827_a() {
         return this.field_176832_e;
      }

      public String toString() {
         return this.field_176829_f;
      }

      public static BlockRedSandstone.EnumType func_176825_a(int var0) {
         if (var0 < 0 || var0 >= field_176831_d.length) {
            var0 = 0;
         }

         return field_176831_d[var0];
      }

      public String func_176610_l() {
         return this.field_176829_f;
      }

      public String func_176828_c() {
         return this.field_176830_g;
      }

      static {
         BlockRedSandstone.EnumType[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            BlockRedSandstone.EnumType var3 = var0[var2];
            field_176831_d[var3.func_176827_a()] = var3;
         }

      }
   }
}
