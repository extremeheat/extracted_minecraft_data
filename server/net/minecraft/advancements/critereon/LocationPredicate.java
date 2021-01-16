package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
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
   private final MinMaxBounds.Floats x;
   private final MinMaxBounds.Floats y;
   private final MinMaxBounds.Floats z;
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

   public LocationPredicate(MinMaxBounds.Floats var1, MinMaxBounds.Floats var2, MinMaxBounds.Floats var3, @Nullable ResourceKey<Biome> var4, @Nullable StructureFeature<?> var5, @Nullable ResourceKey<Level> var6, @Nullable Boolean var7, LightPredicate var8, BlockPredicate var9, FluidPredicate var10) {
      super();
      this.x = var1;
      this.y = var2;
      this.z = var3;
      this.biome = var4;
      this.feature = var5;
      this.dimension = var6;
      this.smokey = var7;
      this.light = var8;
      this.block = var9;
      this.fluid = var10;
   }

   public static LocationPredicate inBiome(ResourceKey<Biome> var0) {
      return new LocationPredicate(MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, var0, (StructureFeature)null, (ResourceKey)null, (Boolean)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
   }

   public static LocationPredicate inDimension(ResourceKey<Level> var0) {
      return new LocationPredicate(MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, (ResourceKey)null, (StructureFeature)null, var0, (Boolean)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
   }

   public static LocationPredicate inFeature(StructureFeature<?> var0) {
      return new LocationPredicate(MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, (ResourceKey)null, var0, (ResourceKey)null, (Boolean)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
   }

   public boolean matches(ServerLevel var1, double var2, double var4, double var6) {
      return this.matches(var1, (float)var2, (float)var4, (float)var6);
   }

   public boolean matches(ServerLevel var1, float var2, float var3, float var4) {
      if (!this.x.matches(var2)) {
         return false;
      } else if (!this.y.matches(var3)) {
         return false;
      } else if (!this.z.matches(var4)) {
         return false;
      } else if (this.dimension != null && this.dimension != var1.dimension()) {
         return false;
      } else {
         BlockPos var5 = new BlockPos((double)var2, (double)var3, (double)var4);
         boolean var6 = var1.isLoaded(var5);
         Optional var7 = var1.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getResourceKey(var1.getBiome(var5));
         if (!var7.isPresent()) {
            return false;
         } else if (this.biome != null && (!var6 || this.biome != var7.get())) {
            return false;
         } else if (this.feature != null && (!var6 || !var1.structureFeatureManager().getStructureAt(var5, true, this.feature).isValid())) {
            return false;
         } else if (this.smokey != null && (!var6 || this.smokey != CampfireBlock.isSmokeyPos(var1, var5))) {
            return false;
         } else if (!this.light.matches(var1, var5)) {
            return false;
         } else if (!this.block.matches(var1, var5)) {
            return false;
         } else {
            return this.fluid.matches(var1, var5);
         }
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         if (!this.x.isAny() || !this.y.isAny() || !this.z.isAny()) {
            JsonObject var2 = new JsonObject();
            var2.add("x", this.x.serializeToJson());
            var2.add("y", this.y.serializeToJson());
            var2.add("z", this.z.serializeToJson());
            var1.add("position", var2);
         }

         if (this.dimension != null) {
            DataResult var10000 = Level.RESOURCE_KEY_CODEC.encodeStart(JsonOps.INSTANCE, this.dimension);
            Logger var10001 = LOGGER;
            var10001.getClass();
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
         MinMaxBounds.Floats var3 = MinMaxBounds.Floats.fromJson(var2.get("x"));
         MinMaxBounds.Floats var4 = MinMaxBounds.Floats.fromJson(var2.get("y"));
         MinMaxBounds.Floats var5 = MinMaxBounds.Floats.fromJson(var2.get("z"));
         ResourceKey var13;
         if (var1.has("dimension")) {
            DataResult var10000 = ResourceLocation.CODEC.parse(JsonOps.INSTANCE, var1.get("dimension"));
            Logger var10001 = LOGGER;
            var10001.getClass();
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
      ANY = new LocationPredicate(MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, (ResourceKey)null, (StructureFeature)null, (ResourceKey)null, (Boolean)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
   }

   public static class Builder {
      private MinMaxBounds.Floats x;
      private MinMaxBounds.Floats y;
      private MinMaxBounds.Floats z;
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
         this.x = MinMaxBounds.Floats.ANY;
         this.y = MinMaxBounds.Floats.ANY;
         this.z = MinMaxBounds.Floats.ANY;
         this.light = LightPredicate.ANY;
         this.block = BlockPredicate.ANY;
         this.fluid = FluidPredicate.ANY;
      }

      public static LocationPredicate.Builder location() {
         return new LocationPredicate.Builder();
      }

      public LocationPredicate.Builder setBiome(@Nullable ResourceKey<Biome> var1) {
         this.biome = var1;
         return this;
      }

      public LocationPredicate.Builder setBlock(BlockPredicate var1) {
         this.block = var1;
         return this;
      }

      public LocationPredicate.Builder setSmokey(Boolean var1) {
         this.smokey = var1;
         return this;
      }

      public LocationPredicate build() {
         return new LocationPredicate(this.x, this.y, this.z, this.biome, this.feature, this.dimension, this.smokey, this.light, this.block, this.fluid);
      }
   }
}
