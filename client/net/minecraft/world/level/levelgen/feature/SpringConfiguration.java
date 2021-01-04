package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class SpringConfiguration implements FeatureConfiguration {
   public final FluidState state;

   public SpringConfiguration(FluidState var1) {
      super();
      this.state = var1;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("state"), FluidState.serialize(var1, this.state).getValue())));
   }

   public static <T> SpringConfiguration deserialize(Dynamic<T> var0) {
      FluidState var1 = (FluidState)var0.get("state").map(FluidState::deserialize).orElse(Fluids.EMPTY.defaultFluidState());
      return new SpringConfiguration(var1);
   }
}
