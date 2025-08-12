package net.minecraft.client.resources;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;

public record WaypointStyle(int nearDistance, int farDistance, List<ResourceLocation> sprites, List<ResourceLocation> spriteLocations) {
   @VisibleForTesting
   public static final String ICON_LOCATION_PREFIX = "hud/locator_bar_dot/";
   public static final int DEFAULT_NEAR_DISTANCE = 128;
   public static final int DEFAULT_FAR_DISTANCE = 332;
   private static final Codec<Integer> DISTANCE_CODEC = Codec.intRange(0, 60000000);
   public static final Codec<WaypointStyle> CODEC = RecordCodecBuilder.create((var0) -> var0.group(DISTANCE_CODEC.optionalFieldOf("near_distance", 128).forGetter(WaypointStyle::nearDistance), DISTANCE_CODEC.optionalFieldOf("far_distance", 332).forGetter(WaypointStyle::farDistance), ExtraCodecs.nonEmptyList(ResourceLocation.CODEC.listOf()).fieldOf("sprites").forGetter(WaypointStyle::sprites)).apply(var0, WaypointStyle::new)).validate(WaypointStyle::validate);

   public WaypointStyle(int var1, int var2, List<ResourceLocation> var3) {
      this(var1, var2, var3, var3.stream().map((var0) -> var0.withPrefix("hud/locator_bar_dot/")).toList());
   }

   public WaypointStyle(int var1, int var2, List<ResourceLocation> var3, List<ResourceLocation> var4) {
      super();
      this.nearDistance = var1;
      this.farDistance = var2;
      this.sprites = var3;
      this.spriteLocations = var4;
   }

   @VisibleForTesting
   public DataResult<WaypointStyle> validate() {
      if (this.sprites.isEmpty()) {
         return DataResult.error(() -> "Must have at least one sprite icon");
      } else if (this.nearDistance <= 0) {
         return DataResult.error(() -> "Near distance (" + this.nearDistance + ") must be greater than zero");
      } else {
         return this.nearDistance >= this.farDistance ? DataResult.error(() -> "Far distance (" + this.farDistance + ") cannot be closer or equal to near distance (" + this.nearDistance + ")") : DataResult.success(this);
      }
   }

   public ResourceLocation sprite(float var1) {
      if (var1 < (float)this.nearDistance) {
         return (ResourceLocation)this.spriteLocations.getFirst();
      } else if (var1 >= (float)this.farDistance) {
         return (ResourceLocation)this.spriteLocations.getLast();
      } else if (this.spriteLocations.size() == 1) {
         return (ResourceLocation)this.spriteLocations.getFirst();
      } else if (this.spriteLocations.size() == 3) {
         return (ResourceLocation)this.spriteLocations.get(1);
      } else {
         int var2 = Mth.lerpInt((var1 - (float)this.nearDistance) / (float)(this.farDistance - this.nearDistance), 1, this.spriteLocations.size() - 1);
         return (ResourceLocation)this.spriteLocations.get(var2);
      }
   }
}
