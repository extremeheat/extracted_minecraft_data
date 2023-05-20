package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.slf4j.Logger;

public class LocationPredicate {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final LocationPredicate ANY = new LocationPredicate(
      MinMaxBounds.Doubles.ANY,
      MinMaxBounds.Doubles.ANY,
      MinMaxBounds.Doubles.ANY,
      null,
      null,
      null,
      null,
      LightPredicate.ANY,
      BlockPredicate.ANY,
      FluidPredicate.ANY
   );
   private final MinMaxBounds.Doubles x;
   private final MinMaxBounds.Doubles y;
   private final MinMaxBounds.Doubles z;
   @Nullable
   private final ResourceKey<Biome> biome;
   @Nullable
   private final ResourceKey<Structure> structure;
   @Nullable
   private final ResourceKey<Level> dimension;
   @Nullable
   private final Boolean smokey;
   private final LightPredicate light;
   private final BlockPredicate block;
   private final FluidPredicate fluid;

   public LocationPredicate(
      MinMaxBounds.Doubles var1,
      MinMaxBounds.Doubles var2,
      MinMaxBounds.Doubles var3,
      @Nullable ResourceKey<Biome> var4,
      @Nullable ResourceKey<Structure> var5,
      @Nullable ResourceKey<Level> var6,
      @Nullable Boolean var7,
      LightPredicate var8,
      BlockPredicate var9,
      FluidPredicate var10
   ) {
      super();
      this.x = var1;
      this.y = var2;
      this.z = var3;
      this.biome = var4;
      this.structure = var5;
      this.dimension = var6;
      this.smokey = var7;
      this.light = var8;
      this.block = var9;
      this.fluid = var10;
   }

   public static LocationPredicate inBiome(ResourceKey<Biome> var0) {
      return new LocationPredicate(
         MinMaxBounds.Doubles.ANY,
         MinMaxBounds.Doubles.ANY,
         MinMaxBounds.Doubles.ANY,
         var0,
         null,
         null,
         null,
         LightPredicate.ANY,
         BlockPredicate.ANY,
         FluidPredicate.ANY
      );
   }

   public static LocationPredicate inDimension(ResourceKey<Level> var0) {
      return new LocationPredicate(
         MinMaxBounds.Doubles.ANY,
         MinMaxBounds.Doubles.ANY,
         MinMaxBounds.Doubles.ANY,
         null,
         null,
         var0,
         null,
         LightPredicate.ANY,
         BlockPredicate.ANY,
         FluidPredicate.ANY
      );
   }

   public static LocationPredicate inStructure(ResourceKey<Structure> var0) {
      return new LocationPredicate(
         MinMaxBounds.Doubles.ANY,
         MinMaxBounds.Doubles.ANY,
         MinMaxBounds.Doubles.ANY,
         null,
         var0,
         null,
         null,
         LightPredicate.ANY,
         BlockPredicate.ANY,
         FluidPredicate.ANY
      );
   }

   public static LocationPredicate atYLocation(MinMaxBounds.Doubles var0) {
      return new LocationPredicate(
         MinMaxBounds.Doubles.ANY, var0, MinMaxBounds.Doubles.ANY, null, null, null, null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY
      );
   }

   public boolean matches(ServerLevel var1, double var2, double var4, double var6) {
      if (!this.x.matches(var2)) {
         return false;
      } else if (!this.y.matches(var4)) {
         return false;
      } else if (!this.z.matches(var6)) {
         return false;
      } else if (this.dimension != null && this.dimension != var1.dimension()) {
         return false;
      } else {
         BlockPos var8 = BlockPos.containing(var2, var4, var6);
         boolean var9 = var1.isLoaded(var8);
         if (this.biome == null || var9 && var1.getBiome(var8).is(this.biome)) {
            if (this.structure == null || var9 && var1.structureManager().getStructureWithPieceAt(var8, this.structure).isValid()) {
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
         if (!this.x.isAny() || !this.y.isAny() || !this.z.isAny()) {
            JsonObject var2 = new JsonObject();
            var2.add("x", this.x.serializeToJson());
            var2.add("y", this.y.serializeToJson());
            var2.add("z", this.z.serializeToJson());
            var1.add("position", var2);
         }

         if (this.dimension != null) {
            Level.RESOURCE_KEY_CODEC
               .encodeStart(JsonOps.INSTANCE, this.dimension)
               .resultOrPartial(LOGGER::error)
               .ifPresent(var1x -> var1.add("dimension", var1x));
         }

         if (this.structure != null) {
            var1.addProperty("structure", this.structure.location().toString());
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
         ResourceKey var6 = var1.has("dimension")
            ? ResourceLocation.CODEC
               .parse(JsonOps.INSTANCE, var1.get("dimension"))
               .resultOrPartial(LOGGER::error)
               .map(var0x -> ResourceKey.create(Registries.DIMENSION, var0x))
               .orElse(null)
            : null;
         ResourceKey var7 = var1.has("structure")
            ? ResourceLocation.CODEC
               .parse(JsonOps.INSTANCE, var1.get("structure"))
               .resultOrPartial(LOGGER::error)
               .map(var0x -> ResourceKey.create(Registries.STRUCTURE, var0x))
               .orElse(null)
            : null;
         ResourceKey var8 = null;
         if (var1.has("biome")) {
            ResourceLocation var9 = new ResourceLocation(GsonHelper.getAsString(var1, "biome"));
            var8 = ResourceKey.create(Registries.BIOME, var9);
         }

         Boolean var13 = var1.has("smokey") ? var1.get("smokey").getAsBoolean() : null;
         LightPredicate var10 = LightPredicate.fromJson(var1.get("light"));
         BlockPredicate var11 = BlockPredicate.fromJson(var1.get("block"));
         FluidPredicate var12 = FluidPredicate.fromJson(var1.get("fluid"));
         return new LocationPredicate(var3, var4, var5, var8, var7, var6, var13, var10, var11, var12);
      } else {
         return ANY;
      }
   }

   public static class Builder {
      private MinMaxBounds.Doubles x = MinMaxBounds.Doubles.ANY;
      private MinMaxBounds.Doubles y = MinMaxBounds.Doubles.ANY;
      private MinMaxBounds.Doubles z = MinMaxBounds.Doubles.ANY;
      @Nullable
      private ResourceKey<Biome> biome;
      @Nullable
      private ResourceKey<Structure> structure;
      @Nullable
      private ResourceKey<Level> dimension;
      @Nullable
      private Boolean smokey;
      private LightPredicate light = LightPredicate.ANY;
      private BlockPredicate block = BlockPredicate.ANY;
      private FluidPredicate fluid = FluidPredicate.ANY;

      public Builder() {
         super();
      }

      public static LocationPredicate.Builder location() {
         return new LocationPredicate.Builder();
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

      public LocationPredicate.Builder setBiome(@Nullable ResourceKey<Biome> var1) {
         this.biome = var1;
         return this;
      }

      public LocationPredicate.Builder setStructure(@Nullable ResourceKey<Structure> var1) {
         this.structure = var1;
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
         return new LocationPredicate(this.x, this.y, this.z, this.biome, this.structure, this.dimension, this.smokey, this.light, this.block, this.fluid);
      }
   }
}
