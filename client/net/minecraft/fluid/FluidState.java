package net.minecraft.fluid;

import com.google.common.collect.ImmutableMap;
import net.minecraft.state.AbstractStateHolder;
import net.minecraft.state.IProperty;

public class FluidState extends AbstractStateHolder<Fluid, IFluidState> implements IFluidState {
   public FluidState(Fluid var1, ImmutableMap<IProperty<?>, Comparable<?>> var2) {
      super(var1, var2);
   }

   public Fluid func_206886_c() {
      return (Fluid)this.field_206876_a;
   }
}
