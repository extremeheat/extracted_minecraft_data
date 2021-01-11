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
import net.minecraft.world.World;

public abstract class BlockStoneSlab extends BlockSlab {
   public static final PropertyBool field_176555_b = PropertyBool.func_177716_a("seamless");
   public static final PropertyEnum<BlockStoneSlab.EnumType> field_176556_M = PropertyEnum.func_177709_a("variant", BlockStoneSlab.EnumType.class);

   public BlockStoneSlab() {
      super(Material.field_151576_e);
      IBlockState var1 = this.field_176227_L.func_177621_b();
      if (this.func_176552_j()) {
         var1 = var1.func_177226_a(field_176555_b, false);
      } else {
         var1 = var1.func_177226_a(field_176554_a, BlockSlab.EnumBlockHalf.BOTTOM);
      }

      this.func_180632_j(var1.func_177226_a(field_176556_M, BlockStoneSlab.EnumType.STONE));
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Item.func_150898_a(Blocks.field_150333_U);
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return Item.func_150898_a(Blocks.field_150333_U);
   }

   public String func_150002_b(int var1) {
      return super.func_149739_a() + "." + BlockStoneSlab.EnumType.func_176625_a(var1).func_176627_c();
   }

   public IProperty<?> func_176551_l() {
      return field_176556_M;
   }

   public Object func_176553_a(ItemStack var1) {
      return BlockStoneSlab.EnumType.func_176625_a(var1.func_77960_j() & 7);
   }

   public void func_149666_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      if (var1 != Item.func_150898_a(Blocks.field_150334_T)) {
         BlockStoneSlab.EnumType[] var4 = BlockStoneSlab.EnumType.values();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            BlockStoneSlab.EnumType var7 = var4[var6];
            if (var7 != BlockStoneSlab.EnumType.WOOD) {
               var3.add(new ItemStack(var1, 1, var7.func_176624_a()));
            }
         }

      }
   }

   public IBlockState func_176203_a(int var1) {
      IBlockState var2 = this.func_176223_P().func_177226_a(field_176556_M, BlockStoneSlab.EnumType.func_176625_a(var1 & 7));
      if (this.func_176552_j()) {
         var2 = var2.func_177226_a(field_176555_b, (var1 & 8) != 0);
      } else {
         var2 = var2.func_177226_a(field_176554_a, (var1 & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);
      }

      return var2;
   }

   public int func_176201_c(IBlockState var1) {
      byte var2 = 0;
      int var3 = var2 | ((BlockStoneSlab.EnumType)var1.func_177229_b(field_176556_M)).func_176624_a();
      if (this.func_176552_j()) {
         if ((Boolean)var1.func_177229_b(field_176555_b)) {
            var3 |= 8;
         }
      } else if (var1.func_177229_b(field_176554_a) == BlockSlab.EnumBlockHalf.TOP) {
         var3 |= 8;
      }

      return var3;
   }

   protected BlockState func_180661_e() {
      return this.func_176552_j() ? new BlockState(this, new IProperty[]{field_176555_b, field_176556_M}) : new BlockState(this, new IProperty[]{field_176554_a, field_176556_M});
   }

   public int func_180651_a(IBlockState var1) {
      return ((BlockStoneSlab.EnumType)var1.func_177229_b(field_176556_M)).func_176624_a();
   }

   public MapColor func_180659_g(IBlockState var1) {
      return ((BlockStoneSlab.EnumType)var1.func_177229_b(field_176556_M)).func_181074_c();
   }

   public static enum EnumType implements IStringSerializable {
      STONE(0, MapColor.field_151665_m, "stone"),
      SAND(1, MapColor.field_151658_d, "sandstone", "sand"),
      WOOD(2, MapColor.field_151663_o, "wood_old", "wood"),
      COBBLESTONE(3, MapColor.field_151665_m, "cobblestone", "cobble"),
      BRICK(4, MapColor.field_151645_D, "brick"),
      SMOOTHBRICK(5, MapColor.field_151665_m, "stone_brick", "smoothStoneBrick"),
      NETHERBRICK(6, MapColor.field_151655_K, "nether_brick", "netherBrick"),
      QUARTZ(7, MapColor.field_151677_p, "quartz");

      private static final BlockStoneSlab.EnumType[] field_176640_i = new BlockStoneSlab.EnumType[values().length];
      private final int field_176637_j;
      private final MapColor field_181075_k;
      private final String field_176638_k;
      private final String field_176635_l;

      private EnumType(int var3, MapColor var4, String var5) {
         this(var3, var4, var5, var5);
      }

      private EnumType(int var3, MapColor var4, String var5, String var6) {
         this.field_176637_j = var3;
         this.field_181075_k = var4;
         this.field_176638_k = var5;
         this.field_176635_l = var6;
      }

      public int func_176624_a() {
         return this.field_176637_j;
      }

      public MapColor func_181074_c() {
         return this.field_181075_k;
      }

      public String toString() {
         return this.field_176638_k;
      }

      public static BlockStoneSlab.EnumType func_176625_a(int var0) {
         if (var0 < 0 || var0 >= field_176640_i.length) {
            var0 = 0;
         }

         return field_176640_i[var0];
      }

      public String func_176610_l() {
         return this.field_176638_k;
      }

      public String func_176627_c() {
         return this.field_176635_l;
      }

      static {
         BlockStoneSlab.EnumType[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            BlockStoneSlab.EnumType var3 = var0[var2];
            field_176640_i[var3.func_176624_a()] = var3;
         }

      }
   }
}
