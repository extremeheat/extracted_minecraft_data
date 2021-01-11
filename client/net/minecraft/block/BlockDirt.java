package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDirt extends Block {
   public static final PropertyEnum<BlockDirt.DirtType> field_176386_a = PropertyEnum.func_177709_a("variant", BlockDirt.DirtType.class);
   public static final PropertyBool field_176385_b = PropertyBool.func_177716_a("snowy");

   protected BlockDirt() {
      super(Material.field_151578_c);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176386_a, BlockDirt.DirtType.DIRT).func_177226_a(field_176385_b, false));
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public MapColor func_180659_g(IBlockState var1) {
      return ((BlockDirt.DirtType)var1.func_177229_b(field_176386_a)).func_181066_d();
   }

   public IBlockState func_176221_a(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      if (var1.func_177229_b(field_176386_a) == BlockDirt.DirtType.PODZOL) {
         Block var4 = var2.func_180495_p(var3.func_177984_a()).func_177230_c();
         var1 = var1.func_177226_a(field_176385_b, var4 == Blocks.field_150433_aE || var4 == Blocks.field_150431_aC);
      }

      return var1;
   }

   public void func_149666_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      var3.add(new ItemStack(this, 1, BlockDirt.DirtType.DIRT.func_176925_a()));
      var3.add(new ItemStack(this, 1, BlockDirt.DirtType.COARSE_DIRT.func_176925_a()));
      var3.add(new ItemStack(this, 1, BlockDirt.DirtType.PODZOL.func_176925_a()));
   }

   public int func_176222_j(World var1, BlockPos var2) {
      IBlockState var3 = var1.func_180495_p(var2);
      return var3.func_177230_c() != this ? 0 : ((BlockDirt.DirtType)var3.func_177229_b(field_176386_a)).func_176925_a();
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176386_a, BlockDirt.DirtType.func_176924_a(var1));
   }

   public int func_176201_c(IBlockState var1) {
      return ((BlockDirt.DirtType)var1.func_177229_b(field_176386_a)).func_176925_a();
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176386_a, field_176385_b});
   }

   public int func_180651_a(IBlockState var1) {
      BlockDirt.DirtType var2 = (BlockDirt.DirtType)var1.func_177229_b(field_176386_a);
      if (var2 == BlockDirt.DirtType.PODZOL) {
         var2 = BlockDirt.DirtType.DIRT;
      }

      return var2.func_176925_a();
   }

   public static enum DirtType implements IStringSerializable {
      DIRT(0, "dirt", "default", MapColor.field_151664_l),
      COARSE_DIRT(1, "coarse_dirt", "coarse", MapColor.field_151664_l),
      PODZOL(2, "podzol", MapColor.field_151654_J);

      private static final BlockDirt.DirtType[] field_176930_d = new BlockDirt.DirtType[values().length];
      private final int field_176931_e;
      private final String field_176928_f;
      private final String field_176929_g;
      private final MapColor field_181067_h;

      private DirtType(int var3, String var4, MapColor var5) {
         this(var3, var4, var4, var5);
      }

      private DirtType(int var3, String var4, String var5, MapColor var6) {
         this.field_176931_e = var3;
         this.field_176928_f = var4;
         this.field_176929_g = var5;
         this.field_181067_h = var6;
      }

      public int func_176925_a() {
         return this.field_176931_e;
      }

      public String func_176927_c() {
         return this.field_176929_g;
      }

      public MapColor func_181066_d() {
         return this.field_181067_h;
      }

      public String toString() {
         return this.field_176928_f;
      }

      public static BlockDirt.DirtType func_176924_a(int var0) {
         if (var0 < 0 || var0 >= field_176930_d.length) {
            var0 = 0;
         }

         return field_176930_d[var0];
      }

      public String func_176610_l() {
         return this.field_176928_f;
      }

      static {
         BlockDirt.DirtType[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            BlockDirt.DirtType var3 = var0[var2];
            field_176930_d[var3.func_176925_a()] = var3;
         }

      }
   }
}
