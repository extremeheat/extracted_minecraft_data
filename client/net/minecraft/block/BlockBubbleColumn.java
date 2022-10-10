package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class BlockBubbleColumn extends Block implements IBucketPickupHandler {
   public static final BooleanProperty field_203160_a;

   public BlockBubbleColumn(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_203160_a, true));
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public void func_196262_a(IBlockState var1, World var2, BlockPos var3, Entity var4) {
      IBlockState var5 = var2.func_180495_p(var3.func_177984_a());
      if (var5.func_196958_f()) {
         var4.func_203002_i((Boolean)var1.func_177229_b(field_203160_a));
         if (!var2.field_72995_K) {
            WorldServer var6 = (WorldServer)var2;

            for(int var7 = 0; var7 < 2; ++var7) {
               var6.func_195598_a(Particles.field_197606_Q, (double)((float)var3.func_177958_n() + var2.field_73012_v.nextFloat()), (double)(var3.func_177956_o() + 1), (double)((float)var3.func_177952_p() + var2.field_73012_v.nextFloat()), 1, 0.0D, 0.0D, 0.0D, 1.0D);
               var6.func_195598_a(Particles.field_197612_e, (double)((float)var3.func_177958_n() + var2.field_73012_v.nextFloat()), (double)(var3.func_177956_o() + 1), (double)((float)var3.func_177952_p() + var2.field_73012_v.nextFloat()), 1, 0.0D, 0.01D, 0.0D, 0.2D);
            }
         }
      } else {
         var4.func_203004_j((Boolean)var1.func_177229_b(field_203160_a));
      }

   }

   public void func_196259_b(IBlockState var1, World var2, BlockPos var3, IBlockState var4) {
      func_203159_a(var2, var3.func_177984_a(), func_203157_b(var2, var3.func_177977_b()));
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      func_203159_a(var2, var3.func_177984_a(), func_203157_b(var2, var3));
   }

   public IFluidState func_204507_t(IBlockState var1) {
      return Fluids.field_204546_a.func_207204_a(false);
   }

   public static void func_203159_a(IWorld var0, BlockPos var1, boolean var2) {
      if (func_208072_b(var0, var1)) {
         var0.func_180501_a(var1, (IBlockState)Blocks.field_203203_C.func_176223_P().func_206870_a(field_203160_a, var2), 2);
      }

   }

   public static boolean func_208072_b(IWorld var0, BlockPos var1) {
      IFluidState var2 = var0.func_204610_c(var1);
      return var0.func_180495_p(var1).func_177230_c() == Blocks.field_150355_j && var2.func_206882_g() >= 8 && var2.func_206889_d();
   }

   private static boolean func_203157_b(IBlockReader var0, BlockPos var1) {
      IBlockState var2 = var0.func_180495_p(var1);
      Block var3 = var2.func_177230_c();
      if (var3 == Blocks.field_203203_C) {
         return (Boolean)var2.func_177229_b(field_203160_a);
      } else {
         return var3 != Blocks.field_150425_aM;
      }
   }

   public int func_149738_a(IWorldReaderBase var1) {
      return 5;
   }

   public void func_180655_c(IBlockState var1, World var2, BlockPos var3, Random var4) {
      double var5 = (double)var3.func_177958_n();
      double var7 = (double)var3.func_177956_o();
      double var9 = (double)var3.func_177952_p();
      if ((Boolean)var1.func_177229_b(field_203160_a)) {
         var2.func_195589_b(Particles.field_203218_U, var5 + 0.5D, var7 + 0.8D, var9, 0.0D, 0.0D, 0.0D);
         if (var4.nextInt(200) == 0) {
            var2.func_184134_a(var5, var7, var9, SoundEvents.field_203282_jc, SoundCategory.BLOCKS, 0.2F + var4.nextFloat() * 0.2F, 0.9F + var4.nextFloat() * 0.15F, false);
         }
      } else {
         var2.func_195589_b(Particles.field_203220_f, var5 + 0.5D, var7, var9 + 0.5D, 0.0D, 0.04D, 0.0D);
         var2.func_195589_b(Particles.field_203220_f, var5 + (double)var4.nextFloat(), var7 + (double)var4.nextFloat(), var9 + (double)var4.nextFloat(), 0.0D, 0.04D, 0.0D);
         if (var4.nextInt(200) == 0) {
            var2.func_184134_a(var5, var7, var9, SoundEvents.field_203251_S, SoundCategory.BLOCKS, 0.2F + var4.nextFloat() * 0.2F, 0.9F + var4.nextFloat() * 0.15F, false);
         }
      }

   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      if (!var1.func_196955_c(var4, var5)) {
         return Blocks.field_150355_j.func_176223_P();
      } else {
         if (var2 == EnumFacing.DOWN) {
            var4.func_180501_a(var5, (IBlockState)Blocks.field_203203_C.func_176223_P().func_206870_a(field_203160_a, func_203157_b(var4, var6)), 2);
         } else if (var2 == EnumFacing.UP && var3.func_177230_c() != Blocks.field_203203_C && func_208072_b(var4, var6)) {
            var4.func_205220_G_().func_205360_a(var5, this, this.func_149738_a(var4));
         }

         var4.func_205219_F_().func_205360_a(var5, Fluids.field_204546_a, Fluids.field_204546_a.func_205569_a(var4));
         return super.func_196271_a(var1, var2, var3, var4, var5, var6);
      }
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      Block var4 = var2.func_180495_p(var3.func_177977_b()).func_177230_c();
      return var4 == Blocks.field_203203_C || var4 == Blocks.field_196814_hQ || var4 == Blocks.field_150425_aM;
   }

   public boolean func_149703_v() {
      return false;
   }

   public int func_196264_a(IBlockState var1, Random var2) {
      return 0;
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   public EnumBlockRenderType func_149645_b(IBlockState var1) {
      return EnumBlockRenderType.INVISIBLE;
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_203160_a);
   }

   public Fluid func_204508_a(IWorld var1, BlockPos var2, IBlockState var3) {
      var1.func_180501_a(var2, Blocks.field_150350_a.func_176223_P(), 11);
      return Fluids.field_204546_a;
   }

   static {
      field_203160_a = BlockStateProperties.field_208179_f;
   }
}
