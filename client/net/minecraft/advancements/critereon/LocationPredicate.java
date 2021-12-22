package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LocationPredicate {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final LocationPredicate ANY;
   // $FF: renamed from: x net.minecraft.advancements.critereon.MinMaxBounds$Doubles
   private final MinMaxBounds.Doubles field_435;
   // $FF: renamed from: y net.minecraft.advancements.critereon.MinMaxBounds$Doubles
   private final MinMaxBounds.Doubles field_436;
   // $FF: renamed from: z net.minecraft.advancements.critereon.MinMaxBounds$Doubles
   private final MinMaxBounds.Doubles field_437;
   @Nullable
   private final ResourceKey<Biome> biome;
   @Nullable
   private final StructureFeature<?> feature;
   @Nullable
   private final ResourceKey<Level> dimension;
   @Nullable
   private final Boolean smokey;
   private final LightPredicate light;
   private final BlockPredicate block;
   private final FluidPredicate fluid;

   public LocationPredicate(MinMaxBounds.Doubles var1, MinMaxBounds.Doubles var2, MinMaxBounds.Doubles var3, @Nullable ResourceKey<Biome> var4, @Nullable StructureFeature<?> var5, @Nullable ResourceKey<Level> var6, @Nullable Boolean var7, LightPredicate var8, BlockPredicate var9, FluidPredicate var10) {
      super();
      this.field_435 = var1;
      this.field_436 = var2;
      this.field_437 = var3;
      this.biome = var4;
      this.feature = var5;
      this.dimension = var6;
      this.smokey = var7;
      this.light = var8;
      this.block = var9;
      this.fluid = var10;
   }

   public static LocationPredicate inBiome(ResourceKey<Biome> var0) {
      return new LocationPredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, var0, (StructureFeature)null, (ResourceKey)null, (Boolean)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
   }

   public static LocationPredicate inDimension(ResourceKey<Level> var0) {
      return new LocationPredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, (ResourceKey)null, (StructureFeature)null, var0, (Boolean)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
   }

   public static LocationPredicate inFeature(StructureFeature<?> var0) {
      return new LocationPredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, (ResourceKey)null, var0, (ResourceKey)null, (Boolean)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
   }

   public static LocationPredicate atYLocation(MinMaxBounds.Doubles var0) {
      return new LocationPredicate(MinMaxBounds.Doubles.ANY, var0, MinMaxBounds.Doubles.ANY, (ResourceKey)null, (StructureFeature)null, (ResourceKey)null, (Boolean)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
   }

   public boolean matches(ServerLevel var1, double var2, double var4, double var6) {
      if (!this.field_435.matches(var2)) {
         return false;
      } else if (!this.field_436.matches(var4)) {
         return false;
      } else if (!this.field_437.matches(var6)) {
         return false;
      } else if (this.dimension != null && this.dimension != var1.dimension()) {
         return false;
      } else {
         BlockPos var8 = new BlockPos(var2, var4, var6);
         boolean var9 = var1.isLoaded(var8);
         Optional var10 = var1.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getResourceKey(var1.getBiome(var8));
         if (!var10.isPresent()) {
            return false;
         } else if (this.biome != null && (!var9 || this.biome != var10.get())) {
            return false;
         } else if (this.feature == null || var9 && var1.structureFeatureManager().getStructureWithPieceAt(var8, this.feature).isValid()) {
            if (this.smokey == null || var9 && this.smokey == CampfireBlock.isSmokeyPos(var1, var8)) {
               if (!this.light.matches(var1, var8)) {
                  return false;
               } else if (!this.block.matches(var1, var8)) {
                  return false;
               } else {
                  return this.fluid.matches(var1, var8);
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         if (!this.field_435.isAny() || !this.field_436.isAny() || !this.field_437.isAny()) {
            JsonObject var2 = new JsonObject();
            var2.add("x", this.field_435.serializeToJson());
            var2.add("y", this.field_436.serializeToJson());
            var2.add("z", this.field_437.serializeToJson());
            var1.add("position", var2);
         }

         if (this.dimension != null) {
            DataResult var10000 = Level.RESOURCE_KEY_CODEC.encodeStart(JsonOps.INSTANCE, this.dimension);
            Logger var10001 = LOGGER;
            Objects.requireNonNull(var10001);
            var10000.resultOrPartial(var10001::error).ifPresent((var1x) -> {
               var1.add("dimension", var1x);
            });
         }

         if (this.feature != null) {
            var1.addProperty("feature", this.feature.getFeatureName());
         }

         if (this.biome != null) {
            var1.addProperty("biome", this.biome.location().toString());
         }

         if (this.smokey != null) {
            var1.addProperty("smokey", this.smokey);
         }

         var1.add("light", this.light.serializeToJson());
         var1.add("block", this.block.serializeToJson());
         var1.add("fluid", this.fluid.serializeToJson());
         return var1;
      }
   }

   public static LocationPredicate fromJson(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(var0, "location");
         JsonObject var2 = GsonHelper.getAsJsonObject(var1, "position", new JsonObject());
         MinMaxBounds.Doubles var3 = MinMaxBounds.Doubles.fromJson(var2.get("x"));
         MinMaxBounds.Doubles var4 = MinMaxBounds.Doubles.fromJson(var2.get("y"));
         MinMaxBounds.Doubles var5 = MinMaxBounds.Doubles.fromJson(var2.get("z"));
         ResourceKey var13;
         if (var1.has("dimension")) {
            DataResult var10000 = ResourceLocation.CODEC.parse(JsonOps.INSTANCE, var1.get("dimension"));
            Logger var10001 = LOGGER;
            Objects.requireNonNull(var10001);
            var13 = (ResourceKey)var10000.resultOrPartial(var10001::error).map((var0x) -> {
               return ResourceKey.create(Registry.DIMENSION_REGISTRY, var0x);
            }).orElse((Object)null);
         } else {
            var13 = null;
         }

         ResourceKey var6 = var13;
         StructureFeature var7 = var1.has("feature") ? (StructureFeature)StructureFeature.STRUCTURES_REGISTRY.get(GsonHelper.getAsString(var1, "feature")) : null;
         ResourceKey var8 = null;
         if (var1.has("biome")) {
            ResourceLocation var9 = new ResourceLocation(GsonHelper.getAsString(var1, "biome"));
            var8 = ResourceKey.create(Registry.BIOME_REGISTRY, var9);
         }

         Boolean var14 = var1.has("smokey") ? var1.get("smokey").getAsBoolean() : null;
         LightPredicate var10 = LightPredicate.fromJson(var1.get("light"));
         BlockPredicate var11 = BlockPredicate.fromJson(var1.get("block"));
         FluidPredicate var12 = FluidPredicate.fromJson(var1.get("fluid"));
         return new LocationPredicate(var3, var4, var5, var8, var7, var6, var14, var10, var11, var12);
      } else {
         return ANY;
      }
   }

   static {
      ANY = new LocationPredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, (ResourceKey)null, (StructureFeature)null, (ResourceKey)null, (Boolean)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
   }

   public static class Builder {
      // $FF: renamed from: x net.minecraft.advancements.critereon.MinMaxBounds$Doubles
      private MinMaxBounds.Doubles field_393;
      // $FF: renamed from: y net.minecraft.advancements.critereon.MinMaxBounds$Doubles
      private MinMaxBounds.Doubles field_394;
      // $FF: renamed from: z net.minecraft.advancements.critereon.MinMaxBounds$Doubles
      private MinMaxBounds.Doubles field_395;
      @Nullable
      private ResourceKey<Biome> biome;
      @Nullable
      private StructureFeature<?> feature;
      @Nullable
      private ResourceKey<Level> dimension;
      @Nullable
      private Boolean smokey;
      private LightPredicate light;
      private BlockPredicate block;
      private FluidPredicate fluid;

      public Builder() {
         super();
         this.field_393 = MinMaxBounds.Doubles.ANY;
         this.field_394 = MinMaxBounds.Doubles.ANY;
         this.field_395 = MinMaxBounds.Doubles.ANY;
         this.light = LightPredicate.ANY;
         this.block = BlockPredicate.ANY;
         this.fluid = FluidPredicate.ANY;
      }

      public static LocationPredicate.Builder location() {
         return new LocationPredicate.Builder();
      }

      public LocationPredicate.Builder setX(MinMaxBounds.Doubles var1) {
         this.field_393 = var1;
         return this;
      }

      public LocationPredicate.Builder setY(MinMaxBounds.Doubles var1) {
         this.field_394 = var1;
         return this;
      }

      public LocationPredicate.Builder setZ(MinMaxBounds.Doubles var1) {
         this.field_395 = var1;
         return this;
      }

      public LocationPredicate.Builder setBiome(@Nullable ResourceKey<Biome> var1) {
         this.biome = var1;
         return this;
      }

      public LocationPredicate.Builder setFeature(@Nullable StructureFeature<?> var1) {
         this.feature = var1;
         return this;
      }

      public LocationPredicate.Builder setDimension(@Nullable ResourceKey<Level> var1) {
         this.dimension = var1;
         return this;
      }

      public LocationPredicate.Builder setLight(LightPredicate var1) {
         this.light = var1;
         return this;
      }

      public LocationPredicate.Builder setBlock(BlockPredicate var1) {
         this.block = var1;
         return this;
      }

      public LocationPredicate.Builder setFluid(FluidPredicate var1) {
         this.fluid = var1;
         return this;
      }

      public LocationPredicate.Builder setSmokey(Boolean var1) {
         this.smokey = var1;
         return this;
      }

      public LocationPredicate build() {
         return new LocationPredicate(this.field_393, this.field_394, this.field_395, this.biome, this.feature, this.dimension, this.smokey, this.light, this.block, this.fluid);
      }
   }
}
