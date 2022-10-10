package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

public class BlockBeacon extends BlockContainer {
   public BlockBeacon(Block.Properties var1) {
      super(var1);
   }

   public TileEntity func_196283_a_(IBlockReader var1) {
      return new TileEntityBeacon();
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      if (var2.field_72995_K) {
         return true;
      } else {
         TileEntity var10 = var2.func_175625_s(var3);
         if (var10 instanceof TileEntityBeacon) {
            var4.func_71007_a((TileEntityBeacon)var10);
            var4.func_195066_a(StatList.field_188082_P);
         }

         return true;
      }
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public EnumBlockRenderType func_149645_b(IBlockState var1) {
      return EnumBlockRenderType.MODEL;
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      if (var5.func_82837_s()) {
         TileEntity var6 = var1.func_175625_s(var2);
         if (var6 instanceof TileEntityBeacon) {
            ((TileEntityBeacon)var6).func_200227_a(var5.func_200301_q());
         }
      }

   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT;
   }

   public static void func_176450_d(World var0, BlockPos var1) {
      HttpUtil.field_180193_a.submit(() -> {
         Chunk var2 = var0.func_175726_f(var1);

         for(int var3 = var1.func_177956_o() - 1; var3 >= 0; --var3) {
            BlockPos var4 = new BlockPos(var1.func_177958_n(), var3, var1.func_177952_p());
            if (!var2.func_177444_d(var4)) {
               break;
            }

            IBlockState var5 = var0.func_180495_p(var4);
            if (var5.func_177230_c() == Blocks.field_150461_bJ) {
               ((WorldServer)var0).func_152344_a(() -> {
                  TileEntity var2 = var0.func_175625_s(var4);
                  if (var2 instanceof TileEntityBeacon) {
                     ((TileEntityBeacon)var2).func_174908_m();
                     var0.func_175641_c(var4, Blocks.field_150461_bJ, 1, 0);
                  }

               });
            }
         }

      });
   }
}
