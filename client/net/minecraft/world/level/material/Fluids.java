package net.minecraft.world.level.material;

import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;

public class Fluids {
   public static final Fluid EMPTY;
   public static final FlowingFluid FLOWING_WATER;
   public static final FlowingFluid WATER;
   public static final FlowingFluid FLOWING_LAVA;
   public static final FlowingFluid LAVA;

   public Fluids() {
      super();
   }

   private static <T extends Fluid> T register(final ResourceKey<Fluid> id, final T fluid) {
      return (T)(Registry.register(BuiltInRegistries.FLUID, (ResourceKey)id, fluid));
   }

   static {
      EMPTY = register(FluidIds.EMPTY, new EmptyFluid());
      FLOWING_WATER = (FlowingFluid)register(FluidIds.FLOWING_WATER, new WaterFluid.Flowing());
      WATER = (FlowingFluid)register(FluidIds.WATER, new WaterFluid.Source());
      FLOWING_LAVA = (FlowingFluid)register(FluidIds.FLOWING_LAVA, new LavaFluid.Flowing());
      LAVA = (FlowingFluid)register(FluidIds.LAVA, new LavaFluid.Source());

      for(Fluid fluid : BuiltInRegistries.FLUID) {
         UnmodifiableIterator var2 = fluid.getStateDefinition().getPossibleStates().iterator();

         while(var2.hasNext()) {
            FluidState state = (FluidState)var2.next();
            Fluid.FLUID_STATE_REGISTRY.add(state);
         }
      }

   }
}
