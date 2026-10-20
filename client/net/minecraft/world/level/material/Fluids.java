package net.minecraft.world.level.material;

import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.references.ItemIds;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;

public class Fluids {
   public static final Fluid EMPTY;
   public static final FlowingFluid WATER;
   public static final FlowingFluid LAVA;

   public Fluids() {
      super();
   }

   private static <T extends Fluid> T register(final ResourceKey<Fluid> id, final T fluid) {
      return (T)(Registry.register(BuiltInRegistries.FLUID, (ResourceKey)id, fluid));
   }

   static {
      EMPTY = register(FluidIds.EMPTY, new EmptyFluid(Fluid.Properties.of()));
      WATER = (FlowingFluid)register(FluidIds.WATER, new WaterFluid(Fluid.Properties.of().bucketItem(ItemIds.WATER_BUCKET).soundEvent(SoundEvents.BUCKET_FILL).dripParticle(ParticleTypes.DRIPPING_WATER).explosionResistance(100.0F).drag(0.8F)));
      LAVA = (FlowingFluid)register(FluidIds.LAVA, new LavaFluid(Fluid.Properties.of().bucketItem(ItemIds.LAVA_BUCKET).soundEvent(SoundEvents.BUCKET_FILL_LAVA).dripParticle(ParticleTypes.DRIPPING_LAVA).explosionResistance(100.0F).randomTicking().drag(0.5F)));

      for(Fluid fluid : BuiltInRegistries.FLUID) {
         UnmodifiableIterator var2 = fluid.getStateDefinition().getPossibleStates().iterator();

         while(var2.hasNext()) {
            FluidState state = (FluidState)var2.next();
            Fluid.FLUID_STATE_REGISTRY.add(state);
         }
      }

   }
}
