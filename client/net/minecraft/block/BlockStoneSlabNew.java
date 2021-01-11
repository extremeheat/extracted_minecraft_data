package net.minecraft.block;

import java.util.List;
import java.util.Random;
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
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public abstract class BlockStoneSlabNew extends BlockSlab {
   public static final PropertyBool field_176558_b = PropertyBool.func_177716_a("seamless");
   public static final PropertyEnum<BlockStoneSlabNew.EnumType> field_176559_M = PropertyEnum.func_177709_a("variant", BlockStoneSlabNew.EnumType.class);

   public BlockStoneSlabNew() {
      super(Material.field_151576_e);
      IBlockState var1 = this.field_176227_L.func_177621_b();
      if (this.func_176552_j()) {
         var1 = var1.func_177226_a(field_176558_b, false);
      } else {
         var1 = var1.func_177226_a(field_176554_a, BlockSlab.EnumBlockHalf.BOTTOM);
      }

      this.func_180632_j(var1.func_177226_a(field_176559_M, BlockStoneSlabNew.EnumType.RED_SANDSTONE));
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public String func_149732_F() {
      return StatCollector.func_74838_a(this.func_149739_a() + ".red_sandstone.name");
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Item.func_150898_a(Blocks.field_180389_cP);
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return Item.func_150898_a(Blocks.field_180389_cP);
   }

   public String func_150002_b(int var1) {
      return super.func_149739_a() + "." + BlockStoneSlabNew.EnumType.func_176916_a(var1).func_176918_c();
   }

   public IProperty<?> func_176551_l() {
      return field_176559_M;
   }

   public Object func_176553_a(ItemStack var1) {
      return BlockStoneSlabNew.EnumType.func_176916_a(var1.func_77960_j() & 7);
   }

   public void func_149666_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      if (var1 != Item.func_150898_a(Blocks.field_180388_cO)) {
         BlockStoneSlabNew.EnumType[] var4 = BlockStoneSlabNew.EnumType.values();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            BlockStoneSlabNew.EnumType var7 = var4[var6];
            var3.add(new ItemStack(var1, 1, var7.func_176915_a()));
         }

      }
   }

   public IBlockState func_176203_a(int var1) {
      IBlockState var2 = this.func_176223_P().func_177226_a(field_176559_M, BlockStoneSlabNew.EnumType.func_176916_a(var1 & 7));
      if (this.func_176552_j()) {
         var2 = var2.func_177226_a(field_176558_b, (var1 & 8) != 0);
      } else {
         var2 = var2.func_177226_a(field_176554_a, (var1 & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);
      }

      return var2;
   }

   public int func_176201_c(IBlockState var1) {
      byte var2 = 0;
      int var3 = var2 | ((BlockStoneSlabNew.EnumType)var1.func_177229_b(field_176559_M)).func_176915_a();
      if (this.func_176552_j()) {
         if ((Boolean)var1.func_177229_b(field_176558_b)) {
            var3 |= 8;
         }
      } else if (var1.func_177229_b(field_176554_a) == BlockSlab.EnumBlockHalf.TOP) {
         var3 |= 8;
      }

      return var3;
   }

   protected BlockState func_180661_e() {
      return this.func_176552_j() ? new BlockState(this, new IProperty[]{field_176558_b, field_176559_M}) : new BlockState(this, new IProperty[]{field_176554_a, field_176559_M});
   }

   public MapColor func_180659_g(IBlockState var1) {
      return ((BlockStoneSlabNew.EnumType)var1.func_177229_b(field_176559_M)).func_181068_c();
   }

   public int func_180651_a(IBlockState var1) {
      return ((BlockStoneSlabNew.EnumType)var1.func_177229_b(field_176559_M)).func_176915_a();
   }

   public static enum EnumType implements IStringSerializable {
      RED_SANDSTONE(0, "red_sandstone", BlockSand.EnumType.RED_SAND.func_176687_c());

      private static final BlockStoneSlabNew.EnumType[] field_176921_b = new BlockStoneSlabNew.EnumType[values().length];
      private final int field_176922_c;
      private final String field_176919_d;
      private final MapColor field_181069_e;

      private EnumType(int var3, String var4, MapColor var5) {
         this.field_176922_c = var3;
         this.field_176919_d = var4;
         this.field_181069_e = var5;
      }

      public int func_176915_a() {
         return this.field_176922_c;
      }

      public MapColor func_181068_c() {
         return this.field_181069_e;
      }

      public String toString() {
         return this.field_176919_d;
      }

      public static BlockStoneSlabNew.EnumType func_176916_a(int var0) {
         if (var0 < 0 || var0 >= field_176921_b.length) {
            var0 = 0;
         }

         return field_176921_b[var0];
      }

      public String func_176610_l() {
         return this.field_176919_d;
      }

      public String func_176918_c() {
         return this.field_176919_d;
      }

      static {
         BlockStoneSlabNew.EnumType[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            BlockStoneSlabNew.EnumType var3 = var0[var2];
            field_176921_b[var3.func_176915_a()] = var3;
         }

      }
   }
}
