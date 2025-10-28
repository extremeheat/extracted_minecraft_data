package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;

public record BiomeSpecialEffects(int waterColor, Optional<Integer> foliageColorOverride, Optional<Integer> dryFoliageColorOverride, Optional<Integer> grassColorOverride, GrassColorModifier grassColorModifier) {
   public static final Codec<BiomeSpecialEffects> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ExtraCodecs.STRING_RGB_COLOR.fieldOf("water_color").forGetter(BiomeSpecialEffects::waterColor), ExtraCodecs.STRING_RGB_COLOR.optionalFieldOf("foliage_color").forGetter(BiomeSpecialEffects::foliageColorOverride), ExtraCodecs.STRING_RGB_COLOR.optionalFieldOf("dry_foliage_color").forGetter(BiomeSpecialEffects::dryFoliageColorOverride), ExtraCodecs.STRING_RGB_COLOR.optionalFieldOf("grass_color").forGetter(BiomeSpecialEffects::grassColorOverride), BiomeSpecialEffects.GrassColorModifier.CODEC.optionalFieldOf("grass_color_modifier", BiomeSpecialEffects.GrassColorModifier.NONE).forGetter(BiomeSpecialEffects::grassColorModifier)).apply(var0, BiomeSpecialEffects::new));

   public BiomeSpecialEffects(int var1, Optional<Integer> var2, Optional<Integer> var3, Optional<Integer> var4, GrassColorModifier var5) {
      super();
      this.waterColor = var1;
      this.foliageColorOverride = var2;
      this.dryFoliageColorOverride = var3;
      this.grassColorOverride = var4;
      this.grassColorModifier = var5;
   }

   public static class Builder {
      private OptionalInt waterColor = OptionalInt.empty();
      private Optional<Integer> foliageColorOverride = Optional.empty();
      private Optional<Integer> dryFoliageColorOverride = Optional.empty();
      private Optional<Integer> grassColorOverride = Optional.empty();
      private GrassColorModifier grassColorModifier;

      public Builder() {
         super();
         this.grassColorModifier = BiomeSpecialEffects.GrassColorModifier.NONE;
      }

      public Builder waterColor(int var1) {
         this.waterColor = OptionalInt.of(var1);
         return this;
      }

      public Builder foliageColorOverride(int var1) {
         this.foliageColorOverride = Optional.of(var1);
         return this;
      }

      public Builder dryFoliageColorOverride(int var1) {
         this.dryFoliageColorOverride = Optional.of(var1);
         return this;
      }

      public Builder grassColorOverride(int var1) {
         this.grassColorOverride = Optional.of(var1);
         return this;
      }

      public Builder grassColorModifier(GrassColorModifier var1) {
         this.grassColorModifier = var1;
         return this;
      }

      public BiomeSpecialEffects build() {
         return new BiomeSpecialEffects(this.waterColor.orElseThrow(() -> new IllegalStateException("Missing 'water' color.")), this.foliageColorOverride, this.dryFoliageColorOverride, this.grassColorOverride, this.grassColorModifier);
      }
   }

   public static enum GrassColorModifier implements StringRepresentable {
      NONE("none") {
         public int modifyColor(double var1, double var3, int var5) {
            return var5;
         }
      },
      DARK_FOREST("dark_forest") {
         public int modifyColor(double var1, double var3, int var5) {
            return (var5 & 16711422) + 2634762 >> 1;
         }
      },
      SWAMP("swamp") {
         public int modifyColor(double var1, double var3, int var5) {
            double var6 = Biome.BIOME_INFO_NOISE.getValue(var1 * 0.0225, var3 * 0.0225, false);
            return var6 < -0.1 ? 5011004 : 6975545;
         }
      };

      private final String name;
      public static final Codec<GrassColorModifier> CODEC = StringRepresentable.<GrassColorModifier>fromEnum(GrassColorModifier::values);

      public abstract int modifyColor(double var1, double var3, int var5);

      GrassColorModifier(final String var3) {
         this.name = var3;
      }

      public String getName() {
         return this.name;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static GrassColorModifier[] $values() {
         return new GrassColorModifier[]{NONE, DARK_FOREST, SWAMP};
      }
   }
}
