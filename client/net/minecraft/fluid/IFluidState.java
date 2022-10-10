package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.particles.IParticleData;
import net.minecraft.state.IStateHolder;
import net.minecraft.tags.Tag;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public interface IFluidState extends IStateHolder<IFluidState> {
   Fluid func_206886_c();

   default boolean func_206889_d() {
      return this.func_206886_c().func_207193_c(this);
   }

   default boolean func_206888_e() {
      return this.func_206886_c().func_204538_c();
   }

   default float func_206885_f() {
      return this.func_206886_c().func_207181_a(this);
   }

   default int func_206882_g() {
      return this.func_206886_c().func_207192_d(this);
   }

   default boolean func_205586_a(IBlockReader var1, BlockPos var2) {
      for(int var3 = -1; var3 <= 1; ++var3) {
         for(int var4 = -1; var4 <= 1; ++var4) {
            BlockPos var5 = var2.func_177982_a(var3, 0, var4);
            IFluidState var6 = var1.func_204610_c(var5);
            if (!var6.func_206886_c().func_207187_a(this.func_206886_c()) && !var1.func_180495_p(var5).func_200015_d(var1, var5)) {
               return true;
            }
         }
      }

      return false;
   }

   default void func_206880_a(World var1, BlockPos var2) {
      this.func_206886_c().func_207191_a(var1, var2, this);
   }

   default void func_206881_a(World var1, BlockPos var2, Random var3) {
      this.func_206886_c().func_204522_a(var1, var2, this, var3);
   }

   default boolean func_206890_h() {
      return this.func_206886_c().func_207196_h();
   }

   default void func_206891_b(World var1, BlockPos var2, Random var3) {
      this.func_206886_c().func_207186_b(var1, var2, this, var3);
   }

   default Vec3d func_206887_a(IWorldReaderBase var1, BlockPos var2) {
      return this.func_206886_c().func_205564_a(var1, var2, this);
   }

   default IBlockState func_206883_i() {
      return this.func_206886_c().func_204527_a(this);
   }

   @Nullable
   default IParticleData func_204521_c() {
      return this.func_206886_c().func_204521_c();
   }

   default BlockRenderLayer func_180664_k() {
      return this.func_206886_c().func_180664_k();
   }

   default boolean func_206884_a(Tag<Fluid> var1) {
      return this.func_206886_c().func_207185_a(var1);
   }

   default float func_210200_l() {
      return this.func_206886_c().func_210195_d();
   }

   default boolean func_211725_a(Fluid var1, EnumFacing var2) {
      return this.func_206886_c().func_211757_a(this, var1, var2);
   }
}
