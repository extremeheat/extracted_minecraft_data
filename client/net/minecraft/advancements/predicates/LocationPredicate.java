package net.minecraft.advancements.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.levelgen.structure.Structure;

public record LocationPredicate(Optional<PositionPredicate> position, Optional<HolderSet<Biome>> biomes, Optional<HolderSet<Structure>> structures, Optional<ResourceKey<Level>> dimension, Optional<Boolean> smokey, Optional<LightPredicate> light, Optional<BlockPredicate> block, Optional<FluidPredicate> fluid, Optional<Boolean> canSeeSky) {
   public static final Codec<LocationPredicate> CODEC = RecordCodecBuilder.create((i) -> i.group(LocationPredicate.PositionPredicate.CODEC.optionalFieldOf("position").forGetter(LocationPredicate::position), RegistryCodecs.homogeneousList(Registries.BIOME).optionalFieldOf("biomes").forGetter(LocationPredicate::biomes), RegistryCodecs.homogeneousList(Registries.STRUCTURE).optionalFieldOf("structures").forGetter(LocationPredicate::structures), ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("dimension").forGetter(LocationPredicate::dimension), Codec.BOOL.optionalFieldOf("smokey").forGetter(LocationPredicate::smokey), LightPredicate.CODEC.optionalFieldOf("light").forGetter(LocationPredicate::light), BlockPredicate.CODEC.optionalFieldOf("block").forGetter(LocationPredicate::block), FluidPredicate.CODEC.optionalFieldOf("fluid").forGetter(LocationPredicate::fluid), Codec.BOOL.optionalFieldOf("can_see_sky").forGetter(LocationPredicate::canSeeSky)).apply(i, LocationPredicate::new));

   public LocationPredicate {
      super();
   }

   public boolean matches(final ServerLevel level, final double x, final double y, final double z) {
      if (this.position.isPresent() && !((PositionPredicate)this.position.get()).matches(x, y, z)) {
         return false;
      } else if (this.dimension.isPresent() && this.dimension.get() != level.dimension()) {
         return false;
      } else {
         BlockPos pos = BlockPos.containing(x, y, z);
         boolean loaded = level.isLoaded(pos);
         if (!this.biomes.isPresent() || loaded && ((HolderSet)this.biomes.get()).contains(level.getBiome(pos))) {
            if (!this.structures.isPresent() || loaded && level.structureManager().getStructureWithPieceAt(pos, (HolderSet)this.structures.get()).isValid()) {
               if (!this.smokey.isPresent() || loaded && (Boolean)this.smokey.get() == CampfireBlock.isSmokeyPos(level, pos)) {
                  if (this.light.isPresent() && !((LightPredicate)this.light.get()).matches(level, pos)) {
                     return false;
                  } else if (this.block.isPresent() && !((BlockPredicate)this.block.get()).matches(level, pos)) {
                     return false;
                  } else if (this.fluid.isPresent() && !((FluidPredicate)this.fluid.get()).matches(level, pos)) {
                     return false;
                  } else {
                     return !this.canSeeSky.isPresent() || (Boolean)this.canSeeSky.get() == level.canSeeSky(pos);
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

   private static record PositionPredicate(MinMaxBounds.Doubles x, MinMaxBounds.Doubles y, MinMaxBounds.Doubles z) {
      public static final Codec<PositionPredicate> CODEC = RecordCodecBuilder.create((i) -> i.group(MinMaxBounds.Doubles.CODEC.optionalFieldOf("x", MinMaxBounds.Doubles.ANY).forGetter(PositionPredicate::x), MinMaxBounds.Doubles.CODEC.optionalFieldOf("y", MinMaxBounds.Doubles.ANY).forGetter(PositionPredicate::y), MinMaxBounds.Doubles.CODEC.optionalFieldOf("z", MinMaxBounds.Doubles.ANY).forGetter(PositionPredicate::z)).apply(i, PositionPredicate::new));

      private PositionPredicate {
         super();
      }

      private static Optional<PositionPredicate> of(final MinMaxBounds.Doubles x, final MinMaxBounds.Doubles y, final MinMaxBounds.Doubles z) {
         return x.isAny() && y.isAny() && z.isAny() ? Optional.empty() : Optional.of(new PositionPredicate(x, y, z));
      }

      public boolean matches(final double x, final double y, final double z) {
         return this.x.matches(x) && this.y.matches(y) && this.z.matches(z);
      }
   }

   public static class Builder {
      private MinMaxBounds.Doubles x;
      private MinMaxBounds.Doubles y;
      private MinMaxBounds.Doubles z;
      private Optional<HolderSet<Biome>> biomes;
      private Optional<HolderSet<Structure>> structures;
      private Optional<ResourceKey<Level>> dimension;
      private Optional<Boolean> smokey;
      private Optional<LightPredicate> light;
      private Optional<BlockPredicate> block;
      private Optional<FluidPredicate> fluid;
      private Optional<Boolean> canSeeSky;

      public Builder() {
         super();
         this.x = MinMaxBounds.Doubles.ANY;
         this.y = MinMaxBounds.Doubles.ANY;
         this.z = MinMaxBounds.Doubles.ANY;
         this.biomes = Optional.empty();
         this.structures = Optional.empty();
         this.dimension = Optional.empty();
         this.smokey = Optional.empty();
         this.light = Optional.empty();
         this.block = Optional.empty();
         this.fluid = Optional.empty();
         this.canSeeSky = Optional.empty();
      }

      public static Builder location() {
         return new Builder();
      }

      public static Builder inBiome(final Holder<Biome> biome) {
         return location().setBiomes(HolderSet.direct(biome));
      }

      public static Builder inDimension(final ResourceKey<Level> dimension) {
         return location().setDimension(dimension);
      }

      public static Builder inStructure(final Holder<Structure> structure) {
         return location().setStructures(HolderSet.direct(structure));
      }

      public static Builder atYLocation(final MinMaxBounds.Doubles yLocation) {
         return location().setY(yLocation);
      }

      public Builder setX(final MinMaxBounds.Doubles x) {
         this.x = x;
         return this;
      }

      public Builder setY(final MinMaxBounds.Doubles y) {
         this.y = y;
         return this;
      }

      public Builder setZ(final MinMaxBounds.Doubles z) {
         this.z = z;
         return this;
      }

      public Builder setBiomes(final HolderSet<Biome> biomes) {
         this.biomes = Optional.of(biomes);
         return this;
      }

      public Builder setStructures(final HolderSet<Structure> structures) {
         this.structures = Optional.of(structures);
         return this;
      }

      public Builder setDimension(final ResourceKey<Level> dimension) {
         this.dimension = Optional.of(dimension);
         return this;
      }

      public Builder setLight(final LightPredicate.Builder light) {
         this.light = Optional.of(light.build());
         return this;
      }

      public Builder setBlock(final BlockPredicate.Builder block) {
         this.block = Optional.of(block.build());
         return this;
      }

      public Builder setFluid(final FluidPredicate.Builder fluid) {
         this.fluid = Optional.of(fluid.build());
         return this;
      }

      public Builder setSmokey(final boolean smokey) {
         this.smokey = Optional.of(smokey);
         return this;
      }

      public Builder setCanSeeSky(final boolean canSeeSky) {
         this.canSeeSky = Optional.of(canSeeSky);
         return this;
      }

      public LocationPredicate build() {
         Optional<PositionPredicate> position = LocationPredicate.PositionPredicate.of(this.x, this.y, this.z);
         return new LocationPredicate(position, this.biomes, this.structures, this.dimension, this.smokey, this.light, this.block, this.fluid, this.canSeeSky);
      }
   }
}
