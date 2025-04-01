package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.mines.SpecialMine;
import net.minecraft.world.level.mines.WorldEffect;

public record LocationPredicate(Optional<PositionPredicate> position, Optional<HolderSet<Biome>> biomes, Optional<HolderSet<Structure>> structures, Optional<ResourceKey<Level>> dimension, Optional<ResourceKey<SpecialMine>> specialMine, Optional<Boolean> smokey, Optional<LightPredicate> light, Optional<BlockPredicate> block, Optional<FluidPredicate> fluid, Optional<Boolean> canSeeSky, List<WorldEffect> worldEffects) {
   public static final Codec<LocationPredicate> CODEC = RecordCodecBuilder.create((var0) -> var0.group(LocationPredicate.PositionPredicate.CODEC.optionalFieldOf("position").forGetter(LocationPredicate::position), RegistryCodecs.homogeneousList(Registries.BIOME).optionalFieldOf("biomes").forGetter(LocationPredicate::biomes), RegistryCodecs.homogeneousList(Registries.STRUCTURE).optionalFieldOf("structures").forGetter(LocationPredicate::structures), ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("dimension").forGetter(LocationPredicate::dimension), ResourceKey.codec(Registries.SPECIAL_MINE).optionalFieldOf("special_mine").forGetter(LocationPredicate::specialMine), Codec.BOOL.optionalFieldOf("smokey").forGetter(LocationPredicate::smokey), LightPredicate.CODEC.optionalFieldOf("light").forGetter(LocationPredicate::light), BlockPredicate.CODEC.optionalFieldOf("block").forGetter(LocationPredicate::block), FluidPredicate.CODEC.optionalFieldOf("fluid").forGetter(LocationPredicate::fluid), Codec.BOOL.optionalFieldOf("can_see_sky").forGetter(LocationPredicate::canSeeSky), WorldEffect.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(LocationPredicate::worldEffects)).apply(var0, LocationPredicate::new));

   public LocationPredicate(Optional<PositionPredicate> var1, Optional<HolderSet<Biome>> var2, Optional<HolderSet<Structure>> var3, Optional<ResourceKey<Level>> var4, Optional<ResourceKey<SpecialMine>> var5, Optional<Boolean> var6, Optional<LightPredicate> var7, Optional<BlockPredicate> var8, Optional<FluidPredicate> var9, Optional<Boolean> var10, List<WorldEffect> var11) {
      super();
      this.position = var1;
      this.biomes = var2;
      this.structures = var3;
      this.dimension = var4;
      this.specialMine = var5;
      this.smokey = var6;
      this.light = var7;
      this.block = var8;
      this.fluid = var9;
      this.canSeeSky = var10;
      this.worldEffects = var11;
   }

   public boolean matches(ServerLevel var1, double var2, double var4, double var6) {
      if (this.position.isPresent() && !((PositionPredicate)this.position.get()).matches(var2, var4, var6)) {
         return false;
      } else if (this.dimension.isPresent() && this.dimension.get() != var1.dimension()) {
         return false;
      } else {
         Optional var10000 = var1.specialMine();
         Registry var10001 = BuiltInRegistries.SPECIAL_MINE;
         Objects.requireNonNull(var10001);
         Optional var8 = var10000.flatMap(var10001::getResourceKey);
         if (!this.specialMine.isPresent() || !var8.isEmpty() && this.specialMine.get() == var8.get()) {
            BlockPos var9 = BlockPos.containing(var2, var4, var6);
            boolean var10 = var1.isLoaded(var9);
            if (!this.biomes.isPresent() || var10 && ((HolderSet)this.biomes.get()).contains(var1.getBiome(var9))) {
               if (!this.structures.isPresent() || var10 && var1.structureManager().getStructureWithPieceAt(var9, (HolderSet)this.structures.get()).isValid()) {
                  if (!this.smokey.isPresent() || var10 && (Boolean)this.smokey.get() == CampfireBlock.isSmokeyPos(var1, var9)) {
                     if (this.light.isPresent() && !((LightPredicate)this.light.get()).matches(var1, var9)) {
                        return false;
                     } else if (this.block.isPresent() && !((BlockPredicate)this.block.get()).matches(var1, var9)) {
                        return false;
                     } else if (this.fluid.isPresent() && !((FluidPredicate)this.fluid.get()).matches(var1, var9)) {
                        return false;
                     } else if (this.canSeeSky.isPresent() && (Boolean)this.canSeeSky.get() != var1.canSeeSky(var9)) {
                        return false;
                     } else {
                        for(WorldEffect var12 : this.worldEffects) {
                           if (!var1.isActive(var12)) {
                              return false;
                           }
                        }

                        return true;
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
         } else {
            return false;
         }
      }
   }

   static record PositionPredicate(MinMaxBounds.Doubles x, MinMaxBounds.Doubles y, MinMaxBounds.Doubles z) {
      public static final Codec<PositionPredicate> CODEC = RecordCodecBuilder.create((var0) -> var0.group(MinMaxBounds.Doubles.CODEC.optionalFieldOf("x", MinMaxBounds.Doubles.ANY).forGetter(PositionPredicate::x), MinMaxBounds.Doubles.CODEC.optionalFieldOf("y", MinMaxBounds.Doubles.ANY).forGetter(PositionPredicate::y), MinMaxBounds.Doubles.CODEC.optionalFieldOf("z", MinMaxBounds.Doubles.ANY).forGetter(PositionPredicate::z)).apply(var0, PositionPredicate::new));

      private PositionPredicate(MinMaxBounds.Doubles var1, MinMaxBounds.Doubles var2, MinMaxBounds.Doubles var3) {
         super();
         this.x = var1;
         this.y = var2;
         this.z = var3;
      }

      static Optional<PositionPredicate> of(MinMaxBounds.Doubles var0, MinMaxBounds.Doubles var1, MinMaxBounds.Doubles var2) {
         return var0.isAny() && var1.isAny() && var2.isAny() ? Optional.empty() : Optional.of(new PositionPredicate(var0, var1, var2));
      }

      public boolean matches(double var1, double var3, double var5) {
         return this.x.matches(var1) && this.y.matches(var3) && this.z.matches(var5);
      }
   }

   public static class Builder {
      private MinMaxBounds.Doubles x;
      private MinMaxBounds.Doubles y;
      private MinMaxBounds.Doubles z;
      private Optional<HolderSet<Biome>> biomes;
      private Optional<HolderSet<Structure>> structures;
      private Optional<ResourceKey<Level>> dimension;
      private Optional<ResourceKey<SpecialMine>> specialMine;
      private Optional<Boolean> smokey;
      private Optional<LightPredicate> light;
      private Optional<BlockPredicate> block;
      private Optional<FluidPredicate> fluid;
      private Optional<Boolean> canSeeSky;
      private List<WorldEffect> effects;

      public Builder() {
         super();
         this.x = MinMaxBounds.Doubles.ANY;
         this.y = MinMaxBounds.Doubles.ANY;
         this.z = MinMaxBounds.Doubles.ANY;
         this.biomes = Optional.empty();
         this.structures = Optional.empty();
         this.dimension = Optional.empty();
         this.specialMine = Optional.empty();
         this.smokey = Optional.empty();
         this.light = Optional.empty();
         this.block = Optional.empty();
         this.fluid = Optional.empty();
         this.canSeeSky = Optional.empty();
         this.effects = new ArrayList();
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

      public Builder setSpecialMine(ResourceKey<SpecialMine> var1) {
         this.specialMine = Optional.of(var1);
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

      public Builder setCanSeeSky(boolean var1) {
         this.canSeeSky = Optional.of(var1);
         return this;
      }

      public Builder hasWorldEffects(WorldEffect... var1) {
         this.effects.addAll(Arrays.asList(var1));
         return this;
      }

      public LocationPredicate build() {
         Optional var1 = LocationPredicate.PositionPredicate.of(this.x, this.y, this.z);
         return new LocationPredicate(var1, this.biomes, this.structures, this.dimension, this.specialMine, this.smokey, this.light, this.block, this.fluid, this.canSeeSky, List.copyOf(this.effects));
      }
   }
}
