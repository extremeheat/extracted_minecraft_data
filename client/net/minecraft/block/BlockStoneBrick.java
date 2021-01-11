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

public class BlockStoneBrick extends Block {
   public static final PropertyEnum<BlockStoneBrick.EnumType> field_176249_a = PropertyEnum.func_177709_a("variant", BlockStoneBrick.EnumType.class);
   public static final int field_176248_b;
   public static final int field_176250_M;
   public static final int field_176251_N;
   public static final int field_176252_O;

   public BlockStoneBrick() {
      super(Material.field_151576_e);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176249_a, BlockStoneBrick.EnumType.DEFAULT));
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public int func_180651_a(IBlockState var1) {
      return ((BlockStoneBrick.EnumType)var1.func_177229_b(field_176249_a)).func_176612_a();
   }

   public void func_149666_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      BlockStoneBrick.EnumType[] var4 = BlockStoneBrick.EnumType.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         BlockStoneBrick.EnumType var7 = var4[var6];
         var3.add(new ItemStack(var1, 1, var7.func_176612_a()));
      }

   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176249_a, BlockStoneBrick.EnumType.func_176613_a(var1));
   }

   public int func_176201_c(IBlockState var1) {
      return ((BlockStoneBrick.EnumType)var1.func_177229_b(field_176249_a)).func_176612_a();
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176249_a});
   }

   static {
      field_176248_b = BlockStoneBrick.EnumType.DEFAULT.func_176612_a();
      field_176250_M = BlockStoneBrick.EnumType.MOSSY.func_176612_a();
      field_176251_N = BlockStoneBrick.EnumType.CRACKED.func_176612_a();
      field_176252_O = BlockStoneBrick.EnumType.CHISELED.func_176612_a();
   }

   public static enum EnumType implements IStringSerializable {
      DEFAULT(0, "stonebrick", "default"),
      MOSSY(1, "mossy_stonebrick", "mossy"),
      CRACKED(2, "cracked_stonebrick", "cracked"),
      CHISELED(3, "chiseled_stonebrick", "chiseled");

      private static final BlockStoneBrick.EnumType[] field_176618_e = new BlockStoneBrick.EnumType[values().length];
      private final int field_176615_f;
      private final String field_176616_g;
      private final String field_176622_h;

      private EnumType(int var3, String var4, String var5) {
         this.field_176615_f = var3;
         this.field_176616_g = var4;
         this.field_176622_h = var5;
      }

      public int func_176612_a() {
         return this.field_176615_f;
      }

      public String toString() {
         return this.field_176616_g;
      }

      public static BlockStoneBrick.EnumType func_176613_a(int var0) {
         if (var0 < 0 || var0 >= field_176618_e.length) {
            var0 = 0;
         }

         return field_176618_e[var0];
      }

      public String func_176610_l() {
         return this.field_176616_g;
      }

      public String func_176614_c() {
         return this.field_176622_h;
      }

      static {
         BlockStoneBrick.EnumType[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            BlockStoneBrick.EnumType var3 = var0[var2];
            field_176618_e[var3.func_176612_a()] = var3;
         }

      }
   }
}
