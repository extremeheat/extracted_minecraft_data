package net.minecraft.client.resources;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;

public record WaypointStyle(int nearDistance, int farDistance, List<Identifier> sprites, List<Identifier> spriteLocations) {
   @VisibleForTesting
   public static final String ICON_LOCATION_PREFIX = "hud/locator_bar_dot/";
   public static final int DEFAULT_NEAR_DISTANCE = 128;
   public static final int DEFAULT_FAR_DISTANCE = 332;
   private static final Codec<Integer> DISTANCE_CODEC = Codec.intRange(0, 60000000);
   public static final Codec<WaypointStyle> CODEC = RecordCodecBuilder.create((i) -> i.group(DISTANCE_CODEC.optionalFieldOf("near_distance", 128).forGetter(WaypointStyle::nearDistance), DISTANCE_CODEC.optionalFieldOf("far_distance", 332).forGetter(WaypointStyle::farDistance), ExtraCodecs.nonEmptyList(Identifier.CODEC.listOf()).fieldOf("sprites").forGetter(WaypointStyle::sprites)).apply(i, WaypointStyle::new)).validate(WaypointStyle::validate);

   public WaypointStyle(final int nearDistance, final int farDistance, final List<Identifier> sprites) {
      this(nearDistance, farDistance, sprites, sprites.stream().map((sprite) -> sprite.withPrefix("hud/locator_bar_dot/")).toList());
   }

   public WaypointStyle {
      super();
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

   public Identifier sprite(final float distance) {
      if (distance < (float)this.nearDistance) {
         return (Identifier)this.spriteLocations.getFirst();
      } else if (distance >= (float)this.farDistance) {
         return (Identifier)this.spriteLocations.getLast();
      } else if (this.spriteLocations.size() == 1) {
         return (Identifier)this.spriteLocations.getFirst();
      } else if (this.spriteLocations.size() == 3) {
         return (Identifier)this.spriteLocations.get(1);
      } else {
         int index = Mth.lerpInt((distance - (float)this.nearDistance) / (float)(this.farDistance - this.nearDistance), 1, this.spriteLocations.size() - 1);
         return (Identifier)this.spriteLocations.get(index);
      }
   }
}
