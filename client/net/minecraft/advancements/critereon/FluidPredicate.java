package net.minecraft.advancements.critereon;

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
   public static final Codec<FluidPredicate> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(RegistryCodecs.homogeneousList(Registries.FLUID).optionalFieldOf("fluids").forGetter(FluidPredicate::fluids), StatePropertiesPredicate.CODEC.optionalFieldOf("state").forGetter(FluidPredicate::properties)).apply(var0, FluidPredicate::new);
   });

   public FluidPredicate(Optional<HolderSet<Fluid>> fluids, Optional<StatePropertiesPredicate> properties) {
      super();
      this.fluids = fluids;
      this.properties = properties;
   }

   public boolean matches(ServerLevel var1, BlockPos var2) {
      if (!var1.isLoaded(var2)) {
         return false;
      } else {
         FluidState var3 = var1.getFluidState(var2);
         if (this.fluids.isPresent() && !var3.is((HolderSet)this.fluids.get())) {
            return false;
         } else {
            return !this.properties.isPresent() || ((StatePropertiesPredicate)this.properties.get()).matches(var3);
         }
      }
   }

   public Optional<HolderSet<Fluid>> fluids() {
      return this.fluids;
   }

   public Optional<StatePropertiesPredicate> properties() {
      return this.properties;
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

      public Builder of(Fluid var1) {
         this.fluids = Optional.of(HolderSet.direct(var1.builtInRegistryHolder()));
         return this;
      }

      public Builder of(HolderSet<Fluid> var1) {
         this.fluids = Optional.of(var1);
         return this;
      }

      public Builder setProperties(StatePropertiesPredicate var1) {
         this.properties = Optional.of(var1);
         return this;
      }

      public FluidPredicate build() {
         return new FluidPredicate(this.fluids, this.properties);
      }
   }
}
