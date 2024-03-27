package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public record FluidPredicate(Optional<HolderSet<Fluid>> b, Optional<StatePropertiesPredicate> c) {
   private final Optional<HolderSet<Fluid>> fluids;
   private final Optional<StatePropertiesPredicate> properties;
   public static final Codec<FluidPredicate> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               RegistryCodecs.homogeneousList(Registries.FLUID).optionalFieldOf("fluids").forGetter(FluidPredicate::fluids),
               StatePropertiesPredicate.CODEC.optionalFieldOf("state").forGetter(FluidPredicate::properties)
            )
            .apply(var0, FluidPredicate::new)
   );

   public FluidPredicate(Optional<HolderSet<Fluid>> var1, Optional<StatePropertiesPredicate> var2) {
      super();
      this.fluids = var1;
      this.properties = var2;
   }

   public boolean matches(ServerLevel var1, BlockPos var2) {
      if (!var1.isLoaded(var2)) {
         return false;
      } else {
         FluidState var3 = var1.getFluidState(var2);
         if (this.fluids.isPresent() && !var3.is(this.fluids.get())) {
            return false;
         } else {
            return !this.properties.isPresent() || ((StatePropertiesPredicate)this.properties.get()).matches(var3);
         }
      }
   }

   public static class Builder {
      private Optional<HolderSet<Fluid>> fluids = Optional.empty();
      private Optional<StatePropertiesPredicate> properties = Optional.empty();

      private Builder() {
         super();
      }

      public static FluidPredicate.Builder fluid() {
         return new FluidPredicate.Builder();
      }

      public FluidPredicate.Builder of(Fluid var1) {
         this.fluids = Optional.of(HolderSet.direct(var1.builtInRegistryHolder()));
         return this;
      }

      public FluidPredicate.Builder of(HolderSet<Fluid> var1) {
         this.fluids = Optional.of(var1);
         return this;
      }

      public FluidPredicate.Builder setProperties(StatePropertiesPredicate var1) {
         this.properties = Optional.of(var1);
         return this;
      }

      public FluidPredicate build() {
         return new FluidPredicate(this.fluids, this.properties);
      }
   }
}
