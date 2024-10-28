package net.minecraft.world.level.material;

import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class Fluids {
   public static final Fluid EMPTY = register("empty", new EmptyFluid());
   public static final FlowingFluid FLOWING_WATER = (FlowingFluid)register("flowing_water", new WaterFluid.Flowing());
   public static final FlowingFluid WATER = (FlowingFluid)register("water", new WaterFluid.Source());
   public static final FlowingFluid FLOWING_LAVA = (FlowingFluid)register("flowing_lava", new LavaFluid.Flowing());
   public static final FlowingFluid LAVA = (FlowingFluid)register("lava", new LavaFluid.Source());

   public Fluids() {
      super();
   }

   private static <T extends Fluid> T register(String var0, T var1) {
      return (Fluid)Registry.register(BuiltInRegistries.FLUID, (String)var0, var1);
   }

   static {
      Iterator var0 = BuiltInRegistries.FLUID.iterator();

      while(var0.hasNext()) {
         Fluid var1 = (Fluid)var0.next();
         UnmodifiableIterator var2 = var1.getStateDefinition().getPossibleStates().iterator();

         while(var2.hasNext()) {
            FluidState var3 = (FluidState)var2.next();
            Fluid.FLUID_STATE_REGISTRY.add(var3);
         }
      }

   }
}
