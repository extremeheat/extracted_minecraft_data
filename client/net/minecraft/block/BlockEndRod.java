package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Particles;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockEndRod extends BlockDirectional {
   protected static final VoxelShape field_185630_a = Block.func_208617_a(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D);
   protected static final VoxelShape field_185631_b = Block.func_208617_a(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 16.0D);
   protected static final VoxelShape field_185632_c = Block.func_208617_a(0.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);

   protected BlockEndRod(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176387_N, EnumFacing.UP));
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      return (IBlockState)var1.func_206870_a(field_176387_N, var2.func_185831_a((EnumFacing)var1.func_177229_b(field_176387_N)));
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      return (IBlockState)var1.func_206870_a(field_176387_N, var2.func_185803_b((EnumFacing)var1.func_177229_b(field_176387_N)));
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      switch(((EnumFacing)var1.func_177229_b(field_176387_N)).func_176740_k()) {
      case X:
      default:
         return field_185632_c;
      case Z:
         return field_185631_b;
      case Y:
         return field_185630_a;
      }
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      EnumFacing var2 = var1.func_196000_l();
      IBlockState var3 = var1.func_195991_k().func_180495_p(var1.func_195995_a().func_177972_a(var2.func_176734_d()));
      return var3.func_177230_c() == this && var3.func_177229_b(field_176387_N) == var2 ? (IBlockState)this.func_176223_P().func_206870_a(field_176387_N, var2.func_176734_d()) : (IBlockState)this.func_176223_P().func_206870_a(field_176387_N, var2);
   }

   public void func_180655_c(IBlockState var1, World var2, BlockPos var3, Random var4) {
      EnumFacing var5 = (EnumFacing)var1.func_177229_b(field_176387_N);
      double var6 = (double)var3.func_177958_n() + 0.55D - (double)(var4.nextFloat() * 0.1F);
      double var8 = (double)var3.func_177956_o() + 0.55D - (double)(var4.nextFloat() * 0.1F);
      double var10 = (double)var3.func_177952_p() + 0.55D - (double)(var4.nextFloat() * 0.1F);
      double var12 = (double)(0.4F - (var4.nextFloat() + var4.nextFloat()) * 0.4F);
      if (var4.nextInt(5) == 0) {
         var2.func_195594_a(Particles.field_197624_q, var6 + (double)var5.func_82601_c() * var12, var8 + (double)var5.func_96559_d() * var12, var10 + (double)var5.func_82599_e() * var12, var4.nextGaussian() * 0.005D, var4.nextGaussian() * 0.005D, var4.nextGaussian() * 0.005D);
      }

   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT;
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176387_N);
   }

   public EnumPushReaction func_149656_h(IBlockState var1) {
      return EnumPushReaction.NORMAL;
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }
}
