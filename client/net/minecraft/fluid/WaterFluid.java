package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFlowingFluid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.particles.IParticleData;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public abstract class WaterFluid extends FlowingFluid {
   public WaterFluid() {
      super();
   }

   public Fluid func_210197_e() {
      return Fluids.field_207212_b;
   }

   public Fluid func_210198_f() {
      return Fluids.field_204546_a;
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   public Item func_204524_b() {
      return Items.field_151131_as;
   }

   public void func_204522_a(World var1, BlockPos var2, IFluidState var3, Random var4) {
      if (!var3.func_206889_d() && !(Boolean)var3.func_177229_b(field_207209_a)) {
         if (var4.nextInt(64) == 0) {
            var1.func_184134_a((double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 0.5D, (double)var2.func_177952_p() + 0.5D, SoundEvents.field_187917_gq, SoundCategory.BLOCKS, var4.nextFloat() * 0.25F + 0.75F, var4.nextFloat() + 0.5F, false);
         }
      } else if (var4.nextInt(10) == 0) {
         var1.func_195594_a(Particles.field_197605_P, (double)((float)var2.func_177958_n() + var4.nextFloat()), (double)((float)var2.func_177956_o() + var4.nextFloat()), (double)((float)var2.func_177952_p() + var4.nextFloat()), 0.0D, 0.0D, 0.0D);
      }

   }

   @Nullable
   public IParticleData func_204521_c() {
      return Particles.field_197618_k;
   }

   protected boolean func_205579_d() {
      return true;
   }

   protected void func_205580_a(IWorld var1, BlockPos var2, IBlockState var3) {
      var3.func_196949_c(var1.func_201672_e(), var2, 0);
   }

   public int func_185698_b(IWorldReaderBase var1) {
      return 4;
   }

   public IBlockState func_204527_a(IFluidState var1) {
      return (IBlockState)Blocks.field_150355_j.func_176223_P().func_206870_a(BlockFlowingFluid.field_176367_b, func_207205_e(var1));
   }

   public boolean func_207187_a(Fluid var1) {
      return var1 == Fluids.field_204546_a || var1 == Fluids.field_207212_b;
   }

   public int func_204528_b(IWorldReaderBase var1) {
      return 1;
   }

   public int func_205569_a(IWorldReaderBase var1) {
      return 5;
   }

   public boolean func_211757_a(IFluidState var1, Fluid var2, EnumFacing var3) {
      return var3 == EnumFacing.DOWN && !var2.func_207185_a(FluidTags.field_206959_a);
   }

   protected float func_210195_d() {
      return 100.0F;
   }

   public static class Flowing extends WaterFluid {
      public Flowing() {
         super();
      }

      protected void func_207184_a(StateContainer.Builder<Fluid, IFluidState> var1) {
         super.func_207184_a(var1);
         var1.func_206894_a(field_207210_b);
      }

      public int func_207192_d(IFluidState var1) {
         return (Integer)var1.func_177229_b(field_207210_b);
      }

      public boolean func_207193_c(IFluidState var1) {
         return false;
      }
   }

   public static class Source extends WaterFluid {
      public Source() {
         super();
      }

      public int func_207192_d(IFluidState var1) {
         return 8;
      }

      public boolean func_207193_c(IFluidState var1) {
         return true;
      }
   }
}
