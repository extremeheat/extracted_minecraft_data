package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public record FluidPredicate(Optional<TagKey<Fluid>> b, Optional<Holder<Fluid>> c, Optional<StatePropertiesPredicate> d) {
   private final Optional<TagKey<Fluid>> tag;
   private final Optional<Holder<Fluid>> fluid;
   private final Optional<StatePropertiesPredicate> properties;
   public static final Codec<FluidPredicate> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ExtraCodecs.strictOptionalField(TagKey.codec(Registries.FLUID), "tag").forGetter(FluidPredicate::tag),
               ExtraCodecs.strictOptionalField(BuiltInRegistries.FLUID.holderByNameCodec(), "fluid").forGetter(FluidPredicate::fluid),
               ExtraCodecs.strictOptionalField(StatePropertiesPredicate.CODEC, "state").forGetter(FluidPredicate::properties)
            )
            .apply(var0, FluidPredicate::new)
   );

   public FluidPredicate(Optional<TagKey<Fluid>> var1, Optional<Holder<Fluid>> var2, Optional<StatePropertiesPredicate> var3) {
      super();
      this.tag = var1;
      this.fluid = var2;
      this.properties = var3;
   }

   public boolean matches(ServerLevel var1, BlockPos var2) {
      if (!var1.isLoaded(var2)) {
         return false;
      } else {
         FluidState var3 = var1.getFluidState(var2);
         if (this.tag.isPresent() && !var3.is(this.tag.get())) {
            return false;
         } else if (this.fluid.isPresent() && !var3.is(this.fluid.get().value())) {
            return false;
         } else {
            return !this.properties.isPresent() || this.properties.get().matches(var3);
         }
      }
   }

   public static class Builder {
      private Optional<Holder<Fluid>> fluid = Optional.empty();
      private Optional<TagKey<Fluid>> fluids = Optional.empty();
      private Optional<StatePropertiesPredicate> properties = Optional.empty();

      private Builder() {
         super();
      }

      public static FluidPredicate.Builder fluid() {
         return new FluidPredicate.Builder();
      }

      public FluidPredicate.Builder of(Fluid var1) {
         this.fluid = Optional.of(var1.builtInRegistryHolder());
         return this;
      }

      public FluidPredicate.Builder of(TagKey<Fluid> var1) {
         this.fluids = Optional.of(var1);
         return this;
      }

      public FluidPredicate.Builder setProperties(StatePropertiesPredicate var1) {
         this.properties = Optional.of(var1);
         return this;
      }

      public FluidPredicate build() {
         return new FluidPredicate(this.fluids, this.fluid, this.properties);
      }
   }
}
