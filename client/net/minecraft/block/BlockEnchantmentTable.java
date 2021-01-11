package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockEnchantmentTable extends BlockContainer {
   protected BlockEnchantmentTable() {
      super(Material.field_151576_e, MapColor.field_151645_D);
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
      this.func_149713_g(0);
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   public boolean func_149686_d() {
      return false;
   }

   public void func_180655_c(World var1, BlockPos var2, IBlockState var3, Random var4) {
      super.func_180655_c(var1, var2, var3, var4);

      for(int var5 = -2; var5 <= 2; ++var5) {
         for(int var6 = -2; var6 <= 2; ++var6) {
            if (var5 > -2 && var5 < 2 && var6 == -1) {
               var6 = 2;
            }

            if (var4.nextInt(16) == 0) {
               for(int var7 = 0; var7 <= 1; ++var7) {
                  BlockPos var8 = var2.func_177982_a(var5, var7, var6);
                  if (var1.func_180495_p(var8).func_177230_c() == Blocks.field_150342_X) {
                     if (!var1.func_175623_d(var2.func_177982_a(var5 / 2, 0, var6 / 2))) {
                        break;
                     }

                     var1.func_175688_a(EnumParticleTypes.ENCHANTMENT_TABLE, (double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 2.0D, (double)var2.func_177952_p() + 0.5D, (double)((float)var5 + var4.nextFloat()) - 0.5D, (double)((float)var7 - var4.nextFloat() - 1.0F), (double)((float)var6 + var4.nextFloat()) - 0.5D);
                  }
               }
            }
         }
      }

   }

   public boolean func_149662_c() {
      return false;
   }

   public int func_149645_b() {
      return 3;
   }

   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityEnchantmentTable();
   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.field_72995_K) {
         return true;
      } else {
         TileEntity var9 = var1.func_175625_s(var2);
         if (var9 instanceof TileEntityEnchantmentTable) {
            var4.func_180468_a((TileEntityEnchantmentTable)var9);
         }

         return true;
      }
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      super.func_180633_a(var1, var2, var3, var4, var5);
      if (var5.func_82837_s()) {
         TileEntity var6 = var1.func_175625_s(var2);
         if (var6 instanceof TileEntityEnchantmentTable) {
            ((TileEntityEnchantmentTable)var6).func_145920_a(var5.func_82833_r());
         }
      }

   }
}
