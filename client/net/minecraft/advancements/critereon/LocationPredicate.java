package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.levelgen.structure.Structure;

public record LocationPredicate(
   Optional<LocationPredicate.PositionPredicate> b,
   Optional<HolderSet<Biome>> c,
   Optional<HolderSet<Structure>> d,
   Optional<ResourceKey<Level>> e,
   Optional<Boolean> f,
   Optional<LightPredicate> g,
   Optional<BlockPredicate> h,
   Optional<FluidPredicate> i
) {
   private final Optional<LocationPredicate.PositionPredicate> position;
   private final Optional<HolderSet<Biome>> biomes;
   private final Optional<HolderSet<Structure>> structures;
   private final Optional<ResourceKey<Level>> dimension;
   private final Optional<Boolean> smokey;
   private final Optional<LightPredicate> light;
   private final Optional<BlockPredicate> block;
   private final Optional<FluidPredicate> fluid;
   public static final Codec<LocationPredicate> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ExtraCodecs.strictOptionalField(LocationPredicate.PositionPredicate.CODEC, "position").forGetter(LocationPredicate::position),
               ExtraCodecs.strictOptionalField(RegistryCodecs.homogeneousList(Registries.BIOME), "biomes").forGetter(LocationPredicate::biomes),
               ExtraCodecs.strictOptionalField(RegistryCodecs.homogeneousList(Registries.STRUCTURE), "structures").forGetter(LocationPredicate::structures),
               ExtraCodecs.strictOptionalField(ResourceKey.codec(Registries.DIMENSION), "dimension").forGetter(LocationPredicate::dimension),
               ExtraCodecs.strictOptionalField(Codec.BOOL, "smokey").forGetter(LocationPredicate::smokey),
               ExtraCodecs.strictOptionalField(LightPredicate.CODEC, "light").forGetter(LocationPredicate::light),
               ExtraCodecs.strictOptionalField(BlockPredicate.CODEC, "block").forGetter(LocationPredicate::block),
               ExtraCodecs.strictOptionalField(FluidPredicate.CODEC, "fluid").forGetter(LocationPredicate::fluid)
            )
            .apply(var0, LocationPredicate::new)
   );

   public LocationPredicate(
      Optional<LocationPredicate.PositionPredicate> var1,
      Optional<HolderSet<Biome>> var2,
      Optional<HolderSet<Structure>> var3,
      Optional<ResourceKey<Level>> var4,
      Optional<Boolean> var5,
      Optional<LightPredicate> var6,
      Optional<BlockPredicate> var7,
      Optional<FluidPredicate> var8
   ) {
      super();
      this.position = var1;
      this.biomes = var2;
      this.structures = var3;
      this.dimension = var4;
      this.smokey = var5;
      this.light = var6;
      this.block = var7;
      this.fluid = var8;
   }

   public boolean matches(ServerLevel var1, double var2, double var4, double var6) {
      if (this.position.isPresent() && !((LocationPredicate.PositionPredicate)this.position.get()).matches(var2, var4, var6)) {
         return false;
      } else if (this.dimension.isPresent() && this.dimension.get() != var1.dimension()) {
         return false;
      } else {
         BlockPos var8 = BlockPos.containing(var2, var4, var6);
         boolean var9 = var1.isLoaded(var8);
         if (!this.biomes.isPresent() || var9 && this.biomes.get().contains(var1.getBiome(var8))) {
            if (!this.structures.isPresent() || var9 && var1.structureManager().getStructureWithPieceAt(var8, this.structures.get()).isValid()) {
               if (!this.smokey.isPresent() || var9 && this.smokey.get() == CampfireBlock.isSmokeyPos(var1, var8)) {
                  if (this.light.isPresent() && !((LightPredicate)this.light.get()).matches(var1, var8)) {
                     return false;
                  } else if (this.block.isPresent() && !((BlockPredicate)this.block.get()).matches(var1, var8)) {
                     return false;
                  } else {
                     return !this.fluid.isPresent() || ((FluidPredicate)this.fluid.get()).matches(var1, var8);
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public static class Builder {
      private MinMaxBounds.Doubles x = MinMaxBounds.Doubles.ANY;
      private MinMaxBounds.Doubles y = MinMaxBounds.Doubles.ANY;
      private MinMaxBounds.Doubles z = MinMaxBounds.Doubles.ANY;
      private Optional<HolderSet<Biome>> biomes = Optional.empty();
      private Optional<HolderSet<Structure>> structures = Optional.empty();
      private Optional<ResourceKey<Level>> dimension = Optional.empty();
      private Optional<Boolean> smokey = Optional.empty();
      private Optional<LightPredicate> light = Optional.empty();
      private Optional<BlockPredicate> block = Optional.empty();
      private Optional<FluidPredicate> fluid = Optional.empty();

      public Builder() {
         super();
      }

      public static LocationPredicate.Builder location() {
         return new LocationPredicate.Builder();
      }

      public static LocationPredicate.Builder inBiome(Holder<Biome> var0) {
         return location().setBiomes(HolderSet.direct(var0));
      }

      public static LocationPredicate.Builder inDimension(ResourceKey<Level> var0) {
         return location().setDimension(var0);
      }

      public static LocationPredicate.Builder inStructure(Holder<Structure> var0) {
         return location().setStructures(HolderSet.direct(var0));
      }

      public static LocationPredicate.Builder atYLocation(MinMaxBounds.Doubles var0) {
         return location().setY(var0);
      }

      public LocationPredicate.Builder setX(MinMaxBounds.Doubles var1) {
         this.x = var1;
         return this;
      }

      public LocationPredicate.Builder setY(MinMaxBounds.Doubles var1) {
         this.y = var1;
         return this;
      }

      public LocationPredicate.Builder setZ(MinMaxBounds.Doubles var1) {
         this.z = var1;
         return this;
      }

      public LocationPredicate.Builder setBiomes(HolderSet<Biome> var1) {
         this.biomes = Optional.of(var1);
         return this;
      }

      public LocationPredicate.Builder setStructures(HolderSet<Structure> var1) {
         this.structures = Optional.of(var1);
         return this;
      }

      public LocationPredicate.Builder setDimension(ResourceKey<Level> var1) {
         this.dimension = Optional.of(var1);
         return this;
      }

      public LocationPredicate.Builder setLight(LightPredicate.Builder var1) {
         this.light = Optional.of(var1.build());
         return this;
      }

      public LocationPredicate.Builder setBlock(BlockPredicate.Builder var1) {
         this.block = Optional.of(var1.build());
         return this;
      }

      public LocationPredicate.Builder setFluid(FluidPredicate.Builder var1) {
         this.fluid = Optional.of(var1.build());
         return this;
      }

      public LocationPredicate.Builder setSmokey(boolean var1) {
         this.smokey = Optional.of(var1);
         return this;
      }

      public LocationPredicate build() {
         Optional var1 = LocationPredicate.PositionPredicate.of(this.x, this.y, this.z);
         return new LocationPredicate(var1, this.biomes, this.structures, this.dimension, this.smokey, this.light, this.block, this.fluid);
      }
   }

   static record PositionPredicate(MinMaxBounds.Doubles b, MinMaxBounds.Doubles c, MinMaxBounds.Doubles d) {
      private final MinMaxBounds.Doubles x;
      private final MinMaxBounds.Doubles y;
      private final MinMaxBounds.Doubles z;
      public static final Codec<LocationPredicate.PositionPredicate> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  ExtraCodecs.strictOptionalField(MinMaxBounds.Doubles.CODEC, "x", MinMaxBounds.Doubles.ANY).forGetter(LocationPredicate.PositionPredicate::x),
                  ExtraCodecs.strictOptionalField(MinMaxBounds.Doubles.CODEC, "y", MinMaxBounds.Doubles.ANY).forGetter(LocationPredicate.PositionPredicate::y),
                  ExtraCodecs.strictOptionalField(MinMaxBounds.Doubles.CODEC, "z", MinMaxBounds.Doubles.ANY).forGetter(LocationPredicate.PositionPredicate::z)
               )
               .apply(var0, LocationPredicate.PositionPredicate::new)
      );

      private PositionPredicate(MinMaxBounds.Doubles var1, MinMaxBounds.Doubles var2, MinMaxBounds.Doubles var3) {
         super();
         this.x = var1;
         this.y = var2;
         this.z = var3;
      }

      static Optional<LocationPredicate.PositionPredicate> of(MinMaxBounds.Doubles var0, MinMaxBounds.Doubles var1, MinMaxBounds.Doubles var2) {
         return var0.isAny() && var1.isAny() && var2.isAny() ? Optional.empty() : Optional.of(new LocationPredicate.PositionPredicate(var0, var1, var2));
      }

      public boolean matches(double var1, double var3, double var5) {
         return this.x.matches(var1) && this.y.matches(var3) && this.z.matches(var5);
      }
   }
}
