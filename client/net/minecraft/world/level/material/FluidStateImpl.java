package net.minecraft.world.level.material;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.Property;

public class FluidStateImpl extends AbstractStateHolder<Fluid, FluidState> implements FluidState {
   public FluidStateImpl(Fluid var1, ImmutableMap<Property<?>, Comparable<?>> var2) {
      super(var1, var2);
   }

   public Fluid getType() {
      return (Fluid)this.owner;
   }
}
