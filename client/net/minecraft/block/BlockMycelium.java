package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMycelium extends Block {
   public static final PropertyBool field_176384_a = PropertyBool.func_177716_a("snowy");

   protected BlockMycelium() {
      super(Material.field_151577_b, MapColor.field_151678_z);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176384_a, false));
      this.func_149675_a(true);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public IBlockState func_176221_a(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      Block var4 = var2.func_180495_p(var3.func_177984_a()).func_177230_c();
      return var1.func_177226_a(field_176384_a, var4 == Blocks.field_150433_aE || var4 == Blocks.field_150431_aC);
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (!var1.field_72995_K) {
         if (var1.func_175671_l(var2.func_177984_a()) < 4 && var1.func_180495_p(var2.func_177984_a()).func_177230_c().func_149717_k() > 2) {
            var1.func_175656_a(var2, Blocks.field_150346_d.func_176223_P().func_177226_a(BlockDirt.field_176386_a, BlockDirt.DirtType.DIRT));
         } else {
            if (var1.func_175671_l(var2.func_177984_a()) >= 9) {
               for(int var5 = 0; var5 < 4; ++var5) {
                  BlockPos var6 = var2.func_177982_a(var4.nextInt(3) - 1, var4.nextInt(5) - 3, var4.nextInt(3) - 1);
                  IBlockState var7 = var1.func_180495_p(var6);
                  Block var8 = var1.func_180495_p(var6.func_177984_a()).func_177230_c();
                  if (var7.func_177230_c() == Blocks.field_150346_d && var7.func_177229_b(BlockDirt.field_176386_a) == BlockDirt.DirtType.DIRT && var1.func_175671_l(var6.func_177984_a()) >= 4 && var8.func_149717_k() <= 2) {
                     var1.func_175656_a(var6, this.func_176223_P());
                  }
               }
            }

         }
      }
   }

   public void func_180655_c(World var1, BlockPos var2, IBlockState var3, Random var4) {
      super.func_180655_c(var1, var2, var3, var4);
      if (var4.nextInt(10) == 0) {
         var1.func_175688_a(EnumParticleTypes.TOWN_AURA, (double)((float)var2.func_177958_n() + var4.nextFloat()), (double)((float)var2.func_177956_o() + 1.1F), (double)((float)var2.func_177952_p() + var4.nextFloat()), 0.0D, 0.0D, 0.0D);
      }

   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Blocks.field_150346_d.func_180660_a(Blocks.field_150346_d.func_176223_P().func_177226_a(BlockDirt.field_176386_a, BlockDirt.DirtType.DIRT), var2, var3);
   }

   public int func_176201_c(IBlockState var1) {
      return 0;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176384_a});
   }
}
