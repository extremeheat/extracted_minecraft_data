package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

public class LocationPredicate {
   public static final LocationPredicate ANY;
   private final MinMaxBounds.Floats x;
   private final MinMaxBounds.Floats y;
   private final MinMaxBounds.Floats z;
   @Nullable
   private final Biome biome;
   @Nullable
   private final StructureFeature feature;
   @Nullable
   private final DimensionType dimension;
   private final LightPredicate light;
   private final BlockPredicate block;
   private final FluidPredicate fluid;

   public LocationPredicate(MinMaxBounds.Floats var1, MinMaxBounds.Floats var2, MinMaxBounds.Floats var3, @Nullable Biome var4, @Nullable StructureFeature var5, @Nullable DimensionType var6, LightPredicate var7, BlockPredicate var8, FluidPredicate var9) {
      this.x = var1;
      this.y = var2;
      this.z = var3;
      this.biome = var4;
      this.feature = var5;
      this.dimension = var6;
      this.light = var7;
      this.block = var8;
      this.fluid = var9;
   }

   public static LocationPredicate inBiome(Biome var0) {
      return new LocationPredicate(MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, var0, (StructureFeature)null, (DimensionType)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
   }

   public static LocationPredicate inDimension(DimensionType var0) {
      return new LocationPredicate(MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, (Biome)null, (StructureFeature)null, var0, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
   }

   public static LocationPredicate inFeature(StructureFeature var0) {
      return new LocationPredicate(MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, (Biome)null, var0, (DimensionType)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
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
      } else if (this.dimension != null && this.dimension != var1.dimension.getType()) {
         return false;
      } else {
         BlockPos var5 = new BlockPos((double)var2, (double)var3, (double)var4);
         boolean var6 = var1.isLoaded(var5);
         if (this.biome != null && (!var6 || this.biome != var1.getBiome(var5))) {
            return false;
         } else if (this.feature != null && (!var6 || !this.feature.isInsideFeature(var1, var5))) {
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
            var1.addProperty("dimension", DimensionType.getName(this.dimension).toString());
         }

         if (this.feature != null) {
            var1.addProperty("feature", (String)Feature.STRUCTURES_REGISTRY.inverse().get(this.feature));
         }

         if (this.biome != null) {
            var1.addProperty("biome", Registry.BIOME.getKey(this.biome).toString());
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
         DimensionType var6 = var1.has("dimension") ? DimensionType.getByName(new ResourceLocation(GsonHelper.getAsString(var1, "dimension"))) : null;
         StructureFeature var7 = var1.has("feature") ? (StructureFeature)Feature.STRUCTURES_REGISTRY.get(GsonHelper.getAsString(var1, "feature")) : null;
         Biome var8 = null;
         if (var1.has("biome")) {
            ResourceLocation var9 = new ResourceLocation(GsonHelper.getAsString(var1, "biome"));
            var8 = (Biome)Registry.BIOME.getOptional(var9).orElseThrow(() -> {
               return new JsonSyntaxException("Unknown biome '" + var9 + "'");
            });
         }

         LightPredicate var12 = LightPredicate.fromJson(var1.get("light"));
         BlockPredicate var10 = BlockPredicate.fromJson(var1.get("block"));
         FluidPredicate var11 = FluidPredicate.fromJson(var1.get("fluid"));
         return new LocationPredicate(var3, var4, var5, var8, var7, var6, var12, var10, var11);
      } else {
         return ANY;
      }
   }

   static {
      ANY = new LocationPredicate(MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, (Biome)null, (StructureFeature)null, (DimensionType)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
   }

   public static class Builder {
      private MinMaxBounds.Floats x;
      private MinMaxBounds.Floats y;
      private MinMaxBounds.Floats z;
      @Nullable
      private Biome biome;
      @Nullable
      private StructureFeature feature;
      @Nullable
      private DimensionType dimension;
      private LightPredicate light;
      private BlockPredicate block;
      private FluidPredicate fluid;

      public Builder() {
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

      public LocationPredicate.Builder setBiome(@Nullable Biome var1) {
         this.biome = var1;
         return this;
      }

      public LocationPredicate build() {
         return new LocationPredicate(this.x, this.y, this.z, this.biome, this.feature, this.dimension, this.light, this.block, this.fluid);
      }
   }
}
