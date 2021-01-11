package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.HttpUtil;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

public class BlockBeacon extends BlockContainer {
   public BlockBeacon() {
      super(Material.field_151592_s, MapColor.field_151648_G);
      this.func_149711_c(3.0F);
      this.func_149647_a(CreativeTabs.field_78026_f);
   }

   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityBeacon();
   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.field_72995_K) {
         return true;
      } else {
         TileEntity var9 = var1.func_175625_s(var2);
         if (var9 instanceof TileEntityBeacon) {
            var4.func_71007_a((TileEntityBeacon)var9);
            var4.func_71029_a(StatList.field_181730_N);
         }

         return true;
      }
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_149686_d() {
      return false;
   }

   public int func_149645_b() {
      return 3;
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      super.func_180633_a(var1, var2, var3, var4, var5);
      if (var5.func_82837_s()) {
         TileEntity var6 = var1.func_175625_s(var2);
         if (var6 instanceof TileEntityBeacon) {
            ((TileEntityBeacon)var6).func_145999_a(var5.func_82833_r());
         }
      }

   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      TileEntity var5 = var1.func_175625_s(var2);
      if (var5 instanceof TileEntityBeacon) {
         ((TileEntityBeacon)var5).func_174908_m();
         var1.func_175641_c(var2, this, 1, 0);
      }

   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   public static void func_176450_d(final World var0, final BlockPos var1) {
      HttpUtil.field_180193_a.submit(new Runnable() {
         public void run() {
            Chunk var1x = var0.func_175726_f(var1);

            for(int var2 = var1.func_177956_o() - 1; var2 >= 0; --var2) {
               final BlockPos var3 = new BlockPos(var1.func_177958_n(), var2, var1.func_177952_p());
               if (!var1x.func_177444_d(var3)) {
                  break;
               }

               IBlockState var4 = var0.func_180495_p(var3);
               if (var4.func_177230_c() == Blocks.field_150461_bJ) {
                  ((WorldServer)var0).func_152344_a(new Runnable() {
                     public void run() {
                        TileEntity var1x = var0.func_175625_s(var3);
                        if (var1x instanceof TileEntityBeacon) {
                           ((TileEntityBeacon)var1x).func_174908_m();
                           var0.func_175641_c(var3, Blocks.field_150461_bJ, 1, 0);
                        }

                     }
                  });
               }
            }

         }
      });
   }
}
