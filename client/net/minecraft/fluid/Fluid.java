package net.minecraft.fluid;

import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.particles.IParticleData;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.Tag;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public abstract class Fluid {
   public static final ObjectIntIdentityMap<IFluidState> field_207201_d = new ObjectIntIdentityMap();
   protected final StateContainer<Fluid, IFluidState> field_207202_e;
   private IFluidState field_207200_b;

   protected Fluid() {
      super();
      StateContainer.Builder var1 = new StateContainer.Builder(this);
      this.func_207184_a(var1);
      this.field_207202_e = var1.func_206893_a(FluidState::new);
      this.func_207183_f((IFluidState)this.field_207202_e.func_177621_b());
   }

   protected void func_207184_a(StateContainer.Builder<Fluid, IFluidState> var1) {
   }

   public StateContainer<Fluid, IFluidState> func_207182_e() {
      return this.field_207202_e;
   }

   protected final void func_207183_f(IFluidState var1) {
      this.field_207200_b = var1;
   }

   public final IFluidState func_207188_f() {
      return this.field_207200_b;
   }

   protected abstract BlockRenderLayer func_180664_k();

   public abstract Item func_204524_b();

   protected void func_204522_a(World var1, BlockPos var2, IFluidState var3, Random var4) {
   }

   protected void func_207191_a(World var1, BlockPos var2, IFluidState var3) {
   }

   protected void func_207186_b(World var1, BlockPos var2, IFluidState var3, Random var4) {
   }

   @Nullable
   protected IParticleData func_204521_c() {
      return null;
   }

   protected abstract boolean func_211757_a(IFluidState var1, Fluid var2, EnumFacing var3);

   protected abstract Vec3d func_205564_a(IWorldReaderBase var1, BlockPos var2, IFluidState var3);

   public abstract int func_205569_a(IWorldReaderBase var1);

   protected boolean func_207196_h() {
      return false;
   }

   protected boolean func_204538_c() {
      return false;
   }

   protected abstract float func_210195_d();

   public abstract float func_207181_a(IFluidState var1);

   protected abstract IBlockState func_204527_a(IFluidState var1);

   public abstract boolean func_207193_c(IFluidState var1);

   public abstract int func_207192_d(IFluidState var1);

   public boolean func_207187_a(Fluid var1) {
      return var1 == this;
   }

   public boolean func_207185_a(Tag<Fluid> var1) {
      return var1.func_199685_a_(this);
   }

   public static void func_207195_i() {
      func_207194_a(IRegistry.field_212619_h.func_212609_b(), new EmptyFluid());
      func_207198_a("flowing_water", new WaterFluid.Flowing());
      func_207198_a("water", new WaterFluid.Source());
      func_207198_a("flowing_lava", new LavaFluid.Flowing());
      func_207198_a("lava", new LavaFluid.Source());
      Iterator var0 = IRegistry.field_212619_h.iterator();

      while(var0.hasNext()) {
         Fluid var1 = (Fluid)var0.next();
         UnmodifiableIterator var2 = var1.func_207182_e().func_177619_a().iterator();

         while(var2.hasNext()) {
            IFluidState var3 = (IFluidState)var2.next();
            field_207201_d.func_195867_b(var3);
         }
      }

   }

   private static void func_207198_a(String var0, Fluid var1) {
      func_207194_a(new ResourceLocation(var0), var1);
   }

   private static void func_207194_a(ResourceLocation var0, Fluid var1) {
      IRegistry.field_212619_h.func_82595_a(var0, var1);
   }
}
