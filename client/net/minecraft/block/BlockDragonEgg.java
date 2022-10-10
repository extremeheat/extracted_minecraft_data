package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Particles;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockDragonEgg extends BlockFalling {
   protected static final VoxelShape field_196444_a = Block.func_208617_a(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

   public BlockDragonEgg(Block.Properties var1) {
      super(var1);
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196444_a;
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      this.func_196443_d(var1, var2, var3);
      return true;
   }

   public void func_196270_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4) {
      this.func_196443_d(var1, var2, var3);
   }

   private void func_196443_d(IBlockState var1, World var2, BlockPos var3) {
      for(int var4 = 0; var4 < 1000; ++var4) {
         BlockPos var5 = var3.func_177982_a(var2.field_73012_v.nextInt(16) - var2.field_73012_v.nextInt(16), var2.field_73012_v.nextInt(8) - var2.field_73012_v.nextInt(8), var2.field_73012_v.nextInt(16) - var2.field_73012_v.nextInt(16));
         if (var2.func_180495_p(var5).func_196958_f()) {
            if (var2.field_72995_K) {
               for(int var6 = 0; var6 < 128; ++var6) {
                  double var7 = var2.field_73012_v.nextDouble();
                  float var9 = (var2.field_73012_v.nextFloat() - 0.5F) * 0.2F;
                  float var10 = (var2.field_73012_v.nextFloat() - 0.5F) * 0.2F;
                  float var11 = (var2.field_73012_v.nextFloat() - 0.5F) * 0.2F;
                  double var12 = (double)var5.func_177958_n() + (double)(var3.func_177958_n() - var5.func_177958_n()) * var7 + (var2.field_73012_v.nextDouble() - 0.5D) + 0.5D;
                  double var14 = (double)var5.func_177956_o() + (double)(var3.func_177956_o() - var5.func_177956_o()) * var7 + var2.field_73012_v.nextDouble() - 0.5D;
                  double var16 = (double)var5.func_177952_p() + (double)(var3.func_177952_p() - var5.func_177952_p()) * var7 + (var2.field_73012_v.nextDouble() - 0.5D) + 0.5D;
                  var2.func_195594_a(Particles.field_197599_J, var12, var14, var16, (double)var9, (double)var10, (double)var11);
               }
            } else {
               var2.func_180501_a(var5, var1, 2);
               var2.func_175698_g(var3);
            }

            return;
         }
      }

   }

   public int func_149738_a(IWorldReaderBase var1) {
      return 5;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      return false;
   }
}
