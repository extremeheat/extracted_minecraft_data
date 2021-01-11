package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;

public class BlockDoublePlant extends BlockBush implements IGrowable {
   public static final PropertyEnum<BlockDoublePlant.EnumPlantType> field_176493_a = PropertyEnum.func_177709_a("variant", BlockDoublePlant.EnumPlantType.class);
   public static final PropertyEnum<BlockDoublePlant.EnumBlockHalf> field_176492_b = PropertyEnum.func_177709_a("half", BlockDoublePlant.EnumBlockHalf.class);
   public static final PropertyEnum<EnumFacing> field_181084_N;

   public BlockDoublePlant() {
      super(Material.field_151582_l);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176493_a, BlockDoublePlant.EnumPlantType.SUNFLOWER).func_177226_a(field_176492_b, BlockDoublePlant.EnumBlockHalf.LOWER).func_177226_a(field_181084_N, EnumFacing.NORTH));
      this.func_149711_c(0.0F);
      this.func_149672_a(field_149779_h);
      this.func_149663_c("doublePlant");
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   public BlockDoublePlant.EnumPlantType func_176490_e(IBlockAccess var1, BlockPos var2) {
      IBlockState var3 = var1.func_180495_p(var2);
      if (var3.func_177230_c() == this) {
         var3 = this.func_176221_a(var3, var1, var2);
         return (BlockDoublePlant.EnumPlantType)var3.func_177229_b(field_176493_a);
      } else {
         return BlockDoublePlant.EnumPlantType.FERN;
      }
   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      return super.func_176196_c(var1, var2) && var1.func_175623_d(var2.func_177984_a());
   }

   public boolean func_176200_f(World var1, BlockPos var2) {
      IBlockState var3 = var1.func_180495_p(var2);
      if (var3.func_177230_c() != this) {
         return true;
      } else {
         BlockDoublePlant.EnumPlantType var4 = (BlockDoublePlant.EnumPlantType)this.func_176221_a(var3, var1, var2).func_177229_b(field_176493_a);
         return var4 == BlockDoublePlant.EnumPlantType.FERN || var4 == BlockDoublePlant.EnumPlantType.GRASS;
      }
   }

   protected void func_176475_e(World var1, BlockPos var2, IBlockState var3) {
      if (!this.func_180671_f(var1, var2, var3)) {
         boolean var4 = var3.func_177229_b(field_176492_b) == BlockDoublePlant.EnumBlockHalf.UPPER;
         BlockPos var5 = var4 ? var2 : var2.func_177984_a();
         BlockPos var6 = var4 ? var2.func_177977_b() : var2;
         Object var7 = var4 ? this : var1.func_180495_p(var5).func_177230_c();
         Object var8 = var4 ? var1.func_180495_p(var6).func_177230_c() : this;
         if (var7 == this) {
            var1.func_180501_a(var5, Blocks.field_150350_a.func_176223_P(), 2);
         }

         if (var8 == this) {
            var1.func_180501_a(var6, Blocks.field_150350_a.func_176223_P(), 3);
            if (!var4) {
               this.func_176226_b(var1, var6, var3, 0);
            }
         }

      }
   }

   public boolean func_180671_f(World var1, BlockPos var2, IBlockState var3) {
      if (var3.func_177229_b(field_176492_b) == BlockDoublePlant.EnumBlockHalf.UPPER) {
         return var1.func_180495_p(var2.func_177977_b()).func_177230_c() == this;
      } else {
         IBlockState var4 = var1.func_180495_p(var2.func_177984_a());
         return var4.func_177230_c() == this && super.func_180671_f(var1, var2, var4);
      }
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      if (var1.func_177229_b(field_176492_b) == BlockDoublePlant.EnumBlockHalf.UPPER) {
         return null;
      } else {
         BlockDoublePlant.EnumPlantType var4 = (BlockDoublePlant.EnumPlantType)var1.func_177229_b(field_176493_a);
         if (var4 == BlockDoublePlant.EnumPlantType.FERN) {
            return null;
         } else if (var4 == BlockDoublePlant.EnumPlantType.GRASS) {
            return var2.nextInt(8) == 0 ? Items.field_151014_N : null;
         } else {
            return Item.func_150898_a(this);
         }
      }
   }

   public int func_180651_a(IBlockState var1) {
      return var1.func_177229_b(field_176492_b) != BlockDoublePlant.EnumBlockHalf.UPPER && var1.func_177229_b(field_176493_a) != BlockDoublePlant.EnumPlantType.GRASS ? ((BlockDoublePlant.EnumPlantType)var1.func_177229_b(field_176493_a)).func_176936_a() : 0;
   }

   public int func_180662_a(IBlockAccess var1, BlockPos var2, int var3) {
      BlockDoublePlant.EnumPlantType var4 = this.func_176490_e(var1, var2);
      return var4 != BlockDoublePlant.EnumPlantType.GRASS && var4 != BlockDoublePlant.EnumPlantType.FERN ? 16777215 : BiomeColorHelper.func_180286_a(var1, var2);
   }

   public void func_176491_a(World var1, BlockPos var2, BlockDoublePlant.EnumPlantType var3, int var4) {
      var1.func_180501_a(var2, this.func_176223_P().func_177226_a(field_176492_b, BlockDoublePlant.EnumBlockHalf.LOWER).func_177226_a(field_176493_a, var3), var4);
      var1.func_180501_a(var2.func_177984_a(), this.func_176223_P().func_177226_a(field_176492_b, BlockDoublePlant.EnumBlockHalf.UPPER), var4);
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      var1.func_180501_a(var2.func_177984_a(), this.func_176223_P().func_177226_a(field_176492_b, BlockDoublePlant.EnumBlockHalf.UPPER), 2);
   }

   public void func_180657_a(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, TileEntity var5) {
      if (var1.field_72995_K || var2.func_71045_bC() == null || var2.func_71045_bC().func_77973_b() != Items.field_151097_aZ || var4.func_177229_b(field_176492_b) != BlockDoublePlant.EnumBlockHalf.LOWER || !this.func_176489_b(var1, var3, var4, var2)) {
         super.func_180657_a(var1, var2, var3, var4, var5);
      }
   }

   public void func_176208_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      if (var3.func_177229_b(field_176492_b) == BlockDoublePlant.EnumBlockHalf.UPPER) {
         if (var1.func_180495_p(var2.func_177977_b()).func_177230_c() == this) {
            if (!var4.field_71075_bZ.field_75098_d) {
               IBlockState var5 = var1.func_180495_p(var2.func_177977_b());
               BlockDoublePlant.EnumPlantType var6 = (BlockDoublePlant.EnumPlantType)var5.func_177229_b(field_176493_a);
               if (var6 != BlockDoublePlant.EnumPlantType.FERN && var6 != BlockDoublePlant.EnumPlantType.GRASS) {
                  var1.func_175655_b(var2.func_177977_b(), true);
               } else if (!var1.field_72995_K) {
                  if (var4.func_71045_bC() != null && var4.func_71045_bC().func_77973_b() == Items.field_151097_aZ) {
                     this.func_176489_b(var1, var2, var5, var4);
                     var1.func_175698_g(var2.func_177977_b());
                  } else {
                     var1.func_175655_b(var2.func_177977_b(), true);
                  }
               } else {
                  var1.func_175698_g(var2.func_177977_b());
               }
            } else {
               var1.func_175698_g(var2.func_177977_b());
            }
         }
      } else if (var4.field_71075_bZ.field_75098_d && var1.func_180495_p(var2.func_177984_a()).func_177230_c() == this) {
         var1.func_180501_a(var2.func_177984_a(), Blocks.field_150350_a.func_176223_P(), 2);
      }

      super.func_176208_a(var1, var2, var3, var4);
   }

   private boolean func_176489_b(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      BlockDoublePlant.EnumPlantType var5 = (BlockDoublePlant.EnumPlantType)var3.func_177229_b(field_176493_a);
      if (var5 != BlockDoublePlant.EnumPlantType.FERN && var5 != BlockDoublePlant.EnumPlantType.GRASS) {
         return false;
      } else {
         var4.func_71029_a(StatList.field_75934_C[Block.func_149682_b(this)]);
         int var6 = (var5 == BlockDoublePlant.EnumPlantType.GRASS ? BlockTallGrass.EnumType.GRASS : BlockTallGrass.EnumType.FERN).func_177044_a();
         func_180635_a(var1, var2, new ItemStack(Blocks.field_150329_H, 2, var6));
         return true;
      }
   }

   public void func_149666_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      BlockDoublePlant.EnumPlantType[] var4 = BlockDoublePlant.EnumPlantType.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         BlockDoublePlant.EnumPlantType var7 = var4[var6];
         var3.add(new ItemStack(var1, 1, var7.func_176936_a()));
      }

   }

   public int func_176222_j(World var1, BlockPos var2) {
      return this.func_176490_e(var1, var2).func_176936_a();
   }

   public boolean func_176473_a(World var1, BlockPos var2, IBlockState var3, boolean var4) {
      BlockDoublePlant.EnumPlantType var5 = this.func_176490_e(var1, var2);
      return var5 != BlockDoublePlant.EnumPlantType.GRASS && var5 != BlockDoublePlant.EnumPlantType.FERN;
   }

   public boolean func_180670_a(World var1, Random var2, BlockPos var3, IBlockState var4) {
      return true;
   }

   public void func_176474_b(World var1, Random var2, BlockPos var3, IBlockState var4) {
      func_180635_a(var1, var3, new ItemStack(this, 1, this.func_176490_e(var1, var3).func_176936_a()));
   }

   public IBlockState func_176203_a(int var1) {
      return (var1 & 8) > 0 ? this.func_176223_P().func_177226_a(field_176492_b, BlockDoublePlant.EnumBlockHalf.UPPER) : this.func_176223_P().func_177226_a(field_176492_b, BlockDoublePlant.EnumBlockHalf.LOWER).func_177226_a(field_176493_a, BlockDoublePlant.EnumPlantType.func_176938_a(var1 & 7));
   }

   public IBlockState func_176221_a(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      if (var1.func_177229_b(field_176492_b) == BlockDoublePlant.EnumBlockHalf.UPPER) {
         IBlockState var4 = var2.func_180495_p(var3.func_177977_b());
         if (var4.func_177230_c() == this) {
            var1 = var1.func_177226_a(field_176493_a, var4.func_177229_b(field_176493_a));
         }
      }

      return var1;
   }

   public int func_176201_c(IBlockState var1) {
      return var1.func_177229_b(field_176492_b) == BlockDoublePlant.EnumBlockHalf.UPPER ? 8 | ((EnumFacing)var1.func_177229_b(field_181084_N)).func_176736_b() : ((BlockDoublePlant.EnumPlantType)var1.func_177229_b(field_176493_a)).func_176936_a();
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176492_b, field_176493_a, field_181084_N});
   }

   public Block.EnumOffsetType func_176218_Q() {
      return Block.EnumOffsetType.XZ;
   }

   static {
      field_181084_N = BlockDirectional.field_176387_N;
   }

   public static enum EnumBlockHalf implements IStringSerializable {
      UPPER,
      LOWER;

      private EnumBlockHalf() {
      }

      public String toString() {
         return this.func_176610_l();
      }

      public String func_176610_l() {
         return this == UPPER ? "upper" : "lower";
      }
   }

   public static enum EnumPlantType implements IStringSerializable {
      SUNFLOWER(0, "sunflower"),
      SYRINGA(1, "syringa"),
      GRASS(2, "double_grass", "grass"),
      FERN(3, "double_fern", "fern"),
      ROSE(4, "double_rose", "rose"),
      PAEONIA(5, "paeonia");

      private static final BlockDoublePlant.EnumPlantType[] field_176941_g = new BlockDoublePlant.EnumPlantType[values().length];
      private final int field_176949_h;
      private final String field_176950_i;
      private final String field_176947_j;

      private EnumPlantType(int var3, String var4) {
         this(var3, var4, var4);
      }

      private EnumPlantType(int var3, String var4, String var5) {
         this.field_176949_h = var3;
         this.field_176950_i = var4;
         this.field_176947_j = var5;
      }

      public int func_176936_a() {
         return this.field_176949_h;
      }

      public String toString() {
         return this.field_176950_i;
      }

      public static BlockDoublePlant.EnumPlantType func_176938_a(int var0) {
         if (var0 < 0 || var0 >= field_176941_g.length) {
            var0 = 0;
         }

         return field_176941_g[var0];
      }

      public String func_176610_l() {
         return this.field_176950_i;
      }

      public String func_176939_c() {
         return this.field_176947_j;
      }

      static {
         BlockDoublePlant.EnumPlantType[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            BlockDoublePlant.EnumPlantType var3 = var0[var2];
            field_176941_g[var3.func_176936_a()] = var3;
         }

      }
   }
}
