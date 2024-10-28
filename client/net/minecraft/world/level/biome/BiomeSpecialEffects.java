package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.StringRepresentable;

public class BiomeSpecialEffects {
   public static final Codec<BiomeSpecialEffects> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.INT.fieldOf("fog_color").forGetter((var0x) -> {
         return var0x.fogColor;
      }), Codec.INT.fieldOf("water_color").forGetter((var0x) -> {
         return var0x.waterColor;
      }), Codec.INT.fieldOf("water_fog_color").forGetter((var0x) -> {
         return var0x.waterFogColor;
      }), Codec.INT.fieldOf("sky_color").forGetter((var0x) -> {
         return var0x.skyColor;
      }), Codec.INT.optionalFieldOf("foliage_color").forGetter((var0x) -> {
         return var0x.foliageColorOverride;
      }), Codec.INT.optionalFieldOf("grass_color").forGetter((var0x) -> {
         return var0x.grassColorOverride;
      }), BiomeSpecialEffects.GrassColorModifier.CODEC.optionalFieldOf("grass_color_modifier", BiomeSpecialEffects.GrassColorModifier.NONE).forGetter((var0x) -> {
         return var0x.grassColorModifier;
      }), AmbientParticleSettings.CODEC.optionalFieldOf("particle").forGetter((var0x) -> {
         return var0x.ambientParticleSettings;
      }), SoundEvent.CODEC.optionalFieldOf("ambient_sound").forGetter((var0x) -> {
         return var0x.ambientLoopSoundEvent;
      }), AmbientMoodSettings.CODEC.optionalFieldOf("mood_sound").forGetter((var0x) -> {
         return var0x.ambientMoodSettings;
      }), AmbientAdditionsSettings.CODEC.optionalFieldOf("additions_sound").forGetter((var0x) -> {
         return var0x.ambientAdditionsSettings;
      }), Music.CODEC.optionalFieldOf("music").forGetter((var0x) -> {
         return var0x.backgroundMusic;
      })).apply(var0, BiomeSpecialEffects::new);
   });
   private final int fogColor;
   private final int waterColor;
   private final int waterFogColor;
   private final int skyColor;
   private final Optional<Integer> foliageColorOverride;
   private final Optional<Integer> grassColorOverride;
   private final GrassColorModifier grassColorModifier;
   private final Optional<AmbientParticleSettings> ambientParticleSettings;
   private final Optional<Holder<SoundEvent>> ambientLoopSoundEvent;
   private final Optional<AmbientMoodSettings> ambientMoodSettings;
   private final Optional<AmbientAdditionsSettings> ambientAdditionsSettings;
   private final Optional<Music> backgroundMusic;

   BiomeSpecialEffects(int var1, int var2, int var3, int var4, Optional<Integer> var5, Optional<Integer> var6, GrassColorModifier var7, Optional<AmbientParticleSettings> var8, Optional<Holder<SoundEvent>> var9, Optional<AmbientMoodSettings> var10, Optional<AmbientAdditionsSettings> var11, Optional<Music> var12) {
      super();
      this.fogColor = var1;
      this.waterColor = var2;
      this.waterFogColor = var3;
      this.skyColor = var4;
      this.foliageColorOverride = var5;
      this.grassColorOverride = var6;
      this.grassColorModifier = var7;
      this.ambientParticleSettings = var8;
      this.ambientLoopSoundEvent = var9;
      this.ambientMoodSettings = var10;
      this.ambientAdditionsSettings = var11;
      this.backgroundMusic = var12;
   }

   public int getFogColor() {
      return this.fogColor;
   }

   public int getWaterColor() {
      return this.waterColor;
   }

   public int getWaterFogColor() {
      return this.waterFogColor;
   }

   public int getSkyColor() {
      return this.skyColor;
   }

   public Optional<Integer> getFoliageColorOverride() {
      return this.foliageColorOverride;
   }

   public Optional<Integer> getGrassColorOverride() {
      return this.grassColorOverride;
   }

   public GrassColorModifier getGrassColorModifier() {
      return this.grassColorModifier;
   }

   public Optional<AmbientParticleSettings> getAmbientParticleSettings() {
      return this.ambientParticleSettings;
   }

   public Optional<Holder<SoundEvent>> getAmbientLoopSoundEvent() {
      return this.ambientLoopSoundEvent;
   }

   public Optional<AmbientMoodSettings> getAmbientMoodSettings() {
      return this.ambientMoodSettings;
   }

   public Optional<AmbientAdditionsSettings> getAmbientAdditionsSettings() {
      return this.ambientAdditionsSettings;
   }

   public Optional<Music> getBackgroundMusic() {
      return this.backgroundMusic;
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
      public static final Codec<GrassColorModifier> CODEC = StringRepresentable.fromEnum(GrassColorModifier::values);

      public abstract int modifyColor(double var1, double var3, int var5);

      GrassColorModifier(String var3) {
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

   public static class Builder {
      private OptionalInt fogColor = OptionalInt.empty();
      private OptionalInt waterColor = OptionalInt.empty();
      private OptionalInt waterFogColor = OptionalInt.empty();
      private OptionalInt skyColor = OptionalInt.empty();
      private Optional<Integer> foliageColorOverride = Optional.empty();
      private Optional<Integer> grassColorOverride = Optional.empty();
      private GrassColorModifier grassColorModifier;
      private Optional<AmbientParticleSettings> ambientParticle;
      private Optional<Holder<SoundEvent>> ambientLoopSoundEvent;
      private Optional<AmbientMoodSettings> ambientMoodSettings;
      private Optional<AmbientAdditionsSettings> ambientAdditionsSettings;
      private Optional<Music> backgroundMusic;

      public Builder() {
         super();
         this.grassColorModifier = BiomeSpecialEffects.GrassColorModifier.NONE;
         this.ambientParticle = Optional.empty();
         this.ambientLoopSoundEvent = Optional.empty();
         this.ambientMoodSettings = Optional.empty();
         this.ambientAdditionsSettings = Optional.empty();
         this.backgroundMusic = Optional.empty();
      }

      public Builder fogColor(int var1) {
         this.fogColor = OptionalInt.of(var1);
         return this;
      }

      public Builder waterColor(int var1) {
         this.waterColor = OptionalInt.of(var1);
         return this;
      }

      public Builder waterFogColor(int var1) {
         this.waterFogColor = OptionalInt.of(var1);
         return this;
      }

      public Builder skyColor(int var1) {
         this.skyColor = OptionalInt.of(var1);
         return this;
      }

      public Builder foliageColorOverride(int var1) {
         this.foliageColorOverride = Optional.of(var1);
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

      public Builder ambientParticle(AmbientParticleSettings var1) {
         this.ambientParticle = Optional.of(var1);
         return this;
      }

      public Builder ambientLoopSound(Holder<SoundEvent> var1) {
         this.ambientLoopSoundEvent = Optional.of(var1);
         return this;
      }

      public Builder ambientMoodSound(AmbientMoodSettings var1) {
         this.ambientMoodSettings = Optional.of(var1);
         return this;
      }

      public Builder ambientAdditionsSound(AmbientAdditionsSettings var1) {
         this.ambientAdditionsSettings = Optional.of(var1);
         return this;
      }

      public Builder backgroundMusic(@Nullable Music var1) {
         this.backgroundMusic = Optional.ofNullable(var1);
         return this;
      }

      public BiomeSpecialEffects build() {
         return new BiomeSpecialEffects(this.fogColor.orElseThrow(() -> {
            return new IllegalStateException("Missing 'fog' color.");
         }), this.waterColor.orElseThrow(() -> {
            return new IllegalStateException("Missing 'water' color.");
         }), this.waterFogColor.orElseThrow(() -> {
            return new IllegalStateException("Missing 'water fog' color.");
         }), this.skyColor.orElseThrow(() -> {
            return new IllegalStateException("Missing 'sky' color.");
         }), this.foliageColorOverride, this.grassColorOverride, this.grassColorModifier, this.ambientParticle, this.ambientLoopSoundEvent, this.ambientMoodSettings, this.ambientAdditionsSettings, this.backgroundMusic);
      }
   }
}
