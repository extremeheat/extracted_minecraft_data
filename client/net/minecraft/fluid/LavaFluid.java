package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFlowingFluid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
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

public abstract class LavaFluid extends FlowingFluid {
   public LavaFluid() {
      super();
   }

   public Fluid func_210197_e() {
      return Fluids.field_207213_d;
   }

   public Fluid func_210198_f() {
      return Fluids.field_204547_b;
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.SOLID;
   }

   public Item func_204524_b() {
      return Items.field_151129_at;
   }

   public void func_204522_a(World var1, BlockPos var2, IFluidState var3, Random var4) {
      BlockPos var5 = var2.func_177984_a();
      if (var1.func_180495_p(var5).func_196958_f() && !var1.func_180495_p(var5).func_200015_d(var1, var5)) {
         if (var4.nextInt(100) == 0) {
            double var6 = (double)((float)var2.func_177958_n() + var4.nextFloat());
            double var8 = (double)(var2.func_177956_o() + 1);
            double var10 = (double)((float)var2.func_177952_p() + var4.nextFloat());
            var1.func_195594_a(Particles.field_197595_F, var6, var8, var10, 0.0D, 0.0D, 0.0D);
            var1.func_184134_a(var6, var8, var10, SoundEvents.field_187662_cZ, SoundCategory.BLOCKS, 0.2F + var4.nextFloat() * 0.2F, 0.9F + var4.nextFloat() * 0.15F, false);
         }

         if (var4.nextInt(200) == 0) {
            var1.func_184134_a((double)var2.func_177958_n(), (double)var2.func_177956_o(), (double)var2.func_177952_p(), SoundEvents.field_187656_cX, SoundCategory.BLOCKS, 0.2F + var4.nextFloat() * 0.2F, 0.9F + var4.nextFloat() * 0.15F, false);
         }
      }

   }

   public void func_207186_b(World var1, BlockPos var2, IFluidState var3, Random var4) {
      if (var1.func_82736_K().func_82766_b("doFireTick")) {
         int var5 = var4.nextInt(3);
         if (var5 > 0) {
            BlockPos var6 = var2;

            for(int var7 = 0; var7 < var5; ++var7) {
               var6 = var6.func_177982_a(var4.nextInt(3) - 1, 1, var4.nextInt(3) - 1);
               if (!var1.func_195588_v(var6)) {
                  return;
               }

               IBlockState var8 = var1.func_180495_p(var6);
               if (var8.func_196958_f()) {
                  if (this.func_176369_e(var1, var6)) {
                     var1.func_175656_a(var6, Blocks.field_150480_ab.func_176223_P());
                     return;
                  }
               } else if (var8.func_185904_a().func_76230_c()) {
                  return;
               }
            }
         } else {
            for(int var9 = 0; var9 < 3; ++var9) {
               BlockPos var10 = var2.func_177982_a(var4.nextInt(3) - 1, 0, var4.nextInt(3) - 1);
               if (!var1.func_195588_v(var10)) {
                  return;
               }

               if (var1.func_175623_d(var10.func_177984_a()) && this.func_176368_m(var1, var10)) {
                  var1.func_175656_a(var10.func_177984_a(), Blocks.field_150480_ab.func_176223_P());
               }
            }
         }

      }
   }

   private boolean func_176369_e(IWorldReaderBase var1, BlockPos var2) {
      EnumFacing[] var3 = EnumFacing.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         EnumFacing var6 = var3[var5];
         if (this.func_176368_m(var1, var2.func_177972_a(var6))) {
            return true;
         }
      }

      return false;
   }

   private boolean func_176368_m(IWorldReaderBase var1, BlockPos var2) {
      return var2.func_177956_o() >= 0 && var2.func_177956_o() < 256 && !var1.func_175667_e(var2) ? false : var1.func_180495_p(var2).func_185904_a().func_76217_h();
   }

   @Nullable
   public IParticleData func_204521_c() {
      return Particles.field_197617_j;
   }

   protected void func_205580_a(IWorld var1, BlockPos var2, IBlockState var3) {
      this.func_205581_a(var1, var2);
   }

   public int func_185698_b(IWorldReaderBase var1) {
      return var1.func_201675_m().func_177500_n() ? 4 : 2;
   }

   public IBlockState func_204527_a(IFluidState var1) {
      return (IBlockState)Blocks.field_150353_l.func_176223_P().func_206870_a(BlockFlowingFluid.field_176367_b, func_207205_e(var1));
   }

   public boolean func_207187_a(Fluid var1) {
      return var1 == Fluids.field_204547_b || var1 == Fluids.field_207213_d;
   }

   public int func_204528_b(IWorldReaderBase var1) {
      return var1.func_201675_m().func_177500_n() ? 1 : 2;
   }

   public boolean func_211757_a(IFluidState var1, Fluid var2, EnumFacing var3) {
      return var1.func_206885_f() >= 0.44444445F && var2.func_207185_a(FluidTags.field_206959_a);
   }

   public int func_205569_a(IWorldReaderBase var1) {
      return var1.func_201675_m().func_177495_o() ? 10 : 30;
   }

   public int func_205578_a(World var1, IFluidState var2, IFluidState var3) {
      int var4 = this.func_205569_a(var1);
      if (!var2.func_206888_e() && !var3.func_206888_e() && !(Boolean)var2.func_177229_b(field_207209_a) && !(Boolean)var3.func_177229_b(field_207209_a) && var3.func_206885_f() > var2.func_206885_f() && var1.func_201674_k().nextInt(4) != 0) {
         var4 *= 4;
      }

      return var4;
   }

   protected void func_205581_a(IWorld var1, BlockPos var2) {
      double var3 = (double)var2.func_177958_n();
      double var5 = (double)var2.func_177956_o();
      double var7 = (double)var2.func_177952_p();
      var1.func_184133_a((EntityPlayer)null, var2, SoundEvents.field_187659_cY, SoundCategory.BLOCKS, 0.5F, 2.6F + (var1.func_201674_k().nextFloat() - var1.func_201674_k().nextFloat()) * 0.8F);

      for(int var9 = 0; var9 < 8; ++var9) {
         var1.func_195594_a(Particles.field_197594_E, var3 + Math.random(), var5 + 1.2D, var7 + Math.random(), 0.0D, 0.0D, 0.0D);
      }

   }

   protected boolean func_205579_d() {
      return false;
   }

   protected void func_205574_a(IWorld var1, BlockPos var2, IBlockState var3, EnumFacing var4, IFluidState var5) {
      if (var4 == EnumFacing.DOWN) {
         IFluidState var6 = var1.func_204610_c(var2);
         if (this.func_207185_a(FluidTags.field_206960_b) && var6.func_206884_a(FluidTags.field_206959_a)) {
            if (var3.func_177230_c() instanceof BlockFlowingFluid) {
               var1.func_180501_a(var2, Blocks.field_150348_b.func_176223_P(), 3);
            }

            this.func_205581_a(var1, var2);
            return;
         }
      }

      super.func_205574_a(var1, var2, var3, var4, var5);
   }

   protected boolean func_207196_h() {
      return true;
   }

   protected float func_210195_d() {
      return 100.0F;
   }

   public static class Flowing extends LavaFluid {
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

   public static class Source extends LavaFluid {
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
