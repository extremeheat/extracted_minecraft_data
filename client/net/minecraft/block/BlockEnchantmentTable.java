package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockEnchantmentTable extends BlockContainer {
   protected static final VoxelShape field_196322_a = Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

   protected BlockEnchantmentTable(Block.Properties var1) {
      super(var1);
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196322_a;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public void func_180655_c(IBlockState var1, World var2, BlockPos var3, Random var4) {
      super.func_180655_c(var1, var2, var3, var4);

      for(int var5 = -2; var5 <= 2; ++var5) {
         for(int var6 = -2; var6 <= 2; ++var6) {
            if (var5 > -2 && var5 < 2 && var6 == -1) {
               var6 = 2;
            }

            if (var4.nextInt(16) == 0) {
               for(int var7 = 0; var7 <= 1; ++var7) {
                  BlockPos var8 = var3.func_177982_a(var5, var7, var6);
                  if (var2.func_180495_p(var8).func_177230_c() == Blocks.field_150342_X) {
                     if (!var2.func_175623_d(var3.func_177982_a(var5 / 2, 0, var6 / 2))) {
                        break;
                     }

                     var2.func_195594_a(Particles.field_197623_p, (double)var3.func_177958_n() + 0.5D, (double)var3.func_177956_o() + 2.0D, (double)var3.func_177952_p() + 0.5D, (double)((float)var5 + var4.nextFloat()) - 0.5D, (double)((float)var7 - var4.nextFloat() - 1.0F), (double)((float)var6 + var4.nextFloat()) - 0.5D);
                  }
               }
            }
         }
      }

   }

   public EnumBlockRenderType func_149645_b(IBlockState var1) {
      return EnumBlockRenderType.MODEL;
   }

   public TileEntity func_196283_a_(IBlockReader var1) {
      return new TileEntityEnchantmentTable();
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      if (var2.field_72995_K) {
         return true;
      } else {
         TileEntity var10 = var2.func_175625_s(var3);
         if (var10 instanceof TileEntityEnchantmentTable) {
            var4.func_180468_a((TileEntityEnchantmentTable)var10);
         }

         return true;
      }
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      if (var5.func_82837_s()) {
         TileEntity var6 = var1.func_175625_s(var2);
         if (var6 instanceof TileEntityEnchantmentTable) {
            ((TileEntityEnchantmentTable)var6).func_200229_a(var5.func_200301_q());
         }
      }

   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return var4 == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }

   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      return false;
   }
}
