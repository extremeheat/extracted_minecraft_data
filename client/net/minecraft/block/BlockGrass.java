package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;

public class BlockGrass extends Block implements IGrowable {
   public static final PropertyBool field_176498_a = PropertyBool.func_177716_a("snowy");

   protected BlockGrass() {
      super(Material.field_151577_b);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176498_a, false));
      this.func_149675_a(true);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public IBlockState func_176221_a(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      Block var4 = var2.func_180495_p(var3.func_177984_a()).func_177230_c();
      return var1.func_177226_a(field_176498_a, var4 == Blocks.field_150433_aE || var4 == Blocks.field_150431_aC);
   }

   public int func_149635_D() {
      return ColorizerGrass.func_77480_a(0.5D, 1.0D);
   }

   public int func_180644_h(IBlockState var1) {
      return this.func_149635_D();
   }

   public int func_180662_a(IBlockAccess var1, BlockPos var2, int var3) {
      return BiomeColorHelper.func_180286_a(var1, var2);
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (!var1.field_72995_K) {
         if (var1.func_175671_l(var2.func_177984_a()) < 4 && var1.func_180495_p(var2.func_177984_a()).func_177230_c().func_149717_k() > 2) {
            var1.func_175656_a(var2, Blocks.field_150346_d.func_176223_P());
         } else {
            if (var1.func_175671_l(var2.func_177984_a()) >= 9) {
               for(int var5 = 0; var5 < 4; ++var5) {
                  BlockPos var6 = var2.func_177982_a(var4.nextInt(3) - 1, var4.nextInt(5) - 3, var4.nextInt(3) - 1);
                  Block var7 = var1.func_180495_p(var6.func_177984_a()).func_177230_c();
                  IBlockState var8 = var1.func_180495_p(var6);
                  if (var8.func_177230_c() == Blocks.field_150346_d && var8.func_177229_b(BlockDirt.field_176386_a) == BlockDirt.DirtType.DIRT && var1.func_175671_l(var6.func_177984_a()) >= 4 && var7.func_149717_k() <= 2) {
                     var1.func_175656_a(var6, Blocks.field_150349_c.func_176223_P());
                  }
               }
            }

         }
      }
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Blocks.field_150346_d.func_180660_a(Blocks.field_150346_d.func_176223_P().func_177226_a(BlockDirt.field_176386_a, BlockDirt.DirtType.DIRT), var2, var3);
   }

   public boolean func_176473_a(World var1, BlockPos var2, IBlockState var3, boolean var4) {
      return true;
   }

   public boolean func_180670_a(World var1, Random var2, BlockPos var3, IBlockState var4) {
      return true;
   }

   public void func_176474_b(World var1, Random var2, BlockPos var3, IBlockState var4) {
      BlockPos var5 = var3.func_177984_a();

      label38:
      for(int var6 = 0; var6 < 128; ++var6) {
         BlockPos var7 = var5;

         for(int var8 = 0; var8 < var6 / 16; ++var8) {
            var7 = var7.func_177982_a(var2.nextInt(3) - 1, (var2.nextInt(3) - 1) * var2.nextInt(3) / 2, var2.nextInt(3) - 1);
            if (var1.func_180495_p(var7.func_177977_b()).func_177230_c() != Blocks.field_150349_c || var1.func_180495_p(var7).func_177230_c().func_149721_r()) {
               continue label38;
            }
         }

         if (var1.func_180495_p(var7).func_177230_c().field_149764_J == Material.field_151579_a) {
            if (var2.nextInt(8) == 0) {
               BlockFlower.EnumFlowerType var11 = var1.func_180494_b(var7).func_180623_a(var2, var7);
               BlockFlower var9 = var11.func_176964_a().func_180346_a();
               IBlockState var10 = var9.func_176223_P().func_177226_a(var9.func_176494_l(), var11);
               if (var9.func_180671_f(var1, var7, var10)) {
                  var1.func_180501_a(var7, var10, 3);
               }
            } else {
               IBlockState var12 = Blocks.field_150329_H.func_176223_P().func_177226_a(BlockTallGrass.field_176497_a, BlockTallGrass.EnumType.GRASS);
               if (Blocks.field_150329_H.func_180671_f(var1, var7, var12)) {
                  var1.func_180501_a(var7, var12, 3);
               }
            }
         }
      }

   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.CUTOUT_MIPPED;
   }

   public int func_176201_c(IBlockState var1) {
      return 0;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176498_a});
   }
}
