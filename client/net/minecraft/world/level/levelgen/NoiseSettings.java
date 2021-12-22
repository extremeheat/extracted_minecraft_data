package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.TerrainShaper;
import net.minecraft.world.level.dimension.DimensionType;

public record NoiseSettings(int b, int c, NoiseSamplingSettings d, NoiseSlider e, NoiseSlider f, int g, int h, boolean i, boolean j, boolean k, TerrainShaper l) {
   private final int minY;
   private final int height;
   private final NoiseSamplingSettings noiseSamplingSettings;
   private final NoiseSlider topSlideSettings;
   private final NoiseSlider bottomSlideSettings;
   private final int noiseSizeHorizontal;
   private final int noiseSizeVertical;
   private final boolean islandNoiseOverride;
   private final boolean isAmplified;
   private final boolean largeBiomes;
   private final TerrainShaper terrainShaper;
   public static final Codec<NoiseSettings> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.intRange(DimensionType.MIN_Y, DimensionType.MAX_Y).fieldOf("min_y").forGetter(NoiseSettings::minY), Codec.intRange(0, DimensionType.Y_SIZE).fieldOf("height").forGetter(NoiseSettings::height), NoiseSamplingSettings.CODEC.fieldOf("sampling").forGetter(NoiseSettings::noiseSamplingSettings), NoiseSlider.CODEC.fieldOf("top_slide").forGetter(NoiseSettings::topSlideSettings), NoiseSlider.CODEC.fieldOf("bottom_slide").forGetter(NoiseSettings::bottomSlideSettings), Codec.intRange(1, 4).fieldOf("size_horizontal").forGetter(NoiseSettings::noiseSizeHorizontal), Codec.intRange(1, 4).fieldOf("size_vertical").forGetter(NoiseSettings::noiseSizeVertical), Codec.BOOL.optionalFieldOf("island_noise_override", false, Lifecycle.experimental()).forGetter(NoiseSettings::islandNoiseOverride), Codec.BOOL.optionalFieldOf("amplified", false, Lifecycle.experimental()).forGetter(NoiseSettings::isAmplified), Codec.BOOL.optionalFieldOf("large_biomes", false, Lifecycle.experimental()).forGetter(NoiseSettings::largeBiomes), TerrainShaper.CODEC.fieldOf("terrain_shaper").forGetter(NoiseSettings::terrainShaper)).apply(var0, NoiseSettings::new);
   }).comapFlatMap(NoiseSettings::guardY, Function.identity());

   public NoiseSettings(int var1, int var2, NoiseSamplingSettings var3, NoiseSlider var4, NoiseSlider var5, int var6, int var7, boolean var8, boolean var9, boolean var10, TerrainShaper var11) {
      super();
      this.minY = var1;
      this.height = var2;
      this.noiseSamplingSettings = var3;
      this.topSlideSettings = var4;
      this.bottomSlideSettings = var5;
      this.noiseSizeHorizontal = var6;
      this.noiseSizeVertical = var7;
      this.islandNoiseOverride = var8;
      this.isAmplified = var9;
      this.largeBiomes = var10;
      this.terrainShaper = var11;
   }

   private static DataResult<NoiseSettings> guardY(NoiseSettings var0) {
      if (var0.minY() + var0.height() > DimensionType.MAX_Y + 1) {
         return DataResult.error("min_y + height cannot be higher than: " + (DimensionType.MAX_Y + 1));
      } else if (var0.height() % 16 != 0) {
         return DataResult.error("height has to be a multiple of 16");
      } else {
         return var0.minY() % 16 != 0 ? DataResult.error("min_y has to be a multiple of 16") : DataResult.success(var0);
      }
   }

   public static NoiseSettings create(int var0, int var1, NoiseSamplingSettings var2, NoiseSlider var3, NoiseSlider var4, int var5, int var6, boolean var7, boolean var8, boolean var9, TerrainShaper var10) {
      NoiseSettings var11 = new NoiseSettings(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      guardY(var11).error().ifPresent((var0x) -> {
         throw new IllegalStateException(var0x.message());
      });
      return var11;
   }

   /** @deprecated */
   @Deprecated
   public boolean islandNoiseOverride() {
      return this.islandNoiseOverride;
   }

   /** @deprecated */
   @Deprecated
   public boolean isAmplified() {
      return this.isAmplified;
   }

   /** @deprecated */
   @Deprecated
   public boolean largeBiomes() {
      return this.largeBiomes;
   }

   public int getCellHeight() {
      return QuartPos.toBlock(this.noiseSizeVertical());
   }

   public int getCellWidth() {
      return QuartPos.toBlock(this.noiseSizeHorizontal());
   }

   public int getCellCountY() {
      return this.height() / this.getCellHeight();
   }

   public int getMinCellY() {
      return Mth.intFloorDiv(this.minY(), this.getCellHeight());
   }

   public int minY() {
      return this.minY;
   }

   public int height() {
      return this.height;
   }

   public NoiseSamplingSettings noiseSamplingSettings() {
      return this.noiseSamplingSettings;
   }

   public NoiseSlider topSlideSettings() {
      return this.topSlideSettings;
   }

   public NoiseSlider bottomSlideSettings() {
      return this.bottomSlideSettings;
   }

   public int noiseSizeHorizontal() {
      return this.noiseSizeHorizontal;
   }

   public int noiseSizeVertical() {
      return this.noiseSizeVertical;
   }

   public TerrainShaper terrainShaper() {
      return this.terrainShaper;
   }
}
