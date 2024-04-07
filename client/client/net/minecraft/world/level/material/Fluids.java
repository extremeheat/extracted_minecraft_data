package net.minecraft.world.level.material;

import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class Fluids {
   public static final Fluid EMPTY = register("empty", new EmptyFluid());
   public static final FlowingFluid FLOWING_WATER = register("flowing_water", new WaterFluid.Flowing());
   public static final FlowingFluid WATER = register("water", new WaterFluid.Source());
   public static final FlowingFluid FLOWING_LAVA = register("flowing_lava", new LavaFluid.Flowing());
   public static final FlowingFluid LAVA = register("lava", new LavaFluid.Source());

   public Fluids() {
      super();
   }

   private static <T extends Fluid> T register(String var0, T var1) {
      return Registry.register(BuiltInRegistries.FLUID, var0, (T)var1);
   }

   static {
      for (Fluid var1 : BuiltInRegistries.FLUID) {
         UnmodifiableIterator var2 = var1.getStateDefinition().getPossibleStates().iterator();

         while (var2.hasNext()) {
            FluidState var3 = (FluidState)var2.next();
            Fluid.FLUID_STATE_REGISTRY.add(var3);
         }
      }
   }
}
