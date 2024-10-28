package net.minecraft.advancements.critereon;

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

public record LocationPredicate(Optional<PositionPredicate> position, Optional<HolderSet<Biome>> biomes, Optional<HolderSet<Structure>> structures, Optional<ResourceKey<Level>> dimension, Optional<Boolean> smokey, Optional<LightPredicate> light, Optional<BlockPredicate> block, Optional<FluidPredicate> fluid) {
   public static final Codec<LocationPredicate> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(LocationPredicate.PositionPredicate.CODEC.optionalFieldOf("position").forGetter(LocationPredicate::position), RegistryCodecs.homogeneousList(Registries.BIOME).optionalFieldOf("biomes").forGetter(LocationPredicate::biomes), RegistryCodecs.homogeneousList(Registries.STRUCTURE).optionalFieldOf("structures").forGetter(LocationPredicate::structures), ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("dimension").forGetter(LocationPredicate::dimension), Codec.BOOL.optionalFieldOf("smokey").forGetter(LocationPredicate::smokey), LightPredicate.CODEC.optionalFieldOf("light").forGetter(LocationPredicate::light), BlockPredicate.CODEC.optionalFieldOf("block").forGetter(LocationPredicate::block), FluidPredicate.CODEC.optionalFieldOf("fluid").forGetter(LocationPredicate::fluid)).apply(var0, LocationPredicate::new);
   });

   public LocationPredicate(Optional<PositionPredicate> position, Optional<HolderSet<Biome>> biomes, Optional<HolderSet<Structure>> structures, Optional<ResourceKey<Level>> dimension, Optional<Boolean> smokey, Optional<LightPredicate> light, Optional<BlockPredicate> block, Optional<FluidPredicate> fluid) {
      super();
      this.position = position;
      this.biomes = biomes;
      this.structures = structures;
      this.dimension = dimension;
      this.smokey = smokey;
      this.light = light;
      this.block = block;
      this.fluid = fluid;
   }

   public boolean matches(ServerLevel var1, double var2, double var4, double var6) {
      if (this.position.isPresent() && !((PositionPredicate)this.position.get()).matches(var2, var4, var6)) {
         return false;
      } else if (this.dimension.isPresent() && this.dimension.get() != var1.dimension()) {
         return false;
      } else {
         BlockPos var8 = BlockPos.containing(var2, var4, var6);
         boolean var9 = var1.isLoaded(var8);
         if (!this.biomes.isPresent() || var9 && ((HolderSet)this.biomes.get()).contains(var1.getBiome(var8))) {
            if (this.structures.isPresent() && (!var9 || !var1.structureManager().getStructureWithPieceAt(var8, (HolderSet)this.structures.get()).isValid())) {
               return false;
            } else if (!this.smokey.isPresent() || var9 && (Boolean)this.smokey.get() == CampfireBlock.isSmokeyPos(var1, var8)) {
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
      }
   }

   public Optional<PositionPredicate> position() {
      return this.position;
   }

   public Optional<HolderSet<Biome>> biomes() {
      return this.biomes;
   }

   public Optional<HolderSet<Structure>> structures() {
      return this.structures;
   }

   public Optional<ResourceKey<Level>> dimension() {
      return this.dimension;
   }

   public Optional<Boolean> smokey() {
      return this.smokey;
   }

   public Optional<LightPredicate> light() {
      return this.light;
   }

   public Optional<BlockPredicate> block() {
      return this.block;
   }

   public Optional<FluidPredicate> fluid() {
      return this.fluid;
   }

   private static record PositionPredicate(MinMaxBounds.Doubles x, MinMaxBounds.Doubles y, MinMaxBounds.Doubles z) {
      public static final Codec<PositionPredicate> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(MinMaxBounds.Doubles.CODEC.optionalFieldOf("x", MinMaxBounds.Doubles.ANY).forGetter(PositionPredicate::x), MinMaxBounds.Doubles.CODEC.optionalFieldOf("y", MinMaxBounds.Doubles.ANY).forGetter(PositionPredicate::y), MinMaxBounds.Doubles.CODEC.optionalFieldOf("z", MinMaxBounds.Doubles.ANY).forGetter(PositionPredicate::z)).apply(var0, PositionPredicate::new);
      });

      private PositionPredicate(MinMaxBounds.Doubles x, MinMaxBounds.Doubles y, MinMaxBounds.Doubles z) {
         super();
         this.x = x;
         this.y = y;
         this.z = z;
      }

      static Optional<PositionPredicate> of(MinMaxBounds.Doubles var0, MinMaxBounds.Doubles var1, MinMaxBounds.Doubles var2) {
         return var0.isAny() && var1.isAny() && var2.isAny() ? Optional.empty() : Optional.of(new PositionPredicate(var0, var1, var2));
      }

      public boolean matches(double var1, double var3, double var5) {
         return this.x.matches(var1) && this.y.matches(var3) && this.z.matches(var5);
      }

      public MinMaxBounds.Doubles x() {
         return this.x;
      }

      public MinMaxBounds.Doubles y() {
         return this.y;
      }

      public MinMaxBounds.Doubles z() {
         return this.z;
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
      }

      public static Builder location() {
         return new Builder();
      }

      public static Builder inBiome(Holder<Biome> var0) {
         return location().setBiomes(HolderSet.direct(var0));
      }

      public static Builder inDimension(ResourceKey<Level> var0) {
         return location().setDimension(var0);
      }

      public static Builder inStructure(Holder<Structure> var0) {
         return location().setStructures(HolderSet.direct(var0));
      }

      public static Builder atYLocation(MinMaxBounds.Doubles var0) {
         return location().setY(var0);
      }

      public Builder setX(MinMaxBounds.Doubles var1) {
         this.x = var1;
         return this;
      }

      public Builder setY(MinMaxBounds.Doubles var1) {
         this.y = var1;
         return this;
      }

      public Builder setZ(MinMaxBounds.Doubles var1) {
         this.z = var1;
         return this;
      }

      public Builder setBiomes(HolderSet<Biome> var1) {
         this.biomes = Optional.of(var1);
         return this;
      }

      public Builder setStructures(HolderSet<Structure> var1) {
         this.structures = Optional.of(var1);
         return this;
      }

      public Builder setDimension(ResourceKey<Level> var1) {
         this.dimension = Optional.of(var1);
         return this;
      }

      public Builder setLight(LightPredicate.Builder var1) {
         this.light = Optional.of(var1.build());
         return this;
      }

      public Builder setBlock(BlockPredicate.Builder var1) {
         this.block = Optional.of(var1.build());
         return this;
      }

      public Builder setFluid(FluidPredicate.Builder var1) {
         this.fluid = Optional.of(var1.build());
         return this;
      }

      public Builder setSmokey(boolean var1) {
         this.smokey = Optional.of(var1);
         return this;
      }

      public LocationPredicate build() {
         Optional var1 = LocationPredicate.PositionPredicate.of(this.x, this.y, this.z);
         return new LocationPredicate(var1, this.biomes, this.structures, this.dimension, this.smokey, this.light, this.block, this.fluid);
      }
   }
}
