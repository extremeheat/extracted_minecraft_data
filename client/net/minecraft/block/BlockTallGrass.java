package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTallGrass extends BlockBush implements IGrowable {
   public static final PropertyEnum<BlockTallGrass.EnumType> field_176497_a = PropertyEnum.func_177709_a("type", BlockTallGrass.EnumType.class);

   protected BlockTallGrass() {
      super(Material.field_151582_l);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176497_a, BlockTallGrass.EnumType.DEAD_BUSH));
      float var1 = 0.4F;
      this.func_149676_a(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, 0.8F, 0.5F + var1);
   }

   public int func_149635_D() {
      return ColorizerGrass.func_77480_a(0.5D, 1.0D);
   }

   public boolean func_180671_f(World var1, BlockPos var2, IBlockState var3) {
      return this.func_149854_a(var1.func_180495_p(var2.func_177977_b()).func_177230_c());
   }

   public boolean func_176200_f(World var1, BlockPos var2) {
      return true;
   }

   public int func_180644_h(IBlockState var1) {
      if (var1.func_177230_c() != this) {
         return super.func_180644_h(var1);
      } else {
         BlockTallGrass.EnumType var2 = (BlockTallGrass.EnumType)var1.func_177229_b(field_176497_a);
         return var2 == BlockTallGrass.EnumType.DEAD_BUSH ? 16777215 : ColorizerGrass.func_77480_a(0.5D, 1.0D);
      }
   }

   public int func_180662_a(IBlockAccess var1, BlockPos var2, int var3) {
      return var1.func_180494_b(var2).func_180627_b(var2);
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return var2.nextInt(8) == 0 ? Items.field_151014_N : null;
   }

   public int func_149679_a(int var1, Random var2) {
      return 1 + var2.nextInt(var1 * 2 + 1);
   }

   public void func_180657_a(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, TileEntity var5) {
      if (!var1.field_72995_K && var2.func_71045_bC() != null && var2.func_71045_bC().func_77973_b() == Items.field_151097_aZ) {
         var2.func_71029_a(StatList.field_75934_C[Block.func_149682_b(this)]);
         func_180635_a(var1, var3, new ItemStack(Blocks.field_150329_H, 1, ((BlockTallGrass.EnumType)var4.func_177229_b(field_176497_a)).func_177044_a()));
      } else {
         super.func_180657_a(var1, var2, var3, var4, var5);
      }

   }

   public int func_176222_j(World var1, BlockPos var2) {
      IBlockState var3 = var1.func_180495_p(var2);
      return var3.func_177230_c().func_176201_c(var3);
   }

   public void func_149666_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      for(int var4 = 1; var4 < 3; ++var4) {
         var3.add(new ItemStack(var1, 1, var4));
      }

   }

   public boolean func_176473_a(World var1, BlockPos var2, IBlockState var3, boolean var4) {
      return var3.func_177229_b(field_176497_a) != BlockTallGrass.EnumType.DEAD_BUSH;
   }

   public boolean func_180670_a(World var1, Random var2, BlockPos var3, IBlockState var4) {
      return true;
   }

   public void func_176474_b(World var1, Random var2, BlockPos var3, IBlockState var4) {
      BlockDoublePlant.EnumPlantType var5 = BlockDoublePlant.EnumPlantType.GRASS;
      if (var4.func_177229_b(field_176497_a) == BlockTallGrass.EnumType.FERN) {
         var5 = BlockDoublePlant.EnumPlantType.FERN;
      }

      if (Blocks.field_150398_cm.func_176196_c(var1, var3)) {
         Blocks.field_150398_cm.func_176491_a(var1, var3, var5, 2);
      }

   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176497_a, BlockTallGrass.EnumType.func_177045_a(var1));
   }

   public int func_176201_c(IBlockState var1) {
      return ((BlockTallGrass.EnumType)var1.func_177229_b(field_176497_a)).func_177044_a();
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176497_a});
   }

   public Block.EnumOffsetType func_176218_Q() {
      return Block.EnumOffsetType.XYZ;
   }

   public static enum EnumType implements IStringSerializable {
      DEAD_BUSH(0, "dead_bush"),
      GRASS(1, "tall_grass"),
      FERN(2, "fern");

      private static final BlockTallGrass.EnumType[] field_177048_d = new BlockTallGrass.EnumType[values().length];
      private final int field_177049_e;
      private final String field_177046_f;

      private EnumType(int var3, String var4) {
         this.field_177049_e = var3;
         this.field_177046_f = var4;
      }

      public int func_177044_a() {
         return this.field_177049_e;
      }

      public String toString() {
         return this.field_177046_f;
      }

      public static BlockTallGrass.EnumType func_177045_a(int var0) {
         if (var0 < 0 || var0 >= field_177048_d.length) {
            var0 = 0;
         }

         return field_177048_d[var0];
      }

      public String func_176610_l() {
         return this.field_177046_f;
      }

      static {
         BlockTallGrass.EnumType[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            BlockTallGrass.EnumType var3 = var0[var2];
            field_177048_d[var3.func_177044_a()] = var3;
         }

      }
   }
}
