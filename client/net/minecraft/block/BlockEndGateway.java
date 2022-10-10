package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockEndGateway extends BlockContainer {
   protected BlockEndGateway(Block.Properties var1) {
      super(var1);
   }

   public TileEntity func_196283_a_(IBlockReader var1) {
      return new TileEntityEndGateway();
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public int func_196264_a(IBlockState var1, Random var2) {
      return 0;
   }

   public void func_180655_c(IBlockState var1, World var2, BlockPos var3, Random var4) {
      TileEntity var5 = var2.func_175625_s(var3);
      if (var5 instanceof TileEntityEndGateway) {
         int var6 = ((TileEntityEndGateway)var5).func_195493_h();

         for(int var7 = 0; var7 < var6; ++var7) {
            double var8 = (double)((float)var3.func_177958_n() + var4.nextFloat());
            double var10 = (double)((float)var3.func_177956_o() + var4.nextFloat());
            double var12 = (double)((float)var3.func_177952_p() + var4.nextFloat());
            double var14 = ((double)var4.nextFloat() - 0.5D) * 0.5D;
            double var16 = ((double)var4.nextFloat() - 0.5D) * 0.5D;
            double var18 = ((double)var4.nextFloat() - 0.5D) * 0.5D;
            int var20 = var4.nextInt(2) * 2 - 1;
            if (var4.nextBoolean()) {
               var12 = (double)var3.func_177952_p() + 0.5D + 0.25D * (double)var20;
               var18 = (double)(var4.nextFloat() * 2.0F * (float)var20);
            } else {
               var8 = (double)var3.func_177958_n() + 0.5D + 0.25D * (double)var20;
               var14 = (double)(var4.nextFloat() * 2.0F * (float)var20);
            }

            var2.func_195594_a(Particles.field_197599_J, var8, var10, var12, var14, var16, var18);
         }

      }
   }

   public ItemStack func_185473_a(IBlockReader var1, BlockPos var2, IBlockState var3) {
      return ItemStack.field_190927_a;
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }
}
