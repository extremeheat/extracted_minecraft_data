package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.util.StringRepresentable;

public class BiomeSpecialEffects {
   public static final Codec<BiomeSpecialEffects> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.INT.fieldOf("water_color").forGetter((var0x) -> var0x.waterColor), Codec.INT.optionalFieldOf("foliage_color").forGetter((var0x) -> var0x.foliageColorOverride), Codec.INT.optionalFieldOf("dry_foliage_color").forGetter((var0x) -> var0x.dryFoliageColorOverride), Codec.INT.optionalFieldOf("grass_color").forGetter((var0x) -> var0x.grassColorOverride), BiomeSpecialEffects.GrassColorModifier.CODEC.optionalFieldOf("grass_color_modifier", BiomeSpecialEffects.GrassColorModifier.NONE).forGetter((var0x) -> var0x.grassColorModifier)).apply(var0, BiomeSpecialEffects::new));
   private final int waterColor;
   private final Optional<Integer> foliageColorOverride;
   private final Optional<Integer> dryFoliageColorOverride;
   private final Optional<Integer> grassColorOverride;
   private final GrassColorModifier grassColorModifier;

   BiomeSpecialEffects(int var1, Optional<Integer> var2, Optional<Integer> var3, Optional<Integer> var4, GrassColorModifier var5) {
      super();
      this.waterColor = var1;
      this.foliageColorOverride = var2;
      this.dryFoliageColorOverride = var3;
      this.grassColorOverride = var4;
      this.grassColorModifier = var5;
   }

   public int getWaterColor() {
      return this.waterColor;
   }

   public Optional<Integer> getFoliageColorOverride() {
      return this.foliageColorOverride;
   }

   public Optional<Integer> getDryFoliageColorOverride() {
      return this.dryFoliageColorOverride;
   }

   public Optional<Integer> getGrassColorOverride() {
      return this.grassColorOverride;
   }

   public GrassColorModifier getGrassColorModifier() {
      return this.grassColorModifier;
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
