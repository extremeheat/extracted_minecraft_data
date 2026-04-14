package net.minecraft.advancements.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public record FluidPredicate(Optional<HolderSet<Fluid>> fluids, Optional<StatePropertiesPredicate> properties) {
   public static final Codec<FluidPredicate> CODEC = RecordCodecBuilder.create((i) -> i.group(RegistryCodecs.homogeneousList(Registries.FLUID).optionalFieldOf("fluids").forGetter(FluidPredicate::fluids), StatePropertiesPredicate.CODEC.optionalFieldOf("state").forGetter(FluidPredicate::properties)).apply(i, FluidPredicate::new));

   public FluidPredicate {
      super();
   }

   public boolean matches(final ServerLevel level, final BlockPos pos) {
      if (!level.isLoaded(pos)) {
         return false;
      } else {
         FluidState state = level.getFluidState(pos);
         if (this.fluids.isPresent() && !state.is((HolderSet)this.fluids.get())) {
            return false;
         } else {
            return !this.properties.isPresent() || ((StatePropertiesPredicate)this.properties.get()).matches(state);
         }
      }
   }

   public static class Builder {
      private Optional<HolderSet<Fluid>> fluids = Optional.empty();
      private Optional<StatePropertiesPredicate> properties = Optional.empty();

      private Builder() {
         super();
      }

      public static Builder fluid() {
         return new Builder();
      }

      public Builder of(final Fluid fluid) {
         this.fluids = Optional.of(HolderSet.direct(fluid.builtInRegistryHolder()));
         return this;
      }

      public Builder of(final HolderSet<Fluid> fluids) {
         this.fluids = Optional.of(fluids);
         return this;
      }

      public Builder setProperties(final StatePropertiesPredicate properties) {
         this.properties = Optional.of(properties);
         return this;
      }

      public FluidPredicate build() {
         return new FluidPredicate(this.fluids, this.properties);
      }
   }
}
